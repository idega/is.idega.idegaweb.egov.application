<?xml version="1.0"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:app="http://xmlns.idega.com/is.idega.idegaweb.egov.application"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:ws="http://xmlns.idega.com/com.idega.workspace" version="1.2">
	<jsp:directive.page contentType="text/html" />
	<f:view>
		<ws:page 	id="eGov" 
					showFunctionMenu="true">
					<h:form id="appAdminForm">
						<app:applicationCreator />
					</h:form>
		</ws:page>
	</f:view>
</jsp:root>