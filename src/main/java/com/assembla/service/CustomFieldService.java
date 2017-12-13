package com.assembla.service;

import static java.lang.String.format;

import java.util.List;

import com.assembla.CustomField;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaClient;
import com.assembla.utils.ValidationUtils;

public class CustomFieldService extends AbstractBaseService implements CustomFieldResource {

	public CustomFieldService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.CustomFieldResource#getAll()
	 */
	@Override
	public List<CustomField> getAll() {
		String uri = format(AssemblaConstants.CUSTOM_FIELDS, super.getSpaceId());
		AssemblaRequest request = new AssemblaRequest(uri, CustomField[].class);
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.CustomFieldResource#get(int)
	 */
	@Override
	public CustomField get(int id) {
		String uri = format(AssemblaConstants.CUSTOM_FIELDS_BY_ID, super.getSpaceId(), id);
		AssemblaRequest request = new AssemblaRequest(uri, CustomField.class);
		return super.get(request, format("No custom field with id : %d exists", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.CustomFieldResource#create(com.assembla.CustomField)
	 */
	@Override
	public CustomField create(CustomField customField) {
		ValidationUtils.notNull(customField, "customerField == null");
		String uri = format(AssemblaConstants.CUSTOM_FIELDS, super.getSpaceId());
		AssemblaRequest request = new AssemblaRequest(uri, CustomField.class);
		request.withBody(customField);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.CustomFieldResource#update(com.assembla.CustomField)
	 */
	@Override
	public void update(CustomField customField) {
		ValidationUtils.notNull(customField, "customerField == null");
		ValidationUtils.notNull(customField.getId(), "customField requires an id");
		String uri = format(AssemblaConstants.CUSTOM_FIELDS_BY_ID, super.getSpaceId(), customField.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		request.withBody(customField);
		client.put(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.CustomFieldResource#delete(com.assembla.CustomField)
	 */
	@Override
	public void delete(CustomField customField) {
		ValidationUtils.notNull(customField, "customerField == null");
		ValidationUtils.notNull(customField.getId(), "customField requires an id");
		String uri = format(AssemblaConstants.CUSTOM_FIELDS_BY_ID, super.getSpaceId(), customField.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		client.delete(request);
	}

}
