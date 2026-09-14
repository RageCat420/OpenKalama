package me.matl114.hacks.utils.config;

import com.google.common.collect.Streams;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.IconElement;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.config.Ref;
import me.matl114.managers.config.StringRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.RegistryUtils;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WeakRegistryRegex<T> implements NBTParsable<WeakRegistryRegex<T>>, Predicate<T> {
   private static final MinecraftClient bj = MinecraftClient.getInstance();
   public static final Class<WeakRegistryRegex<EntityType<?>>> ENTITY_TYPE = (Class<WeakRegistryRegex<EntityType<?>>>)(Class<?>)WeakRegistryRegex.class;
   public static final Class<WeakRegistryRegex<Item>> ITEM_TYPE = (Class<WeakRegistryRegex<Item>>)(Class<?>)WeakRegistryRegex.class;
   public static final Class<WeakRegistryRegex<Block>> BLOCK_TYPE = (Class<WeakRegistryRegex<Block>>)(Class<?>)WeakRegistryRegex.class;
   public static final NBTType<WeakRegistryRegex> TYPE = new NBTType<>(
      "weakregistryregex",
      RecordCodecBuilder.create(
         instance -> instance.group(
               Regex.TYPE.typeCodec().fieldOf("regex").forGetter(WeakRegistryRegex::parent),
               Identifier.CODEC.fieldOf("registry").forGetter(WeakRegistryRegex::registry)
            )
            .apply(instance, WeakRegistryRegex::new)
      ),
      WeakRegistryRegex::createTextEditWidget,
      new WeakRegistryRegex(Regex.EMPTY, RegistryKeys.ITEM.getValue())
   );
   protected final Identifier registry;
   protected final Regex parent;
   protected Set<T> filterEntry;

   public static <T> Class<WeakRegistryRegex<T>> uA() {
      return (Class<WeakRegistryRegex<T>>)(Class<?>)WeakRegistryRegex.class;
   }

   public <W extends WeakRegistryRegex<T>> W withParent(Regex parent) {
      return (W)(new WeakRegistryRegex(parent, this.registry));
   }

   public WeakRegistryRegex(Regex parent, Identifier registry) {
      this.parent = parent;
      this.registry = registry;
   }

   public Optional<Registry<T>> resolveRegistry() {
      ClientPlayNetworkHandler handler = bj.getNetworkHandler();
      return handler != null && handler.getRegistryManager() != null
         ? handler.getRegistryManager().getOptional(RegistryKey.ofRegistry(this.registry))
         : Optional.empty();
   }

   public Set<T> getFilterValue() {
      if (this.filterEntry == null) {
         Registry<T> registryValue = this.resolveRegistry().orElse(null);
         if (registryValue == null) {
            return Set.of();
         }

         this.filterEntry = RegistryUtils.parseWhiteList(registryValue, this.parent.pattern());
      }

      return this.filterEntry;
   }

   @Override
   public boolean test(T val) {
      return this.getFilterValue().contains(val);
   }

   public boolean test(RegistryEntry<T> val) {
      return this.getFilterValue().contains(val.value());
   }

   public static <T, W extends WeakRegistryRegex<T>> DrawableWidget createTextEditWidget(AttrKeyValue<W> attr, int x, int y, int width, int height) {
      KalamaHelperHelperCX subScreenWidget = new KalamaHelperHelperCX(x, y, width, height);
      W originValue = (W)attr.getOriginValue();
      AttrKeyValue<Regex> attrKeyValue = new TypeConvertAttrKeyValue<>(attr, WrapperFactory.of(originValue::withParent, WeakRegistryRegex::parent), Regex.TYPE);
      Optional<Registry<T>> registry = originValue.resolveRegistry();
      if (registry.isPresent()) {
         Registry<T> lookupValue = registry.get();
         subScreenWidget.Q(attrKeyValue.generateValueWidget(0, 0, width - height, height));
         subScreenWidget.Q(
            ExecutableWidget.instance(width - height, 0, height, height)
               .eV(
                  IconElement.cm(KalamaHelperHelperB.f, ButtonAction.a(() -> openRegexListView(lookupValue, attr)))
                     .aO(TooltipHandler.ap(Streams.concat(new Stream[]{originValue.getRules().stream(), KalamaHelperHelperB.c().stream()}).toList()))
               )
         );
      } else {
         subScreenWidget.Q(attrKeyValue.generateValueWidget(0, 0, width, height));
      }

      return subScreenWidget;
   }

   private static <T, W extends WeakRegistryRegex<T>> void openRegexListView(Registry<T> registry, AttrKeyValue<W> original) {
      AttrKeyValue<W> originalCopy = original.copy();
      W originalValue = (W)originalCopy.getOriginValue();
      AttrKeyValue<Regex> regexWrapper = new TypeConvertAttrKeyValue<>(
         originalCopy, WrapperFactory.of(originalValue::withParent, WeakRegistryRegex::parent), Regex.TYPE
      );
      ScreenAccess.of(new HackUtilHelperF(registry, v -> original.valueChange(null, originalCopy.getValue()), originalCopy, regexWrapper, original))
         .openFromCurrent();
   }

   @Override
   public NBTType<WeakRegistryRegex<T>> type() {
      return TYPE.cast();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof WeakRegistryRegex<?> that)
            ? false
            : Objects.equals(this.registry, that.registry) && Objects.equals(this.parent, that.parent);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.registry, this.parent);
   }

   @Override
   public boolean isSameType(NBTParsable<?> type) {
      return NBTParsable.super.isSameType(type) && type instanceof WeakRegistryRegex<?> registryRegex && Objects.equals(registryRegex.registry, this.registry);
   }

   @Override
   public <W> Optional<WeakRegistryRegex<T>> tryTypeConvert(Ref<W> ref) {
      if (ref instanceof StringRef stringRef) {
         Optional<Regex> regex = this.parent.tryTypeConvert(stringRef);
         if (regex.isPresent()) {
            return Optional.of(new WeakRegistryRegex<>(regex.get(), this.registry));
         }
      }

      return Optional.empty();
   }

   public List<Text> getRules() {
      return ChatUtils.parseTranslation("widget.nbt-parsable.registry-regex.rules.tooltips", "");
   }

   public Identifier registry() {
      return this.registry;
   }

   public Regex parent() {
      return this.parent;
   }

   public Set<T> filterEntry() {
      return this.filterEntry;
   }
}
