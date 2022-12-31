package com.github.MASIJUN99.mlog.sample;

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
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.spel.support.DataBindingMethodResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@SpringBootTest
public class MLogTest {

  @Autowired
  private TestService testService;

  @Test
  public void test() throws InterruptedException {
    TestModel mock = JMockData.mock(TestModel.class);
    testService.add(mock);
    Thread.sleep(2000);  // wait for async result...
  }

  @Test
  public void test2() throws InterruptedException {
    TestModel mock = JMockData.mock(TestModel.class);
    testService.recursion(mock);
    Thread.sleep(2000);  // wait for async result...
  }

  @Test
  public void test3() throws AccessException, InterruptedException {
    TestModel mock = JMockData.mock(TestModel.class);
    EvaluationContext evaluationContext = new StandardEvaluationContext();
    evaluationContext.setVariable("mock", mock);
    DataBindingMethodResolver dataBindingMethodResolver = DataBindingMethodResolver
        .forInstanceMethodInvocation();
    TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(TestModel.class);
    ArrayList<TypeDescriptor> arrayList = new ArrayList<>(){{add(typeDescriptor);}};
    MethodExecutor add = dataBindingMethodResolver
        .resolve(evaluationContext, testService, "add", arrayList);
    add.execute(evaluationContext, testService, mock);
    System.out.println(111);
    Thread.sleep(1000);
  }

}
