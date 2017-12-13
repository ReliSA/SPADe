package com.assembla.service;

import static java.lang.String.format;

import java.util.List;

import com.assembla.User;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaClient;
import com.assembla.utils.ValidationUtils;

public class UserService extends AbstractBaseService implements UserResource {

	public UserService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.UserResource#get()
	 */
	@Override
	public User get() {
		AssemblaRequest request = new AssemblaRequest(AssemblaConstants.USER_CURRENT, User.class);
		return super.get(request, "Error retreiving current user");
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.UserResource#get(java.lang.String)
	 */
	@Override
	public User get(String idOrLogin) {
		ValidationUtils.notNull(idOrLogin, "idOrLogin == null");
		String uri = format(AssemblaConstants.USER_ID_OR_LOGIN, idOrLogin);
		AssemblaRequest request = new AssemblaRequest(uri, User.class);
		return super.get(request, format("User not found with id/login : %s", idOrLogin));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.UserResource#getForSpace()
	 */
	@Override
	public List<User> getForSpace() {
		String uri = format(AssemblaConstants.USERS_FOR_SPACE, super.getSpaceId());
		AssemblaRequest request = new AssemblaRequest(uri, User[].class);
		return super.getList(request);
	}

}
