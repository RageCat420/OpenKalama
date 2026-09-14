package me.matl114.hacks.utils.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.RegistryAttrKeyValue;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public record WeakHolder<T>(Identifier registry, Identifier location) implements NBTParsable<WeakHolder<T>> {
   public static final Identifier ru = new Identifier("minecraft", "default");
   private static final MinecraftClient bj = MinecraftClient.getInstance();
   public static NBTType<WeakHolder> TYPE = new NBTType<>(
      "weakholder",
      Codec.STRING.comapFlatMap(WeakHolder::parse, WeakHolder::aog),
      (w, x, y, dx, dy) -> {
         Identifier registry = ((WeakHolder)w.getOriginValue()).registry();
         ClientPlayNetworkHandler handler = bj.getNetworkHandler();
         Optional<Registry<Object>> optionalLookup;
         if (handler != null && handler.getRegistryManager() != null) {
            Immutable registryLookup = handler.getRegistryManager();
            optionalLookup = registryLookup.getOptional(RegistryKey.ofRegistry(registry));
         } else {
            optionalLookup = Optional.empty();
         }

         TypeConvertAttrKeyValue<WeakHolder, Identifier> wrapper = new TypeConvertAttrKeyValue<>(
            w, WrapperFactory.of(s -> new WeakHolder(registry, s), WeakHolder::location), NBTTypes.l
         );
         if (optionalLookup.isPresent()) {
            Registry<Object> lookupValue = optionalLookup.get();
            return RegistryAttrKeyValue.generateTextInputWithRegistrySearch((Registry)lookupValue, wrapper, x, y, dx, dy);
         } else {
            return wrapper.generateValueWidget(x, y, dx, dy);
         }
      },
      new WeakHolder(Enchantments.AQUA_AFFINITY)
   );

   public WeakHolder(RegistryKey<T> registryKey) {
      this(registryKey.getRegistry(), registryKey.getValue());
   }

   public static <W> Class<WeakHolder<W>> uA() {
      return (Class<WeakHolder<W>>)(Class<?>)WeakHolder.class;
   }

   public static <T> DataResult<WeakHolder<T>> parse(String s) {
      String[] split = s.split("\\|");
      if (split.length == 2) {
         Identifier identifier = Identifier.tryParse(split[0]);
         Identifier location = Identifier.tryParse(split[1]);
         return identifier != null && location != null ? DataResult.success(new WeakHolder(identifier, location)) : DataResult.error(() -> "Invalid format");
      } else {
         return DataResult.error(() -> "Invalid format");
      }
   }

   public String aog() {
      return this.registry + "|" + this.location;
   }

   @Override
   public NBTType<WeakHolder<T>> type() {
      return TYPE.cast();
   }

   public RegistryKey<T> toRegistryKey() {
      return RegistryKey.of(RegistryKey.ofRegistry(this.registry), this.location);
   }

   public Optional<RegistryEntry<T>> getEntry() {
      RegistryKey<T> key = this.toRegistryKey();
      return ItemStackUtils.registry().get(this.toRegistryKey().getRegistryRef()).getEntry(key).map(s -> (RegistryEntry<T>)s);
   }

   public WeakHolder<T> withRegistry(Identifier registry) {
      return this.registry == registry ? this : new WeakHolder<>(registry, this.location);
   }

   public WeakHolder<T> withLocation(Identifier location) {
      return this.location == location ? this : new WeakHolder<>(this.registry, location);
   }
}
