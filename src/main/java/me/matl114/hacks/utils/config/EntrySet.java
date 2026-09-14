package me.matl114.hacks.utils.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.presets.choices.RegistryChooseScreen2;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.config.Ref;
import me.matl114.utils.config.AttrKeyValue;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EntrySet<T> implements NBTParsable<EntrySet<T>>, Predicate<T> {
   final Registry<T> registry;
   final Set<T> set;
   List<Identifier> data;
   public static final NBTType<EntrySet<Object>> TYPE = create();

   public static <T> Class<EntrySet<T>> parameter() {
      return (Class<EntrySet<T>>)(Class<?>)EntrySet.class;
   }

   public EntrySet(Regex regex, Registry<T> registry) {
      this.registry = registry;
      this.set = new LinkedHashSet<>();

      for (Identifier re : registry.getIds()) {
         if (regex.test(re.getPath())) {
            this.set.add((T)registry.get(re));
         }
      }
   }

   public EntrySet(Registry<T> registry, Collection<T> set) {
      this.registry = registry;
      this.set = new LinkedHashSet<>(set);
   }

   public EntrySet(List<Identifier> data, Registry<T> registry) {
      this.registry = registry;
      this.data = new ArrayList<>(data);
      this.set = new LinkedHashSet<>();

      for (Identifier id : data) {
         if (id != null) {
            registry.getOrEmpty(id).ifPresent(this.set::add);
         }
      }
   }

   public List<Identifier> idList() {
      if (this.data == null) {
         List<Identifier> cached = new ArrayList<>(this.set.size());

         for (T entry : this.set) {
            Identifier id = this.registry.getId(entry);
            if (id != null) {
               cached.add(id);
            }
         }

         this.data = cached;
      }

      return this.data;
   }

   public List<T> list() {
      return this.set.stream().toList();
   }

   public static <T> NBTType<EntrySet<T>> create() {
      return new NBTType<>(
         "entryset",
         RecordCodecBuilder.<EntrySet<T>>create(
               instance -> instance.group(
                     Codec.list(Identifier.CODEC).fieldOf("data").forGetter(EntrySet::idList),
                     ((Codec<Registry<T>>)Registries.REGISTRIES.getCodec()).fieldOf("key_type").forGetter(EntrySet::registry)
                  )
                  .apply(instance, EntrySet::new)
            ),
         EntrySet::generateValueWidget,
         (EntrySet<T>)new EntrySet<>(Registries.BLOCK, Set.of())
      );
   }

   private static <T> DrawableWidget generateValueWidget(AttrKeyValue<EntrySet<T>> attr, int x, int y, int dx, int dy) {
      return new KalamaHelperHelperCX(x, y, dx, dy)
         .Q(
            new ExecutableWidget(dy, 0, dx - dy, dy)
               .eV(
                  new ButtonElement(TextProvider.c(KalamaHelperHelperB.i), ButtonAction.a(() -> openRegistrySelectScreen(attr)))
                     .aO(TooltipHandler.ap(KalamaHelperHelperB.b()))
               )
         )
         .Q(DisplayWidget.instance(0, 0, dy - 1, dy).setRenderHandler(IconElement.cm(KalamaHelperHelperB.f, ButtonAction.c())));
   }

   private static <T> void openRegistrySelectScreen(AttrKeyValue<EntrySet<T>> attr) {
      EntrySet<T> current = attr.getOriginValue();
      ScreenAccess.of(new RegistryChooseScreen2<>(current.registry, current.set, selected -> {
         if (selected != null) {
            attr.valueChangeInternal(null, new EntrySet<>(current.registry, selected));
         }
      })).openFromCurrent();
   }

   @Override
   public NBTType<EntrySet<T>> type() {
      return TYPE.cast();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof EntrySet<?> that) ? false : Objects.equals(this.registry, that.registry) && Objects.equals(this.set, that.set);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.registry, this.set);
   }

   @Override
   public boolean isSameType(NBTParsable<?> type) {
      return NBTParsable.super.isSameType(type) && type instanceof EntrySet<?> that && that.registry == this.registry;
   }

   @Override
   public <W> Optional<EntrySet<T>> tryTypeConvert(Ref<W> ref) {
      return ref instanceof NBTRef nbt && nbt.get() instanceof RegistryRegex<?> oldRegex && oldRegex.registry == this.registry
         ? Optional.of(new EntrySet<>(oldRegex.getParent(), (Registry<T>)oldRegex.registry))
         : Optional.empty();
   }

   @Override
   public boolean test(T t) {
      return this.set.contains(t);
   }

   public Registry<T> registry() {
      return this.registry;
   }

   public Set<T> set() {
      return this.set;
   }

   public List<Identifier> data() {
      return this.data;
   }
}
