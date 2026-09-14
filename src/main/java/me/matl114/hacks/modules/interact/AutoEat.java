package me.matl114.hacks.modules.interact;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import me.matl114.accessors.access.ClientAccess;
import me.matl114.accessors.hacks.KeyBindAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.UseItem;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.combat.TargetSelector;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.move.ElytraExtra;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.versioned.api.VItem;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.FoodComponent.StatusEffectEntry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.apache.commons.lang3.stream.Streams;

public class AutoEat extends BaseModule {
   public final FlagRef enableHunger;
   private Runnable gq;
   boolean gu;
   public final FlagRef inv;
   public final DoubleRef noEnemyDistanceGround;
   public final IntRef cooldown;
   public final FlagRef pauseInLiquid;
   public final ModulePath ga;
   public final FlagRef fireworkFix;
   public final FlagRef noEnemy;
   public final KeyBindRef J;
   public final ModulePath fZ = makePath(Configs.n, "interaction-tweaks");
   public final FlagRef autoFireworks;
   public final DoubleRef healthLevel;
   private final Set<RegistryEntry<StatusEffect>> gv;
   private boolean gp;
   public final FlagRef leftClickToolForceEat;
   private int gr;
   public final FlagRef enableHealth;
   public final IntRef hungerLevel;
   private int gs;
   public final FlagRef ae;
   private int gt;
   public final FlagRef log;
   public final DoubleRef noEnemyDistanceAir;
   public final NBTRef<EntrySet<Item>> gl;

   private boolean isGoldenAppleFood(ItemStack stack) {
      return stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE);
   }

   private boolean isHealingPotion(ItemStack stack) {
      FoodComponent var2 = (FoodComponent)stack.get(DataComponentTypes.FOOD);
      PotionContentsComponent var3 = (PotionContentsComponent)stack.get(DataComponentTypes.POTION_CONTENTS);
      if (var2 == null && var3 == null) {
         return false;
      } else if (var3 != null && Streams.of(var3.getEffects()).<RegistryEntry>map(StatusEffectInstance::getEffectType).anyMatch(this.gv::contains)) {
         return true;
      } else {
         if (var2 != null) {
            for (StatusEffectEntry var5 : var2.effects()) {
               if (this.gv.contains(var5.effect().getEffectType())) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private void stopEating() {
      if (this.gp) {
         KeyBindAccess.of(mc.options.useKey).resetKeyState();
         if (!checkNull() && this.gq != null) {
            this.gq.run();
         }
      }

      this.gq = null;
      this.gp = false;
      this.gr = -1;
      this.gs = Tasks.b() + this.cooldown.get();
   }

   private FoodComponent getFoodComponent(ItemStack stack) {
      return stack != null && !stack.isEmpty() ? (FoodComponent)stack.get(DataComponentTypes.FOOD) : null;
   }

   private boolean canContinueEat() {
      return this.gr >= 0
         && mc.player.isUsingItem()
         && mc.player.getActiveHand() == (this.gr == 40 ? Hand.OFF_HAND : Hand.MAIN_HAND)
         && (InventoryUtils.getSelectedSlot() == this.gr || this.gr == 40)
         && this.gp;
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bd(), this::onTickPre);
      this.registerListener(Listener.be(), this::onTickPost);
      this.registerListener(Listener.N(), this::y);
      this.registerListener(Listener.ar().getChannel(EntityStatusS2CPacket.class), this::onStatusConsumed);
      this.registerListener(Listener.bh(), this::onRightClick);
   }

   public AutoEat() {
      super("AutoEat");
      this.ga = this.fZ.add("auto-eat");
      this.ae = this.flagBuilder(this.ga.addEnable()).build();
      this.J = this.toggleHotkey(this.ga.addHotkey(), new MultiKeyBind(), this.ga.addEnable()).build();
      this.log = this.builder(this.ga.add("log"), Boolean.class).defaultValue(true).build();
      this.inv = this.builder(this.ga.add("inv"), Boolean.class).defaultValue(true).build();
      this.leftClickToolForceEat = this.flagBuilder(this.ga.add("left-click-tool-force-eat")).build();
      this.enableHealth = this.builder(this.ga.add("enable-health"), Boolean.class).defaultValue(true).build();
      this.enableHunger = this.builder(this.ga.add("enable-hunger"), Boolean.class).defaultValue(true).build();
      this.healthLevel = this.doubleBuilder(this.ga.add("health-level")).defaultValue(10.0).validator(Configs.doubleRange(0.0, 20.0)).build();
      this.hungerLevel = this.intBuilder(this.ga.add("hunger-level")).defaultValue(16).validator(Configs.intRange(0, 20)).build();
      this.noEnemy = this.builder(this.ga.add("no-enemy"), Boolean.class).defaultValue(true).build();
      this.noEnemyDistanceAir = this.doubleBuilder(this.ga.add("no-enemy-distance-air")).defaultValue(8.0).validator(Configs.doubleRange(0.0, 64.0)).build();
      this.noEnemyDistanceGround = this.doubleBuilder(this.ga.add("no-enemy-distance-ground"))
         .defaultValue(8.0)
         .validator(Configs.doubleRange(0.0, 64.0))
         .build();
      this.cooldown = this.intBuilder(this.ga.add("cooldown")).defaultValue(20).build();
      this.gl = this.builder(this.ga.add("white-list-item"), EntrySet.<Item>parameter())
         .defaultValue(new EntrySet<Item>(new Regex("^(golden_apple|potion|golden_carrot)$"), Registries.ITEM))
         .build();
      this.fireworkFix = this.builder(this.ga.add("firework-fix"), Boolean.class).defaultValue(true).build();
      this.autoFireworks = this.builder(this.ga.add("auto-fireworks"), Boolean.class).defaultValue(false).build();
      this.pauseInLiquid = this.builder(this.ga.add("pause-in-liquid"), Boolean.class).defaultValue(false).build();
      this.gq = null;
      this.gr = -1;
      this.gs = 0;
      this.gt = 0;
      this.gu = false;
      this.gv = new HashSet<>();
      this.gv.add(StatusEffects.INSTANT_HEALTH);
      this.gv.add(StatusEffects.REGENERATION);
      this.bindFlag(this.ae);
   }

   public void onTickPost(Event<Void> event) {
      if (!checkNull()) {
         ClientPlayerEntity var2 = mc.player;
         if (this.ae.get() && !this.gp) {
            boolean var3 = false;
            boolean var4 = this.inv.get();
            boolean var5 = false;
            if (!this.mayUseItem()) {
               if (this.gt != 0) {
                  var3 = true;
                  var4 = true;
                  var5 = this.gt > 1;
                  this.gt = 0;
               } else {
                  label81:
                  if (this.gs <= Tasks.b() && (!this.pauseInLiquid.get() || !PlayerStateManager.INSTANCE.jy && !PlayerStateManager.INSTANCE.jG)) {
                     if (this.noEnemy.get()) {
                        double var6 = mc.player.isFallFlying() ? this.noEnemyDistanceAir.get() : this.noEnemyDistanceGround.get();
                        if (var6 > 1.0E-6 && TargetSelector.INSTANCE.akK(var6, true, pl -> pl instanceof PlayerEntity) != null) {
                           break label81;
                        }
                     }

                     if (this.enableHealth.get() && var2.getHealth() <= this.healthLevel.get()) {
                        var3 = true;
                     } else if (this.enableHunger.get() && var2.getHungerManager().getFoodLevel() <= this.hungerLevel.get()) {
                        var3 = true;
                     }
                  }
               }
            }

            if (var3) {
               boolean var8 = true;
               KalamaHelperHelperK var9 = this.findFood(var4);
               if (var9 == null) {
                  return;
               }

               if (this.fireworkFix.get() && var2.isFallFlying() && ElytraExtra.INSTANCE.agm() > 10) {
                  var8 = false;
               }

               if (this.autoFireworks.get() && var2.isFallFlying() && !var8 && !this.gu) {
                  ElytraExtra.INSTANCE.afI();
                  this.gu = true;
               }

               if (var8) {
                  this.gu = false;
                  this.tryStartEating(var9, false);
               }
            }
         }
      }
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      this.stopEating();
   }

   public void onTickPre(Event<Void> event) {
      ClientPlayerEntity var2 = mc.player;
      if (checkNull() && this.gp) {
         this.stopEating();
      }

      if (this.gp) {
         if (this.canContinueEat()) {
            mc.options.useKey.setPressed(true);
         } else {
            if (this.log.get()) {
               this.logI18N("message.module.auto-eat.stop", new Object[0]);
            }

            this.stopEating();
         }
      }
   }

   private Double scoreFood(ItemStack stack, boolean hurtPriority) {
      if (!VItem.w().h(stack)) {
         return null;
      } else if (!this.gl.get().test(stack.getItem())) {
         return null;
      } else {
         FoodComponent var3 = this.getFoodComponent(stack);
         double var5;
         if (var3 != null) {
            if (!mc.player.canConsume(var3.canAlwaysEat())) {
               return null;
            }

            int var4 = var3.nutrition();
            var5 = var3.saturation() * var4;
         } else {
            var5 = 0.0;
         }

         if (hurtPriority && mc.world.getPlayers().size() > 1) {
            if (this.isGoldenAppleFood(stack)) {
               var5 += 100.0;
            }

            if (this.isHealingPotion(stack)) {
               var5 += 50.0;
            }
         }

         return var5 <= 0.0 ? null : var5;
      }
   }

   public boolean mayUseItem() {
      if (mc.player.isUsingItem()) {
         return true;
      } else {
         if (VItem.w().b(mc.player.getStackInHand(Hand.MAIN_HAND)) || VItem.w().b(mc.player.getStackInHand(Hand.OFF_HAND))) {
            if (mc.options.useKey.isPressed()) {
               return true;
            }

            if (InteractionTasks.R().lastAutoUsingSpear) {
               return true;
            }
         }

         return false;
      }
   }

   private void onStatusConsumed(Event<EntityStatusS2CPacket> event) {
      if (this.gp && ((EntityStatusS2CPacket)event.b).getEntity(mc.world) == mc.player && ((EntityStatusS2CPacket)event.b).getStatus() == 9) {
         this.stopEating();
         this.gs = Tasks.b() + this.cooldown.get();
      }
   }

   private KalamaHelperHelperK<ItemStack> findFood(boolean useInv) {
      boolean var2 = this.enableHealth.get() && mc.player.getHealth() <= this.healthLevel.get();
      return useInv ? InventoryUtils.v(stack -> this.scoreFood(stack, var2), true, false) : this.findHandStack(var2);
   }

   public void onRightClick(Event<UseItem> event) {
      Hand var2 = ((UseItem)event.b).c();
      if (this.ae.get() && this.leftClickToolForceEat.get() && mc.options.useKey.isPressed() && !this.gp) {
         ItemStack var3 = mc.player.getStackInHand(var2);
         Hand var4 = var2 == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
         ItemStack var5 = mc.player.getStackInHand(var4);
         if ((VItem.w().d(var3) || VItem.w().c(var3)) && !VItem.w().b(var3) && !InteractUtils.canHoldUse(var5)) {
            KalamaHelperHelperK var6 = this.findFood(true);
            if (var6 == null) {
               return;
            }

            boolean var7 = true;
            if (this.fireworkFix.get() && mc.player.isFallFlying() && ElytraExtra.INSTANCE.agm() > 10) {
               var7 = false;
            }

            if (this.autoFireworks.get() && mc.player.isFallFlying() && !var7 && !this.gu) {
               ElytraExtra.INSTANCE.afI();
               this.gu = true;
            }

            if (var7) {
               this.gu = false;
               this.tryStartEating(var6, var2 == Hand.OFF_HAND);
               if (this.gp) {
                  event.cancel();
                  ((UseItem)event.b).actionResult(ActionResult.SUCCESS);
               }
            }
         }
      }
   }

   private KalamaHelperHelperK<ItemStack> findHandStack(boolean health) {
      ItemStack var2 = mc.player.getMainHandStack();
      ItemStack var3 = mc.player.getOffHandStack();
      Double var4 = this.scoreFood(var2, health);
      Double var5 = this.scoreFood(var3, health);
      if (var4 != null) {
         return var5 != null && var5 > var4 ? new KalamaHelperHelperK<>(40, var3) : new KalamaHelperHelperK<>(InventoryUtils.getSelectedSlot(), var2);
      } else {
         return var5 != null ? new KalamaHelperHelperK<>(40, var3) : null;
      }
   }

   private void tryStartEating(@Nonnull KalamaHelperHelperK<ItemStack> re, boolean offHand) {
      if (this.log.get()) {
         MutableText var3 = VItem.w().l((ItemStack)re.val());
         this.logI18N("message.module.auto-eat.start", new Object[]{var3});
      }

      offHand = offHand || re.index() == 40;
      Runnable var5 = offHand ? InvExtra.INSTANCE.uh(re.index()) : InvExtra.INSTANCE.swapInventoryIndexToHand(re.index());
      if (var5 != null) {
         ClientAccess.of(mc).simulateUseItem(offHand ? Hand.OFF_HAND : Hand.MAIN_HAND);
         if (mc.player.isUsingItem()
            && mc.player.getActiveHand() == Hand.OFF_HAND == offHand
            && ItemStack.areItemsAndComponentsEqual((ItemStack)re.val(), mc.player.getActiveItem())) {
            mc.options.useKey.setPressed(true);
            this.gp = true;
            this.gq = var5;
            this.gr = offHand ? 40 : InventoryUtils.getSelectedSlot();
         } else {
            KeyBindAccess.of(mc.options.useKey).resetKeyState();
            var5.run();
         }
      }
   }

   private void y(Event<World> event) {
      this.stopEating();
   }
}
