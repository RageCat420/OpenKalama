package me.matl114.hacks.modules.inv;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.managers.Configs;
import me.matl114.managers.config.ConfigLoader;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.itemdb.ItemStackData;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SaveItem extends BaseModule {
   Gson nZ;
   public Codec<Map<String, ItemStackData>> nY;
   public KeyBindRef saveSlotItem;
   private boolean nV;
   public final ModulePath mH = makePath(Configs.l, "inventory");
   public static final String SAVE_PATH = "sfhelper-configs/recipes/saved-items.json";
   boolean kK;
   Map<String, ItemStackData> nW;

   public SaveItem() {
      super("SaveItem");
      this.saveSlotItem = this.hotkey(this.mH.add("save-slot-item")).defaultValue(new MultiKeyBind()).registerHotkey(HotKeyUtils.e(this::saveItem)).build();
      this.nV = false;
      this.nW = new LinkedHashMap<>();
      this.nY = (Codec)Codec.list(Codec.STRING)
         .xmap(
            lst -> lst.stream().collect(Collectors.toMap(Function.identity(), InvTasks.as()::getDataFromCodecId, (k, v) -> v, LinkedHashMap::new)),
            mp -> mp.keySet().stream().toList()
         )
         .optionalFieldOf("saved-ids", new LinkedHashMap<String, ItemStackData>())
         .codec();
      this.nZ = new GsonBuilder().disableHtmlEscaping().create();
      this.kK = false;
   }

   public void addSaveItem(ItemStack item) {
      this.vZ();
      Pair var2 = InvTasks.as().getOrRegisterItem(item);
      if (this.nW.containsKey(var2.getFirst())) {
         Debug.b(Text.literal("该物品已经保存过了!").formatted(Formatting.YELLOW));
      } else {
         this.nW.put((String)var2.getFirst(), (ItemStackData)var2.getSecond());
         this.kK = true;
         Debug.b(Text.literal("成功保存物品!").formatted(Formatting.GREEN));
      }
   }

   public void removeSavedItem(ItemStack item) {
      this.vZ();
      String var2 = InvTasks.as().getItemIdOrNull(item);
      if (var2 != null && this.nW.remove(var2) != null) {
         this.kK = true;
         Debug.b(Text.literal("已经成功移除这个保存物品").formatted(Formatting.GREEN));
      }
   }

   public boolean saveItem() {
      ClientPlayerEntity var1 = mc.player;
      if (var1 == null) {
         return false;
      } else {
         ItemStack var2 = ScreenUtils.getSelectingOrHandItem();
         if (this.vZ()) {
            if (var2 != null && !var2.isEmpty()) {
               this.addSaveItem(var2);
               return true;
            }

            if (var2 != null) {
               Debug.b(Text.literal("不能保存空物品").formatted(Formatting.RED));
            }
         } else {
            Debug.b(Text.literal("数据库正在加载,请稍后重试..."));
         }

         return false;
      }
   }

   public void wc() {
      this.nV = false;
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(InvTasks.as().y(), (java.util.function.Consumer)ev -> this.onLoad());
      this.registerListener(InvTasks.as().z(), (java.util.function.Consumer)ev -> this.onSave());
      this.registerListener(InvTasks.as().A(), (java.util.function.Consumer)ev -> this.wc());
   }

   public boolean vZ() {
      return !this.nV ? InvTasks.as().a() : true;
   }

   public void onSave() {
      if (this.kK) {
         this.kK = false;

         try {
            JsonElement var1 = (JsonElement)(Object)this.nY.encodeStart(JsonOps.INSTANCE, this.nW).getOrThrow();
            CompletableFuture.runAsync(() -> {
               String var2x = this.nZ.toJson(var1);

               try {
                  ConfigLoader.saveToFile("sfhelper-configs/recipes/saved-items.json", var2x);
               } catch (IOException var4) {
                  Debug.f(var4);
               }
            });
         } catch (Throwable var2) {
            Debug.a("序列化SavedItems数据失败, 错误:");
            Debug.f(var2);
         }
      }
   }

   public void onLoad() {
      try {
         String var1 = ConfigLoader.loadExternalJson("sfhelper-configs/recipes/saved-items.json");
         JsonElement var2 = (JsonElement)(Object)this.nZ.fromJson(var1, JsonElement.class);
         this.nW.clear();
         Map var3 = (Map)((Pair)(Object)this.nY.decode(JsonOps.INSTANCE, var2).getOrThrow()).getFirst();
         this.nW.putAll(var3);
         this.kK = false;
      } catch (Throwable var4) {
         Debug.f(var4);
      }

      this.nV = true;
   }

   public Map<String, ItemStackData> getSavedItemDataMap() {
      this.vZ();
      return Collections.unmodifiableMap(this.nW);
   }
}
