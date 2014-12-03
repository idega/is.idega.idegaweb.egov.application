/**
 * @(#)ApplicationPaymentInformation.java    1.0.0 15:58:37
 *
 * Idega Software hf. Source Code Licence Agreement x
 *
 * This agreement, made this 10th of February 2006 by and between 
 * Idega Software hf., a business formed and operating under laws 
 * of Iceland, having its principal place of business in Reykjavik, 
 * Iceland, hereinafter after referred to as "Manufacturer" and Agura 
 * IT hereinafter referred to as "Licensee".
 * 1.  License Grant: Upon completion of this agreement, the source 
 *     code that may be made available according to the documentation for 
 *     a particular software product (Software) from Manufacturer 
 *     (Source Code) shall be provided to Licensee, provided that 
 *     (1) funds have been received for payment of the License for Software and 
 *     (2) the appropriate License has been purchased as stated in the 
 *     documentation for Software. As used in this License Agreement, 
 *     Licensee shall also mean the individual using or installing 
 *     the source code together with any individual or entity, including 
 *     but not limited to your employer, on whose behalf you are acting 
 *     in using or installing the Source Code. By completing this agreement, 
 *     Licensee agrees to be bound by the terms and conditions of this Source 
 *     Code License Agreement. This Source Code License Agreement shall 
 *     be an extension of the Software License Agreement for the associated 
 *     product. No additional amendment or modification shall be made 
 *     to this Agreement except in writing signed by Licensee and 
 *     Manufacturer. This Agreement is effective indefinitely and once
 *     completed, cannot be terminated. Manufacturer hereby grants to 
 *     Licensee a non-transferable, worldwide license during the term of 
 *     this Agreement to use the Source Code for the associated product 
 *     purchased. In the event the Software License Agreement to the 
 *     associated product is terminated; (1) Licensee's rights to use 
 *     the Source Code are revoked and (2) Licensee shall destroy all 
 *     copies of the Source Code including any Source Code used in 
 *     Licensee's applications.
 * 2.  License Limitations
 *     2.1 Licensee may not resell, rent, lease or distribute the 
 *         Source Code alone, it shall only be distributed as a 
 *         compiled component of an application.
 *     2.2 Licensee shall protect and keep secure all Source Code 
 *         provided by this this Source Code License Agreement. 
 *         All Source Code provided by this Agreement that is used 
 *         with an application that is distributed or accessible outside
 *         Licensee's organization (including use from the Internet), 
 *         must be protected to the extent that it cannot be easily 
 *         extracted or decompiled.
 *     2.3 The Licensee shall not resell, rent, lease or distribute 
 *         the products created from the Source Code in any way that 
 *         would compete with Idega Software.
 *     2.4 Manufacturer's copyright notices may not be removed from 
 *         the Source Code.
 *     2.5 All modifications on the source code by Licencee must 
 *         be submitted to or provided to Manufacturer.
 * 3.  Copyright: Manufacturer's source code is copyrighted and contains 
 *     proprietary information. Licensee shall not distribute or 
 *     reveal the Source Code to anyone other than the software 
 *     developers of Licensee's organization. Licensee may be held 
 *     legally responsible for any infringement of intellectual property 
 *     rights that is caused or encouraged by Licensee's failure to abide 
 *     by the terms of this Agreement. Licensee may make copies of the 
 *     Source Code provided the copyright and trademark notices are 
 *     reproduced in their entirety on the copy. Manufacturer reserves 
 *     all rights not specifically granted to Licensee.
 *
 * 4.  Warranty & Risks: Although efforts have been made to assure that the 
 *     Source Code is correct, reliable, date compliant, and technically 
 *     accurate, the Source Code is licensed to Licensee as is and without 
 *     warranties as to performance of merchantability, fitness for a 
 *     particular purpose or use, or any other warranties whether 
 *     expressed or implied. Licensee's organization and all users 
 *     of the source code assume all risks when using it. The manufacturers, 
 *     distributors and resellers of the Source Code shall not be liable 
 *     for any consequential, incidental, punitive or special damages 
 *     arising out of the use of or inability to use the source code or 
 *     the provision of or failure to provide support services, even if we 
 *     have been advised of the possibility of such damages. In any case, 
 *     the entire liability under any provision of this agreement shall be 
 *     limited to the greater of the amount actually paid by Licensee for the 
 *     Software or 5.00 USD. No returns will be provided for the associated 
 *     License that was purchased to become eligible to receive the Source 
 *     Code after Licensee receives the source code. 
 */
package is.idega.idegaweb.egov.application.bean;

import is.idega.idegaweb.egov.application.IWBundleStarter;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.data.ApplicationPaymentInformationEntity;
import is.idega.idegaweb.egov.application.data.dao.ApplicationPaymentInformationDAO;
import is.idega.idegaweb.egov.application.presentation.ApplicationPaymentInformationEditor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.builder.business.BuilderLogic;
import com.idega.user.data.bean.User;
import com.idega.util.CoreUtil;
import com.idega.util.StringUtil;
import com.idega.util.WebUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>JSF managed bean for {@link ApplicationPaymentInformationEntity}</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2014 lapkr. 13
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
public class ApplicationPaymentInformation {

	public static final String PAYMENT_ID = "payment_id";
	public static final String SUBMITTED = "submitted";
	
	private ApplicationPaymentInformationEntity entity;

	private Long id = null;

	private Long applicationId;

	private Application application;

	private User user;

	private Boolean active;

	private String creditCardNumber;

	@Autowired
	private WebUtil webUtil;

	@Autowired
	private ApplicationPaymentInformationDAO dao;

	public ApplicationPaymentInformation() {}

	public ApplicationPaymentInformation(ApplicationPaymentInformationEntity entity) {
		if (entity != null) {
			this.entity = entity;
		}
	} 

	protected WebUtil getWebUtil() {
		if (this.webUtil == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.webUtil;
	}
	
	protected ApplicationPaymentInformationDAO getDao() {
		if (this.dao == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.dao;
	}

	public ApplicationPaymentInformationEntity getEntity() {
		return this.entity;
	}

	public Long getId() {
		if (getEntity() != null) {
			return getEntity().getId();
		}

		return null;
	}

	public Long getApplicationId() {
		if (getEntity() != null) {
			return getEntity().getApplicationId();
		}

		return null;
	}

	public Application getApplication() {
		if (this.application == null) {
			this.application = getDao().findApplication(getEntity());
		}

		return this.application;
	}

	public String getApplicationName() {
		if (getApplication() != null) {
			return getApplication().getNameByLocale();
		}

		return null;
	}

	public User getUser() {
		if (getEntity() != null && this.user == null) {
			this.user = getEntity().getUser();
		} else {
			this.user = CoreUtil.getIWContext().getLoggedInUser();
		}

		return this.user;
	}

	public String getPersonalId() {
		if (getUser() != null) {
			return getUser().getPersonalID();
		}

		return null;
	}

	public String getUserName() {
		if (getUser() != null) {
			return getUser().getName();
		}

		return null;
	}
	
	public Boolean getActive() {
		if (getEntity() != null) {
			return getEntity().isActive();
		}

		return null;
	}

	public String getActiveValue() {
		if (getEntity() != null) {
			if(getEntity().isActive()) {
				return getWebUtil().getLocalizedString(
						IWBundleStarter.IW_BUNDLE_IDENTIFIER, 
						"active", "Active");
			}
		}

		return getWebUtil().getLocalizedString(
				IWBundleStarter.IW_BUNDLE_IDENTIFIER, 
				"not_active", "Not active");
	}

	public String getCreditCardNumber() {
		if (getEntity() != null) {
			String number = getEntity().getCreditCardNumber();
			if (!StringUtil.isEmpty(number) && number.length() > 12) {
				return "**** **** **** " + number.substring(12);
			}
		}

		return null;
	}

	public Date getModified() {
		if (getEntity() != null) {
			return getEntity().getModified();
		}

		return null;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public void save() {
		getDao().update(
				this.id, 
				this.applicationId, 
				getUser(), 
				this.active, 
				this.creditCardNumber);
		CoreUtil.getIWContext().setMultipartParameter(SUBMITTED, Boolean.TRUE.toString());
	}

	public boolean isSubmitted() {
		String submitted = CoreUtil.getIWContext().getParameter(SUBMITTED);
		if (Boolean.TRUE.toString().equals(submitted)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public String getEditorLink() {
		if (getEntity() != null) {
			List<AdvancedProperty> parameters = new ArrayList<AdvancedProperty>();
			parameters.add(new AdvancedProperty(PAYMENT_ID, getId()));
			return BuilderLogic.getInstance().getUriToObject(
					ApplicationPaymentInformationEditor.class, parameters);
		}

		return null;
	}
}
