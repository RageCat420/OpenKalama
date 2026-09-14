package me.matl114.hacks.modules.move;

import java.util.EnumMap;
import java.util.function.Predicate;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ItemStackUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3d;

public class NoFall extends BaseModule implements HackUtilHelperJ {
   public final FlagRef toggle;
   double UF;
   EnumMap<NoFall$Mode, HackUtilHelperJ> UH;
   public final KeyBindRef J;
   private static final int UB = 1;
   public final NBTRef<Regex> equipmentIdBypassNofall;
   public final ModulePath gQ = makePath(Configs.m, "move-safety");
   boolean holdingMace;
   double Uz;
   public final EnumRef<NoFall$Mode> bypassMode;
   public final IntRef safeDistanceModify;
   public final ModulePath Uq = this.gQ.add("no-fall");
   public static final double DELTA_Y = 9.0E-8;
   int entityStage;
   private static final int UD = 3;
   MoveSubHelperY UG;
   private static final int UC = 2;
   public final FlagRef disableWhenAllowFlying;
   double Uw;
   public static HackUtilHelperD instance;
   double Ux;
   private static final int UA = 0;

   public void onVcUpdate(Event<Vec3d> tickEvent) {
      if (tickEvent.getArgs(0) instanceof ClientPlayerEntity var3 && var3 == mc.player) {
         this.<MoveSubHelperY>getDelegate().jy(tickEvent);
      }
   }

   protected void ji(Event<MovTasks$MovInfo> setBackEvent) {
      this.<MoveSubHelperY>getDelegate().ji(setBackEvent);
   }

   @Override
   public void iC(Event<LegalMovementManager> movementManagerEvent) {
      if (this.entityStage == 1) {
         this.UG.iC(movementManagerEvent);
      }
   }

   private void amb() {
      this.UH.clear();
      this.UH.put(NoFall$Mode.NO_BYPASS, new MoveSubHelperAc(this));
      this.UH.put(NoFall$Mode.LAZY_MODE, new MoveSubHelperW(this));
      this.UH.put(NoFall$Mode.BYPASS_GRIM, new MoveSubHelperS(this));
      this.UH.put(NoFall$Mode.LAZY_BYPASS_GRIM, new MoveSubHelperG(this));
      this.UH.put(NoFall$Mode.LAZY_GRIM_PLUS, new MoveSubHelperA(this));
      this.UH.put(NoFall$Mode.LAZY_GRIM_PLUS_2, new MoveSubHelperZ(this));
      this.UH.put(NoFall$Mode.DUP_FULL_FAKE_GROUND, new MoveSubHelperEX(this));
      this.UH.put(NoFall$Mode.TEST, new MoveSubHelperN(this));
      this.UH.put(NoFall$Mode.TEST2, new MoveSubHelperAq(this));
   }

   public boolean amh() {
      return this.Ux <= this.Uw - this.UF;
   }

   public static MoveSubHelperV applyInputWay(ClientPlayerEntity player) {
      double var1 = 0.2;
      float var3 = player.getYaw();
      Vec3d var4 = Vec3d.fromPolar(0.0F, var3).multiply(var1);
      Vec3d var5 = var4.negate();
      Vec3d var6 = Vec3d.fromPolar(0.0F, var3 + 90.0F).multiply(-var1);
      Vec3d var7 = var6.negate();
      boolean var8 = MovTasks.hasHorizontalCollision(player, var4);
      boolean var9 = MovTasks.hasHorizontalCollision(player, var5);
      boolean var10 = MovTasks.hasHorizontalCollision(player, var6);
      boolean var11 = MovTasks.hasHorizontalCollision(player, var7);
      return new MoveSubHelperV(var8, var9, var10, var11);
   }

   protected <T extends MoveSubHelperY> T getDelegate() {
      HackUtilHelperJ var1 = this.UH.get(this.bypassMode.get());
      return (T)(var1 == null ? this.UH.get(NoFall$Mode.LAZY_MODE) : var1);
   }

   public boolean checkInvulnerableEquipment() {
      Predicate var1 = this.equipmentIdBypassNofall.get().asPredicate();

      for (EquipmentSlot var5 : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
         ItemStack var6 = mc.player.getEquippedStack(var5);
         if (!var6.isEmpty()) {
            String var7 = Registries.ITEM.getId(var6.getItem()).getPath();
            if (var1.test(var7)) {
               return true;
            }

            String var8 = ItemStackUtils.aa(var6);
            if (var8 != null && var1.test(var8)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public void onCreate() {
      super.onCreate();
      this.amc();
      this.amb();
   }

   public void RA(Event<ClientPlayerEntity> player) {
      this.amc();
      this.amb();
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.aA(), this::ji);
      this.registerListener(Listener.aN().c(EntityType.PLAYER), this::Zm);
      this.registerListener(Listener.aE(), this::RA);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::le);
      this.registerListener(Listener.aN().c(EntityType.PLAYER), this::onVcUpdate);
      this.registerListener(Listener.ap().getChannel(PlayerMoveC2SPacket.class), this::onPlayerMovePacketSend);
      this.registerListener(Listener.aw(), this::amf);
      this.registerListener(Listener.ay(), this::onWeb);
   }

   public void onPlayerMovePacketSend(Event<PlayerMoveC2SPacket> movePacket) {
      PlayerMoveC2SPacket var2 = (PlayerMoveC2SPacket)movePacket.e();
      if (var2.changesPosition()) {
         this.Uz = var2.getY(0.0);
      }
   }

   public void le(Event<KalamaHelperHelperI<ModulePreset>> presetEvent) {
      ModulePreset var2 = (ModulePreset)((KalamaHelperHelperI)presetEvent.e()).b();
      switch (var2) {
         case fg:
         case fh:
            if (this.bypassMode.get() != NoFall$Mode.LAZY_GRIM_PLUS) {
               this.bypassMode.set(NoFall$Mode.LAZY_GRIM_PLUS);
            }
            break;
         default:
            this.bypassMode.set(NoFall$Mode.LAZY_MODE);
      }
   }

   private void amc() {
      this.Uw = -2.1474836E9F;
      this.Ux = 0.0;
   }

   protected void Zm(Event<Vec3d> vc) {
      if (vc.getArgs(0) == mc.player && this.getDelegate() instanceof MoveSubHelperG var3) {
         var3.onVelocity(vc);
      }
   }

   public void amf(Event<Integer> jumpEvent) {
      this.<MoveSubHelperY>getDelegate().AH(jumpEvent);
   }

   @Override
   public int priority() {
      return 2147483646;
   }

   public void setLastOnGroundHeight(double lastOnGroundHeight) {
      this.Uw = lastOnGroundHeight;
   }

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      this.UG = this.getDelegate();
      ClientPlayerEntity var2 = ((LegalMovementManager)movementManagerEvent.b).c.a;
      Vec3d var3 = var2.getPos();
      if (var3 == null) {
         this.entityStage = 0;
      } else if (var2.getAbilities().invulnerable || this.disableWhenAllowFlying.get() && MovTasks.at().hB || this.checkInvulnerableEquipment()) {
         this.entityStage = 3;
      } else if (!var2.isAlive()) {
         this.entityStage = 2;
      } else {
         this.entityStage = 1;
         this.holdingMace = var2.getMainHandStack().getItem() instanceof MaceItem;
         this.Ux = var2.getY();
         this.UF = var2.getAttributeValue(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE) + this.safeDistanceModify.get();
         if (var2.isOnGround()
            || var2.isTouchingWater()
            || var2.getBlockStateAtPos().isOf(Blocks.BUBBLE_COLUMN)
            || PlayerStateManager.INSTANCE.jE
            || PlayerStateManager.INSTANCE.jz) {
            this.Uw = this.Ux;
            this.UG.counter = 0;
         } else if (this.Ux > this.Uw) {
            this.Uw = this.Ux;
            this.UG.counter = 0;
         }

         this.UG.bb(movementManagerEvent);
      }
   }

   public NoFall() {
      super("NoFall");
      this.toggle = this.flagBuilder(this.Uq.add("toggle")).build();
      this.J = this.moduleEntry(this.Uq.addHotkey(), new MultiKeyBind(), this.Uq.add("toggle"), moduleMeta(() -> this.bypassMode)).build();
      this.bypassMode = this.builder(this.Uq.add("bypass-mode"), NoFall$Mode.class).defaultValue(NoFall$Mode.LAZY_MODE).build();
      this.safeDistanceModify = this.intBuilder(this.Uq.add("safe-distance-modify")).defaultValue(0).build();
      this.equipmentIdBypassNofall = this.builder(this.Uq.add("equipment-id-bypass-nofall"), Regex.class).defaultValue(new Regex("^(SLIME.*_BOOTS)$")).build();
      this.disableWhenAllowFlying = this.builder(this.Uq.add("disable-when-allow-flying"), Boolean.class).defaultValue(true).build();
      this.Uw = -2.1474836E9F;
      this.holdingMace = false;
      this.Uz = 0.0;
      this.entityStage = 0;
      this.UF = 0.0;
      this.UG = null;
      this.UH = new EnumMap<>(NoFall$Mode.class);
      if (instance == null) {
         instance = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> instance);
      }

      instance.mN(this::cast);
      this.bindFlag(this.toggle);
   }

   @Override
   public void gz(Event<LegalMovementManager> movementManagerEvent) {
      if (this.entityStage == 1) {
         this.UG.gz(movementManagerEvent);
      }
   }

   public void onWeb(Event<Vec3d> vec3d) {
      this.Uw = mc.player.getY();
      if (this.UG != null) {
         this.UG.counter = 0;
      }
   }

   public void applyBeforeTravelTick(Event<LegalMovementManager> movementManagerEvent, Event<Vec3d> moveEvent) {
      if (this.entityStage == 1) {
         this.UG.jI(movementManagerEvent, moveEvent);
      }
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (this.entityStage == 1) {
         this.UG.postModify(movementManagerEvent, enabledThisTick);
         this.UG.Md = false;
      }

      return true;
   }
}
