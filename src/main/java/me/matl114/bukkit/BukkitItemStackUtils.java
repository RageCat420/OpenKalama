package me.matl114.bukkit;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Collection;
import javax.annotation.Nonnull;
import me.matl114.utils.Debug;
import me.matl114.utils.ItemStackUtils;
import me.matl114.versioned.api.VRecord;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BukkitItemStackUtils {
   public static ItemStack b = new ItemStack(Items.BARRIER, 1);
   public static ConfigurationSerializableDataType<BukkitItemStack> a = new ConfigurationSerializableDataType<>(BukkitItemStack.class);

   public static ProfileComponent e(String hash) {
      try {
         KalamaHelperHelperK var1 = BukkitPlayerProfile.to(hash);
         BukkitPlayerProfile var2 = var1.getProfile();
         var2.name = "CS-CoreLib";
         return var2.te();
      } catch (Throwable var3) {
         return null;
      }
   }

   public static void init() {
   }

   public static ItemStack getAsDisplayItem(BukkitItemStack itemStack) {
      try {
         if (itemStack instanceof CraftItemStack var6) {
            return var6.buildDisplay();
         } else {
            ItemStack var1 = new ItemStack(itemStack.dq());
            var1.setCount(itemStack.getAmount());
            if (itemStack.hasItemMeta()) {
               BukkitMetaItem var2 = itemStack.dx();
               if (var2.ME()) {
                  ItemStackUtils.setCustomName(var1, ItemStackUtils.jsonRawToText(var2.MF()));
               }

               if (var2.MG()) {
                  ItemStackUtils.setLore(var1, var2.MH().stream().map(ItemStackUtils::jsonRawToText).toList());
               }

               if (var2.MK()) {
                  ItemStackUtils.setEnchantmentGlow(var1);
               }

               if (BukkitMetaType.lB.isType(var2)) {
                  ItemStackUtils.setEnchantmentGlow(var1);
               }

               if (BukkitMetaType.lC.isType(var2) && BukkitMetaType.lC.getAttr(var2, "skull-owner") instanceof BukkitPlayerProfile var4) {
                  var4.addGameProfile(var1);
               }

               BukkitPersistentDataContainer var7 = var2.MO();
               if (var7 != null) {
                  ItemStackUtils.T(var1, nbtCompound -> nbtCompound.put("PublicBukkitValues", var7.toCompound()));
               }

               if (var2.MI()) {
                  ItemStackUtils.setCustomModelData(var1, var2.getCustomModelData());
               }
            }

            return var1;
         }
      } catch (Throwable var5) {
         Debug.a("error in ItemConvertion");
         return b;
      }
   }

   @Nonnull
   public static PropertyMap buildPropertyMap(PropertyMap oldMap, String hash) {
      try {
         Property var2 = BukkitPlayerProfile.encodeUrlToProperty(BukkitPlayerProfile.tl(hash), KalamaHelperHelperM.yr, null);
         LinkedHashMultimap var3 = LinkedHashMultimap.create();
         var3.putAll(oldMap);
         var3.removeAll("textures");
         var3.put("textures", var2);
         return VRecord.createProperty(var3);
      } catch (Throwable var4) {
         return oldMap;
      }
   }

   static {
      Debug.a("Bukkit ItemStack Utils enabled");
   }

   public static String getHashFromProfile(ProfileComponent profileComponent) {
      Collection var1 = VRecord.getGameProfileProperties(profileComponent).get("textures");
      if (var1 != null && !var1.isEmpty()) {
         Property var2 = (Property)Iterables.getFirst(var1, null);
         if (var2 == null) {
            return null;
         } else {
            JsonObject var3 = KalamaHelperHelperK.decodePropertyValue(var2.value());
            if (var3 != null
               && var3.get("textures") instanceof JsonObject var5
               && var5.get("SKIN") instanceof JsonObject var9
               && var9.get("url") instanceof JsonPrimitive var10) {
               String var11 = var10.getAsString();
               String[] var8 = var11.split("/");
               return var8[var8.length - 1];
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }
}
