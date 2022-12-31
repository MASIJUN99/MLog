package com.github.MASIJUN99.mlog.context;

import static com.github.MASIJUN99.mlog.constant.LogVariablesConstant.currentValueKey;
import static com.github.MASIJUN99.mlog.constant.LogVariablesConstant.originValueKey;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import org.springframework.util.SerializationUtils;

public class LogVariablesContext {

  private static Boolean flag = false;

  private static Stack<Method> callStack = new Stack<>();

  private static final ThreadLocal<Stack<Map<String, Object>>> threadLocal =
      ThreadLocal.withInitial(Stack::new);

  /**
   * 进入新方法时，为threadlocal添加一个新的Map
   */
  public static void push() {
    Stack<Map<String, Object>> stack = threadLocal.get();
    stack.push(new HashMap<>());
  }

  /**
   * 方法结束时，删除一个map
   */
  public static void pop() {
    Stack<Map<String, Object>> stack = threadLocal.get();
    stack.pop();
  }

  /**
   * 设置上下文变量
   * @param key 键
   * @param value 值
   */
  public synchronized static <T extends Serializable> void setVariable(String key, T value) {
    Stack<Map<String, Object>> stack = threadLocal.get();
    Map<String, Object> map = stack.peek();
    map.put(key, SerializationUtils.deserialize(SerializationUtils.serialize(value)));
//    threadLocal.set(map);
  }

  /**
   * 设置上下文变量
   * @param map 变量映射关系
   */
  public synchronized static void setVariables(Map<String, ? extends Serializable> map) {
    for (Entry<String, ? extends Serializable> entry : map.entrySet()) {
      setVariable(entry.getKey(), entry.getValue());
    }
  }

  /**
   * 获得某个变量
   * @param key 键
   */
  public static Object getVariable(String key) {
    Stack<Map<String, Object>> stack = threadLocal.get();
    Map<String, Object> map = stack.peek();
    return map.get(key);
  }

  /**
   * 获得所有变量
   * @return 映射map
   */
  public static Map<String, Object> getAllVariables() {
    return threadLocal.get().peek();
  }

  /**
   * 设置业务实体原来的值(特殊变量)
   * @param originValue 值
   */
  public synchronized static <T extends Serializable> void setOriginValue(T originValue) {
    setVariable(originValueKey, originValue);
  }

  /**
   * 获得业务实体原来的值
   * @return 值
   */
  public static Object getOriginValue() {
    return getVariable(originValueKey);
  }

  /**
   * 设置业务实体现在的值(特殊变量)
   * @param currentValue 值
   */
  public synchronized static <T extends Serializable> void setCurrentValue(T currentValue) {
    setVariable(currentValueKey, currentValue);
  }

  /**
   * 获得业务实体现在的值
   * @return 值
   */
  public static Object getCurrentValue() {
    return getVariable(currentValueKey);
  }

  /**
   * 是否是被嵌套调用的日志
   * @return 是/否
   */
  public static Boolean isRecursion() {
    return flag;
  }

  /**
   * 设置被嵌套调用
   */
  public static void setRecursionFlag() {
    LogVariablesContext.flag = true;
  }

  /**
   * 销毁
   */
  public static void destroy() {
    flag = false;
    callStack = new Stack<>();
    threadLocal.remove();
  }

}
