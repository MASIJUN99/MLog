package com.github.MASIJUN99.mlog.sample;

import com.github.MASIJUN99.mlog.context.MLogContextAware;
import com.github.MASIJUN99.mlog.sample.model.TestModel;
import com.github.MASIJUN99.mlog.sample.service.TestService;
import com.github.jsonzou.jmockdata.JMockData;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.DataBindingMethodResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@SpringBootTest
public class MLogTest {

  @Autowired
  private TestService testService;
  @Autowired
  private MLogContextAware mLogContextAware;

  @Test
  public void add() throws InterruptedException {
    TestModel mock = JMockData.mock(TestModel.class);
    testService.add(mock);
    Thread.sleep(1000);  // wait for async result...
  }

  @Test
  public void update() throws InterruptedException {
    TestModel mock = JMockData.mock(TestModel.class);
    testService.update(mock);
    Thread.sleep(1000);  // wait for async result...
  }

  @Test
  public void recursion() throws InterruptedException {
    TestModel mock = JMockData.mock(TestModel.class);
    testService.recursion(mock);
    Thread.sleep(1000);  // wait for async result...
  }

  @Test
  public void test3() throws AccessException, InterruptedException {
    TestModel mock = JMockData.mock(TestModel.class);
    EvaluationContext evaluationContext = new StandardEvaluationContext();
    evaluationContext.setVariable("mock", mock);

    String spel = "#{#mock.id}";
    SpelExpressionParser parser = new SpelExpressionParser();
    Expression expression = parser.parseExpression(spel, new TemplateParserContext());
    Object value = expression.getValue(evaluationContext);
    System.out.println(value);

    DataBindingMethodResolver dataBindingMethodResolver = DataBindingMethodResolver
        .forInstanceMethodInvocation();
    TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(TestModel.class);
    ArrayList<TypeDescriptor> arrayList = new ArrayList<TypeDescriptor>(){{add(typeDescriptor);}};
    Object testService = mLogContextAware.getBean("testServiceImpl");
    MethodExecutor add = dataBindingMethodResolver
        .resolve(evaluationContext, testService, "add", arrayList);
    add.execute(evaluationContext, testService, mock);
    System.out.println(111);
    Thread.sleep(1000);
  }

}
