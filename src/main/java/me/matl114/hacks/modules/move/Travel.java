package me.matl114.hacks.modules.move;

import java.util.Optional;
import java.util.function.Consumer;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.commands.MainCommand;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.KalamaHelperHelperUX;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.hacks.utils.move.FlightVelocity;
import me.matl114.managers.Configs;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperD;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TaskSubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.types.ExecutePos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

public class Travel extends BaseModule implements HackUtilHelperJ {
   public DoubleRef pitch40NegativeDelta;
   public NBTRef<OptionalPrimitive<Double>> pitch40HeightLimit;
   public IntRef void2DupPacket;
   public IntRef pitch40PitchNegative;
   public static MoveSubHelperSX CW;
   public EnumRef<TravellingControl$Type> controlType;
   boolean CV;
   static HackUtilHelperD cy;
   public final ModulePath iN = makePath(Configs.m, "travelling-control");
   public FlagRef pitch40EndSafety2;
   private MoveSubHelperAh CG = v -> true;
   public FlagRef pitch40EndSafety;
   public IntRef pitch40PitchPositive;
   public FlagRef pitch40EndKick;
   public FlagRef clearTargetWhenExit;
   public KeyBindRef J = this.hotkey(this.iN.add("toggle-auto-speed")).defaultValue(new MultiKeyBind()).registerHotkey(HotKeyUtils.b(this::QO)).build();
   public DoubleRef elytraSpeed;
   public FlagRef pitch40AutoPullUp;
   public DoubleRef speed;
   public IntRef minHeight;
   public IntRef maxHeight;

   private void QI(Event<PlayerPositionLookS2CPacket> eventPosition) {
      if (this.CG instanceof MoveSubHelperP var3) {
         var3.QI(eventPosition);
      }
   }

   public void QN(PlayerEntity var1, Vec3d parsedCoord) {
      this.QL();
      if (CW != null) {
         Debug.b("上一个travel task仍旧在执行,自动取消中");
         this.QZ();
      }

      if (parsedCoord != null) {
         this.QQ(Optional.of(parsedCoord));
         if (CW == null) {
         }
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerCommandBootstrap(this::QK);
      this.registerListener(Listener.bx().c(FlightVelocity.class), this::QY);
      this.registerListener(Listener.aq().getChannel(EnterReconfigurationS2CPacket.class), this::QJ);
      this.registerListener(Listener.aq().getChannel(PlayerPositionLookS2CPacket.class), this::QI);
      this.registerListener(Listener.S(), this::aO);
   }

   public boolean QW(MoveSubHelperSX ti) {
      if (CW == ti && ti.f == this && !(mc.player.getPos().subtract(ti.getCurrentFlyingTarget()).horizontalLengthSquared() < 900.0)) {
         return false;
      } else {
         this.QR(ti);
         if (mc.player != null) {
            mc.player.setOnGround(false);
            ClientPlayerAccess.of(mc.player).setForceNoFall(true);
         }

         CW = null;
         MovTasks.doingTp = false;
         return true;
      }
   }

   public void QO() {
      if (!checkNull()) {
         this.QL();
         if (CW == null) {
            Debug.b("启动Auto飞行模式");
            this.QQ(Optional.empty());
         } else {
            Debug.b("上一个travel task仍旧在执行,自动取消中...");
            this.QZ();
         }
      }
   }

   private boolean QU(MoveSubHelperSX ti) {
      if (ti != null && ti.h) {
         return false;
      } else if (checkNull()) {
         if (this.clearTargetWhenExit.get()) {
            this.QS(ti);
            return true;
         } else {
            this.QT(ti);
            return true;
         }
      } else if (ti != null && !ti.e && ti.f == this) {
         return false;
      } else {
         this.QS(ti);
         return true;
      }
   }

   public void QP(CommandExecution var1) {
      this.QL();
      if (CW == null) {
         this.QQ(Optional.empty());
      } else {
         Debug.b("上一个travel task仍旧在执行,自动取消中...");
      }
   }

   private void QY(Event<KalamaHelperHelperI<FlightVelocity>> event) {
      if (this.CG instanceof MoveSubHelperX var3) {
         var3.onElytra(event);
      }
   }

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      acceptor.accept(this.createTitleLabel("widget.travelling-control.command", 0, dblank, dx, dy));
   }

   public boolean QM(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
      ExecutePos var4 = streamArgs.f();
      if (var4 != null) {
         Vector3d var5 = var4.wu(var1);
         this.QN(var1.si(), new Vec3d(var5.x, var5.y, var5.z));
      }

      return true;
   }

   public void QX(MoveSubHelperSX ti, double currentY) {
      switch (ti.d) {
         case mt:
            if (currentY > this.maxHeight.get()) {
               ti.d = MoveSubHelperOX.ms;
            }
            break;
         case mr:
            if (currentY < this.maxHeight.get()) {
               ti.d = MoveSubHelperOX.mt;
            }
            break;
         case ms:
            if (currentY < this.minHeight.get()) {
               ti.d = MoveSubHelperOX.mr;
            } else if (currentY > this.maxHeight.get()) {
               ti.d = MoveSubHelperOX.ms;
            }
      }
   }

   private void QT(MoveSubHelperSX info) {
      if (info == null) {
         this.QS(info);
      } else {
         info.h = true;
         this.CG.onStop();
      }
   }

   public void QK(MainCommand mainCommand) {
      TreeSubCommand var2 = mainCommand.bC().a("travel").k();
      var2.subBuilder(SubCommand.bp())
         .u("travel")
         .z(
            s -> s.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
               .u("to")
               .x("message.command.travel.travel.to.help")
               .A(KalamaHelperHelperA.<KalamaHelperHelperUX, ExecutePos>b(KalamaHelperHelperUX::new).B("target").v())
               .z(e -> e.executor(this::QM))
               .r()
               .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
               .u("auto")
               .x("message.command.travel.travel.auto.help")
               .z(e -> e.executor(KalamaHelperHelperH.l(this::QP)))
               .r()
               .subBuilder(SubCommand.bo())
               .u("cancel")
               .x("message.command.travel.travel.cancel.help")
               .z(e -> e.executor(KalamaHelperHelperH.g(this::QZ)))
               .r()
         )
         .r();
   }

   public Travel() {
      super("Travel");
      this.controlType = this.builder(this.iN.add("control-type"), TravellingControl$Type.class)
         .defaultValue(TravellingControl$Type.MOV_VOID)
         .updateListener(tt -> {
            if (this.CG == null || this.CG.getType() != tt) {
               if (this.CG != null) {
                  this.CG.onStop();
               }

               this.CG = this.QH(tt);
            }
         })
         .build();
      this.speed = this.builder(this.iN.add("speed"), DoubleRef.TYPE)
         .show(() -> this.controlType.get().isIn(new ConfigEnum[]{TravellingControl$Type.MOV_VOID, TravellingControl$Type.MOV_VOID_2}))
         .defaultValue(9.9)
         .build();
      this.elytraSpeed = this.builder(this.iN.add("elytra-speed"), DoubleRef.TYPE)
         .defaultValue(1.7)
         .show(() -> this.controlType.get().isIn(new ConfigEnum[]{TravellingControl$Type.ELYTRASKY}))
         .build();
      this.minHeight = this.builder(this.iN.add("min-height"), IntRef.TYPE).defaultValue(256).build();
      this.maxHeight = this.builder(this.iN.add("max-height"), IntRef.TYPE).defaultValue(400).build();
      this.void2DupPacket = this.builder(this.iN.add("void-2-dup-packet"), IntRef.TYPE)
         .defaultValue(4)
         .validator(Configs.e)
         .show(() -> this.controlType.get().isIn(new ConfigEnum[]{TravellingControl$Type.MOV_VOID_2}))
         .build();
      this.pitch40EndSafety = this.flagBuilder(this.iN.add("pitch-40-end-safety"))
         .show(() -> this.controlType.get().isIn(new ConfigEnum[]{TravellingControl$Type.ELYTRA_PITCH40, TravellingControl$Type.ELYTRA_GRIM_FLY40}))
         .build();
      this.pitch40EndKick = this.flagBuilder(this.iN.add("pitch-40-end-kick"))
         .show(() -> this.controlType.get().isIn(new ConfigEnum[]{TravellingControl$Type.ELYTRA_PITCH40, TravellingControl$Type.ELYTRA_GRIM_FLY40}))
         .build();
      this.pitch40EndSafety2 = this.flagBuilder(this.iN.add("pitch-40-end-safety-2"))
         .show(() -> this.controlType.get().isIn(new ConfigEnum[]{TravellingControl$Type.ELYTRA_PITCH40, TravellingControl$Type.ELYTRA_GRIM_FLY40}))
         .build();
      this.pitch40AutoPullUp = this.flagBuilder(this.iN.add("pitch-40-auto-pull-up"))
         .show(() -> this.controlType.get().isIn(new ConfigEnum[]{TravellingControl$Type.ELYTRA_PITCH40, TravellingControl$Type.ELYTRA_GRIM_FLY40}))
         .build();
      this.pitch40PitchPositive = this.builder(this.iN.add("pitch-40-pitch-positive"), IntRef.TYPE)
         .defaultValue(15)
         .validator(Configs.e)
         .show(() -> this.controlType.get().isIn(new ConfigEnum[]{TravellingControl$Type.ELYTRA_PITCH40, TravellingControl$Type.ELYTRA_GRIM_FLY40}))
         .build();
      this.pitch40PitchNegative = this.builder(this.iN.add("pitch-40-pitch-negative"), IntRef.TYPE)
         .defaultValue(60)
         .validator(Configs.e)
         .show(() -> this.controlType.get().isIn(new ConfigEnum[]{TravellingControl$Type.ELYTRA_PITCH40, TravellingControl$Type.ELYTRA_GRIM_FLY40}))
         .build();
      this.pitch40NegativeDelta = this.builder(this.iN.add("pitch-40-negative-delta"), DoubleRef.TYPE)
         .defaultValue(0.0)
         .validator(Configs.doubleRange(0.0, 90.0))
         .show(() -> this.controlType.get().isIn(new ConfigEnum[]{TravellingControl$Type.ELYTRA_PITCH40, TravellingControl$Type.ELYTRA_GRIM_FLY40}))
         .build();
      this.pitch40HeightLimit = this.builder(this.iN.add("pitch-40-height-limit"), OptionalPrimitive.DOUBLE_TYPE)
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.e, 900.0))
         .show(() -> this.controlType.get().isIn(new ConfigEnum[]{TravellingControl$Type.ELYTRA_PITCH40, TravellingControl$Type.ELYTRA_GRIM_FLY40}))
         .build();
      this.clearTargetWhenExit = this.flagBuilder(this.iN.add("clear-target-when-exit")).build();
      this.CV = false;
      if (cy == null) {
         cy = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> cy);
      }

      cy.mN(this::cast);
   }

   public void QQ(Optional<Vec3d> traget) {
      TravellingControl$Type var2 = this.controlType.get();
      Debug.b("当前运动类型: " + var2.resultAsString().getString());
      MoveSubHelperSX var3 = new MoveSubHelperSX();
      var3.a = traget;
      var3.onStart(this, mc.player.getPos());
      CW = var3;
      double var4 = mc.player.getY();
      if (var4 < this.minHeight.get()) {
         var3.d = MoveSubHelperOX.mr;
      } else if (var4 > this.maxHeight.get()) {
         var3.d = MoveSubHelperOX.ms;
      } else {
         var3.d = MoveSubHelperOX.mt;
      }

      if (this.CG != null) {
         this.CG.onStart(var3);
      } else {
         CW.e = true;
         CW = null;
      }
   }

   public boolean QV(MoveSubHelperSX ti, Vec3d delta) {
      if (delta.length() == 0.0) {
         MovTasks.moveToWithPackets(mc.player.getPos(), null);
         return false;
      } else {
         MovTasks.moveToWithPackets(mc.player.getPos().add(delta), false);
         return this.QW(ti);
      }
   }

   private void aO(Event<Void> event) {
      if (this.CV && mc.player != null) {
         this.CV = false;
      }

      if (CW != null) {
         if (this.QU(CW)) {
            return;
         }

         if (mc.player != null && this.CG != null) {
            MoveSubHelperSX var2 = CW;
            if (var2.h) {
               Debug.b("重新加载上一个Travel task中...");
               var2.onStart(this, mc.player.getPos());
               this.CG.onStop();
               this.CG.onStart(var2);
               Debug.b("上一个travel task重新加载完成,使用travel cancel取消");
            }

            if (var2.shouldNotRun()) {
               return;
            }

            if (this.CG.onTick(event)) {
               this.QS(var2);
            }
         }
      }
   }

   private void QR(MoveSubHelperSX ti) {
      if (ti != null && ti.startingTime != 0L) {
         Debug.b("当前travel task已完成或者终止");
         long var2 = (System.currentTimeMillis() - ti.startingTime) / 1000L;
         Debug.e("using time", var2);
         if (mc.player != null) {
            double var4 = mc.player.getPos().distanceTo(ti.c);
            double var6 = var2 > 0L ? var4 / var2 : 0.0;
            Debug.chat("时间开销:", var2, "s, 运行距离: ", String.format("%.2f", var4), ", 平均速度: ", String.format("%.2f", var6), "m/s");
            mc.player.setOnGround(false);
         }
      }
   }

   private void QL() {
      if (CW != null && CW.f != this) {
         CW.e = true;
         CW = null;
      }
   }

   public void QS(MoveSubHelperSX info) {
      if (info != null) {
         info.e = true;
      }

      CW = null;
      this.CG.onStop();
   }

   private void QJ(Event<EnterReconfigurationS2CPacket> eventKick) {
      this.CV = true;
   }

   @Override
   public void bb(Event<LegalMovementManager> movementManagerEvent) {
      if (CW != null && !CW.shouldNotRun() && this.CG instanceof HackUtilHelperJ var3) {
         var3.bb(movementManagerEvent);
      }
   }

   private MoveSubHelperAh QH(TravellingControl$Type type) {
      MoveSubHelperAh var2 = switch (type) {
         case ELYTRASKY -> new MoveSubHelperX(this);
         case ELYTRA_PITCH40 -> new MoveSubHelperKX(this);
         case ELYTRA_GRIM_FLY40 -> new MoveSubHelperUX(this);
         case MOV_VOID -> new MoveSubHelperAk(this);
         case MOV_VOID_2 -> new MoveSubHelperP(this);
         default -> eve -> true;
      };
      return var2;
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (CW != null && !CW.shouldNotRun() && this.CG instanceof HackUtilHelperJ var4) {
         var4.postModify(movementManagerEvent, enabledThisTick);
      }

      return true;
   }

   public void QZ() {
      if (CW != null) {
         CW.e = true;
         CW.g = true;
      }

      this.QS(CW);
      this.QR(CW);
      CW = null;
   }
}
