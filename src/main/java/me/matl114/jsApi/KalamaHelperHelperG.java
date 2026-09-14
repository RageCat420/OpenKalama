package me.matl114.jsApi;

import com.jsmacrosce.jsmacros.core.library.BaseLibrary;
import com.jsmacrosce.jsmacros.core.library.Library;
import com.jsmacrosce.jsmacros.core.library.LibraryRegistry;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import me.matl114.events.annotations.Modifiable;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.MineTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.modules.HackModules;
import me.matl114.managers.Tasks;
import me.matl114.utils.ASMUtils;
import me.matl114.utils.ByteCodeUtils;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ClientUtils;
import me.matl114.utils.CollectionUtils;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.CustomClassLoader;
import me.matl114.utils.Debug;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.NetworkUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.ScreenUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.core.Core;

public class KalamaHelperHelperG {
    private static List<Class<?>> a;

    public static synchronized List<Class<?>> e(Class<?> libBase, Class<?> libAnnotation, Class<?> runnerClass) {
        if (a == null) {
            a = new ArrayList<>();

            for (Class var5 : List.of(
                    Consts.class,
                    ClientHelper.class,
                    DataHelper.class,
                    InputHelper.class,
                    KeyBindingHelper.class,
                    PacketHelper.class,
                    RenderHelper.class,
                    ReflectHelper.class,
                    RegistryHelper.class,
                    NBTHelper.class,
                    FileHelper.class,
                    WorldHelper.class,
                    EnumHelper.class,
                    EntityHelper.class,
                    ScreenHelper.class,
                    MovTasks.class,
                    Tasks.class,
                    HackModules.class,
                    CombatTasks.class,
                    InteractionTasks.class,
                    MineTasks.class,
                    InvTasks.class,
                    JsHelper.class,
                    Debug.class,
                    CommonUtils.class,
                    RenderUtils.class,
                    ChatUtils.class,
                    InventoryUtils.class,
                    ItemStackHelper.class,
                    CollectionUtils.class,
                    RaycastUtils.class,
                    ClientUtils.class,
                    ScreenUtils.class,
                    NetworkUtils.class,
                    ItemStackUtils.class)) {
                a.add(f(libBase, libAnnotation, runnerClass, var5));
            }
        }

        return a;
    }

    private static void a() {
        try {
            try {
                label52:
                {
                    try {
                        Class<Core> var7 = Core.class;
                    } catch (Throwable var4) {
                        break label52;
                    }

                    b();
                    return;
                }
            } catch (Throwable var5) {
                Debug.f(var5);
            }

            try {
                try {
                    Class<com.jsmacrosce.jsmacros.core.Core> var0 = com.jsmacrosce.jsmacros.core.Core.class;
                } catch (Throwable var2) {
                    throw new IllegalStateException("No JsMacros instance found");
                }

                c();
            } catch (Throwable var3) {
                Debug.f(var3);
                throw new IllegalStateException("No JsMacros instance found");
            }
        } catch (Throwable var6) {
            Debug.a("JsMacros library inject failed, caused by: ");
            var6.printStackTrace();
            Debug.a("Running Mock js lib test");
            Debug.a("Mock lib test success");
        }
    }

    private static void c() throws Exception {
        Field var0 = Arrays.stream(com.jsmacrosce.jsmacros.core.Core.class.getFields())
                .filter(s -> Modifier.isStatic(s.getModifiers()))
                .filter(s -> s.getType() == com.jsmacrosce.jsmacros.core.Core.class)
                .peek(s -> s.setAccessible(true))
                .findFirst()
                .orElse(null);
        if (var0 == null) {
            var0 = Arrays.stream(com.jsmacrosce.jsmacros.client.JsMacrosClient.class.getFields())
                    .filter(s -> Modifier.isStatic(s.getModifiers()))
                    .filter(s -> s.getType() == com.jsmacrosce.jsmacros.core.Core.class)
                    .peek(s -> s.setAccessible(true))
                    .findFirst()
                    .orElse(null);
        }

        if (var0 == null) {
            throw new IllegalStateException("No valid core find");
        } else {
            com.jsmacrosce.jsmacros.core.Core var1 = (com.jsmacrosce.jsmacros.core.Core) var0.get(null);
            KalamaHelperHelperP.INSTANCE = new KalamaHelperHelperM(var1);
            LibraryRegistry var2 = var1.libraryRegistry;
            Class<BaseLibrary> var3 = BaseLibrary.class;

            for (Class var6 : e(var3, Library.class, com.jsmacrosce.jsmacros.core.Core.class)) {
                var2.addLibrary(var6);
            }

            Debug.a("Successfully injected jsMacrosCE library");
        }
    }

    private static void b() throws Exception {
        Field var0 = Arrays.stream(Core.class.getFields())
                .filter(s -> Modifier.isStatic(s.getModifiers()))
                .filter(s -> s.getType() == Core.class)
                .peek(s -> s.setAccessible(true))
                .findFirst()
                .orElse(null);
        if (var0 == null) {
            Class<JsMacrosClient> var1;
            try {
                var1 = JsMacrosClient.class;
            } catch (Throwable var7) {
                var1 = (Class<JsMacrosClient>) Class.forName("xyz.wagyourtail.jsmacros.client.JsMacros");
            }

            var0 = Arrays.stream(var1.getFields())
                    .filter(s -> Modifier.isStatic(s.getModifiers()))
                    .filter(s -> s.getType() == Core.class)
                    .peek(s -> s.setAccessible(true))
                    .findFirst()
                    .orElse(null);
        }

        if (var0 == null) {
            throw new IllegalStateException("No valid core find");
        } else {
            Core var8 = (Core) var0.get(null);
            KalamaHelperHelperP.INSTANCE = new KalamaHelperHelperD(var8);
            xyz.wagyourtail.jsmacros.core.library.LibraryRegistry var2 = var8.libraryRegistry;
            Class<xyz.wagyourtail.jsmacros.core.library.BaseLibrary> var3 =
                    xyz.wagyourtail.jsmacros.core.library.BaseLibrary.class;

            for (Class var6 : e(var3, xyz.wagyourtail.jsmacros.core.library.Library.class, Core.class)) {
                var2.addLibrary(var6);
            }

            Debug.a("Successfully injected jsMacros library");
        }
    }

    public static synchronized Class<?> f(
            Class<?> targetBaseClass, Class<?> libClass, Class<?> runerClass, Class<?> utilityClass) {
        try {
            boolean var4 = false;
            if (utilityClass.getAnnotation(Modifiable.class) != null) {
                var4 = true;
            }

            boolean var6 = libClass != null;
            String var7 = utilityClass.getName() + "LibImpl";
            String var8 = var7.replace('.', '/');
            String var9 = targetBaseClass.getName().replace('.', '/');
            String var10 = utilityClass.getName().replace('.', '/');
            ClassWriter var11 = new ClassWriter(3);
            var11.visit(65, 49, var8, null, var9, null);
            var11.visitSource(null, null);
            if (var6) {
                String var12 = "L" + libClass.getName().replace('.', '/') + ";";
                AnnotationVisitor var13 = var11.visitAnnotation(var12, true);
                var13.visit("value", utilityClass.getSimpleName());
                var13.visitEnd();
            }

            HashSet var28 = new HashSet();
            HashSet var29 = new HashSet();
            HashSet var14 = new HashSet();

            for (Method var18 : utilityClass.getDeclaredMethods()) {
                int var19 = var18.getModifiers();
                if (Modifier.isPublic(var19)
                        && Modifier.isStatic(var19)
                        && (var4 || var18.getAnnotation(Modifiable.class) != null)) {
                    var28.add(var18);
                    if (var18.getParameterCount() == 0
                            && var18.getReturnType() != void.class
                            && var18.getName().startsWith("get")) {
                        var29.add(var18.getName());
                    }

                    org.objectweb.asm.commons.Method var20 = org.objectweb.asm.commons.Method.getMethod(var18);
                    Type[] var21 = var20.getArgumentTypes();
                    StringBuilder var22 = new StringBuilder("(");

                    for (Type var26 : var21) {
                        var22.append(var26.getDescriptor());
                    }

                    var22.append(")").append(var20.getReturnType().getDescriptor());
                    MethodVisitor var52 = var11.visitMethod(1, var18.getName(), var22.toString(), null, null);
                    var52.visitCode();
                    Class[] var54 = var18.getParameterTypes();
                    int var56 = 1;

                    for (int var57 = 0; var57 < var54.length; var57++) {
                        var56 += ASMUtils.createSuitableLoad(var52, Type.getInternalName(var54[var57]), var56);
                    }

                    var52.visitMethodInsn(184, var10, var18.getName(), var22.toString(), utilityClass.isInterface());
                    ASMUtils.k(var52, Type.getInternalName(var18.getReturnType()));
                    var52.visitMaxs(0, 0);
                    var52.visitEnd();
                }
            }

            for (Field var36 : utilityClass.getDeclaredFields()) {
                int var40 = var36.getModifiers();
                if (Modifier.isPublic(var40)
                        && Modifier.isStatic(var40)
                        && (var4 || var36.getAnnotation(Modifiable.class) != null)) {
                    String var43 = Type.getDescriptor(var36.getType());
                    if (Modifier.isFinal(var40)) {
                        var14.add(var36);
                        var11.visitField(17, var36.getName(), var43, null, null);
                    }

                    String var46 = "get" + var36.getName();
                    if (!var29.contains(var46)) {
                        var29.add(var46);
                        MethodVisitor var49 = var11.visitMethod(17, var46, "()" + var43, null, null);
                        var49.visitCode();
                        var49.visitFieldInsn(
                                178,
                                Type.getInternalName(var36.getDeclaringClass()),
                                var36.getName(),
                                ByteCodeUtils.a(var36.getType()));
                        ASMUtils.k(var49, Type.getInternalName(var36.getType()));
                        var49.visitMaxs(0, 0);
                        var49.visitEnd();
                    }
                }
            }

            Constructor var31 = targetBaseClass.getConstructors()[0];
            String var33 = ByteCodeUtils.getMethodDescriptor("", var31.getParameterTypes(), void.class);
            MethodVisitor var35 = var11.visitMethod(1, "<init>", var33, null, null);
            var35.visitCode();
            var35.visitVarInsn(25, 0);

            for (int var37 = 0; var37 < var31.getParameterCount(); var37++) {
                var35.visitVarInsn(25, var37 + 1);
            }

            var35.visitMethodInsn(183, var9, "<init>", var33, false);

            for (Field var47 : utilityClass.getDeclaredFields()) {
                int var50 = var47.getModifiers();
                if (Modifier.isPublic(var50) && Modifier.isStatic(var50) && var14.contains(var47)) {
                    Object var53 = var47.get(null);
                    String var55 = Type.getDescriptor(var47.getType());
                    var35.visitVarInsn(25, 0);
                    var35.visitFieldInsn(
                            178,
                            Type.getInternalName(var47.getDeclaringClass()),
                            var47.getName(),
                            ByteCodeUtils.a(var47.getType()));
                    var35.visitFieldInsn(181, var8, var47.getName(), var55);
                }
            }

            var35.visitInsn(177);
            var35.visitMaxs(0, 0);
            var35.visitEnd();
            String var39 = "getDelegate";
            String var42 = "()Ljava/lang/String;";
            MethodVisitor var45 = var11.visitMethod(1, var39, var42, null, null);
            var45.visitCode();
            var45.visitLdcInsn(utilityClass.getName());
            var45.visitInsn(176);
            var45.visitMaxs(0, 0);
            var45.visitEnd();
            var11.visitEnd();
            byte[] var48 = var11.toByteArray();
            CustomClassLoader var51 = CustomClassLoader.getInstance();
            var51.b(var7, var48);
            return var51.loadAccessClass(var7);
        } catch (Exception var27) {
            throw new RuntimeException(var27);
        }
    }

    public static void d() {
        Tasks.l(KalamaHelperHelperG::a, 1);
    }
}
