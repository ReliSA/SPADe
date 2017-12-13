package com.assembla.service;

import java.util.List;

import com.assembla.SSHAction;

public interface SSHActionResource {

	List<SSHAction> getAll();

	SSHAction get(int id);

	SSHAction create(SSHAction action);

	void update(SSHAction action);

	void delete(int id);

	SSHAction run(int id);

}
