package com.maintenance.aplication.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.maintenance.aplication.Exception.CustomFieldValidationException;
import com.maintenance.aplication.Exception.UsernameOrIdNotFound;
import com.maintenance.aplication.dto.ChangePasswordForm;
import com.maintenance.aplication.entity.Role;
import com.maintenance.aplication.entity.User;
import com.maintenance.aplication.respository.RoleRepository;
import com.maintenance.aplication.service.UserService;

@Controller
public class UserController {

	@Autowired
	UserService userService;

	@Autowired // solo es para mostrar datos no se crea, ni modifica por lo que no se usa una capa intermedia
	RoleRepository roleRepository;

	@GetMapping({ "/", "/login" })
	public String index() {
		return "index";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		// recogeemos el role para asignar solo el role user
		Role userRole = roleRepository.findByName("USER");
		// para evitar tener que modificar el formulario se crea una lista, ya que se usa el formulario común
		List<Role> roles = Arrays.asList(userRole);
		
		model.addAttribute("signup",true);
		
		model.addAttribute("userForm", new User());
		model.addAttribute("roles", roles);

		return "user-form/user-signup";
	}

	@PostMapping("/signup")
	public String postSignup(@Valid @ModelAttribute("userForm") User user, BindingResult result, ModelMap model) {
		System.out.println("entra en crear signup");
		// recogemos el role para asignar solo el role user
		Role userRole = roleRepository.findByName("USER");
		// para evitar tener que modificar el formulario se crea una lista, ya que se usa el formulario común
		List<Role> roles = Arrays.asList(userRole);

		model.addAttribute("userForm", user);
		model.addAttribute("roles", roles);

		model.addAttribute("signup", true);

		if (result.hasErrors()) {
			return "user-form/user-signup";
		} else {
			try {
				userService.createUser(user);

			} catch (CustomFieldValidationException cfve) {
				// Excepciones propias y añadidas debajo de cada input
				result.rejectValue(cfve.getFieldName(), null, cfve.getMessage());
				return "user-form/user-signup";

			} catch (Exception e) {
				// excepciones genéricas
				model.addAttribute("formErrorMessage", e.getMessage());
				return "user-form/user-signup";
			}
		}
		return index();
	}

	@GetMapping("/userForm")
	public String userForm(Model model) throws Exception {
		model.addAttribute("userForm", new User());
		model.addAttribute("userLogged", userService.getLoggedUser());
		model.addAttribute("userList", userService.getAllUsers());
		model.addAttribute("roles", roleRepository.findAll());
		model.addAttribute("listTab", "active");
		return "user-form/user-view";
	}

	@PostMapping("/userForm")
	public String createUser(@Valid @ModelAttribute("userForm") User user, BindingResult result, Model model) throws Exception {
		System.out.println("entra en crear usuario");
		if (result.hasErrors()) {
			model.addAttribute("userForm", user);
			model.addAttribute("formTab", "active");
		} else {
			try {
				userService.createUser(user);
				model.addAttribute("userForm", new User());
				model.addAttribute("listTab", "active");

			} catch (CustomFieldValidationException cfve) {
				// Excepciones propias y añadidas debajo de cada input
				result.rejectValue(cfve.getFieldName(), null, cfve.getMessage());
				model.addAttribute("userForm", user);
				model.addAttribute("formTab", "active");
				model.addAttribute("userList", userService.getAllUsers());
				model.addAttribute("roles", roleRepository.findAll());
			} catch (Exception e) {
				// excepciones genéricas
				model.addAttribute("formErrorMessage", e.getMessage());
				model.addAttribute("userForm", user);
				model.addAttribute("formTab", "active");
				model.addAttribute("userList", userService.getAllUsers());
				model.addAttribute("roles", roleRepository.findAll());
			}
		}
		model.addAttribute("userList", userService.getAllUsers());
		model.addAttribute("userLogged", userService.getLoggedUser());
		model.addAttribute("roles", roleRepository.findAll());
		return "user-form/user-view";
	}

	@GetMapping("/editUser/{id}")
	public String getEditUserForm(Model model, @PathVariable(name = "id") Long id) throws Exception {

		User userToEdit = userService.getUserById(id);
		model.addAttribute("userForm", userToEdit);
		model.addAttribute("userList", userService.getAllUsers());
		model.addAttribute("userLogged", userService.getLoggedUser());
		model.addAttribute("roles", roleRepository.findAll());
		model.addAttribute("formTab", "active");
		model.addAttribute("editMode", "true");
		// cada vez que se agrefa el formulario se recoge el id del usuario
		model.addAttribute("passwordForm", new ChangePasswordForm(id));

		return "user-form/user-view";
	}

	@PostMapping("/editUser")
	public String editUser(@Valid @ModelAttribute("userForm") User user, BindingResult result, Model model) throws Exception {
		if (result.hasErrors()) {
			model.addAttribute("userForm", user);
			model.addAttribute("userLogged", userService.getLoggedUser());
			model.addAttribute("formTab", "active");
			model.addAttribute("editMode", "true");
			// para actualizar la contraseña
			model.addAttribute("passwordForm", new ChangePasswordForm(user.getId()));

		} else {

			try {
				userService.updateUser(user);
				model.addAttribute("userForm", new User());
				model.addAttribute("userLogged", userService.getLoggedUser());
				model.addAttribute("listTab", "active");

			} catch (Exception e) {
				model.addAttribute("formErrorMessage", e.getMessage());
				model.addAttribute("userForm", user);
				model.addAttribute("userLogged", userService.getLoggedUser());
				model.addAttribute("formTab", "active");
				model.addAttribute("userList", userService.getAllUsers());
				model.addAttribute("roles", roleRepository.findAll());
				// se queda en la misma pantalla si hay un error
				model.addAttribute("editMode", "false");
				model.addAttribute("passwordForm", new ChangePasswordForm(user.getId()));
			}
		}
		model.addAttribute("userList", userService.getAllUsers());
		model.addAttribute("roles", roleRepository.findAll());
		return "user-form/user-view";
	}

	@GetMapping("/userForm/cancel")
	public String cancelEditUser(ModelMap model) {
		model.addAttribute("userForm", new User());// para vaciar el formulario
		return "redirect:/userForm";
	}

	@GetMapping("/deleteUser/{id}")
	public String deleteUSer(Model model, @PathVariable(name = "id") Long id) {
		try {
			userService.deleteUser(id);
		} catch (UsernameOrIdNotFound uoidnfound) {
			model.addAttribute("listErrorMessage", uoidnfound.getMessage());
		}
		return "redirect:/userForm";
	}

	@PostMapping("/editUser/changePassword")
	public ResponseEntity<String> editUserChangePassword(@Valid @RequestBody ChangePasswordForm form, Errors errors) {
		try {
			if (errors.hasErrors()) {
				// si hay errores se lanzan en una excepción con un string
				String result = errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(""));
				throw new Exception(result);
			}
			userService.changePassword(form);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok("Success");

	}

}
