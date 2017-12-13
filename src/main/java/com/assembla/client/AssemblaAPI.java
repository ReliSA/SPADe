package com.assembla.client;

import com.assembla.service.ActivityResource;
import com.assembla.service.ActivityService;
import com.assembla.service.CustomFieldResource;
import com.assembla.service.CustomFieldService;
import com.assembla.service.DocumentResource;
import com.assembla.service.DocumentService;
import com.assembla.service.MentionResource;
import com.assembla.service.MentionService;
import com.assembla.service.MergeRequestResource;
import com.assembla.service.MergeRequestService;
import com.assembla.service.MergeRequestVersionResource;
import com.assembla.service.MergeRequestVersionService;
import com.assembla.service.MilestoneResource;
import com.assembla.service.MilestoneService;
import com.assembla.service.SSHActionLaunchResource;
import com.assembla.service.SSHActionLaunchService;
import com.assembla.service.SSHActionResource;
import com.assembla.service.SSHActionService;
import com.assembla.service.SSHKeyResource;
import com.assembla.service.SSHKeyService;
import com.assembla.service.SSHServerResource;
import com.assembla.service.SSHServerService;
import com.assembla.service.SpaceResource;
import com.assembla.service.SpaceSSHKeyResource;
import com.assembla.service.SpaceSSHKeyService;
import com.assembla.service.SpaceService;
import com.assembla.service.SpaceToolResource;
import com.assembla.service.SpaceToolService;
import com.assembla.service.StandupAwayReportResource;
import com.assembla.service.StandupAwayReportService;
import com.assembla.service.StandupReportResource;
import com.assembla.service.StandupReportService;
import com.assembla.service.TagResource;
import com.assembla.service.TagService;
import com.assembla.service.TaskResource;
import com.assembla.service.TaskService;
import com.assembla.service.TicketAssociationResource;
import com.assembla.service.TicketAssociationService;
import com.assembla.service.TicketCommentResource;
import com.assembla.service.TicketCommentService;
import com.assembla.service.TicketResource;
import com.assembla.service.TicketService;
import com.assembla.service.TicketStatusResource;
import com.assembla.service.TicketStatusService;
import com.assembla.service.UserResource;
import com.assembla.service.UserRoleResource;
import com.assembla.service.UserRoleService;
import com.assembla.service.UserService;
import com.assembla.service.WebhookResource;
import com.assembla.service.WebhookService;
import com.assembla.service.WikiPageResource;
import com.assembla.service.WikiService;
import com.assembla.utils.ValidationUtils;

/**
 * Base class for accessing Assembla's REST API
 * 
 * @see <a href="https://api-doc.assembla.com/content/api_reference.html">
 *      Assembla API Documentation</a>
 * 
 * @author matthewsladen
 *
 */
public final class AssemblaAPI implements AssemblaResource {

	private final AssemblaClient assemblaClient;

	AssemblaAPI(String apiKey, String apiSecret, AssemblaClient client) {
		this.assemblaClient = client;
	}

	/**
	 * Create a new AssemblaAPI object.
	 * 
	 * @param apiKey
	 *            User's Assembla API key, not null
	 * @param apiSecret
	 *            Users Assembla API key secret, not null
	 * @return A new AssemblaAPI which will use the supplied {@code apiKey} and
	 *         {@code apiSecret} to make requests
	 * 
	 * @see <a
	 *      href="https://api-doc.assembla.com/content/authentication.html#key_secret_credentials">
	 *      Assembla Documentation for getting an API key</a>
	 */
	public static AssemblaResource create(String apiKey, String apiSecret) {
		ValidationUtils.notNull(apiKey, "API Key Required");
		ValidationUtils.notNull(apiSecret, "API Secret Required");
		AssemblaClient client = new AssemblaClient(apiKey, apiSecret);
		return new AssemblaAPI(apiKey, apiSecret, client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#tickets(java.lang.String)
	 */
	@Override
	public TicketResource tickets(String spaceId) {
		return new TicketService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#activity(java.lang.String)
	 */
	@Override
	public ActivityResource activity(String spaceId) {
		return new ActivityService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#mentions(java.lang.String)
	 */
	@Override
	public MentionResource mentions(String spaceId) {
		return new MentionService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#users(java.lang.String)
	 */
	@Override
	public UserResource users(String spaceId) {
		return new UserService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#spaces(java.lang.String)
	 */
	@Override
	public SpaceResource spaces(String spaceId) {
		return new SpaceService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#roles(java.lang.String)
	 */
	@Override
	public UserRoleResource roles(String spaceId) {
		return new UserRoleService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#tools(java.lang.String)
	 */
	@Override
	public SpaceToolResource tools(String spaceId) {
		return new SpaceToolService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#ticketStatuses(java.lang.String)
	 */
	@Override
	public TicketStatusResource ticketStatuses(String spaceId) {
		return new TicketStatusService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#tags(java.lang.String)
	 */
	@Override
	public TagResource tags(String spaceId) {
		return new TagService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#customFields(java.lang.String)
	 */
	@Override
	public CustomFieldResource customFields(String spaceId) {
		return new CustomFieldService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#ticketAssociations(java.lang.String)
	 */
	@Override
	public TicketAssociationResource ticketAssociations(String spaceId) {
		return new TicketAssociationService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#ticketComments(java.lang.String)
	 */
	@Override
	public TicketCommentResource ticketComments(String spaceId) {
		return new TicketCommentService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#milestones(java.lang.String)
	 */
	@Override
	public MilestoneResource milestones(String spaceId) {
		return new MilestoneService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#documents(java.lang.String)
	 */
	@Override
	public DocumentResource documents(String spaceId) {
		return new DocumentService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#standUps(java.lang.String)
	 */
	@Override
	public StandupReportResource standUps(String spaceId) {
		return new StandupReportService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#standUpsAway(java.lang.String)
	 */
	@Override
	public StandupAwayReportResource standUpsAway(String spaceId) {
		return new StandupAwayReportService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#mergeRequests(java.lang.String, int)
	 */
	@Override
	public MergeRequestResource mergeRequests(String spaceId, int spaceToolId) {
		return new MergeRequestService(assemblaClient, spaceId, spaceToolId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#mergeRequestVersions(java.lang.String, int)
	 */
	@Override
	public MergeRequestVersionResource mergeRequestVersions(String spaceId, int spaceToolId) {
		return new MergeRequestVersionService(assemblaClient, spaceId, spaceToolId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#wikis(java.lang.String)
	 */
	@Override
	public WikiPageResource wikis(String spaceId) {
		return new WikiService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#webhooks(java.lang.String)
	 */
	@Override
	public WebhookResource webhooks(String spaceId) {
		return new WebhookService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#tasks()
	 */
	@Override
	public TaskResource tasks() {
		return new TaskService(assemblaClient);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#userKeys()
	 */
	@Override
	public SSHKeyResource userKeys() {
		return new SSHKeyService(assemblaClient);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#sshActions(java.lang.String)
	 */
	@Override
	public SSHActionResource sshActions(String spaceId) {
		return new SSHActionService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#sshActionLaunches(java.lang.String)
	 */
	@Override
	public SSHActionLaunchResource sshActionLaunches(String spaceId) {
		return new SSHActionLaunchService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#sshServers(java.lang.String)
	 */
	@Override
	public SSHServerResource sshServers(String spaceId) {
		return new SSHServerService(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.client.AssemblaResource#spaceKeys(java.lang.String)
	 */
	@Override
	public SpaceSSHKeyResource spaceKeys(String spaceId) {
		return new SpaceSSHKeyService(assemblaClient, spaceId);
	}

}
