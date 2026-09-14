package me.matl114.hacks.modules.move;

import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;

public class MoveTimer extends BaseModule {
   public final IntRef multiply;
   public final ModulePath hm = makePath(Configs.m, "move-speed");
   public final KeyBindRef timerEnableHotkey;
   public final ModulePath QJ = this.hm.add("timer");
   public final FlagRef ae = this.flagBuilder(this.QJ.add("timer-enable")).build();

   public MoveTimer() {
      super("MoveTimer");
      this.timerEnableHotkey = this.toggleHotkey(this.QJ.add("timer-enable-hotkey"), new MultiKeyBind(), this.QJ.add("timer-enable")).build();
      this.multiply = this.builder(this.QJ.add("multiply"), IntRef.TYPE).defaultValue(0).validator(Configs.d).build();
      this.bindFlag(this.ae);
   }
}
