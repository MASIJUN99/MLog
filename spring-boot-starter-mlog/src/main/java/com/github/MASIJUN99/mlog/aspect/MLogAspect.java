package com.github.MASIJUN99.mlog.aspect;

import static com.github.MASIJUN99.mlog.constant.LogVariablesConstant.currentValueKey;
import static com.github.MASIJUN99.mlog.constant.LogVariablesConstant.originValueKey;

import com.github.MASIJUN99.mlog.annotations.MLog;
import com.github.MASIJUN99.mlog.context.LogVariablesContext;
import com.github.MASIJUN99.mlog.exceptions.ConditionTypeException;
import com.github.MASIJUN99.mlog.exceptions.LogAppendException;
import com.github.MASIJUN99.mlog.model.MLogRecord;
import com.github.MASIJUN99.mlog.model.MLogRecord.MLogRecordBuilder;
import com.github.MASIJUN99.mlog.service.LogService;
import com.github.MASIJUN99.mlog.service.OperatorService;
import com.github.MASIJUN99.mlog.service.TraceIdService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

@Aspect
public class MLogAspect {

  private final ExecutorService executorService;
  private final LogService logService;
  private final OperatorService operatorService;
  private final TraceIdService traceIdService;
  private final SpelExpressionParser spelExpressionParser;
  private final TemplateParserContext templateParserContext;

  public MLogAspect(ExecutorService executorService, LogService logService,
      OperatorService operatorService,
      TraceIdService traceIdService,
      SpelExpressionParser spelExpressionParser,
      TemplateParserContext templateParserContext) {
    this.executorService = executorService;
    this.logService = logService;
    this.operatorService = operatorService;
    this.traceIdService = traceIdService;
    this.spelExpressionParser = spelExpressionParser;
    this.templateParserContext = templateParserContext;
  }

  @Pointcut("@annotation(com.github.MASIJUN99.mlog.annotations.MLog)")
  private void pointcut() {
    // 切点
  }

  /**
   * 切面逻辑
   *
   * <li>1. 应当在进入切点的第一时刻检验是不是非其他日志行为调用的方法</li>
   * <li>2. 正常执行方法, 遇到异常要抛出, 在finally进行日志行为, 同时要正确设置成功失败的标志位</li>
   * <li>3.1 若方法执行成功, 获取所有设置变量</li>
   * <li>3.2 获取方法的自定义注解, 此步因反射会抛出NoSuchMethodException, 非极端情况不存在该问题, 忽略即可</li>
   * <li>3.3 解析自定义注解中的成功或失败模板, 此处不会抛出RuntimeException</li>
   * <li>3.4 解析得到的表达式根据ThreadLocal进行求值, 此处会抛出EvaluationException, 通常是因为没有SpEL模板中该变量未在EvaluationContext中找到引起</li>
   * <li>3.5 调用异步插入</li>
   * <li>4 若方法执行失败, 调用失败模板, 解析后调用异步插入</li>
   *
   * @param proceedingJoinPoint 切点
   * @return 原方法结果
   * @throws Throwable 原方法抛出的异常
   */
  @Around("pointcut()")
  private Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    // 进入切点
    Object res;
    LogVariablesContext.call((MethodSignature) proceedingJoinPoint.getSignature());
    Date startTime = new Date();
    try {
      res = proceedingJoinPoint.proceed();
      handle(proceedingJoinPoint, startTime, new Date());
    } catch (Throwable throwable) {
      // 需要抛出 不然无法正常捕获异常
      handle(proceedingJoinPoint, startTime, new Date(), getExceptionStackTrace(throwable));
      throw throwable;
    } finally {
      LogVariablesContext.hang();
    }
    return res;
  }

  private void handle(ProceedingJoinPoint proceedingJoinPoint, Date startTime, Date endTime) {
    handle(proceedingJoinPoint, startTime, endTime, "");
  }

  /**
   * 异步处理注解内容和上下文变量
   * <li>1. 上下文变量放入表达式上下文中</li>
   * <li>2. 获得注解</li>
   * <li>3. 填充各项参数</li>
   * <li>4. 插入或不插入 成功或失败</li>
   */
  private void handle(ProceedingJoinPoint proceedingJoinPoint, Date startTime, Date endTime, String exceptionContent) {
    // 全部写进try, 捕获所有异常的可能性, 防止影响业务
    try {
      // 1. 将ThreadLocal变量全部拿出来放到EvaluationContext
      Map<String, Object> allVariables = LogVariablesContext.getAllVariables();
      StandardEvaluationContext context = new StandardEvaluationContext();
      context.setVariables(allVariables);
      executorService.submit(() -> {
        try {
          // 2. 获取注解
          // 2.1 获取目标对象
          Object target = proceedingJoinPoint.getTarget();
          // 2.2 获取目标方法对象
          // 2.2.1 获取方法签名
          MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
          // 2.2.2 通过字节码对象以及方法签名获取目标方法
          Method method = target.getClass().getMethod(signature.getName(), signature.getParameterTypes());
          // 2.2.3 获取方法上的自定义注解
          MLog mLog = method.getAnnotation(MLog.class);
          // 2.3 获得SpEL模板
          String template = !StringUtils.hasText(exceptionContent) ? mLog.success() : mLog.fail();

          // 3. 拼装日志实体类
          MLogRecordBuilder builder = MLogRecord.builder();
          // 3.1 获得operator
          builder.operator(StringUtils.hasText(mLog.operator()) ?
              getStringValue(mLog.operator(), context) :  // 解析SpEL获得
              operatorService.getCurrentOperator());  // 通过接口获得
          // 3.2 是否成功
          builder.success(!StringUtils.hasText(exceptionContent));
          builder.exception(StringUtils.hasText(exceptionContent) ? "" : exceptionContent);
          // 3.3 业务实体坐标
          builder.business(mLog.business().getName());
          // 3.4 业务实体唯一id
          builder.businessNo(getStringValue(mLog.businessNo(), context));
          // 3.5 业务实体原始值与当前值
          builder.originValue(StringUtils.hasText(mLog.originValue()) ?
              getValue(mLog.originValue(), context) :
              context.lookupVariable(originValueKey));
          builder.currentValue(StringUtils.hasText(mLog.currentValue()) ?
              getValue(mLog.currentValue(), context) :
              context.lookupVariable(currentValueKey));
          // 3.6 内容
          builder.content(getStringValue(template, context));
          // 3.7 发生时间
          builder.startTime(startTime);
          builder.endTime(endTime);
          // 3.8 业务方法签名
          builder.signature(signature.toLongString());
          // 3.9 trace id
          builder.traceId(traceIdService.getTraceId());

          // 4. 插入
          MLogRecord mLogRecord = builder.build();
          if (StringUtils.hasText(mLog.condition())) {
            Object condition = getValue(mLog.condition(), context);
            if (condition instanceof Boolean) {
              if ((boolean) condition) {
                append(mLogRecord);
              } else {
                logService.callback(mLogRecord);
              }
            } else {
              throw new ConditionTypeException("Condition should be parsed to boolean type variable!");
            }
          } else {
            append(mLogRecord);
          }
        } catch (Exception e) {
          logService.failAction(e);
        }
      });
    } catch (Exception e) {
      logService.failAction(e);
    }
  }

  private void append(MLogRecord mLogRecord) {
    boolean res;
    try {
      res = logService.append(mLogRecord);
    } catch (Exception e) {
      // 若捕获到异常
      logService.failAction(e);
      throw e;
    }
    if (!res) {
      // 若没异常但返回值没成功
      logService.failAction(new LogAppendException("日志添加失败！"));
    }
  }

  private Object getValue(String spelTemplates, EvaluationContext context) {
    Expression expression = spelExpressionParser
        .parseExpression(spelTemplates, templateParserContext);
    return expression.getValue(context);
  }

  private String getStringValue(String spelTemplates, EvaluationContext context) {
    Expression expression = spelExpressionParser
        .parseExpression(spelTemplates, templateParserContext);
    return expression.getValue(context, String.class);
  }

  private String getExceptionStackTrace(Throwable throwable) throws IOException {
    String exceptionContent = "";
    if (throwable != null) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(bos);
      throwable.printStackTrace(printStream);
      exceptionContent = bos.toString("UTF-8");
      bos.close();
      printStream.close();
    }
    return exceptionContent;
  }

}
