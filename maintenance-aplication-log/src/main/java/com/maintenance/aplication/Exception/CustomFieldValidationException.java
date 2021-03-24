package com.maintenance.aplication.Exception;
// Errores debajo de cada input
public class CustomFieldValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6283134262113750050L;
	
	private String fieldName;
	// recibe el mensaje y el nombre del input
	public CustomFieldValidationException(String message, String fieldName) {
		super(message);
		this.fieldName = fieldName;
	}
	
	public String getFieldName() {
		return this.fieldName;
	}

}
