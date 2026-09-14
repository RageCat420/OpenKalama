package me.matl114.bukkit;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.matl114.events.annotations.Dispatch;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class BukkitMetaItem implements Cloneable, Dispatch {
   protected HashMap<String, Object> yE = new HashMap<>();
   protected Item eq;

   public boolean MK() {
      return !this.ML().isEmpty();
   }

   @Override
   public String toString() {
      return "Bukkit Meta:{" + this.yE.toString() + "}";
   }

   public boolean MG() {
      return this.yE.containsKey("lore");
   }

   public int getCustomModelData() {
      Object var1 = this.yE.get("custom-model-data");
      if (var1 instanceof Integer) {
         return (Integer)var1;
      } else if (var1 instanceof KalamaHelperHelperA var2) {
         try {
            if (var2.b.containsKey("floats")) {
               List var3 = (List)var2.b.get("floats");
               return var3.isEmpty() ? 0 : (var3.get(0) instanceof Number var5 ? var5.intValue() : 0);
            } else {
               return 0;
            }
         } catch (Throwable var6) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public BukkitMetaItem(Item material) {
      this.eq = material;
      this.yE = new HashMap<>();
   }

   public boolean ME() {
      return this.yE.containsKey("display-name");
   }

   public static BukkitMetaItem deserialize(@NotNull Map<String, Object> map) throws Throwable {
      Preconditions.checkArgument(map != null, "Cannot deserialize null map");
      BukkitMetaItem bmi = new BukkitMetaItem(Items.AIR);

      try {
         bmi.yE.putAll(map);
         return bmi;
      } catch (Throwable var3) {
         throw new AssertionError(var3);
      }
   }

   @NotNull
   public Map ML() {
      return (Map)(Object)this.yE.getOrDefault("enchants", new HashMap());
   }

   @NotNull
   public String MF() {
      return this.yE.get("display-name").toString();
   }

   @Override
   public Map<String, Object> by() {
      throw new AssertionError();
   }

   public BukkitPersistentDataContainer MO() {
      if (this.yE.containsKey("PublicBukkitValues")) {
         Object var1 = this.yE.get("PublicBukkitValues");
         if (var1 != null) {
            BukkitPersistentDataContainer var2 = new BukkitPersistentDataContainer();
            var2.putData((NbtCompound)KalamaHelperHelperT.a(var1));
            return var2;
         }
      }

      return null;
   }

   @Nullable
   public List<String> MH() {
      return (List<String>)(Object)this.yE.get("lore");
   }

   @Internal
   public void setVersion(int var1) {
   }

   public boolean MI() {
      return this.yE.containsKey("custom-model-data");
   }

   @NotNull
   public BukkitMetaItem MN() {
      BukkitMetaItem var1 = null;

      try {
         var1 = (BukkitMetaItem)super.clone();
      } catch (Throwable var3) {
         var3.printStackTrace();
      }

      var1.eq = this.eq;
      var1.yE = (HashMap<String, Object>)this.yE.clone();
      return var1;
   }



   @Override
   public Map<String, Object> serialize() { return null; }

}
