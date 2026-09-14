package me.matl114.mixins.interfaces;

import me.matl114.accessors.interfaces.MetadataHolder;
import me.matl114.utils.containers.KalamaHelperHelperB;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin({Screen.class})
public class ScreenMetadataHolderMixin implements MetadataHolder {
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
