package util;

import java.io.UnsupportedEncodingException;

/**
 * @author: dahua
 * @date: 2020/6/3
 * @description: lianxi jilu
 */
public class HuaDecode {

    private static final char[] chars = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_', '!',
            '@', '#', '$', '%', '^', '&', '*'
    };

    public static String decode(final String str) throws HuaDecodeException {
        byte[] bytes = new byte[0];
        try {
            bytes = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuffer sb = new StringBuffer();
        for (int b = 0; b < bytes.length; b++) {
            if (bytes[b] > 0) {
                sb.append(chars[bytes[b] * (b + 1) / chars.length]);
                sb.append(chars[bytes[b] * (b + 1) % chars.length]);
            } else {
                throw new HuaDecodeException("param can not be chinese!");
            }
        }
        return new String(sb);
    }

    public static String encode(final String str) throws HuaDecodeException {
        byte[] bytes = new byte[str.length()];
        for (int a = 0; a < str.toCharArray().length; a++) {
            bytes[a] = (byte) getIndex(chars, str.toCharArray()[a]);
        }
        byte[] result = new byte[str.length() / 2];
        for (int a = 0; a < str.toCharArray().length; a += 2) {
            if (a % 2 == 0) {
                result[a / 2] = (byte) ((getIndex(chars, str.toCharArray()[a]) * chars.length + getIndex(chars, str.toCharArray()[a + 1])) / (a / 2 + 1));
            }
        }
        try {
            return new String(result, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        throw new HuaDecodeException();
    }

    private static int getIndex(char[] arr, char value) throws HuaDecodeException {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                return i;
            }
        }
        throw new HuaDecodeException("error param!");
    }

    static public class HuaDecodeException extends UnsupportedEncodingException {

        public HuaDecodeException() {
            super();
        }

        public HuaDecodeException(String s) {
            super(s);
        }
    }
}
