package me.matl114.hooks.impl.xaeroplus;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import me.matl114.hooks.impl.xaeroplus.wrapper.ElementSupplier;
import me.matl114.hooks.impl.xaeroplus.wrapper.EllipseWrapper;
import me.matl114.hooks.impl.xaeroplus.wrapper.LineWrapper;
import me.matl114.hooks.impl.xaeroplus.wrapper.TextWrapper;

public interface IMapDrawFactory {
   IMapDrawFeature ellipses(String var1, ElementSupplier<List<EllipseWrapper<?>>> var2, IntSupplier var3, Supplier<Float> var4, int var5);

   IMapDrawFeature chunkHighlights(String var1, ElementSupplier<Long2LongMap> var2, IntSupplier var3, int var4);

   IMapDrawFeature asyncChunkHighlights(String var1, ElementSupplier<Long2LongMap> var2, IntSupplier var3);

   IMapDrawFeature lines(String var1, ElementSupplier<List<LineWrapper<?>>> var2, IntSupplier var3, Supplier<Float> var4, int var5);

   IMapDrawFeature text(String var1, ElementSupplier<Long2ObjectMap<TextWrapper<?>>> var2);

   IMapDrawFeature asyncText(String var1, ElementSupplier<Long2ObjectMap<TextWrapper<?>>> var2, int var3);

   void unregisterId(String var1);
}
