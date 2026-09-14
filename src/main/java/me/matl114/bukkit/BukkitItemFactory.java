package me.matl114.bukkit;

import net.minecraft.item.Item;

public class BukkitItemFactory {
   public BukkitMetaItem asMetaFor(BukkitMetaItem meta, Item material) {
      if (meta != null) {
         meta.eq = material;
      }

      return meta;
   }

   public BukkitMetaItem getItemMeta(Item material) {
      return new BukkitMetaItem(material);
   }

   public boolean equals(BukkitMetaItem meta1, BukkitMetaItem meta2) {
      return meta1 != null ? meta1.equals(meta2) : meta2 == null;
   }
}
