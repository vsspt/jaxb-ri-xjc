package com.github.vsspt.xjc.serializable;

import java.io.Serializable;
import java.util.List;


import com.github.vsspt.xjc.AbstractVssPluginImpl;
import com.github.vsspt.xjc.model.ClassRepresentation;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;

public class PluginImpl extends AbstractVssPluginImpl {

  private static final String OPTION_NAME = "XvsSerializable";
  private static final String USAGE = "XvsSerializable :  inject Serializable";
  private static final String VALUE = "value";
  private static final String SERIAL = "serial";

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
    return null;
  }

  @Override
  protected boolean checkAnnotationsFields() {
    return false;
  }

  @Override
  protected void generateMethod(final ClassRepresentation clazz, final List<JFieldVar> excludeFields, final boolean hasSuperClassFields) {

    final JDefinedClass implClass = clazz.getClassOutline().implClass;
    implClass._implements(Serializable.class);
    implClass.annotate(SuppressWarnings.class).param(VALUE, SERIAL);
  }
}
