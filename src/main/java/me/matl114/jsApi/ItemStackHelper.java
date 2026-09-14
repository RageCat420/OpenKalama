package me.matl114.jsApi;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import me.matl114.events.annotations.Modifiable;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.inventory.MutableInventory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Modifiable
public class ItemStackHelper {
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   public static void setCustomName(Object what, List<String> lore) {
      ItemStack var2 = JsHelper.a(what, ItemStack.class);
      if (!var2.isEmpty()) {
         ItemStackUtils.setLore(var2, lore != null && !lore.isEmpty() ? lore.stream().map(ChatUtils::f).map(Text.class::cast).toList() : null);
      }
   }

   public static Inventory m(List<ItemStack> list, int size) {
      return new MutableInventory(size, list);
   }

   public static ItemStack loadItemFromMap(Map<String, Object> itemStack) {
      return itemStack.isEmpty()
         ? ItemStack.EMPTY
         : (ItemStack)((Pair)ItemStack.CODEC.decode(ItemStackUtils.registry().getOps(JavaOps.INSTANCE), itemStack).getOrThrow()).getFirst();
   }

   public static List<String> getLore(Object what) {
      ItemStack var1 = JsHelper.a(what, ItemStack.class);
      if (var1.isEmpty()) {
         return null;
      } else {
         List var2 = ItemStackUtils.getLore(var1);
         return var2.isEmpty() ? null : var2.stream().map(ChatUtils::textToLegacyString).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
      }
   }

   public static ItemStack createStack(Object object, int num) {
      if (object instanceof ItemStack var2) {
         return var2.copyWithCount(num);
      } else if (object instanceof ItemConvertible var3) {
         return new ItemStack(var3, num);
      } else if (object instanceof String var4) {
         Item var5 = (Item)Registries.ITEM.get(Identifier.tryParse(var4));
         return new ItemStack(var5, num);
      } else {
         throw new IllegalArgumentException(object + " is not a stack related argument");
      }
   }

   public static Inventory l(List<?> list, int size) {
      return new MutableInventory(size, list.stream().map(s -> JsHelper.a(s, ItemStack.class)).collect(Collectors.toCollection(ArrayList::new)));
   }

   public static ComponentType<?> getComponentType(String name) {
      return RegistryHelper.getInRegistry(Registries.DATA_COMPONENT_TYPE, name);
   }

   public static Map<String, Object> saveItemToMap(Object itemStack) {
      ItemStack var1 = JsHelper.a(itemStack, ItemStack.class);
      return (Map<String, Object>)(var1.isEmpty()
         ? new LinkedHashMap<>()
         : (Map)ItemStack.CODEC.encodeStart(ItemStackUtils.registry().getOps(JavaOps.INSTANCE), var1).getOrThrow());
   }

   public static Object k(ItemStack stack) {
      return JsMacrosBridge.i().f(stack);
   }

   public static Inventory createJSMappingInventory(List<?> list, int size) {
      if (list.size() < size) {
         list.add(JsMacrosBridge.i().f(ItemStack.EMPTY));
      }

      return new KalamaHelperHelperE(size, list);
   }

   public static String getCustomName(Object what) {
      ItemStack var1 = JsHelper.a(what, ItemStack.class);
      if (var1.isEmpty()) {
         return null;
      } else {
         Text var2 = ItemStackUtils.getCustomName(var1);
         return Objects.equals(var2, Text.empty()) ? null : ChatUtils.textToLegacyString(var2);
      }
   }

   public static Optional<?> getComponent(Object what, ComponentType<?> type) {
      ItemStack var2 = JsHelper.a(what, ItemStack.class);
      if (var2.isEmpty()) {
         return null;
      } else {
         Reference2ObjectMap var3 = var2.components.changedComponents;
         return var3 == null ? null : (Optional)var3.get(type);
      }
   }

   public static void e(Object what, String name) {
      ItemStack var2 = JsHelper.a(what, ItemStack.class);
      if (!var2.isEmpty()) {
         ItemStackUtils.setCustomName(var2, name == null ? null : ChatUtils.textFromJsonString(name));
      }
   }

   public static <T> void setComponent(Object what, ComponentType<T> type, @Nullable Optional<T> value) {
      ItemStack var3 = JsHelper.a(what, ItemStack.class);
      if (!var3.isEmpty()) {
         if (value == null) {
            ItemStackUtils.setOrRemoveChange(var3, type, null);
         } else if (value.isPresent()) {
            ItemStackUtils.setOrRemoveChange(var3, type, value.get());
         } else {
            ItemStackUtils.markRemoveAsChange(var3, type);
         }
      }
   }
}
