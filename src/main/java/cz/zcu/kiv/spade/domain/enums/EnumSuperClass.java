package cz.zcu.kiv.spade.domain.enums;

public enum EnumSuperClass {

    /* resolution */
    FINISHED,
    UNFINISHED,

    /* severity */
    MINOR,
    NORMAL,
    MAJOR,

    /* priority */
    LOW,
    //NORMAL,   already in severity
    HIGH,

    /* status */
    OPEN,
    CLOSED
}
