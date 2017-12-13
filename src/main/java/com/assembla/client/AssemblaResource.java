package com.assembla.client;

import com.assembla.service.ActivityResource;
import com.assembla.service.CustomFieldResource;
import com.assembla.service.DocumentResource;
import com.assembla.service.MentionResource;
import com.assembla.service.MergeRequestResource;
import com.assembla.service.MergeRequestVersionResource;
import com.assembla.service.MilestoneResource;
import com.assembla.service.SSHActionLaunchResource;
import com.assembla.service.SSHActionResource;
import com.assembla.service.SSHKeyResource;
import com.assembla.service.SSHServerResource;
import com.assembla.service.SpaceResource;
import com.assembla.service.SpaceSSHKeyResource;
import com.assembla.service.SpaceToolResource;
import com.assembla.service.StandupAwayReportResource;
import com.assembla.service.StandupReportResource;
import com.assembla.service.TagResource;
import com.assembla.service.TaskResource;
import com.assembla.service.TicketAssociationResource;
import com.assembla.service.TicketCommentResource;
import com.assembla.service.TicketResource;
import com.assembla.service.TicketStatusResource;
import com.assembla.service.UserResource;
import com.assembla.service.UserRoleResource;
import com.assembla.service.WebhookResource;
import com.assembla.service.WikiPageResource;

public interface AssemblaResource {

	/**
	 * Create and return new resource for accessing ticket end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return Ticket end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#tickets">
	 *      Documentation for ticket end points</a>
	 * 
	 */
	TicketResource tickets(String spaceId);

	/**
	 * Create and return new resource for accessing activity end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return Activity end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#activity">
	 *      Documentation for activity end points</a>
	 * 
	 */
	ActivityResource activity(String spaceId);

	/**
	 * Create and return new resource for accessing mentions end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return mentions end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#mentions">
	 *      Documentation for mentions end points</a>
	 * 
	 */
	MentionResource mentions(String spaceId);

	/**
	 * Create and return new resource for accessing users end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return users end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#users">
	 *      Documentation for users end points</a>
	 * 
	 */
	UserResource users(String spaceId);

	/**
	 * Create and return new resource for accessing spaces end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return spaces end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#spaces">
	 *      Documentation for spaces end points</a>
	 * 
	 */
	SpaceResource spaces(String spaceId);

	/**
	 * Create and return new resource for accessing user roles end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return user roles end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#user-roles-space-members">
	 *      Documentation for user roles end points</a>
	 * 
	 */
	UserRoleResource roles(String spaceId);

	/**
	 * Create and return new resource for accessing space tools end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return space tools end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#space-tools">
	 *      Documentation for space tools end points</a>
	 * 
	 */
	SpaceToolResource tools(String spaceId);

	/**
	 * Create and return new resource for accessing ticket statuses end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return ticket statuses end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#ticket-statuses">
	 *      Documentation for ticket statuses end points</a>
	 * 
	 */
	TicketStatusResource ticketStatuses(String spaceId);

	/**
	 * Create and return new resource for accessing tags end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return tags end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#tags">
	 *      Documentation for tags end points</a>
	 * 
	 */
	TagResource tags(String spaceId);

	/**
	 * Create and return new resource for accessing ticket custom fields end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return ticket custom fields end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#tickets-custom-fields">
	 *      Documentation for ticket custom fields end points</a>
	 * 
	 */
	CustomFieldResource customFields(String spaceId);

	/**
	 * Create and return new resource for accessing ticket associations end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return ticket associations end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#ticket-associations">
	 *      Documentation for ticket associations end points</a>
	 * 
	 */
	TicketAssociationResource ticketAssociations(String spaceId);

	/**
	 * Create and return new resource for accessing ticket comments end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return ticket comments end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#ticket-comments">
	 *      Documentation for ticket comments end points</a>
	 * 
	 */
	TicketCommentResource ticketComments(String spaceId);

	/**
	 * Create and return new resource for accessing milestones end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return milestones end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#milestones">
	 *      Documentation for milestones end points</a>
	 * 
	 */
	MilestoneResource milestones(String spaceId);

	/**
	 * Create and return new resource for accessing document end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return document end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#documents">
	 *      Documentation for document end points</a>
	 * 
	 */
	DocumentResource documents(String spaceId);

	/**
	 * Create and return new resource for accessing stand up report end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return stand up report end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#standup-reports">
	 *      Document for stand up end points</a>
	 * 
	 */
	StandupReportResource standUps(String spaceId);

	/**
	 * Create and return new resource for accessing stand up away report end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return stand up away report end point resource
	 * @see <a href=
	 *      "https://api-doc.assembla.com/content/api_reference.html#standup-away-reports"
	 *      > Documentation for stand up away reports<a>
	 * 
	 */
	StandupAwayReportResource standUpsAway(String spaceId);

	/**
	 * Create and return new resource for accessing merge request end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @param spaceToolId
	 *            space tool id or name
	 * @return merge request end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#merge-requests">
	 *      Documentation for merge request end points</a>
	 * 
	 */
	MergeRequestResource mergeRequests(String spaceId, int spaceToolId);

	/**
	 * Create and return new resource for accessing merge request version end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @param spaceToolId
	 *            space tool id or name
	 * @return merge request version end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#merge-request-versions">
	 *      Documentation for merge request version end points</a>
	 * 
	 */
	MergeRequestVersionResource mergeRequestVersions(String spaceId, int spaceToolId);

	/**
	 * Create and return new resource for accessing wiki page end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return wiki page end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#wiki-pages">
	 *      Documentation for wiki page end points</a>
	 * 
	 */
	WikiPageResource wikis(String spaceId);

	/**
	 * Create and return new resource for accessing webhook end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return webhook end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#webhooks">
	 *      Documentation for webhook end points</a>
	 * 
	 */
	WebhookResource webhooks(String spaceId);

	/**
	 * Create and return new resource for accessing tasks end points
	 * 
	 * @return tasks end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#tasks">
	 *      Documentation for tasks end points</a>
	 * 
	 */
	TaskResource tasks();

	/**
	 * Create and return new resource for accessing user SSH key end points
	 * 
	 * @return user SSH key end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#user-ssh-keys">
	 *      Documentation for user SSH key end points</a>
	 * 
	 */
	SSHKeyResource userKeys();

	/**
	 * Get resource for accessing SSH action end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return SSH action end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#ssh-actions">
	 *      Documentation for SSH action end points</a>
	 * 
	 */
	SSHActionResource sshActions(String spaceId);

	/**
	 * Create and return new resource for accessing SSH action launch end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return SSH action launch end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#ssh-action-launches">
	 *      Documentation for SSH action launch end points</a>
	 * 
	 */
	SSHActionLaunchResource sshActionLaunches(String spaceId);

	/**
	 * Create and return new resource for accessing SSH server end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return SSH server end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#ssh-servers">
	 *      Documentation for SSH server end points</a>
	 * 
	 */
	SSHServerResource sshServers(String spaceId);

	/**
	 * Create and return new resource for accessing space SSH key end points
	 * 
	 * @param spaceId
	 *            Id of the space for which requests will be made to
	 * @return space SSH key end point resource
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/api_reference.html#space-ssh-keys">
	 *      Documentation for space SSH key end points</a>
	 * 
	 */
	SpaceSSHKeyResource spaceKeys(String spaceId);

}
