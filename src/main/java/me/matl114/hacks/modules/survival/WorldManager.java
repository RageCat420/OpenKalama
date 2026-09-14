package me.matl114.hacks.modules.survival;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import me.matl114.accessors.interfaces.EntityInventory;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.task.ServerStorage;
import me.matl114.hacks.modules.task.TaskSubHelperJ;
import me.matl114.hacks.utils.world.BlockStorage;
import me.matl114.hacks.utils.world.EntityStorage;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.NBTUtils;
import me.matl114.utils.algorithms.SerialExecutor;
import me.matl114.utils.world.BlockLocation;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

public class WorldManager extends BaseModule {
   public static WorldManager INSTANCE;
   public static final String Ow = "kalama:villager/trade_info";
   public final Map<UUID, SurvivalSubHelperH> Or;
   public static final String Oy = "kalama:trade_list";
   int lh;
   public final FlagRef enableBlockEntities;
   public static final String Ox = "kalama:trade_lock";
   public ModulePath aD = makePath(Configs.o, "world-manager");
   public static final String Ov = "kalama:world_manager/block_data_storage";
   public final Map<BlockLocation, SurvivalSubHelperI> Os;
   private final Executor Ot;
   public static final String Ou = "kalama:world_manager/entity_data_storage";
   public final FlagRef Op = this.builder(this.aD.add("enable-entity"), Boolean.class).defaultValue(true).build();

   public void Qe(Event<TaskSubHelperJ> event) {
      TaskSubHelperJ var2 = (TaskSubHelperJ)event.b;
      DynamicRegistryManager var3 = event.getArgs(1);
      this.Or.clear();
      this.Os.clear();

      for (EntityStorage var5 : var2.m()) {
         if (var5.contains("kalama:world_manager/entity_data_storage")) {
            SurvivalSubHelperH var6 = var5.c("kalama:world_manager/entity_data_storage", SurvivalSubHelperH.CODEC, var3);
            if (var6 != null) {
               this.Or.put(var6.e(), var6);
            }
         }
      }

      for (BlockStorage var8 : var2.g()) {
         if (var8.contains("kalama:world_manager/block_data_storage")) {
            SurvivalSubHelperI var9 = var8.c("kalama:world_manager/block_data_storage", SurvivalSubHelperI.CODEC, var3);
            if (var9 != null) {
               this.Os.put(BlockLocation.YN(var8.getDimension(), var8.v()), var9);
            }
         }
      }
   }

   public void Qf(Event<TaskSubHelperJ> event) {
      DynamicRegistryManager var2 = event.getArgs(1);
      TaskSubHelperJ var3 = (TaskSubHelperJ)event.b;
      if (this.Op.getValue()) {
         for (Entry var5 : this.Or.entrySet()) {
            SurvivalSubHelperH var6 = (SurvivalSubHelperH)var5.getValue();
            if (var6.i()) {
               EntityStorage var7 = var3.q((UUID)var5.getKey(), true);
               if (!var6.isEmpty()) {
                  var7.put("kalama:world_manager/entity_data_storage", var6, SurvivalSubHelperH.CODEC, var2);
               } else {
                  var7.f("kalama:world_manager/entity_data_storage", null);
               }

               var6.dirty = false;
            }
         }
      }

      if (this.enableBlockEntities.get()) {
         for (Entry var10 : this.Os.entrySet()) {
            BlockLocation var11 = (BlockLocation)var10.getKey();
            SurvivalSubHelperI var12 = (SurvivalSubHelperI)var10.getValue();
            if (var12.h()) {
               if (!var12.isEmpty()) {
                  BlockStorage var8 = var3.n(var11.vS(), var11.YO(), true);
                  var8.put("kalama:world_manager/block_data_storage", var12, SurvivalSubHelperI.CODEC, var2);
               } else {
                  BlockStorage var13 = var3.n(var11.vS(), var11.YO(), false);
                  if (var13 != null) {
                     var13.f("kalama:world_manager/block_data_storage", null);
                     ServerStorage.Jk(var13, true);
                  }
               }

               var12.dirty = false;
            }
         }
      }
   }

   public static boolean aeZ(MerchantScreenHandler handler) {
      return handler.getExperience() == 0 && handler.getLevelProgress() <= 1;
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(ServerStorage.JP(), this::Qe);
      this.registerListener(Listener.V(), this::afe);
      this.registerListener(Listener.aU(), this::afh);
      this.registerListener(Listener.au().c(EntityType.VILLAGER), this::afd);
      this.registerListener(Listener.aq().getChannel(SetTradeOffersS2CPacket.class), this::afc);
      this.registerListener(ServerStorage.JQ(), this::Qf);
      this.registerListener(ServerStorage.JP(), this::Qe);
   }

   public WorldManager() {
      super("WorldManager");
      this.enableBlockEntities = this.builder(this.aD.add("enable-block-entities"), Boolean.class).defaultValue(true).build();
      this.Or = new ConcurrentHashMap<>();
      this.Os = new ConcurrentHashMap<>();
      this.Ot = new SerialExecutor(CompletableFuture::runAsync);
      this.lh = 0;
      INSTANCE = this;
   }

   public SurvivalSubHelperI afg(BlockEntity entity, boolean create) {
      BlockLocation var3 = BlockLocation.YM(entity.getWorld(), entity.getPos());
      return this.Os.compute(var3, (k, v) -> {
         if (v == null) {
            return create ? new SurvivalSubHelperI(entity.getType()) : null;
         } else {
            return (SurvivalSubHelperI)(v.f() == entity.getType() ? v : null);
         }
      });
   }

   public void aeX(VillagerEntity villager, boolean lock) {
      SurvivalSubHelperH var3 = this.aff(villager, true);
      NbtCompound var4 = var3.h();
      NbtCompound var5 = NBTUtils.ensurePath(var4, "kalama:villager/trade_info");
      var5.putByte("kalama:trade_lock", (byte)(lock ? 1 : 0));
      var3.a();
   }

   public void afe(Event<ClientPlayerEntity> event) {
      if (!checkNull()) {
         if (++this.lh >= 10) {
            this.lh = 0;
            Iterator var2 = this.Os.entrySet().iterator();

            while (var2.hasNext()) {
               Entry var3 = (Entry)var2.next();
               BlockLocation var4 = (BlockLocation)var3.getKey();
               if (var4.isLocationLoaded(mc.world)) {
                  BlockPos var5 = var4.YO();
                  BlockEntity var6 = mc.world.getBlockEntity(var5);
                  if (var6 != null && var6.getType() == ((SurvivalSubHelperI)var3.getValue()).f()) {
                     ((SurvivalSubHelperI)var3.getValue()).update(var5, var6);
                  } else {
                     var2.remove();
                     this.afi(var4);
                  }
               }
            }

            for (Entry var8 : this.Or.entrySet()) {
               if (mc.world.getEntityLookup().get((UUID)var8.getKey()) instanceof LivingEntity var9) {
                  ((SurvivalSubHelperH)var8.getValue()).c(var9);
               }
            }
         }
      }
   }

   private void afi(BlockLocation location) {
      BlockStorage var2 = ServerStorage.IZ().n(location.vS(), location.YO(), false);
      if (var2 != null) {
         var2.f("kalama:world_manager/block_data_storage", null);
         ServerStorage.Jk(var2, true);
      }
   }

   public SurvivalSubHelperH aff(LivingEntity entity, boolean create) {
      if (entity.getHealth() > 0.0F) {
         return create ? this.Or.computeIfAbsent(entity.getUuid(), SurvivalSubHelperH::new) : this.Or.get(entity.getUuid());
      } else {
         return null;
      }
   }

   public boolean aeY(VillagerEntity villager) {
      SurvivalSubHelperH var2 = this.aff(villager, false);
      return var2 != null && NBTUtils.resolve(var2.h(), "kalama:villager/trade_info", "kalama:trade_lock") instanceof NbtByte var4 && var4.byteValue() == 1;
   }

   @Nullable
   public List<SurvivalSubHelperS> afb(VillagerEntity villager) {
      SurvivalSubHelperH var2 = this.aff(villager, false);
      return var2 != null && NBTUtils.resolve(var2.h(), "kalama:villager/trade_info", "kalama:trade_list") instanceof NbtList var4
         ? NBTUtils.toValue(var4, Codec.list(SurvivalSubHelperS.CODEC), mc.getNetworkHandler().getRegistryManager())
         : null;
   }

   public void afh(Event<Entity> event) {
      if (!checkNull()) {
         Entity var2 = (Entity)event.b;
         if (var2 instanceof LivingEntity var3 && var3.getHealth() <= 0.0F) {
            this.Or.remove(var2.getUuid());
            TaskSubHelperJ var4 = ServerStorage.IZ();
            if (var4 != null) {
               EntityStorage var5 = var4.q(var2.getUuid(), false);
               if (var5 != null) {
                  var5.f("kalama:world_manager/entity_data_storage", null);
               }
            }
         }
      }
   }

   public void afa(VillagerEntity villager, List<SurvivalSubHelperS> trades) {
      SurvivalSubHelperH var3 = this.aff(villager, true);
      NbtCompound var4 = var3.h();
      NbtCompound var5 = NBTUtils.ensurePath(var4, "kalama:villager/trade_info");
      NBTUtils.putValue(var5, "kalama:trade_list", trades, Codec.list(SurvivalSubHelperS.CODEC), mc.getNetworkHandler().getRegistryManager());
      var3.a();
   }

   public void afc(Event<SetTradeOffersS2CPacket> eventSetTrade) {
      if (((SetTradeOffersS2CPacket)eventSetTrade.b).getSyncId() == mc.player.currentScreenHandler.syncId
         && mc.player.currentScreenHandler instanceof EntityInventory var3
         && var3.getOwner() instanceof VillagerEntity var4) {
         this.Ot
            .execute(
               () -> {
                  TradeOfferList var3x = ((SetTradeOffersS2CPacket)eventSetTrade.b).getOffers();
                  boolean var4x = ((SetTradeOffersS2CPacket)eventSetTrade.b).getExperience() == 0
                     && ((SetTradeOffersS2CPacket)eventSetTrade.b).getLevelProgress() <= 1;
                  this.aeX(var4, !var4x);
                  List var5 = var3x.stream()
                     .map(
                        offer -> new SurvivalSubHelperS(
                           offer.getSellItem(),
                           offer.getFirstBuyItem().itemStack(),
                           offer.getSecondBuyItem().<ItemStack>map(TradedItem::itemStack).orElse(ItemStack.EMPTY),
                           offer.getMaxUses()
                        )
                     )
                     .toList();
                  this.afa(var4, var5);
               }
            );
      }
   }

   public void afd(Event<SerializedEntry<?>> eventDataUpdate) {
      if (eventDataUpdate.getArgs(0) instanceof VillagerEntity var3
         && ((SerializedEntry)eventDataUpdate.b).id() == 18
         && ((SerializedEntry)eventDataUpdate.b).value() instanceof VillagerData var5) {
         this.Ot.execute(() -> {
            VillagerProfession var3x = var5.getProfession();
            if (!Objects.equals(var3x, VillagerProfession.NONE) && !Objects.equals(var3x, VillagerProfession.NITWIT)) {
               if (var5.getLevel() > 1) {
                  this.aeX(var3, true);
               }
            } else {
               SurvivalSubHelperH var4 = this.aff(var3, false);
               if (var4 != null) {
                  var4.h().remove("kalama:villager/trade_info");
                  var4.a();
               }
            }
         });
      }
   }
}
