package is.idega.idegaweb.egov.application.model;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Locale;

import com.idega.block.process.data.model.CaseCodeModel;
import com.idega.block.text.model.LocalizedTextModel;

public interface ApplicationModel {

	Object getPrimaryKey();

	boolean getVisible();

	boolean isEnabled();

	boolean getElectronic();

	boolean getRequiresLogin();

	boolean getOpensInNewWindow();

	boolean getHiddenFromGuests();

	String getAppType();

	LocalizedTextModel getLocalizedText(int icLocaleId);

	String getName();

	CaseCodeModel getCaseCode();

	int getAgeFrom();

	int getAgeTo();

	Timestamp getEnabledFrom();

	Timestamp getEnabledTo();

	String getUrl();

	int getTimesClicked();

	void setTimesClicked(int count);

	void store();

	String getUrlByLocale(Locale locale);

	String getLoginPageURL();

	<T extends LocalizedTextModel, L extends Collection<T>> L getLocalizedTexts();

}