package com.assembla.service;

import static java.lang.String.format;

import com.assembla.TicketComment;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaClient;
import com.assembla.client.PagedAssemblaRequest;
import com.assembla.client.PagedIterator;
import com.assembla.utils.ValidationUtils;

public class TicketCommentService extends AbstractBaseService implements TicketCommentResource {

	public TicketCommentService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	@Override
	public PagedIterator<TicketComment> getAll(int number) {
		String uri = format(AssemblaConstants.TICKET_COMMENTS_FOR_TICKET, super.getSpaceId(), number);
		PagedAssemblaRequest request = new PagedAssemblaRequest(uri, TicketComment[].class);
		return new PagedIterator<TicketComment>(request, client);
	}

	@Override
	public TicketComment getById(int number, int commentId) {
		String uri = format(AssemblaConstants.TICKET_COMMENT, super.getSpaceId(), number, commentId);
		AssemblaRequest request = new AssemblaRequest(uri, TicketComment.class);
		return super.get(request, format("No ticket with id %d for ticket %d exists", commentId, number));
	}

	@Override
	public TicketComment create(int number, TicketComment newTicketComment) {
		String uri = format(AssemblaConstants.TICKET_COMMENTS_FOR_TICKET, super.getSpaceId(), number);
		AssemblaRequest request = new AssemblaRequest(uri, TicketComment.class);
		request.withBody(newTicketComment);
		return super.post(request);
	}

	@Override
	public void update(int number, TicketComment ticketComment) {
		ValidationUtils.notNull(ticketComment, "ticketComment == null");
		ValidationUtils.notNull(ticketComment.getId(), "ticketComment requires an id");

		String uri = format(AssemblaConstants.TICKET_COMMENT, super.getSpaceId(), number, ticketComment.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		request.withBody(ticketComment);
		client.put(request);
	}

}
