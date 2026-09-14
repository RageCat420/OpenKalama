package me.matl114.hacks.modules.move;


import net.minecraft.client.MinecraftClient;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.matl114.hacks.MovTasks;
import me.matl114.managers.Tasks;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.inventory.ItemStackSample;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;

public class MoveSubHelperL {
   public Hand f;
   public final Map<RegistryEntry<StatusEffect>, MoveSubHelperYX> i;
   public int d;
   public int a;
   public ItemStack e;
   public boolean g;
   public int c;
   public AttributeContainer b = null;
   public final Set<ItemStackSample> j;
   public boolean h;

   public void tickUpdate(PlayerEntity player) {
      AttributeContainer var2 = new AttributeContainer(DefaultAttributeRegistry.get(player.getType()));
      var2.setFrom(player.getAttributes());
      this.b = var2;
      int var3 = 0;
      int var4 = 0;

      for (EquipmentSlot var8 : PlayerStateManager.ARMOR) {
         ItemStack var9 = player.getEquippedStack(var8);
         if (!var9.isEmpty()) {
            ItemEnchantmentsComponent var10 = (ItemEnchantmentsComponent)var9.get(DataComponentTypes.ENCHANTMENTS);
            if (!var10.isEmpty()) {
               int var11 = ItemStackUtils.getEnchantmentLevel(var10, Enchantments.PROTECTION);
               var3 += var11;
               var11 = ItemStackUtils.getEnchantmentLevel(var10, Enchantments.BLAST_PROTECTION);
               var4 += var11;
            }
         }
      }

      this.c = var3;
      this.d = var4;
      if (player.isUsingItem()) {
         this.e = player.getActiveItem().copy();
         this.f = player.getActiveHand();
      } else {
         this.e = null;
         this.f = null;
      }

      this.g = MovTasks.u(player);
      Box var12 = MinecraftClient.getInstance().player.getBoundingBox();
      this.h = MovTasks.isCollidingWithEnvironment(player, var12.withMinY(var12.maxY).withMaxY(var12.maxY + 0.42));
      this.a = Tasks.b();
   }

   public MoveSubHelperL() {
      this.i = new ConcurrentHashMap<>();
      this.j = new HashSet<>();
   }
}
