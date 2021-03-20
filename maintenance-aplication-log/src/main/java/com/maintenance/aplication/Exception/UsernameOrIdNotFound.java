package com.maintenance.aplication.Exception;

public class UsernameOrIdNotFound  extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2847948394659614330L;
	// constructores
	public UsernameOrIdNotFound() {
		super("Usuario o id no encontrado.");
	}
	public UsernameOrIdNotFound(String message) {
		super(message);
	}

}
