package com.github.MASIJUN99.mlog.sample.service.impl;

import com.github.MASIJUN99.mlog.annotations.MLog;
import com.github.MASIJUN99.mlog.context.LogVariablesContext;
import com.github.MASIJUN99.mlog.sample.service.TestRecursionService;
import org.springframework.stereotype.Service;

@Service
public class TestRecursionServiceImpl implements TestRecursionService {

  @Override
  @MLog(
      success = "嵌套调用！嵌套的entity变量为#{#entity}",
      business = Void.class
  )
  public void test() {
    LogVariablesContext.setVariable("entity", 123);  // add a variable with existed key
    // real business logic
  }

}
