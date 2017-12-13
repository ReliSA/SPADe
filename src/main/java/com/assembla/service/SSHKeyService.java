package com.assembla.service;

import java.util.List;

import com.assembla.SSHKey;
import com.assembla.client.AssemblaClient;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.utils.ValidationUtils;

public class SSHKeyService extends AbstractBaseService implements SSHKeyResource {

	public SSHKeyService(AssemblaClient assemblaClient) {
		super(assemblaClient, null);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHKeyResource#getAll()
	 */
	@Override
	public List<SSHKey> getAll() {
		AssemblaRequest request = new AssemblaRequest(AssemblaConstants.SSH_KEY_ALL, SSHKey[].class);
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHKeyResource#get(java.lang.Integer)
	 */
	@Override
	public SSHKey get(Integer id) {
		ValidationUtils.notNull(id, "id == null");
		String uri = String.format(AssemblaConstants.SSH_KEY, id);
		AssemblaRequest request = new AssemblaRequest(uri, SSHKey.class);
		return super.get(request, String.format("No SSHKey with id %d exists", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHKeyResource#create(com.assembla.SSHKey)
	 */
	@Override
	public SSHKey create(SSHKey sshKey) {
		ValidationUtils.notNull(sshKey, "sshKey == null");
		AssemblaRequest request = new AssemblaRequest(AssemblaConstants.SSH_KEY_ALL, SSHKey.class);
		request.withBody(sshKey);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHKeyResource#update(com.assembla.SSHKey)
	 */
	@Override
	public void update(SSHKey sshKey) {
		ValidationUtils.notNull(sshKey, "sshKey == null");
		ValidationUtils.notNull(sshKey.getId(), "SSHKey must have an id");
		String uri = String.format(AssemblaConstants.SSH_KEY, sshKey.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		request.withBody(sshKey);
		super.client.put(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SSHKeyResource#delete(int)
	 */
	@Override
	public void delete(int id) {
		String uri = String.format(AssemblaConstants.SSH_KEY, id);
		AssemblaRequest request = new AssemblaRequest(uri);
		super.client.delete(request);
	}

}
