package is.idega.idegaweb.egov.application.presentation;

import java.sql.Timestamp;
import java.util.Locale;

import javax.ejb.FinderException;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.business.IBOLookup;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.CSSSpacer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Span;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.ui.BackButton;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.PresentationUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

import is.idega.idegaweb.egov.application.ApplicationConstants;
import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.data.ApplicationHome;
import is.idega.idegaweb.egov.application.data.dao.ApplicationDAO;

public class DisabledApplicationView extends Block {

	@Autowired
	private ApplicationDAO applicationDAO;

	private ApplicationDAO getApplicationDAO() {
		if (applicationDAO == null) {
			ELUtil.getInstance().autowire(this);
		}

		return applicationDAO;
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		Locale locale = iwc.getCurrentLocale();

		String appName = null;
		Application app = null;
		String appId = iwc.getParameter(ApplicationConstants.PARAM_APP_ID);

		if (StringHandler.isNumeric(appId)) {
			ApplicationHome appHome = (ApplicationHome) IDOLookup.getHome(Application.class);
			try {
				app = appHome.findByPrimaryKey(appId);
			} catch (FinderException e) {}
		}
		if (app == null) {
			getLogger().warning("Application by ID: '" + appId + "' does not exist");
		} else {
			ApplicationBusiness appBusiness = IBOLookup.getServiceInstance(iwc, ApplicationBusiness.class);
			appName = appBusiness.getApplicationName(app, locale);
		}

		IWResourceBundle iwrb = getResourceBundle(iwc);
		IWBundle bundle = getBundle(iwc);
		PresentationUtil.addStyleSheetToHeader(iwc, bundle.getVirtualPathWithFileNameString("style/application.css"));

		Layer container = new Layer();
		container.setStyleClass("disabledApplicationView questions_and_answers");
		add(container);

		String header = StringUtil.isEmpty(appName) ?
				iwrb.getLocalizedString("app_is_disabled", "Application is disabled") :
				iwrb.getLocalizedString("application", "Application") + CoreConstants.SPACE + CoreConstants.QOUTE_MARK + appName +
					CoreConstants.QOUTE_MARK + CoreConstants.SPACE + iwrb.getLocalizedString("is_disabled", "is disabled");

		Heading1 heading = new Heading1(header);
		heading.setStyleClass("header");
		container.add(heading);

		Timestamp from = app == null ? null : app.getEnabledFrom();
		Timestamp to = app == null ? null : app.getEnabledTo();
		if ((app == null || (from == null && to == null)) && StringHandler.isNumeric(appId)) {
			is.idega.idegaweb.egov.application.data.bean.Application application = null;
			try {
				application = getApplicationDAO().findById(Integer.valueOf(appId));
			} catch (Exception e) {
				getLogger().warning("Failed to get application by ID: " + appId);
			}
			if (application != null) {
				from = from == null ? application.getEnabledFrom() : from;
				to = to == null ? application.getEnabledTo() : to;
			}
		}

		String text = iwrb.getLocalizedString("disabled_app_text", "Application currently is disabled.");
		if (from != null && to != null) {
			text = iwrb.getLocalizedString("application_is_enabled_from", "Application is enabled from") + CoreConstants.SPACE +
					new IWTimestamp(from).getLocaleDateAndTime(locale) + CoreConstants.SPACE + iwrb.getLocalizedString("to", "to") +
					CoreConstants.SPACE + new IWTimestamp(to).getLocaleDateAndTime(locale);
		} else if (from != null) {
			text = iwrb.getLocalizedString("application_is_enabled_from", "Application is enabled from") + CoreConstants.SPACE +
					new IWTimestamp(from).getLocaleDateAndTime(locale);
		}

		Span disabledAppTextContainer = new Span();
		disabledAppTextContainer.setStyleClass("disabledAppTextContainer");
		disabledAppTextContainer.add(text);
		container.add(disabledAppTextContainer);

		String key = "disabled_app_explanation_".concat(appId == null ? CoreConstants.EMPTY : appId);
		String explanationText = iwrb.getLocalizedString(key, key);
		if (!key.equals(explanationText)) {
			container.add(new CSSSpacer());

			Layer explanationContainer = new Layer();
			container.add(explanationContainer);
			explanationContainer.setStyleClass("disabledAppExplanationContainer");

			Span disabledAppExplanationTextContainer = new Span();
			disabledAppExplanationTextContainer.setStyleClass("disabledAppExplanationTextContainer");
			disabledAppExplanationTextContainer.add(explanationText);
			explanationContainer.add(disabledAppExplanationTextContainer);
		}

		Layer buttons = new Layer();
		container.add(buttons);
		buttons.setStyleClass("bottom");
		BackButton backButton = new BackButton(iwrb.getLocalizedString("back", "Back"));
		backButton.setStyleClass("button");
		buttons.add(backButton);
	}

	@Override
	public String getBundleIdentifier() {
		return ApplicationConstants.IW_BUNDLE_IDENTIFIER;
	}

}