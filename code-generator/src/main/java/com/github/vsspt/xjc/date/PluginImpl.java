package com.github.vsspt.xjc.date;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import com.github.vsspt.xjc.AbstractReplacePluginImpl;

public class PluginImpl extends AbstractReplacePluginImpl {

	private static final String OPTION_NAME = "XvsDate";

	private static final String USAGE = "XvsDate :  replaces XMLCalendar by java.util.Date";

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
		return (Class<T>) Date.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> Class<T> getImplementationClass() {
		return (Class<T>) Date.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> Class<T> getReplaceableClass() {
		return (Class<T>) XMLGregorianCalendar.class;
	}

	@Override
	protected boolean checkNull() {
		return false;
	}

}
