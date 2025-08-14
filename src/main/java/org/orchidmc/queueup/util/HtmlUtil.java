package org.orchidmc.queueup.util;

public class HtmlUtil {
    public static String unescapeHtml(String input) {
        return input.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");
    }
}
