package me.matl114.hacks.modules.combat;

import com.google.common.base.Preconditions;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.KalamaHelperHelperIX;
import me.matl114.hacks.KalamaHelperHelperV;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class BowTp extends BaseModule {
   public DoubleRef bowtpDistance;
   public DoubleRef bowtpTargetDistance;
   public KeyBindRef bowtpEnableHotkey;
   public FlagRef bowtpRenderTarget;
   public final ModulePath CZ = makePath(Configs.k, "bow-att");
   public DoubleRef bowtpStartDeltaY;
   public final FlagRef ae = this.flagBuilder(this.CZ.add("bowtp-enable")).build();

   public void onBowTpAttack(Event<PlayerActionC2SPacket> packetEvent, Entity target) {
      ClientPlayerEntity var3 = mc.player;
      ArrayDeque var4 = new ArrayDeque();
      ArrayDeque var5 = new ArrayDeque();
      Vec3d var6 = mc.player.getPos();
      float var7 = mc.player.getPitch();
      float var8 = mc.player.getYaw();
      var4.addLast(MovTasks$MovInfo.adB(mc.player.getPos()));
      var5.addFirst(MovTasks$MovInfo.adB(mc.player.getPos()));
      if (this.processExactBowTp(var3, target, var4, var5)) {
         if (this.processBowVClip(var3, target, var4, var5)) {
            Iterator var9 = var4.iterator();
            Preconditions.checkArgument(var9.hasNext());
            Vec3d var10 = ((MovTasks$MovInfo)var9.next()).vec3d();
            KalamaHelperHelperIX var11 = KalamaHelperHelperIX.create(var10);
            ArrayList var12 = new ArrayList();
            var9.forEachRemaining(var12::add);
            var5.removeFirst();
            int var13 = var12.size();
            var12.addAll(var5);
            List var14 = MovTasks.createMovingPacketsForMovSequence(var11, var12, true, false);

            for (int var15 = 0; var15 < var13; var15++) {
               ((KalamaHelperHelperV)var14.get(var15)).run();
            }

            Listener.sendPacketNoEvents((Packet<?>)packetEvent.e());

            for (int var17 = var13; var17 < var14.size(); var17++) {
               if (!((KalamaHelperHelperV)var14.get(var17)).success) {
                  List var16 = var12.subList(var17, var12.size());
                  Tasks.l(() -> MovTasks.scheduleFarawayMoveInternal(var16, false, var11.mS(), true), 1);
                  break;
               }

               ((KalamaHelperHelperV)var14.get(var17)).run();
            }

            mc.player.setPosition(var6);
            mc.player.setPitch(var7);
            mc.player.setYaw(var8);
            MovTasks.Y();
            ClientPlayerAccess.of(var3).setForceNoFall(true);
            var3.setOnGround(false);
            packetEvent.cancel();
         }
      }
   }

   private boolean canTp() {
      return this.bowtpDistance.get() > 0.0;
   }

   public void onRender(Event<MatrixStack> stackE) {
      MatrixStack var2 = (MatrixStack)stackE.b;
      if (this.ae.get() && mc.player != null && this.bowtpRenderTarget.get()) {
         float var3 = (Float)stackE.c[0];
         if (mc.player.isUsingItem() && mc.player.getActiveItem().getItem() instanceof BowItem var5) {
            RenderUtils.startDrawVirtual(var2);

            try {
               TargetSelector var13 = CombatTasks.l();
               Entity var6 = var13.akK(this.bowtpTargetDistance.get(), true, e -> var13.checkWeapon(e, true));
               if (var6 != null) {
                  float var7 = var6.distanceTo(mc.player);
                  float var8 = Math.min(0.6F, 0.1F + var7 * 0.02F);
                  Box var9 = RenderUtils.getLerpedBox(var6, var3);
                  RenderUtils.r(var2, var9.getMinPos(), var9.getMaxPos(), ColorUtils.k(Color.GREEN, var8));
               }
            } finally {
               RenderUtils.stopDrawVirtual(var2);
            }
         }
      }
   }

   public void onBowAction(Event<PlayerActionC2SPacket> packetEvent) {
      if (!packetEvent.d()) {
         PlayerActionC2SPacket var2 = (PlayerActionC2SPacket)packetEvent.e();
         if (var2.getAction() == Action.RELEASE_USE_ITEM
            && this.ae.get()
            && this.canTp()
            && mc.player != null
            && mc.player.getActiveItem().getItem() instanceof BowItem var4) {
            TargetSelector var6 = CombatTasks.l();
            Entity var5 = var6.akK(this.bowtpTargetDistance.get(), true, e -> var6.checkWeapon(e, true));
            if (var5 == null) {
               return;
            }

            Debug.b(
               Text.literal("[Bow Attack] Aim at %s".formatted(var5 instanceof PlayerEntity ? "player " : "entity "))
                  .append(EntityUtils.getEntityDisplayable(var5))
                  .formatted(Formatting.GREEN)
            );
            this.onBowTpAttack(packetEvent, var5);
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
      ModulePreset var2 = (ModulePreset)((KalamaHelperHelperI)event.e()).b();
      switch (var2) {
         case fd:
         case fe:
            if (this.bowtpDistance.get() < 0.0) {
               this.bowtpDistance.set(-this.bowtpDistance.get());
            }
            break;
         default:
            if (this.bowtpDistance.get() > 0.0) {
               this.bowtpDistance.set(-this.bowtpDistance.get());
            }
      }
   }

   public BowTp() {
      super("BowTp");
      this.bowtpEnableHotkey = this.toggleHotkey(this.CZ.add("bowtp-enable-hotkey"), new MultiKeyBind(), this.CZ.add("bowtp-enable")).build();
      this.bowtpDistance = this.builder(this.CZ.add("bowtp-distance"), DoubleRef.TYPE).defaultValue(80.0).build();
      this.bowtpTargetDistance = this.builder(this.CZ.add("bowtp-target-distance"), DoubleRef.TYPE).defaultValue(80.0).build();
      this.bowtpRenderTarget = this.flagBuilder(this.CZ.add("bowtp-render-target")).build();
      this.bowtpStartDeltaY = this.builder(this.CZ.add("bowtp-start-delta-y"), DoubleRef.TYPE)
         .defaultValue(0.0)
         .validator(Configs.doubleRange(-1.0E-7, 100.0))
         .build();
      this.bindFlag(this.ae);
   }

   private double getFireArrowPositionHeight(PlayerEntity player, Entity target) {
      return target.getBoundingBox().getLengthY() - player.getEyeHeight(player.getPose()) + 0.11 + this.bowtpStartDeltaY.get();
   }

   private boolean processExactBowTp(PlayerEntity player, Entity target, Deque<MovTasks$MovInfo> movementStack, Deque<MovTasks$MovInfo> shouldMoveBackStack) {
      PositionPredict var5 = CombatTasks.m();
      double var6 = this.bowtpTargetDistance.get();
      Vec3d var8 = player.getPos();
      Vec3d var9 = var5.getExactAttackPosition(target);
      if (var9 != null) {
         var9 = var9.add(0.0, this.getFireArrowPositionHeight(player, target), 0.0);
         if (RenderTasks.f) {
            RenderTasks.drawBox(player.dimensions.getBoxAt(var9), 150, Color.GREEN);
         }

         List var10 = MovTasks.z(var8, var9, false, 1.5 * var6, true);
         List var11 = MovTasks.z(var9, var8, false, 1.5 * var6, true);
         if ((var10.size() == 2 || var10.size() == 4) && (var11.size() == 2 || var11.size() == 4)) {
            if (var10.size() == 2) {
               movementStack.addLast(MovTasks$MovInfo.adA((Vec3d)var10.get(1)));
            } else {
               movementStack.addLast(MovTasks$MovInfo.adA((Vec3d)var10.get(1)));
               movementStack.addLast(MovTasks$MovInfo.adA((Vec3d)var10.get(2)));
               movementStack.addLast(MovTasks$MovInfo.adA((Vec3d)var10.get(3)));
            }

            int var12 = var11.size();

            for (int var13 = var12 - 2; var13 >= 0; var13--) {
               shouldMoveBackStack.addFirst(MovTasks$MovInfo.adz((Vec3d)var11.get(var13)));
            }

            return true;
         }

         Debug.b("[Bow Attack] Can not reach the target");
      }

      return false;
   }

   private boolean processBowVClip(PlayerEntity player, Entity target, Deque<MovTasks$MovInfo> movementStack, Deque<MovTasks$MovInfo> shouldMoveBackStack) {
      if (this.canTp()) {
         double var5 = this.bowtpDistance.get();
         player.setOnGround(false);
         Vec3d var7 = ((MovTasks$MovInfo)movementStack.peekLast()).vec3d();
         double var8 = target.getY() - var7.y;
         double var10 = MovTasks.searchFirstNoCollisionSpaceYHeight(var7.add(0.0, var5, 0.0), 0.0, var5 - 2.0 - var8, false);
         double var12 = var5 + var10;
         double var14 = 0.0;
         if (var12 - var14 > 0.0) {
            Debug.b(Text.literal("[Bow Attack] Bow Attack Simulation: simulate height %.2f".formatted(var12)).formatted(Formatting.GREEN));
            movementStack.addLast(MovTasks$MovInfo.adB(var7.add(0.0, var12, 0.0)));
            movementStack.addLast(new MovTasks$MovInfo(var7.add(0.0, var14, 0.0), null, false, new Vec2f(89.0F, mc.player.getYaw())));
            return true;
         }
      }

      return false;
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ap().getChannel(PlayerActionC2SPacket.class), this::onBowAction, -999);
      this.registerListener(RenderListener.q(), this::onRender);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
   }
}
