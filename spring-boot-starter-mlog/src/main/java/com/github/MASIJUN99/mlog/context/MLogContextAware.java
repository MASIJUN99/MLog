package com.github.MASIJUN99.mlog.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MLogContextAware implements ApplicationContextAware {

  private static ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    MLogContextAware.applicationContext = applicationContext;
  }

  public Object getBean(String name) {
    return applicationContext.getBean(name);
  }


  public <T> T getBean(Class<T> clazz) {
    return applicationContext.getBean(clazz);
  }
}
