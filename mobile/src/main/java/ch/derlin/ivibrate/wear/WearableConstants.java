package ch.derlin.ivibrate.wear;

/**
 * Constants used to communicate with the wearable.
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

    public static final String SEND_TO_WEARABLE_SERVICE_INTENT_FILTER = "SendToWearableService";

    public static final String EXTRA_EVT_TYPE = "evt_type";

    public static final String FAIL_EVT_TYPE = "fail";
    public static final String SUCCESS_EVT_TYPE = "success";
    public static final String EXTRA_STRING = "msg";
}
