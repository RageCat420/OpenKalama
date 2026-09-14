package me.matl114.hacks.modules.models;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.matl114.events.Event;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.FileManager;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.ListRef;
import me.matl114.utils.Debug;
import me.matl114.utils.ItemStackUtils;
import me.matl114.versioned.api.VItem;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.yaml.snakeyaml.Yaml;

public class SlimefunModels extends BaseModule {
   public final FlagRef enableItemModelOverride;
   private final Map<ModelIdentifier, Optional<BakedModel>> iA;
   private final Map<String, ModelIdentifier> iC;
   public final ModulePath iv = makePath(Configs.q, "model-config");
   public final ListRef pathPatternForSlimefunModel;
   private final Map<String, CustomModelDataComponent> iB;
   public final ModulePath iw = makePath(Configs.q, "slimefun-models");
   public final FlagRef ix = this.builder(this.iv.add("enable-slimefun-cmd-override"), Boolean.class).defaultValue(true).build();
   private static final String OUR_NAMESPACE = "kalama";

   public void loadCustomModelDatas() {
      try {
         File var1 = FileManager.loadOrUseInternal("slimefun-item-model.yml");
         Yaml var2 = new Yaml();

         try (FileReader var3 = new FileReader(var1)) {
            Map var4 = var2.load(var3);

            for (Entry var6 : ((java.util.Set<Entry>)(var4).entrySet())) {
               try {
                  int var7 = (Integer)var6.getValue();
                  if (var7 != 0) {
                     this.iB.put((String)var6.getKey(), VItem.w().createModelData(var7));
                  }
               } catch (ClassCastException var9) {
                  Debug.e("Custom Model data could not be loaded :", var6.getKey());
               }
            }
         } catch (Exception var11) {
            Debug.a("AN INTERNAL ERROR WHILE READING CONFIG ITEM-MODEL");
            Debug.f(var11);
         }

         Debug.a("Slimefun Custom Model Data load successfully");
      } catch (Throwable var12) {
         Debug.a("error while loading CustomModelDatas");
         Debug.f(var12);
      }
   }

   public Collection<Identifier> walkThroughResourcePacks(ResourceManager resourceManager, boolean allLoad) {
      this.iC.clear();
      this.iA.clear();
      LinkedHashSet var3 = new LinkedHashSet();
      List<ResourcePack> var4 = resourceManager.streamResourcePacks().toList();
      List<String> var5 = this.pathPatternForSlimefunModel.get();
      String var6 = var5.stream().map(i -> "(" + i + ")").collect(Collectors.joining("|"));
      Predicate var7 = Pattern.compile(var6).asMatchPredicate();

      for (ResourcePack var9 : var4) {
         String var10 = var9.getId();
         if (!var10.equals("minecraft") && !var10.equals("realms") && !var10.startsWith("fabric-") && !var10.equals("fabric") && !var10.equals("vanilla")) {
            if (var10.equals("kalama")) {
               var9.findResources(ResourceType.CLIENT_RESOURCES, "kalama", "models/slimefunitem", (i, j) -> {
                  String var4x = i.getNamespace();
                  if (i.getPath().endsWith(".json")) {
                     String var5x = i.getPath().replaceFirst("^models/", "").replaceAll(".json$", "");
                     Identifier var6x = new Identifier(var4x, var5x);
                     String[] var7x = var5x.split("/");
                     this.iC.put(var7x[var7x.length - 1].toUpperCase(Locale.ROOT), RenderListener.b(var6x));
                     var3.add(var6x);
                  }
               });
            } else {
               for (String var13 : var9.getNamespaces(ResourceType.CLIENT_RESOURCES)) {
                  var9.findResources(ResourceType.CLIENT_RESOURCES, var13, "models", (i, j) -> {
                     String var6x = i.getNamespace();
                     if (i.getPath().endsWith(".json")) {
                        String var7x = i.getPath().replaceFirst("^models/", "").replaceAll(".json$", "");
                        String[] var8 = var7x.split("/");
                        String var9x = var8[var8.length - 1];
                        new Identifier(var6x, var9x);
                        Identifier var11 = new Identifier(var6x, var7x);
                        Identifier var12 = "item".equals(var8[0]) ? new Identifier(var6x, String.join("/", Arrays.copyOfRange(var8, 1, var8.length))) : var11;
                        ModelIdentifier var13x = RenderListener.b(var11);
                        if ("kalama".equals(var13) || var7.test(var12.toString())) {
                           this.iC.put(var8[var8.length - 1].toUpperCase(Locale.ROOT), var13x);
                           var3.add(var11);
                        }
                     }
                  });
               }
            }
         }
      }

      return var3;
   }

   public void fV(Event<Set<Identifier>> event) {
      if (this.enableItemModelOverride.get()) {
         ((Set)event.e()).addAll(this.walkThroughResourcePacks(event.getArgs(0), false));
      }
   }

   public void mb(Event<ResourceManager> resourceManager) {
      this.iB.clear();
      this.loadCustomModelDatas();
   }

   public void fT(Event<BakedModel> event) {
      if (event.b == null) {
         if (this.enableItemModelOverride.get()) {
            ItemStack var2 = event.getArgs(0);
            NbtCompound var3 = ItemStackUtils.getCustomDataReadOnly(var2);

            try {
               String var4 = ItemStackUtils.Y(var3);
               if (var4 != null) {
                  ModelIdentifier var5 = this.iC.get(var4);
                  if (var5 != null) {
                     Optional var6 = this.iA.computeIfAbsent(var5, RenderListener::d);
                     if (var6.isPresent()) {
                        event.context((BakedModel)var6.get());
                        return;
                     }
                  }
               }
            } catch (Throwable var7) {
            }
         }
      }
   }

   public SlimefunModels() {
      super("SlimefunModels");
      this.enableItemModelOverride = this.builder(this.iv.add("enable-item-model-override"), Boolean.class).defaultValue(true).build();
      this.pathPatternForSlimefunModel = this.builder(this.iw.add("path-pattern-for-slimefun-model"), ListRef.TYPE)
         .defaultValue(List.of("^kalama:slimefunitem/.*$", "^kalama:test/.*$"))
         .listValidator(Configs.a)
         .build();
      this.iA = new HashMap<>();
      this.iB = new HashMap<>();
      this.iC = new HashMap<>();
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(RenderListener.u(), this::mb);
      this.registerListener(RenderListener.v(), this::fV);
      this.registerListener(RenderListener.n(), this::fT);
      this.registerListener(RenderListener.m(), this::onItemOverride);
   }

   public void onItemOverride(Event<ItemStack> event) {
      if (this.ix.get()) {
         ItemStack var2 = (ItemStack)event.e();
         if (!var2.isEmpty()) {
            String var3 = ItemStackUtils.aa(var2);
            if (var3 != null && this.iB.containsKey(var3)) {
               CustomModelDataComponent var4 = this.iB.get(var3);
               ItemStack var5 = var2.copy();
               ItemStackUtils.setOrRemoveChange(var5, DataComponentTypes.CUSTOM_MODEL_DATA, var4);
               event.context(var5);
            }
         }
      }
   }
}
