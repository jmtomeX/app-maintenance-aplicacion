package com.maintenance.aplication.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ChangePasswordForm {
	@NotNull
	private Long id;
	
	@NotBlank
	(message="La contraseña actual no debe estar en blanco.")
	private String currentPassword;

	@NotBlank(message="La nueva contraseña no puede estar en blanco")
	private String newPassword;

	@NotBlank(message="La nueva contraseña no puede estar en blanco")
	private String confirmPassword;


	public  ChangePasswordForm(Long id) {
		this.id = id;
	}
	public  ChangePasswordForm() {
		
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
}
