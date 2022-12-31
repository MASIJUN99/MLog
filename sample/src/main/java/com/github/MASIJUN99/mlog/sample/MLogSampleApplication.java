package com.github.MASIJUN99.mlog.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MLogSampleApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(MLogSampleApplication.class, args);
    for (String beanDefinitionName : context.getBeanDefinitionNames()) {
      System.out.println(beanDefinitionName);
    }

  }

}
