package ch.derlin.ivibrate;

/**
 * @author: Lucy Linder
 * @date: 17.06.2015
 */
public class GcmConstants{
    public static final String PROJECT_ID = "621732101176";

    public static final String PACKAGE = "ch.derlin.ivibrate";
    public static final String MESSAGE_ID_KEY = "message_id";
    public static final String TO_KEY = "to";
    public static final String FROM_KEY = "from_account"; // from is a reserved keyword
    public static final String REGID_KEY = "regid";
    public static final String PHONE_KEY = "phone";
    public static final String CATEGORY_KEY = "category";
    public static final String ACCOUNTS_KEY = "accounts";
    public static final String MESSAGE_KEY = "message";
    public static final String ERROR_KEY = "error";

    public static final String PAYLOAD_KEY = "data";
    public static final String ACTION_KEY = "action";

    public static final String ACTION_REGISTER = PACKAGE + ".REGISTER";
    public static final String ACTION_UNREGISTER = PACKAGE + ".UNREGISTER";
    public static final String ACTION_ECHO = PACKAGE + ".ECHO";
    public static final String ACTION_MESSAGE = PACKAGE + ".MESSAGE";
    public static final String ACTION_GET_ACCOUNTS = PACKAGE + ".GET_ACCOUNTS";
    public static final String ACTION_ACK = PACKAGE + "_ack";
    public static final String ACTION_NACK = PACKAGE + "_nack";

    public static final String MESG_TYPE_KEY = "message_type";
}//end class
