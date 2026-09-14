package me.matl114.mixins.interfaces;

import me.matl114.accessors.interfaces.MetadataHolder;
import me.matl114.utils.containers.KalamaHelperHelperB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({BlockEntity.class})
@Environment(EnvType.CLIENT)
public class BlockEntityMetadataHolderMixin implements MetadataHolder {
   @Unique
   public KalamaHelperHelperB metaData;

   @Unique
   @Override
   public KalamaHelperHelperB getMetadata() {
      if (this.metaData == null) {
         this.metaData = new KalamaHelperHelperB();
      }

      return this.metaData;
   }

   @Unique
   @Override
   public boolean isMetaEmpty() {
      return this.metaData == null;
   }
}
