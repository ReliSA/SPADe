package com.assembla.service;

import java.util.List;

import com.assembla.SSHActionLaunch;
import com.assembla.client.AssemblaClient;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;

public class SSHActionLaunchService extends AbstractBaseService implements SSHActionLaunchResource {

	public SSHActionLaunchService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHActionLaunchResource#getAll()
	 */
	@Override
	public List<SSHActionLaunch> getAll() {
		String uri = String.format(AssemblaConstants.SSH_ACTION_LAUNCHES, this.spaceId);
		AssemblaRequest request = new AssemblaRequest(uri, SSHActionLaunch[].class);
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHActionLaunchResource#getAll(int)
	 */
	@Override
	public List<SSHActionLaunch> getAll(int id) {
		String uri = String.format(AssemblaConstants.SSH_ACTION_LAUNCHES_ID, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri, SSHActionLaunch[].class);
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHActionLaunchResource#get(int)
	 */
	@Override
	public SSHActionLaunch get(int id) {
		String uri = String.format(AssemblaConstants.SSH_ACTION_LAUNCH, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri, SSHActionLaunch.class);
		return super.get(request, String.format("No SSHActionLaunch with id %d", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHActionLaunchResource#getFullOutput(int)
	 */
	@Override
	public SSHActionLaunch getFullOutput(int id) {
		String uri = String.format(AssemblaConstants.SHH_ACTION_LAUNCH_OUTPUT, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri, SSHActionLaunch.class);
		return super.get(request, String.format("No SSHActionLaunch with id %d", id));
	}

}
