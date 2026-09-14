package me.matl114.hacks.modules.extra;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.Arm;

public class SkinBlink extends BaseModule {
   Map<PlayerModelPart, Boolean> iJ;
   public final FlagRef switchArm;
   int lastDelay;
   Arm iK;
   public final ModulePath iE = makePath(Configs.j, "other.skin-blink");
   public final FlagRef switchModel;
   public final NBTRef<SkinBlink$PlayerModelPartSelectSet> switchModelParts;
   public final IntRef delay;
   public final FlagRef ae = this.flagBuilder(this.iE.addEnable()).build();
   boolean needRestore;
   public final KeyBindRef J = this.moduleEntry(this.iE.addHotkey(), new MultiKeyBind(), this.iE.addEnable()).build();

   public void onTick(Event<ClientPlayerEntity> eventTick) {
      if (this.ae.get() && ++this.lastDelay > this.delay.get()) {
         this.lastDelay = 0;
         if (this.needRestore) {
            this.restore();
         } else {
            this.blink();
         }

         mc.options.sendClientSettings();
      }
   }

   public void restore() {
      this.needRestore = false;

      for (Entry var2 : this.iJ.entrySet()) {
         mc.options.setPlayerModelPart((PlayerModelPart)var2.getKey(), (Boolean)var2.getValue());
      }

      if (this.iK != null) {
         mc.options.getMainArm().setValue(this.iK);
         this.iK = null;
      }
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      if (this.needRestore) {
         this.restore();
         mc.options.sendClientSettings();
      }
   }

   public void blink() {
      this.needRestore = true;
      this.iJ.clear();
      this.iK = null;
      if (this.switchModel.get()) {
         SkinBlink$PlayerModelPartSelectSet var1 = this.switchModelParts.get();

         for (PlayerModelPart var5 : PlayerModelPart.values()) {
            if (var1.getState(var5)) {
               boolean var6 = mc.options.isPlayerModelPartEnabled(var5);
               this.iJ.put(var5, var6);
               mc.options.setPlayerModelPart(var5, !var6);
            }
         }
      }

      if (this.switchArm.get()) {
         this.iK = (Arm)mc.options.getMainArm().getValue();
         mc.options.getMainArm().setValue(this.iK.getOpposite());
      }
   }

   public SkinBlink() {
      super("SkinBlink");
      this.delay = this.intBuilder(this.iE.add("delay")).defaultValue(20).build();
      this.switchModel = this.builder(this.iE.add("switch-model"), Boolean.class).defaultValue(true).build();
      this.switchModelParts = this.builder(this.iE.add("switch-model-parts"), SkinBlink$PlayerModelPartSelectSet.class)
         .defaultValue(new SkinBlink$PlayerModelPartSelectSet())
         .build();
      this.switchArm = this.flagBuilder(this.iE.add("switch-arm")).build();
      this.iJ = new EnumMap<>(PlayerModelPart.class);
      this.iK = null;
      this.lastDelay = 0;
      this.bindFlag(this.ae);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.V(), this::onTick);
   }
}
