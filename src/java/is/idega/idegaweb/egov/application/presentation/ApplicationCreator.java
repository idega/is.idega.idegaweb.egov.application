/*
 * $Id: ApplicationCreator.java,v 1.44 2008/11/07 10:16:05 valdas Exp $ Created on Jan 12,
 * 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.business.ApplicationType;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.data.ApplicationCategory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlMessage;
import javax.faces.component.html.HtmlMessages;

import com.idega.block.process.data.CaseCode;
import com.idega.block.text.data.LocalizedText;
import com.idega.block.text.data.LocalizedTextHome;
import com.idega.block.web2.business.Web2Business;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.BooleanInput;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.IWDatePicker;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.handlers.IWDatePickerHandler;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
import com.idega.webface.WFUtil;

public class ApplicationCreator extends ApplicationBlock {

//	public static final String APP_FORM_NAME_PARAM = FormDocument.APP_FORM_NAME_PARAM;
//	public static final String APP_ID_PARAM = FormDocument.APP_ID_PARAM;
//	public static final String FROM_APP_REQ_PARAM = FormDocument.FROM_APP_REQ_PARAM;
	public static final String FORMBUILDER_REDIRECT_PATH = "/workspace/forms/formbuilder/";

	private static final String APP_TYPES_HANDLER_ENGINE = "/dwr/interface/ApplicationTypesHandler.js";
	private static final String APP_CREATOR_APP_TYPES = "javascript/applicationTypes.js";
	private static final String web2beanBeanIdentifier = "web2bean";

	private static final String NAME_INPUT = "name";
	private static final String APP_TYPE_INPUT = "appType";
	private static final String REQ_LOGIN_INPUT = "reqLogin";
	private static final String VISIBLE_INPUT = "visible";
	private static final String ENABLED_FROM_INPUT = "enabledFrom";
	private static final String ENABLED_TO_INPUT = "enabledTo";
	private static final String AGE_FROM_INPUT = "ageFrom";
	private static final String AGE_TO_INPUT = "ageTo";
	private static final String CODE_INPUT = "code";
	protected static final String CAT_INPUT = "cat";
	private static final String NEW_WIN_INPUT = "newin";
	private static final String HIDDEN_INPUT = "hidden";

	public static final String APPLICATION_ID_PARAMETER = "prm_application_id";

	public static final String ACTION = "prm_action";
	public static final String SAVE_ACTION = "save";
	public static final String CREATE_ACTION = "create";
	public static final String EDIT_ACTION = "edit";
	public static final String DELETE_ACTION = "delete";

	private IWResourceBundle iwrb;
	private IWBundle iwb;

	private int urlLength = 50;

	private boolean requiresLogin;
	private boolean visibleApplication;
	private boolean hiddenFromGuests;
	private boolean checkIfCanViewApplication;
	private boolean addSaveButton = true;
	private boolean addBackButton = true;

	private String caseCode;

	private List<UIComponent> additionalComponents;

	@Override
	public void present(IWContext iwc) throws Exception {

		this.iwrb = super.getResourceBundle(iwc);
		this.iwb = getBundle(iwc);

		List<ICLocale> locales = ICLocaleBusiness.listOfLocales();

		String action = iwc.getParameter(ACTION);
		String id = iwc.getParameter(APPLICATION_ID_PARAMETER);

		if (CREATE_ACTION.equals(action)) {
			getApplicationCreationForm(iwc, -1, locales);
		} else if (EDIT_ACTION.equals(action)) {
			getApplicationCreationForm(iwc, Integer.parseInt(id), locales);
		} else if (SAVE_ACTION.equals(action)) {
			if(validate(iwc)) {
				saveApplication(iwc, locales);
				listExisting(iwc);
			} else {
				if(id == null) {
					getApplicationCreationForm(iwc, -1, locales);
				} else {
					getApplicationCreationForm(iwc, Integer.parseInt(id), locales);
				}
			}
		} else if (DELETE_ACTION.equals(action)) {
			try {
				Application app = getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(
						new Integer(iwc.getParameter(APPLICATION_ID_PARAMETER)));
				app.remove();
			}
			catch (FinderException f) {
				f.printStackTrace();
			}
			listExisting(iwc);
		}
		else if ("list".equals(action)) {
			listExisting(iwc);
		}
		else {
			listExisting(iwc);
		}
	}

	private String getBooleanValueInString(String value, boolean valueToUse) {
		if (StringUtil.isEmpty(value)) {
			return valueToUse ? CoreConstants.Y : null;
		}
		return value;
	}


	protected String saveApplication(IWContext iwc, List<ICLocale> locales) throws RemoteException, CreateException, FinderException, IDOAddRelationshipException {
		String id = iwc.getParameter(APPLICATION_ID_PARAMETER);
		String name = iwc.getParameter(NAME_INPUT);
		String appType = iwc.getParameter(APP_TYPE_INPUT);
		String requiresLogin = getBooleanValueInString(iwc.getParameter(REQ_LOGIN_INPUT), this.requiresLogin);
		String visible = getBooleanValueInString(iwc.getParameter(VISIBLE_INPUT), this.visibleApplication);
		String enabledFrom = iwc.getParameter(ENABLED_FROM_INPUT);
		String enabledTo = iwc.getParameter(ENABLED_TO_INPUT);

		Integer ageFrom = null;
		Integer ageTo = null;

		if (!validate(iwc)) {
			return null;
		}

		try {
			ageFrom = iwc.isParameterSet(AGE_FROM_INPUT) ? new Integer(iwc.getParameter(AGE_FROM_INPUT)) : null;
		} catch(NumberFormatException exp) {
			exp.printStackTrace();
		}

		try {
			ageTo = iwc.isParameterSet(AGE_TO_INPUT) ? new Integer(iwc.getParameter(AGE_TO_INPUT)) : null;
		} catch(NumberFormatException exp) {
			exp.printStackTrace();
		}

		String cat = iwc.isParameterSet(CAT_INPUT) ? iwc.getParameter(CAT_INPUT) : null;
		String code = iwc.getParameter(CODE_INPUT);
		String opensInNew = iwc.getParameter(NEW_WIN_INPUT);
		String hiddenFromGuests = getBooleanValueInString(iwc.getParameter(HIDDEN_INPUT), this.hiddenFromGuests);

		Application app = null;
		if (id != null) {
			try {
				app = getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(new Integer(id));
			} catch (FinderException f) {
				f.printStackTrace();
			}
		} else {
			app = getApplicationBusiness(iwc).getApplicationHome().create();
		}
		app.setName(name);
		app.setRequiresLogin("Y".equalsIgnoreCase(requiresLogin));
		app.setVisible("Y".equalsIgnoreCase(visible));

		Locale locale = iwc.getCurrentLocale();
		if (StringUtil.isEmpty(enabledFrom)) {
			app.setEnabledFrom(null);
		} else {
			Date from = IWDatePickerHandler.getParsedDate(enabledFrom, locale);
			if (from == null) {
				app.setEnabledFrom(null);
			} else {
				IWTimestamp iwFrom = new IWTimestamp(from);
				app.setEnabledFrom(iwFrom.getTimestamp());
			}
		}
		if (StringUtil.isEmpty(enabledTo)) {
			app.setEnabledTo(null);
		} else {
			Date to = IWDatePickerHandler.getParsedDate(enabledTo, locale);
			if (to == null) {
				app.setEnabledTo(null);
			} else {
				IWTimestamp iwTo = new IWTimestamp(to);
				app.setEnabledTo(iwTo.getTimestamp());
			}
		}

		app.setOpensInNewWindow("Y".equalsIgnoreCase(opensInNew));
		app.setHiddenFromGuests("Y".equalsIgnoreCase(hiddenFromGuests));
		app.setAgeFrom((ageFrom == null)? -1 : ageFrom.intValue());
		app.setAgeTo((ageTo == null)? -1 : ageTo.intValue());
		if (code != null && !code.equals("-1")) {
			app.setCaseCode(getApplicationBusiness(iwc).getCaseCode(code));
		}
		if (!StringUtil.isEmpty(cat)) {
			app.setCategory(getApplicationBusiness(iwc).getApplicationCategoryHome().findByPrimaryKey(Integer.valueOf(cat)));
		}
		ApplicationType applType = getApplicationTypesManager().getApplicationType(appType);
		if (applType != null) {
			app.setAppType(appType);
			applType.beforeStore(iwc, app);
			app.store();

			if (applType.afterStore(iwc, app))
				app.store();

		} else {
			throw new RuntimeException("No Application type registered for: "+appType);
		}

		IWCacheManager.getInstance(iwc.getIWMainApplication()).invalidateCache(ApplicationCategoryViewer.CACHE_KEY);
		IWCacheManager.getInstance(iwc.getIWMainApplication()).invalidateCache(ApplicationFavorites.CACHE_KEY);

//			id == null means it's new app
		/* FIXME: see FormDocument from formbuilder method save()
		if(id == null && at == APP_TYPE_FORMBUILDER && name != null && !name.equals("")) {

			try {

				iwc.setSessionAttribute(APP_FORM_NAME_PARAM, name);
				iwc.setSessionAttribute(APP_ID_PARAM, String.valueOf(app.getPrimaryKey()));
				iwc.getResponse().sendRedirect(
						new StringBuilder(FORMBUILDER_REDIRECT_PATH)
						.append("?")
						.append(FROM_APP_REQ_PARAM)
						.append("=1&encParams=1")
						.toString()
				);

			} catch (IOException e) {
				Logger.getLogger(getClassName()).log(Level.SEVERE, "probably some old component was used? and redirect was called when component was actually already been started rendering", e);
			}
		}
		*/

		for(Iterator<ICLocale> it = locales.iterator(); it.hasNext(); ) {
			ICLocale icLocale = it.next();
			String locName = iwc.getParameter(icLocale.getName() + "_locale");

			if(locName != null && !locName.equals("")) {
				LocalizedText locText = app.getLocalizedText(icLocale.getLocaleID());
				boolean newText = false;
				if(locText == null) {
					locText = getLocalizedTextHome().create();
					newText = true;
				}

				locText.setLocaleId(icLocale.getLocaleID());
				locText.setBody(locName);

				locText.store();

				if(newText) {
					app.addLocalizedName(locText);
				}
			}
		}

		return app.getPrimaryKey().toString();
	}

	protected boolean validate(IWContext iwc) {
		String name = iwc.getParameter(NAME_INPUT);
		String appType = iwc.getParameter(APP_TYPE_INPUT);

		Integer ageFrom = null;
		Integer ageTo = null;

		try {
			ageFrom = iwc.isParameterSet(AGE_FROM_INPUT) ? new Integer(iwc.getParameter(AGE_FROM_INPUT)) : null;
		} catch(NumberFormatException exp) {
			iwc.addMessage(AGE_FROM_INPUT, new FacesMessage(this.iwrb.getLocalizedString("not_number", "The value should be a number")));
		}
		try {
			ageTo = iwc.isParameterSet(AGE_TO_INPUT) ? new Integer(iwc.getParameter(AGE_TO_INPUT)) : null;
		} catch(NumberFormatException exp) {
			iwc.addMessage(AGE_TO_INPUT, new FacesMessage(this.iwrb.getLocalizedString("not_number", "The value should be a number")));
		}
		if(name == null || name.trim().equals(CoreConstants.EMPTY)) {
			iwc.addMessage(NAME_INPUT, new FacesMessage(this.iwrb.getLocalizedString("name_empty", "'Default name' field should not be empty")));
		}
		if(appType.equals("-1")) {
			iwc.addMessage(APP_TYPE_INPUT, new FacesMessage(this.iwrb.getLocalizedString("app_type_select", "'Application type' value field is not selected")));
		}
		if(ageFrom != null && ageFrom.intValue() <= 0) {
			iwc.addMessage(AGE_FROM_INPUT, new FacesMessage(this.iwrb.getLocalizedString("negative_number", "The number should be greater than 0")));
			ageFrom = null;
		}
		if(ageTo != null && ageTo.intValue() <= 0) {
			iwc.addMessage(AGE_TO_INPUT, new FacesMessage(this.iwrb.getLocalizedString("negative_number", "The number should be greater than 0")));
			ageTo = null;
		}
		if(ageTo != null && ageFrom != null && ageTo.intValue() > 0 && ageFrom.intValue() > 0 && ageFrom.intValue() >= ageTo.intValue()) {
			iwc.addMessage(AGE_FROM_INPUT, new FacesMessage(this.iwrb.getLocalizedString("age_greater", "'Age from' field value is greater than 'Age to' field value")));
		}

		if(!appType.equals("-1")) {
			ApplicationType applType = getApplicationTypesManager().getApplicationType(appType);

			if(applType != null) {
				applType.getHandlerComponent().validate(iwc);
			} else {
				throw new RuntimeException("No Application type registered for: " + appType);
			}
		}

		if(iwc.getMessages().hasNext()) {
			return false;
		} else {
			return true;
		}
	}


	protected LocalizedTextHome getLocalizedTextHome() throws RemoteException {
		return (LocalizedTextHome) IDOLookup.getHome(LocalizedText.class);
	}

	private void listExisting(IWContext iwc) throws RemoteException, FinderException {
		ApplicationBusiness appBusiness = getApplicationBusiness(iwc);
		Collection<Application> applications = appBusiness.getAvailableApplications(iwc, getCaseCode());

		Form form = new Form();
		form.setID("applicationCreator");
		form.setStyleClass("adminForm");

		Table2 table = new Table2();
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setStyleClass("ruler");
		table.setStyleClass("adminTable");
		form.add(table);

		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.setStyleClass("application");
		cell.add(new Text(this.iwrb.getLocalizedString("application", "Application")));

		cell = row.createHeaderCell();
		cell.setStyleClass("category");
		cell.add(new Text(this.iwrb.getLocalizedString("category", "Category")));

		cell = row.createHeaderCell();
		cell.setStyleClass("caseCode");
		cell.add(new Text(this.iwrb.getLocalizedString("case_code", "CaseCode")));

		cell = row.createHeaderCell();
		cell.setStyleClass("ageFrom");
		cell.add(new Text(this.iwrb.getLocalizedString("age_from", "Age From")));

		cell = row.createHeaderCell();
		cell.setStyleClass("ageTo");
		cell.add(new Text(this.iwrb.getLocalizedString("age_to", "Age To")));

		cell = row.createHeaderCell();
		cell.setStyleClass("appType");
		cell.add(new Text(this.iwrb.getLocalizedString("appType", "Application type")));

		cell = row.createHeaderCell();
		cell.setStyleClass("electronic");
		cell.add(new Text(this.iwrb.getLocalizedString("electronic", "Electronic")));

		cell = row.createHeaderCell();
		cell.setStyleClass("requiresLogin");
		cell.add(new Text(this.iwrb.getLocalizedString("requires_login", "Requires login")));

		cell = row.createHeaderCell();
		cell.setStyleClass("new_window");
		cell.add(new Text(this.iwrb.getLocalizedString("new_window", "New window")));

		cell = row.createHeaderCell();
		cell.setStyleClass("url");
		cell.add(new Text(this.iwrb.getLocalizedString("url", "URL")));

		cell = row.createHeaderCell();
		cell.setStyleClass("edit");
		cell.add(Text.getNonBrakingSpace());

		cell = row.createHeaderCell();
		cell.setStyleClass("remove");
		cell.setStyleClass("lastColumn");
		cell.add(Text.getNonBrakingSpace());

		group = table.createBodyRowGroup();
		int iRow = 1;

		if (!ListUtil.isEmpty(applications)) {
			Locale locale = iwc.getCurrentLocale();
			for (Application app: applications) {
				CaseCode code = app.getCaseCode();

				row = table.createRow();

				Link edit = new Link(this.iwb.getImage("edit.png", this.iwrb.getLocalizedString("edit", "Edit")));
				edit.addParameter("prm_action", "edit");
				edit.addParameter(APPLICATION_ID_PARAMETER, app.getPrimaryKey().toString());

				Link delete = new Link(this.iwb.getImage("delete.png", this.iwrb.getLocalizedString("remove", "Remove")));
				delete.addParameter("prm_action", "delete");
				delete.addParameter(APPLICATION_ID_PARAMETER, app.getPrimaryKey().toString());


				if (iRow % 2 == 0) {
					row.setStyleClass("evenRow");
				}
				else {
					row.setStyleClass("oddRow");
				}

				cell = row.createCell();
				cell.setStyleClass("firstColumn");
				cell.setStyleClass("application");
				cell.add(new Text(appBusiness.getApplicationName(app, locale)));

				cell = row.createCell();
				cell.setStyleClass("category");
				ApplicationCategory category = app.getCategory();
				if (category != null) {
					cell.add(new Text(category.getName()));
				}

				cell = row.createCell();
				cell.setStyleClass("caseCode");
				if (code != null) {
					cell.add(new Text(code.getCode()));
				}
				else {
					cell.add(new Text("-"));
				}

				cell = row.createCell();
				cell.setStyleClass("ageFrom");
				cell.add(new Text(app.getAgeFrom() > -1 ? Integer.toString(app.getAgeFrom()) : "-"));

				cell = row.createCell();
				cell.setStyleClass("ageTo");
				cell.add(new Text(app.getAgeTo() > -1 ? Integer.toString(app.getAgeTo()) : "-"));

				cell = row.createCell();
				cell.setStyleClass("appType");

				cell.add(new Text(getAppTypeLabel(iwc, app.getAppType())));

				cell = row.createCell();
				cell.setStyleClass("electronic");
				cell.add(new Text(this.iwrb.getLocalizedString(Boolean.toString(app.getElectronic()), Boolean.toString(app.getElectronic()))));

				cell = row.createCell();
				cell.setStyleClass("requiresLogin");
				cell.add(new Text(this.iwrb.getLocalizedString(Boolean.toString(app.getRequiresLogin()), Boolean.toString(app.getRequiresLogin()))));

				cell = row.createCell();
				cell.setStyleClass("new_window");
				cell.add(new Text(this.iwrb.getLocalizedString(Boolean.toString(app.getOpensInNewWindow()), Boolean.toString(app.getOpensInNewWindow()))));


				String URL = app.getUrl() != null ? app.getUrl() : "";
				if (URL.length() > this.urlLength) {
					URL = URL.substring(0, this.urlLength) + "...";
				}
				cell = row.createCell();
				cell.setStyleClass("url");
				cell.add(new Text(URL));

				cell = row.createCell();
				cell.setStyleClass("edit");
				cell.add(edit);

				cell = row.createCell();
				cell.setStyleClass("lastColumn");
				cell.setStyleClass("remove");
				cell.add(delete);

				iRow++;
			}
		}

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		form.add(buttonLayer);

		SubmitButton newLink = new SubmitButton(this.iwrb.getLocalizedString("new_application", "New Application"), "prm_action", "create");
		buttonLayer.add(newLink);

		add(form);
	}

	private DropdownMenu getAppTypesMenu(IWContext iwc) {

		DropdownMenu menu = new DropdownMenu("appType");

		menu.addMenuElement("-1", "Select");

		List<ApplicationType> appTypes = getApplicationTypesManager().getApplicationTypes();

		for (ApplicationType appType : appTypes) {

			menu.addMenuElement(appType.getType(), appType.getLabel(iwc));
		}

		return menu;
	}

	private String getAppTypeLabel(IWContext iwc, String appType) {

		if(appType != null && !CoreConstants.EMPTY.equals(appType)) {

			ApplicationType at = getApplicationTypesManager().getApplicationType(appType);

			if(at != null)
				return at.getLabel(iwc);
		}

		return CoreConstants.EMPTY;
	}

	private void getApplicationCreationForm(IWContext iwc, int applicationID, List<ICLocale> locales) throws RemoteException {
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, getCreationFormJavaScriptSources(iwc));

		String nameValue = iwc.getParameter(NAME_INPUT);
		String appTypeValue = iwc.getParameter(APP_TYPE_INPUT);
		String requiresLoginValue = getBooleanValueInString(iwc.getParameter(REQ_LOGIN_INPUT), this.requiresLogin);
		String visibleValue = getBooleanValueInString(iwc.getParameter(VISIBLE_INPUT), this.visibleApplication);

		Integer ageFromValue = null;
		Integer ageToValue = null;
		try {
			ageFromValue = iwc.isParameterSet(AGE_FROM_INPUT) ? new Integer(iwc.getParameter(AGE_FROM_INPUT)) : null;
		} catch(NumberFormatException exp) {
			exp.printStackTrace();
		}

		try {
			ageToValue = iwc.isParameterSet(AGE_TO_INPUT) ? new Integer(iwc.getParameter(AGE_TO_INPUT)) : null;
		} catch(NumberFormatException exp) {
			exp.printStackTrace();
		}

		String catValue = iwc.getParameter(CAT_INPUT);
		String codeValue = iwc.getParameter(CODE_INPUT);
		String opensInNewValue = iwc.getParameter(NEW_WIN_INPUT);
		String hiddenFromGuestsValue = getBooleanValueInString(iwc.getParameter(HIDDEN_INPUT), this.hiddenFromGuests);

		Form form = new Form();
		form.setID("applicationCreator");
		form.setStyleClass("adminForm");

		TextInput name = new TextInput("name");
		name.setId(NAME_INPUT);
		name.setValue(nameValue);

		DropdownMenu appTypes = getAppTypesMenu(iwc);
		appTypes.setId(APP_TYPE_INPUT);
		appTypes.setValue(appTypeValue);

		BooleanInput requiresLogin = new BooleanInput("reqLogin");
		if(requiresLoginValue != null && requiresLoginValue.equals(CoreConstants.Y)) {
			requiresLogin.setSelected(true);
		} else {
			requiresLogin.setSelected(false);
		}

		BooleanInput visible = new BooleanInput("visible");
		if(visibleValue != null && visibleValue.equals(CoreConstants.Y)) {
			visible.setSelected(true);
		} else {
			visible.setSelected(false);
		}

		Locale locale = iwc.getCurrentLocale();
		IWDatePicker enabledFrom = new IWDatePicker(ENABLED_FROM_INPUT);
		enabledFrom.setVersion("1.8.17");
		enabledFrom.setShowTime(true);
		String enabledFromParam = iwc.getParameter(ENABLED_FROM_INPUT);
		if (StringUtil.isEmpty(enabledFromParam)) {
			enabledFrom.setUseCurrentDateIfNotSet(false);
		} else {
			enabledFrom.setDate(IWDatePickerHandler.getParsedDate(enabledFromParam, locale));
		}
		IWDatePicker enabledTo = new IWDatePicker(ENABLED_TO_INPUT);
		enabledTo.setVersion("1.8.17");
		enabledTo.setShowTime(true);
		String enabledToParam = iwc.getParameter(ENABLED_TO_INPUT);
		if (StringUtil.isEmpty(enabledToParam)) {
			enabledTo.setUseCurrentDateIfNotSet(false);
		} else {
			enabledTo.setDate(IWDatePickerHandler.getParsedDate(enabledToParam, locale));
		}

		BooleanInput newin = new BooleanInput("newin");
		if(opensInNewValue != null && opensInNewValue.equals(CoreConstants.Y)) {
			newin.setSelected(true);
		} else {
			newin.setSelected(false);
		}

		BooleanInput hidden = new BooleanInput("hidden");
		if(hiddenFromGuestsValue != null && hiddenFromGuestsValue.equals(CoreConstants.Y)) {
			hidden.setSelected(true);
		} else {
			hidden.setSelected(false);
		}

		TextInput ageFrom = new TextInput("ageFrom");
		ageFrom.setId(AGE_FROM_INPUT);
		if(ageFromValue != null) {
			ageFrom.setValue(ageFromValue);
		}

		TextInput ageTo = new TextInput("ageTo");
		ageTo.setId(AGE_TO_INPUT);
		if(ageToValue != null) {
			ageTo.setValue(ageToValue);
		}

		Layer handlerContainer = new Layer(Layer.DIV);
		handlerContainer.setStyleClass("appTypeHandlerContainer");

		appTypes.setOnChange("egov_AppTypes.appTypeChanged("+applicationID+", jQuery(this).val(), document.getElementById('"+handlerContainer.getId()+"'));");
		appTypes.setSelectedElement(appTypeValue);

		DropdownMenu category = new DropdownMenu("cat");
		try {
			category.addMenuElements(getApplicationBusiness(iwc).getApplicationCategoryHome().findAllOrderedByName());
			category.setSelectedElement(catValue);
		}
		catch (FinderException e) {
			e.printStackTrace();
		}

		DropdownMenu caseCode = new DropdownMenu("code");
		caseCode.addMenuElementFirst("-1", this.iwrb.getLocalizedString("no_code", "No Code"));
		Collection<CaseCode> caseCodes = getApplicationBusiness(iwc).getCaseCodes();

		for (CaseCode code : caseCodes) {
			caseCode.addMenuElement(code.getPrimaryKey().toString(), code.getDescriptionLocalizedKey() != null ? this.iwrb.getLocalizedString(code.getDescriptionLocalizedKey(), code.getDescription()) : code.getDescription());
			caseCode.setSelectedElement(codeValue);
		}
		if (!StringUtil.isEmpty(this.caseCode)) {
			caseCode.setSelectedElement(this.caseCode);
		}

		Application application = null;

		if (applicationID >= 0) {
			try {
				application = getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(new Integer(applicationID));
				name.setContent(application.getName());

				requiresLogin.setSelected(application.getRequiresLogin());
				visible.setSelected(application.getVisible());
				ageFrom.setContent(application.getAgeFrom() > -1 ? Integer.toString(application.getAgeFrom()) : "");
				ageTo.setContent(application.getAgeFrom() > -1 ? Integer.toString(application.getAgeTo()) : "");
				if (application.getCategory() != null) {
					category.setSelectedElement(application.getCategory().getPrimaryKey().toString());
				}
				if (application.getCaseCode() != null) {
					caseCode.setSelectedElement(application.getCaseCode().getPrimaryKey().toString());
				}
				newin.setSelected(application.getOpensInNewWindow());
				form.add(new HiddenInput(APPLICATION_ID_PARAMETER, Integer.toString(applicationID)));

				enabledFrom.setDate(application.getEnabledFrom());
				enabledTo.setDate(application.getEnabledTo());
			}
			catch (FinderException f) {
				f.printStackTrace();
			}
		}

		String typeValue = null;
		if(appTypeValue != null) {
			typeValue = appTypeValue;
		} else {
			if(application != null) {
				typeValue = application.getAppType();
			}
		}

		if(typeValue != null && !typeValue.trim().equals("")) {
			appTypes.setSelectedElement(typeValue);

			ApplicationType appType = getApplicationTypesManager().getApplicationType(typeValue);

			if(appType != null)
				handlerContainer.add(appType.getHandlerComponent().getUIComponent(iwc, application));
		}

		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("formSection");
		form.add(layer);

		Layer formItem = new Layer(Layer.DIV);
		Layer errorItem = new Layer(Layer.SPAN);
		errorItem.setStyleClass("error");

		formItem.setStyleClass("errors");
		HtmlMessages msgs = (HtmlMessages)iwc.getApplication().createComponent(HtmlMessages.COMPONENT_TYPE);
		formItem.add(msgs);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label(this.iwrb.getLocalizedString("default_name", "Default name"), name);
		HtmlMessage msg = (HtmlMessage)iwc.getApplication().createComponent(HtmlMessage.COMPONENT_TYPE);
		msg.setFor(name.getId());
		errorItem.add(msg);
		formItem.add(label);
		formItem.add(name);
		formItem.add(errorItem);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		errorItem = new Layer(Layer.SPAN);
		errorItem.setStyleClass("error");

		label = new Label(this.iwrb.getLocalizedString("category", "category"), category);
		msg = (HtmlMessage)iwc.getApplication().createComponent(HtmlMessage.COMPONENT_TYPE);
		msg.setFor(category.getId());
		errorItem.add(msg);
		formItem.add(label);
		formItem.add(category);
		formItem.add(errorItem);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		errorItem = new Layer(Layer.SPAN);
		errorItem.setStyleClass("error");
		label = new Label(this.iwrb.getLocalizedString("case_code", "Case code"), caseCode);
		msg = (HtmlMessage)iwc.getApplication().createComponent(HtmlMessage.COMPONENT_TYPE);
		msg.setFor(caseCode.getId());
		errorItem.add(msg);
		formItem.add(label);
		formItem.add(caseCode);
		formItem.add(errorItem);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		errorItem = new Layer(Layer.SPAN);
		errorItem.setStyleClass("error");
		label = new Label(this.iwrb.getLocalizedString("app_type", "Application type"), appTypes);
		msg = (HtmlMessage)iwc.getApplication().createComponent(HtmlMessage.COMPONENT_TYPE);
		msg.setFor(appTypes.getId());
		errorItem.add(msg);
		formItem.add(label);
		formItem.add(appTypes);
		formItem.add(errorItem);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.add(handlerContainer);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("requires_login", "Requires login"), requiresLogin);
		formItem.add(label);
		formItem.add(requiresLogin);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("visible", "Visible"), visible);
		formItem.add(label);
		formItem.add(visible);
		layer.add(formItem);

		//	Enabled from
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("enabled_from", "Enabled from"), enabledFrom);
		formItem.add(label);
		formItem.add(enabledFrom);
		layer.add(formItem);

		//	Enabled to
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("enabled_to", "Enabled to"), enabledTo);
		formItem.add(label);
		formItem.add(enabledTo);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("opens_in_new_window", "Opens in a new window"), newin);
		formItem.add(label);
		formItem.add(newin);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("hidden_from_guests", "Hidden from guests"), hidden);
		formItem.add(label);
		formItem.add(hidden);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		errorItem = new Layer(Layer.SPAN);
		errorItem.setStyleClass("error");
		label = new Label(this.iwrb.getLocalizedString("age_from", "Age from"), ageFrom);
		msg = (HtmlMessage)iwc.getApplication().createComponent(HtmlMessage.COMPONENT_TYPE);
		msg.setFor(ageFrom.getId());
		errorItem.add(msg);
		formItem.add(label);
		formItem.add(ageFrom);
		formItem.add(errorItem);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		errorItem = new Layer(Layer.SPAN);
		errorItem.setStyleClass("error");
		label = new Label(this.iwrb.getLocalizedString("age_to", "Age to"), ageTo);
		msg = (HtmlMessage)iwc.getApplication().createComponent(HtmlMessage.COMPONENT_TYPE);
		msg.setFor(ageTo.getId());
		errorItem.add(msg);
		formItem.add(label);
		formItem.add(ageTo);
		formItem.add(errorItem);
		layer.add(formItem);

		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		layer.add(clearLayer);

		layer = new Layer(Layer.DIV);
		layer.setStyleClass("formSection");
		form.add(layer);

		Text heading = new Text("Localized name");
		heading.setStyleClass("formSectionTitle");
		layer.add(heading);

		for(Iterator<ICLocale> it = locales.iterator(); it.hasNext(); ) {
			ICLocale icLocale = it.next();
			Locale javaLocale = ICLocaleBusiness.getLocaleFromLocaleString(icLocale.getLocale());

			TextInput locInput = new TextInput(icLocale.getName() + "_locale");

			if(application != null) {
				LocalizedText text = application.getLocalizedText(icLocale.getLocaleID());
				locInput.setValue(text == null ? "" : text.getBody());
			}

			formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			label = new Label(javaLocale.getDisplayLanguage(), locInput);
			formItem.add(label);
			formItem.add(locInput);
			layer.add(formItem);
		}

		clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		layer.add(clearLayer);

		if (!ListUtil.isEmpty(additionalComponents)) {
			for (UIComponent component: additionalComponents) {
				form.add(component);
			}
		}

		if (addSaveButton || addBackButton) {
			Layer buttonLayer = new Layer(Layer.DIV);
			buttonLayer.setStyleClass("buttonLayer");
			form.add(buttonLayer);

			if (addBackButton) {
				SubmitButton back = new SubmitButton(this.iwrb.getLocalizedString("back", "Back"), "prm_action", "list");
				buttonLayer.add(back);
			}

			if (addSaveButton) {
				SubmitButton save = new SubmitButton(this.iwrb.getLocalizedString("save", "Save"), "prm_action", "save");
				buttonLayer.add(save);
			}
		}

		add(form);
	}

	public void setURLLength(int urlLength) {
		this.urlLength = urlLength;
	}

	protected List<String> getCreationFormJavaScriptSources(IWContext iwc) {
		List<String> sources = new ArrayList<String>();

		Web2Business web2 = (Web2Business)WFUtil.getBeanInstance(web2beanBeanIdentifier);
		sources.add(web2.getBundleURIToJQueryLib());
		sources.add(CoreConstants.DWR_ENGINE_SCRIPT);
		sources.add(APP_TYPES_HANDLER_ENGINE);
		sources.add(iwc.getIWMainApplication().getBundle(ApplicationBlock.BUNDLE_IDENTIFIER).getVirtualPathWithFileNameString(APP_CREATOR_APP_TYPES));

		return sources;
	}

	protected boolean isRequiresLogin() {
		return requiresLogin;
	}

	protected void setRequiresLogin(boolean requiresLogin) {
		this.requiresLogin = requiresLogin;
	}

	protected boolean isVisibleApplication() {
		return visibleApplication;
	}

	protected void setVisibleApplication(boolean visibleApplication) {
		this.visibleApplication = visibleApplication;
	}

	protected boolean isHiddenFromGuests() {
		return hiddenFromGuests;
	}

	protected void setHiddenFromGuests(boolean hiddenFromGuests) {
		this.hiddenFromGuests = hiddenFromGuests;
	}

	protected boolean isCheckIfCanViewApplication() {
		return checkIfCanViewApplication;
	}

	protected void setCheckIfCanViewApplication(boolean checkIfCanViewApplication) {
		this.checkIfCanViewApplication = checkIfCanViewApplication;
	}

	protected List<UIComponent> getAdditionalComponents() {
		return additionalComponents;
	}

	protected void setAdditionalComponents(List<UIComponent> additionalComponents) {
		this.additionalComponents = additionalComponents;
	}

	public boolean isAddSaveButton() {
		return addSaveButton;
	}

	public void setAddSaveButton(boolean addSaveButton) {
		this.addSaveButton = addSaveButton;
	}

	public boolean isAddBackButton() {
		return addBackButton;
	}

	public void setAddBackButton(boolean addBackButton) {
		this.addBackButton = addBackButton;
	}

	public void setCaseCode(String caseCode) {
		this.caseCode = caseCode;
	}

	public String getCaseCode() {
		return caseCode;
	}
}