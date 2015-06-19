package ch.derlin.ivibrate.gcm;

/**
 * Created by lucy on 17/06/15.
 */
public class GcmConstants{
    public static final String PROJECT_ID = "621732101176";

    public static final String PACKAGE = "ch.derlin.gcm";
    public static final String TO_KEY = "to";
    public static final String FROM_KEY = "from_account"; // from is a reserved keyword
    public static final String ACCOUNTS_KEY = "accounts";
    public static final String MESSAGE_KEY = "message";

    public static final String ACTION_KEY = "action";

    public static final String ACTION_REGISTER = PACKAGE + ".REGISTER";
    public static final String ACTION_UNREGISTER = PACKAGE + ".UNREGISTER";
    public static final String ACTION_ECHO = PACKAGE + ".ECHO";
    public static final String ACTION_MESSAGE = PACKAGE + ".MESSAGE";
    public static final String ACTION_GET_ACCOUNTS = PACKAGE + ".GET_ACCOUNTS";

    public static final String MESG_TYPE_KEY = "message_type";
    public static final String MESG_TYPE_ACK = "ack";
    public static final String MESG_TYPE_NACK = "nack";

    public static final String EXTRA_EVT_TYPE = "event_type";
}//end class