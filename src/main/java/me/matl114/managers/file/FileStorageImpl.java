package me.matl114.managers.file;

import java.io.File;

public abstract class FileStorageImpl implements FileStorage {
   protected boolean f;
   protected final File file;
   protected boolean g;

   public File getFile() {
      return this.file;
   }

   @Override
   public File q() {
      return this.file;
   }

   @Override
   public boolean p() {
      return this.g;
   }

   @Override
   public void l(boolean dirty) {
      this.g = dirty;
   }

   @Override
   public boolean m() {
      return this.f;
   }

   public FileStorageImpl(File file) {
      this.file = file;
      this.ensureParentDir();
   }

   protected void ensureParentDir() {
      File var1 = this.file.getParentFile();
      if (var1 != null && !var1.exists()) {
         var1.mkdirs();
      }
   }

   @Override
   public void n(boolean deprecated) {
      this.f = deprecated;
   }
}
