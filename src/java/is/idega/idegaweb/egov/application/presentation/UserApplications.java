/*
 * $Id$
 * Created on Jan 14, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import com.idega.business.IBORuntimeException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Lists;
import com.idega.util.Age;


public class UserApplications extends ApplicationBlock {

	private String id = "userApplicationViewer";

	public void main(IWContext iwc) throws Exception {
		if (iwc.isLoggedOn()) {
			try {
				Layer layer = new Layer();
				layer.setId(id);
	
				Age[] ages = getApplicationBusiness(iwc).getAgesForUserAndChildren(iwc.getCurrentUser());
				boolean checkAges = (ages != null);
	
				Collection applications = getApplicationBusiness(iwc).getUserApplications(iwc.getCurrentUser());
				if (!applications.isEmpty()) {
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
	}
	
	public void setId(String id) {
		super.setId(id + "_1");
		this.id = id;
	}
}