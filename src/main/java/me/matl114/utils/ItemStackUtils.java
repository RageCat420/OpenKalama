package me.matl114.utils;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import me.matl114.bukkit.BukkitItemStackUtils;
import me.matl114.events.annotations.Modifiable;
import me.matl114.versioned.api.VHideFlag;
import me.matl114.versioned.api.VItem;
import me.matl114.versioned.impl.TooltipHideFlag_v1_21_1;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.Impl;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Modifiable
public class ItemStackUtils {
   private static final Style f;
   protected static boolean isGrassOrShortGrass;
   private static final Gson d = new GsonBuilder().disableHtmlEscaping().create();
   private static final MinecraftClient a = MinecraftClient.getInstance();
   private static final Map<String, EquipmentSlot> e = new HashMap<>();
   protected static String g;
   private static DynamicRegistryManager c;
   private static DynamicRegistryManager b;
   private static final NbtCompound j;
   protected static String h;

   public static <T> Predicate<ItemStack> componentPredicate(ComponentType<T> type, Predicate<T> test, boolean nullDefault) {
      return stack -> {
         Object var4 = stack.get(type);
         return var4 != null ? test.test(var4) : nullDefault;
      };
   }

   public static <T> void markRemoveAsChange(ItemStack stack, ComponentType<T> type) {
      if (stack != null && !stack.isEmpty()) {
         ComponentMapImpl var2 = stack.components;
         Reference2ObjectMap var3 = var2.changedComponents;
         if (var3 != null) {
            if (var3.containsKey(type) && var3.get(type) == Optional.empty()) {
               return;
            }

            var2.onWrite();
            var2.changedComponents.put(type, Optional.empty());
         }
      }
   }

   public static ItemStack H(ItemStack stack, boolean keepDur) {
      return I(stack, keepDur, true);
   }

   public static ItemStack I(ItemStack stack, boolean keepDur, boolean keepEnchant) {
      return J(stack, true, keepDur, keepEnchant);
   }

   @Nullable
   public static Text getCustomName(ItemStack stack) {
      Text var1 = getInPatch(stack, DataComponentTypes.CUSTOM_NAME);
      return (Text)(var1 == null ? Text.empty() : var1);
   }

   public static <T> T getInPatch(ItemStack stack, ComponentType<T> type) {
      if (stack != null && !stack.isEmpty()) {
         Reference2ObjectMap var2 = stack.components.changedComponents;
         if (var2 == null) {
            return null;
         } else {
            Optional var3 = (Optional)var2.get(type);
            return (T)(var3 == null ? null : var3.orElse(null));
         }
      } else {
         return null;
      }
   }

   public static void setLore(ItemStack itemStack, List<Text> lore) {
      if (lore != null && !lore.isEmpty()) {
         setOrRemoveChange(itemStack, DataComponentTypes.LORE, new LoreComponent(lore));
      } else {
         setOrRemoveChange(itemStack, DataComponentTypes.LORE, null);
      }
   }

   public static void setEnchantmentGlow(ItemStack stack) {
      setOrRemoveChange(stack, DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, Boolean.TRUE);
   }

   public static ItemStack G(ItemStack stack) {
      return H(stack, true);
   }

   public static CustomItemStackBuilder a() {
      return new CustomItemStackBuilder();
   }

   public static boolean getIsUnbreakable(ItemStack stack) {
      return g(stack, DataComponentTypes.UNBREAKABLE);
   }

   public static boolean hasInPatch(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         Reference2ObjectMap var1 = stack.components.changedComponents;
         if (var1 == null) {
            return false;
         } else if (var1.isEmpty()) {
            return false;
         } else if (var1.size() >= 2) {
            return true;
         } else if (var1.containsKey(DataComponentTypes.CUSTOM_DATA)) {
            NbtComponent var2 = (NbtComponent)((Optional)var1.get(DataComponentTypes.CUSTOM_DATA)).orElse(null);
            if (var2 != null && !var2.isEmpty()) {
               NbtCompound var3 = var2.getNbt();
               Set<String> var4 = var3.getKeys();
               if (var4.size() > 2) {
                  return true;
               } else {
                  int var7 = var3.get("Damage") instanceof NbtInt var6 ? var6.intValue() : 0;
                  if (var7 > 0) {
                     return true;
                  } else {
                     for (String var8 : var4) {
                        if (!Objects.equals("Damage", var8) && !var8.contains("VV|Protocol")) {
                           return true;
                        }
                     }

                     return false;
                  }
               }
            } else {
               return false;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean matchItemMiningAbility(ItemStack stack1, ItemStack stack2) {
      return Objects.equals(stack1.get(DataComponentTypes.TOOL), stack2.get(DataComponentTypes.TOOL)) && matchEfficiency(stack1, stack2);
   }

   private static boolean matchEfficiency(ItemStack stack1, ItemStack stack2) {
      ItemEnchantmentsComponent var2 = (ItemEnchantmentsComponent)stack1.get(DataComponentTypes.ENCHANTMENTS);
      ItemEnchantmentsComponent var3 = (ItemEnchantmentsComponent)stack2.get(DataComponentTypes.ENCHANTMENTS);
      if (var2 != null && var3 != null) {
         RegistryEntry var4 = registry().getOptional(Enchantments.EFFICIENCY.getRegistryRef()).flatMap(s -> s.getEntry(Enchantments.EFFICIENCY)).orElseThrow();
         return var2.getLevel(var4) == var3.getLevel(var4);
      } else {
         return var2 == var3;
      }
   }

   public static boolean g(ItemStack stack, ComponentType<?> type) {
      if (stack != null && !stack.isEmpty()) {
         Reference2ObjectMap var2 = stack.components.changedComponents;
         return var2 == null ? false : var2.containsKey(type) && !Objects.equals(Optional.empty(), var2.get(type));
      } else {
         return false;
      }
   }

   public static void setCustomModelData(ItemStack stack, int customModelData) {
      setOrRemoveChange(stack, DataComponentTypes.CUSTOM_MODEL_DATA, VItem.w().createModelData(customModelData));
   }

   public static int getEnchantmentLevel(ItemEnchantmentsComponent component, RegistryKey<Enchantment> key) {
      Impl var2 = registry().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
      return component.getLevel(var2.getOrThrow(key));
   }

   public static List<String> B(ItemStack stack) {
      return z(stack).stream().map(txt -> txt.getString().replace("§.", "")).collect(Collectors.toCollection(ArrayList::new));
   }

   public static ItemStack withTypeChange(ItemStack itemStack, Item typeChange) {
      return itemStack.copyComponentsToNewStackIgnoreEmpty(typeChange, itemStack.getCount());
   }

   public static List<Text> getLore(ItemStack stack) {
      LoreComponent var1 = getInPatch(stack, DataComponentTypes.LORE);
      return var1 == null ? new ArrayList<>() : new ArrayList<>(var1.lines());
   }

   public static void setUnbreakable(ItemStack stack, boolean ub) {
      UnbreakableComponent var2 = getInPatch(stack, DataComponentTypes.UNBREAKABLE);
      if (var2 == null) {
         setOrRemoveChange(stack, DataComponentTypes.UNBREAKABLE, ub ? new UnbreakableComponent(true) : null);
      } else if (!ub) {
         setOrRemoveChange(stack, DataComponentTypes.UNBREAKABLE, null);
      }
   }

   public static WrapperLookup l() {
      return KalamaHelperHelperT.INSTANCE;
   }

   public static ItemEnchantmentsComponent getItemEnchant(ItemStack stack) {
      ItemEnchantmentsComponent var1 = getInPatch(stack, DataComponentTypes.ENCHANTMENTS);
      return var1 == null ? ItemEnchantmentsComponent.DEFAULT : var1;
   }

   public static NbtCompound getCustomDataReadOnly(ItemStack itemStack) {
      NbtComponent var1 = getInPatch(itemStack, DataComponentTypes.CUSTOM_DATA);
      return var1 == null ? j : var1.getNbt();
   }

   public static boolean hasCustomData(ItemStack itemStack) {
      NbtComponent var1 = getInPatch(itemStack, DataComponentTypes.CUSTOM_DATA);
      return var1 != null && !var1.isEmpty();
   }

   public static String textToJsonRaw(Text text) {
      if (text == null) {
         return null;
      } else {
         try {
            JsonElement var1 = (JsonElement)TextCodecs.CODEC.encodeStart(registry().getOps(JsonOps.INSTANCE), text).getOrThrow(JsonParseException::new);
            return d.toJson(var1);
         } catch (Throwable var2) {
            return null;
         }
      }
   }

   private static NbtCompound createBukkitValue(NbtCompound nbt) {
      if (nbt.get(g) instanceof NbtCompound var2) {
         return var2;
      } else {
         NbtCompound var3 = new NbtCompound();
         nbt.put(g, var3);
         return var3;
      }
   }

   public static boolean matchItemWithoutLore(ItemStack stack1, ItemStack stack2) {
      if (!stack1.isOf(stack2.getItem())) {
         return false;
      } else if (stack1.isEmpty()) {
         return stack2.isEmpty();
      } else if (stack2.isEmpty()) {
         return false;
      } else {
         Reference2ObjectMap var2 = stack1.components.changedComponents;
         Reference2ObjectMap var3 = stack2.components.changedComponents;
         if (var2 != null && var3 != null) {
            HashMap var4 = new HashMap(var2);
            HashMap var5 = new HashMap(var3);
            Optional var6 = (Optional)var4.remove(DataComponentTypes.LORE);
            Optional var7 = (Optional)var5.remove(DataComponentTypes.LORE);
            return (var6 == null ? var7 == null || var7 == Optional.empty() : var7 != null && var7.isPresent()) && var4.equals(var5);
         } else {
            return var2 == var3;
         }
      }
   }

   public static <T> Identifier solveDynamic(RegistryEntry<T> entry) {
      return ((RegistryKey)entry.getKey().get()).getValue();
   }

   public static void setCustomName(ItemStack stack, Text text) {
      setOrRemoveChange(stack, DataComponentTypes.CUSTOM_NAME, Objects.equals(text, Text.empty()) ? null : text);
   }

   public static NbtCompound U(ItemStack stack) {
      NbtCompound var1 = getCustomDataReadOnly(stack);
      return getBukkitValue(var1);
   }

   public static ItemStack newItem(String type, String id) {
      String[] var2 = type.split("[$]");
      String var3 = var2[0].toLowerCase(Locale.ROOT);
      if ("grass".equals(var3) || "short_grass".equals(var3)) {
         var3 = isGrassOrShortGrass ? "grass" : "short_grass";
      }

      Item var4 = (Item)Registries.ITEM.get(new Identifier("minecraft", var3));
      ItemStack var5 = new ItemStack(var4);
      if (var2.length == 2 && var4 == Items.PLAYER_HEAD) {
         setOrRemoveChange(var5, DataComponentTypes.PROFILE, BukkitItemStackUtils.e(var2[1]));
      }

      if (id != null && !"null".equals(id)) {
         Z(var5, id);
      }

      return var5.isEmpty() ? null : var5;
   }

   public static void applyItemEnchant(ItemStack stack, ItemEnchantmentsComponent ench) {
      setOrRemoveChange(stack, DataComponentTypes.ENCHANTMENTS, Objects.equals(ench, ItemEnchantmentsComponent.DEFAULT) ? null : ench);
   }

   public static Predicate<ItemStack> c(ComponentType<?> type) {
      return stack -> g(stack, type);
   }

   public static VHideFlag[] getHideFlags() {
      return TooltipHideFlag_v1_21_1.values();
   }

   public static String aa(ItemStack stack) {
      NbtCompound var1 = U(stack);
      return var1 == null ? null : getSfIdFromBukkitValues(var1);
   }

   public static <T> RegistryEntry<T> findEntry(Registry<T> registry, Identifier id) {
      return registry.getOrEmpty(id).<RegistryEntry<T>>map(registry::getEntry).orElse(null);
   }

   public static String Y(NbtCompound nbt) {
      NbtCompound var1 = getBukkitValue(nbt);
      return var1 == null ? null : getSfIdFromBukkitValues(var1);
   }

   public static boolean matchItemWithout(ItemStack stack1, ItemStack stack2, boolean matchDur, boolean matchEnch, boolean matchLore) {
      if (stack1.isEmpty()) {
         return stack2.isEmpty();
      } else if (stack2.isEmpty()) {
         return false;
      } else if (!stack1.isOf(stack2.getItem())) {
         return false;
      } else if (matchDur && matchEnch && matchLore) {
         return ItemStack.areItemsAndComponentsEqual(stack1, stack2);
      } else {
         Reference2ObjectMap var5 = stack1.components.changedComponents;
         Reference2ObjectMap var6 = stack2.components.changedComponents;
         if (var5 != null && var6 != null) {
            HashMap var7 = new HashMap(var5);
            HashMap var8 = new HashMap(var6);
            if (!matchLore) {
               Optional var9 = (Optional)var7.remove(DataComponentTypes.LORE);
               Optional var10 = (Optional)var8.remove(DataComponentTypes.LORE);
               if (var9 == null ? var10 != null && var10 != Optional.empty() : var10 == null || !var10.isPresent()) {
                  return false;
               }
            }

            if (!matchEnch) {
               Optional var11 = (Optional)var7.remove(DataComponentTypes.ENCHANTMENTS);
               Optional var12 = (Optional)var8.remove(DataComponentTypes.ENCHANTMENTS);
               if (var11 == null ? var12 != null && var12 != Optional.empty() : var12 == null || !var12.isPresent()) {
                  return false;
               }
            }

            if (!matchDur) {
               var7.remove(DataComponentTypes.DAMAGE);
               var8.remove(DataComponentTypes.DAMAGE);
            }

            return var7.equals(var8);
         } else {
            return var5 == var6;
         }
      }
   }

   @Nonnull
   public static DynamicRegistryManager registry() {
      if (a.getNetworkHandler() != null) {
         return c = a.getNetworkHandler().getRegistryManager();
      } else if (c != null) {
         return c;
      } else {
         if (b == null) {
            b = ClientDynamicRegistryType.createCombinedDynamicRegistries().getCombinedRegistryManager();
         }

         return b;
      }
   }

   public static NbtCompound getBukkitValue(@Nonnull NbtCompound nbt) {
      return nbt.contains(g, 10) ? (nbt.get(g) instanceof NbtCompound var2 ? var2 : null) : null;
   }

   static {
      for (EquipmentSlot var3 : EquipmentSlot.values()) {
         e.put(var3.getName(), var3);
      }

      f = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true);
      g = "PublicBukkitValues";
      h = "slimefun:slimefun_item";
      isGrassOrShortGrass = Registries.ITEM.get(new Identifier("minecraft", "grass")) != Items.AIR;
      j = new NbtCompound(ImmutableMap.of());
   }

   public static ItemEnchantmentsComponent getStoredEnchantment(ItemStack stack) {
      ItemEnchantmentsComponent var1 = getInPatch(stack, DataComponentTypes.STORED_ENCHANTMENTS);
      return var1 == null ? ItemEnchantmentsComponent.DEFAULT : var1;
   }

   public static <T> void setOrRemoveChange(ItemStack stack, ComponentType<T> type, @Nullable T val) {
      if (stack != null && !stack.isEmpty()) {
         ComponentMapImpl var3 = stack.components;
         Reference2ObjectMap var4 = var3.changedComponents;
         if (var4 == null) {
            return;
         }

         boolean var5;
         if (val == null) {
            var5 = var4.containsKey(type);
         } else {
            Optional var6 = (Optional)var4.get(type);
            if (var6 != null && Objects.equals(val, var6.orElse(null))) {
               var5 = false;
            } else {
               var5 = true;
            }
         }

         if (var5) {
            var3.onWrite();
            var4 = var3.changedComponents;
            if (val == null) {
               var4.remove(type);
            } else {
               var4.put(type, Optional.of(val));
            }
         }
      }
   }

   public static void applyEntityModifier(ItemStack stack, AttributeModifiersComponent data) {
      setOrRemoveChange(stack, DataComponentTypes.ATTRIBUTE_MODIFIERS, Objects.equals(data, AttributeModifiersComponent.DEFAULT) ? null : data);
   }

   public static void setStoredEnchantment(ItemStack stack, ItemEnchantmentsComponent enchantments) {
      setOrRemoveChange(stack, DataComponentTypes.STORED_ENCHANTMENTS, Objects.equals(enchantments, ItemEnchantmentsComponent.DEFAULT) ? null : enchantments);
   }

   public static List<Text> z(ItemStack stack) {
      LoreComponent var1 = getInPatch(stack, DataComponentTypes.LORE);
      return var1 == null ? List.of() : var1.lines();
   }

   public static ItemStack J(ItemStack stack, boolean keepNBT, boolean keepDur, boolean keepEnchant) {
      return getCleanedItem(stack, -999, keepNBT, keepDur, keepEnchant);
   }

   public static String getSfIdFromBukkitValues(NbtCompound ntb) {
      return ntb == null ? null : (ntb.get(h) instanceof NbtString var2 ? var2.asString() : null);
   }

   public static void mapCustomData(ItemStack itemStack, UnaryOperator<NbtCompound> updater) {
      NbtComponent var2 = getInPatch(itemStack, DataComponentTypes.CUSTOM_DATA);
      NbtCompound var3;
      if (var2 == null) {
         var3 = new NbtCompound();
      } else {
         var3 = var2.copyNbt();
      }

      var3 = updater.apply(var3);
      if (var3 != null && !var3.isEmpty()) {
         setOrRemoveChange(itemStack, DataComponentTypes.CUSTOM_DATA, new NbtComponent(var3));
      } else {
         setOrRemoveChange(itemStack, DataComponentTypes.CUSTOM_DATA, null);
      }
   }

   public static void T(ItemStack itemStack, Consumer<NbtCompound> updater) {
      NbtComponent var2 = getInPatch(itemStack, DataComponentTypes.CUSTOM_DATA);
      NbtCompound var3;
      if (var2 == null) {
         var3 = new NbtCompound();
      } else {
         var3 = var2.copyNbt();
      }

      updater.accept(var3);
      if (var3 != null && !var3.isEmpty()) {
         setOrRemoveChange(itemStack, DataComponentTypes.CUSTOM_DATA, new NbtComponent(var3));
      } else {
         setOrRemoveChange(itemStack, DataComponentTypes.CUSTOM_DATA, null);
      }
   }

   public static ItemStack getCleanedItem(ItemStack stack, int setAmount, boolean keepNBT, boolean keepDur, boolean keepEnchant) {
      ItemStack var5 = stack.getItem().getDefaultStack();
      if (!keepNBT) {
         if (setAmount != -999) {
            var5.setCount(setAmount);
         }

         return var5;
      } else {
         ItemStack var6 = stack.copy();
         if (setAmount != -999) {
            var6.setCount(setAmount);
         }

         if (!keepDur) {
            setOrRemoveChange(var6, DataComponentTypes.DAMAGE, null);
         }

         if (!keepEnchant) {
            setOrRemoveChange(var6, DataComponentTypes.ENCHANTMENTS, null);
            setOrRemoveChange(var6, DataComponentTypes.STORED_ENCHANTMENTS, null);
         }

         return var6;
      }
   }

   public static Text jsonRawToText(String jsonRaw) {
      try {
         if (jsonRaw == null) {
            return null;
         } else {
            JsonElement var1 = JsonParser.parseString(jsonRaw);
            return var1 == null ? null : (Text)TextCodecs.CODEC.parse(registry().getOps(JsonOps.INSTANCE), var1).getOrThrow(JsonParseException::new);
         }
      } catch (Throwable var2) {
         return null;
      }
   }

   public static void E(ItemStack stack, ItemEnchantmentsComponent enchantments) {
      setOrRemoveChange(stack, DataComponentTypes.ENCHANTMENTS, Objects.equals(enchantments, ItemEnchantmentsComponent.DEFAULT) ? null : enchantments);
   }

   public static AttributeModifiersComponent getEntityModifier(ItemStack stack) {
      AttributeModifiersComponent var1 = getInPatch(stack, DataComponentTypes.ATTRIBUTE_MODIFIERS);
      return var1 == null ? AttributeModifiersComponent.DEFAULT : var1;
   }

   public static void Z(ItemStack stack, String id) {
      if (id != null && !id.isEmpty()) {
         mapCustomData(stack, nbt -> {
            NbtCompound var2 = createBukkitValue(nbt);
            var2.putString(h, id);
            return nbt;
         });
      } else {
         mapCustomData(stack, nbt -> {
            NbtCompound var1 = getBukkitValue(nbt);
            if (var1 != null) {
               var1.remove(h);
               if (var1.isEmpty()) {
                  nbt.remove(g);
               }
            }

            return nbt;
         });
      }
   }

   public static void setDamage(ItemStack stack, int damage) {
      if (stack != ItemStack.EMPTY) {
         if (damage > 0) {
            stack.setDamage(damage);
         } else {
            setOrRemoveChange(stack, DataComponentTypes.DAMAGE, null);
         }
      }
   }
}
