package is.idega.idegaweb.egov.application.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;

@Service("applicationBean")
@Scope("request")
public class ApplicationBean {

	private String name;
	private String personalID;
	private String address;
	private String postal;
	private String phone;
	private String email;
	
	private boolean showContactInfo;

	private String action;
	private String nextAction;
	private String previousAction;
	private Collection<Parameter> parameters = new ArrayList<Parameter>();
	
	private Applicant currentUser;
	private Collection<Applicant> children;
	private String userParameter;
	private Object applicationPK;
	private String caseCode;
	
	private Collection<Parent> parents;
	private Parent performer;
	
	private boolean hasErrors = false;
	private Map<Integer, Boolean> errorMap = new HashMap<Integer, Boolean>();
	private Collection<Error> errors = new ArrayList<Error>();
	
	private Collection<Phase> phases = new ArrayList<Phase>();
	private int phaseCount;
	private int currentPhase;
	
	private String headline;
	private String body;

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

	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getNextAction() {
		return nextAction;
	}
	
	public void setNextAction(String nextAction) {
		this.nextAction = nextAction;
	}
	
	public String getPreviousAction() {
		return previousAction;
	}
	
	public void setPreviousAction(String previousAction) {
		this.previousAction = previousAction;
	}
	
	public void addParameter(String parameter, String value) {
		parameters.add(new Parameter(parameter, value));
	}
	
	public void maintainParameter(IWContext iwc, String parameter) {
		String[] values = iwc.getParameterValues(parameter);
		if (values != null) {
			for (String value : values) {
				addParameter(parameter, value);
			}
		}
	}
	
	public void addEventListener(Class eventListener) {
		addParameter(IWMainApplication.IdegaEventListenerClassParameter, IWMainApplication.getEncryptedClassName(eventListener.getName()));
	}
	
	public Collection<Parameter> getParameters() {
		return parameters;
	}

	public class Parameter {

		String parameter;
		String value;

		public Parameter(String parameter, String value) {
			this.parameter = parameter;
			this.value = value;
		}

		public String getParameter() {
			return parameter;
		}

		public String getValue() {
			return value;
		}
	}

	public Applicant getCurrentUser() {
		return this.currentUser;
	}
	
	public void setCurrentUser(Applicant user) {
		this.currentUser = user;
	}
	
	public Collection<Applicant> getChildren() {
		return children;
	}

	public void setChildren(Collection<Applicant> children) {
		this.children = children;
	}

	public String getUserParameter() {
		return userParameter;
	}

	public void setUserParameter(String userParameter) {
		this.userParameter = userParameter;
	}

	public Object getApplicationPK() {
		return applicationPK;
	}

	public void setApplicationPK(Object applicationPK) {
		this.applicationPK = applicationPK;
	}

	public String getCaseCode() {
		return caseCode;
	}

	public void setCaseCode(String caseCode) {
		this.caseCode = caseCode;
	}

	public Collection<Parent> getParents() {
		return parents;
	}

	public void setParents(Collection<Parent> parents) {
		this.parents = parents;
	}

	public Parent getPerformer() {
		return performer;
	}

	public void setPerformer(Parent performer) {
		this.performer = performer;
	}

	public boolean getHasErrors() {
		return hasErrors;
	}
	
	public boolean hasErrors(int phase) {
		return errorMap.containsKey(phase);
	}
	
	public void setHasErrors(boolean hasErrors) {
		this.hasErrors = hasErrors;
	}
	
	public void addError(String parameter, String errorMessage) {
		errors.add(new Error(parameter, errorMessage));
		setHasErrors(true);
	}
	
	public void addError(int phase, String parameter, String errorMessage) {
		if (!errorMap.containsKey(phase)) {
			errorMap.put(phase, Boolean.TRUE);
		}
		addError(parameter, errorMessage);
	}
	
	public Collection<Error> getErrors() {
		return errors;
	}
	
	public class Error {

		String parameter;
		String message;

		public Error(String parameter, String message) {
			this.parameter = parameter;
			this.message = message;
		}

		public String getParameter() {
			return parameter;
		}

		public String getMessage() {
			return message;
		}
	}
	
	public void addPhase(int number, boolean current) {
		phases.add(new Phase(number, current));
	}
	
	public Collection<Phase> getPhases() {
		return phases;
	}
	
	public class Phase {
		
		int number;
		boolean current;
		
		public Phase(int number, boolean current) {
			this.number = number;
			this.current = current;
		}
		
		public int getNumber() {
			return this.number;
		}
		
		public boolean isCurrent() {
			return this.current;
		}
	}

	public int getPhaseCount() {
		return phaseCount;
	}

	public void setPhaseCount(int phaseCount) {
		this.phaseCount = phaseCount;
	}

	public int getCurrentPhase() {
		return currentPhase;
	}

	public void setCurrentPhase(int currentPhase) {
		this.currentPhase = currentPhase;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}