package me.matl114.hacks.utils.render;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ItemStackDisplayUtils {
   public static int getColorByPercentage(int percentage) {
      if (percentage <= 33) {
         return -65536;
      } else {
         return percentage <= 66 ? -256 : -16711936;
      }
   }

   public static int getDurabilityPercentage(ItemStack stackOverride) {
      return stackOverride.getMaxDamage() > 0 ? (stackOverride.getMaxDamage() - stackOverride.getDamage()) * 100 / stackOverride.getMaxDamage() : 100;
   }

   public static int getDamageDisplayColor(ItemStack stack) {
      return a(stack.getDamage(), stack.getMaxDamage());
   }

   public static Text getDamageShowText(ItemStack stack, ItemStackDisplayUtils$DamageDisplay display) {
      int var2 = stack.getDamage();
      int var3 = stack.getMaxDamage();
      int var4 = var3 - var2;

      return switch (display) {
         case DAMAGE_LEFT -> Text.literal("%d".formatted(var4));
         case DAMAGE -> Text.literal("-%d".formatted(var2));
         case PERCENTAGE -> Text.literal("%d%%".formatted(var4 * 100 / var3));
         default -> null;
      };
   }

   public static int a(int damage, int damageMax) {
      damage = damageMax - damage;
      if (damage < damageMax * 0.33) {
         return -65536;
      } else {
         return damage < damageMax * 0.66 ? -256 : -16711936;
      }
   }
}
