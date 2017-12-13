package com.assembla.service;

import java.util.List;

import com.assembla.SSHKey;

public interface SSHKeyResource {

	List<SSHKey> getAll();

	SSHKey get(Integer id);

	SSHKey create(SSHKey sshKey);

	void update(SSHKey sshKey);

	void delete(int id);

}
