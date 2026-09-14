package me.matl114.hacks.modules.combat;

import net.minecraft.util.math.BlockPos;

public sealed interface CombatSubHelperR permits CombatSubHelperZ, CombatSubHelperL {
   boolean isMetaEmpty();

   BlockPos getMetadata();

}
