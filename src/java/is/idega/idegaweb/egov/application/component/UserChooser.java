package is.idega.idegaweb.egov.application.component;

import is.idega.idegaweb.egov.application.ApplicationConstants;
import is.idega.idegaweb.egov.application.bean.Applicant;
import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.data.Application;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.FinderException;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.util.Age;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.PresentationUtil;

public class UserChooser extends IWBaseComponent {

	private String chosenUser;
	private Collection<Applicant> children;
	
	private String parameterName;
	
	private Object applicationPK;
	private String caseCode;
	
	private boolean setToSubmit = false;
	private boolean disabled = false;
	private String beforeSubmit;
	
	private static final String CHOSEN_USER_PROPERTY = "chosenUser";
	private static final String CHILDREN_PROPERTY = "children";
	private static final String PARAMETER_NAME_PROPERTY = "parameterName";
	private static final String APPLICATION_PK_PROPERTY = "applicationPK";
	private static final String CASE_CODE_PROPERTY = "caseCode";
	private static final String SET_TO_SUBMIT_PROPERTY = "setToSubmit";
	private static final String DISABLED_PROPERTY = "disabled";

	public String getBundleIdentifier() {
		return ApplicationConstants.IW_BUNDLE_IDENTIFIER;
	}

	@Override
	protected void initializeComponent(FacesContext context) {
		IWContext iwc = IWContext.getIWContext(context);
		handleExpressions(context);

		PresentationUtil.addStyleSheetToHeader(iwc, iwc.getIWMainApplication().getBundle(ApplicationConstants.IW_BUNDLE_IDENTIFIER).getVirtualPathWithFileNameString("style/application.css"));
		IWResourceBundle iwrb = getBundle(context, getBundleIdentifier()).getResourceBundle(iwc.getCurrentLocale());
		
		if (children != null) {
			add(getUserChooser(iwc, iwrb));
		}
	}
	
	private void handleExpressions(FacesContext context) {
		ValueExpression ve = getValueExpression(CHOSEN_USER_PROPERTY);
    	if (ve != null) {
	    	String user = (String) ve.getValue(context.getELContext());
	    	setChosenUser(user);
    	}    	

		ve = getValueExpression(CHILDREN_PROPERTY);
    	if (ve != null) {
	    	Collection children = (Collection) ve.getValue(context.getELContext());
	    	setChildren(children);
    	}    	

		ve = getValueExpression(PARAMETER_NAME_PROPERTY);
    	if (ve != null) {
	    	String parameterName = (String) ve.getValue(context.getELContext());
	    	setParameterName(parameterName);
    	}    	

		ve = getValueExpression(APPLICATION_PK_PROPERTY);
    	if (ve != null) {
	    	Object applicationPK = ve.getValue(context.getELContext());
	    	setApplicationPK(applicationPK);
    	}    	

		ve = getValueExpression(CASE_CODE_PROPERTY);
    	if (ve != null) {
	    	String caseCode = (String) ve.getValue(context.getELContext());
	    	setCaseCode(caseCode);
    	}
    	
		ve = getValueExpression(SET_TO_SUBMIT_PROPERTY);
    	if (ve != null) {
	    	boolean setToSubmit = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
    		setSetToSubmit(setToSubmit);
    	}    

		ve = getValueExpression(DISABLED_PROPERTY);
    	if (ve != null) {
	    	boolean disabled = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
    		setDisabled(disabled);
    	}
	}

	protected DropdownMenu getUserChooser(IWContext iwc, IWResourceBundle iwrb) {
		Application application = getApplication(iwc);

		DropdownMenu menu = new DropdownMenu(parameterName);
		menu.setOnChange(getBeforeSubmit(iwc) + ";this.form.submit()");
//		menu.setToSubmit(isSetToSubmit());
		menu.setDisabled(isDisabled());
		menu.setStyleClass("userSelector");
		for (Applicant child : children) {
			boolean addUser = true;

			if (application != null) {
				if (application.getAgeFrom() > -1 && application.getAgeTo() > -1) {
					if (child.getDateOfBirth() != null) {
						IWTimestamp stamp = new IWTimestamp(child.getDateOfBirth());
						stamp.setDay(1);
						stamp.setMonth(1);

						Age age = new Age(stamp.getDate());
						addUser = (application.getAgeFrom() <= age.getYears() && application.getAgeTo() >= age.getYears());
					}
					else {
						addUser = false;
					}
				}
			}

			if (addUser) {
				menu.addMenuElement(child.getId(), child.getName());
			}
		}
		menu.addMenuElementFirst("", iwrb.getLocalizedString("select_applicant", "Select applicant"));

		if (chosenUser != null) {
			menu.setSelectedElement(chosenUser);
		}

		return menu;
	}
	
	private Application getApplication(IWContext iwc) {
		Application application = null;
		if (applicationPK != null) {
			try {
				application = getApplicationBusiness(iwc).getApplication(applicationPK);
			}
			catch (FinderException fe) {
				// Nothing found, continuing...
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		else if (caseCode != null) {
			try {
				application = getApplicationBusiness(iwc).getApplication(caseCode);
			}
			catch (FinderException fe) {
				// Nothing found, continuing...
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		return application;
	}

	protected ApplicationBusiness getApplicationBusiness(IWApplicationContext iwac) {
		try {
			return IBOLookup.getServiceInstance(iwac, ApplicationBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	public void setChosenUser(String chosenUser) {
		this.chosenUser = chosenUser;
	}

	public void setChildren(Collection children) {
		this.children = children;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public void setApplicationPK(Object applicationPK) {
		this.applicationPK = applicationPK;
	}

	public void setCaseCode(String caseCode) {
		this.caseCode = caseCode;
	}

	public boolean isSetToSubmit() {
		return setToSubmit;
	}

	public void setSetToSubmit(boolean setToSubmit) {
		this.setToSubmit = setToSubmit;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getBeforeSubmit(FacesContext context) {
		if(beforeSubmit == null){
			ValueExpression ve = getValueExpression("beforeSubmit");
			if(ve == null){
				beforeSubmit = CoreConstants.EMPTY;
				return beforeSubmit;
			}
			beforeSubmit = (String) ve.getValue(context.getELContext());
		}
		return beforeSubmit;
	}

	public void setBeforeSubmit(String beforeSubmit) {
		this.beforeSubmit = beforeSubmit;
	}
}