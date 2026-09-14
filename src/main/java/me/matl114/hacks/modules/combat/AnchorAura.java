package me.matl114.hacks.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.gui.elements.ResetButtonElement;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.mine.MineSubHelperD;
import me.matl114.hacks.modules.mine.PacketMine;
import me.matl114.hacks.utils.tasks.TimerExecutor;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.ExplosionUtils;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.KalamaHelperHelperHX;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.collections.FlagEntry;
import me.matl114.utils.collections.KalamaHelperHelperK;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class AnchorAura extends BaseModule {
   final TimerExecutor Bn;
   public final IntRef range;
   public final IntRef multiply;
   public final KeyBindRef J;
   public final FlagRef swingHand;
   public final KeyBindRef packetMineBridgeHotkey;
   private static final float Bf = 5.0F;
   public final EnumRef<Configs$LegalInteractMode> mode;
   public Map<BlockPos, CombatSubHelperLX> Bo;
   private static final double Bh = 1.0;
   int lh;
   public final DoubleRef interactRange;
   public final FlagRef airPlace;
   private static final double Bi = 1.0;
   public final FlagRef packetMineBridge;
   public final ModulePath aD = makePath(Configs.k, "combat-utils.anchor-aura");
   public final FlagRef zeroTickPlaceSupply;
   public final DoubleRef targetDamageThreshold;
   public List<PlayerEntity> Bp;
   public final DoubleRef selfDamageThreshold;
   public final FlagRef ae = this.flagBuilder(this.aD.addEnable()).build();
   private static final int Bg = 5;
   private List<Vec3i> zA;
   public final FlagRef zeroTickUse;
   public final IntRef delay;

   public void gu(Event<MineSubHelperD> eventPreMine) {
      if (this.ae.get() && !eventPreMine.d()) {
         BlockPos var2 = eventPreMine.getArgs(0);
         if (this.Bo.containsKey(var2) && mc.world.getBlockState(var2).getBlock() instanceof RespawnAnchorBlock) {
            eventPreMine.cancel();
         }
      }
   }

   private Box PD(PlayerEntity target) {
      Box var2 = target.getBoundingBox();
      Vec3d var3 = target.getPos();
      Vec3d var4 = PositionPredict.INSTANCE.attackPredictArgument.get().predict(target);
      if (var4 != null && !(var4.squaredDistanceTo(var3) <= MathUtils.a(1.0))) {
         Vec3d var5 = MovTasks.simulateMovement(target, var3, var4.subtract(var3), false);
         return var2.offset(var5);
      } else {
         return var2;
      }
   }

   public boolean NK(BlockPos pos) {
      return new Box(pos).squaredMagnitude(mc.player.getEyePos()) < MathUtils.a(this.interactRange.get());
   }

   public boolean Ny() {
      boolean var1 = this.NS(Items.RESPAWN_ANCHOR) != null;
      boolean var2 = this.NS(Items.GLOWSTONE) != null;
      if (var1 && var2) {
         return true;
      } else {
         this.Bn.b(100, () -> {
            if (!var1 && !var2) {
               this.logI18N("message.module.anchor-arua.no-item.double", new Object[]{Items.RESPAWN_ANCHOR.getName(), Items.GLOWSTONE.getName()});
            } else if (!var1) {
               this.logI18N("message.module.anchor-arua.no-item", new Object[]{Items.RESPAWN_ANCHOR.getName()});
            } else {
               this.logI18N("message.module.anchor-arua.no-item", new Object[]{Items.GLOWSTONE.getName()});
            }
         });
         return false;
      }
   }

   public int Pw(BlockPos targetPos, CombatSubHelperLX cache) {
      BlockState var3 = mc.world.getBlockState(targetPos);
      int var4 = 0;
      if (var3.getBlock() instanceof RespawnAnchorBlock) {
         int var5 = cache.powerLevel;
         BlockHitResult var6 = RaycastUtils.f(targetPos);
         if (var5 == 0) {
            KalamaHelperHelperK var7 = this.NS(Items.GLOWSTONE);
            if (var7 == null) {
               return var4;
            }

            Runnable var8 = InvExtra.INSTANCE.swapInventoryIndexToHand(var7.index());
            if (var8 == null) {
               return var4;
            }

            InteractionTasks.g(this.mode.get(), var6, Hand.MAIN_HAND, this.swingHand.get());
            var4++;
            var5 = 1;
            cache.powerLevel = var5;
            var8.run();
            if (!this.zeroTickUse.get()) {
               return var4;
            }
         }

         if (var5 > 0) {
            KalamaHelperHelperK var12 = this.Pv(Items.GLOWSTONE);
            Runnable var13 = InvExtra.INSTANCE.swapInventoryIndexToHand(var12.index());
            if (var13 == null) {
               return var4;
            }

            InteractionTasks.g(this.mode.get(), var6, Hand.MAIN_HAND, this.swingHand.get());
            var4++;
            byte var11 = 0;
            cache.powerLevel = var11;
            var13.run();
            if (this.zeroTickPlaceSupply.get()) {
               KalamaHelperHelperK var9 = this.NS(Items.RESPAWN_ANCHOR);
               if (var9 == null) {
                  return var4;
               }

               Runnable var10 = InvExtra.INSTANCE.swapInventoryIndexToHand(var9.index());
               if (var10 == null) {
                  return var4;
               }

               mc.world.setBlockState(targetPos, Blocks.AIR.getDefaultState());
               InteractionTasks.g(this.mode.get(), var6, Hand.MAIN_HAND, this.swingHand.get());
               var4++;
               var10.run();
            } else {
               this.Bo.remove(targetPos);
            }
         }
      }

      return var4;
   }

   private boolean Pr(BlockPos pos, Map<PlayerEntity, Double> damageMap) {
      return damageMap != null
         && !damageMap.isEmpty()
         && this.NJ(damageMap) > Double.NEGATIVE_INFINITY
         && damageMap.getOrDefault(mc.player, Double.POSITIVE_INFINITY) <= this.selfDamageThreshold.get()
         && this.PE(pos, damageMap);
   }

   public KalamaHelperHelperK<ItemStack> Pv(Item item) {
      KalamaHelperHelperK var2 = InventoryUtils.p(s -> !s.isOf(item), true, true);
      return var2 == null ? InventoryUtils.getSelectedItem() : var2;
   }

   public void PA(BlockPos pos, Map<PlayerEntity, Double> damageMap) {
      Object var3 = damageMap == null ? this.Pn(pos) : new LinkedHashMap(damageMap);
      this.Bo.put(pos, new CombatSubHelperLX(0, (Map<PlayerEntity, Double>)var3));
   }

   public void bk(Event<World> event) {
      this.Bo.clear();
      this.Bp = List.of();
   }

   public void Oe(Event<KalamaHelperHelperI<ModulePreset>> event) {
      this.mode.set(Configs$LegalInteractMode.getFromPreset((ModulePreset)((KalamaHelperHelperI)event.b).b()));
      this.airPlace.set(!((ModulePreset)((KalamaHelperHelperI)event.b).b()).hasAC());
   }

   public void Pp() {
      if (!this.Bo.isEmpty()) {
         HashMap var1 = new HashMap();
         Iterator var2 = this.Bo.entrySet().iterator();

         while (var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            BlockPos var4 = (BlockPos)var3.getKey();
            BlockState var5 = mc.world.getBlockState(var4);
            if (var5.getBlock() instanceof RespawnAnchorBlock && new Box(var4).squaredMagnitude(mc.player.getEyePos()) < MathUtils.a(this.interactRange.get())) {
               Map var6 = this.Po(var4, var1, true);
               if (this.Pq(var4, var6)) {
                  ((CombatSubHelperLX)var3.getValue()).damageCache = var6;
                  ((CombatSubHelperLX)var3.getValue()).powerLevel = (Integer)var5.get(RespawnAnchorBlock.CHARGES);
               } else {
                  var2.remove();
               }
            } else {
               var2.remove();
            }
         }

         for (Entry var9 : CombatManager.INSTANCE.Kt.entrySet()) {
            BlockPos var10 = (BlockPos)var9.getKey();
            BlockState var11 = (BlockState)var9.getValue();
            if (!this.Bo.containsKey(var10) && new Box(var10).squaredMagnitude(mc.player.getEyePos()) < MathUtils.a(this.interactRange.get())) {
               Map var7 = this.Po(var10, var1, true);
               if (this.Pq(var10, var7)) {
                  this.Bo.put(var10, new CombatSubHelperLX((Integer)var11.get(RespawnAnchorBlock.CHARGES), var7));
               }
            }
         }
      }
   }

   public KalamaHelperHelperK<ItemStack> NS(Item item) {
      return InventoryUtils.p(s -> s.isOf(item), true, false);
   }

   public void PB() {
      if (!this.Bp.isEmpty()) {
         HashMap var1 = new HashMap();
         BlockPos var2 = null;
         FlagEntry var3 = null;
         Map var4 = null;
         double var5 = Double.NEGATIVE_INFINITY;
         BlockPos var7 = mc.player.getBlockPos();

         for (Vec3i var9 : this.zA) {
            BlockPos var10 = var7.add(var9);
            if (this.NK(var10) && this.Ps(var10)) {
               FlagEntry var11 = InteractionTasks.l(var10, this.airPlace.get(), !this.mode.get().isLegal());
               if (InteractUtils.C(mc.player, var11)) {
                  Map var12 = this.Po(var10, var1, true);
                  if (this.Pq(var10, var12)) {
                     double var13 = this.NJ(var12);
                     if (var13 > var5) {
                        var2 = var10;
                        var3 = var11;
                        var4 = var12;
                        var5 = var13;
                     }
                  }
               }
            }
         }

         if (var2 != null && InteractUtils.C(mc.player, var3) && this.NK(((BlockHitResult)var3.val()).getBlockPos()) && this.Pz(var2, (BlockHitResult)var3.val())
            )
          {
            this.PA(var2, var4);
         }
      }
   }

   public void Pt(Event<Void> event) {
      if (!checkNull()) {
         if (this.ae.get()) {
            if (!InteractUtils.canRespawnAnchorExplode(mc.world)) {
               this.logI18N("message.module.anchor-arua.invalid-dimension", new Object[]{mc.world.getRegistryKey().getValue()});
               this.ae.set(false);
               return;
            }

            this.dd();
            this.Pp();
            if (++this.lh > this.delay.get() && !this.Bp.isEmpty()) {
               this.lh = 0;
               if (!this.Ny()) {
                  return;
               }

               if (!this.Pu()) {
                  this.PB();
               }
            }
         } else {
            this.Bo.clear();
            this.Bp = List.of();
         }
      }
   }

   private double NJ(Map<PlayerEntity, Double> damageMap) {
      double var2 = Double.NEGATIVE_INFINITY;

      for (PlayerEntity var5 : this.Bp) {
         if (EntityUtils.isEntityValid(var5) && var5 != mc.player) {
            Double var6 = (Double)damageMap.get(var5);
            if (var6 != null && var6 > var2) {
               var2 = var6;
            }
         }
      }

      return var2;
   }

   private boolean PE(BlockPos pos, Map<PlayerEntity, Double> damageMap) {
      if (this.Bp.isEmpty()) {
         return false;
      } else {
         PlayerEntity var3 = this.Bp.stream().filter(s -> s != mc.player).min(Comparator.comparingDouble(s -> pos.getSquaredDistance(s.getPos()))).orElse(null);
         return var3 == null ? false : damageMap.getOrDefault(var3, 0.0) > this.targetDamageThreshold.get();
      }
   }

   public boolean Py(BlockPos pos) {
      FlagEntry var2 = InteractionTasks.l(pos, this.airPlace.get(), !this.mode.get().isLegal());
      return InteractUtils.C(mc.player, var2) && this.NK(((BlockHitResult)var2.val()).getBlockPos()) ? this.Pz(pos, (BlockHitResult)var2.val()) : false;
   }

   public AnchorAura() {
      super("AnchorAura");
      this.J = this.toggleHotkey(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable()).build();
      this.mode = this.builder(this.aD.add("mode"), Configs$LegalInteractMode.class).defaultValue(Configs$LegalInteractMode.NONE).build();
      this.airPlace = this.builder(this.aD.add("air-place"), Boolean.class).defaultValue(false).build();
      this.range = this.intBuilder(this.aD.add("range")).defaultValue(10).build();
      this.zA = new ArrayList<>();
      this.interactRange = this.doubleBuilder(this.aD.add("interact-range")).defaultValue(4.5).updateListener(s -> this.zA = MathUtils.H(s)).build();
      this.delay = this.intBuilder(this.aD.add("delay")).defaultValue(3).validator(Configs.e).build();
      this.multiply = this.intBuilder(this.aD.add("multiply")).defaultValue(1).validator(Configs.e).build();
      this.selfDamageThreshold = this.doubleBuilder(this.aD.add("self-damage-threshold")).defaultValue(8.0).validator(Configs.doubleRange(0.0, 1000.0)).build();
      this.targetDamageThreshold = this.doubleBuilder(this.aD.add("target-damage-threshold"))
         .defaultValue(16.0)
         .validator(Configs.doubleRange(0.0, 1000.0))
         .build();
      this.packetMineBridge = this.flagBuilder(this.aD.add("packet-mine-bridge")).build();
      this.packetMineBridgeHotkey = this.moduleEntry(this.aD.add("packet-mine-bridge-hotkey"), new MultiKeyBind(), this.aD.add("packet-mine-bridge")).build();
      this.zeroTickPlaceSupply = this.flagBuilder(this.aD.add("zero-tick-place-supply")).build();
      this.zeroTickUse = this.flagBuilder(this.aD.add("zero-tick-use")).build();
      this.swingHand = this.builder(this.aD.add("swing-hand"), Boolean.class).defaultValue(true).build();
      this.Bn = new TimerExecutor();
      this.Bo = new LinkedHashMap<>();
      this.Bp = List.of();
      this.lh = 0;
      this.bindFlag(this.ae);
   }

   public boolean Ps(BlockPos pos) {
      BlockState var2 = mc.world.getBlockState(pos);
      return (var2.isAir() || var2.isLiquid() || var2.isReplaceable()) && InteractUtils.canBlockPlace(mc.player, pos, Blocks.RESPAWN_ANCHOR.getDefaultState());
   }

   public boolean Pz(BlockPos pos, BlockHitResult hitResult) {
      KalamaHelperHelperK var3 = this.NS(Items.RESPAWN_ANCHOR);
      if (var3 == null) {
         return false;
      } else {
         Runnable var4 = InvExtra.INSTANCE.swapInventoryIndexToHand(var3.index());
         if (var4 == null) {
            return false;
         } else {
            mc.world.setBlockState(pos, Blocks.AIR.getDefaultState());
            InteractionTasks.g(this.mode.get(), hitResult, Hand.MAIN_HAND, this.swingHand.get());
            var4.run();
            return true;
         }
      }
   }

   private Map<PlayerEntity, Double> Po(BlockPos pos, Map<PlayerEntity, Box> predictedBoxes, boolean usePredict) {
      LinkedHashMap var4 = new LinkedHashMap();
      if (mc.world != null && mc.player != null) {
         Vec3d var5 = pos.toCenterPos();
         Map var6 = Map.of(pos, Blocks.AIR.getDefaultState());
         KalamaHelperHelperHX var7 = ExplosionUtils.q(mc.world, var6);
         var4.put(mc.player, (double)ExplosionUtils.calculateExplosionRawDamage(5.0F, var5, mc.player.getBoundingBox(), var7, ExplosionUtils.h));

         for (PlayerEntity var9 : this.Bp) {
            if (EntityUtils.isEntityValid(var9) && var9 != mc.player) {
               Box var10 = this.PC(var9, predictedBoxes, usePredict);
               var4.put(var9, (double)ExplosionUtils.calculateExplosionRawDamage(5.0F, var5, var10, var7, ExplosionUtils.h));
            }
         }

         return var4;
      } else {
         return var4;
      }
   }

   public void Px(Event<ResetButtonElement> eventPostMine) {
      if (this.ae.get() && this.packetMineBridge.get() && !this.Bp.isEmpty()) {
         float var2 = eventPostMine.<Float>getArgs(1);
         if (var2 > 0.7F) {
            BlockPos var3 = eventPostMine.getArgs(0);
            mc.world.setBlockState(var3, Blocks.AIR.getDefaultState());
            Map var4 = this.Po(var3, null, false);
            if (this.Pr(var3, var4) && this.Py(var3)) {
               this.PA(var3, var4);
            }
         }
      }
   }

   public boolean Pu() {
      ArrayList var1 = new ArrayList<>(this.Bo.entrySet());
      var1.sort(Entry.comparingByValue(this::PF));
      int var2 = this.multiply.get();
      int var3 = 0;

      for (int var4 = 0; var4 < var1.size() && var3 < var2; var4++) {
         BlockPos var5 = (BlockPos)((Entry)var1.get(var4)).getKey();
         var3 += this.Pw(var5, (CombatSubHelperLX)((Entry)var1.get(var4)).getValue());
      }

      return var3 >= var2;
   }

   public void Pm(Event<CombatSubHelperMX> event) {
      if (this.ae.get()) {
         ((CombatSubHelperMX)event.b).g(true);
      }
   }

   private Box PC(PlayerEntity target, Map<PlayerEntity, Box> predictedBoxes, boolean usePredict) {
      return usePredict && predictedBoxes != null ? predictedBoxes.computeIfAbsent(target, this::PD) : target.getBoundingBox();
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(CombatManager.getRequestEnableEvent(), this::Pm);
      this.registerListener(Listener.N(), this::bk);
      this.registerListener(Listener.bd(), this::Pt);
      this.registerListener(PacketMine.aX(), this::Px);
      this.registerListener(PacketMine.aW(), this::gu);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::Oe);
   }

   public void dd() {
      this.Bp = TargetSelector.INSTANCE
         .akC(this.range.get())
         .stream()
         .filter(PlayerEntity.class::isInstance)
         .map(PlayerEntity.class::cast)
         .filter(EntityUtils::isEntityValid)
         .filter(player -> player != mc.player)
         .toList();
   }

   public Map<PlayerEntity, Double> Pn(BlockPos pos) {
      return this.Po(pos, null, true);
   }

   private int PF(CombatSubHelperLX first, CombatSubHelperLX second) {
      double var3 = this.NJ(first.damageCache);
      double var5 = this.NJ(second.damageCache);
      int var7 = Double.compare(var5, var3);
      if (var7 != 0) {
         return var7;
      } else {
         double var8 = first.damageCache.getOrDefault(mc.player, 0.0);
         double var10 = second.damageCache.getOrDefault(mc.player, 0.0);
         int var12 = Double.compare(var8, var10);
         return var12 != 0 ? var12 : Integer.compare(first.powerLevel, second.powerLevel);
      }
   }

   private boolean Pq(BlockPos pos, Map<PlayerEntity, Double> damageMap) {
      if (damageMap != null && !damageMap.isEmpty()) {
         double var3 = damageMap.getOrDefault(mc.player, Double.POSITIVE_INFINITY);
         return var3 > this.selfDamageThreshold.get() ? false : this.NJ(damageMap) >= this.targetDamageThreshold.get();
      } else {
         return false;
      }
   }
}
