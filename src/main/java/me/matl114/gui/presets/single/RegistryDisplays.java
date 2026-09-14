package me.matl114.gui.presets.single;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.RegistryUtils;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleManager.SimpleSpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent.Builder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;

public class RegistryDisplays {
   private static final Random c = new LocalRandom(999L);
   public static final ItemStack a = new ItemStack(Items.ANVIL);
   public static Map<Class<?>, RegistryDisplays$IIcon<?>> d = ImmutableMap.<Class<?>, RegistryDisplays$IIcon<?>>builder()
      .put(Item.class, RegistryDisplays$IIcon.<ItemConvertible>c(ItemStack::new))
      .put(Block.class, RegistryDisplays$IIcon.<ItemConvertible>c(ItemStack::new))
      .put(EntityAttribute.class, RegistryDisplays$IIcon.c(v -> a))
      .put(Enchantment.class, RegistryDisplays$IIcon.c(RegistryDisplays::createEnchantmentIcon))
      .<BlockEntityType>put(BlockEntityType.class, RegistryDisplays$IIcon.c(s -> {
         if (((BlockEntityType)s).blocks.isEmpty()) {
            return RegistryDisplays.b;
         } else {
            List<?> var1 = ((BlockEntityType)s).blocks.stream().toList();
            int var2 = (int)(System.currentTimeMillis() / 1000L) % var1.size();
            return new ItemStack((ItemConvertible)var1.get(var2));
         }
      }))
      .<EntityType<?>>put(EntityType.class, RegistryDisplays$IIcon.c(v -> {
         Item var1 = EntityUtils.c((EntityType<?>)v);
         return new ItemStack(var1 == null ? Items.PIG_SPAWN_EGG : var1);
      }))
      .put(StatusEffect.class, RegistryDisplays$IIcon.<StatusEffect>d(effect -> {
         RegistryEntry<StatusEffect> var1 = Registries.STATUS_EFFECT.getEntry(effect);
         return getEffectTexture(var1);
      }))
      .put(Potion.class, RegistryDisplays$IIcon.<Potion>c(v -> PotionContentsComponent.createStack(Items.POTION, Registries.POTION.getEntry(v))))
      .<ParticleType<?>>put(ParticleType.class, RegistryDisplays$IIcon.d(v -> {
         Identifier var1 = Registries.PARTICLE_TYPE.getId((ParticleType<?>)v);
         ParticleManager var2 = MinecraftClient.getInstance().particleManager;
         Map var3 = var2.spriteAwareFactories;
         SimpleSpriteProvider var4 = (SimpleSpriteProvider)var3.get(var1);
         return var4 != null ? var4.getSprite(c) : null;
      }))
      .build();
   public static final ItemStack b = new ItemStack(Items.AIR);

   public static <T> Text getDisplay(@Nonnull T val) {
      Text var1 = guessTranslation(val);
      if (var1 != null) {
         return var1;
      } else {
         RegistryKey var2 = RegistryUtils.i(val);
         if (var2 != null) {
            Registry var3 = (Registry)Registries.REGISTRIES.get(var2);
            if (var3 != null) {
               Identifier var4 = var3.getId(val);
               if (var4 != null) {
                  return Text.translatable(var2.getValue().getPath() + ".minecraft." + var4.getPath());
               }
            }
         }

         return Text.literal(val.toString());
      }
   }

   public static <T> RenderHandler of(Registry<T> registry, T value, Text name, Identifier identifier) {
      RegistryDisplays$IIcon var4 = g(registry);
      return new KalamaHelperHelperF<>(name, identifier, var4, value);
   }

   public static ItemStack createEnchantmentIcon(Enchantment enchantment) {
      ItemStack var1 = new ItemStack(Items.ENCHANTED_BOOK);
      RegistryEntry var2 = RegistryUtils.getRegistryEntry(ItemStackUtils.registry(), RegistryKeys.ENCHANTMENT, enchantment);
      if (var2 == null) {
         return var1;
      } else {
         Builder var3 = new Builder(ItemEnchantmentsComponent.DEFAULT);
         var3.add(var2, 1);
         ItemEnchantmentsComponent var4 = var3.build();
         var1.set(DataComponentTypes.STORED_ENCHANTMENTS, var4);
         return var1;
      }
   }

   private static <T> Text guessTranslation(T val) {
      if (val instanceof StatusEffect var1) {
         return var1.getName();
      } else if (val instanceof EntityAttribute var2) {
         return Text.translatable(var2.getTranslationKey());
      } else if (val instanceof Enchantment var3) {
         return var3.description();
      } else if (val instanceof BlockEntityType var4) {
         return Text.literal(Registries.BLOCK_ENTITY_TYPE.getId(var4).getPath());
      } else if (val instanceof EntityType var5) {
         return Text.translatable(var5.getTranslationKey());
      } else if (val instanceof Item var6) {
         return var6.getName();
      } else if (val instanceof Block var7) {
         return var7.getName();
      } else if (val instanceof SoundEvent var8) {
         return Text.translatable("subtitles." + var8.getId().getPath());
      } else {
         return val instanceof Potion var9 ? Text.translatable(Items.POTION.getTranslationKey() + ".effect." + var9.baseName) : null;
      }
   }

   public static <T> Text b(Registry<T> registry, @Nonnull T val) {
      Text var2 = guessTranslation(val);
      if (var2 != null) {
         return var2;
      } else {
         Identifier var3 = registry.getId(val);
         return var3 != null ? Text.translatable(registry.getKey().getValue().getPath() + ".minecraft." + var3.getPath()) : Text.literal(val.toString());
      }
   }

   public static Sprite getEffectTexture(RegistryEntry<StatusEffect> effect) {
      return MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(effect);
   }

   public static <T> RegistryDisplays$IIcon<T> g(Registry<T> registryClass) {
      return (RegistryDisplays$IIcon<T>)d.getOrDefault(RegistryUtils.getRegistryType(registryClass), RegistryDisplays$IIcon.b);
   }

   public static <T> RegistryDisplays$IIcon<T> f(Class<T> registryClass) {
      return (RegistryDisplays$IIcon<T>)d.getOrDefault(registryClass, RegistryDisplays$IIcon.b);
   }
}
