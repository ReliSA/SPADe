package com.assembla.service;

import java.util.List;

import com.assembla.PrivateSSHKey;

public interface SpaceSSHKeyResource {

	List<PrivateSSHKey> getAll();

	PrivateSSHKey generate();

}
