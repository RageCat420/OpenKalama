package me.matl114.accessors.interfaces;

import javax.annotation.Nonnull;
import me.matl114.utils.containers.KalamaHelperHelperB;

public interface MetadataHolder {
   @Nonnull
   KalamaHelperHelperB getMetadata();

   boolean isMetaEmpty();
}
