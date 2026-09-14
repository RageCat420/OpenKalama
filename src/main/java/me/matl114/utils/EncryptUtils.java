package me.matl114.utils;

import it.unimi.dsi.fastutil.chars.Char2CharArrayMap;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import it.unimi.dsi.fastutil.chars.Char2CharMaps;
import it.unimi.dsi.fastutil.chars.CharIterator;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class EncryptUtils {
    public static final Decoder e = Base64.getDecoder();
    public static final SecureRandom a = new SecureRandom();
    public static final Encoder d = Base64.getEncoder();
    protected static final Char2CharMap b = createBase64RShifts();
    protected static final Char2CharMap c = createBase64RShiftsReverse();

    public static byte[] c(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] n(String key) throws InvalidKeyException {
        try {
            return e.decode(c(key));
        } catch (Exception var2) {
            throw new InvalidKeyException(var2);
        }
    }

    public static String e(String string) {
        return d(c(string));
    }

    private static Char2CharMap createBase64RShiftsReverse() {
        Char2CharMap var0 = createBase64RShifts();
        Char2CharArrayMap var1 = new Char2CharArrayMap(64);
        CharIterator var2 = var0.keySet().iterator();

        while (var2.hasNext()) {
            char var3 = (Character) var2.next();
            var1.put(var0.get(var3), var3);
        }

        return Char2CharMaps.unmodifiable(var1);
    }

    public static String i(byte[] bytes) {
        return f(d(d.encode(bytes)));
    }

    public static String k(String string) {
        return d(l(string));
    }

    public static byte[] l(String string) {
        return e.decode(c(shiftBase64R(string)));
    }

    public static String m(byte[] key) {
        return d(d.encode(key));
    }

    public static String d(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static Char2CharMap createBase64RShifts() {
        Char2CharArrayMap var0 = new Char2CharArrayMap(64);
        var0.put('A', '!');
        var0.put('B', '"');
        var0.put('C', '#');
        var0.put('D', '$');
        var0.put('E', '%');
        var0.put('F', '¼');
        var0.put('G', '\'');
        var0.put('H', '(');
        var0.put('I', ')');
        var0.put('J', ',');
        var0.put('K', '-');
        var0.put('L', '.');
        var0.put('M', ':');
        var0.put('N', ';');
        var0.put('O', '<');
        var0.put('P', '=');
        var0.put('Q', '>');
        var0.put('R', '?');
        var0.put('S', '@');
        var0.put('T', '[');
        var0.put('U', '\\');
        var0.put('V', ']');
        var0.put('W', '^');
        var0.put('X', '_');
        var0.put('Y', '`');
        var0.put('Z', '{');
        var0.put('a', '|');
        var0.put('b', '}');
        var0.put('c', '~');
        var0.put('d', '¡');
        var0.put('e', '¢');
        var0.put('f', '£');
        var0.put('g', '¤');
        var0.put('h', '¥');
        var0.put('i', '¦');
        var0.put('j', '¨');
        var0.put('k', '©');
        var0.put('l', 'ª');
        var0.put('m', '«');
        var0.put('n', '¬');
        var0.put('o', '®');
        var0.put('p', '¯');
        var0.put('q', '°');
        var0.put('r', '±');
        var0.put('s', '²');
        var0.put('t', '³');
        var0.put('u', 'µ');
        var0.put('v', '¶');
        var0.put('w', '·');
        var0.put('x', '×');
        var0.put('y', '¹');
        var0.put('z', 'º');
        var0.put('0', '0');
        var0.put('1', '1');
        var0.put('2', '2');
        var0.put('3', '3');
        var0.put('4', '4');
        var0.put('5', '5');
        var0.put('6', '6');
        var0.put('7', '7');
        var0.put('8', '8');
        var0.put('9', '9');
        var0.put('+', '+');
        var0.put('/', '»');
        var0.put('=', '¿');
        return Char2CharMaps.unmodifiable(var0);
    }

    public static byte[] j(String string) {
        return c(i(c(string)));
    }

    public static String h(String string) {
        return i(c(string));
    }

    public static String shiftBase64R(String string) {
        char[] var1 = e(string).toCharArray();

        for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = c.get(var1[var2]);
        }

        return new String(var1);
    }

    public static String f(String string) {
        char[] var1 = e(string).toCharArray();

        for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = b.get(var1[var2]);
        }

        return new String(var1);
    }
}
