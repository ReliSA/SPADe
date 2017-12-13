package com.assembla.service;

import java.util.List;

import static java.lang.String.format;

import com.assembla.TicketStatus;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaClient;
import com.assembla.utils.ValidationUtils;

public class TicketStatusService extends AbstractBaseService implements TicketStatusResource {

	public TicketStatusService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TicketStatusResource#getAll()
	 */
	@Override
	public List<TicketStatus> getAll() {
		String uri = format(AssemblaConstants.TICKET_STATUS_ALL, super.spaceId);
		AssemblaRequest request = new AssemblaRequest(uri, TicketStatus[].class);
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TicketStatusResource#get(int)
	 */
	@Override
	public TicketStatus get(int id) {
		String uri = format(AssemblaConstants.TICKET_STATUS_ID, super.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri, TicketStatus.class);
		return super.get(request, format("No TicketStatus with id : %d", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TicketStatusResource#create(com.assembla.TicketStatus)
	 */
	@Override
	public TicketStatus create(TicketStatus status) {
		ValidationUtils.notNull(status, "ticketStatus == null");
		String uri = format(AssemblaConstants.TICKET_STATUS_ALL, super.spaceId);
		AssemblaRequest request = new AssemblaRequest(uri, TicketStatus.class);
		request.withBody(status);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TicketStatusResource#update(com.assembla.TicketStatus)
	 */
	@Override
	public void update(TicketStatus ticketStatus) {
		ValidationUtils.notNull(ticketStatus, "ticketStatus == null");
		ValidationUtils.notNull(ticketStatus.getId(), "ticketStatus must have an id");
		String uri = format(AssemblaConstants.TICKET_STATUS_ID, super.spaceId, ticketStatus.getId());
		AssemblaRequest request = new AssemblaRequest(uri, TicketStatus.class);
		request.withBody(ticketStatus);
		super.client.put(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TicketStatusResource#delete(com.assembla.TicketStatus)
	 */
	@Override
	public void delete(TicketStatus ticketStatus) {
		ValidationUtils.notNull(ticketStatus, "ticketStatus == null");
		ValidationUtils.notNull(ticketStatus.getId(), "ticketStatus must have an id");
		String uri = format(AssemblaConstants.TICKET_STATUS_ID, super.spaceId, ticketStatus.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		super.client.delete(request);
	}

}
