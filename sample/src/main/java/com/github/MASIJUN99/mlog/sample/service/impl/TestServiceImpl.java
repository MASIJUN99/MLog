package com.github.MASIJUN99.mlog.sample.service.impl;

import com.github.MASIJUN99.mlog.annotations.MLog;
import com.github.MASIJUN99.mlog.context.LogVariablesContext;
import com.github.MASIJUN99.mlog.sample.model.TestModel;
import com.github.MASIJUN99.mlog.sample.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

  @Override
  @MLog(
      success = "添加成功, 实体类为#{#entity.key}",
      business = TestModel.class
  )
  public void add(TestModel model) {
    LogVariablesContext.setVariable("entity", model);
    // real business logic
  }
}
