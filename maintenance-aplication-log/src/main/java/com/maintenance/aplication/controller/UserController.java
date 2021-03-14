package com.maintenance.aplication.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.maintenance.aplication.entity.User;
import com.maintenance.aplication.respository.RoleRepository;
import com.maintenance.aplication.service.UserService;

@Controller
public class UserController {

	@Autowired
	UserService userService;

	@Autowired // solo es para mostrar datos no se crea, ni modifica por lo que no se usa una capa intermedia
	RoleRepository roleRepository;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/userForm")
	public String userForm(Model model) {
		model.addAttribute("userForm", new User());
		model.addAttribute("userList", userService.getAllUsers());
		model.addAttribute("roles", roleRepository.findAll());
		model.addAttribute("listTab", "active");
		return "user-form/user-view";
	}

	@PostMapping("/userForm")
	public String createUser(@Valid @ModelAttribute("userForm") User user, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("userForm", user);
			model.addAttribute("formTab", "active");

		} else {

			try {
				
				userService.createUser(user);
				model.addAttribute("userForm", new User());
				model.addAttribute("listTab", "active");

			} catch (Exception e) {
				model.addAttribute("formErrorMessage", e.getMessage());
				model.addAttribute("userForm", user);
				model.addAttribute("formTab", "active");
				model.addAttribute("userList", userService.getAllUsers());
				model.addAttribute("roles", roleRepository.findAll());
			}
		}
		model.addAttribute("userList", userService.getAllUsers());
		model.addAttribute("roles", roleRepository.findAll());
		return "user-form/user-view";
	}

	@GetMapping("/editUser/{id}")
	public String getEditUserForm(Model model, @PathVariable(name = "id") Long id) throws Exception {
		
		User userToEdit = userService.getUserById(id);
		model.addAttribute("userForm", userToEdit);
		model.addAttribute("userList", userService.getAllUsers());
		model.addAttribute("roles", roleRepository.findAll());
		model.addAttribute("formTab", "active");
		model.addAttribute("editMode", "true");

		return "user-form/user-view";
	}
	
	@PostMapping("/editUser")
	public String editUser(@Valid @ModelAttribute("userForm") User user, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("userForm", user);
			model.addAttribute("formTab", "active");
			model.addAttribute("editMode", "true");

		} else {

			try {
				userService.updateUser(user);
				model.addAttribute("userForm", new User());
				model.addAttribute("listTab", "active");

			} catch (Exception e) {
				model.addAttribute("formErrorMessage", e.getMessage());
				model.addAttribute("userForm", user);
				model.addAttribute("formTab", "active");
				model.addAttribute("userList", userService.getAllUsers());
				model.addAttribute("roles", roleRepository.findAll());
				// se queda en la misma pantalla si hay un error 
				model.addAttribute("editMode", "false");
			}
		}
		model.addAttribute("userList", userService.getAllUsers());
		model.addAttribute("roles", roleRepository.findAll());
		return "user-form/user-view";
	}
	@GetMapping("/userForm/cancel")
	public String cancelEditUser(ModelMap model) {
		return "redirect:/userForm";
	}
	@GetMapping("/deleteUser/{id}")
	public String deleteUSer(Model model, @PathVariable(name="id") Long id ) {
		try {
			userService.deleteUser(id);
		} catch(Exception e) {
			model.addAttribute("listErrorMessage",e.getMessage());
		}
		
		return "redirect:/userForm";
	}
}
