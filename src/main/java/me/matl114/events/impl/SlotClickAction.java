package me.matl114.events.impl;

import net.minecraft.screen.slot.SlotActionType;

public record SlotClickAction(SlotActionType actionType, int syncId, int slotId, int button) {
   public int button() {
      return this.button;
   }

   public int slotId() {
      return this.slotId;
   }

   

   public SlotActionType actionType() {
      return this.actionType;
   }

   public int syncId() {
      return this.syncId;
   }
}
