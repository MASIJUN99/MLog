package com.github.MASIJUN99.mlog.service;


import com.github.MASIJUN99.mlog.model.MLogRecord;

public interface LogService {

  // 这里抛出LogAppendException
  boolean append(MLogRecord tgouLogRecord);

  void callback(MLogRecord tgouLogRecord);

  void failAction(Exception e);

}
