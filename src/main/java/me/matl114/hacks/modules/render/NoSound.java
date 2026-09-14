package me.matl114.hacks.modules.render;

import java.util.Map;
import java.util.Set;
import me.matl114.accessors.access.SoundInstanceAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.EntryPrimitiveMap;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class NoSound extends BaseModule {
   public final FlagRef cancelSoundPlay;
   public final FlagRef cancelHudDisplay;
   public final KeyBindRef J;
   public final NBTRef<EntrySet<SoundEvent>> noSounds;
   public ModulePath aD = makePath(Configs.i, "sounds.no-sounds");
   public final NBTRef<EntryPrimitiveMap<SoundEvent, Double>> IY;
   public final FlagRef ae = this.flagBuilder(this.aD.addEnable()).build();

   public void Xy(Event<SoundInstance> event) {
      if (this.cancelHudDisplay.get() && this.shouldCancel((SoundInstance)event.b)) {
         event.cancel();
      }
   }

   public boolean shouldCancel(SoundInstance soundInstance) {
      if (!checkNull() && this.ae.get()) {
         Identifier var2 = soundInstance.getId();
         if (var2 == null) {
            return false;
         } else {
            SoundEvent var3 = (SoundEvent)Registries.SOUND_EVENT.get(var2);
            return var3 != null && this.noSounds.get().set().contains(var3);
         }
      } else {
         return false;
      }
   }

   public void Xx(Event<SoundInstance> event) {
      if (this.cancelSoundPlay.get() && this.shouldCancel((SoundInstance)event.b)) {
         event.cancel();
      } else {
         this.modifyVolume((SoundInstance)event.b);
      }
   }

   public void modifyVolume(SoundInstance instance) {
      Identifier var2 = instance.getId();
      SoundEvent var3 = (SoundEvent)Registries.SOUND_EVENT.get(var2);
      if (var3 != null && instance instanceof AbstractSoundInstance var4) {
         Double var5 = this.IY.get().uJ(var3);
         if (var5 != null) {
            SoundInstanceAccess.of(var4).setScale(var5);
         }
      }
   }

   public NoSound() {
      super("NoSound");
      this.J = this.moduleEntry(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable()).build();
      this.cancelSoundPlay = this.flagBuilder(this.aD.add("cancel-sound-play")).build();
      this.cancelHudDisplay = this.flagBuilder(this.aD.add("cancel-hud-display")).build();
      this.noSounds = this.builder(this.aD.add("no-sounds"), EntrySet.<SoundEvent>parameter())
         .defaultValue(
            new EntrySet<>(
               Registries.SOUND_EVENT,
               Set.of((SoundEvent)SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE.value(), (SoundEvent)SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND.value())
            )
         )
         .build();
      this.IY = this.builder(this.aD.add("volume-override"), EntryPrimitiveMap.<SoundEvent, Double>uA())
         .defaultValue(new EntryPrimitiveMap<SoundEvent, Double>(Registries.SOUND_EVENT, NBTTypes.e, Map.of()))
         .build();
      this.bindFlag(this.ae);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bB(), this::Xx);
      this.registerListener(Listener.bC(), this::Xy);
   }
}
