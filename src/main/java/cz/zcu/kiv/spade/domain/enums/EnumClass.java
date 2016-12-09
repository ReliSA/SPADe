package cz.zcu.kiv.spade.domain.enums;

public enum EnumClass {

    /* type */
    TASK,
    BUG,
    ENHANCEMENT,
    FEATURE,

    /* resolution */
    INVALID,
    DUPLICATE,
    WONT_FIX,
    FIXED,
    WORKS_AS_DESIGNED,
    WORKS_FOR_ME,
    INCOMPLETE,

    /* severity */
    TRIVIAL,
    MINOR,
    NORMAL,
    MAJOR,
    CRITICAL,

    /* priority */
    LOWEST,
    LOW,
    //NORMAL,       already in severity
    HIGH,
    HIGHEST,

    /* status */
    NEW,
    ACCEPTED,
    IN_PROGRESS,
    RESOLVED,
    VERIFIED,
    DONE,
    //INVALID       already in resolution

}
