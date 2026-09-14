package me.matl114.hacks.modules.survival;

import java.util.function.Consumer;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.Vec3;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.move.HackUtilHelperD;
import me.matl114.hacks.utils.move.HackUtilHelperE;
import me.matl114.hacks.utils.move.HackUtilHelperH;
import me.matl114.hacks.utils.tasks.TimerExecutor;
import me.matl114.hooks.BaritoneHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import net.minecraft.util.Formatting;

public class SchedularSettings extends BaseModule {
   public final NBTRef<WrapColor> colorLines;
   public final FlagRef enableAutoExpandChest;
   public final FlagRef pause;
   public final IntRef hotbarProtect;
   public final NBTRef<WrapColor> goalColor;
   public final NBTRef<WrapColor> replenishmentColor;
   public static SchedularSettings INSTANCE;
   public final NBTRef<Vec3> scannChestRange;
   TimerExecutor VV;
   public final EnumRef<SchedularSettings$EngineType> enginType;
   TimerExecutor VX;
   public final FlagRef warn;
   public final FlagRef enableAutoExpandShulker;
   public final FlagRef replenish;
   public final FlagRef enableRender;
   public final FlagRef discharge;
   public final NBTRef<WrapColor> dischargeColor;
   public final KeyBindRef holdReset;
   TimerExecutor VW;
   public final KeyBindRef pauseHotkey;
   public final IntRef invSpeedLimit;
   final ModulePath aD = makePath(Configs.o, "schedular-settings");
   public final FlagRef enableContainerHotReload;
   public final NBTRef<WrapColor> shulkerSupportColor;
   public final FlagRef detailedLog;

   public HackUtilHelperD createEngine() {
      switch ((SchedularSettings$EngineType)this.enginType.get()) {
         case BARITONE:
            if (BaritoneHooks.getInstance().isBaritoneAPISupported()) {
               return new HackUtilHelperH();
            }

            return new HackUtilHelperE();
         default:
            return new HackUtilHelperE();
      }
   }

   public void amB() {
      if (this.warn.get()) {
         this.VV.b(100, () -> this.logI18N("message.module.schedular-settings.logic-error.no-suitable-container-source", new Object[0]));
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
   }

   public void amF() {
      if (this.warn.get()) {
         this.logI18N("message.module.schedular-settings.logic.expand-storage", new Object[0]);
      }
   }

   public SchedularSettings() {
      super("Schedular");
      this.enginType = this.builder(this.aD.add("engin-type"), SchedularSettings$EngineType.class).defaultValue(SchedularSettings$EngineType.BARITONE).build();
      this.replenish = this.builder(this.aD.add("replenish"), Boolean.class).defaultValue(true).build();
      this.discharge = this.builder(this.aD.add("discharge"), Boolean.class).defaultValue(true).build();
      this.invSpeedLimit = this.intBuilder(this.aD.add("inv-speed-limit")).defaultValue(27).build();
      this.hotbarProtect = this.intBuilder(this.aD.add("hotbar-protect")).defaultValue(0).build();
      this.scannChestRange = this.builder(this.aD.add("scann-chest-range"), Vec3.class).defaultValue(new Vec3(20.0, 5.0, 20.0)).build();
      this.enableAutoExpandShulker = this.builder(this.aD.add("enable-auto-expand-shulker"), Boolean.class).defaultValue(true).build();
      this.enableAutoExpandChest = this.flagBuilder(this.aD.add("enable-auto-expand-chest")).build();
      this.enableContainerHotReload = this.flagBuilder(this.aD.add("enable-container-hot-reload")).build();
      this.warn = this.builder(this.aD.add("warn"), Boolean.class).defaultValue(true).build();
      this.detailedLog = this.flagBuilder(this.aD.add("detailed-log")).build();
      this.pause = this.flagBuilder(this.aD.add("pause")).build();
      this.pauseHotkey = this.hotkey(this.aD.add("pause-hotkey"), new MultiKeyBind()).build();
      this.holdReset = this.hotkey(this.aD.add("hold-reset"), new MultiKeyBind()).build();
      this.enableRender = this.builder(this.aD.add("enable-render"), Boolean.class).defaultValue(true).build();
      this.replenishmentColor = this.builder(this.aD.add("replenishment-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.BLUE)).build();
      this.dischargeColor = this.builder(this.aD.add("discharge-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.RED)).build();
      this.shulkerSupportColor = this.builder(this.aD.add("shulker-support-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.YELLOW)).build();
      this.goalColor = this.builder(this.aD.add("goal-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.GREEN)).build();
      this.colorLines = this.builder(this.aD.add("color-lines"), WrapColor.class).defaultValue(new WrapColor(Formatting.AQUA)).build();
      this.VV = new TimerExecutor();
      this.VW = new TimerExecutor();
      this.VX = new TimerExecutor();
      INSTANCE = this;
   }

   public void amA() {
      if (this.warn.get()) {
         this.VV.b(100, () -> this.logI18N("message.module.schedular-settings.logic-error.no-container-source", new Object[0]));
      }
   }

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      super.addCustomWidgets(acceptor, dx, dy, dblank);
      acceptor.accept(this.createTitleLabel("widget.interact.interact-block.use-argument", 0, dblank, dx, dy));
   }

   public void amE() {
      if (this.warn.get()) {
         this.VX.b(100, () -> this.logI18N("message.module.schedular-settings.logic.reset-progress", new Object[0]));
      }
   }

   public void amD() {
      if (this.warn.get()) {
         this.VX.b(100, () -> this.logI18N("message.module.schedular-settings.logic.pause-progress", new Object[0]));
      }
   }

   public void amC() {
      if (this.warn.get()) {
         this.VW.b(100, () -> this.logI18N("message.module.schedular-settings.logic.hot-reload-container-source", new Object[0]));
      }
   }
}
