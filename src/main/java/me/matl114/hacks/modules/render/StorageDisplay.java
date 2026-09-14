package me.matl114.hacks.modules.render;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import me.matl114.bukkit.BukkitItemStack;
import me.matl114.bukkit.BukkitItemStackUtils;
import me.matl114.bukkit.KalamaHelperHelperT;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.model.GuiModel;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.models.NewStyleModel;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.ResourceUtils;
import me.matl114.utils.inventory.ItemStackSample;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class StorageDisplay extends BaseModule {
   protected static String ni;
   protected static String ny;
   protected static String nm;
   protected static String ns;
   private int updateTick;
   protected static String nq;
   protected static String nj;
   private static final Map<EntityType<?>, ItemStack> ne = new HashMap<>();
   protected static String no;
   protected static final HashMap<String, List<Item>> nD;
   public static HashMap<String, ItemStack> nC;
   public final FlagRef enableShulkerDisplay;
   public static final int ES_PREFIX_LEN;
   protected static String nt;
   protected static String nv;
   protected static String nw;
   public final EnumRef<StorageDisplay$Mode> displayMode;
   private final long updateIntervalMs;
   protected static char[] nz;
   public final ModulePath iv = makePath(Configs.i, "itemstack-display.storage-display");
   public static HashMap<String, String> nB;
   protected static String nu;
   public static final String nF = "ELECTRIC_SPAWNER_";
   public final FlagRef enableInfoDisplay;
   protected static String np;
   protected static String nr;
   public static final Set<String> nx;
   protected static final HashMap<String, List<ItemStack>> nE;
   protected static String nk;
   protected static String nn;
   protected static String nl;
   private final Map<ItemStack, RenderSubHelperF> nf;
   public final FlagRef nb = this.builder(this.iv.add("enable-storage-display"), Boolean.class).defaultValue(true).build();
   protected static char[] nA;

   public static String handlePureChickenDNAInfo(ItemStack item) {
      NbtCompound var1 = ItemStackUtils.getCustomDataReadOnly(item);
      String var2 = ItemStackUtils.Y(var1);
      if (var2 != null && var2.startsWith("GCE_") && (var1 = ItemStackUtils.getBukkitValue(var1)) != null && var1.contains(ny)) {
         try {
            if (var1.get(ny) instanceof NbtIntArray var4) {
               int[] var10 = var4.getIntArray();
               int var5 = var10.length;
               StringBuilder var6 = new StringBuilder();

               for (int var7 = 0; var7 < 6; var7++) {
                  if ((var5 <= var7 || var10[var7] != 0) && var10[var7] != 1 && var10[var7] != 3) {
                     var6.append("??");
                  } else if (var10[var7] == 0) {
                     var6.append(nz[var7]).append(nz[var7]);
                  } else {
                     var6.append(nA[var7]).append(nA[var7]);
                  }
               }

               return var6.toString();
            }
         } catch (Throwable var8) {
         }
      }

      return null;
   }

   public static BukkitItemStack getNetworkStoraged(NbtCompound tag) {
      try {
         if (tag != null) {
            if (tag.contains(ni)) {
               if (tag.get(ni) instanceof NbtCompound var2 && var2.get(nk) instanceof NbtByteArray var3) {
                  byte[] var6 = var3.getByteArray();
                  return BukkitItemStackUtils.a.fromPrimitive(var6);
               }
            } else if (tag.contains(nq)) {
               if (tag.get(nq) instanceof NbtByteArray var10) {
                  byte[] var13 = var10.getByteArray();
                  return BukkitItemStackUtils.a.fromPrimitive(var13);
               }
            } else if (tag.contains(nj) && tag.get(nj) instanceof NbtCompound var11 && var11.get(nl) instanceof NbtByteArray var14) {
               byte[] var9 = var14.getByteArray();
               return BukkitItemStackUtils.a.fromPrimitive(var9);
            }
         }

         return null;
      } catch (Exception var4) {
         return null;
      }
   }

   public static BukkitItemStack getInfinityStorage(NbtCompound tag) {
      try {
         if (tag != null && tag.contains(nu) && tag.get(nu) instanceof NbtString var2) {
            String var4 = var2.asString();
            return KalamaHelperHelperT.c(var4);
         } else {
            return null;
         }
      } catch (Exception var3) {
         return null;
      }
   }

   private static boolean hasAnyStorage(NbtCompound tag) {
      return tag != null && ItemStackUtils.getSfIdFromBukkitValues(tag) != null && tag.getKeys().size() > 1 && tag.getKeys().stream().anyMatch(nx::contains);
   }

   public void ve(Event<List<GuiModel>> event) {
      if (this.nb.get()) {
         ItemStack var2 = event.getArgs(0);
         NbtCompound var3 = ItemStackUtils.U(var2);
         if (hasAnyStorage(var3)) {
            RenderSubHelperF var4 = this.asyncUpdateItemInfo(
               var2,
               st -> {
                  BukkitItemStack var2x;
                  if ((var2x = getNetworkStoraged(var3)) == null
                     && (var2x = getNetworkBlueprint(var3)) == null
                     && (var2x = getLogitechSingularity(var3)) == null
                     && (var2x = getInfinityStorage(var3)) == null
                     && (var2x = getFinalTechStorage(var3)) == null) {
                     var2x = null;
                  }

                  if (var2x == null) {
                     return null;
                  } else {
                     ItemStack var3x = BukkitItemStackUtils.getAsDisplayItem(var2x);
                     return var3x != null && !var3x.isEmpty() ? List.of(var3x) : null;
                  }
               }
            );
            List var5 = var4.itemStack;
            this.appendContainerInfos((List<GuiModel>)event.b, var5);
         }
      }
   }

   public void uZ(Event<List<GuiModel>> event) {
      if (this.enableInfoDisplay.get()) {
         ItemStack var2 = event.getArgs(0);
         EntityType var3 = EntityUtils.getStoredEntityType(var2);
         if (var3 != null) {
            ItemStack var4 = getRenderingEntityContent(var3);
            if (var4 != null) {
               ((List)event.e()).add(GuiModel.c(var4));
            }
         }
      }
   }

   public static ItemStack handleElectricSpawnerInfo(ItemStack item, String sfid) {
      if (sfid != null && sfid.startsWith("ELECTRIC_SPAWNER_")) {
         String var2 = sfid.substring(ES_PREFIX_LEN);
         EntityType var3 = (EntityType)Registries.ENTITY_TYPE.getOrEmpty(new Identifier("minecraft", var2.toLowerCase(Locale.ROOT))).orElse(null);
         if (var3 != null) {
            return getRenderingEntityContent(var3);
         }
      }

      return null;
   }

   public static ItemStack getRenderingEntityContent(EntityType<?> typed) {
      return ne.containsKey(typed) ? ne.get(typed).copy() : null;
   }

   public void vh(Event<Set<Identifier>> reloadEvent) {
      if (reloadEvent.getArgs(1).equals(new Identifier("minecraft", "blocks"))) {
         ((Set)reloadEvent.e()).addAll(ResourceUtils.c(reloadEvent.getArgs(0), "gce"));
      }
   }

   public void vf(Event<List<GuiModel>> event) {
      if (this.enableInfoDisplay.get()) {
         ItemStack var2 = event.getArgs(0);
         String var3 = ItemStackUtils.aa(var2);
         if (var3 != null) {
            ItemStack var4;
            if ((var4 = vq(var2)) == null && (var4 = vu(var2, var3)) == null && (var4 = handleElectricSpawnerInfo(var2, var3)) == null) {
               return;
            }

            ((List)event.e()).add(GuiModel.c(var4));
         }
      }
   }

   public static ItemStack vq(ItemStack item) {
      String var1 = handlePureChickenDNAInfo(item);
      return var1 != null ? nC.get(var1).copy() : null;
   }

   private void appendContainerInfos(List<GuiModel> event, List<ItemStack> stack) {
      if (stack != null && !stack.isEmpty()) {
         switch ((StorageDisplay$Mode)this.displayMode.get()) {
            case MOST:
               if (!stack.isEmpty()) {
                  event.add(GuiModel.c((ItemStack)stack.get(0)));
               }
               break;
            case ONLY_ONE:
               if (stack.size() == 1) {
                  event.add(GuiModel.c((ItemStack)stack.get(0)));
               }
               break;
            case ALL:
               stack.stream().map(GuiModel::c).forEach(event::add);
         }
      }
   }

   public static ItemStack vu(ItemStack item, String sfid) {
      if (sfid != null && sfid.startsWith("CLT_")) {
         Item var2 = vs(sfid);
         if (var2 != null) {
            return new ItemStack(var2);
         }

         ItemStack var3 = vt(sfid);
         if (var3 != null) {
            return var3;
         }
      }

      return null;
   }

   public void onContainerVanilla(Event<List<GuiModel>> event) {
      if (this.enableShulkerDisplay.get()) {
         ItemStack var2 = event.getArgs(0);
         ContainerComponent var3 = (ContainerComponent)var2.get(DataComponentTypes.CONTAINER);
         if (var3 != null) {
            RenderSubHelperF var4 = this.asyncUpdateItemInfo(
               var2,
               st0 -> {
                  ItemStack var1 = (ItemStack)st0;
                  ContainerComponent var2x = (ContainerComponent)var1.get(DataComponentTypes.CONTAINER);
                  if (var2x == null) {
                     return null;
                  } else {
                     LinkedHashMap var3x = new LinkedHashMap();

                     label33:
                     for (ItemStack var5x : var2x.iterateNonEmpty()) {
                        if (!var5x.isEmpty()) {
                           for (Entry var7 : ((java.util.Set<Entry>)(var3x).entrySet())) {
                              if (ItemStackUtils.matchItemWithout(var5x, ((ItemStackSample)var7.getKey()).fS(), false, false, false)) {
                                 var7.setValue((Integer)var7.getValue() + var5x.getCount());
                                 continue label33;
                              }
                           }

                           var3x.put(ItemStackSample.of(var5x), var5x.getCount());
                        }
                     }

                     return var3x.entrySet()
                        .stream()
                        .sorted(Comparator.comparingInt(v -> -(Integer)v.getValue()))
                        .map(Entry::getKey)
                        .map(ItemStackSample::fS)
                        .toList();
                  }
               }
            );
            List var5 = var4.itemStack;
            this.appendContainerInfos((List<GuiModel>)event.b, var5);
         }
      }
   }

   static {
      for (EntityType var1 : Registries.ENTITY_TYPE) {
         Item var2 = EntityUtils.c(var1);
         if (var2 != null && var2 != Items.AIR) {
            ne.put(var1, NewStyleModel.ofNewVersion(new ItemStack(var2)));
         }
      }

      ni = "networks:quantum_storage";
      nj = "networks-changed:quantum_storage";
      nk = "networks:item";
      nl = "networks-changed:item";
      nm = "networks:ntw_blueprint";
      nn = "networks-changed:blueprint";
      no = "networks:output";
      np = "networks-changed:output";
      nq = "networks:item_mover_item";
      nr = "networks:amount";
      ns = "logitech:data";
      nt = "logitech:sin_item";
      nu = "infinityexpansion:item";
      nv = "finaltech-changed:item";
      nw = "finaltech:item";
      nx = Set.of(ni, nq, nj, nm, nn, nt, nu, nv, nw);
      ny = "geneticchickengineering:gce_pocket_chicken_dna";
      nz = new char[]{'b', 'c', 'd', 'f', 's', 'w'};
      nA = new char[]{'B', 'C', 'D', 'F', 'S', 'W'};
      nB = new RenderSubHelperR();
      nC = new RenderSubHelperRX();
      nD = new RenderSubHelperZ();
      nE = new RenderSubHelperB();
      ES_PREFIX_LEN = "ELECTRIC_SPAWNER_".length();
   }

   @Nonnull
   private RenderSubHelperF asyncUpdateItemInfo(ItemStack stack, Function<ItemStack, List<ItemStack>> func) {
      RenderSubHelperF var3 = this.nf.get(stack);
      if (var3 == null || var3.lastUpdated < System.currentTimeMillis() - 10000L) {
         if (var3 == null) {
            var3 = new RenderSubHelperF(System.currentTimeMillis(), null);
         } else {
            var3.lastUpdated = System.currentTimeMillis();
         }

         ItemStack var4 = stack.copyWithCount(1);
         RenderSubHelperF var5 = var3;
         this.nf.put(var4, var5);
         CompletableFuture.<List>supplyAsync(() -> (List)func.apply(var4)).thenAccept(s -> var5.itemStack = (List<ItemStack>)s);
      }

      return var3;
   }

   public static <T> List<T> ofNullableList(T... values) {
      List var1 = Arrays.stream(values).filter(Objects::nonNull).toList();
      return var1.isEmpty() ? null : var1;
   }

   private void vb(Event<Void> gameTick) {
      if (this.updateTick < 1200) {
         this.updateTick++;
      } else {
         this.updateTick = 0;
         Iterator var2 = this.nf.entrySet().iterator();

         while (var2.hasNext()) {
            RenderSubHelperF var3 = (RenderSubHelperF)((Entry)var2.next()).getValue();
            if (var3.lastUpdated < System.currentTimeMillis() - 10000L) {
               var2.remove();
            }
         }
      }
   }

   public static Item vs(String sfid) {
      List var1 = nD.get(sfid);
      return var1 == null ? null : (Item)var1.get(Math.abs(Tasks.d()) % var1.size());
   }

   public static ItemStack vt(String id) {
      List var1 = nE.get(id);
      return var1 == null ? null : ((ItemStack)var1.get(Math.abs(Tasks.d()) % var1.size())).copy();
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(RenderListener.o(), this::uZ, 1005);
      this.registerListener(RenderListener.o(), this::onContainerVanilla, 1000);
      this.registerListener(RenderListener.o(), this::ve, 1000);
      this.registerListener(RenderListener.o(), this::vf, 1000);
      this.registerListener(RenderListener.n(), this::vi);
      this.registerListener(Listener.T(), this::vb);
      this.registerListener(RenderListener.w(), this::vh);
      this.registerListener(RenderListener.v(), this::vg);
   }

   public StorageDisplay() {
      super("StorageDisplay");
      this.enableInfoDisplay = this.builder(this.iv.add("enable-info-display"), Boolean.class).defaultValue(true).build();
      this.enableShulkerDisplay = this.flagBuilder(this.iv.add("enable-shulker-display")).build();
      this.displayMode = this.builder(this.iv.add("display-mode"), StorageDisplay$Mode.class).defaultValue(StorageDisplay$Mode.ALL).build();
      this.nf = new Object2ReferenceOpenCustomHashMap(new RenderSubHelperHX(this));
      this.updateTick = 0;
      this.updateIntervalMs = 10000L;
   }

   public static BukkitItemStack getFinalTechStorage(NbtCompound tag) {
      try {
         if (tag != null) {
            if (tag.contains(nw)) {
               if (tag.get(nw) instanceof NbtString var2) {
                  String var4 = var2.asString();
                  return KalamaHelperHelperT.c(var4);
               }
            } else if (tag.contains(nv) && tag.get(nv) instanceof NbtString var7) {
               String var6 = var7.asString();
               return KalamaHelperHelperT.c(var6);
            }
         }

         return null;
      } catch (Exception var3) {
         return null;
      }
   }

   public static BukkitItemStack getLogitechSingularity(NbtCompound tag) {
      try {
         if (tag != null && tag.contains(nt) && tag.get(nt) instanceof NbtCompound var2 && var2.get(ns) instanceof NbtByteArray var3) {
            byte[] var6 = var3.getByteArray();
            return BukkitItemStackUtils.a.fromPrimitive(var6);
         } else {
            return null;
         }
      } catch (Exception var4) {
         return null;
      }
   }

   public static BukkitItemStack getNetworkBlueprint(NbtCompound tag) {
      try {
         if (tag != null) {
            if (tag.contains(nm)) {
               if (tag.get(nm) instanceof NbtCompound var2 && var2.get(no) instanceof NbtByteArray var3) {
                  byte[] var6 = var3.getByteArray();
                  return BukkitItemStackUtils.a.fromPrimitive(var6);
               }
            } else if (tag.contains(np) && tag.get(nn) instanceof NbtCompound var10 && var10.get(np) instanceof NbtByteArray var11) {
               byte[] var9 = var11.getByteArray();
               return BukkitItemStackUtils.a.fromPrimitive(var9);
            }
         }

         return null;
      } catch (Exception var4) {
         return null;
      }
   }

   public void vg(Event<Set<Identifier>> reloadEvent) {
      ((Set)reloadEvent.e()).addAll(ResourceUtils.b(reloadEvent.getArgs(0), "gce"));
   }

   public void vi(Event<BakedModel> bakedModelEvent) {
      if (bakedModelEvent.e() == null) {
         if (this.enableInfoDisplay.get()) {
            ItemStack var2 = bakedModelEvent.getArgs(0);
            String var3 = handlePureChickenDNAInfo(var2);
            if (var3 != null) {
               String var4 = nB.get(var3);
               if (var4 != null) {
                  RenderListener.c(new Identifier("kalama", "gce/" + var4)).ifPresent(bakedModelEvent::context);
               }
            }
         }
      }
   }
}
