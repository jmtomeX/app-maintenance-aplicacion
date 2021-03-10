package com.maintenance.aplication.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maintenance.aplication.entity.User;
import com.maintenance.aplication.respository.UserRepository;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository repository;
	
	@Override
	public Iterable<User> getAllUsers() {
		return repository.findAll();
	}
	
	// comprobar si el usuario existe
	private boolean checkUsernameAvalible(User user) throws Exception{
		Optional<User> userFound = repository.findByUsername(user.getUsername());
		if (userFound.isPresent()) {
			throw new Exception("Username no disponible.");
		}
		return true;
	}
	
	private boolean checkPasswordValid(User user) throws Exception {
		if (!user.getPassword().equals(user.getConfirmPassword())) {
			throw new Exception("Las contraseñas no coinciden.");
		}
		return true;
	}
	
	@Override
	public User createUser(User user) throws Exception {
		if (checkPasswordValid(user) && checkUsernameAvalible(user)) {
			user = repository.save(user);
			System.out.print(user);
		}
		return user;
	}

	@Override
	public User getUserById(Long id) throws Exception {
		// si no lo encuentra lanza una excepción.
	return repository.findById(id).orElseThrow(()-> new Exception("El usuario para editar no existe."));
	}

}
