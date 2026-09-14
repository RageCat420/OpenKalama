package me.matl114.hacks.modules.extra;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import me.matl114.hacks.utils.config.BoundedPrimitiveFlagMap;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import net.minecraft.entity.player.PlayerModelPart;

public class SkinBlink$PlayerModelPartSelectSet extends BoundedPrimitiveFlagMap<PlayerModelPart> implements NBTParsable<SkinBlink$PlayerModelPartSelectSet> {
   public static final NBTType<SkinBlink$PlayerModelPartSelectSet> TYPE = createEnumMap(
      "PlayerModelPartSelectSet".toLowerCase(Locale.ROOT), PlayerModelPart.class, SkinBlink$PlayerModelPartSelectSet::new
   );

   public SkinBlink$PlayerModelPartSelectSet(List<PlayerModelPart> keys, Map<PlayerModelPart, Boolean> map, NBTType<Boolean> type) {
      super(keys, map, type);
   }

   public SkinBlink$PlayerModelPartSelectSet() {
      super(PlayerModelPart.class);
   }

   @Override
   public NBTType<SkinBlink$PlayerModelPartSelectSet> type() {
      return TYPE.cast();
   }
}
