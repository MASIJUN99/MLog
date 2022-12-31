package com.github.MASIJUN99.mlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  // 运行时可使用反射获取
@Target(ElementType.METHOD)
public @interface MLog {

  /**
   * 日志操作的发生者，否则为实现OperatorService接口的方法
   */
  String operator() default "";

  /**
   * 成功时日志模板, SpEL模板
   */
  String success();

  /**
   * 失败时日志模板, SpEL模板
   */
  String fail() default "操作执行失败";

  /**
   * 实体类原来的值, SpEL模板
   */
  String originValue() default "";

  /**
   * 实体类更新后的值, SpEL模板
   */
  String currentValue() default "";

  /**
   * 业务实体的类
   */
  Class<?> business();

  /**
   * 业务实体唯一id, 一般即id字段
   */
  String businessNo() default "";

  /**
   * 触发日志发生的条件，SpEL模板，默认触发，若为否触发LogService中的callback
   */
  String condition() default "";

}
