package com.github.vsspt.xjc.set;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.vsspt.xjc.AbstractReplacePluginImpl;
import com.google.common.collect.ImmutableSet;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class PluginImpl extends AbstractReplacePluginImpl {

	private static final String OPTION_NAME = "XvsHashset";

	private static final String USAGE = "XvsHashset :  replaces List by java.util.HashSet";

	private static final String COPY_OF = "copyOf";

	@Override
	public String getOptionName() {
		return OPTION_NAME;
	}

	@Override
	public String getUsage() {
		return USAGE;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> Class<T> getInterfaceClass() {
		return (Class<T>) Set.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> Class<T> getImplementationClass() {
		return (Class<T>) HashSet.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> Class<T> getReplaceableClass() {
		return (Class<T>) List.class;
	}

	@Override
	protected boolean checkNull() {
		return true;
	}

	@Override
	protected void assignSetterValue(final JDefinedClass implClass, final JFieldVar field, final JMethod method, final JVar var) {

		final JBlock block = new JBlock();
		final JClass immutableListClass = implClass.owner().ref(ImmutableSet.class);
		final JInvocation invocation = block.staticInvoke(immutableListClass, COPY_OF);

		invocation.arg(var);

		method.body().assign(JExpr._this().ref(field), invocation);
	}

}
