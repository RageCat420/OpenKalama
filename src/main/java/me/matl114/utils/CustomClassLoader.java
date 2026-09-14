package me.matl114.utils;

import java.lang.ref.WeakReference;
import java.util.HashSet;

public class CustomClassLoader extends ClassLoader {
   private final HashSet<String> loadedClassNames = new HashSet<>();
   static WeakReference<CustomClassLoader> instance = new WeakReference<>(null);

   public Class loadAccessClass(String name) {
      if (this.loadedClassNames.contains(name)) {
         try {
            return super.loadClass(name, false);
         } catch (ClassNotFoundException var3) {
            throw new RuntimeException(var3);
         }
      } else {
         return null;
      }
   }

   public boolean isClassPresent(String name) {
      return this.loadedClassNames.contains(name) ? true : super.findLoadedClass(name) != null;
   }

   public Class<?> b(String name, byte[] bytes) throws ClassFormatError {
      this.loadedClassNames.add(name);
      return this.e(name, bytes);
   }

   Class<?> e(String name, byte[] bytes) throws ClassFormatError {
      return this.defineClass(name, bytes, 0, bytes.length, this.getClass().getProtectionDomain());
   }

   public static synchronized CustomClassLoader getInstance() {
      if (instance.get() == null) {
         instance = new WeakReference<>(new CustomClassLoader(CustomClassLoader.class.getClassLoader()));
      }

      return instance.get();
   }

   CustomClassLoader(ClassLoader parent) {
      super(parent);
      Debug.a("Creating new CustomClassLoader");
   }
}
