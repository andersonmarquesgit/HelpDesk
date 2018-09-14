package com.helpdesk.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.helpdesk.api.entity.Ticket;

public interface TicketRepository extends MongoRepository<Ticket, String> {

	Page<Ticket> findByUserIdOrderByDateDesc(Pageable pages, String userId);
	
	Page<Ticket> findByTitleIgnoreCaseContainingAndStatusIgnoreCaseContainingAndPriorityOrderByDateDesc(
			Pageable pages,
			String title,
			String status,
			String priority);
	
	Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc(
			Pageable pages,
			String title,
			String status,
			String priority);
	
	Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityIgnoreCaseContainingAndAssigneUserIdOrderByDateDesc(
			Pageable pages,
			String title,
			String status,
			String priority);
	
	Page<Ticket> findByNumber(Integer number, Pageable pages);
	
	Ticket findOneById(String id);
}
