package com.github.MASIJUN99.mlog.service.impl;

import com.github.MASIJUN99.mlog.service.TraceIdService;

public class DefaultTraceIdServiceImpl implements TraceIdService {

  @Override
  public String getTraceId() {
    return "default-trace-id";
  }
}
