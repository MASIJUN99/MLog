package com.github.MASIJUN99.mlog.model;

import java.util.Date;

public class MLogRecord {

  /**
   * 日志自增id
   */
  protected Long id;

  /**
   * 操作者
   */
  protected String operator;

  /**
   * 业务命名空间id
   */
  protected Long namespaceId;

  /**
   * 业务是否成功
   */
  protected Boolean success;

  /**
   * 若业务执行失败，抛出的异常是什么
   */
  protected String exception;

  /**
   * 业务实体类坐标
   */
  protected String business;

  /**
   * 业务实体类的唯一id
   */
  protected String businessNo;

  /**
   * 业务实体原始值
   */
  protected Object originValue;

  /**
   * 业务实体当前值
   */
  protected Object currentValue;

  /**
   * 日志内容
   */
  protected String content;

  /**
     * 发生时间
   */
  protected Date startTime;

  /**
   * 结束时间
   */
  protected Date endTime;

  /**
   * 业务方法签名
   */
  protected String signature;

  /**
   * Trace id
   */
  protected String traceId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getNamespaceId() {
    return namespaceId;
  }

  public void setNamespaceId(Long namespaceId) {
    this.namespaceId = namespaceId;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public String getException() {
    return exception;
  }

  public void setException(String exception) {
    this.exception = exception;
  }

  public String getBusiness() {
    return business;
  }

  public void setBusiness(String business) {
    this.business = business;
  }

  public String getBusinessNo() {
    return businessNo;
  }

  public void setBusinessNo(String businessNo) {
    this.businessNo = businessNo;
  }

  public Object getOriginValue() {
    return originValue;
  }

  public void setOriginValue(Object originValue) {
    this.originValue = originValue;
  }

  public Object getCurrentValue() {
    return currentValue;
  }

  public void setCurrentValue(Object currentValue) {
    this.currentValue = currentValue;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(String traceId) {
    this.traceId = traceId;
  }

  public static MLogRecordBuilder builder() {
    return new MLogRecordBuilder();
  }

  public static class MLogRecordBuilder {
    private Long namespaceId;
    private Boolean success;
    private String exception;
    private String business;
    private String businessNo;
    private Object originValue;
    private Object currentValue;
    private String content;
    private String operator;
    private Date startTime;
    private Date endTime;
    private String signature;
    private String traceId;

    public MLogRecordBuilder namespaceId(Long namespaceId) {
      this.namespaceId = namespaceId;
      return this;
    }

    public MLogRecordBuilder success(Boolean success) {
      this.success = success;
      return this;
    }

    public MLogRecordBuilder exception(String exception) {
      this.exception = exception;
      return this;
    }

    public MLogRecordBuilder originValue(Object originValue) {
      this.originValue = originValue;
      return this;
    }

    public MLogRecordBuilder currentValue(Object currentValue) {
      this.currentValue = currentValue;
      return this;
    }

    public MLogRecordBuilder content(String content) {
      this.content = content;
      return this;
    }

    public MLogRecordBuilder operator(String operator) {
      this.operator = operator;
      return this;
    }

    public MLogRecordBuilder startTime(Date operateTime) {
      this.startTime = operateTime;
      return this;
    }

    public MLogRecordBuilder endTime(Date endTime) {
      this.endTime = endTime;
      return this;
    }

    public MLogRecordBuilder business(String business) {
      this.business = business;
      return this;
    }

    public MLogRecordBuilder businessNo(String businessNo) {
      this.businessNo = businessNo;
      return this;
    }

    public MLogRecordBuilder signature(String signature) {
      this.signature = signature;
      return this;
    }

    public MLogRecordBuilder traceId(String traceId) {
      this.traceId = traceId;
      return this;
    }

    public MLogRecord build() {
      MLogRecord mLogRecord = new MLogRecord();
      mLogRecord.setNamespaceId(namespaceId);
      mLogRecord.setSuccess(success);
      mLogRecord.setException(exception);
      mLogRecord.setOriginValue(originValue);
      mLogRecord.setCurrentValue(currentValue);
      mLogRecord.setContent(content);
      mLogRecord.setOperator(operator);
      mLogRecord.setStartTime(startTime);
      mLogRecord.setEndTime(endTime);
      mLogRecord.setBusiness(business);
      mLogRecord.setBusinessNo(businessNo);
      mLogRecord.setSignature(signature);
      mLogRecord.setTraceId(traceId);
      return mLogRecord;
    }
  }
}
