package com.assembla.client;

public interface AssemblaConstants {

	/**
	 * API URI
	 */
	String HOST_API = "api.assembla.com";
	/**
	 * API URL
	 */
	String PROTOCOL = "https";
	/**
	 * API Version
	 */
	String VERSON_PREFIX = "/v1";
	/*
	 * HTTP Header for API Key
	 */
	String HEADER_API_KEY = "X-Api-Key";
	/*
	 * HTTP Header for API Secret Key
	 */
	String HEADER_API_SECRET = "X-Api-Secret";
	/*
	 * HTTP Header for User-Agent
	 */
	String HEADER_USER_AGENT = "User-Agent";
	/*
	 * User agent name
	 */
	String USER_AGENT = "AssemblaJavaApi/V1";
	/**
	 * Http Status for NO CONTENT response
	 */
	int NO_CONTENT = 204;
	/**
	 * API URL
	 */
	String URL = PROTOCOL + "://" + HOST_API + VERSON_PREFIX;
	/**
	 * API URL with a subdomain
	 */
	String URL_WITH_SUBDOMAIN = PROTOCOL + "://%s." + HOST_API + VERSON_PREFIX;
	/**
	 * Request parameter determining page number when making a request which
	 * returns paginated response
	 */
	String PAGE_PARAMETER = "page";
	/**
	 * Request parameter determining page size when making a request which
	 * returns paginated response
	 */
	String PAGE_SIZE_PARAMETER = "per_page";
	/*
	 * Request parameter for specifying location of uploaded document
	 */
	String UPLOAD_LOCATION = "folder_name";
	/*
	 * Format of file name for posting multipart form data
	 */
	String UPLOAD_NAME_FORMAT="%s[file]";
	/**
	 * Default page number when making request which returns paginated results
	 */
	int DEFAULT_PAGE = 1;
	/**
	 * Default page size when making request which returns paginated results
	 */
	int DEFAULT_PAGE_SIZE = 75;
	/**
	 * Media Type for JSON
	 */
	String JSON_MEDIA_TYPE = "application/json; charset=utf-8";
	/**
	 * URI for retrieving a ticket identified by an ID.
	 */
	String TICKET_BY_ID = "/spaces/%s/tickets/id/%s.json";
	/**
	 * URI for retrieving and modifying a a ticket identified by a ticket
	 * number.
	 */
	String TICKET_BY_NUMBER = "/spaces/%s/tickets/%d.json";
	/**
	 * URI for retreiving tickets by milestone
	 */
	String TICKETS_BY_NO_MILESTONE = "/spaces/%s/tickets/no_milestone.json";
	/**
	 * URI for retreiving full ticket list
	 */
	String TICKETS_BY_SPACE = "/spaces/%s/tickets.json";
	/**
	 * URI for retrieving a list of tickets user is following.
	 */
	String TICKETS_FOLLOWED = "/spaces/%s/tickets/my_followed.json";
	/**
	 * URI for retreiving a list of tickets assigned to current user.
	 */
	String TICKETS_BY_STATUS = "/spaces/%s/tickets/my_active.json";
	/**
	 * URI for retreiving tags associated with ticket
	 */
	String TAGS_FOR_TICKET = "/spaces/%s/tickets/%d/tags.json";
	/**
	 * URI for retreiving attachments associated with a ticket
	 */
	String ATTACHMENTS_FOR_TICKET = "/spaces/%s/tickets/%d/attachments.json";
	/**
	 * URI for retreiving a list of user and team reports available for the
	 * space.
	 */
	String TICKET_CUSTOM_REPORTS = "/spaces/%s/tickets/custom_reports.json";
	/**
	 * URI for deleting a ticket by number
	 */
	String TICKET_DELETE = "/spaces/%s/tickets/%d.json";
	/**
	 * URI for retrieving currently authenticated user.
	 */
	String USER_CURRENT = "/user.json";
	/**
	 * URI for retrieving a user by id or login
	 */
	String USER_ID_OR_LOGIN = "/users/%s.json";
	/**
	 * URI for retreiving users for a space
	 */
	String USERS_FOR_SPACE = "/spaces/%s/users.json";
	/**
	 * URI for user mentions
	 */
	String MENTIONS = "/user/mentions.json";
	/**
	 * URI for extensions by id
	 */
	String MENTION_BY_ID = "/user/mentions/%d.json";
	/**
	 * URI for marking a mention as read
	 */
	String MENTION_MARK_AS_READ = "/user/mention/%d/mark_as_read.json";
	/**
	 * URI for accessing and modifying ticket comments for a ticket
	 */
	String TICKET_COMMENTS_FOR_TICKET = "/spaces/%s/tickets/%d/ticket_comments.json";
	/**
	 * URI for getting a ticket's comment by id
	 */
	String TICKET_COMMENT = "/spaces/%s/tickets/%d/ticket_comments/%d.json";
	/**
	 * URI for returning list of spaces which current user is participating in.
	 */
	String SPACES = "/spaces.json";
	/**
	 * URI for returning a space by id or wiki name
	 */
	String SPACE_BY_ID = "/spaces/%s.json";
	/**
	 * URI for copying a space from a given template
	 */
	String SPACE_COPY = "/spaces/%s/copy.json";
	/**
	 * URI for retrieving a list of all tags.
	 */
	String TAGS = "/spaces/%s/tags.json";
	/**
	 * URI for retrieving a list of active tags
	 */
	String ACTIVE_TAGS = "/spaces/%s/tags/active.json";
	/**
	 * URI for retrieving a list of proposed tags
	 */
	String PROPOSED_TAGS = "/spaces/%s/tags/proposed.json";
	/**
	 * URI for retrieving a list of hidden tags
	 */
	String HIDDEN_TAGS = "/spaces/%s/tags/hidden.json";
	/**
	 * URI for retrieving a list of tickets associated with the tag.
	 */
	String TICKETS_BY_TAG = "/spaces/%s/tags/%d/tickets.json";
	/**
	 * URI for retrieving a tag by id
	 */
	String TAG_BY_ID = "/spaces/%s/tags/%d.json";
	/**
	 * URI for retrieving all upcoming milestones for a space
	 */
	String MILESTONE_UPCOMING = "/spaces/%s/milestones/upcoming.json";
	/**
	 * URI for retrieving all milestones for a space
	 */
	String MILESTONE_ALL = "/spaces/%s/milestones/all.json";
	/**
	 * URI for retrieving all complete milestones for a space
	 */
	String MILESTONE_COMPLETE = "/spaces/%s/milestones/completed.json";
	/**
	 * URI for retrieving all released milestones for a space
	 */
	String MILESTONE_RELEASE = "/spaces/%s/milestones/release_notes.json";
	/**
	 * URI for retrieving milestone by id
	 */
	String MILESTONE_BY_ID = "/spaces/%s/milestones/%s.json";
	/**
	 * URI for creating a milestone
	 */
	String MILESTONE = "/spaces/%s/milestones.json";
	/**
	 * URI for retrieving custom fields for a space
	 */
	String CUSTOM_FIELDS = "/spaces/%s/tickets/custom_fields.json";
	/**
	 * URI for retrieving custom fields by id
	 */
	String CUSTOM_FIELDS_BY_ID = "/spaces/%s/tickets/custom_fields/%d.json";
	/**
	 * URI for retrieving ticket associations by ticket number
	 */
	String TICKET_ASSOCIATIONS = "/spaces/%s/tickets/%d/ticket_associations.json";
	/*
	 * URI for retrieving ticket association for a ticket by id
	 */
	String TICKET_ASSOCIATIONS_BY_ID = "/spaces/%s/tickets/%d/ticket_associations/%d.json";
	/*
	 * URI for retrieving all user roles for a space
	 */
	String USER_ROLES_ALL = "/spaces/%s/user_roles.json";
	/*
	 * URI for retrieving user role by id
	 */
	String USER_ROLES_ID = "/spaces/%s/user_roles/%d.json";
	/*
	 * URI for retrieving ticket statuses for a space
	 */
	String TICKET_STATUS_ALL = "/spaces/%s/tickets/statuses.json";
	/*
	 * URI for retrieving ticket status by id
	 */
	String TICKET_STATUS_ID = "/spaces/%s/tickets/statuses/%d.json";
	/*
	 * URI for retrieving space tools
	 */
	String SPACE_TOOLS = "/spaces/%s/space_tools.json";
	/*
	 * URI for retrieving space tools which are repositories
	 */
	String SPACE_TOOLS_REPOS = "/spaces/%s/space_tools/repo.json";
	/*
	 * URI for retrieving space tool by id
	 */
	String SPACE_TOOLS_ID = "/spaces/%s/space_tools/%s.json";
	/*
	 * URI for adding a new space tool to a space
	 */
	String SPACE_TOOLS_ADD = "/spaces/%s/space_tools/%d/add.json";
	/**
	 * URI for retrieving user activity feed
	 */
	String ACTIVITY = "/activity.json";
	/**
	 * yyy-MM-dd HH:mm date format
	 */
	String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	/*
	 * Parameter used to specify from date
	 */
	String DATE_FROM_PARAMETER = "from";
	/*
	 * Parameter used to specify to date
	 */
	String DATE_TO_PARAMETER = "to";
	/*
	 * Parameter used to specify report to use when retrieving tickets
	 */
	String REPORT_PARAMETER = "report";
	/*
	 * Parameter used to specify field to sort on when retrieving tickets
	 */
	String SORT_BY_PARAMETER = "sort_by";
	/*
	 * Parameter used to specify sort direction to use when retrieving tickets
	 */
	String SORT_DIRECTION_PARAMETER = "sort_order";
	/*
	 * URI for retrieving merge requests
	 */
	String MERGE_REQUESTS = "/spaces/%s/space_tools/%d/merge_requests.json";
	/*
	 * URI for retrieving merge request by id
	 */
	String MERGE_REQUEST_BY_ID = "/spaces/%s/space_tools/%s/merge_requests/%d.json";
	/*
	 * URI for retrieving comments for merge request URI for retrieving comments
	 * for merge request
	 */
	String MERGE_REQUEST_COMMENTS = "/spaces/%s/space_tools/%s/merge_requests/%d/comments.json";
	/*
	 * URI for applying and closing merge request by id
	 */
	String MERGE_AND_CLOSE = "/spaces/%s/space_tools/%s/merge_requests/%d/merge_and_close.json";
	/*
	 * URI for ignoring merge request by id
	 */
	String MERGE_REQUEST_IGNORE = "/spaces/%s/space_tools/%s/merge_requests/%d/ignore.json";
	/*
	 * URI for retrieving tickets associated to a merge request
	 */
	String MERGE_REQUEST_TICKETS = "/spaces/%s/space_tools/%s/merge_requests/%d/tickets.json";
	/*
	 * URI for retrieving versions of a merge request
	 */
	String MERGE_REQUEST_VERSIONS = "/spaces/%s/space_tools/%s/merge_requests/%d/versions.json";
	/*
	 * URI for retrieving merge request version
	 */
	String MERGE_REQUEST_VERSION = "/spaces/%s/space_tools/%s/merge_requests/%d/versions/%d.json";
	/*
	 * URI for retrieving merge request version comments
	 */
	String MERGE_REQUEST_VERSION_COMMENTS = "/spaces/%s/space_tools/%s/merge_requests/%d/versions/%d/comments.json";
	/*
	 * URI for retrieving merge request votes
	 */
	String MERGE_REQUEST_VOTES = "/spaces/%s/space_tools/%s/merge_requests/%d/versions/%d/votes.json";
	/*
	 * URI for voting up a merge request
	 */
	String MERGE_REQUEST_VOTE_UP = "/spaces/%s/space_tools/%s/merge_requests/%d/versions/%d/votes/upvote.json";
	/*
	 * URI for voting down a merge request
	 */
	String MERGE_REQUEST_VOTE_DOWN = "/spaces/%s/space_tools/%s/merge_requests/%d/versions/%d/votes/downvote.json";
	/*
	 * URI for voting deleting a merge request
	 */
	String MERGE_REQUEST_VOTE_DELETE = "/spaces/%s/space_tools/%s/merge_requests/%d/versions/%d/votes/delete.json";
	/*
	 * URI for retrieving wiki pages for space
	 */
	String WIKI_PAGES = "/spaces/%s/wiki_pages.json";
	/*
	 * URI for retrieving wiki page by id
	 */
	String WIKI_PAGE = "/spaces/%s/wiki_pages/%s.json";
	/*
	 * URI for wiki page and all sub (child) pages
	 */
	String WIKI_PAGE_ALL = "/spaces/%s/wiki_pages/%s/all.json";
	/*
	 * URI for moving all child wiki pages up one level and delete wiki page
	 */
	String WIKI_CONTAINER = "/spaces/%s/wiki_pages/%s/container.json";
	/*
	 * URI for retrieving versions of a wiki
	 */
	String WIKI_PAGE_VERSIONS = "/spaces/%s/wiki_pages/%s/versions.json";
	/*
	 * URi for retrieving wiki page version by id
	 */
	String WIKI_PAGE_VERSION = "/spaces/%s/wiki_pages/%s/versions/%s.json";
	/*
	 * URI for retrieving all documents
	 */
	String DOCUMENTS_ALL = "/spaces/%s/documents.json";
	/*
	 * URI for retrieving document by id 
	 */
	String DOCUMENT = "/spaces/%s/documents/%s.json";
	/*
	 * URI for retrieving all documents
	 */
	String DOCUMENTS_ALL_NO_EXTENSION = "/spaces/%s/documents";
	/*
	 * URI for retrieving document by id 
	 */
	String DOCUMENT_NO_EXTENSION = "/spaces/%s/documents/%s";
	/*
	 * URI for retrieving all current user's ssh keys
	 */
	String SSH_KEY_ALL = "/user/ssh_keys.json";
	/*
	 * URI for retrieving SSH key by id
	 */
	String SSH_KEY = "/user/ssh_keys/%d.json";
	/*
	 * URI for retrieving ssh actions
	 *
	 */
	String SSH_ACTIONS = "/spaces/%s/ssh/actions.json";
	/*
	 * URI for retrieving ssh action by id
	 */
	String SSH_ACTION = "/spaces/%s/ssh/actions/%d.json";
	/*
	 * URI for running ssh action by id
	 */
	String SSH_ACTION_RUN = "/spaces/%s/ssh/actions/%d/run.json";
	/*
	 * URI for retrieving ssh action launches for a space
	 */
	String SSH_ACTION_LAUNCHES = "/spaces/%s/ssh/launches.json";
	/*
	 * URI for retrieving ssh action launches for action id
	 * 
	 */
	String SSH_ACTION_LAUNCHES_ID = "/spaces/%s/ssh/actions/%d/launches.json";
	/*
	 * URI for retrieving ssh action launch by id
	 */
	String SSH_ACTION_LAUNCH = "/spaces/%s/ssh/launches/%d.json";
	/*
	 * URI for retrieving ssh action launch with full output
	 */
	String SHH_ACTION_LAUNCH_OUTPUT = "/spaces/%s/ssh/launches/%d/output.json";
	/*
	 * URI for retrieving ssh servers for a space
	 */
	String SSH_SERVERS = "/spaces/%s/ssh/servers.json";
	/*
	 * URI for retrieving ssh server by id 
	 */
	String SSH_SERVER = "/spaces/%s/ssh/servers/%s.json";
	/*
	 * URI for retrieving space ssh keys
	 */
	String SPACE_SSH_KEYS = "/spaces/%s/ssh/key.json";
	/*
	 * URI for generating a new space ssh key
	 */
	String SPACE_SSH_KEY_GENERATE = "/spaces/%s/ssh/generate_key.json";
	/*
	 * URI for retrieving tickets for milestone
	 */
	String TICKET_BY_MILESTONE = "/spaces/%s/tickets/milestone/%s.json";
	/*
	 * Request parameter determining what status tickets to fetch
	 */
	String TICKET_STATUS_PARAMETER = "ticket_status";

}
