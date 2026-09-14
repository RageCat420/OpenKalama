package me.matl114.utils;

import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Method;
import org.objectweb.asm.Type;

public class ByteCodeUtils {
    public static String f(Method method) {
        StringBuilder var1 = new StringBuilder();
        var1.append(method.getName());
        var1.append("(");

        for (Class var5 : method.getParameterTypes()) {
            var1.append(a(var5));
        }

        var1.append(")");
        var1.append(a(method.getReturnType()));
        return var1.toString();
    }

    public static String getMethodDescriptor(String name, Class[] arguments, Class returnType) {
        StringBuilder var3 = new StringBuilder();
        var3.append(name);
        var3.append("(");

        for (Class var7 : arguments) {
            var3.append(a(var7));
        }

        var3.append(")");
        var3.append(a(returnType));
        return var3.toString();
    }

    public static String getFieldDescriptor(String fieldName, Class<?> fieldType) {
        return fieldName + a(fieldType);
    }

    public static String c(String jvm) {
        if (jvm.charAt(0) == '[') {
            return jvm.replace('/', '.');
        } else {
            return switch (jvm) {
                case "V" -> "void";
                case "I" -> "int";
                case "Z" -> "boolean";
                case "B" -> "byte";
                case "C" -> "char";
                case "S" -> "short";
                case "D" -> "double";
                case "F" -> "float";
                case "J" -> "long";
                default -> jvm.substring(1, jvm.length() - 1).replace('/', '.');
            };
        }
    }

    public static String i(String descriptor) {
        if (descriptor.charAt(descriptor.length() - 1) == ';') {
            int var3 = descriptor.indexOf(91);
            if (var3 > 0) {
                return descriptor.substring(0, var3);
            } else {
                String var2 = descriptor.substring(0, descriptor.indexOf(47));
                return var2.substring(0, var2.lastIndexOf(76));
            }
        } else {
            int var1 = descriptor.indexOf(91);
            return var1 > 0 ? descriptor.substring(0, var1) : descriptor.substring(0, descriptor.length() - 1);
        }
    }

    public static String b(String clazzName) {
        if (clazzName.charAt(0) == '[') {
            return clazzName.replace('.', '/');
        } else {
            return switch (clazzName) {
                case "void" -> "V";
                case "int" -> "I";
                case "boolean" -> "Z";
                case "byte" -> "B";
                case "char" -> "C";
                case "short" -> "S";
                case "double" -> "D";
                case "float" -> "F";
                case "long" -> "J";
                default -> "L" + clazzName.replace(".", "/") + ";";
            };
        }
    }

    public static String h(String descriptor) {
        return descriptor.substring(0, descriptor.indexOf(40));
    }

    public static Pair<String, String> getComponentType(Class<?> clazz) {
        if (!clazz.isArray()) {
            return Pair.of("", clazz.getName());
        } else {
            StringBuilder var1;
            for (var1 = new StringBuilder(); clazz.isArray(); clazz = clazz.getComponentType()) {
                var1.append('[');
            }

            return Pair.of(var1.toString(), clazz.getName());
        }
    }

    public static String a(Class<?> clazz) {
        return Type.getDescriptor(clazz);
    }

    public static String getPrimitiveType(char descriptor) {
        return switch (descriptor) {
            case 'B' -> "byte";
            case 'C' -> "char";
            case 'D' -> "double";
            default -> null;
            case 'F' -> "float";
            case 'I' -> "int";
            case 'J' -> "long";
            case 'S' -> "short";
            case 'V' -> "void";
            case 'Z' -> "boolean";
        };
    }
}
