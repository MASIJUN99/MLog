package com.github.MASIJUN99.mlog.service.impl;

import com.github.MASIJUN99.mlog.service.OperatorService;

public class DefaultOperatorServiceImpl implements OperatorService {

  @Override
  public String getCurrentOperator() {
    // 规约为id:name:username
    return "1:默认:default";
  }
}
