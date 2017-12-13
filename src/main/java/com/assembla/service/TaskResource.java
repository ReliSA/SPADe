package com.assembla.service;

import com.assembla.Task;
import com.assembla.client.PagedIterator;

public interface TaskResource {

	PagedIterator<Task> getAll(TaskRequest taskReq);

	Task getTask(int id);

	Task create(Task task);

	void delete(int id);

	void update(Task task);

}
