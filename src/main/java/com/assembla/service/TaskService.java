package com.assembla.service;

import com.assembla.Task;
import com.assembla.client.AssemblaClient;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.PagedAssemblaRequest;
import com.assembla.client.PagedIterator;
import com.assembla.utils.ValidationUtils;

public class TaskService extends AbstractBaseService implements TaskResource {

	public TaskService(AssemblaClient client) {
		super(client, null);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TaskResource#getAll(com.assembla.service.TaskRequest)
	 */
	@Override
	public PagedIterator<Task> getAll(TaskRequest taskReq) {
		ValidationUtils.notNull(taskReq, "taskReq == null");
		String uri = "/tasks.json";
		PagedAssemblaRequest request = new PagedAssemblaRequest(uri, Task[].class, taskReq.getPageNumber(),
				taskReq.getPageSize());

		taskReq.getDateRange().ifPresent(e -> request.addParam("from", e.getFromString()));
		taskReq.getDateRange().ifPresent(e -> request.addParam("to", e.getToString()));
		taskReq.getDirection().ifPresent(e -> request.addParam("sort_order", e.getValue()));
		taskReq.getSortBy().ifPresent(e -> request.addParam("sort_by", e));
		if (!taskReq.getSpaces().isEmpty()) {
			request.addParam("spaces[]", taskReq.getSpaces().toArray());
		}
		if (!taskReq.getTickets().isEmpty()) {
			request.addParam("ticket_ids[]", taskReq.getTickets().toArray());
		}
		if (!taskReq.getUsers().isEmpty()) {
			request.addParam("users[]", taskReq.getUsers().toArray());
		}

		return new PagedIterator<Task>(request, client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TaskResource#getTask(int)
	 */
	@Override
	public Task getTask(int id) {
		String uri = String.format("/tasks/%d.json", id);
		AssemblaRequest request = new AssemblaRequest(uri, Task.class);
		return super.get(request, String.format("No Task with id %d", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TaskResource#create(com.assembla.Task)
	 */
	@Override
	public Task create(Task task) {
		ValidationUtils.notNull(task, "task == null");
		String uri = "/tasks.json";
		AssemblaRequest request = new AssemblaRequest(uri, Task.class);
		request.withBody(task);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TaskResource#delete(int)
	 */
	@Override
	public void delete(int id) {
		String uri = String.format("/tasks/%d.json", id);
		AssemblaRequest request = new AssemblaRequest(uri);
		super.client.delete(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.TaskResource#update(com.assembla.Task)
	 */
	@Override
	public void update(Task task) {
		ValidationUtils.notNull(task, "task ==null");
		ValidationUtils.notNull(task.getId(), "task must have an id");
		String uri = String.format("/tasks/%d.json", task.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		request.withBody(task);
		super.client.put(request);
	}
}
