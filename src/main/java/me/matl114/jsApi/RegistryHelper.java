package me.matl114.jsApi;

import java.util.Objects;
import me.matl114.events.annotations.Modifiable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@Modifiable
public class RegistryHelper {
   private static MinecraftClient mc = MinecraftClient.getInstance();

   public static <T> String getIdInRegistry(Registry<T> registry, T value) {
      return Objects.requireNonNull(registry.getId(value)).toString();
   }

   public static <T> Registry<T> getRegistry(String resourceKey) {
      return (Registry<T>)mc.getNetworkHandler().getRegistryManager().getOptional(RegistryKey.ofRegistry(Identifier.tryParse(resourceKey))).orElseThrow();
   }

   public static <T> T getInRegistry(Registry<T> registry, String key) {
      return (T)registry.get(Identifier.tryParse(key));
   }
}
