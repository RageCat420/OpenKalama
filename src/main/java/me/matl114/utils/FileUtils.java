package me.matl114.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtils {
   public static void b(File tempFile, File targetFile) throws IOException {
      boolean var2 = false;

      try {
         a(tempFile, targetFile);
         var2 = true;
      } finally {
         if (!var2 && tempFile.exists() && !tempFile.delete()) {
            tempFile.deleteOnExit();
         }
      }
   }

   public static void a(File tempFile, File targetFile) throws IOException {
      try {
         Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException var3) {
         Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      }
   }
}
