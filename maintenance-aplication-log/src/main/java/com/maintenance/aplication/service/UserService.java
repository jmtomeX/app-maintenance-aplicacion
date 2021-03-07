package com.maintenance.aplication.service;

import com.maintenance.aplication.entity.User;

public interface UserService {
	// Iterable puede ser de cualquier tipo List, array, arrayList, set...
	public Iterable<User> getAllUsers();
}
