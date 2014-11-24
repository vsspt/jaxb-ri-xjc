package com.github.vsspt.xjc.equals;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import com.github.vsspt.xjc.AbstractVssPluginImpl;
import com.github.vsspt.xjc.annotation.IncludeOnEqualsAndHash;
import com.github.vsspt.xjc.model.ClassRepresentation;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JVar;


public class PluginImpl extends AbstractVssPluginImpl {

  private static final String OPTION_NAME = "XvsEqualsHashCode";
  private static final String USAGE = "XvsEqualsHashCode :  inject equals";
  private static final String OPERATION_EQUALS = "equals";
  private static final String OPERATION_HASH = "hashCode";
  private static final String OPERATION_HASH_METHOD = "hash";
  private static final String OBJ = "obj";
  private static final String OTHER = "other";
  private static final int IDX_TO_REMOVE = 0;
  


  @Override
  public String getOptionName() {
    return OPTION_NAME;
  }

  @Override
  public String getUsage() {
    return USAGE;
  }

  @Override
  protected String getAnnotationName() {
    return IncludeOnEqualsAndHash.class.getName();
  }
  
  @Override
  protected boolean checkAnnotationsFields() {
    return true;
  }  


  @Override
  protected void generateMethod(final ClassRepresentation clazz, final List<JFieldVar> fields, final boolean hasSuperClassFields) {

    if (fields == null || fields.isEmpty()) {
      return;
    }
    
    final JDefinedClass implClass = clazz.getClassOutline().implClass;

    createEqualsMethod(implClass, fields, hasSuperClassFields);
    createHashCodeMethod(implClass, fields, hasSuperClassFields);
  }

  private void createEqualsMethod(final JDefinedClass implClass, final List<JFieldVar> fields, final boolean isSuperClass) {
    final JMethod method = implClass.method(JMod.PUBLIC, implClass.owner().BOOLEAN, OPERATION_EQUALS);
    method.annotate(Override.class);

    final JVar vObj = method.param(Object.class, OBJ);
    final JConditional condMe = method.body()._if(JExpr._this().eq(vObj));
    condMe._then()._return(JExpr.TRUE);

    final JConditional condNull = method.body()._if(vObj.eq(JExpr._null()));
    condNull._then()._return(JExpr.FALSE);

    if (isSuperClass) {
      final JConditional condSuper = method.body()._if(JExpr._super().invoke(OPERATION_EQUALS).arg(vObj).eq(JExpr.FALSE));
      condSuper._then()._return(JExpr.FALSE);
    }

    final JVar vOther = method.body().decl(implClass, OTHER, JExpr.cast(implClass, vObj));
    final JClass objectsClass = implClass.owner().ref(Objects.class);

    final List<JFieldVar> clonedList = new ArrayList<JFieldVar>(fields.size());
    clonedList.addAll(fields);

    final JFieldVar first = clonedList.remove(IDX_TO_REMOVE);
    final JBlock block = new JBlock();
    JExpression invocation = block.staticInvoke(objectsClass, OPERATION_EQUALS).arg(JExpr.ref(first.name())).arg(vOther.ref(first.name()));

    for (final JFieldVar jFieldVar : clonedList) {
      invocation = JOp.cand(invocation, block.staticInvoke(objectsClass, OPERATION_EQUALS).arg(JExpr.ref(jFieldVar.name())).arg(vOther.ref(jFieldVar.name())));
    }

    method.body()._return(invocation);

  }

  private void createHashCodeMethod(final JDefinedClass implClass, final List<JFieldVar> fields, final boolean isSuperClass) {
    final JMethod method = implClass.method(JMod.PUBLIC, implClass.owner().INT, OPERATION_HASH);
    method.annotate(Override.class);

    final JClass objectsClass = implClass.owner().ref(Objects.class);
    final JBlock block = new JBlock();
    final JInvocation invocation = block.staticInvoke(objectsClass, OPERATION_HASH_METHOD);

    for (final JFieldVar jFieldVar : fields) {
      invocation.arg(JExpr.ref(jFieldVar.name()));
    }

    if (isSuperClass) {
      invocation.arg(JExpr._super().invoke(OPERATION_HASH));
    }

    method.body()._return(invocation);
  }

}
