package com.github.MASIJUN99.mlog.config;

import com.github.MASIJUN99.mlog.aspect.MLogAspect;
import com.github.MASIJUN99.mlog.properties.MLogProperties;
import com.github.MASIJUN99.mlog.properties.MLogProperties.SPELTemplate;
import com.github.MASIJUN99.mlog.properties.MLogProperties.ThreadPool;
import com.github.MASIJUN99.mlog.service.LogService;
import com.github.MASIJUN99.mlog.service.OperatorService;
import com.github.MASIJUN99.mlog.service.TraceIdService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(MLogProperties.class)
public class TgouLogConfiguration {

  private final LogService logService;
  private final OperatorService operatorService;
  private final TraceIdService traceIdService;

  public TgouLogConfiguration(LogService logService,
      OperatorService operatorService,
      TraceIdService traceIdService) {
    this.logService = logService;
    this.operatorService = operatorService;
    this.traceIdService = traceIdService;
  }

  @Bean
  public MLogAspect tgouLogAspect(MLogProperties mLogProperties) {
    return new MLogAspect(
        mLogExecutorService(mLogProperties),
        logService, operatorService, traceIdService,
        spelExpressionParser(), templateParserContext(mLogProperties));
  }

  @Bean
  public SpelExpressionParser spelExpressionParser() {
    SpelParserConfiguration config = new SpelParserConfiguration(
        SpelCompilerMode.OFF, this.getClass().getClassLoader());
    return new SpelExpressionParser(config);
  }

  @Bean
  public TemplateParserContext templateParserContext(MLogProperties mLogProperties) {
    SPELTemplate spelTemplate = mLogProperties.getSpelTemplate();
    return new TemplateParserContext(spelTemplate.getPrefix(), spelTemplate.getSuffix());
  }

  @Bean
  public ExecutorService mLogExecutorService(MLogProperties mLogProperties) {
    ThreadPool threadPool = mLogProperties.getThreadPool();
    return new ThreadPoolExecutor(
        threadPool.getCorePoolSize(),
        threadPool.getMaximumPoolSize(),
        threadPool.getKeepAliveTime(),
        threadPool.getUnit(),
        new LinkedBlockingQueue<>(1000));
  }
}
