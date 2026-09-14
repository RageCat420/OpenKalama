package me.matl114.managers.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import me.matl114.utils.commands.params.impl.StringArgumentResult;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public interface ConfigEnum extends StringIdentifiable, StringArgumentResult, AutoRegisterType {
   Map<String, Map<String, ConfigEnum>> registeredConfigs = new HashMap<>();
   Map<String, Class<? extends ConfigEnum>> registeredEnumsClasses = new HashMap<>();

   static void register(String type, Class<? extends Enum> configEnum) {
      if (registeredEnumsClasses.containsKey(type)) {
         throw new IllegalArgumentException("Duplicate config enum name: " + type);
      } else {
         registeredEnumsClasses.put(type, (Class)configEnum);
         Map<String, ConfigEnum> maps = new LinkedHashMap<>();

         for (Enum e : configEnum.getEnumConstants()) {
            maps.put(e.name(), (ConfigEnum)e);
         }

         registeredConfigs.put(type, maps);
      }
   }

   static String getConfigEnumType(Class<?> configEnum) {
      return configEnum.getEnumConstants().length == 0
         ? getBinarySimpleName(configEnum).toLowerCase(Locale.ROOT)
         : ((ConfigEnum)configEnum.getEnumConstants()[0]).getConfigEnumType();
   }

   static String getBinarySimpleName(Class<?> type) {
      String name = type.getName();
      int separator = Math.max(name.lastIndexOf(46), name.lastIndexOf(36));
      return separator < 0 ? name : name.substring(separator + 1);
   }

   static void ensureRegistered(Class<?> configEnum) {
      String configTypeName = getConfigEnumType(configEnum);
      if (!registeredConfigs.containsKey(configTypeName)) {
         register(configTypeName, (Class<? extends Enum>)configEnum);
      }
   }

   default Enum cast() {
      return (Enum)(Object)this;
   }

   default String getConfigEnumType() {
      return getBinarySimpleName(this.getClass()).toLowerCase(Locale.ROOT);
   }

   default String asString() {
      return "enum:" + this.getConfigEnumType() + ":" + this.cast().name();
   }

   default Map<String, ConfigEnum> getMap() {
      ensureRegistered(this.getClass());
      return registeredConfigs.get(this.getConfigEnumType());
   }

   static <T extends Enum<T>> Map<String, T> getMap(Class<T> configEnum) {
      ConfigEnum enumValue = (ConfigEnum)((Enum[])configEnum.getEnumConstants())[0];
      return (Map)enumValue.getMap();
   }

   static void onLoad(Class<?> clazz) {
      if (ConfigEnum.class.isAssignableFrom(clazz) && Enum.class.isAssignableFrom(clazz)) {
         ensureRegistered(clazz);
      }
   }

   @Override
   default Text resultAsString() {
      return Text.translatable("configenum." + this.getConfigEnumType().replace("_", "-") + "." + this.cast().name().toLowerCase(Locale.ROOT));
   }

   default boolean isIn(ConfigEnum... e) {
      for (ConfigEnum re : e) {
         if (re == this) {
            return true;
         }
      }

      return false;
   }

   default boolean isNotIn(ConfigEnum... e) {
      for (ConfigEnum re : e) {
         if (re == this) {
            return false;
         }
      }

      return true;
   }
}
