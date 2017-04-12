
package com.dilapp.radar.ble;

import java.util.HashMap;

/**
 * @author hj
 * @hide
 */

public class KraitGattAttribute {
	
    public static String KRAIT_DEVICE           = "0000d001-0000-1000-8000-00805f9b34fb";
    public static String KRAIT_DEVICE_NOTIFY_1  = "0000d101-0000-1000-8000-00805f9b34fb";
    public static String KRAIT_DEVICE_NOTIFY_2  = "0000d102-0000-1000-8000-00805f9b34fb";
    public static String KRAIT_DEVICE_NOTIFY_3  = "0000d103-0000-1000-8000-00805f9b34fb";
    public static String KRAIT_DEVICE_NOTIFY_4  = "0000d104-0000-1000-8000-00805f9b34fb";
    public static String KRAIT_DEVICE_NOTIFY_5  = "0000d105-0000-1000-8000-00805f9b34fb";
    public static String KRAIT_DEVICE_ALL       = "0000d120-0000-1000-8000-00805f9b34fb";
    
    private static HashMap<String, String> attributes = new HashMap();
    
    static {
        attributes.put(KRAIT_DEVICE_ALL, "KRAIT_DEVICE_ALL");
        attributes.put(KRAIT_DEVICE_NOTIFY_1, "KRAIT_DEVICE_NOTIFY_1");
        attributes.put(KRAIT_DEVICE_NOTIFY_2, "KRAIT_DEVICE_NOTIFY_1");
        attributes.put(KRAIT_DEVICE_NOTIFY_3, "KRAIT_DEVICE_NOTIFY_1");
        attributes.put(KRAIT_DEVICE_NOTIFY_4, "KRAIT_DEVICE_NOTIFY_1");
        attributes.put(KRAIT_DEVICE_NOTIFY_5, "KRAIT_DEVICE_NOTIFY_1");
    }
    
    public static String lookup(String uuid) {
        String name = attributes.get(uuid);
        return name;
    }
}
