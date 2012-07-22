package net.holmes.core.util;

public class StringUtils {

    public static String escapeUpnpId(String id) {
        return id.replaceAll("&", "__AMP__");
    }

    public static String unescapeUpnpId(String id) {
        return id.replaceAll("__AMP__", "&");
    }

}
