jQuery.noConflict();

jQuery(document).ready(function(){
	var url = window.location.href;
	if (
		url != null &&
		(
			url.indexOf('?appId=1783') != -1 ||
			url.indexOf('?appId=1781') != -1 ||
			url.indexOf('?appId=1881') != -1 ||
			url.indexOf('?appId=1341') != -1
		)
	) {
		jQuery('span.disabledAppTextContainer').css('display', 'none');
	}
});