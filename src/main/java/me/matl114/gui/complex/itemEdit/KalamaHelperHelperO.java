package me.matl114.gui.complex.itemEdit;

import com.mojang.datafixers.util.Pair;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.RegistryUtils;
import me.matl114.utils.config.AttrKeyValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public class KalamaHelperHelperO {
   AttrKeyValue<Integer> b;
   AttrKeyValue<Enchantment> a;

   public KalamaHelperHelperO(String enchantment, int level) {
      this.a = AttrKeyValue.openRegistry(
         "widget.gui.item-edit-screen.nbt-editor.enchantment.id",
         (Registry<Enchantment>)ItemStackUtils.registry().getOptional(RegistryKeys.ENCHANTMENT).orElseThrow(),
         enchantment
      );
      this.b = AttrKeyValue.integer("widget.gui.item-edit.screen.nbt-editor.enchantment.lvl", level);
   }

   public Pair<RegistryEntry<Enchantment>, Integer> entryValue() {
      try {
         Enchantment var1 = this.a.getOriginValue();
         RegistryEntry var2 = RegistryUtils.getRegistryEntry(ItemStackUtils.registry(), RegistryKeys.ENCHANTMENT, var1);
         return new Pair(var2, this.b.getOriginValue());
      } catch (Throwable var3) {
         return new Pair(null, 0);
      }
   }

   public DrawableWidget a() {
      return new KalamaHelperHelperCX(0, 0, 0, 0)
         .Q(new me.matl114.gui.complex.config.KalamaHelperHelperE<>(0, 0, 120, 20, 30, this.a))
         .Q(new me.matl114.gui.complex.config.KalamaHelperHelperE<>(120, 0, 60, 20, 30, this.b));
   }

   public Pair<String, Integer> b() {
      return new Pair(this.a.getValue(), this.b.getOriginValue());
   }
}
