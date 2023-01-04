package com.github.MASIJUN99.mlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.LOCAL_VARIABLE, ElementType.PARAMETER, ElementType.TYPE})
public @interface MLogVariable {

  /**
   * key in LogVariableContext
   */
  @AliasFor("key")
  String value() default "";

  /**
   * alias name for value()
   */
  @AliasFor("value")
  String key() default "";

}
