package me.matl114.bukkit;

import java.util.Arrays;
import java.util.HashSet;

public enum BukkitMetaType {
   lB("stored-enchants"),
   lC("skull-owner");
   HashSet<String> moreAttributes = new HashSet<>();
   // $VF: synthetic field

   public Object getAttr(BukkitMetaItem meta, String key) {
      if (this.moreAttributes.contains(key)) {
         return meta.yE.get(key);
      } else {
         throw new AssertionError("key not in type Attribute record");
      }
   }

   private BukkitMetaType(String... attrs) {
      this.moreAttributes.addAll(Arrays.stream(attrs).toList());
   }

   public boolean isType(BukkitMetaItem meta0) {
      if (meta0 != null) {
         for (String var3 : this.moreAttributes) {
            if (meta0.yE.containsKey(var3)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
