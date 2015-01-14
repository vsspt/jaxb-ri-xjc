package com.github.vsspt.xjc.setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.vsspt.xjc.AbstractVssPluginImpl;
import com.github.vsspt.xjc.annotation.Setter;
import com.github.vsspt.xjc.model.ClassRepresentation;
import com.google.common.collect.ImmutableList;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class PluginImpl extends AbstractVssPluginImpl {

  private static final String OPTION_NAME = "XvsSetter";
  private static final String USAGE = "XvsSetter :  create setter accessors";
  private static final String OPERATION_PREFIX = "set";
  private static final String PARAM_NAME = "value";

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
    return Setter.class.getName();
  }

  @Override
  protected boolean checkAnnotationsFields() {
    return true;
  }

  @Override
  protected void generateMethod(final ClassRepresentation clazz, final List<JFieldVar> includeFields, final boolean hasSuperClassFields) {

    final JDefinedClass implClass = clazz.getClassOutline().implClass;

    final List<JFieldVar> clonedList = new ArrayList<JFieldVar>();
    final Collection<JFieldVar> fields = implClass.fields().values();

    if (fields != null && !fields.isEmpty()) {
      clonedList.addAll(implClass.fields().values());

      for (final JFieldVar field : clonedList) {
        if (includeFields != null && includeFields.contains(field)) {

          final JMethod method = implClass.method(JMod.PUBLIC, Void.TYPE, OPERATION_PREFIX + capitalizeFirstLetter(field.name()));
          final JVar var = method.param(JMod.FINAL, field.type(), PARAM_NAME);

          final JBlock block = new JBlock();
          final JClass immutableListClass = implClass.owner().ref(ImmutableList.class);
          final JInvocation invocation = block.staticInvoke(immutableListClass, "copyOf");

          invocation.arg(var);

          method.body().assign(JExpr._this().ref(field), invocation);

        }
      }
    }
  }

  private String capitalizeFirstLetter(final String value) {
    return value == null ? null : value.substring(0, 1).toUpperCase() + value.substring(1);
  }
}
