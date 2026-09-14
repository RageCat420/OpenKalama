package me.matl114.hacks.modules.render;

import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.StringRef;

public class CustomOverlay extends BaseModule {
   public NBTRef<WrapColor> customProgressBarColor;
   public final ModulePath aV = makePath(Configs.i, "custom-overlay");
   public IntRef customBackgroundColor;
   public FlagRef ae = this.flagBuilder(this.aV.add("enable-custom")).build();
   public StringRef aW = this.builder(this.aV.add("enable-custom-path"), StringRef.TYPE)
      .defaultValue("kalama:textures/custom/genshin_impact.png")
      .validator(Configs.c)
      .build();

   @Override
   public void registerAll() {
      super.registerAll();
   }

   public CustomOverlay() {
      super("CustomOverlay");
      this.customBackgroundColor = this.builder(this.aV.add("custom-background-color"), IntRef.TYPE).defaultValue(-1).build();
      this.customProgressBarColor = this.builder(this.aV.add("custom-progress-bar-color"), WrapColor.class).defaultValue(WrapColor.WHITE).build();
      this.bindFlag(this.ae);
   }
}
