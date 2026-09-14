package me.matl114.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.List;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.client.sound.SoundInstance.AttenuationType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({SoundSystem.class})
public class SoundSystemEvents {
   @Shadow
   @Final
   private List<SoundInstanceListener> field_5558;
   @Shadow
   @Final
   private SoundManager field_5552;

   @WrapOperation(
      method = {"play(Lnet/minecraft/client/sound/SoundInstance;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/sound/SoundInstanceListener;onSoundPlayed(Lnet/minecraft/client/sound/SoundInstance;Lnet/minecraft/client/sound/WeightedSoundSet;F)V"
      )}
   )
   private void play(SoundInstanceListener instance, SoundInstance soundInstance, WeightedSoundSet weightedSoundSet, float v, Operation<Void> original) {
      if (instance instanceof SubtitlesHud hud) {
         Event<SoundInstance> event = new Event<>(soundInstance, true, false);
         Listener.bC().b(event);
         if (event.d()) {
            return;
         }

         original.call(new Object[]{instance, event.b, weightedSoundSet, v});
      } else {
         original.call(new Object[]{instance, soundInstance, weightedSoundSet, v});
      }
   }

   @Inject(
      method = {"play(Lnet/minecraft/client/sound/SoundInstance;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onInterceptPlay(SoundInstance sound, CallbackInfo ci, @Local(argsOnly = true) LocalRef<SoundInstance> args) {
      Event<SoundInstance> event = new Event<>(sound, true, true);
      Listener.bB().b(event);
      if (event.d()) {
         ci.cancel();
         this.onSoundPlayed(event.b);
      } else {
         if (sound != event.b) {
            args.set(event.b);
         }
      }
   }

   @Unique
   private void onSoundPlayed(SoundInstance sound) {
      WeightedSoundSet weightedSoundSet = sound.getSoundSet(this.field_5552);
      if (weightedSoundSet != null) {
         Sound sound2 = sound.getSound();
         if (sound2 != SoundManager.INTENTIONALLY_EMPTY_SOUND) {
            if (sound2 != SoundManager.MISSING_SOUND) {
               if (!this.field_5558.isEmpty()) {
                  boolean bl = sound.isRelative();
                  AttenuationType attenuationType = sound.getAttenuationType();
                  float f = sound.getVolume();
                  float g = Math.max(f, 1.0F) * sound2.getAttenuation();
                  float j = !bl && attenuationType != AttenuationType.NONE ? g : Float.POSITIVE_INFINITY;

                  for (SoundInstanceListener soundInstanceListener : this.field_5558) {
                     if (soundInstanceListener instanceof SubtitlesHud) {
                        Event<SoundInstance> event = new Event<>(sound, true, true);
                        Listener.bC().b(event);
                        if (event.d()) {
                           continue;
                        }
                     }

                     soundInstanceListener.onSoundPlayed(sound, weightedSoundSet, j);
                  }
               }
            }
         }
      }
   }
}
