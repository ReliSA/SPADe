package com.assembla.service;

import java.util.List;

import com.assembla.SSHServer;

public interface SSHServerResource {

	List<SSHServer> getAll();

	SSHServer get(int id);

	SSHServer create(SSHServer sshServer);

	void update(SSHServer sshServer);

	void delete(int id);

}
