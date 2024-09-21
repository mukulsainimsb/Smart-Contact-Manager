package com.smartcontact.project.smart_contact_manager.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smartcontact.project.smart_contact_manager.Userr;
import com.smartcontact.project.smart_contact_manager.configuration.CustomUserDetails;
import com.smartcontact.project.smart_contact_manager.repository.UserRepository;

@Configuration
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Userr user= userRepository.findByEmail(username);
		
		if(user==null) {
			throw new UsernameNotFoundException("user not found");
		}
		else {
		CustomUserDetails customUserDetails=new CustomUserDetails(user);
		return customUserDetails;
	}
	}
}
	
