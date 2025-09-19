package com.danilomekic.http.util;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * RFC 9110
 * HTTP represents data via fields or key-value pairs with a registered key namespace
 *
 * field-name = token
 * Field names are case-insensitive and are registered in "Hypertext Transfer Protocol (HTTP) Field Name Registry"
 *
 * field-value = *field-content
 * field-content = field-vchar [ 1*( SP / HTABA / field-vchar ) field-vchar ]
 * field-vchar = VCHAR / obs-text
 * obs-text = %x80-FF
*/
public class HttpMessageUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpMessageUtil.class);

    private static final Set<Character> DELIMITERS = Set.of(
        '(', ')', '<', '>', '[', ']', '{', '}',
        '"', '=', '/', '\\', ':', ';', '?', '@');

    public static boolean isCharacterUSASCII(int codePoint) {
        return Character.UnicodeBlock.of(codePoint) == Character.UnicodeBlock.BASIC_LATIN;
    }

    public static boolean isValidToken(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return false;
        }

        return fieldName
            .chars()
            .allMatch(c -> c >= 0x21 && c <= 0x7E && !DELIMITERS.contains((char) c));
    }
}
