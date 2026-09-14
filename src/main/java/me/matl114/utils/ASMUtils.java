package me.matl114.utils;

import com.google.common.base.Preconditions;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nullable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class ASMUtils {
   public static void m(MethodVisitor mv, String originType, String targetType) {
      if (!a(originType) && a(targetType)) {
         if (!b(originType)) {
            String var5 = d(targetType);
            m(mv, originType, var5);
            o(mv, targetType);
         } else {
            String var6 = c(originType);
            o(mv, var6);
            q(mv, var6, targetType);
         }
      } else if (a(originType) && !a(targetType)) {
         if (b(targetType)) {
            String var3 = c(targetType);
            q(mv, originType, var3);
            castFromPrimitiveType(mv, var3);
         } else {
            String var4 = d(originType);
            castFromPrimitiveType(mv, originType);
            if (!Objects.equals(targetType, var4)) {
               n(mv, targetType);
            }
         }
      } else if (a(originType) && a(targetType)) {
         q(mv, originType, targetType);
      } else {
         n(mv, targetType);
      }
   }

   public static void e(MethodVisitor mv, String log) {
      mv.visitLdcInsn(log);
      mv.visitMethodInsn(184, Type.getInternalName(Debug.class), "logger", "(Ljava/lang/String;)V", false);
   }

   public static boolean a(String val) {
      return switch (val) {
         case "int", "void", "boolean", "long", "double", "float", "short", "byte", "char" -> true;
         default -> false;
      };
   }

   public static int createSuitableLoad(MethodVisitor mv, String loadType, int loadIndex) {
      switch (loadType) {
         case "int":
         case "boolean":
         case "short":
         case "byte":
         case "char":
            mv.visitVarInsn(21, loadIndex);
            return 1;
         case "long":
            mv.visitVarInsn(22, loadIndex);
            return 2;
         case "double":
            mv.visitVarInsn(24, loadIndex);
            return 2;
         case "float":
            mv.visitVarInsn(23, loadIndex);
            return 1;
         case "void":
            throw new IllegalArgumentException("Can not load a void variable");
         default:
            mv.visitVarInsn(25, loadIndex);
            return 1;
      }
   }

   public static void createMethodLookupField(ClassWriter cw) {
      FieldVisitor var1 = cw.visitField(25, "lookup", "Ljava/lang/invoke/MethodHandles$Lookup;", null, null);
      var1.visitEnd();
   }

   public static void l(MethodVisitor mv, String returnType) {
      switch (returnType) {
         case "int":
         case "boolean":
         case "short":
         case "byte":
         case "char":
            mv.visitInsn(3);
            mv.visitInsn(172);
            break;
         case "long":
            mv.visitInsn(9);
            mv.visitInsn(173);
            break;
         case "double":
            mv.visitInsn(14);
            mv.visitInsn(175);
            break;
         case "float":
            mv.visitInsn(11);
            mv.visitInsn(174);
            break;
         case "void":
            mv.visitInsn(177);
            break;
         default:
            mv.visitInsn(1);
            mv.visitInsn(176);
      }
   }

   private static void n(MethodVisitor mv, String to) {
      if (!"java/lang/Object".equals(to)) {
         mv.visitTypeInsn(192, to);
      }
   }

   public static String c(String boxedClassName) {
      return switch (boxedClassName) {
         case "java/lang/Integer" -> "int";
         case "java/lang/Boolean" -> "boolean";
         case "java/lang/Long" -> "long";
         case "java/lang/Double" -> "double";
         case "java/lang/Float" -> "float";
         case "java/lang/Short" -> "short";
         case "java/lang/Byte" -> "byte";
         case "java/lang/Character" -> "char";
         case "java/lang/Void" -> "void";
         default -> throw new IllegalArgumentException("Not a boxed primitive class: " + boxedClassName);
      };
   }

   public static boolean b(String className) {
      return switch (className) {
         case "java/lang/Integer", "java/lang/Boolean", "java/lang/Long", "java/lang/Double", "java/lang/Float", "java/lang/Short", "java/lang/Byte", "java/lang/Character", "java/lang/Void" -> true;
         default -> false;
      };
   }

   public static MethodVisitor createOverrideMethodImpl(ClassWriter cw, Method method) {
      Class[] var2 = method.getExceptionTypes();
      String[] var3 = var2 != null && var2.length != 0 ? Arrays.stream(var2).map(Type::getInternalName).toArray(String[]::new) : null;
      int var4 = method.getModifiers();
      var4 &= -1025;
      return cw.visitMethod(var4, method.getName(), Type.getMethodDescriptor(method), null, var3);
   }

   public static void f(ClassWriter cw, @Nullable String parentCls) {
      MethodVisitor var2 = cw.visitMethod(1, "<init>", "()V", null, null);
      var2.visitCode();
      var2.visitVarInsn(25, 0);
      var2.visitMethodInsn(183, parentCls == null ? "java/lang/Object" : parentCls.replace(".", "/"), "<init>", "()V", false);
      var2.visitInsn(177);
      var2.visitMaxs(0, 0);
      var2.visitEnd();
   }

   public static String d(String primitive) {
      switch (primitive) {
         case "int":
            return "java/lang/Integer";
         case "boolean":
            return "java/lang/Boolean";
         case "long":
            return "java/lang/Long";
         case "double":
            return "java/lang/Double";
         case "float":
            return "java/lang/Float";
         case "short":
            return "java/lang/Short";
         case "byte":
            return "java/lang/Byte";
         case "char":
            return "java/lang/Character";
         case "void":
            return "java/lang/Void";
         default:
            throw new IllegalArgumentException("Unsupported primitive type: " + primitive);
      }
   }

   public static void k(MethodVisitor mv, String returnType) {
      switch (returnType) {
         case "int":
         case "boolean":
         case "short":
         case "byte":
         case "char":
            mv.visitInsn(172);
            break;
         case "long":
            mv.visitInsn(173);
            break;
         case "double":
            mv.visitInsn(175);
            break;
         case "float":
            mv.visitInsn(174);
            break;
         case "void":
            mv.visitInsn(177);
            break;
         default:
            mv.visitInsn(176);
      }
   }

   public static void o(MethodVisitor mv, String primitive) {
      switch (primitive) {
         case "int":
            mv.visitMethodInsn(182, "java/lang/Integer", "intValue", "()I", false);
            break;
         case "boolean":
            mv.visitMethodInsn(182, "java/lang/Boolean", "booleanValue", "()Z", false);
            break;
         case "long":
            mv.visitMethodInsn(182, "java/lang/Long", "longValue", "()J", false);
            break;
         case "double":
            mv.visitMethodInsn(182, "java/lang/Double", "doubleValue", "()D", false);
            break;
         case "float":
            mv.visitMethodInsn(182, "java/lang/Float", "floatValue", "()F", false);
            break;
         case "short":
            mv.visitMethodInsn(182, "java/lang/Short", "shortValue", "()S", false);
            break;
         case "byte":
            mv.visitMethodInsn(182, "java/lang/Byte", "byteValue", "()B", false);
            break;
         case "char":
            mv.visitMethodInsn(182, "java/lang/Character", "charValue", "()C", false);
            break;
         case "void":
         default:
            throw new IllegalArgumentException("Unsupported primitive type: " + primitive);
      }
   }

   public static void j(MethodVisitor mv, String implPath) {
      mv.visitLdcInsn(Type.getType(ByteCodeUtils.b(implPath)));
      mv.visitMethodInsn(184, Type.getInternalName(MethodHandles.class), "lookup", "()Ljava/lang/invoke/MethodHandles$Lookup;", false);
      mv.visitFieldInsn(179, implPath, "lookup", "Ljava/lang/invoke/MethodHandles$Lookup;");
   }

   public static void castFromPrimitiveType(MethodVisitor mv, String primitive) {
      String var2 = d(primitive);
      Preconditions.checkArgument(!Objects.equals(primitive, "void"), "can not cast " + primitive + " to " + var2);
      mv.visitMethodInsn(184, var2, "valueOf", "(" + ByteCodeUtils.b(primitive) + ")" + ByteCodeUtils.b(var2), false);
   }

   public static void q(MethodVisitor mv, String primFrom, String primTo) {
      if (!Objects.equals(primFrom, primTo)) {
         String var3 = primFrom + "->" + primTo;
         switch (var3) {
            case "int->long":
               mv.visitInsn(133);
               break;
            case "int->float":
               mv.visitInsn(134);
               break;
            case "int->double":
               mv.visitInsn(135);
               break;
            case "int->short":
               mv.visitInsn(147);
               break;
            case "int->byte":
               mv.visitInsn(145);
               break;
            case "int->char":
               mv.visitInsn(146);
               break;
            case "long->int":
               mv.visitInsn(136);
               break;
            case "long->float":
               mv.visitInsn(137);
               break;
            case "long->double":
               mv.visitInsn(138);
               break;
            case "float->int":
               mv.visitInsn(139);
               break;
            case "float->long":
               mv.visitInsn(140);
               break;
            case "float->double":
               mv.visitInsn(141);
               break;
            case "double->int":
               mv.visitInsn(142);
               break;
            case "double->long":
               mv.visitInsn(143);
               break;
            case "double->float":
               mv.visitInsn(144);
            case "short->int":
            case "byte->int":
            case "char->int":
               break;
            default:
               throw new IllegalArgumentException("Unsupported cast: " + primFrom + " to " + primTo);
         }
      }
   }
}
