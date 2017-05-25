package is.idega.idegaweb.egov.application.presentation;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.block.login.bean.OAuthToken;
import com.idega.block.login.business.OAuth2Service;
import com.idega.block.process.data.model.CaseCodeModel;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.data.bean.LoginInfo;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.IFrame;
import com.idega.util.CoreUtil;
import com.idega.util.StringUtil;
import com.idega.util.URIUtil;
import com.idega.util.expression.ELUtil;

import is.idega.idegaweb.egov.application.ApplicationConstants;
import is.idega.idegaweb.egov.application.data.bean.Application;

public class ApplicationFormInIframe extends ApplicationForm {

	@Autowired(required = false)
	private OAuth2Service oauth2Service;

	private String caseCode = null;

	private OAuth2Service getOAuth2Service() {
		if (oauth2Service == null) {
			try {
				ELUtil.getInstance().autowire(this);
			} catch (Exception e) {}
		}
		return oauth2Service;
	}

	@Override
	protected String getCaseCode() {
		if (StringUtil.isEmpty(caseCode)) {
			CaseCodeModel caseCode = getApplication(CoreUtil.getIWContext()).getCaseCode();
			this.caseCode = caseCode == null ? null : caseCode.getCode();
		}
		return caseCode;
	}

	@Override
	protected void present(IWContext iwc) {
		Application app = getApplication(iwc);
		if (app == null) {
			getLogger().warning("Failed to find application by ID " + iwc.getParameter(ApplicationConstants.PARAM_APP_ID) + ", param " + ApplicationConstants.PARAM_APP_ID);
			return;
		}

		IFrame iframe = new IFrame(app.getName(), getRemoteFormURL(iwc, app));
		iframe.setStyleClass("remote-application-in-iframe");
		add(iframe);
	}

	private String getRemoteFormURL(IWContext iwc, Application app) {
		URIUtil uri = new URIUtil(app.getUrl());

		if (iwc.isLoggedOn()) {
			LoggedOnInfo loggedOnInfo = LoginBusinessBean.getLoggedOnInfo(iwc);
			LoginInfo loginInfo = loggedOnInfo.getUserLogin().getLoginInfo();

			OAuth2Service oauth2Service = getOAuth2Service();
			String clientId = oauth2Service.getDefaultClientId();
			OAuthToken token = oauth2Service.getToken(clientId, loginInfo.getUserLogin().getUserLogin());
			if (token != null) {
				uri.setParameter("access_token", token.getAccess_token());
			}
		}

		return uri.getUri();
	}
}