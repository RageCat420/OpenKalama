package me.matl114.hacks.modules.models;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import me.matl114.events.Event;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.Debug;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.ResourceUtils;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class NewStyleModel extends BaseModule {
   public static final String cm = "enchanted_book/";
   public static final String cn = "_max";
   private Map<Item, Optional<BakedModel>> cq;
   public final FlagRef enableNewStyleNbt;
   private Map<Identifier, Optional<BakedModel>> cp;
   public final FlagRef enableNewStyleItem;
   public static final String co = "_over";
   public final Map<Item, ModelIdentifier> cr;
   public static String ck = "new-version";
   public final ModulePath cg = makePath(Configs.q, "new-style-item");
   public final FlagRef ch = this.builder(this.cg.add("enable-enchant-book"), Boolean.class).defaultValue(true).build();
   public static String cl = "kalama";

   public static boolean isNewVersion(ItemStack stack) {
      return ItemStackUtils.getCustomDataReadOnly(stack).contains(ck);
   }

   public boolean shouldEnableNewStyle(ItemStack item) {
      return this.enableNewStyleItem.get() && this.cq.containsKey(item.getItem()) || this.enableNewStyleNbt.get() && isNewVersion(item);
   }

   public NewStyleModel() {
      super("NewStyleModel");
      this.enableNewStyleItem = this.builder(this.cg.add("enable-new-style-item"), Boolean.class).defaultValue(false).build();
      this.enableNewStyleNbt = this.builder(this.cg.add("enable-new-style-nbt"), Boolean.class).defaultValue(true).build();
      this.cp = new HashMap<>();
      this.cq = new HashMap<>();
      this.cr = new HashMap<>();
   }

   public void fV(Event<Set<Identifier>> event) {
      ((Set)event.e()).addAll(ResourceUtils.b(event.getArgs(0), "enchanted_book"));
      ((Set)event.e()).addAll(ResourceUtils.b(event.getArgs(0), "new-version"));
   }

   public void onRefreshCache(Event<ResourceManager> event) {
      this.cp.clear();
      this.cq.clear();

      for (Item var3 : Registries.ITEM) {
         Identifier var4 = new Identifier(cl, ck + "/" + Registries.ITEM.getId(var3).getPath());
         Optional var5 = RenderListener.c(var4);
         if (var5.isPresent()) {
            this.cq.put(var3, var5);
            Debug.e("Loading new-version model", var4);
         }
      }
   }

   public void fU(Event<Set<Identifier>> event) {
      if (event.getArgs(1).equals(new Identifier("minecraft", "blocks"))) {
         ((Set)event.e()).addAll(ResourceUtils.c(event.getArgs(0), "enchanted_book"));
         ((Set)event.e()).addAll(ResourceUtils.c(event.getArgs(0), "new-version"));
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(RenderListener.n(), this::onModelOverride);
      this.registerListener(RenderListener.u(), this::onRefreshCache);
      this.registerListener(RenderListener.w(), this::fU);
      this.registerListener(RenderListener.v(), this::fV);
   }

   public static ItemStack ofNewVersion(ItemStack stack) {
      ItemStackUtils.T(stack, nbtCompound -> nbtCompound.putBoolean(ck, true));
      return stack;
   }

   public void onModelOverride(Event<BakedModel> event) {
      if (event.b == null) {
         ItemStack var2 = event.getArgs(0);
         if (this.ch.get()) {
            ItemEnchantmentsComponent var3 = ItemStackUtils.getStoredEnchantment(var2);
            if (var3 != null && !var3.isEmpty()) {
               Optional var4 = var3.getEnchantmentEntries().stream().findFirst();
               if (var4.isPresent()) {
                  Entry var5 = (Entry)var4.get();
                  Enchantment var6 = (Enchantment)((RegistryEntry)var5.getKey()).value();
                  Optional var7 = ((RegistryEntry)var5.getKey()).getKey();
                  if (var6 != null && var7.isPresent()) {
                     Identifier var8 = ((RegistryKey)var7.get()).getValue();
                     int var9 = var6.getMaxLevel();
                     int var10 = var5.getIntValue();
                     if (var10 == 0) {
                        return;
                     }

                     Identifier var11 = var10 == 1
                        ? new Identifier(cl, "enchanted_book/" + var8.getPath())
                        : (
                           var10 == var9
                              ? new Identifier(cl, "enchanted_book/" + var8.getPath() + "_max")
                              : (
                                 var10 > var9
                                    ? new Identifier(cl, "enchanted_book/" + var8.getPath() + "_over")
                                    : new Identifier(cl, "enchanted_book/" + var8.getPath() + "_" + var10)
                              )
                        );
                     Optional var12 = this.cp.computeIfAbsent(var11, RenderListener::c);
                     var12.ifPresent(event::context);
                  }
               }
            }
         }

         if (this.shouldEnableNewStyle(var2)) {
            Optional var13 = this.cq.get(var2.getItem());
            if (var13 != null && var13.isPresent()) {
               event.context((BakedModel)var13.get());
            }
         }
      }
   }
}
