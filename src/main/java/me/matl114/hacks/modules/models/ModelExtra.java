package me.matl114.hacks.modules.models;

import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;

public class ModelExtra extends BaseModule {
   public final FlagRef enableBlockModelProtect;
   public final ModulePath iv = makePath(Configs.q, "model-config");

   public ModelExtra() {
      super("ModelExtra");
      this.enableBlockModelProtect = this.flagBuilder(this.iv.add("enable-block-model-protect")).build();
   }
}
