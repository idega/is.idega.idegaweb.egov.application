package is.idega.idegaweb.egov.application.model;

import java.util.Collection;

import com.idega.block.text.model.LocalizedTextModel;

public interface ApplicationCategoryModel {

	Object getPrimaryKey();

	LocalizedTextModel getLocalizedText(int icLocaleId);

	String getName();

	String getDescription();

	String getLocalizedName();
	String getLocalizedName(int icLocaleId);

	String getDefaultName();

	<T extends LocalizedTextModel, L extends Collection<T>> L getLocalizedTexts();

}