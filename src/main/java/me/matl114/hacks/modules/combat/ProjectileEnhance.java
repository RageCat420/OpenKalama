package me.matl114.hacks.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.matl114.accessors.access.PlayerInteractItemC2SPacketAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.KalamaHelperHelperFX;
import me.matl114.hacks.KalamaHelperHelperIX;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.ItemStackUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.EggItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ProjectileEnhance extends BaseModule {
   public EnumRef<Configs$LegalInteractMode> targetingMode;
   public NBTRef<Regex> tpAccelerateExactTp2;
   public FlagRef aimEnable;
   public final ModulePath RY = makePath(Configs.k, "projectile");
   public FlagRef tpEnable;
   public DoubleRef tpAccelerate;
   public KeyBindRef projectileEnhanceHotkey;
   public FlagRef ae = this.flagBuilder(this.RY.add("projectile-enhance")).build();
   public FlagRef tridentAutoDupe;
   public FlagRef tpAccelerateExactTp;

   public void onPlayerInteractItem(Event<PlayerInteractItemC2SPacket> packetMutableObject) {
      if (!packetMutableObject.d()) {
         if (this.ae.get()) {
            PlayerInteractItemC2SPacket var2 = (PlayerInteractItemC2SPacket)packetMutableObject.e();
            Hand var3 = var2.getHand();
            ItemStack var4 = PlayerInteractItemC2SPacketAccess.of((PlayerInteractItemC2SPacket)packetMutableObject.b).getItemStack();
            if (var4 != null && !var4.isEmpty()) {
               if (this.aimEnable.get()) {
                  boolean var5 = false;
                  float var6 = 3.6E9F;
                  if (this.passUseItemIdCheck(var4)) {
                     var5 = true;
                  } else if (var4.getItem() instanceof CrossbowItem
                     || var4.getItem() instanceof SplashPotionItem
                     || var4.getItem() instanceof LingeringPotionItem) {
                     var5 = true;
                     var6 = getShootingPowerCrossbow(var4);
                  }

                  if (var5) {
                     Entity var7 = CombatTasks.l().akN(false);
                     if (var7 != null) {
                        Debug.b(
                           Text.literal("[Proj Aim] Aim at %s".formatted(var7 instanceof PlayerEntity var8 ? "player " : "entity "))
                              .append(EntityUtils.getEntityDisplayable(var7))
                              .formatted(Formatting.GREEN)
                        );
                        Vec3d var29 = CombatTasks.m().predictAimPositionForEntity(var7, var6).subtract(mc.player.getEyePos());
                        Vec2f var9 = CombatTasks.calculatePitchYawPredict(var6, Vec3d.ZERO, var29);
                        if (!Float.isNaN(var9.x) && !Float.isInfinite(var9.x) && !Float.isNaN(var9.y) && !Float.isInfinite(var9.y)) {
                           var2 = new PlayerInteractItemC2SPacket(var3, var2.getSequence(), var9.y, var9.x);
                        } else {
                           Debug.b("[Proj Aim] Proj failed to reach the target");
                        }
                     } else {
                        Debug.b(Text.literal("[Proj Aim] Target absent"));
                     }
                  }
               }

               label79:
               if (this.tpEnable.get()
                  && (
                     var4.getItem() instanceof EnderPearlItem var26
                        || var4.getItem() instanceof SplashPotionItem
                        || var4.getItem() instanceof ExperienceBottleItem
                        || var4.getItem() instanceof LingeringPotionItem
                        || var4.getItem() instanceof EggItem
                  )) {
                  boolean var28 = this.tpAccelerateExactTp.get();
                  double var10 = this.tpAccelerate.get();
                  Vec3d var30 = EntityUtils.pitchYawToRotation(var2.getPitch(), var2.getYaw());
                  Vec3d var12 = var30.normalize();
                  Vec3d var13 = Vec3d.ZERO.subtract(var12);
                  Vec3d var14 = Vec3d.ZERO;
                  Vec3d var15 = mc.player.getPos();
                  KalamaHelperHelperFX var16 = new KalamaHelperHelperFX(mc.player, var15, var15.add(var13.multiply(var10 + 1.0)), true);
                  double var17 = var10;

                  label112:
                  while (true) {
                     if (!(var17 > 10.0)) {
                        var17 = 10.0;

                        while (true) {
                           if (!(var17 > 0.0)) {
                              break label112;
                           }

                           Vec3d var33 = var13.multiply(var17);
                           if (var28) {
                              if (MovTasks.validMoveTo(var16, var15.add(var33), Vec3d.ZERO.subtract(var33))) {
                                 var14 = var33;
                                 break label112;
                              }
                           } else if (MovTasks.validMoveToAndBack(var16, var15, var33)) {
                              var14 = var33;
                              break label112;
                           }

                           Vec3d var20 = new Vec3d(var33.x, 0.0, var33.z);
                           Vec3d var21 = var16.simulateMovement(mc.player, var15, var20);
                           if (MovTasks.validMovementAsServer(var20, var21)) {
                              Vec3d var22 = var16.simulateMovement(mc.player, var15.add(var21), new Vec3d(0.0, var33.y, 0.0));
                              Vec3d var23 = var21.add(var22);
                              if (MovTasks.validMoveTo(var16, var15.add(var23), var23.multiply(-1.0))) {
                                 var14 = var23;
                                 break label112;
                              }
                           }

                           var17 -= 0.5;
                        }
                     }

                     if (var28) {
                        Vec3d var19 = var13.multiply(var17);
                        if (MovTasks.validMoveTo(var16, var15.add(var19), Vec3d.ZERO.subtract(var19))) {
                           var14 = var19;
                           break;
                        }
                     } else if (MovTasks.validMoveToAndBack(var16, var15, var13.multiply(var17))) {
                        var14 = var13.multiply(var17);
                        break;
                     }

                     var17--;
                  }

                  if (var14.lengthSquared() > 1.0E-4) {
                     List var31 = MovTasks.z(var15, var15.add(var14), false, 161.0, true);
                     if (!var31.isEmpty()) {
                        Debug.b(Text.literal("[Proj TP] Projectile Velocity Simulate %.2f".formatted(var14.length())).formatted(Formatting.GREEN));
                        ArrayList var24 = new ArrayList();
                        int var25 = var31.size();

                        for (int var34 = 0; var34 < var25; var34++) {
                           var24.add(var34 == 0 ? MovTasks$MovInfo.adA((Vec3d)var31.get(var34)) : MovTasks$MovInfo.adz((Vec3d)var31.get(var34)));
                        }

                        var24.add(MovTasks$MovInfo.adz(var15.add(0.0, 9.0E-8, 0.0)));
                        MovTasks.scheduleFarawayMoveInternal(var24, false, KalamaHelperHelperIX.create(var15), true);
                        MovTasks.Y();
                        break label79;
                     }
                  }

                  Debug.b(Text.literal("[Proj TP] Projectile Velocity fail to simulate"));
               }
            }

            packetMutableObject.context(var2);
         }
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ap().getChannel(PlayerActionC2SPacket.class), this::onTridentDupe);
      this.registerListener(Listener.ap().getChannel(PlayerInteractItemC2SPacket.class), this::onPlayerInteractItem);
   }

   public static float getShootingPowerCrossbow(ItemStack a) {
      ChargedProjectilesComponent var1 = (ChargedProjectilesComponent)a.get(DataComponentTypes.CHARGED_PROJECTILES);
      return var1 != null && var1.contains(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
   }

   public ProjectileEnhance() {
      super("ProjectileEnhance");
      this.projectileEnhanceHotkey = this.toggleHotkey(this.RY.add("projectile-enhance-hotkey"), new MultiKeyBind(), this.RY.add("projectile-enhance")).build();
      this.aimEnable = this.flagBuilder(this.RY.add("aim-enable")).build();
      this.tpEnable = this.flagBuilder(this.RY.add("tp-enable")).build();
      this.targetingMode = this.builder(this.RY.add("targeting-mode"), Configs$LegalInteractMode.class)
         .defaultValue(Configs$LegalInteractMode.USEITEM_PACKET)
         .build();
      this.tpAccelerate = this.builder(this.RY.add("tp-accelerate"), DoubleRef.TYPE).defaultValue(150.0).show(this.tpEnable::get).build();
      this.tpAccelerateExactTp = this.flagBuilder(this.RY.add("tp-accelerate-exact-tp")).show(this.tpEnable::get).build();
      this.tpAccelerateExactTp2 = this.builder(this.RY.add("tp-accelerate-exact-tp"), Regex.class).defaultValue(new Regex("^(LOGITECH_LASER_GUN)$")).build();
      this.tridentAutoDupe = this.flagBuilder(this.RY.add("trident-auto-dupe")).build();
      this.bindFlag(this.ae);
   }

   private boolean passUseItemIdCheck(ItemStack stack) {
      String var2 = Registries.ITEM.getId(stack.getItem()).getPath();
      if (this.tpAccelerateExactTp2.get().test(var2)) {
         return true;
      } else {
         String var3 = ItemStackUtils.aa(stack);
         return var3 != null && this.tpAccelerateExactTp2.get().test(var3);
      }
   }

   public void onTridentDupe(Event<PlayerActionC2SPacket> actionC2SPacketEvent) {
      PlayerActionC2SPacket var2 = (PlayerActionC2SPacket)actionC2SPacketEvent.e();
      if (var2.getAction() == Action.RELEASE_USE_ITEM
         && mc.player != null
         && this.tridentAutoDupe.get()
         && mc.player.getMainHandStack().getItem() instanceof TridentItem var4) {
         mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 3, InventoryUtils.getSelectedSlot(), SlotActionType.SWAP, mc.player);
         Tasks.l(
            () -> mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 3, InventoryUtils.getSelectedSlot(), SlotActionType.SWAP, mc.player),
            1
         );
      }
   }
}
