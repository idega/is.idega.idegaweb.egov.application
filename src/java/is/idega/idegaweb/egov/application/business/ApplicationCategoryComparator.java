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

import is.idega.idegaweb.egov.application.data.ApplicationCategory;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;


public class ApplicationCategoryComparator implements Comparator {

	private Locale iLocale;
	
	public ApplicationCategoryComparator(Locale locale) {
		this.iLocale = locale;
	}
	
	public int compare(Object o1, Object o2) {
		Collator coll = Collator.getInstance(this.iLocale);
		
		ApplicationCategory category1 = (ApplicationCategory) o1;
		ApplicationCategory category2 = (ApplicationCategory) o2;
		
		return coll.compare(category1.getName(), category2.getName());
	}
}
