package com.assembla.service;

import static java.lang.String.format;

import java.util.List;

import com.assembla.Space;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaClient;
import com.assembla.utils.ValidationUtils;

public class SpaceService extends AbstractBaseService implements SpaceResource {

	public SpaceService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SpaceResource#getAll()
	 */
	@Override
	public List<Space> getAll() {
		AssemblaRequest request = new AssemblaRequest(AssemblaConstants.SPACES, Space[].class);
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SpaceResource#get(java.lang.String)
	 */
	@Override
	public Space get(String id) {
		ValidationUtils.notNull(id, "id == null");
		AssemblaRequest request = new AssemblaRequest(format(AssemblaConstants.SPACE_BY_ID, id), Space.class);
		return super.get(request, format("Space with id %s does not exist", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SpaceResource#create(com.assembla.Space)
	 */
	@Override
	public Space create(Space space) {
		AssemblaRequest request = new AssemblaRequest(AssemblaConstants.SPACES, Space.class);
		request.withBody(space);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SpaceResource#update(com.assembla.Space)
	 */
	@Override
	public void update(Space space) {
		ValidationUtils.notNull(space, "space == null");
		ValidationUtils.notNull(space.getId(), "space requires an id");

		String uri = format(AssemblaConstants.SPACE_BY_ID, space.getId());
		AssemblaRequest request = new AssemblaRequest(uri, Space.class);
		request.withBody(space);
		super.client.put(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SpaceResource#delete(com.assembla.Space)
	 */
	@Override
	public void delete(Space space) {
		ValidationUtils.notNull(space, "space == null");
		ValidationUtils.notNull(space.getId(), "space requires an id");

		String uri = format(AssemblaConstants.SPACE_BY_ID, space.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		client.delete(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.SpaceResource#copy(com.assembla.Space, java.lang.String, java.lang.String)
	 */
	@Override
	public Space copy(Space oldSpace, String name, String wikiName) {
		ValidationUtils.notNull(oldSpace, "oldSpace == null");
		ValidationUtils.notNull(name, "name must not be null");
		ValidationUtils.notNull(wikiName, "wikiName must not be null");

		String uri = format(AssemblaConstants.SPACE_COPY, oldSpace.getId());
		AssemblaRequest request = new AssemblaRequest(uri, Space.class);
		// Template for serializing
		Space space = new Space();
		space.setName(name);
		space.setWikiName(wikiName);
		request.withBody(space);

		return super.post(request);
	}

}
