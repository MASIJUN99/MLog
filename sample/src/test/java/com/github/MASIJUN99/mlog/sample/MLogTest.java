package com.github.MASIJUN99.mlog.sample;

import com.github.MASIJUN99.mlog.sample.model.TestModel;
import com.github.MASIJUN99.mlog.sample.service.TestService;
import com.github.jsonzou.jmockdata.JMockData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MLogTest {

  @Autowired
  private TestService testService;

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

}
