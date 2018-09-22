package com.helpdesk.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.helpdesk.api.entity.ChangeStatus;
import com.helpdesk.api.entity.Ticket;
import com.helpdesk.api.repository.ChangeStatusRepository;
import com.helpdesk.api.repository.TicketRepository;
import com.helpdesk.api.service.TicketService;

@Service
public class TicketServiceImpl implements TicketService {

	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private ChangeStatusRepository changeStatusRepository;
	
	public TicketServiceImpl() {
	}

	@Override
	public Ticket createOrUpdate(Ticket ticket) {
		return this.ticketRepository.save(ticket);
	}

	@Override
	public Ticket findById(String id) {
		return this.ticketRepository.findOneById(id);
	}

	@Override
	public void delete(Ticket ticket) {
		this.ticketRepository.delete(ticket);
	}

	@Override
	public Page<Ticket> listTicket(int page, int count) {
		return this.ticketRepository.findAll(PageRequest.of(page, count));
	}

	@Override
	public ChangeStatus createChangeStatus(ChangeStatus changeStatus) {
		return this.changeStatusRepository.save(changeStatus);
	}

	@Override
	public Iterable<ChangeStatus> listChangeStatus(String ticketId) {
		return this.changeStatusRepository.findByTicketIdOrderByDateChangeDesc(ticketId);
	}

	@Override
	public Page<Ticket> findByCurrentUser(int page, int count, String userId) {
		return this.ticketRepository.findByUserIdOrderByDateDesc(PageRequest.of(page, count), userId);
	}

	@Override
	public Page<Ticket> findByParameters(int page, int count, String title, String status, String priority) {
		return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusIgnoreCaseContainingAndPriorityOrderByDateDesc(
				 title, status, priority, PageRequest.of(page, count));
	}

	@Override
	public Page<Ticket> findByParametersAndCurrentUser(int page, int count, String title, String status,
			String priority, String userId) {
		return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc(
				title, status, priority, userId, PageRequest.of(page, count));
	}

	@Override
	public Page<Ticket> findByNumber(int page, int count, Integer number) {
		return this.ticketRepository.findByNumber(number, PageRequest.of(page, count));
	}

	@Override
	public Iterable<Ticket> findAll() {
		return this.ticketRepository.findAll();
	}

	@Override
	public Page<Ticket> findByParametersAndAssignedUser(int page, int count, String title, String status,
			String priority, String assignedUserId) {
		return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityIgnoreCaseContainingAndAssigneUserIdOrderByDateDesc(
				title, status, priority, assignedUserId, PageRequest.of(page, count));
	}

}
