package com.helpdesk.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.helpdesk.api.entity.User;
import com.helpdesk.api.response.Response;
import com.helpdesk.api.service.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")//Permitindo o acesso de qualquer IP, porta, etc.
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN')")//Autorização com base no perfil. Nesse caso apenas ADMIN podem criar usuários.
	public ResponseEntity<Response<User>> create(HttpServletRequest request, @RequestBody User user, 
			BindingResult result) {
		Response<User> response = new Response<User>();
		
		try {
			validateCreateUser(user, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			user.setPassword(this.passwordEncoder.encode(user.getPassword()));
			User userPersisted = this.userService.createOrUpdate(user);
			response.setData(userPersisted);
		} catch (DuplicateKeyException dE) {
			response.getErrors().add("Email already registered!");
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	private void validateCreateUser(User user, BindingResult result) {
		if(user.getEmail() == null) {
			result.addError(new ObjectError("User", "Email no information"));
		}
	}
	
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN')")//Autorização com base no perfil. Nesse caso apenas ADMIN podem atualizar usuários.
	public ResponseEntity<Response<User>> update(HttpServletRequest request, @RequestBody User user, 
			BindingResult result) {
		Response<User> response = new Response<User>();
		try {
			validateUpdateUser(user, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			user.setPassword(this.passwordEncoder.encode(user.getPassword()));
			User userUpdated = this.userService.createOrUpdate(user);
			response.setData(userUpdated);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	private void validateUpdateUser(User user, BindingResult result) {
		if(user.getId() == null) {
			result.addError(new ObjectError("User", "Id no information"));
		}
		if(user.getEmail() == null) {
			result.addError(new ObjectError("User", "Email no information"));
		}
	}
	
	@GetMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")//Autorização com base no perfil. Nesse caso apenas ADMIN podem consultar usuários.
	public ResponseEntity<Response<User>> findById(@PathVariable String id) {
		Response<User> response = new Response<User>();
		User user = this.userService.findById(id);
		
		if(user == null) {
			response.getErrors().add("Register not found! ID: " + id);
			return ResponseEntity.badRequest().body(response);
		}
		
		response.setData(user);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(value = "{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")//Autorização com base no perfil. Nesse caso apenas ADMIN podem consultar usuários.
	public ResponseEntity<Response<String>> delete(@PathVariable String id) {
		Response<String> response = new Response<String>();
		User user = this.userService.findById(id);
		
		if(user == null) {
			response.getErrors().add("Register not found! ID: " + id);
			return ResponseEntity.badRequest().body(response);
		}
		
		this.userService.delete(id);
		
		return ResponseEntity.ok(new Response<String>());
	}
	
	@GetMapping(value = "{page}/{count}")
	@PreAuthorize("hasAnyRole('ADMIN')")//Autorização com base no perfil. Nesse caso apenas ADMIN podem consultar usuários.
	public ResponseEntity<Response<Page<User>>> findAll(@PathVariable int page, @PathVariable int count) {
		Response<Page<User>> response = new Response<Page<User>>();
		Page<User> users = this.userService.findAll(page, count);
		response.setData(users);
		
		return ResponseEntity.ok(response);
	}
}
