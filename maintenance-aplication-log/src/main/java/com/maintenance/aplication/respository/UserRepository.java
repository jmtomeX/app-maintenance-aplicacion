package com.maintenance.aplication.respository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.maintenance.aplication.entity.User;
@Repository
public interface UserRepository extends CrudRepository<User, Long>{
	
	public Optional<User> findByUsername(String username);
}
