/*
 * $Id$
 * Created on Oct 30, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.include.ExternalLink;
import com.idega.idegaweb.include.GlobalIncludeManager;


public class IWBundleStarter implements IWBundleStartable {

	public void start(IWBundle starterBundle) {
		GlobalIncludeManager.getInstance().addBundleStyleSheet("is.idega.idegaweb.egov.application", "/style/application.css", ExternalLink.MEDIA_SCREEN);
		GlobalIncludeManager.getInstance().addBundleStyleSheet("is.idega.idegaweb.egov.application", "/style/application-print.css", ExternalLink.MEDIA_PRINT);
	}

	public void stop(IWBundle starterBundle) {
	}
}