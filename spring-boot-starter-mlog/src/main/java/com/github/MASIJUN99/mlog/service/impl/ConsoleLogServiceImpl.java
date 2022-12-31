package com.github.MASIJUN99.mlog.service.impl;

import com.github.MASIJUN99.mlog.model.MLogRecord;
import com.github.MASIJUN99.mlog.service.LogService;

public class ConsoleLogServiceImpl implements LogService {

  @Override
  public boolean append(MLogRecord tgouLogRecord) {
    System.out.println(tgouLogRecord.toString());
    return true;
  }

  @Override
  public void callback(MLogRecord tgouLogRecord) {
    System.out.println("未触发添加日志条件, 触发默认回调");
  }

  @Override
  public void failAction(Exception e) {
    e.printStackTrace();
  }
}
