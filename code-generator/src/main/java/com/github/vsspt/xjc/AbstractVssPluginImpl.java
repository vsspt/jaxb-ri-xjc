package com.github.vsspt.xjc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;


import com.github.vsspt.xjc.model.ClassRepresentation;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

public abstract class AbstractVssPluginImpl extends Plugin {

  protected final static String SEPARATOR = ",";

  protected abstract String getAnnotationName();
  
  protected abstract boolean checkAnnotationsFields();  

  protected abstract void generateMethod(ClassRepresentation clazz, List<JFieldVar> fields, boolean hasSuperClassFields);

  @Override
  public boolean run(final Outline outline, final Options options, final ErrorHandler arg2) throws SAXException {

    final Map<String, ClassRepresentation> classes = new HashMap<String, ClassRepresentation>();

    for (final ClassOutline classOutline : outline.getClasses()) {

      final ClassRepresentation classRepresentation = new ClassRepresentation(classOutline, getAnnotationName(), checkAnnotationsFields());

      if (classRepresentation.isValidForAugument()) {
        classes.put(classRepresentation.getName(), classRepresentation);
      }
    }

    generateCode(outline, classes);

    return true;
  }

  protected void generateCode(final Outline outline, final Map<String, ClassRepresentation> classes) {
    for (final ClassOutline classOutline : outline.getClasses()) {
      final String key = classOutline.implClass.name();
      final ClassRepresentation clazz = classes.get(key);
      if (clazz != null) {

        final JDefinedClass implementationClass = classOutline.implClass;

        final Map<String, List<JFieldVar>> fields = new HashMap<String, List<JFieldVar>>();
        for (final JFieldVar field : implementationClass.fields().values()) {

          if (clazz.getFieldsFromAnnotations().contains(field.name())) {
            List<JFieldVar> classFields = fields.get(key);
            if (classFields == null) {
              classFields = new ArrayList<JFieldVar>();
            }
            classFields.add(field);

            fields.put(key, classFields);
          }
        }
        generateMethod(clazz, fields.get(implementationClass.name()), checkForSuperClassFields((ClassOutlineImpl) classOutline, fields));
      }
    }
  }

  protected boolean checkForSuperClassFields(final ClassOutlineImpl classOutline, final Map<String, List<JFieldVar>> classFields) {
    if (classOutline.getSuperClass() == null) {
      return false;
    }
    final String key = classOutline.getSuperClass().implClass.name();
    final List<JFieldVar> fields = classFields.get(key);

    return fields == null || fields.isEmpty();

  }
}
