package com.assembla.service;

import static java.lang.String.format;

import java.util.List;

import com.assembla.Mention;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaClient;

public final class MentionService extends AbstractBaseService implements MentionResource {

	public MentionService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MentionResource#getAll()
	 */
	@Override
	public List<Mention> getAll() {
		return getAll(false, false);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MentionResource#getRead()
	 */
	@Override
	public List<Mention> getRead() {
		return getAll(true, false);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MentionResource#getUnread()
	 */
	@Override
	public List<Mention> getUnread() {
		return getAll(false, true);
	}

	private List<Mention> getAll(boolean read, boolean unread) {
		String uri = format(AssemblaConstants.MENTIONS, super.getSpaceId());
		AssemblaRequest request = new AssemblaRequest(uri, Mention[].class);
		if (read) {
			request.addParam("read", true);
		} else if (unread) {
			request.addParam("unread", true);
		}
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MentionResource#get(int)
	 */
	@Override
	public Mention get(int id) {
		String uri = format(AssemblaConstants.MENTION_BY_ID, id);
		AssemblaRequest request = new AssemblaRequest(uri, Mention.class);
		return super.get(request, format("Mention with id %d does not exist", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MentionResource#markAsRead(int)
	 */
	@Override
	public void markAsRead(int id) {
		String uri = format(AssemblaConstants.MENTION_MARK_AS_READ, id);
		AssemblaRequest request = new AssemblaRequest(uri);
		client.put(request);
	}

}
