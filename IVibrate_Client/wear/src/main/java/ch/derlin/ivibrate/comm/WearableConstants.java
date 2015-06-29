package ch.derlin.ivibrate.comm;

 /**
  * Constants used to communicate with the handheld device.
  * -------------------------------------------------  <br />
  * context      Advanced Interface - IVibrate project <br />
  * date         June 2015                             <br />
  * -------------------------------------------------  <br />
  *
  * @author Lucy Linder
  */
public class WearableConstants{
    public static final String PHONE_TO_WEARABLE_DATA_PATH = "/derlin/ivibrate/to/wearable";
    public static final String WEARABLE_TO_PHONE_DATA_PATH = "/derlin/ivibrate/from/wearable";

     public static final String ACTION_KEY = "action";
     public static final String ACTION_FEEDBACK = "feedback";
     public static final String ACTION_SEND_MSG = "send";
     public static final String ACTION_OPEN = "open";
     public static final String ACTION_GET_CONTACTS = "getContacts";
     public static final String ACTION_PLAY_PATTERN = "pattern";

     public static final String EXTRA_PHONE = "phone";
     public static final String EXTRA_PATTERN = "pattern";
     public static final String EXTRA_TEXT = "text";
     public static final String EXTRA_STATUS = "result";
     public static final String EXTRA_CONTACTS_LIST = "contacts";
     public static final String EXTRA_CONTACT_NAME = "contact_name";
 }
