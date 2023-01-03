package com.github.MASIJUN99.mlog.annotations.processors;

import com.github.MASIJUN99.mlog.annotations.MLog;
import com.github.MASIJUN99.mlog.annotations.MLogVariable;
import com.google.auto.service.AutoService;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
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

  private ArrayList<JCStatement> handleMLogMethod(JCMethodDecl jcMethodDecl) {
    ArrayList<JCStatement> statements = new ArrayList<>();
    jcMethodDecl.getBody().getStatements().forEach(state -> {
      if (state instanceof JCVariableDecl) {
        JCVariableDecl jcVariableDecl = (JCVariableDecl) state;
        for (JCAnnotation annotation : jcVariableDecl.mods.annotations) {
          if (isMLogVariable(annotation)) {
            String key = getKey(annotation, jcVariableDecl);
            // type list
//        List<JCExpression> type = List.of(memberAccess("java.lang.String"), memberAccess("java.lang.Object"));
            List<JCExpression> type = List.nil();
            // method
            JCFieldAccess method = treeMaker
                .Select(treeMaker.Ident(names.fromString("LogVariablesContext")),
                    names.fromString("setVariable"));
            // var
            List<JCExpression> vars = List.of(treeMaker.Literal(key),
                treeMaker.Ident(names.fromString(jcVariableDecl.name.toString())));
            JCExpressionStatement exec = treeMaker.Exec(treeMaker.Apply(type, method, vars));
            statements.add(exec);
          }
        }
      }
    });
    for (JCStatement statement : statements) {
      messager.printMessage(Kind.NOTE, statement.toString());
      jcMethodDecl.getBody().stats = jcMethodDecl.getBody().getStatements().append(statement);
//      messager.printMessage(Kind.NOTE, jcMethodDecl.getBody().getStatements().toString());
    }
//    messager.printMessage(Kind.WARNING, jcMethodDecl.getBody().stats.toString());
    return statements;
  }

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
        JCExpressionStatement exec = treeMaker.Exec(treeMaker.Apply(type, method, vars));
        return exec;
      }
    }
    return null;
  }

  private boolean isMLogVariable(JCAnnotation annotation) {
    String[] split = MLogVariable.class.getCanonicalName().split("\\.");
    return annotation.getAnnotationType().toString()
        .equalsIgnoreCase(split[split.length - 1]);
  }

  private String getKey(JCAnnotation annotation, JCVariableDecl variableDecl) {
    final String[] value = {""};
    final String[] key = {""};
    for (JCExpression argument : annotation.getArguments()) {
      argument.accept(new TreeTranslator(){
        @Override
        public void visitAssign(JCAssign jcAssign) {
          if (jcAssign.lhs.toString().equalsIgnoreCase("value")) {
            String s = jcAssign.rhs.toString();
            value[0] = s.substring(1, s.length() - 1);
          }
          if (jcAssign.lhs.toString().equalsIgnoreCase("key")) {
            String s = jcAssign.rhs.toString();
            key[0] = s.substring(1, s.length() - 1);
          }
          super.visitAssign(jcAssign);
        }
      });
    }
    if (StringUtils.hasText(value[0])) {
      return value[0];
    } else if (StringUtils.hasText(key[0])) {
      return key[0];
    } else {
      return variableDecl.name.toString();
    }
  }

  private JCExpression memberAccess(String className) {
    String[] array = className.split("\\.");
    JCExpression expression = treeMaker.Ident(names.fromString(array[0]));
    for (int i = 1; i < array.length; i++) {
      expression = treeMaker.Select(expression, names.fromString(array[i]));
    }
    return expression;
  }

}
