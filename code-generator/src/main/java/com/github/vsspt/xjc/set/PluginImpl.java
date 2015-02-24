package com.github.vsspt.xjc.set;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.vsspt.xjc.AbstractReplacePluginImpl;

public class PluginImpl extends AbstractReplacePluginImpl {

	private static final String OPTION_NAME = "XvsHashset";

	private static final String USAGE = "XvsHashset :  replaces List by java.util.HashSet";

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
		return false;
	}

	@Override
	protected boolean assignOnFieldDeclaration() {
		return true;
	}
}
