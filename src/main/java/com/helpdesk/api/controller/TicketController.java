package com.helpdesk.api.controller;

import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.helpdesk.api.entity.Ticket;
import com.helpdesk.api.entity.User;
import com.helpdesk.api.enums.StatusEnum;
import com.helpdesk.api.response.Response;
import com.helpdesk.api.security.jwt.JwtTokenUtil;
import com.helpdesk.api.service.TicketService;
import com.helpdesk.api.service.UserService;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins = "*")
public class TicketController {

	@Autowired
	private TicketService ticketService;
	
	@Autowired
	protected JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserService userService;
	
	@PostMapping
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<?> createOrUpdate(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result) {
		Response<Ticket> response = new Response<Ticket>();
		
		try {
			validateCreateTicket(ticket, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			} 
			
			ticket.setStatus(StatusEnum.getStatus("New"));
			ticket.setUser(this.userFromRequest(request));
			ticket.setDate(new Date());
			ticket.setNumber(this.generateNumber());
			Ticket ticketPersisted = this.ticketService.createOrUpdate(ticket);
			response.setData(ticketPersisted);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	private Integer generateNumber() {
		Random random = new Random();
		return random.nextInt(9999);
	}

	private User userFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		String email = jwtTokenUtil.getUserNameFromToken(token);
		return this.userService.findByEmail(email);
	}

	private void validateCreateTicket(Ticket ticket, BindingResult result) {
		if(ticket.getTitle() == null) {
			result.addError(new ObjectError("Ticket", "Title no information"));
		}
	}

}
