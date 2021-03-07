package com.maintenance.aplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
		model.addAttribute("userForm",new User());
		model.addAttribute("userList",userService.getAllUsers());
		model.addAttribute("roles",roleRepository.findAll());
		model.addAttribute("listTab","active");
		return "user-form/user-view";
	}
}
