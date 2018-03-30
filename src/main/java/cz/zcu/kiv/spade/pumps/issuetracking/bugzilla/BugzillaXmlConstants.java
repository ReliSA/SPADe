package cz.zcu.kiv.spade.pumps.issuetracking.bugzilla;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class BugzillaXmlConstants {

    public static final String BUG_URL_FORMAT = "https://%s//show_bug.cgi?id=";

    static final String FOLDER_NAME_FORMAT = "input/xml/%s";
    static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss Z", Locale.ENGLISH);
    static final DateFormat DEADLINE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    static final DateFormat HTML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    static final String ACTIVITY_URL_FORMAT = "https://%s//show_activity.cgi?id=%s";
    static final String CHANGELOG_FILE_FORMAT = "input/xml/%s/changelogs/%s.html";


    static final String BUG = "bug";
    static final String BUG_ID = "bug_id";
    static final String CREATION_TS = "creation_ts";
    static final String SHORT_DESC = "short_desc";
    static final String CLASSIFICATION_ID = "classification_id";
    static final String CLASSIFICATION = "classification";
    static final String COMPONENT = "component";
    static final String VERSION = "version";
    static final String UNSPECIFIED = "unspecified";
    static final String BUG_STATUS = "bug_status";
    static final String RESOLUTION = "resolution";
    static final String DUP_ID = "dup_id";
    static final String KEYWORDS = "keywords";
    static final String PRIORITY = "priority";
    static final String BUG_SEVERITY = "bug_severity";
    static final String REPORTER = "reporter";
    static final String NAME = "name";
    static final String UNASSIGNED = "unassigned";
    static final String ASSIGNED_TO = "assigned_to";
    static final String ESTIMATED_TIME = "estimated_time";
    static final String ACTUAL_TIME = "actual_time";
    static final String DEADLINE = "deadline";
    static final String CF_FIXED_BY_COMMITS = "cf_fixed_by_commits";
    static final String LONG_DESC = "long_desc";
    static final String THETEXT = "thetext";
    static final String BUG_WHEN = "bug_when";
    static final String COMMENT_ID = "comment_id";
    static final String WHO = "who";
    static final String DEPENDSON = "dependson";
    static final String BLOCKED = "blocked";
    static final String ATTACHMENT = "attachment";
    static final String ATTACHID = "attachid";
    static final String DATE = "date";
    static final String DESC = "desc";
    static final String FILENAME = "filename";
    static final String TYPE = "type";
    static final String SIZE = "size";
    static final String ATTACHER = "attacher";
    static final String SEE_ALSO = "see_also";
    static final String DELTA_TS = "delta_ts";
    static final String ASSIGNEE = "Assignee";
    static final String TR = "tr";
    static final String LEFT_BRACKET = "[";
    static final String RIGHT_BRACKET = "]";
    static final String HOURS_WORKED = "Hours Worked";

    static final String UNUSED_PRIORITY_MARKER = "P";
    static final String HASH = "#";
}
