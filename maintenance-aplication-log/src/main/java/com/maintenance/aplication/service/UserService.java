package com.maintenance.aplication.service;

import com.maintenance.aplication.entity.User;

public interface UserService {
	// Iterable puede ser de cualquier tipo List, array, arrayList, set...
	public Iterable<User> getAllUsers();
	
	public User createUser(User formUser) throws Exception;
	
	public User getUserById(Long id) throws Exception;
	
	public User updateUser(User user) throws Exception;
	
	public void deleteUser(Long id) throws Exception;
	
	
}
