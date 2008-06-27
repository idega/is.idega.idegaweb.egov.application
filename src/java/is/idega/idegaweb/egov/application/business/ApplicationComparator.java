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


public class ApplicationComparator implements Comparator {

	private Locale iLocale;
	
	public ApplicationComparator(Locale locale) {
		this.iLocale = locale;
	}
	
	public int compare(Object o1, Object o2) {
		Collator coll = Collator.getInstance(this.iLocale);
		
		Application application1 = (Application) o1;
		Application application2 = (Application) o2;
		
//		return coll.compare(application1.getName(), application2.getName());
		return coll.compare(application1.getNameByLocale(), application2.getNameByLocale());
	}
}
