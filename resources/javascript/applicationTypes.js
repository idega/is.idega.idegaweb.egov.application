jQuery.noConflict();

jQuery(document).ready(function(){

});

if(egov_AppTypes == null) var egov_AppTypes = {};

egov_AppTypes.appTypeChanged = function(appId, typeSelected, container) {

	jQuery(container).empty();
	
	ApplicationTypesHandler.getApplicationTypeHandler(appId, typeSelected, 
		{
			callback: function(result) {
			
				console.log('got result: '+result);
				console.log(container);
				
				if(result != null)
					insertNodesToContainer(result, container);
			}
		}
	);
}