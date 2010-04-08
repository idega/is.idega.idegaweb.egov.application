package is.idega.idegaweb.egov.application.component;

import is.idega.idegaweb.egov.application.ApplicationConstants;
import is.idega.idegaweb.egov.application.bean.Applicant;
import is.idega.idegaweb.egov.application.bean.ApplicationBean;

import java.rmi.RemoteException;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.PostalCode;
import com.idega.facelets.ui.FaceletComponent;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.user.business.NoEmailFoundException;
import com.idega.user.business.NoPhoneFoundException;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.PersonalIDFormatter;
import com.idega.util.PresentationUtil;
import com.idega.util.text.Name;

public class UserInfo extends IWBaseComponent {

	private String name;
	private String personalID;
	private String address;
	private String postal;
	private String phone;
	private String email;
	
	private String applicantPK;

	private boolean showContactInfo;
	
	private static final String APPLICANT_PROPERTY = "applicant";
	private static final String APPLICANT_ID_PROPERTY = "applicantId";
	private static final String NAME_PROPERTY = "name";
	private static final String PERSONAL_ID_PROPERTY = "personalId";
	private static final String ADDRESS_PROPERTY = "address";
	private static final String POSTAL_PROPERTY = "postal";
	private static final String PHONE_PROPERTY = "phone";
	private static final String EMAIL_PROPERTY = "email";
	private static final String SHOW_CONTACT_INFO_PROPERTY = "showContactInfo";

	public String getBundleIdentifier() {
		return ApplicationConstants.IW_BUNDLE_IDENTIFIER;
	}

	@Override
	protected void initializeComponent(FacesContext context) {
		IWContext iwc = IWContext.getIWContext(context);
		handleExpressions(context);

		ApplicationBean bean = getBeanInstance("applicationBean");
		bean.setName(getName());
		bean.setPersonalID(PersonalIDFormatter.format(getPersonalID(), iwc.getCurrentLocale()));
		bean.setAddress(getAddress());
		bean.setPostal(getPostal());
		bean.setPhone(getPhone());
		bean.setEmail(getEmail());
		bean.setShowContactInfo(isShowContactInfo());

		PresentationUtil.addStyleSheetToHeader(iwc, iwc.getIWMainApplication().getBundle(ApplicationConstants.IW_BUNDLE_IDENTIFIER).getVirtualPathWithFileNameString("style/application.css"));

		FaceletComponent facelet = (FaceletComponent) iwc.getApplication().createComponent(FaceletComponent.COMPONENT_TYPE);
		facelet.setFaceletURI(getBundle(context, getBundleIdentifier()).getFaceletURI("userInfo.xhtml"));
		add(facelet);
	}

	private void handleExpressions(FacesContext context) {
		ValueExpression ve = getValueExpression(APPLICANT_ID_PROPERTY);
    	if (ve != null) {
	    	String applicantId = String.valueOf(ve.getValue(context.getELContext()));
	    	fetchUserInfo(context, applicantId);
    	}

    	ve = getValueExpression(APPLICANT_PROPERTY);
    	if (ve != null) {
	    	Applicant applicant = (Applicant) ve.getValue(context.getELContext());
	    	setName(applicant.getName());
	    	setPersonalID(applicant.getPersonalID());
	    	setAddress(applicant.getAddress());
	    	setPostal(applicant.getPostal());
	    	setPhone(applicant.getHomePhone());
	    	setEmail(applicant.getEmail());
    	}

		ve = getValueExpression(NAME_PROPERTY);
    	if (ve != null) {
	    	String name = (String) ve.getValue(context.getELContext());
	    	setName(name);
    	}    	

		ve = getValueExpression(PERSONAL_ID_PROPERTY);
    	if (ve != null) {
	    	String personalID = (String) ve.getValue(context.getELContext());
	    	setPersonalID(personalID);
    	}    	

		ve = getValueExpression(ADDRESS_PROPERTY);
    	if (ve != null) {
	    	String address = (String) ve.getValue(context.getELContext());
	    	setAddress(address);
    	}    	

		ve = getValueExpression(POSTAL_PROPERTY);
    	if (ve != null) {
	    	String postal = (String) ve.getValue(context.getELContext());
	    	setPostal(postal);
    	}    	

		ve = getValueExpression(PHONE_PROPERTY);
    	if (ve != null) {
	    	String phone = (String) ve.getValue(context.getELContext());
	    	setPhone(phone);
    	}    	

		ve = getValueExpression(EMAIL_PROPERTY);
    	if (ve != null) {
	    	String email = (String) ve.getValue(context.getELContext());
	    	setEmail(email);
    	}    	

		ve = getValueExpression(SHOW_CONTACT_INFO_PROPERTY);
    	if (ve != null) {
	    	boolean showContactInfo = ((Boolean) ve.getValue(context.getELContext())).booleanValue();
	    	setShowContactInfo(showContactInfo);
    	}
	}
	
	private void fetchUserInfo(FacesContext context, String applicantId) {
    	IWContext iwc = IWContext.getIWContext(context);
    	try {
	    	User user = getUserBusiness(iwc).getUser(new Integer(applicantId));
	    	if (user != null) {
	    		setName(new Name(user.getFirstName(), user.getMiddleName(), user.getLastName()).getName(iwc.getCurrentLocale()));
	    		if (user.getPersonalID() != null) {
	    			setPersonalID(PersonalIDFormatter.format(user.getPersonalID(), iwc.getCurrentLocale()));
	    		}
				Address address = getUserBusiness(iwc).getUsersMainAddress(user);
				if (address != null) {
					setAddress(address.getStreetAddress());
					PostalCode postal = address.getPostalCode();
					if (postal != null) {
						setPostal(postal.getPostalAddress());
					}
				}
				try {
					Phone phone = getUserBusiness(iwc).getUsersHomePhone(user);
					if (phone != null) {
						setPhone(phone.getNumber());
					}
				}
				catch (NoPhoneFoundException e) {
					//No phone found...
				}

				try {
					Email email = getUserBusiness(iwc).getUsersMainEmail(user);
					if (email != null) {
						setEmail(email.getEmailAddress());
					}
				}
				catch (NoEmailFoundException e) {
					//No email found...
				}
	    	}
    	}
    	catch (RemoteException re) {
    		throw new IBORuntimeException(re);
    	}
	}
	
	private UserBusiness getUserBusiness(IWApplicationContext iwac) {
		try {
			return (UserBusiness) IBOLookup.getServiceInstance(iwac, UserBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	public String getApplicantPK() {
		return applicantPK;
	}

	public void setApplicantPK(String applicantPK) {
		this.applicantPK = applicantPK;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPersonalID() {
		return personalID;
	}

	public void setPersonalID(String personalID) {
		this.personalID = personalID;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostal() {
		return postal;
	}

	public void setPostal(String postal) {
		this.postal = postal;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isShowContactInfo() {
		return showContactInfo;
	}

	public void setShowContactInfo(boolean showContactInfo) {
		this.showContactInfo = showContactInfo;
	}
}