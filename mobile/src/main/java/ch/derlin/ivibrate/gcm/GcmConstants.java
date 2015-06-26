package ch.derlin.ivibrate.gcm;

/**
 * Constants used to communicate with the GCM server.
 *
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class GcmConstants{

    /**GCM project ID for the Google API **/
    public static final String PROJECT_ID = "621732101176";
    /** Global intent filter for local broadcasts **/
    public static final String GCM_SERVICE_INTENT_FILTER = "GCM_SERVICE";
    /** prefix for the event types **/
    public static final String PACKAGE = "ch.derlin.gcm";
    /** extra present on the intent to launch the main activity from a click on notification  **/
    public static final String NOTIFICATION_KEY = "NOTIFICATION";

    /** Keys to the different extras in a GCM message **/
    public static final String TO_KEY = "to";
    public static final String FROM_KEY = "from_account"; // from is a reserved keyword
    public static final String REGID_KEY = "regid"; // present in all message
    public static final String ACCOUNTS_KEY = "accounts"; // the list of accounts/phones registered
    public static final String MESSAGE_KEY = "message";  // the text message
    public static final String PATTERN_KEY = "pattern";  // the pattern message
    public static final String MESSAGE_ID_KEY = "my_message_id"; // to associate the message and its ack
    public static final String ERROR_KEY = "error";

    public static final String ACTION_KEY = "action"; // key to the "action" field (messsage, ack, etc)

    /** the different kind of action/message **/
    public static final String ACTION_REGISTER = PACKAGE + ".REGISTER";
    public static final String ACTION_UNREGISTER = PACKAGE + ".UNREGISTER";
    public static final String ACTION_ECHO = PACKAGE + ".ECHO";
    public static final String ACTION_MESSAGE_RECEIVED = PACKAGE + ".MESSAGE";
    public static final String ACTION_MESSAGE_SENT = "MESSAGE_SENT";
    public static final String ACTION_GET_ACCOUNTS = PACKAGE + ".GET_ACCOUNTS";
    public static final String ACTION_ACK = PACKAGE + "_ack";
    public static final String ACTION_NACK = PACKAGE + "_nack";

    /** the same as the action key (mainly used on server side **/
    public static final String MESG_TYPE_KEY = "message_type";

    /** Refers to the action in the GCMCallback and localbroadcast **/
    public static final String EXTRA_EVT_TYPE = "event_type";
}//end class