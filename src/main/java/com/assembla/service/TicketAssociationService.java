package com.assembla.service;

import static java.lang.String.format;

import java.util.List;

import com.assembla.Ticket;
import com.assembla.TicketAssociation;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaClient;
import com.assembla.utils.ValidationUtils;

public class TicketAssociationService extends AbstractBaseService implements TicketAssociationResource {

	public TicketAssociationService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TicketAssociationResource#get(com.assembla.Ticket)
	 */
	@Override
	public List<TicketAssociation> get(Ticket ticket) {
		ValidationUtils.notNull(ticket, "ticket == null");
		String uri = format(AssemblaConstants.TICKET_ASSOCIATIONS, this.getSpaceId(), ticket.getNumber());
		AssemblaRequest request = new AssemblaRequest(uri, TicketAssociation[].class);
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TicketAssociationResource#getById(com.assembla.Ticket, int)
	 */
	@Override
	public TicketAssociation getById(Ticket ticket, int id) {
		ValidationUtils.notNull(ticket, "ticket == null");
		String uri = format(AssemblaConstants.TICKET_ASSOCIATIONS_BY_ID, this.getSpaceId(), ticket.getNumber(), id);
		AssemblaRequest request = new AssemblaRequest(uri, TicketAssociation.class);
		return super.get(request, format("No association with id %d for ticket %d", ticket.getNumber(), id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TicketAssociationResource#create(com.assembla.Ticket, com.assembla.TicketAssociation)
	 */
	@Override
	public TicketAssociation create(Ticket ticket, TicketAssociation association) {
		ValidationUtils.notNull(ticket, "ticket == null");
		ValidationUtils.notNull(association, "association == null");
		ValidationUtils.notNull(association.getTicket2Id(), "Association must have a ticket2 id");

		String uri = format(AssemblaConstants.TICKET_ASSOCIATIONS, super.getSpaceId(), ticket.getNumber(),
				association.getTicket2Id());
		AssemblaRequest request = new AssemblaRequest(uri, TicketAssociation.class);
		request.withBody(association);

		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TicketAssociationResource#update(com.assembla.Ticket, com.assembla.TicketAssociation)
	 */
	@Override
	public void update(Ticket ticket, TicketAssociation association) {
		ValidationUtils.notNull(ticket, "ticket == null");
		ValidationUtils.notNull(association, "association == null");
		ValidationUtils.notNull(association.getId(), "Association must have an id");
		ValidationUtils.notNull(ticket.getNumber(), "ticket must have a number");

		String uri = format(AssemblaConstants.TICKET_ASSOCIATIONS_BY_ID, super.getSpaceId(), ticket.getNumber(),
				association.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		request.withBody(association);

		client.put(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TicketAssociationResource#delete(com.assembla.Ticket, com.assembla.TicketAssociation)
	 */
	@Override
	public void delete(Ticket ticket, TicketAssociation association) {
		ValidationUtils.notNull(ticket, "ticket == null");
		ValidationUtils.notNull(association, "association == null");
		ValidationUtils.notNull(association.getId(), "Association must have an id");
		ValidationUtils.notNull(ticket.getNumber(), "ticket must have a number");

		String uri = format(AssemblaConstants.TICKET_ASSOCIATIONS_BY_ID, super.getSpaceId(), ticket.getNumber(),
				association.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		super.client.delete(request);
	}

}
