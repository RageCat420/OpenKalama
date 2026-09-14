package me.matl114.mixins.events;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.ArrayList;
import java.util.List;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracked;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({DataTracker.class})
@Environment(EnvType.CLIENT)
public class DataTrackerEvents {
    @Final
    @Shadow
    private DataTracked field_13333;

    @Inject(
            method = {"writeUpdatedEntries"},
            at = {@At("HEAD")})
    private void callDataTrackerEntryUpdateEvents(
            List<SerializedEntry<?>> entries,
            CallbackInfo ci,
            @Local(argsOnly = true) LocalRef<List<SerializedEntry<?>>> entryRef) {
        if (!Listener.au().d()) {
            if (this.field_13333 instanceof Entity entity) {
                List<SerializedEntry<?>> entryList = new ArrayList<>();

                for (SerializedEntry<?> serializedEntry : entries) {
                    Event<SerializedEntry<?>> serializedEntryMutableObject =
                            new Event<>(serializedEntry, true, true, this.field_13333);
                    Listener.au().b(serializedEntryMutableObject);
                    if (!serializedEntryMutableObject.d() && serializedEntryMutableObject.e() != null) {
                        entryList.add(serializedEntryMutableObject.e());
                    }
                }

                entryRef.set(entryList);
            }
        }
    }
}
