package me.matl114.hacks.utils.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.UUID;
import me.matl114.versioned.api.VNbt;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;

public class EntityStorage extends IStorage {
   public static final Codec<EntityStorage> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Uuids.CODEC.fieldOf("uuid").forGetter(EntityStorage::getUuid), Codec.unboundedMap(Codec.STRING, VNbt.a).fieldOf("storage").forGetter(v -> v.b)
         )
         .apply(instance, EntityStorage::new)
   );
   public final UUID uuid;

   public EntityStorage(UUID uuid, Map<String, NbtElement> elementMap) {
      super(World.OVERWORLD, elementMap);
      this.uuid = uuid;
   }

   public EntityStorage(UUID uuid) {
      this(uuid, null);
   }

   public UUID getUuid() {
      return this.uuid;
   }
}
