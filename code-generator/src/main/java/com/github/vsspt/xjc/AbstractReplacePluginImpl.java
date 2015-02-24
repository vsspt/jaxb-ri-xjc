package com.github.vsspt.xjc;

import java.util.List;

import org.jvnet.jaxb2_commons.util.FieldAccessorUtils;

import com.github.vsspt.xjc.model.ClassRepresentation;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;

public abstract class AbstractReplacePluginImpl extends AbstractVssPluginImpl {

	private static final String OPERATION_GET_PREFIX = "get";

	private static final String OPERATION_SET_PREFIX = "set";

	private static final String PARAM_NAME = "value";

	protected abstract <T> Class<T> getInterfaceClass();

	protected abstract <T> Class<T> getImplementationClass();

	protected abstract <T> Class<T> getReplaceableClass();

	protected abstract boolean checkNull();

	protected abstract boolean assignOnFieldDeclaration();

	@Override
	protected String getAnnotationName() {
		return null;
	}

	@Override
	protected boolean checkAnnotationsFields() {
		return false;
	}

	@Override
	protected void generateMethod(final ClassRepresentation clazz, final List<JFieldVar> includeFields, final boolean hasSuperClassFields) {

		final ClassOutline co = clazz.getClassOutline();

		for (final FieldOutline fo : co.getDeclaredFields()) {

			if (fo.getRawType().fullName().startsWith(getReplaceableClass().getName())) {

				final JFieldVar field = FieldAccessorUtils.field(fo);
				final List<JClass> typeParameters = ((JClass) field.type()).getTypeParameters();
				final JClass inner = typeParameters.size() > 0 ? typeParameters.get(0) : null;
				final JType setType = inner == null ? co.parent().getCodeModel().ref(getInterfaceClass()) : co.parent().getCodeModel().ref(getInterfaceClass()).narrow(inner);

				field.type(setType);

				if (assignOnFieldDeclaration()) {
					final JType classType = co.parent().getCodeModel().ref(getImplementationClass());
					field.assign(JExpr._new(classType));
				}

				replaceGetter(co, field, setType);
				replaceSetter(co, field, setType);
			}
		}

	}

	private void replaceGetter(final ClassOutline co, final JFieldVar field, final JType setType) {

		// Create the method name
		final String methodName = OPERATION_GET_PREFIX + capitalizeFirstLetter(field.name());

		// Find and remove Old Getter!
		final JMethod oldGetter = co.implClass.getMethod(methodName, new JType[0]);
		co.implClass.methods().remove(oldGetter);

		final JMethod getter = co.implClass.method(JMod.PUBLIC, setType, methodName);

		if (checkNull()) {
			final JType classType = co.parent().getCodeModel().ref(getImplementationClass());

			getter.body()._if(JExpr.ref(field.name()).eq(JExpr._null()))._then().assign(field, JExpr._new(classType));
		}

		getter.body()._return(JExpr.ref(field.name()));
	}

	private void replaceSetter(final ClassOutline co, final JFieldVar field, final JType setType) {

		// Create the method name
		final String methodName = OPERATION_SET_PREFIX + capitalizeFirstLetter(field.name());

		// Create Date JType
		final JType dateType = co.parent().getCodeModel().ref(getReplaceableClass());

		// Find and remove Old setter!
		final JMethod oldSetter = co.implClass.getMethod(methodName, new JType[] { dateType });
		co.implClass.methods().remove(oldSetter);

		// Create New Setter
		final JMethod setter = co.implClass.method(JMod.PUBLIC, Void.TYPE, methodName);
		final JVar var = setter.param(JMod.FINAL, setType, PARAM_NAME);

		assignSetterValue(co.implClass, field, setter, var);
	}

	protected void assignSetterValue(final JDefinedClass implClass, final JFieldVar field, final JMethod setter, final JVar var) {
		setter.body().assign(field, var);
	}

	private String capitalizeFirstLetter(final String value) {
		return value == null ? null : value.substring(0, 1).toUpperCase() + value.substring(1);
	}
}
