package is.idega.idegaweb.egov.application.presentation;

import is.idega.idegaweb.egov.application.ApplicationConstants;
import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.data.ApplicationHome;

import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading3;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

public class DisabledApplicationView extends Block {

	public static final String PARAM_APP_ID = "appId";

	@Override
	public void main(IWContext iwc) throws Exception {
		Locale locale = iwc.getCurrentLocale();

		String appName = null;
		Application app = null;
		String appId = iwc.getParameter(PARAM_APP_ID);
		if (StringHandler.isNumeric(appId)) {
			ApplicationHome appHome = (ApplicationHome) IDOLookup.getHome(Application.class);
			try {
				app = appHome.findByPrimaryKey(appId);
			} catch (FinderException e) {}
		}
		if (app != null) {
			ApplicationBusiness appBusiness = IBOLookup.getServiceInstance(iwc, ApplicationBusiness.class);
			appName = appBusiness.getApplicationName(app, locale);
		}

		IWResourceBundle iwrb = getResourceBundle(iwc);

		Layer container = new Layer();
		container.setStyleClass("disabledApplicationView");
		add(container);



		Heading3 disabledAppHeader = StringUtil.isEmpty(appName) ?
				new Heading3(iwrb.getLocalizedString("app_is_disabled", "Application is disabled")) :
				new Heading3(
						iwrb.getLocalizedString("application", "Application") + CoreConstants.SPACE + CoreConstants.QOUTE_MARK + appName +
						CoreConstants.QOUTE_MARK + CoreConstants.SPACE + iwrb.getLocalizedString("is_disabled", "is disabled")
				);
		container.add(disabledAppHeader);

		Layer disabledAppText = new Layer();
		container.add(disabledAppText);
		disabledAppText.add(app == null ?
				iwrb.getLocalizedString("disabled_app_text", "Application currently is disabled.") :
				iwrb.getLocalizedString("application_is_enabled_from", "Application is enabled from") + CoreConstants.SPACE +
					new IWTimestamp(app.getEnabledFrom()).getLocaleDateAndTime(locale) + CoreConstants.SPACE + iwrb.getLocalizedString("to", "to") +
					CoreConstants.SPACE + new IWTimestamp(app.getEnabledTo()).getLocaleDateAndTime(locale)
		);
	}

	@Override
	public String getBundleIdentifier() {
		return ApplicationConstants.IW_BUNDLE_IDENTIFIER;
	}

}