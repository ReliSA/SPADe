package cz.zcu.kiv.spade.pumps.issuetracking.redmine;

import javax.swing.text.html.HTML;
import java.text.SimpleDateFormat;

class RedmineXmlConstants {

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm");

    static final String FIELDS_FILE = "fields.properties";
    static final String CSN = "UTF8";

    static final String PROJECTS_RELATIVE_URL = "/projects";

    static final String A_ELEMENT = HTML.Tag.A.toString();
    static final String DEL_ELEMENT = "del";
    static final String EM_ELEMENT = HTML.Tag.EM.toString();
    static final String H4_ELEMENT = HTML.Tag.H4.toString();
    static final String I_ELEMENT = HTML.Tag.I.toString();
    static final String LI_ELEMENT = HTML.Tag.LI.toString();
    static final String P_ELEMENT = HTML.Tag.P.toString();
    static final String STRONG_ELEMENT = HTML.Tag.STRONG.toString();
    static final String TBODY_ELEMENT = "tbody";
    static final String TR_ELEMENT = HTML.Tag.TR.toString();

    static final String CLASS_ATTR = HTML.Attribute.CLASS.toString();
    static final String HREF_ATTR = HTML.Attribute.HREF.toString();
    static final String ID_ATTR = HTML.Attribute.ID.toString();
    static final String TITLE_ATTR = HTML.Attribute.TITLE.toString();

    static final String DOWNLOAD_PREFIX = "/download/";
    static final String CHANGE_PREFIX = "change-";
    static final String REVISION_PREFIX = "Revi";
    static final String USERS_PREFIX = "/users/";
    static final String WIKI_PREFIX = "/wiki/";
    static final String WIKI_INDEX_SUFFIX = "/wiki/index";

    static final String ADDED = "přidán";
    static final String DELETED = "smazán";
    static final String SET_TO = "nastaven na";

    static final String ATTACHMENTS = "attachments";
    static final String AUTHOR = "author";
    static final String COMMENTS = "comments";
    static final String DETAILS = "details";
    static final String DIFF_IN = "diff_in";
    static final String DIFF_OUT = "diff_out";
    static final String PAGES_HIERARCHY = "pages-hierarchy";
    static final String SIZE = "size";
    static final String TEXT_DIFF = "text-diff";
    static final String UPDATED_ON = "updated_on";
    static final String WIKI = "wiki";
    static final String WIKI_PAGE = "wiki-page";

    static final String ATTRIBUTE = "attribute";

    static final String LEFT_BRACKET = "[";
    static final String RIGHT_BRACKET = "]";
    static final String HASH = "#";
    static final String UNDERSCORE = "_";
    static final String WIKI_MARK = "¶";

    static final String DISPLAY_NAME_LABEL = "Display name: ";

    static final String RELATED = "related";

}
