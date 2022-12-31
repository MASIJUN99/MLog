package com.github.MASIJUN99.mlog.sample.service.impl;

import com.github.MASIJUN99.mlog.annotations.MLog;
import com.github.MASIJUN99.mlog.context.LogVariablesContext;
import com.github.MASIJUN99.mlog.sample.model.TestModel;
import com.github.MASIJUN99.mlog.sample.service.TestRecursionService;
import com.github.MASIJUN99.mlog.sample.service.TestService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

  @Resource
  TestRecursionService testRecursionService;

  @Override
  @MLog(
      success = "添加成功, 实体类为#{#entity.key}",
      business = TestModel.class
  )
  public void add(TestModel model) {
    LogVariablesContext.setVariable("entity", model);
    // real business logic
  }

  @Override
  @MLog(
      success = "修改成功, 实体类为#{#entity.key}",
      fail = "修改失败, 请检查报错",
      currentValue = "#{#entity}",
      business = TestModel.class,
      businessNo = "#{#entity.id}",
      condition = "#{#entity.id != null}"
  )
  public void update(TestModel model) {
    LogVariablesContext.setOriginValue(model);
    LogVariablesContext.setVariable("entity", model);
    // real business logic
  }

  @Override
  @MLog(
      success = "嵌套调用，如果嵌套失败会直接报错, 实体类为#{#entity.key}",
      business = TestModel.class
  )
  public void recursion(TestModel model) {
    LogVariablesContext.setVariable("entity", model);
    testRecursionService.test();
  }
}
