package me.matl114.hacks.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.gui.elements.ResetButtonElement;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.ac.Disabler;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.mine.MineSubHelperD;
import me.matl114.hacks.modules.mine.PacketMine;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.hacks.utils.tasks.TimerExecutor;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.ExplosionUtils;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.KalamaHelperHelperHX;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.collections.FlagEntry;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class CrystalAura extends BaseModule {
   public final ModulePath aD = makePath(Configs.k, "combat-utils.crystal-aura");
   Map<EndCrystalEntity, Integer> zU;
   List<PlayerEntity> zS;
   RenderCollector<Box> zW;
   public final DoubleRef selfDamageThreshold;
   private static final int zy = 5;
   public final EnumRef<Configs$LegalInteractMode> mode;
   Map<BlockPos, Integer> zV;
   public final FlagRef autoFill;
   public final FlagRef damageUsePredictor;
   public final KeyBindRef autoFillHotkey;
   public final KeyBindRef J;
   public final IntRef range;
   public final FlagRef swingHand;
   List<BlockPos> zT;
   public final DoubleRef baseValueReduce;
   public final FlagRef zeroTickBase;
   int lh;
   private List<Vec3i> zA;
   public final FlagRef offhand;
   public final FlagRef placeAfterAttack;
   TimerExecutor zP;
   public final IntRef delay;
   public final FlagRef notifySupply;
   public final NBTRef<EntrySet<Item>> fillWhiteList;
   public final DoubleRef interactRange;
   TimerExecutor zR;
   public final FlagRef fillIgnoreDamage;
   public final FlagRef airPlace;
   TimerExecutor zQ;
   public final DoubleRef targetDamageThreshold;
   public final FlagRef autoBase;
   public final FlagRef ae = this.flagBuilder(this.aD.addEnable()).build();

   private boolean NY(BlockPos basePos) {
      if (!this.NQ(basePos)) {
         return false;
      } else {
         KalamaHelperHelperK var2 = this.NU();
         if (var2 == null) {
            return false;
         } else {
            Runnable var3 = this.offhand.get() ? InvExtra.INSTANCE.uh(var2.index()) : InvExtra.INSTANCE.swapInventoryIndexToHand(var2.index());
            if (var3 == null) {
               return false;
            } else {
               InteractionTasks.g(
                  this.mode.get(), RaycastUtils.g(basePos, mc.player.getEyePos()), this.offhand.get() ? Hand.OFF_HAND : Hand.MAIN_HAND, this.swingHand.get()
               );
               var3.run();
               return true;
            }
         }
      }
   }

   private boolean Oa(BlockPos pos, BlockHitResult hitResult) {
      KalamaHelperHelperK var3 = this.NT();
      if (var3 == null) {
         return false;
      } else {
         Runnable var4 = this.offhand.get() ? InvExtra.INSTANCE.uh(var3.index()) : InvExtra.INSTANCE.swapInventoryIndexToHand(var3.index());
         if (var4 == null) {
            return false;
         } else {
            mc.world.setBlockState(pos, Blocks.AIR.getDefaultState());
            InteractionTasks.g(this.mode.get(), hitResult, this.offhand.get() ? Hand.OFF_HAND : Hand.MAIN_HAND, this.swingHand.get());
            var4.run();
            this.zV.put(pos, Tasks.b());
            return true;
         }
      }
   }

   private boolean NN(Entity entity) {
      return EntityUtils.isEntityValid(entity) && !(entity instanceof EndCrystalEntity var2 && this.zU.containsKey(var2));
   }

   private KalamaHelperHelperK<ItemStack> NT() {
      return InventoryUtils.p(stack -> stack.getItem() instanceof BlockItem var3 && this.fillWhiteList.get().test(stack.getItem()), true, false);
   }

   private boolean NH(Map<PlayerEntity, Double> damageMap) {
      return damageMap != null && !damageMap.isEmpty() ? this.NJ(damageMap) >= this.targetDamageThreshold.get() : false;
   }

   private boolean NQ(BlockPos basePos) {
      return this.NP(basePos.up(), Map.of());
   }

   private List<EndCrystalEntity> Nz() {
      double var1 = CombatExtra.INSTANCE.getAttackRange();
      return CombatManager.INSTANCE
         .Kx
         .stream()
         .filter(EntityUtils::isEntityValid)
         .filter(crystal -> !this.zU.containsKey(crystal))
         .filter(crystal -> TargetSelector.INSTANCE.isTargetInRange(crystal, var1, 0))
         .toList();
   }

   private boolean NV(BlockPos glassPos) {
      if (this.fillIgnoreDamage.get()) {
         return true;
      } else {
         Map var2 = Map.of(glassPos, Blocks.AIR.getDefaultState(), glassPos.down(), Blocks.OBSIDIAN.getDefaultState());
         Map var3 = this.NF(glassPos.toBottomCenterPos(), var2);
         return this.NH(var3);
      }
   }

   private Map<PlayerEntity, Double> NE(Vec3d explosionPos) {
      return this.NF(explosionPos, Map.of());
   }

   private KalamaHelperHelperK<ItemStack> NS(Item item) {
      return InventoryUtils.p(stack -> stack.isOf(item), true, false);
   }

   public void gu(Event<MineSubHelperD> eventPreMine) {
      if (this.ae.get() && !eventPreMine.d()) {
         BlockPos var2 = eventPreMine.getArgs(0);
         Integer var3 = this.zV.get(var2);
         if (var3 != null) {
            BlockPos var4 = var2.down();
            boolean var5 = Tasks.b() - var3 > 2;
            Map var6 = Map.of(var2, Blocks.AIR.getDefaultState());
            if (!var5 || !this.NK(var4) || !this.NL(var4) || !this.NP(var2, var6) || !this.NI(this.NF(var2.toBottomCenterPos(), var6))) {
               eventPreMine.cancel();
            }
         }
      }
   }

   public void NC() {
      for (BlockPos var2 : this.zT) {
         if (this.NK(var2) && this.NL(var2) && this.NQ(var2)) {
            this.NY(var2);
         }
      }

      this.zT.clear();
   }

   public void Gn(Event<MatrixStack> event) {
      if (this.ae.get()) {
         RenderUtils.startDrawVirtual((MatrixStack)event.b);

         try {
            this.zW.a((MatrixStack)event.b);
         } finally {
            RenderUtils.stopDrawVirtual((MatrixStack)event.b);
         }
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.N(), this::y);
      this.registerListener(Listener.bd(), this::bl);
      this.registerListener(PacketMine.aW(), this::gu);
      this.registerListener(PacketMine.aX(), this::Oc);
      this.registerListener(CombatManager.getRequestEnableEvent(), this::ll);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::Oe);
      this.registerListener(RenderListener.q(), this::Gn);
   }

   private KalamaHelperHelperK<ItemStack> NU() {
      return InventoryUtils.p(stack -> stack.isOf(Items.END_CRYSTAL), true, false);
   }

   private boolean NK(BlockPos pos) {
      return new Box(pos).squaredMagnitude(mc.player.getEyePos()) <= MathUtils.a(this.interactRange.get());
   }

   private boolean NM(BlockState state) {
      return state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.BEDROCK);
   }

   private boolean Ny() {
      boolean var1 = this.NU() != null;
      if (!var1) {
         this.zP.b(100, () -> {
            if (this.notifySupply.get()) {
               this.logI18N("message.module.crystal-aura.no-item.crystal", new Object[0]);
            }
         });
         return false;
      } else {
         return true;
      }
   }

   private boolean Ob(BlockPos crystalPos) {
      BlockState var2 = mc.world.getBlockState(crystalPos.down());
      boolean var3 = var2.isOf(Blocks.OBSIDIAN) || var2.isOf(Blocks.BEDROCK);
      if (!var3 && this.autoBase.get() && var2.isAir()) {
         FlagEntry var4 = InteractionTasks.l(crystalPos.down(), this.airPlace.get(), !this.mode.get().isLegal());
         if (InteractUtils.C(mc.player, var4) && InteractUtils.canBlockPlace(mc.player, crystalPos.down(), Blocks.OBSIDIAN.getDefaultState())) {
            var3 = true;
         }
      }

      return var3 && !mc.world.getBlockState(crystalPos).isLiquid();
   }

   public void Oc(Event<ResetButtonElement> eventPostMine) {
      if (this.ae.get()) {
         BlockPos var2 = eventPostMine.getArgs(0);
         float var3 = eventPostMine.<Float>getArgs(1);
         if (!(var3 <= 0.7F)) {
            if (!this.zS.isEmpty()) {
               if (this.autoFill.get()) {
                  if (this.Ob(var2)) {
                     if (this.NK(var2.down())) {
                        var2 = var2.toImmutable();
                        HashMap var4 = new HashMap();
                        var4.put(var2, Blocks.AIR.getDefaultState());
                        mc.world.setBlockState(var2, Blocks.AIR.getDefaultState());
                        if (this.zV.containsKey(var2) && this.NP(var2, var4)) {
                           this.zV.remove(var2);
                           BlockPos var8 = var2.down();
                           this.NY(var8);
                        } else if (this.NT() == null) {
                           this.zR.b(100, () -> {
                              if (this.notifySupply.get()) {
                                 this.logI18N("message.module.crystal-aura.no-item.fill-block", new Object[0]);
                              }
                           });
                        } else {
                           if (!this.fillIgnoreDamage.get()) {
                              Map var5 = this.NF(var2.toBottomCenterPos(), var4);
                              if (!this.NH(var5)) {
                                 return;
                              }
                           }

                           FlagEntry var7 = InteractionTasks.l(var2, this.airPlace.get(), !this.mode.get().isLegal());
                           if (InteractUtils.C(mc.player, var7)) {
                              this.Oa(var2, (BlockHitResult)var7.val());
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void Oe(Event<KalamaHelperHelperI<ModulePreset>> event) {
      this.mode.set(Configs$LegalInteractMode.getFromPreset((ModulePreset)((KalamaHelperHelperI)event.b).b()));
      this.airPlace.set(!((ModulePreset)((KalamaHelperHelperI)event.b).b()).hasAC());
   }

   public void NB() {
      if (TargetSelector.INSTANCE == null) {
         this.zS = List.of();
      } else {
         this.zS = TargetSelector.INSTANCE
            .akC(this.range.get())
            .stream()
            .filter(PlayerEntity.class::isInstance)
            .map(PlayerEntity.class::cast)
            .filter(EntityUtils::isEntityValid)
            .filter(player -> player != mc.player)
            .toList();
      }
   }

   private int Od(Map<PlayerEntity, Double> first, Map<PlayerEntity, Double> second) {
      double var3 = this.NJ(first);
      double var5 = this.NJ(second);
      int var7 = Double.compare(var5, var3);
      if (var7 != 0) {
         return var7;
      } else {
         double var8 = first.getOrDefault(mc.player, Double.POSITIVE_INFINITY);
         double var10 = second.getOrDefault(mc.player, Double.POSITIVE_INFINITY);
         return Double.compare(var8, var10);
      }
   }

   private boolean NL(BlockPos basePos) {
      Vec3d var2 = basePos.up().toBottomCenterPos();
      Box var3 = new Box(var2.x - 1.0, var2.y, var2.z - 1.0, var2.x + 1.0, var2.y + 2.0, var2.z + 1.0);
      return var3.squaredMagnitude(mc.player.getEyePos()) <= MathUtils.a(CombatExtra.INSTANCE.getAttackRange());
   }

   public CrystalAura() {
      super("CrystalAura");
      this.J = this.toggleHotkey(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable()).build();
      this.mode = this.builder(this.aD.add("mode"), Configs$LegalInteractMode.class).defaultValue(Configs$LegalInteractMode.NONE).build();
      this.range = this.intBuilder(this.aD.add("range")).defaultValue(10).build();
      this.zA = new ArrayList<>();
      this.interactRange = this.doubleBuilder(this.aD.add("interact-range")).defaultValue(4.5).updateListener(s -> this.zA = MathUtils.H(s)).build();
      this.delay = this.intBuilder(this.aD.add("delay")).defaultValue(3).validator(Configs.e).build();
      this.placeAfterAttack = this.builder(this.aD.add("place-after-attack"), Boolean.class).defaultValue(true).build();
      this.autoBase = this.builder(this.aD.add("auto-base"), Boolean.class).defaultValue(true).build();
      this.offhand = this.builder(this.aD.add("offhand"), Boolean.class).defaultValue(false).build();
      this.airPlace = this.builder(this.aD.add("air-place"), Boolean.class).defaultValue(false).build();
      this.zeroTickBase = this.builder(this.aD.add("zero-tick-base"), Boolean.class).defaultValue(false).build();
      this.baseValueReduce = this.builder(this.aD.add("base-value-reduce"), DoubleRef.TYPE).defaultValue(0.9).build();
      this.autoFill = this.builder(this.aD.add("auto-fill"), Boolean.class).defaultValue(false).build();
      this.autoFillHotkey = this.moduleEntry(this.aD.add("auto-fill-hotkey"), new MultiKeyBind(), this.aD.add("auto-fill")).build();
      this.fillWhiteList = this.builder(this.aD.add("fill-white-list"), EntrySet.<Item>parameter())
         .defaultValue(new EntrySet<Item>(Registries.ITEM, List.of(Items.GLASS, Items.OAK_LEAVES)))
         .build();
      this.fillIgnoreDamage = this.builder(this.aD.add("fill-ignore-damage"), Boolean.class).defaultValue(false).build();
      this.selfDamageThreshold = this.doubleBuilder(this.aD.add("self-damage-threshold")).defaultValue(8.0).validator(Configs.doubleRange(0.0, 1000.0)).build();
      this.targetDamageThreshold = this.doubleBuilder(this.aD.add("target-damage-threshold"))
         .defaultValue(16.0)
         .validator(Configs.doubleRange(0.0, 1000.0))
         .build();
      this.damageUsePredictor = this.builder(this.aD.add("damage-use-predictor"), Boolean.class).defaultValue(true).build();
      this.notifySupply = this.builder(this.aD.add("notify-supply"), Boolean.class).defaultValue(true).build();
      this.swingHand = this.builder(this.aD.add("swing-hand"), Boolean.class).defaultValue(true).build();
      this.zP = new TimerExecutor();
      this.zQ = new TimerExecutor();
      this.zR = new TimerExecutor();
      this.zS = List.of();
      this.zT = new ArrayList<>();
      this.zU = new HashMap<>();
      this.zV = new ConcurrentHashMap<>();
      this.lh = 0;
      this.zW = RenderCollectors.createBoxCollector(true, false, false);
      this.bindFlag(this.ae);
   }

   private boolean NW(BlockState state) {
      if (state != null && !state.isAir() && !state.isLiquid()) {
         Item var2 = state.getBlock().asItem();
         return var2 != Items.AIR && this.fillWhiteList.get().test(var2);
      } else {
         return false;
      }
   }

   private double NJ(Map<PlayerEntity, Double> damageMap) {
      double var2 = Double.NEGATIVE_INFINITY;

      for (PlayerEntity var5 : this.zS) {
         if (EntityUtils.isEntityValid(var5) && var5 != mc.player) {
            Double var6 = (Double)damageMap.get(var5);
            if (var6 != null && var6 > var2) {
               var2 = var6;
            }
         }
      }

      return var2;
   }

   public void bl(Event<Void> event) {
      if (!checkNull()) {
         this.zW.clear();
         this.zU.entrySet().removeIf(entity -> !EntityUtils.isEntityValid((Entity)entity.getKey()) || Tasks.b() > entity.getValue() + 3);
         if (this.ae.get()) {
            this.NB();
            this.NX();
            boolean var2 = this.NA();
            this.NC();
            if ((++this.lh > this.delay.get() || this.placeAfterAttack.get() && var2) && !this.zS.isEmpty()) {
               this.lh = 0;
               if (this.Ny()) {
                  this.ND();
               }
            }
         } else {
            this.zS = List.of();
            this.zU.clear();
            this.zV.clear();
            this.lh = 0;
         }
      }
   }

   public boolean Nx(EndCrystalEntity entity) {
      if (!Attack.INSTANCE.Yh(entity)) {
         this.zU.put(entity, Tasks.b());
         return true;
      } else {
         return false;
      }
   }

   private void NX() {
      if (!this.zV.isEmpty()) {
         if (this.zS.isEmpty()) {
            this.zV.clear();
         } else {
            this.zV.keySet().removeIf(s -> s.getSquaredDistance(mc.player.getPos()) > MathUtils.b(8));
            this.zV.entrySet().removeIf(entry -> {
               BlockPos var2 = entry.getKey();
               BlockState var3 = mc.world.getBlockState(var2);
               return !this.NW(var3) || !this.NV(var2);
            });
         }
      }
   }

   private boolean NO(BlockPos crystalPos) {
      return !mc.world.getOtherEntities(null, new Box(crystalPos), this::NN).isEmpty();
   }

   public boolean NA() {
      if (this.zS.isEmpty()) {
         return false;
      } else {
         ArrayList<Entry<EndCrystalEntity, Map<PlayerEntity, Double>>> var1 = new ArrayList<>();

         for (EndCrystalEntity var3 : this.Nz()) {
            if (!this.zU.containsKey(var3) && EntityUtils.isEntityValid(var3)) {
               Map<PlayerEntity, Double> var4 = this.NE(var3.getPos());
               if (this.NI(var4)) {
                  var1.add(Map.entry(var3, var4));
               }
            }
         }

         var1.sort(Entry.comparingByValue(this::Od));
         boolean var6 = false;

         for (Entry var8 : var1) {
            EndCrystalEntity var5 = (EndCrystalEntity)var8.getKey();
            this.zW.submit(var5.getBoundingBox(), ColorUtils.i(Color.MAGENTA, 255));
            if (!this.Nx(var5)) {
               break;
            }

            var6 = true;
         }

         return var6;
      }
   }

   public void ND() {
      if (!this.zS.isEmpty()) {
         boolean var1 = this.mode.get().isLegal();
         Object var2 = null;
         double var3 = Double.NEGATIVE_INFINITY;
         double var5 = Double.POSITIVE_INFINITY;
         boolean var7 = this.autoBase.get();
         if (var7 && this.NS(Items.OBSIDIAN) == null) {
            this.zQ.b(100, () -> {
               if (this.notifySupply.get()) {
                  this.logI18N("message.module.crystal-aura.no-item.base", new Object[0]);
               }
            });
            var7 = false;
         }

         BlockPos var8 = mc.player.getBlockPos();

         for (Vec3i var10 : this.zA) {
            BlockPos var11 = var8.add(var10);
            if (this.NK(var11) && this.NL(var11) && !this.NO(var11)) {
               boolean var12 = this.NQ(var11);
               Object var13;
               if (var12) {
                  var13 = new CombatSubHelperL(var11);
               } else {
                  if (!var7 || !this.NR(var11)) {
                     continue;
                  }

                  FlagEntry var14 = InteractionTasks.l(var11, this.airPlace.get(), !var1);
                  if (!InteractUtils.C(mc.player, var14) || !this.NK(((BlockHitResult)var14.val()).getBlockPos())) {
                     continue;
                  }

                  var13 = new CombatSubHelperZ(var11, (BlockHitResult)var14.val());
               }

               Map<PlayerEntity, Double> var21 = var12 ? this.NE(this.NG(var11)) : this.NF(this.NG(var11), Map.of(var11, Blocks.OBSIDIAN.getDefaultState()));
               if (this.NI(var21)) {
                  double var15 = this.NJ(var21);
                  double var17 = var21.getOrDefault(mc.player, Double.POSITIVE_INFINITY);
                  double var19 = var15 * (((CombatSubHelperR)var13).isMetaEmpty() ? this.baseValueReduce.get() : 1.0);
                  if (var19 > var3 || var19 == var3 && var17 < var5) {
                     var2 = var13;
                     var3 = var19;
                     var5 = var17;
                  }
               }
            }
         }

         if (var2 != null) {
            this.zW.submit(new Box(((CombatSubHelperR)var2).getMetadata()), ColorUtils.i(Color.MAGENTA, 255));
            if (((CombatSubHelperR)var2).isMetaEmpty()) {
               this.NZ(((CombatSubHelperR)var2).getMetadata(), ((CombatSubHelperZ)var2).basePlaceResult());
            } else {
               this.NY(((CombatSubHelperR)var2).getMetadata());
            }
         }
      }
   }

   private void ll(Event<CombatSubHelperMX> event) {
      if (this.ae.get()) {
         ((CombatSubHelperMX)event.e()).g(true);
      }
   }

   private boolean NI(Map<PlayerEntity, Double> damageMap) {
      if (damageMap != null && !damageMap.isEmpty()) {
         double var2 = damageMap.getOrDefault(mc.player, Double.POSITIVE_INFINITY);
         return var2 > this.selfDamageThreshold.get() ? false : this.NJ(damageMap) >= this.targetDamageThreshold.get();
      } else {
         return false;
      }
   }

   private boolean NZ(BlockPos pos, BlockHitResult hitResult) {
      KalamaHelperHelperK var3 = this.NS(Items.OBSIDIAN);
      if (var3 == null) {
         return false;
      } else {
         Runnable var4 = InvExtra.INSTANCE.swapInventoryIndexToHand(var3.index());
         if (var4 == null) {
            return false;
         } else {
            InteractionTasks.g(this.mode.get(), hitResult, Hand.MAIN_HAND, this.swingHand.get());
            var4.run();
            if (this.zeroTickBase.get() && Disabler.INSTANCE.isMultiRotPlaceCheckDisabled(this.mode.get().canMultiRotPlace())) {
               this.NY(pos);
            } else {
               this.zT.add(pos);
            }

            return true;
         }
      }
   }

   private Map<PlayerEntity, Double> NF(Vec3d explosionPos, Map<BlockPos, BlockState> overrides) {
      LinkedHashMap var3 = new LinkedHashMap();
      if (mc.world != null && mc.player != null) {
         KalamaHelperHelperHX var4 = overrides.isEmpty() ? ExplosionUtils.p(mc.world) : ExplosionUtils.q(mc.world, overrides);
         var3.put(mc.player, (double)ExplosionUtils.calculateExplosionRawDamage(6.0F, explosionPos, mc.player.getBoundingBox(), var4, ExplosionUtils.h));

         for (PlayerEntity var6 : this.zS) {
            if (EntityUtils.isEntityValid(var6) && var6 != mc.player) {
               Vec3d var7 = var6.getPos();
               Vec3d var10;
               if (this.damageUsePredictor.get()) {
                  Vec3d var8 = PositionPredict.INSTANCE.attackPredictArgument.get().predict(var6);
                  if (var8.squaredDistanceTo(var7) > MathUtils.a(0.5)) {
                     Vec3d var9 = var8.subtract(var7);
                     var10 = MovTasks.simulateMovement(var6, var7, var9, false);
                  } else {
                     var10 = Vec3d.ZERO;
                  }
               } else {
                  var10 = Vec3d.ZERO;
               }

               var3.put(
                  var6, (double)ExplosionUtils.calculateExplosionRawDamage(6.0F, explosionPos, var6.getBoundingBox().offset(var10), var4, ExplosionUtils.h)
               );
            }
         }

         return var3;
      } else {
         return var3;
      }
   }

   private Vec3d NG(BlockPos basePos) {
      return basePos.up().toBottomCenterPos();
   }

   private boolean NP(BlockPos crystalPos, Map<BlockPos, BlockState> overrides) {
      KalamaHelperHelperHX var3 = ExplosionUtils.q(mc.world, overrides);
      BlockState var4 = var3.a(crystalPos);
      if (var4 != null && var4.isAir()) {
         BlockState var5 = var3.a(crystalPos.down());
         return var5 != null && this.NM(var5) ? !this.NO(crystalPos) : false;
      } else {
         return false;
      }
   }

   private boolean NR(BlockPos pos) {
      BlockState var2 = mc.world.getBlockState(pos);
      return (var2.isAir() || var2.isLiquid() || var2.isReplaceable())
         && InteractUtils.canBlockPlace(mc.player, pos, Blocks.OBSIDIAN.getDefaultState())
         && this.NP(pos.up(), Map.of(pos, Blocks.OBSIDIAN.getDefaultState()));
   }

   public void y(Event<World> event) {
      this.zS = List.of();
      this.zU.clear();
      this.zV.clear();
      this.lh = 0;
   }
}
