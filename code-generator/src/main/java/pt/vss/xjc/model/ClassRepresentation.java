package pt.vss.xjc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.outline.ClassOutline;

public class ClassRepresentation {

  private final ClassOutline classOutline;
  private final String annotationOption;
  private final String name;
  private final List<String> classFields = new ArrayList<String>();
  private final List<String> fieldsFromAnnotations = new ArrayList<String>();
  private final boolean checkAnnotationfields;

  public ClassRepresentation(final ClassOutline classOutline, final String annotationOption, final boolean checkAnnotationfields) {
    this.classOutline = classOutline;
    this.annotationOption = annotationOption;
    name = classOutline.implClass.name();
    this.checkAnnotationfields = checkAnnotationfields;
    process();
  }

  public ClassOutline getClassOutline() {
    return classOutline;
  }

  public String getName() {
    return name;
  }

  public List<String> getClassFields() {
    return classFields;
  }

  public List<String> getFieldsFromAnnotations() {
    return fieldsFromAnnotations;
  }

  public boolean isValidForAugument() {
    return checkAnnotationfields == false ? true : !fieldsFromAnnotations.isEmpty();
  }

  @Override
  public String toString() {
    return "Name = " + name + ", classFields = " + classFields + ", fieldsFromAnnotations= " + fieldsFromAnnotations;
  }

  private void process() {

    for (final JFieldVar jfield : classOutline.implClass.fields().values()) {

      final Collection<JAnnotationUse> annotations = jfield.annotations();
      for (final JAnnotationUse annotation : annotations) {
        final String fullclassName = annotation.getAnnotationClass().fullName();
        if (isAnnotationEligible(fullclassName) && !fieldsFromAnnotations.contains(fullclassName)) {
          fieldsFromAnnotations.add(jfield.name());
        }
      }

      final String checkedValue = jfield.name();
      if (!classFields.contains(checkedValue)) {
        classFields.add(checkedValue);
      }
    }
  }

  private boolean isAnnotationEligible(final String annotationName) {
    return annotationOption == null ? false : annotationOption.equals(annotationName);
  }

}
