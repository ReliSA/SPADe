package com.assembla.service;

import java.util.List;

import com.assembla.SSHServer;
import com.assembla.client.AssemblaClient;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.utils.ValidationUtils;

public class SSHServerService extends AbstractBaseService implements SSHServerResource {

	public SSHServerService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHServerResource#getAll()
	 */
	@Override
	public List<SSHServer> getAll() {
		String uri = String.format(AssemblaConstants.SSH_SERVERS, this.spaceId);
		AssemblaRequest request = new AssemblaRequest(uri, SSHServer[].class);
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHServerResource#get(int)
	 */
	@Override
	public SSHServer get(int id) {
		String uri = String.format(AssemblaConstants.SSH_SERVER, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri, SSHServer.class);
		return super.get(request, String.format("Error getting SSHServer with id %d", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHServerResource#create(com.assembla.SSHServer)
	 */
	@Override
	public SSHServer create(SSHServer sshServer) {
		ValidationUtils.notNull(sshServer, "sshServer == null");
		String uri = String.format(AssemblaConstants.SSH_SERVERS, this.spaceId);
		AssemblaRequest request = new AssemblaRequest(uri, SSHServer.class);
		request.withBody(sshServer);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHServerResource#update(com.assembla.SSHServer)
	 */
	@Override
	public void update(SSHServer sshServer) {
		ValidationUtils.notNull(sshServer, "sshServer == null");
		ValidationUtils.notNull(sshServer.getId(), "sshServer must have an id");
		String uri = String.format(AssemblaConstants.SSH_SERVER, this.spaceId, sshServer.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		request.withBody(sshServer);
		super.client.put(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHServerResource#delete(int)
	 */
	@Override
	public void delete(int id) {
		String uri = String.format(AssemblaConstants.SSH_SERVER, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri);
		super.client.delete(request);
	}

}
