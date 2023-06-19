package com.springboot.blog.service.impl;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.repository.RolesRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.service.AuthService;

@Service
public class AuthServiceImp implements AuthService{

	private AuthenticationManager authenticationManager;
	private UserRepository userRepository;
	private RolesRepository rolesRepository;
	private PasswordEncoder passwordEncoder;

	public AuthServiceImp(AuthenticationManager authenticationManager, 
			UserRepository userRepository,
			RolesRepository rolesRepository, 
			PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.rolesRepository = rolesRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public String login(LoginDto loginDto) {
		Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				loginDto.getUsernameOrEmail(), loginDto.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		
		return "User Logged-in Successfully.";
	}

	@Override
	public String register(RegisterDto registerDto) {
		
		//add check for if username exists in database
		if(userRepository.existsByUsername(registerDto.getUsername())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Username already exists !!");
		}
		
		//add check for if user email exists in databse
		if(userRepository.existsByEmail(registerDto.getEmail())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "User's Email already exists !!");
		}
		
		User user = new User();
		user.setName(registerDto.getName());
		user.setUsername(registerDto.getUsername());
		user.setEmail(registerDto.getEmail());
		user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
		
		Set<Role> roles = new HashSet<>();
		Role userRole = rolesRepository.findByName("ROLE_USER").get();
		roles.add(userRole);
		user.setRoles(roles);
		
		userRepository.save(user);
		
		return "User Registered Successfully.";
		
	}

}
