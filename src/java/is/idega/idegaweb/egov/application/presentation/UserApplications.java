/*
 * $Id$ Created on Jan 14, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import is.idega.idegaweb.egov.application.business.ApplicationComparator;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.idega.business.IBORuntimeException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Lists;
import com.idega.util.Age;

public class UserApplications extends ApplicationBlock {

	private String layerID = "userApplicationViewer";

	protected String getUniqueIdentifier() {
		return "userApplications";
	}

	public void present(IWContext iwc) throws Exception {
		try {
			Layer layer = new Layer();
			layer.setId(this.layerID);
			Age[] ages = getApplicationBusiness(iwc).getAgesForUserAndChildren(iwc.getCurrentUser());
			boolean checkAges = useAgeRestriction(iwc) && (ages != null);
			List applications = new ArrayList(getApplicationBusiness(iwc).getUserApplications(iwc.getCurrentUser()));
			if (!applications.isEmpty()) {
				Collections.sort(applications, new ApplicationComparator(iwc.getCurrentLocale()));
				Lists list = getApplicationList(iwc, checkAges, applications, ages);
				if (list.getChildrenCount() > 0) {
					layer.add(list);
				}
			}
			add(layer);
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	public void setLayerID(String id) {
		this.layerID = id;
	}
}