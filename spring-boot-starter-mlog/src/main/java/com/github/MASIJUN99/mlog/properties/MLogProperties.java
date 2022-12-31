package com.github.MASIJUN99.mlog.properties;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tgou-log")
public class MLogProperties {

  private SPELTemplate spelTemplate = new SPELTemplate();

  private ThreadPool threadPool = new ThreadPool();

  public SPELTemplate getSpelTemplate() {
    return spelTemplate;
  }

  public void setSpelTemplate(SPELTemplate spelTemplate) {
    this.spelTemplate = spelTemplate;
  }

  public ThreadPool getThreadPool() {
    return threadPool;
  }

  public void setThreadPool(ThreadPool threadPool) {
    this.threadPool = threadPool;
  }

  public static class SPELTemplate {
    /**
     * SpEL模板解析前缀
     */
    private String prefix = "#{";

    /**
     * SpEL模板解析后缀
     */
    private String suffix = "}";

    public String getPrefix() {
      return prefix;
    }

    public void setPrefix(String prefix) {
      this.prefix = prefix;
    }

    public String getSuffix() {
      return suffix;
    }

    public void setSuffix(String suffix) {
      this.suffix = suffix;
    }
  }

  public static class ThreadPool {
    private int corePoolSize = 1;
    private int maximumPoolSize = 1;
    private long keepAliveTime = 0;
    private TimeUnit unit = TimeUnit.SECONDS;
    // TODO: add other configurations


    public int getCorePoolSize() {
      return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
      return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
      this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
      return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
    }

    public TimeUnit getUnit() {
      return unit;
    }

    public void setUnit(TimeUnit unit) {
      this.unit = unit;
    }
  }

}
