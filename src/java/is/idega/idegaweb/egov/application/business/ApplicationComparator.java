/*
 * $Id$
 * Created on Jan 15, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.business;

import is.idega.idegaweb.egov.application.data.Application;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class ApplicationComparator implements Comparator<Application> {

	private Locale locale = null;
	private Collator collator = null;

	public ApplicationComparator(Locale locale) {
		this.locale = locale;
		collator = Collator.getInstance(locale);
	}

	@Override
	public int compare(Application app1, Application app2) {
		return collator.compare(app1.getNameByLocale(locale), app2.getNameByLocale(locale));
	}

}