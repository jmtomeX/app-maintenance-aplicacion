package com.maintenance.aplication.service;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.maintenance.aplication.entity.Role;
import com.maintenance.aplication.respository.UserRepository;
// COnfiguración de spring security
@Service
@Transactional  // se bloquean las transacciones mientras se estén usando
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // Excepción para cuando el username no existe ya explicita
		com.maintenance.aplication.entity.User appUser = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Login username invalido."));

		// recoger los roles, se usa set para que los valores no estén repetidos
		Set<GrantedAuthority> grantList = new HashSet<GrantedAuthority>();
		for (Role role : appUser.getRoles()) {
			System.out.println(role.getDescription());
			GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getDescription());
			grantList.add(grantedAuthority);
		};
		// usuario que se carga en sesión --> IMPORTANTE importar el usuario de spring security
		UserDetails user = (UserDetails) new User(username, appUser.getPassword(),grantList);
		return user;
	}

	public boolean isLoggedUserADMIN() {
		return loggedUserHasRole("ROLE_ADMIN");
	}

	public boolean loggedUserHasRole(String role) {

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		UserDetails loggedUser = null;

		Object roles = null;

		if (principal instanceof UserDetails) {
			loggedUser = (UserDetails) principal;
			roles = loggedUser.getAuthorities().stream().filter(x -> role.equals(x.getAuthority())).findFirst().orElse(null); // loggedUser = null;
		}

		return roles != null ? true : false;

	}

}
