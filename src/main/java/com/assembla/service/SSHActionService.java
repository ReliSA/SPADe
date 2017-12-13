package com.assembla.service;

import java.util.List;

import com.assembla.SSHAction;
import com.assembla.client.AssemblaClient;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.utils.ValidationUtils;

public class SSHActionService extends AbstractBaseService implements SSHActionResource {

	public SSHActionService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHActionResource#getAll()
	 */
	@Override
	public List<SSHAction> getAll() {
		String uri = String.format(AssemblaConstants.SSH_ACTIONS, this.spaceId);
		AssemblaRequest request = new AssemblaRequest(uri, SSHAction[].class);
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHActionResource#get(int)
	 */
	@Override
	public SSHAction get(int id) {
		String uri = String.format(AssemblaConstants.SSH_ACTION, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri, SSHAction.class);
		return super.get(request, String.format("No SSHAction with id %d", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHActionResource#create(com.assembla.SSHAction)
	 */
	@Override
	public SSHAction create(SSHAction action) {
		ValidationUtils.notNull(action, "action == null");
		String uri = String.format(AssemblaConstants.SSH_ACTIONS, this.spaceId);
		AssemblaRequest request = new AssemblaRequest(uri, SSHAction.class);
		request.withBody(action);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHActionResource#update(com.assembla.SSHAction)
	 */
	@Override
	public void update(SSHAction action) {
		ValidationUtils.notNull(action, "action == null");
		ValidationUtils.notNull(action.getId(), "action must have an id");
		String uri = String.format(AssemblaConstants.SSH_ACTION, this.spaceId, action.getId());
		AssemblaRequest request = new AssemblaRequest(uri, SSHAction.class);
		request.withBody(action);
		super.client.put(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHActionResource#delete(int)
	 */
	@Override
	public void delete(int id) {
		String uri = String.format(AssemblaConstants.SSH_ACTION, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri);
		super.client.delete(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHActionResource#run(int)
	 */
	@Override
	public SSHAction run(int id) {
		String uri = String.format(AssemblaConstants.SSH_ACTION_RUN, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri, SSHAction.class);
		return super.get(request, String.format("Error running SSHAction with id %d", id));
	}

}
