package is.idega.idegaweb.egov.application;

import is.idega.idegaweb.egov.application.presentation.ApplicationCreator;

import java.util.ArrayList;
import java.util.Collection;
import javax.faces.context.FacesContext;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Singleton;
import com.idega.workspace.view.WorkspaceApplicationNode;
import com.idega.workspace.view.WorkspaceClassViewNode;

public class ApplicationViewManager implements Singleton  {

	private static final String IW_APPLICATION_VIEW_MANAGER_KEY = "iw_egovapplicationviewmanager";
	private static final String GROUP_ID = "eGov";
	
	private ViewNode rootNode;
	private IWMainApplication iwma;
	
	private ApplicationViewManager(IWMainApplication iwma){
		
		this.iwma = iwma;
	}

	public static synchronized ApplicationViewManager getInstance(IWMainApplication iwma) {
		ApplicationViewManager application_view_manager = (ApplicationViewManager) iwma.getAttribute(IW_APPLICATION_VIEW_MANAGER_KEY);
		
		if (application_view_manager == null) {
			application_view_manager = new ApplicationViewManager(iwma);
			iwma.setAttribute(IW_APPLICATION_VIEW_MANAGER_KEY, application_view_manager);
	    }
	    return application_view_manager;
	}	
	
	public static ApplicationViewManager getInstance(FacesContext context){
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(context);
		
		return getInstance(iwma);
	}
	
	public ViewManager getViewManager(){
		return ViewManager.getInstance(iwma);
	}
	
	
	public ViewNode getContentNode(){
		
		IWBundle iwb = iwma.getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER);
		
		if(rootNode == null) {
			rootNode = initalizeContentNode(iwb);
		}
		return rootNode;
	}
	
	public ViewNode initalizeContentNode(IWBundle contentBundle) {
		
		ViewManager viewManager = ViewManager.getInstance(iwma);
		ViewNode workspace = viewManager.getWorkspaceRoot();
		
		Collection roles = new ArrayList();
		roles.add(StandardRoles.ROLE_KEY_ADMIN);
		
		DefaultViewNode egovNode = new WorkspaceApplicationNode(GROUP_ID, workspace, roles);
		//devNode.setName("#{localizedStrings['com.idega.developer']['developer']}");
		
		WorkspaceClassViewNode applicationsNode = new WorkspaceClassViewNode("egovapp", egovNode);
		applicationsNode.setName("Applications");
		applicationsNode.setComponentClass(ApplicationCreator.class);
		applicationsNode.setMaximizeBlockVertically(true);
		
		return egovNode;
	}
}
