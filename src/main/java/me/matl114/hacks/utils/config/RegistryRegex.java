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
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

public class RegistryRegex<T> implements NBTParsable<RegistryRegex<T>>, Predicate<T> {
   public static final Class<RegistryRegex<EntityType<?>>> ENTITY_TYPE = (Class<RegistryRegex<EntityType<?>>>)(Class<?>)RegistryRegex.class;
   public static final Class<RegistryRegex<Item>> ITEM_TYPE = (Class<RegistryRegex<Item>>)(Class<?>)RegistryRegex.class;
   public static final Class<RegistryRegex<Block>> BLOCK_TYPE = (Class<RegistryRegex<Block>>)(Class<?>)RegistryRegex.class;
   public static final NBTType<RegistryRegex> TYPE = new NBTType<>(
      "registryregex",
      RecordCodecBuilder.create(
         instance -> instance.group(
               Regex.TYPE.typeCodec().fieldOf("regex").forGetter(RegistryRegex::getParent),
               Registries.REGISTRIES.getCodec().fieldOf("registry").forGetter(RegistryRegex::getRegistry)
            )
            .apply(instance, RegistryRegex::new)
      ),
      RegistryRegex::createTextEditWidget,
      new RegistryRegex(Regex.EMPTY, Registries.ITEM)
   );
   protected final Registry<T> registry;
   protected final Regex parent;
   protected Set<T> filterEntry;

   public static <T> Class<RegistryRegex<T>> uA() {
      return (Class<RegistryRegex<T>>)(Class<?>)RegistryRegex.class;
   }

   public <W extends RegistryRegex<T>> W withParent(Regex parent) {
      return (W)(new RegistryRegex(parent, this.registry));
   }

   public RegistryRegex(Regex parent, Registry<T> registry) {
      this.parent = parent;
      this.registry = registry;
   }

   public Set<T> getFilterValue() {
      if (this.filterEntry == null) {
         this.filterEntry = RegistryUtils.parseWhiteList(this.registry, this.parent.pattern());
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

   public static <T, W extends RegistryRegex<T>> DrawableWidget createTextEditWidget(AttrKeyValue<W> attr, int x, int y, int width, int height) {
      KalamaHelperHelperCX subScreenWidget = new KalamaHelperHelperCX(x, y, width, height);
      W originValue = (W)attr.getOriginValue();
      Registry<T> registry = originValue.getRegistry();
      AttrKeyValue<Regex> attrKeyValue = new TypeConvertAttrKeyValue<>(attr, WrapperFactory.of(originValue::withParent, RegistryRegex::getParent), Regex.TYPE);
      subScreenWidget.Q(attrKeyValue.generateValueWidget(0, 0, width - height, height));
      subScreenWidget.Q(
         ExecutableWidget.instance(width - height, 0, height, height)
            .eV(
               IconElement.cm(KalamaHelperHelperB.f, ButtonAction.a(() -> openRegexListView(registry, attr)))
                  .aO(TooltipHandler.ap(Streams.concat(new Stream[]{originValue.getRules().stream(), KalamaHelperHelperB.c().stream()}).toList()))
            )
      );
      return subScreenWidget;
   }

   private static <T, W extends RegistryRegex<T>> void openRegexListView(Registry<T> registry, AttrKeyValue<W> original) {
      AttrKeyValue<W> originalCopy = original.copy();
      W originalValue = (W)originalCopy.getOriginValue();
      AttrKeyValue<Regex> regexWrapper = new TypeConvertAttrKeyValue<>(
         originalCopy, WrapperFactory.of(originalValue::withParent, RegistryRegex::getParent), Regex.TYPE
      );
      ScreenAccess.of(new HackUtilHelperE(registry, v -> original.valueChange(null, originalCopy.getValue()), originalCopy, regexWrapper, original))
         .openFromCurrent();
   }

   @Override
   public NBTType<RegistryRegex<T>> type() {
      return TYPE.cast();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof RegistryRegex<?> that) ? false : Objects.equals(this.registry, that.registry) && Objects.equals(this.parent, that.parent);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.registry, this.parent);
   }

   @Override
   public boolean isSameType(NBTParsable<?> type) {
      return NBTParsable.super.isSameType(type) && type instanceof RegistryRegex<?> registryRegex && registryRegex.registry == this.registry;
   }

   @Override
   public <W> Optional<RegistryRegex<T>> tryTypeConvert(Ref<W> ref) {
      if (ref instanceof StringRef str) {
         Optional<Regex> regex = this.parent.tryTypeConvert(str);
         if (regex.isPresent()) {
            return Optional.of(new RegistryRegex<>(regex.get(), this.registry));
         }
      }

      return Optional.empty();
   }

   public List<Text> getRules() {
      return ChatUtils.parseTranslation("widget.nbt-parsable.registry-regex.rules.tooltips", "");
   }

   public Registry<T> getRegistry() {
      return this.registry;
   }

   public Regex getParent() {
      return this.parent;
   }
}
