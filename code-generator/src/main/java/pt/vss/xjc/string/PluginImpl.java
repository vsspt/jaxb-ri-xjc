package pt.vss.xjc.string;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.vss.xjc.AbstractVssPluginImpl;
import pt.vss.xjc.annotation.ExcludeOnToString;
import pt.vss.xjc.model.ClassRepresentation;

import com.google.common.collect.Iterables;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JOp;

public class PluginImpl extends AbstractVssPluginImpl {

  private static final String OPTION_NAME = "XvsToString";
  private static final String USAGE = "XvsToString :  inject ToString";
  private static final String OPERATION = "toString";

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
    return ExcludeOnToString.class.getName();
  }

  @Override
  protected void generateMethod(final ClassRepresentation clazz, final List<JFieldVar> excludeFields, final boolean hasSuperClassFields) {

    final JDefinedClass implClass = clazz.getClassOutline().implClass;

    final JMethod mehtod = implClass.method(JMod.PUBLIC, String.class, OPERATION);
    mehtod.annotate(Override.class);

    JExpression invocation = null;

    if (hasSuperClassFields) {
      invocation = JExpr._super().invoke(OPERATION);
    }

    final List<JFieldVar> clonedList = new ArrayList<JFieldVar>();
    final Collection<JFieldVar> fields = implClass.fields().values();

    if (fields != null && !fields.isEmpty()) {
      clonedList.addAll(implClass.fields().values());

      final JFieldVar last = Iterables.getLast(clonedList);

      for (final JFieldVar field : clonedList) {

        if (excludeFields == null || !excludeFields.contains(field)) {
          final String lastChar = field.name().equals(last.name()) ? ") " : "), ";

          if (invocation != null) {
            invocation = JOp.plus(invocation, JExpr.lit("(" + field.name() + " = ")).plus(JExpr.ref(field.name())).plus(JExpr.lit(lastChar));
          } else {
            invocation = JExpr.lit("(" + field.name() + " = ").plus(JExpr.ref(field.name())).plus(JExpr.lit(lastChar));
          }
        }
      }
    }

    final JExpression returnValue = invocation == null ? JExpr.lit("") : invocation;
    mehtod.body()._return(returnValue);
  }

}
