package com.github.MASIJUN99.mlog.config;

import com.github.MASIJUN99.mlog.service.LogService;
import com.github.MASIJUN99.mlog.service.OperatorService;
import com.github.MASIJUN99.mlog.service.TraceIdService;
import com.github.MASIJUN99.mlog.service.impl.ConsoleLogServiceImpl;
import com.github.MASIJUN99.mlog.service.impl.DefaultOperatorServiceImpl;
import com.github.MASIJUN99.mlog.service.impl.DefaultTraceIdServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class ServiceConfiguration {

  @Bean
  @ConditionalOnMissingBean(value = LogService.class)
  public LogService consoleLogService() {
    return new ConsoleLogServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean(value = OperatorService.class)
  public OperatorService defaultOperatorServiceImpl() {
    return new DefaultOperatorServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean(value = TraceIdService.class)
  public TraceIdService defaultTraceIdServiceImpl() {
    return new DefaultTraceIdServiceImpl();
  }

}
