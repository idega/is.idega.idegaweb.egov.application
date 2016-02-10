package is.idega.idegaweb.egov.application.model;

import com.idega.block.text.data.LocalizedText;

public interface ApplicationCategoryModel {

	Object getPrimaryKey();

	LocalizedText getLocalizedText(int icLocaleId);

	String getName();

	String getDescription();

	String getLocalizedName();
	String getLocalizedName(int icLocaleId);

	String getDefaultName();

}