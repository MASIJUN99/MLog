package com.github.MASIJUN99.mlog.annotations.processors;

import com.github.MASIJUN99.mlog.annotations.MLog;
import com.github.MASIJUN99.mlog.annotations.MLogVariable;
import com.google.auto.service.AutoService;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import org.springframework.util.StringUtils;

@SupportedAnnotationTypes("com.github.MASIJUN99.mlog.annotations.MLog")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class MLogVariableProcessor extends AbstractProcessor {

  private Messager messager;
  private JavacTrees trees;
  private TreeMaker treeMaker;
  private Names names;
  private Filer filer;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    // 消除Idea编译器警告
    processingEnv = jbUnwrap(ProcessingEnvironment.class, processingEnv);

    this.trees = JavacTrees.instance(processingEnv);
    super.init(processingEnv);
    Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
    this.treeMaker = TreeMaker.instance(context);
    this.names = Names.instance(context);
    this.messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    messager.printMessage(Kind.NOTE, "测试打印");

    roundEnv.getElementsAnnotatedWith(MLog.class).stream()
        .map(element -> trees.getTree(element))
        .forEach(method -> method.accept(new TreeTranslator() {
          @Override
          public void visitMethodDef(JCMethodDecl jcMethodDecl) {
            handleMLogMethod(jcMethodDecl);
            super.visitMethodDef(jcMethodDecl);
          }
        }));
    return true;
  }

  private void handleMLogMethod(JCMethodDecl jcMethodDecl) {

    // 遍历方法参数, 检测注解存在, 若返回的注解拼接语句不为空, 那就加
    // 此处产生的语句应放置于方法体最前方!
    ArrayList<JCStatement> statements = new ArrayList<>();

    for (JCVariableDecl param : jcMethodDecl.getParameters()) {
      JCStatement jcStatement = handleParameter(param);
      if (jcStatement != null) {
        statements.add(jcStatement);
      }
    }

    for (JCStatement statement : jcMethodDecl.getBody().getStatements()) {
      statements.add(statement);
      statement.accept(new TreeTranslator(){
        @Override
        public void visitAssign(JCAssign jcAssign) {
          JCStatement jcStatement = handleAssign(jcAssign);
          super.visitAssign(jcAssign);
        }

        @Override
        public void visitVarDef(JCVariableDecl jcVariableDecl) {
          JCStatement jcStatement = handleVarDecl(jcVariableDecl);
          if (jcStatement != null) {
            statements.add(jcStatement);
          }
          super.visitVarDef(jcVariableDecl);
        }
      });
    }
    jcMethodDecl.getBody().stats = array2List(statements);
  }


  /**
   * 处理方法参数
   */
  private JCStatement handleParameter(JCVariableDecl param) {
    for (JCAnnotation annotation : param.mods.annotations) {
      if (isMLogVariable(annotation)) {
        String key = getKey(annotation, param);
        // type list
        List<JCExpression> type = List.nil();
        // method
        JCFieldAccess method = treeMaker
            .Select(treeMaker.Ident(names.fromString("LogVariablesContext")),
                names.fromString("setVariable"));
        // var
        List<JCExpression> vars = List.of(treeMaker.Literal(key),
            treeMaker.Ident(names.fromString(param.name.toString())));
        return treeMaker.Exec(treeMaker.Apply(type, method, vars));
      }
    }
    return null;
  }

  /**
   * 处理声明变量
   */
  private JCStatement handleVarDecl(JCVariableDecl jcVariableDecl) {
    for (JCAnnotation annotation : jcVariableDecl.mods.annotations) {
      if (isMLogVariable(annotation)) {
        String key = getKey(annotation, jcVariableDecl);
        // type list
        List<JCExpression> type = List.nil();
        // method
        JCFieldAccess method = treeMaker
            .Select(treeMaker.Ident(names.fromString("LogVariablesContext")),
                names.fromString("setVariable"));
        // var
        List<JCExpression> vars = List.of(treeMaker.Literal(key),
            treeMaker.Ident(names.fromString(jcVariableDecl.name.toString())));
        return treeMaker.Exec(treeMaker.Apply(type, method, vars));
      }
    }
    return null;
  }

  /**
   * 处理变量赋值(暂时不做)
   */
  private JCStatement handleAssign(JCAssign jcAssign) {
//    messager.printMessage(Kind.NOTE, jcAssign.toString());
    return null;
  }

  /**
   * 判断注解是不是MLogVariabke
   * @param annotation 注解
   * @return 是否
   */
  private boolean isMLogVariable(JCAnnotation annotation) {
    String[] split = MLogVariable.class.getCanonicalName().split("\\.");
    return annotation.getAnnotationType().toString()
        .equalsIgnoreCase(split[split.length - 1]);
  }

  /**
   * 决定变量上注解中或者变量名的key
   * @param annotation 注解
   * @param variableDecl 变量
   * @return key
   */
  private String getKey(JCAnnotation annotation, JCVariableDecl variableDecl) {
    String value = "";
    String key = "";
    for (JCExpression arg : annotation.getArguments()) {
      if (arg instanceof JCAssign) {
        JCAssign jcAssign = (JCAssign) arg;
        if (jcAssign.lhs.toString().equalsIgnoreCase("value")) {
          String s = jcAssign.rhs.toString();
          value = s.substring(1, s.length() - 1);
        }
        if (jcAssign.lhs.toString().equalsIgnoreCase("key")) {
          String s = jcAssign.rhs.toString();
          key = s.substring(1, s.length() - 1);
        }
      }
    }
    if (StringUtils.hasText(value)) {
      return value;
    } else if (StringUtils.hasText(key)) {
      return key;
    } else {
      return variableDecl.name.toString();
    }
  }

  /**
   * Idea给出的解决方案 消除编译器警告
   */
  private static <T> T jbUnwrap(Class<? extends T> iface, T wrapper) {
    T unwrapped = null;
    try {
      final Class<?> apiWrappers = wrapper.getClass().getClassLoader().loadClass("org.jetbrains.jps.javac.APIWrappers");
      final Method unwrapMethod = apiWrappers.getDeclaredMethod("unwrap", Class.class, Object.class);
      unwrapped = iface.cast(unwrapMethod.invoke(null, iface, wrapper));
    }
    catch (Throwable ignored) {}
    return unwrapped != null? unwrapped : wrapper;
  }

  private <T> List<T> array2List(ArrayList<T> array) {
    List<T> res = List.nil();
    for (T t : array) {
      res = res.append(t);
    }
    return res;
  }

}
