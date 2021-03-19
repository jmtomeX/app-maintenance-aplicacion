package com.maintenance.aplication.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maintenance.aplication.dto.ChangePasswordForm;
import com.maintenance.aplication.entity.User;
import com.maintenance.aplication.respository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	// codificación de password
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	UserRepository repository;

	@Override
	@Transactional(readOnly = true)
	public Iterable<User> getAllUsers() {
		return repository.findAll();
	}

	// comprobar si el usuario existe
	private boolean checkUsernameAvalible(User user) throws Exception {
		Optional<User> userFound = repository.findByUsername(user.getUsername());
		if (userFound.isPresent()) {
			throw new Exception("Username no disponible.");
		}
		return true;
	}

	private boolean checkPasswordValid(User user) throws Exception {
		if (user.getConfirmPassword() == null || user.getConfirmPassword().isEmpty()) {
			throw new Exception("Confirm password es obligatorio");
		}

		if (!user.getPassword().equals(user.getConfirmPassword())) {
			throw new Exception("Las contraseñas no coinciden.");
		}
		return true;
	}

	@Override
	@Transactional
	public User createUser(User user) throws Exception {
		if (checkPasswordValid(user) && checkUsernameAvalible(user)) {
			user = repository.save(user);
			System.out.print(user);
		}
		return user;
	}
		
	/*
	 	@Override
	@Transactional
	public User createUser(User user) throws Exception {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
		if (checkUsernameAvalible(user) && checkPasswordValid(user) && checkEmailAvailable(user)) { 
			// modificar el password para que sea seguro
			user.setPassword(password);
			(bCryptPasswordEncoder.encode(user.getPassword()));
			// modificar el password para que sea seguro
			user = repository.save(user);
		}
		return user;
	}
	 * */
	
	@Override
	@Transactional(readOnly = true)
	public User getUserById(Long id) throws Exception {
		// si no lo encuentra lanza una excepción.
		User user = repository.findById(id).orElseThrow(() -> new Exception("El usuario no existe."));
		return user;
	}

	// recibe un usuario y se lo pasa a toUser mapeado con el método mapUser
	@Override
	public User updateUser(User fromUser) throws Exception {
		// se consulta en la bbdd porque hay que comparar si es nuevo o no, ya que el método save puede crear o actualizar
		User toUser = getUserById(fromUser.getId());
		mapUser(fromUser, toUser);
		return repository.save(toUser);

	}

	protected void mapUser(User from, User to) {
		to.setUsername(from.getUsername());
		to.setFirstName(from.getFirstName());
		to.setLastName(from.getLastName());
		to.setEmail(from.getEmail());
		to.setRoles(from.getRoles());
	}

	@Override
	public void deleteUser(Long id) throws Exception {
		User user = getUserById(id);
		repository.delete(user);

	}

	@Override
	public User changePassword(ChangePasswordForm form) throws Exception {
		// recoger el id del usr que ya está guardado en el formulario
		User user = getUserById(form.getId());

		// verificar el password con él de la bbdd y comprobar si es admin o no
		if (!loggedUserHasADMIN() && !user.getPassword().equals(form.getCurrentPassword())) {
			throw new Exception("Password actual incorrecto.");
		}

		// verificar si es distinto al viejo
		if (user.getPassword().equals(form.getNewPassword())) {
			throw new Exception("El password tiene que ser distinto al actual.");
		}

		// verificar el passw con el confirm
		if (user.getPassword().equals(form.getConfirmPassword())) {
			throw new Exception("Los password deben de ser iguales.");
		}
		// codifica el password
		String encodePassword = bCryptPasswordEncoder.encode(form.getNewPassword());
		
		// si todo ha ido bien le mandamos el nuevo password
		user.setPassword(encodePassword);
		System.out.println(encodePassword);

		return repository.save(user);
	}
	
	// Comprobar si el usuario en sesión es ADMIN
	// Obteniendo el objeto del usuario en sesión
	public boolean loggedUserHasADMIN() {
		// recoger usuario autentificado
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserDetails loggedUser = null;
		Object roles = null; 
		// si es una instancia de userDet hacemos cast
		if (principal instanceof UserDetails) {
			loggedUser = (UserDetails) principal;
		
			roles = loggedUser.getAuthorities().stream()
					// si hay alguna autoridad que diga ADMIN
					.filter(x -> "ROLE_ADMIN".equals(x.getAuthority() ))      
					.findFirst()
					.orElse(null); // si no devuelve --> loggedUser = null;
		}
		return roles != null ?true :false;
	}
	
	// Obtener el usuario logeado
	private User getLoggedUser() throws Exception {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		UserDetails loggedUser = null;

		//Verificar que ese objeto traido de sesion es el usuario
		if (principal instanceof UserDetails) {
			loggedUser = (UserDetails) principal;
		}
		
		User myUser = repository
				.findByUsername(loggedUser.getUsername()).orElseThrow(() -> new Exception("Error obteniendo el usuario logeado desde la sesion."));
		
		return myUser;
	}
}
/*
 * Al momento de crear contraseñas nosotros ya las mandamos encriptadas, 
 * por tanto cuando tratas de comparar las mismas debemos verificar no con equals sino con matches
 * @Service public class UsuarioServiceImpl implements UsuarioService { *
 * 
 * @Autowired
 *
 * UsuarioRepository usuarioRepository;
 * 
 * @Autowired 
 * BCryptPasswordEncoder bCryptPasswordEncoder;
 * 
 * @Autowired 
 * PasswordEncoder passwordEncoder;
 * 
 * @Override
 * 
 * public Usuario createUser(Usuario user) throws Exception { BCryptPasswordEncoder bCryptPasswordEncoder = new
 * BCryptPasswordEncoder(4); if (checkUsernameAvailable(user) && checkPasswordValid(user) && checkEmailAvailable(user))
 * { //modificar el password para que sea seguro user.setContrasena(bCryptPasswordEncoder.encode(user.getContrasena()));
 * //modificar el password para que sea seguro user = usuarioRepository.save(user); } return user }
 * 
 * 
 * @Override public Usuario changePassword(ChangePasswordForm form) throws Exception {
 *  Usuario user =  getUserById(form.getId());
 * 
 * //encoder.matches("123456", passwd) if ( !isLoggedUserADMIN() && ! passwordEncoder.matches(form.getCurrentPassword(),
 * user.getContrasena())) { throw new Exception ("Current Password invalido."); }
 * if(passwordEncoder.matches(form.getNewPassword(), user.getContrasena())) { throw new Exception
 * ("Nuevo debe ser diferente al password actual."); } if( !form.getNewPassword().equals(form.getConfirmPassword())) {
 * 
 * throw new Exception ("Nuevo Password y Confirm Password no coinciden."); }
 * 
 * String encodePassword = bCryptPasswordEncoder.encode(form.getNewPassword()); user.setContrasena(encodePassword);
 * return usuarioRepository.save(user); }
 */
