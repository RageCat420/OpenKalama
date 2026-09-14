package me.matl114.hacks.utils.config;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.EnumAttrKeyValue;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;

public class WrapEnum<T extends ConfigEnum> implements NBTParsable<WrapEnum<T>> {
   public String type;
   public String valueString;
   public T value;
   public boolean resolved;
   public static final NBTType<WrapEnum> TYPE = create().cast();

   private WrapEnum() {
   }

   public WrapEnum(T configEnum) {
      ConfigEnum.ensureRegistered(configEnum.getClass());
      this.value = configEnum;
      this.type = configEnum.getConfigEnumType();
      this.valueString = configEnum.cast().name();
      this.resolved = true;
   }

   public WrapEnum(String strstr) {
      try {
         EnumRef<?> ref = new EnumRef(strstr);
         this.type = ref.enumType;
         this.valueString = ref.enumValue;
         this.resolved = ref.resolved;
         if (this.resolved) {
            this.value = (T)ref.get();
         }
      } catch (Throwable var3) {
         this.type = null;
         this.value = null;
         this.valueString = null;
         this.resolved = false;
      }
   }

   public WrapEnum(EnumRef<?> ref) {
      this.type = ref.enumType;
      this.valueString = ref.enumValue;
      this.resolved = ref.resolved;
      if (this.resolved) {
         this.value = (T)ref.get();
      }
   }

   public String aog() {
      return "enum:" + this.type + ":" + this.valueString;
   }

   public static DataResult<WrapEnum<?>> fromString(String str) {
      WrapEnum<ConfigEnum> re = new WrapEnum<>(str);
      return re.type != null && re.valueString != null ? DataResult.success(re) : DataResult.error(() -> "Can not parse ConfigEnum type: " + str);
   }

   public T get() {
      if (!this.resolved) {
         this.tryResolve();
      }

      return this.value;
   }

   protected void tryResolve() {
      if (!this.resolved) {
         Map<String, ConfigEnum> re = ConfigEnum.registeredConfigs.get(this.type);
         if (re == null) {
            this.resolved = false;
         } else {
            ConfigEnum val = re.get(this.valueString);
            Preconditions.checkNotNull(val, "Unregistered enum value %s in enum type %s with %s".formatted(this.valueString, this.type, re.toString()));
            this.resolved = true;
            this.value = (T)val;
         }
      }
   }

   private static <T extends ConfigEnum> NBTType<WrapEnum<T>> create() {
      return new NBTType<>(
         "wrapenum",
         Codec.STRING.comapFlatMap(WrapEnum::fromString, WrapEnum::aog),
         (s, x, y, dx, dy) -> {
            WrapEnum<T> wrapEnum = (WrapEnum<T>)s.getOriginValue();
            if (!wrapEnum.resolved) {
               wrapEnum.tryResolve();
            }

            if (wrapEnum.resolved) {
               Map<String, T> configEnumType = (Map<String, T>)wrapEnum.value.getMap();
               Map<T, String> inversedMap = configEnumType.entrySet().stream().collect(Collectors.toMap(Entry::getValue, Entry::getKey));
               WrapperFactory<String, T> factory = WrapperFactory.of(configEnumType::get, inversedMap::get);
               return EnumAttrKeyValue.createFiniteLookupWidgetFactory(configEnumType)
                  .generateWidget(new TypeConvertAttrKeyValue<>(s, WrapperFactory.of(WrapEnum::new, WrapEnum::get), null, factory), x, y, dx, dy);
            } else {
               throw new IllegalStateException("Access to a config enum instance before it is registered");
            }
         },
         WrapperFactory.of(WrapEnum::new, WrapEnum::aog),
         new WrapEnum<>()
      );
   }

   @Override
   public NBTType<WrapEnum<T>> type() {
      return TYPE.cast();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof WrapEnum wrap) ? false : Objects.equals(wrap.type, this.type) && Objects.equals(wrap.valueString, this.valueString);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.valueString);
   }
}
