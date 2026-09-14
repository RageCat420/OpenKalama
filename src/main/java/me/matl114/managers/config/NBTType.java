package me.matl114.managers.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.Locale;
import java.util.Objects;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.AttrKeyValues;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

public class NBTType<T> implements WrapperFactory<NbtElement, T> {
   final String typeName;
   final Codec<T> typeCodec;
   AttrKeyValue.CustomWidgetFactory<T> customWidgetFactory;
   WrapperFactory<String, T> stringifyFactory;
   final T empty;

   public NBTType(String typeName, Codec<T> codec, AttrKeyValue.CustomWidgetFactory<T> customWidgetFactory, T empty) {
      this(typeName, codec, null, customWidgetFactory, empty);
      this.stringifyFactory = createDefaultFactory(this);
   }

   public NBTType(String typeName, Codec<T> codec, AttrKeyValue.CustomWidgetFactory<T> customWidgetFactory, WrapperFactory<String, T> stringifyFactory, T empty) {
      this(typeName, codec, Objects.requireNonNull(stringifyFactory), customWidgetFactory, empty);
   }

   public NBTType(String typeName, Codec<T> codec, WrapperFactory<String, T> stringifyFactory, AttrKeyValue.CustomWidgetFactory<T> customWidgetFactory, T empty) {
      this.typeName = typeName.toLowerCase(Locale.ROOT);
      this.typeCodec = codec;
      this.customWidgetFactory = customWidgetFactory;
      this.stringifyFactory = stringifyFactory;
      this.empty = empty;
   }

   public T parse(NbtElement element) {
      return (T)((Pair)(Object)this.typeCodec.decode(NbtOps.INSTANCE, element).getOrThrow()).getFirst();
   }

   public T createEmpty() {
      return (T)((Pair)(Object)this.typeCodec.decode(NbtOps.INSTANCE, (NbtElement)(Object)this.typeCodec.encodeStart(NbtOps.INSTANCE, this.empty).getOrThrow()).getOrThrow())
         .getFirst();
   }

   public NbtElement toNbt(T val) {
      return (NbtElement)(Object)this.typeCodec.encodeStart(NbtOps.INSTANCE, val).getOrThrow();
   }

   public BaseAttrKeyValue<T> createAttrKeyValue(String key, T value) {
      return new BaseAttrKeyValue<>(key, value, this.customWidgetFactory, AttrKeyValues.NBT_FACTORY.concat(WrapperFactory.of(this::parse, this::toNbt)));
   }

   public DrawableWidget generateValueWidget(AttrKeyValue<T> value, int x, int y, int inputDx, int dy) {
      if (this.customWidgetFactory != null) {
         return this.customWidgetFactory.generateWidget(value, x, y, inputDx, dy);
      } else {
         throw new IllegalStateException("Can not find factory");
      }
   }

   public T create(NbtElement va) {
      return this.parse(va);
   }

   public NbtElement get(T va) {
      return this.toNbt(va);
   }

   public static <T> WrapperFactory<String, T> createDefaultFactory(NBTType<T> type) {
      return AttrKeyValues.NBT_FACTORY.concat(type);
   }

   public static <T> Class<T> parameter(Class<?> wClass) {
      return (Class<T>)wClass;
   }

   public <W> NBTType<W> cast() {
      return (NBTType<W>)(Object)this;
   }

   @Override
   public String toString() {
      return "NBTType[" + this.typeName + "]";
   }

   public String typeName() {
      return this.typeName;
   }

   public Codec<T> typeCodec() {
      return this.typeCodec;
   }

   public AttrKeyValue.CustomWidgetFactory<T> customWidgetFactory() {
      return this.customWidgetFactory;
   }

   public WrapperFactory<String, T> stringifyFactory() {
      return this.stringifyFactory;
   }

   public T empty() {
      return this.empty;
   }

   public NBTType<T> customWidgetFactory(AttrKeyValue.CustomWidgetFactory<T> customWidgetFactory) {
      this.customWidgetFactory = customWidgetFactory;
      return this;
   }

   public NBTType<T> stringifyFactory(WrapperFactory<String, T> stringifyFactory) {
      this.stringifyFactory = stringifyFactory;
      return this;
   }
}
