package com.danilomekic.http.server.util;

import com.danilomekic.http.server.model.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private static final Set<Character> DELIMITERS =
            Set.of('(', ')', '<', '>', '[', ']', '{', '}', '"', '=', '/', '\\', ':', ';', '?', '@');

    public static boolean isCharacterUSASCII(int codePoint) {
        return Character.UnicodeBlock.of(codePoint) == Character.UnicodeBlock.BASIC_LATIN;
    }

    /*
     * Tokens are short textual identifiers that do not include whitespace or delimiters.
     * token          = 1*tchar
     *
     * tchar          = "!" / "#" / "$" / "%" / "&" / "'" / "*"
     *                  / "+" / "-" / "." / "^" / "_" / "`" / "|" / "~"
     *                  / DIGIT / ALPHA
     *                  ; any VCHAR, except delimiters
     */
    public static boolean isValidToken(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return false;
        }

        return fieldName
                .chars()
                .allMatch(c -> c >= 0x21 && c <= 0x7E && !DELIMITERS.contains((char) c));
    }

    public static boolean isValidMethod(String methodName) {
        return Arrays.stream(Method.values()).map(Method::name).anyMatch(methodName::equals);
    }

    public static List<String> getFieldValuesList(String fieldValue) {
        return Arrays.stream(fieldValue.split(","))
                .filter(s -> !s.isBlank())
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
