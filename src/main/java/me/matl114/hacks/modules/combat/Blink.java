package me.matl114.hacks.modules.combat;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.util.OptionalInt;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.KalamaHelperHelperG;
import me.matl114.events.Listener;
import me.matl114.events.PacketManager;
import me.matl114.events.RenderListener;
import me.matl114.events.packets.PacketStorage;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.entity.EntityMovementStatus;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class Blink extends BaseModule {
   public final IntRef closeDelay;
   int wb;
   public final EnumRef<Blink$Action> onHurtBehaviour;
   public final EnumRef<Blink$Action> onVelocityBehaviour;
   public final EnumRef<Blink$Action> onNearTarget;
   public final KeyBindRef hotkey;
   public final EnumRef<Blink$Action> onEnermyNear;
   public final FlagRef enable;
   public EntityMovementStatus<ClientPlayerEntity> wa;
   boolean wd;
   boolean we;
   public final IntRef autoFlushPeriod;
   public final EnumRef<Blink$Action> attackBehaviour;
   public final KeyBindRef revert;
   public final ModulePath vL;
   public final EnumRef<Blink$Action> onInventoryBehaviour;
   int wc;
   public final FlagRef autoFlush;
   public final ModulePath pd = makePath(Configs.k, "lag-utils");
   public final EnumRef<Blink$Action> onTotemBehaviour;
   public final DoubleRef enermyNearRange;
   public final FlagRef closeOnDelay;
   public final FlagRef elytraSupport;
   public final FlagRef render;

   public void onFireworkOwner(Event<SerializedEntry<?>> firework) {
      if (this.enable.get()
         && this.elytraSupport.get()
         && ((SerializedEntry)firework.e()).id() == 9
         && firework.getArgs(0) instanceof FireworkRocketEntity var3
         && mc.player != null
         && mc.player.isFallFlying()
         && ((SerializedEntry)firework.e()).value() instanceof OptionalInt var4
         && var4.isPresent()
         && var4.getAsInt() == mc.player.getId()) {
         this.flush();
      }
   }

   public void flush() {
      this.wc = Tasks.b();
      PacketManager.flushOutBound();
      if (mc.player != null) {
         this.wa = new EntityMovementStatus(mc.player);
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(PacketManager.z().c(NetworkSide.SERVERBOUND), this::onPacketQueue);
      this.registerListener(PacketManager.A(), this::GT);
      this.registerListener(Listener.T(), this::onTick);
      this.registerListener(Listener.O(), this::z);
      this.registerListener(Listener.ap().getChannel(EntityDamageS2CPacket.class), this::onPacketHurt);
      this.registerListener(Listener.ap().getChannel(EntityVelocityUpdateS2CPacket.class), this::onPacketVelocity);
      this.registerListener(RenderListener.q(), this::onRender);
      this.registerListener(Listener.au().c(EntityType.FIREWORK_ROCKET), this::onFireworkOwner);
      this.registerListener(Listener.ap().getChannel(EntityStatusS2CPacket.class), this::onEntityStatus);
   }

   public void onTick(Event<Void> tickEvent) {
      if (this.enable.get()) {
         if (mc.player != null && this.wa == null) {
            this.wa = new EntityMovementStatus(mc.player);
         }

         if (mc.player != null && this.autoFlush.get() && this.wc + this.autoFlushPeriod.get() < Tasks.b()) {
            Debug.b("[Blink] Auto flush");
            this.flush();
         }

         if (this.closeOnDelay.get() && Tasks.b() > this.wb + this.closeDelay.get()) {
            this.enable.set(false);
         }

         if (this.onEnermyNear.get() != Blink$Action.NONE && this.wa != null) {
            boolean var2 = false;
            ImmutableList var3 = ImmutableList.copyOf(mc.world.getEntities());
            Vec3d var4 = this.wa.g;
            Vec3d var5 = var4.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0);

            for (Entity var7 : var3) {
               if (var7.getBoundingBox().squaredMagnitude(var5) < MathUtils.a(this.enermyNearRange.get()) && TargetSelector.INSTANCE.canAttack(var7)) {
                  var2 = true;
                  break;
               }
            }

            if (var2) {
               this.GW(this.onEnermyNear.get());
            }
         }

         if (this.onNearTarget.get() != Blink$Action.NONE) {
            Entity var8 = TargetSelector.INSTANCE.akJ(CombatExtra.INSTANCE.getAttackRange(), true, 1);
            if (var8 != null) {
               this.GW(this.onNearTarget.get());
            }
         }
      }
   }

   public Blink() {
      super("Blink");
      this.vL = this.pd.add("blink");
      this.enable = this.flagBuilder(this.vL.add("enable")).build();
      this.hotkey = this.toggleHotkey(this.vL.add("hotkey"), new MultiKeyBind(), this.vL.add("enable")).build();
      this.revert = this.hotkey(this.vL.add("revert")).defaultValue(new MultiKeyBind()).registerHotkey(HotKeyUtils.b(this::GU)).build();
      this.render = this.flagBuilder(this.vL.add("render")).build();
      this.closeOnDelay = this.flagBuilder(this.vL.add("close-on-delay")).build();
      this.closeDelay = this.intBuilder(this.vL.add("close-delay")).defaultValue(50).build();
      this.autoFlush = this.flagBuilder(this.vL.add("auto-flush")).build();
      this.autoFlushPeriod = this.intBuilder(this.vL.add("auto-flush-period")).defaultValue(20).build();
      this.attackBehaviour = this.builder(this.vL.add("attack-behaviour"), Blink$Action.class).defaultValue(Blink$Action.FLUSH).build();
      this.onHurtBehaviour = this.builder(this.vL.add("on-hurt-behaviour"), Blink$Action.class).defaultValue(Blink$Action.NONE).build();
      this.onVelocityBehaviour = this.builder(this.vL.add("on-velocity-behaviour"), Blink$Action.class).defaultValue(Blink$Action.NONE).build();
      this.onInventoryBehaviour = this.builder(this.vL.add("on-inventory-behaviour"), Blink$Action.class).defaultValue(Blink$Action.NONE).build();
      this.onTotemBehaviour = this.builder(this.vL.add("on-totem-behaviour"), Blink$Action.class).defaultValue(Blink$Action.NONE).build();
      this.onEnermyNear = this.builder(this.vL.add("on-enermy-near"), Blink$Action.class).defaultValue(Blink$Action.NONE).build();
      this.enermyNearRange = this.builder(this.vL.add("enermy-near-range"), DoubleRef.TYPE).defaultValue(3.5).build();
      this.onNearTarget = this.builder(this.vL.add("on-near-target"), Blink$Action.class).defaultValue(Blink$Action.NONE).build();
      this.elytraSupport = this.flagBuilder(this.vL.add("elytra-support")).build();
      this.wb = 0;
      this.wc = 0;
      this.wd = false;
      this.we = false;
      this.bindFlag(this.enable);
   }

   public void z(Event<Void> disconnect) {
      this.enable.set(false);
   }

   public void GW(Blink$Action action) {
      switch (action) {
         case FLUSH:
            this.flush();
            break;
         case CLOSE:
            this.enable.set(false);
      }
   }

   public void onPacketHurt(Event<EntityDamageS2CPacket> damage) {
      if (this.enable.get() && mc.player != null && ((EntityDamageS2CPacket)damage.b).entityId() == mc.player.getId()) {
         this.GW(this.onHurtBehaviour.get());
      }
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      this.flush();
      this.wa = null;
      if (!checkNull()) {
         Debug.b("[Blink] Disable and flush");
      }
   }

   @Override
   public void onEnableModule() {
      super.onEnableModule();
      this.wb = Tasks.b();
      this.wa = null;
   }

   public void GU() {
      if (!checkNull()) {
         if (this.enable.get() && this.wa != null) {
            Debug.b("[Blink] Start revert");

            try {
               MutableBoolean var1 = new MutableBoolean(false);
               this.wc = Tasks.b();
               PacketManager.n(
                  packets -> {
                     PacketType var2 = packets.packetType();
                     if (var2 != PlayPackets.MOVE_PLAYER_POS
                        && var2 != PlayPackets.MOVE_PLAYER_ROT
                        && var2 != PlayPackets.MOVE_PLAYER_POS_ROT
                        && var2 != PlayPackets.MOVE_PLAYER_STATUS_ONLY) {
                        if (var2 == PlayPackets.PLAYER_INPUT) {
                           return KalamaHelperHelperG.NT;
                        } else if (var2 == PlayPackets.ACCEPT_TELEPORTATION) {
                           var1.setTrue();
                           return KalamaHelperHelperG.NU;
                        } else {
                           return KalamaHelperHelperG.NU;
                        }
                     } else {
                        return var1.booleanValue() ? KalamaHelperHelperG.NU : KalamaHelperHelperG.NT;
                     }
                  }
               );
            } finally {
               ClientPlayerAccess.of(mc.player).resyncInput();
               this.wa.restore();
            }
         }
      }
   }

   public void onRender(Event<MatrixStack> eve) {
      if (this.enable.get() && this.render.get()) {
         MatrixStack var2 = (MatrixStack)eve.e();
         if (this.wa != null) {
            RenderUtils.startDrawVirtual(var2);

            try {
               Box var3 = this.wa.a.dimensions.getBoxAt(this.wa.g);
               RenderUtils.drawOutlinedBox(var2, var3.getMinPos(), var3.getMaxPos(), Color.MAGENTA);
            } finally {
               RenderUtils.stopDrawVirtual(var2);
            }
         }
      }
   }

   public void GT(Event<Void> eve) {
      if (this.enable.get()) {
         this.enable.set(false);
      }
   }

   public void handleQueueAction(Event<?> packet, Blink$Action action) {
      switch (action) {
         case FLUSH:
            this.flush();
            break;
         case CLOSE:
            this.enable.set(false);
            break;
         default:
            packet.cancel();
      }
   }

   public void onPacketVelocity(Event<EntityVelocityUpdateS2CPacket> event) {
      if (this.enable.get() && mc.player != null && ((EntityVelocityUpdateS2CPacket)event.b).getEntityId() == mc.player.getId() && !event.d()) {
         this.GW(this.onVelocityBehaviour.get());
      }
   }

   public void onPacketQueue(Event<PacketStorage> packet) {
      if (this.enable.get()) {
         PacketStorage var2 = (PacketStorage)packet.b;
         PacketType var3 = var2.packetType();
         if (PacketManager.isAsyncOrNotTransactionC2SPacket(var3)) {
            return;
         }

         if (this.onInventoryBehaviour.get() != Blink$Action.NONE
            && (!this.elytraSupport.get() || !mc.player.isFallFlying())
            && PacketManager.isInventoryPacket(var3)) {
            return;
         }

         if (var3 == PlayPackets.USE_ITEM && mc.player.isFallFlying() && this.onFireworkUse()) {
            return;
         }

         if (var3 == PlayPackets.INTERACT) {
            this.handleQueueAction(packet, this.attackBehaviour.get());
            this.wd = true;
         } else if (var3 == PlayPackets.SWING && this.wd) {
            this.wd = false;
            this.handleQueueAction(packet, this.attackBehaviour.get());
         } else if (var3 == PlayPackets.ACCEPT_TELEPORTATION) {
            this.flush();
         } else {
            packet.cancel();
         }
      }
   }

   public void onEntityStatus(Event<EntityStatusS2CPacket> eventTotem) {
      if (!checkNull()) {
         if (this.enable.get()
            && ((EntityStatusS2CPacket)eventTotem.b).getEntity(mc.world) == mc.player
            && ((EntityStatusS2CPacket)eventTotem.b).getStatus() == 35) {
            this.GW(this.onTotemBehaviour.get());
         }
      }
   }

   public boolean onFireworkUse() {
      if (this.enable.get() && this.elytraSupport.get()) {
         this.flush();
         return true;
      } else {
         return false;
      }
   }
}
