package xhyrom.nexusblock.utils;

import java.util.HashMap;

public class Loader {

    private static HashMap<String, Object> read(final HashMap nexusObject) {
        HashMap<String, Object> map = new HashMap<>();

        for (Object key : nexusObject.keySet()) {
            Object value = nexusObject.get(key.toString());
            if (value instanceof HashMap) {
                map.put(key.toString(), value);

                continue;
            }

            map.put(key.toString(), value.toString());
        }

        return map;
    }
}
