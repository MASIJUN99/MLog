package com.github.MASIJUN99.mlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.LOCAL_VARIABLE, ElementType.PARAMETER, ElementType.TYPE})
public @interface MLogVariable {

  @AliasFor("key")
  String value() default "";

  @AliasFor("value")
  String key() default "";

}
