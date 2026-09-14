package me.matl114.managers;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.matl114.SlimefunHelper;
import me.matl114.managers.file.FileStorage;
import me.matl114.managers.file.NBTFileStorageImpl;
import me.matl114.utils.Debug;
import net.fabricmc.loader.api.FabricLoader;
import org.yaml.snakeyaml.Yaml;

public class FileManager {
   public static final Map<File, FileStorage> g = new ConcurrentHashMap<>();
   public static final File f = new File(FileManager.d, "config");
   public static final String b = "internal";
   public static final String c = "config";
   public static final String a = "kalama";
   public static final File e = new File(FileManager.d, "internal");
   protected static final FileManager INSTANCE = new FileManager();
   public static final File d = FabricLoader.getInstance().getGameDir().resolve("kalama").toFile();

   private FileStorage x(File file) {
      String var2 = file.getName();
      if (!var2.endsWith(".nbt") && !var2.endsWith(".dat")) {
         throw new UnsupportedOperationException("Unsupported file type: " + var2 + " (only .nbt/.dat supported for now)");
      } else {
         return new NBTFileStorageImpl(file);
      }
   }

   public boolean l(File file) {
      return file.exists() && file.isFile();
   }

   public boolean s(String filePath) {
      File var2 = new File(f, filePath);
      return this.l(var2);
   }

   public FileStorage o(String filePath) {
      return this.j(new File(e, filePath), false, true);
   }

   public File c(String path) {
      this.f(path);
      return this.e(path);
   }

   public FileStorage n(File flie) {
      return this.j(flie, false, true);
   }

   public FileStorage u(String filePath, boolean createOnNoExist) {
      return this.j(new File(f, filePath), false, createOnNoExist);
   }

   public File d(File path) {
      this.b(path);
      return path;
   }

   public void checkFolder(File file) {
      if (file.exists() && file.isDirectory()) {
         Preconditions.checkArgument(file.delete(), "File delete failure");
         FileStorage var2 = g.get(file);
         if (var2 != null && !var2.m()) {
            var2.g();
         }
      }
   }

   private void i() {
      Iterator var1 = g.entrySet().iterator();

      while (var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         FileStorage var3 = (FileStorage)var2.getValue();
         if (var3.m()) {
            var1.remove();
         } else if (var3.p()) {
            var3.g();
            var3.l(false);
         }
      }
   }

   public FileStorage m(File file, boolean reload) {
      return this.j(file, reload, true);
   }

   public void f(String s) {
      this.b(new File(d, s));
   }

   public FileStorage t(String filePath) {
      return this.j(new File(f, filePath), false, true);
   }

   public FileStorage p(String file) {
      return this.j(new File(d, file), false, true);
   }

   public FileStorage v(String filePath, boolean reload, boolean createOnNoExist) {
      return this.j(new File(f, filePath), reload, createOnNoExist);
   }

   public boolean k(String path) {
      return this.l(new File(d, path));
   }

   public void h(String s) {
      this.checkFolder(new File(d, s));
   }

   public FileStorage q(String file, boolean createOnNoExist) {
      return this.j(new File(d, file), false, createOnNoExist);
   }

   public void w() {
      for (File var2 : new ArrayList<>(g.keySet())) {
         this.j(var2, true, true);
      }
   }

   public static void syncKeys(HashMap config, HashMap defaults) {
      for (Object var3 : defaults.keySet()) {
         if (config.containsKey(var3)) {
            Object var4 = config.get(var3);
            Object var5 = defaults.get(var3);
            if (var4 instanceof HashMap var6 && var5 instanceof HashMap var7) {
               syncKeys(var6, var7);
            }
         } else {
            config.put(var3, defaults.get(var3));
         }
      }
   }

   public FileStorage j(File file, boolean reload, boolean createOnNoExist) {
      this.checkFolder(file);
      FileStorage var4 = g.get(file);
      if (var4 != null) {
         if (!var4.m()) {
            if (reload) {
               var4.h();
            }

            return var4;
         }

         g.remove(file, var4);
      }

      if (!createOnNoExist && !file.exists()) {
         return null;
      } else {
         var4 = this.x(file);
         FileStorage var5 = g.remove(file);
         if (var5 != null) {
            var5.n(true);
         }

         g.put(file, var4);
         return var4;
      }
   }

   public File e(String path) {
      return new File(d, path);
   }

   public void b(File file) {
      if (!file.exists() || !file.isDirectory()) {
         Preconditions.checkArgument(file.mkdirs(), "File create failure");
      }
   }

   public static FileManager getInstance() {
      return INSTANCE;
   }

   public FileStorage r(String filePath, boolean reload, boolean createOnNoExist) {
      return this.j(new File(d, filePath), reload, createOnNoExist);
   }

   public static File loadOrUseInternal(String configName) {
      File var1 = FabricLoader.getInstance().getConfigDir().resolve(configName).toFile();
      if (!var1.exists()) {
         try {
            if (!var1.getParentFile().exists()) {
               Files.createDirectories(var1.toPath().getParent());
            }

            Files.copy(SlimefunHelper.getInstance().getClass().getResourceAsStream("/" + configName), var1.toPath());
         } catch (Throwable var14) {
            Debug.a("AN INTERNAL ERROR WHILE LOADING DEFAULT CONFIG");
            Debug.f(var14);
            return null;
         }
      }

      Yaml var2 = new Yaml();
      HashMap var3 = new HashMap();
      Object var4 = null;

      try (FileReader var5 = new FileReader(var1)) {
         var3 = var2.load(var5);
         var3 = var3 == null ? new HashMap() : var3;
         var4 = (HashMap)var2.load(SlimefunHelper.getInstance().getClass().getResourceAsStream("/" + configName));
         var4 = var4 == null ? new HashMap() : var4;
         syncKeys(var3, (HashMap)var4);
      } catch (Throwable var13) {
         Debug.a("AN INTERNAL ERROR WHILE LOADING DEFAULT CONFIG");
         var13.printStackTrace();
      }

      try (FileWriter var17 = new FileWriter(var1)) {
         var2.dump(var3, var17);
      } catch (Throwable var10) {
         Debug.a("AN INTERNAL ERROR WHILE WRITING CONFIG");
         Debug.f(var10);
      }

      return var1;
   }

   private FileManager() {
      if (!d.exists() || !d.isDirectory()) {
         Preconditions.checkArgument(d.mkdirs(), "File create failure");
      }

      this.b(e);
      this.b(f);
      KalamaHelperHelperG.b(this::i, 15000L, 15000L);
   }
}
