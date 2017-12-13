package com.assembla.service;

import java.util.List;

import com.assembla.SSHActionLaunch;

public interface SSHActionLaunchResource {

	List<SSHActionLaunch> getAll();

	List<SSHActionLaunch> getAll(int id);

	SSHActionLaunch get(int id);

	SSHActionLaunch getFullOutput(int id);

}
