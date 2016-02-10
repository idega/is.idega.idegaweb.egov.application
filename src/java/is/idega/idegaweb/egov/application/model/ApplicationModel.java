package is.idega.idegaweb.egov.application.model;

import java.sql.Timestamp;

import com.idega.block.process.data.model.CaseCodeModel;
import com.idega.block.text.data.LocalizedText;

public interface ApplicationModel {

	Object getPrimaryKey();

	boolean getVisible();

	boolean isEnabled();

	boolean getElectronic();

	boolean getRequiresLogin();

	boolean getOpensInNewWindow();

	boolean getHiddenFromGuests();

	String getAppType();

	LocalizedText getLocalizedText(int icLocaleId);

	String getName();

	CaseCodeModel getCaseCode();

	int getAgeFrom();

	int getAgeTo();

	Timestamp getEnabledFrom();

	Timestamp getEnabledTo();

	String getUrl();

}