# 开发日志

## 项目背景

反正就是，领导有想法，我就给他想办法做，正好遇到了美团的解决方案，就想办法实现，过程中有很多困难，我来一点一点记录。

## SPEL

Spring Expression Language（简称 SpEL）。是一种功能强大的表达式语言，支持运行时查询和操作对象图 。表达式语言一般是用最简单的形式完成最主要的工作，以此减少工作量。

实际上我们学习Spring时应该都学过，但是实际工作中，正常业务用SPEL的几率还是很低的。

如果硬要说，我们比较常用的SPEL是`@Value`注解，通过SPEL向属性赋值。

举个SPEL的小例子

```java
ExpressionParser parser = new SpelExpressionParser();
Expression exp = parser.parseExpression("'Hello World'.concat('!')"); 
String message = (String) exp.getValue();
```
`message`值为`Hello World!`

### TemplateParserContext

通过名称，我们能大概了解到这个类的作用，其实就是一个模板解析器，我们通过配置他的Suffix和Prefix来自定义我们的SPEL模板解析规则。

虽然提供了自定义的方法，但是我建议不要改。

### EvaluationContext

这个接口十分重要，直译过来是“求值上下文”。

我抛出一个问题，其实大家就能理解为什么这个类这么重要了。

刚刚的例子里，我们通过调用`String.concat()`方法拼接了一个字符串，其实更多时候我们希望SPEL能读取我们自定义变量，例如`#{name}`

那么，SPEL解析过程中，他该去哪找这个`name`变量呢？我们的`name`变量该定义在哪呢？

其实这就是这个接口的意义。他为SPEL解析的过程提供变量上下文，甚至是某些方法。

#### StandardEvaluationContext

我不复杂化问题，使用最基础的`StandardEvaluationContext`。

在使用过程中，我也遇到了问题，我在创建这个类的时候，他有有参构造，要求传入`Object root`。那么什么是这个`root`呢？

我在单元测试中测了几次，我发现他是这样的：

当我传入root时，我SPEL表达式可以直接获取我传入类的属性，例如我传入`User`，我可以直接通过`#{username}`获取到`username`。
当我通过`setVariable("user", user)`的方式，那我就只能通过`#{#user.username}`来获取

#### MethodResolver

TODO

## AOP

AOP接触不会少，我采用“环绕通知”的方法，进行切面。

### 获取注解

我目的是通过注解的方式，告诉AOP我的日志模板，然后AOP调用SPEL解释我的模板。

那我的第一步就是获取注解。代码如下：

```java
// 2.2.1 获取方法签名
MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
// 2.2.2 通过字节码对象以及方法签名获取目标方法
Method method = target.getClass().getMethod(signature.getName(), signature.getParameterTypes());
// 2.2.3 获取方法上的自定义注解
MLog mLog = method.getAnnotation(MLog.class);
```

> 我也不知道为什么ProceedingJoinPoint要处理的这么麻烦，也没深耕这部分的源码，都是执行方法，如果能像Method一样，直接获取多好呢？

### 解析注解



## 参考来源

1. [Spring SpEL表达式语言](http://c.biancheng.net/spring/spel.html)

2. [Spring Expression Language (SpEL)](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions)
