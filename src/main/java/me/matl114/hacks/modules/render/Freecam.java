package me.matl114.hacks.modules.render;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.entity.CameraEntity;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.hacks.utils.move.FlightVelocity;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.collections.KalamaHelperHelperM;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class Freecam extends BaseModule implements HackUtilHelperJ {
   CameraEntity eo;
   CameraEntity en;
   private static HackUtilHelperD instance;
   public final FlagRef enable;
   public final ModulePath ek = makePath(Configs.i, "freecam");
   public final DoubleRef speed;
   PlayerInputUtils$Input ep;
   public final KeyBindRef el;

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      if (this.en != null) {
         ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
         PlayerInputUtils$Input var3 = PlayerInputUtils.of(mc.options);
         this.ep = var3;
         Vec3d var4 = new Vec3d(var3.rp(), var3.rq(), var3.ro());
         Vec3d var5 = EntityUtils.movementInputToVelocity(var4, (float)this.speed.get(), this.en.getYaw());
         this.en.setVelocity(var5);
         PlayerInputUtils.a.rs(mc.options);
         var2.setSneaking(var3.rJ());
      }
   }

   public void y(Event<World> event) {
      if (this.enable.get()) {
         Tasks.l(this::initializeCamera, 1);
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.N(), this::y);
      this.registerListener(Listener.aA(), this::onPosResync);
      this.registerListener(Listener.V(), this::onTick);
      this.registerListener(Listener.ap().getChannel(PlayerInteractEntityC2SPacket.class), this::onStopInteractWithSelf);
      this.registerListener(Listener.aI(), this::iz);
      this.registerListener(Listener.bx().c(FlightVelocity.class), this::iA);
   }

   public void onTick(Event<ClientPlayerEntity> event) {
      if (this.en == null || mc.world == null) {
         ;
      }
   }

   public Freecam() {
      super("Freecam");
      this.enable = this.flagBuilder(this.ek.add("enable")).build();
      this.el = this.toggleHotkey(Configs.i, this.ek.add("enable-hotkey").toPath(), new MultiKeyBind(85), this.ek.add("enable").toPath()).build();
      this.speed = this.builder(this.ek.add("speed"), DoubleRef.TYPE).defaultValue(1.0).validator(Configs.doubleRange(0.0, 100000.0)).build();
      this.bindFlag(this.enable);
      if (instance == null) {
         instance = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> instance);
      }

      instance.mN(this::cast);
   }

   public void onPosResync(Event<MovTasks$MovInfo> resync) {
      if (this.en != null) {
         MovTasks$MovInfo var2 = (MovTasks$MovInfo)resync.e();
         Vec3d var3 = var2.vec3d();
         Vec2f var4 = var2.rotationOverride();
         if (var3 != null && this.en.getPos().squaredDistanceTo(var3) > MathUtils.b(100)) {
            this.en.setPosition(var3);
         }
      }
   }

   public void iA(Event<KalamaHelperHelperI<FlightVelocity>> event) {
   }

   public void applyAfterInputTick(Event<LegalMovementManager> movementManagerEvent) {
      if (this.ep != null) {
         this.ep.rs(mc.options);
         PlayerInputUtils.a.rQ(this.ep.rJ()).rD(this.ep.rK()).applyInput(mc.player);
         this.ep = null;
      }
   }

   @Override
   public int priority() {
      return -100000;
   }

   @Override
   public void onEnableModule() {
      super.onEnableModule();
      this.initializeCamera();
   }

   public void removeCamera() {
      if (this.en != null) {
         this.en.remove(RemovalReason.DISCARDED);
      }

      if (this.eo != null) {
         this.eo.remove(RemovalReason.DISCARDED);
      }

      if (mc.getCameraEntity() == this.en) {
         mc.setCameraEntity(mc.player);
      }

      this.en = null;
      this.eo = null;
   }

   public void initializeCamera() {
      this.removeCamera();
      if (mc.player != null) {
         this.en = new CameraEntity(mc.world, mc.player, GameMode.SPECTATOR, true);
         this.eo = new CameraEntity(mc.world, mc.player, GameMode.CREATIVE, false);
         mc.world.addEntity(this.en);
         mc.world.addEntity(this.eo);
         mc.setCameraEntity(this.en);
      }
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      this.removeCamera();
   }

   public void iz(Event<KalamaHelperHelperM> event) {
      if (this.en != null) {
         this.en.changeLookDirection(((KalamaHelperHelperM)event.b).a, ((KalamaHelperHelperM)event.b).b);
         event.cancel();
      }
   }

   public void applyBeforeInputPacketModify(Event<LegalMovementManager> movementManagerEvent) {
      if (this.en != null && mc.getCameraEntity() == this.en) {
         mc.setCameraEntity(((LegalMovementManager)movementManagerEvent.b).c.a);
      }
   }

   public void onStopInteractWithSelf(Event<PlayerInteractEntityC2SPacket> packet) {
      if (this.en != null) {
         PlayerInteractEntityC2SPacket var2 = (PlayerInteractEntityC2SPacket)packet.e();
         if (this.eo != null && var2.entityId == this.eo.getId()) {
            packet.cancel();
            return;
         }

         if (mc.player != null && var2.entityId == mc.player.getId()) {
            packet.cancel();
            return;
         }
      }
   }

   @Override
   public void iC(Event<LegalMovementManager> movementManagerEvent) {
      if (this.en != null && mc.getCameraEntity() == this.en) {
         mc.setCameraEntity(((LegalMovementManager)movementManagerEvent.b).c.a);
      }
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (this.en != null && mc.getCameraEntity() == ((LegalMovementManager)movementManagerEvent.b).c.a) {
         mc.setCameraEntity(this.en);
      }

      return true;
   }
}
