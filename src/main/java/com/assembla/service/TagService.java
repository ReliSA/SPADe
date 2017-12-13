package com.assembla.service;

import static java.lang.String.format;

import com.assembla.Tag;
import com.assembla.Ticket;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaClient;
import com.assembla.client.PagedAssemblaRequest;
import com.assembla.client.PagedIterator;
import com.assembla.utils.ValidationUtils;

public class TagService extends AbstractBaseService implements TagResource {

	public TagService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TagResource#getAll()
	 */
	@Override
	public PagedIterator<Tag> getAll() {
		String uri = format(AssemblaConstants.TAGS, super.getSpaceId());
		PagedAssemblaRequest request = new PagedAssemblaRequest(uri, Tag[].class);
		return new PagedIterator<>(request, client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TagResource#getActive()
	 */
	@Override
	public PagedIterator<Tag> getActive() {
		String uri = format(AssemblaConstants.ACTIVE_TAGS, super.getSpaceId());
		PagedAssemblaRequest request = new PagedAssemblaRequest(uri, Tag[].class);
		return new PagedIterator<>(request, client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TagResource#getProposed()
	 */
	@Override
	public PagedIterator<Tag> getProposed() {
		String uri = format(AssemblaConstants.PROPOSED_TAGS, super.getSpaceId());
		PagedAssemblaRequest request = new PagedAssemblaRequest(uri, Tag[].class);
		return new PagedIterator<>(request, client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TagResource#getHidden()
	 */
	@Override
	public PagedIterator<Tag> getHidden() {
		String uri = format(AssemblaConstants.HIDDEN_TAGS, super.getSpaceId());
		PagedAssemblaRequest request = new PagedAssemblaRequest(uri, Tag[].class);
		return new PagedIterator<>(request, client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TagResource#getTickets(com.assembla.Tag)
	 */
	@Override
	public PagedIterator<Ticket> getTickets(Tag tag) {
		ValidationUtils.notNull(tag, "tag == null");
		ValidationUtils.notNull(tag.getId(), "tag requires an id");

		String uri = format(AssemblaConstants.TICKETS_BY_TAG, super.getSpaceId(), tag.getId());
		PagedAssemblaRequest request = new PagedAssemblaRequest(uri, Tag[].class);
		return new PagedIterator<>(request, client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TagResource#get(int)
	 */
	@Override
	public Tag get(int id) {
		String uri = format(AssemblaConstants.TAG_BY_ID, super.getSpaceId(), id);
		AssemblaRequest request = new AssemblaRequest(uri, Tag.class);
		return super.get(request, format("Error getting tag by id (%s)", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TagResource#create(com.assembla.Tag)
	 */
	@Override
	public Tag create(Tag tag) {
		ValidationUtils.notNull(tag, "tag == null");

		String uri = format(AssemblaConstants.TAGS, super.getSpaceId());
		AssemblaRequest request = new AssemblaRequest(uri, Tag.class);
		request.withBody(tag);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TagResource#update(com.assembla.Tag)
	 */
	@Override
	public void update(Tag tag) {
		ValidationUtils.notNull(tag, "tag == null");
		ValidationUtils.notNull(tag.getId(), "tag requires an id");

		String uri = format(AssemblaConstants.TAG_BY_ID, super.getSpaceId(), tag.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		request.withBody(tag);
		client.put(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TagResource#delete(com.assembla.Tag)
	 */
	@Override
	public void delete(Tag tag) {
		ValidationUtils.notNull(tag, "tag == null");
		ValidationUtils.notNull(tag.getId(), "tag requires an id");

		String uri = format(AssemblaConstants.TAG_BY_ID, super.getSpaceId(), tag.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		client.delete(request);
	}

}
