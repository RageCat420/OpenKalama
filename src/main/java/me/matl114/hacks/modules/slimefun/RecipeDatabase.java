package me.matl114.hacks.modules.slimefun;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.catchers.TimedPacketCatcherImpl;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.managers.Configs;
import me.matl114.managers.config.ConfigLoader;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.StringRef;
import me.matl114.utils.Debug;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.itemdb.ItemStackDataWithAmount;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

public class RecipeDatabase extends BaseModule {
   boolean nV;
   private Map<String, SlimefunSubHelperO> Iq;
   private boolean Ii;
   private final Map<String, SlimefunSubHelperJ> Ij;
   private static final Set<?> In = Set.of(
      ScreenHandlerType.GENERIC_9X6, ScreenHandlerType.GENERIC_9X3, ScreenHandlerType.GENERIC_9X4, ScreenHandlerType.GENERIC_9X5
   );
   public final FlagRef lockExisting;
   private final Codec<Map<String, SlimefunSubHelperJ>> Im;
   public final FlagRef saveData;
   public static final ItemStack Ip = new ItemStack(Items.BARRIER);
   static Direction[] in = new Direction[]{Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST};
   static Direction[] io = new Direction[]{Direction.NORTH, Direction.WEST};
   private static final String Ig = "sfhelper-configs/recipes/craft-types.json";
   private final Map<String, SlimefunSubHelperQ> Ih;
   public final FlagRef lockCurrentData;
   public final FlagRef ae;
   private boolean Ik;
   private Map<Block, Set<SlimefunSubHelperO>> Ir;
   private static final String If = "sfhelper-configs/recipes/recipe-data.json";
   public final ModulePath HZ = makePath(Configs.p, "recipe-record");
   private final Codec<Map<String, SlimefunSubHelperQ>> Il;
   Gson nZ;
   public final NBTRef<Regex> rpTitle;
   private static final int[] recipeSlots = new int[]{3, 4, 5, 12, 13, 14, 21, 22, 23};
   public final StringRef multiblockPattern;

   public boolean WP() {
      return this.nV;
   }

   public SlimefunSubHelperJ WI(String id) {
      return this.Wz() ? this.Ij.get(id) : null;
   }

   public Map<String, SlimefunSubHelperO> WL() {
      return this.Wz() ? Collections.unmodifiableMap(this.Iq) : Map.of();
   }

   public SlimefunSubHelperQ WH(String rid) {
      return this.Wz() ? this.Ih.get(rid) : null;
   }

   public void WC(SlimefunSubHelperJ entry) {
      if (this.Wz()) {
         this.WA(entry);
      }
   }

   public void onLoad() {
      this.nV = true;

      try {
         this.Ih.clear();
         String var1 = ConfigLoader.loadExternalJson("sfhelper-configs/recipes/craft-types.json");
         JsonObject var2 = (JsonObject)(Object)this.nZ.fromJson(var1, JsonObject.class);
         Map var3 = (Map)((Pair)(Object)this.Il.decode(JsonOps.INSTANCE, var2).getOrThrow()).getFirst();
         this.Ih.putAll(var3);
         this.Ii = false;
      } catch (Throwable var5) {
         Debug.a("反序列化RecipeTypes数据失败, 错误:");
         Debug.f(var5);
      }

      try {
         this.Ij.clear();
         String var6 = ConfigLoader.loadExternalJson("sfhelper-configs/recipes/recipe-data.json");
         JsonObject var7 = (JsonObject)(Object)this.nZ.fromJson(var6, JsonObject.class);
         Map var8 = (Map)((Pair)(Object)this.Im.decode(JsonOps.INSTANCE, var7).getOrThrow()).getFirst();
         this.Ij.putAll(var8);
         this.Ik = false;
      } catch (Throwable var4) {
         Debug.a("反序列化RecipeEntry数据失败, 错误:");
         Debug.f(var4);
      }

      this.WN();
      this.Ij.forEach((k, v) -> {
         if (Pattern.matches(this.multiblockPattern.get(), v.JR)) {
            this.WO(v);
         }
      });
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(InvTasks.as().y(), (java.util.function.Consumer)ev -> this.onLoad());
      this.registerListener(InvTasks.as().z(), (java.util.function.Consumer)ev -> this.onSave());
      this.registerListener(InvTasks.as().A(), (java.util.function.Consumer)ev -> this.wc());
      this.Ih.clear();
      this.Ij.clear();
      if (InvTasks.as().x()) {
         this.onLoad();
      }

      this.registerListener(Listener.ar().getChannel(OpenScreenS2CPacket.class), this::onScreenOpen);
   }

   public void onScreenContent(GenericContainerScreen screen) {
      if (mc.player != null) {
         DefaultedList var2 = ((GenericContainerScreenHandler)screen.getScreenHandler()).slots;
         if (var2.size() >= 27
            && ((Slot)var2.get(2)).getStack().isEmpty()
            && ((Slot)var2.get(11)).getStack().isEmpty()
            && ((Slot)var2.get(20)).getStack().isEmpty()
            && ((Slot)var2.get(15)).getStack().isEmpty()
            && ((Slot)var2.get(17)).getStack().isEmpty()
            && ((Slot)var2.get(25)).getStack().isEmpty()
            && !((Slot)var2.get(16)).getStack().isEmpty()) {
            ItemStack var3 = ((Slot)var2.get(16)).getStack();
            String var4 = ItemStackUtils.aa(var3);
            if (var4 != null) {
               boolean var5 = false;
               if (this.Wz()) {
                  if (this.Ij.containsKey(var4)) {
                     SlimefunSubHelperJ var11 = this.Ij.get(var4);
                     if (var11.Ct().isEmpty() && !((Slot)var2.get(16)).getStack().isEmpty()) {
                        var5 = true;
                     } else if (this.lockExisting.get()) {
                        var5 = false;
                     } else {
                        ItemStack[] var13 = var11.Zb();
                        if (var13.length == 9) {
                           for (int var8 = 0; var8 < 9; var8++) {
                              ItemStack var9 = ((Slot)var2.get(recipeSlots[var8])).getStack();
                              if (isLockedItem(var9)) {
                                 return;
                              }

                              if (!ItemStackUtils.matchItemWithoutLore(var13[var8], var9)) {
                                 var5 = true;
                                 break;
                              }
                           }
                        } else {
                           var5 = true;
                        }
                     }
                  } else {
                     for (int var6 = 0; var6 < 9; var6++) {
                        ItemStack var7 = ((Slot)var2.get(recipeSlots[var6])).getStack();
                        if (isLockedItem(var7)) {
                           return;
                        }
                     }

                     var5 = true;
                  }
               }

               if (var5) {
                  ItemStack var12 = ((Slot)var2.get(10)).getStack();
                  String var14 = var12.isEmpty() ? "NULL_RECIPE" : var12.getName().getString().replace("§.", "");
                  this.WD(var14, var12);
                  ArrayList var15 = new ArrayList();

                  for (int var16 = 0; var16 < 9; var16++) {
                     var15.add(ItemStackDataWithAmount.of(((Slot)var2.get(recipeSlots[var16])).getStack()));
                  }

                  ItemStackDataWithAmount var17 = ItemStackDataWithAmount.of(((Slot)var2.get(16)).getStack());
                  SlimefunSubHelperJ var10 = new SlimefunSubHelperJ(var14, var4, var15, var17);
                  this.WC(var10);
               }
            }
         }
      }
   }

   public void WD(String rid, ItemStack icon) {
      if (this.Wz()) {
         this.putRecipeType(rid, icon);
      }
   }

   private void WO(SlimefunSubHelperJ entry) {
      SlimefunSubHelperO var2 = SlimefunSubHelperO.of(entry.Zb(), entry.id);
      if (var2 != null) {
         this.Iq.put(entry.id, var2);

         for (Block var5 : var2.getPotentials()) {
            this.Ir.computeIfAbsent(var5, b -> new ReferenceArraySet()).add(var2);
         }
      }
   }

   private void putRecipeType(String rid, ItemStack icon) {
      if (!this.Ih.containsKey(rid)) {
         ItemStack var3 = icon.isEmpty() ? Ip.copy() : icon.copy();
         var3.setCount(1);
         this.Ih.put(rid, new SlimefunSubHelperQ(rid, ItemStackDataWithAmount.of(var3)));
         this.Ii = true;
      }
   }

   public void onSave() {
      if (this.saveData.get()) {
         if (this.Ii) {
            this.Ii = false;

            try {
               JsonElement var1 = (JsonElement)(Object)this.Il.encodeStart(JsonOps.INSTANCE, this.Ih).getOrThrow();
               CompletableFuture.runAsync(() -> {
                  String var2x = this.nZ.toJson(var1);

                  try {
                     ConfigLoader.saveToFile("sfhelper-configs/recipes/craft-types.json", var2x);
                  } catch (IOException var4x) {
                     Debug.f(var4x);
                  }
               });
            } catch (Throwable var3) {
               Debug.a("序列化RecipeTypes数据失败, 错误:");
               Debug.f(var3);
            }
         }

         if (this.Ik) {
            this.Ik = false;

            try {
               JsonElement var4 = (JsonElement)(Object)this.Im.encodeStart(JsonOps.INSTANCE, this.Ij).getOrThrow();
               CompletableFuture.runAsync(() -> {
                  String var2x = this.nZ.toJson(var4);

                  try {
                     ConfigLoader.saveToFile("sfhelper-configs/recipes/recipe-data.json", var2x);
                  } catch (IOException var4x) {
                     Debug.f(var4x);
                  }
               });
            } catch (Throwable var2) {
               Debug.a("序列化RecipeEntry数据失败, 错误:");
               Debug.f(var2);
            }
         }
      }
   }

   private void WA(SlimefunSubHelperJ entry) {
      this.Ij.put(entry.id, entry);
      this.Ik = true;
      if (Pattern.matches(this.multiblockPattern.get(), entry.JR)) {
         this.WO(entry);
      }
   }

   private void WN() {
      if (this.Iq != null) {
         this.Iq.clear();
      }

      this.Iq = new Object2ReferenceOpenHashMap();
      if (this.Ir != null) {
         this.Ir = new Reference2ReferenceOpenHashMap();
      }
   }

   public void wc() {
      this.nV = false;
   }

   public boolean Wz() {
      return !this.nV ? InvTasks.as().a() : true;
   }

   public RecipeDatabase() {
      super("RecipeDatabase");
      this.ae = this.flagBuilder(this.HZ.addEnable()).build();
      this.saveData = this.builder(this.HZ.add("save-data"), FlagRef.TYPE).defaultValue(true).build();
      this.lockExisting = this.flagBuilder(this.HZ.add("lock-existing")).build();
      this.lockCurrentData = this.flagBuilder(this.HZ.add("lock-current-data")).build();
      this.multiblockPattern = this.builder(this.HZ.add("multiblock-pattern"), StringRef.TYPE)
         .defaultValue("^(多方块结构|MultiBlock)$")
         .validator(Configs.a)
         .build();
      this.rpTitle = this.builder(this.HZ.add("rp-title"), Regex.class).defaultValue(new Regex("^(Slimefun 指南.*)$")).build();
      this.nV = false;
      this.Ih = new LinkedHashMap<>();
      this.Ii = false;
      this.Ij = new LinkedHashMap<>();
      this.Ik = false;
      this.Il = Codec.unboundedMap(Codec.STRING, SlimefunSubHelperQ.dt);
      this.Im = Codec.unboundedMap(Codec.STRING, SlimefunSubHelperJ.CODEC);
      this.nZ = new GsonBuilder().disableHtmlEscaping().create();
      this.Iq = new Object2ReferenceOpenHashMap();
      this.Ir = new Reference2ReferenceOpenHashMap();
      this.bindFlag(this.ae);
   }

   public Set<SlimefunSubHelperO> WM(Block block) {
      return this.Wz() ? this.Ir.getOrDefault(block, Set.of()) : Set.of();
   }

   public Map<String, SlimefunSubHelperJ> WK() {
      return this.Wz() ? Collections.unmodifiableMap(this.Ij) : Map.of();
   }

   public Map<String, SlimefunSubHelperQ> WJ() {
      return this.Wz() ? Collections.unmodifiableMap(this.Ih) : Map.of();
   }

   public void onScreenOpen(Event<OpenScreenS2CPacket> event) {
      if (mc.player != null) {
         HandledScreen var2 = ClientPlayerAccess.of(mc.player).getServerOpeningScreen();
         if (var2 != null && var2.getScreenHandler().syncId == ((OpenScreenS2CPacket)event.b).getSyncId()) {
            if (this.ae.get()
               && !this.lockCurrentData.get()
               && In.contains(var2.getScreenHandler().getType())
               && var2 instanceof GenericContainerScreen var3
               && var2.getTitle() != null) {
               String var4 = var2.getTitle().getString();
               if (var4 != null) {
                  var4 = var4.replaceAll("§.", "");
                  if (this.rpTitle.get().test(var4)) {
                     Listener.C(new TimedPacketCatcherImpl<InventoryS2CPacket>(InventoryS2CPacket.class, 20, packetEvent -> {
                        InventoryS2CPacket var3x = packetEvent.e();
                        if (var3x.getSyncId() == ((GenericContainerScreenHandler)var3.getScreenHandler()).syncId) {
                           mc.executeSync(() -> this.onScreenContent(var3));
                           return true;
                        } else {
                           return false;
                        }
                     }));
                  }
               }
            }
         }
      }
   }

   public static boolean isLockedItem(ItemStack lockIcon) {
      if (lockIcon.getItem() == Items.BARRIER) {
         for (String var3 : ItemStackUtils.B(lockIcon)) {
            if (var3.contains("已锁定")) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
