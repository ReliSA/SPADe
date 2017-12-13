package com.assembla.service;

import com.assembla.Webhook;

public interface WebhookResource {

	Webhook create(Webhook webhook);

	void update(Webhook webhook);

	void delete(int id);

}
