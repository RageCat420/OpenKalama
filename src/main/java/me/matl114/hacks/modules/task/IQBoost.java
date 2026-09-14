package me.matl114.hacks.modules.task;

import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import net.minecraft.text.Text;

public class IQBoost extends BaseModule {
   public final KeyBindRef J;
   ModulePath iE = makePath(Configs.r, "iq-boost");
   public final IntRef boostValue;
   public final FlagRef ae = this.flagBuilder(this.iE.addEnable()).build();

   public IQBoost() {
      super("IQBoost");
      this.J = this.moduleEntry(this.iE.addHotkey(), new MultiKeyBind(), this.iE.addEnable(), () -> Text.literal(String.valueOf(this.boostValue.get())))
         .build();
      this.boostValue = this.builder(this.iE.add("boost-value"), IntRef.TYPE).defaultValue(114514).build();
      this.bindFlag(this.ae);
   }

   @Override
   public void registerAll() {
      super.registerAll();
   }
}
