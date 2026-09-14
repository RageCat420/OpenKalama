package me.matl114.hacks.modules.inv;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.InventoryUtils;
import me.matl114.versioned.api.VItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class NoQDrop extends BaseModule {
   public final FlagRef forceEquipment;
   public final NBTRef<EntrySet<Item>> gl;
   public final FlagRef ae;
   public final ModulePath aD = makePath(Configs.l, "inv-utils.no-q-drop");
   public final KeyBindRef J;
   public final FlagRef logToPlayer;

   public void onPlayerDropAction(Event<Boolean> eventDrop) {
      if (this.ae.get()) {
         ItemStack var2 = InventoryUtils.getSelectedItem().val();
         if (this.forceEquipment.get() && var2.isDamageable() || this.gl.get().test(var2.getItem())) {
            if (this.logToPlayer.get()) {
               Debug.chat(ChatUtils.textFromLegacyString("&c[NoQDrop] &fCancel dropping"), VItem.w().l(var2));
            }

            eventDrop.cancel();
         }
      }
   }

   public NoQDrop() {
      super("NoQDrop");
      this.ae = this.flagBuilder(this.aD.addEnable()).build();
      this.J = this.moduleEntry(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable()).build();
      this.forceEquipment = this.flagBuilder(this.aD.add("force-equipment")).build();
      this.gl = this.builder(this.aD.add("white-list-items"), EntrySet.<Item>parameter())
         .defaultValue(new EntrySet<Item>(new Regex("^(.*diamond.*|.*netherite.*|elytra|mace|.*sword)$"), Registries.ITEM))
         .build();
      this.logToPlayer = this.flagBuilder(this.aD.add("log-to-player")).build();
      this.bindFlag(this.ae);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bf(), this::onPlayerDropAction);
   }
}
