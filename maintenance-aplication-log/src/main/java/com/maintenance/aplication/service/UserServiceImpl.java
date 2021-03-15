package com.maintenance.aplication.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maintenance.aplication.dto.ChangePasswordForm;
import com.maintenance.aplication.entity.User;
import com.maintenance.aplication.respository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

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
	public void deleteUser(Long id ) throws Exception {
		User user = getUserById(id);
		repository.delete(user);
	
	}

	@Override
	public User changePassword(ChangePasswordForm form) throws Exception {
		// recoger el id del usr que ya está guardado en el formulario
		User user = getUserById(form.getId());
		
		// verificar el password con el de la bbdd
		if (!user.getPassword().equals(form.getCurrentPassword())) {
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
		// si todo ha ido bien le mandamos el nuevo password
		user.setPassword(form.getNewPassword());
		return repository.save(user);
	}
}
