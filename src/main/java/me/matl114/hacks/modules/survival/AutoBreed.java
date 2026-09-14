package me.matl114.hacks.modules.survival;

import net.minecraft.item.Item;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import me.matl114.accessors.interfaces.MetadataHolder;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.combat.TargetSelector;
import me.matl114.hacks.modules.interact.Interact;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.utils.config.EntityTypeRegex;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.move.PathingSchedular;
import me.matl114.hacks.utils.move.goal.IPathGoal;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.FarmingUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.j.KalamaHelperHelperA;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

@KalamaHelperHelperA
public class AutoBreed extends BaseModule {
   public final NBTRef<EntityTypeRegex> entityWhitelist;
   private final PathingSchedular aL;
   public final FlagRef enableBreed;
   public final NBTRef<WrapColor> renderOtherAnimalColor;
   private int lastInteractTick;
   private static final String BABY_BREED_COUNT_KEY = "kalama:auto_breed/baby_breed_count";
   private SurvivalSubHelperF Re;
   private static final int QT = 5;
   private static final int QS = 32;
   public final FlagRef render;
   private static final int QU = 256;
   public final FlagRef enableFeedBaby;
   public final NBTRef<WrapColor> renderPenColor;
   public final IntRef babyFeedLimit;
   private AnimalEntity Rd;
   private static final int QV = 16;
   public final FlagRef ae;
   public final NBTRef<WrapColor> renderColor;
   public final ModulePath aD = makePath(Configs.o, "survival-interact-utils.auto-breed");
   public final KeyBindRef J;
   public final FlagRef enableBaritone;

   public boolean ahW() {
      return this.Rd != null && this.ahV(this.Rd);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bd(), this::onPreInputEvent);
      this.registerListener(RenderListener.q(), this::onRender);
   }

   private AnimalEntity getBreedTargetsInPen(SurvivalSubHelperF pen) {
      if (pen == null) {
         return null;
      } else {
         List<AnimalEntity> var2 = mc.world.getEntitiesByClass(AnimalEntity.class, pen.sP(), this::isBreedTarget);
         return var2.stream().min(Comparator.comparingDouble(animal -> animal.squaredDistanceTo(mc.player))).orElse(null);
      }
   }

   public void tickBreed() {
      if (this.Rd != null && !this.aL.y() && TargetSelector.INSTANCE.isWithinAttackRange(mc.player.getPos(), this.Rd)) {
         KalamaHelperHelperK var1 = this.ahU(this.Rd);
         if (var1 != null && this.lastInteractTick + 5 < Tasks.b() && Interact.INSTANCE != null) {
            Runnable var2 = InvExtra.INSTANCE.swapInventoryIndexToHand(var1.index());
            if (var2 != null) {
               Interact.INSTANCE.Si(this.Rd);
               var2.run();
               if (this.Rd.isBaby()) {
                  this.ahR(this.Rd);
               }

               this.lastInteractTick = Tasks.b();
            }
         }
      }
   }

   private void refreshTarget() {
      if (this.Rd != null && this.isBreedTarget(this.Rd)) {
         this.Re = this.locateAnimalPen(this.Rd);
         if (this.Re != null) {
            AnimalEntity var4 = this.getBreedTargetsInPen(this.Re);
            if (var4 != null) {
               this.Rd = var4;
            }
         }
      } else {
         this.Rd = null;
         this.Re = null;
         List<AnimalEntity> var1 = mc.world.getEntitiesByClass(AnimalEntity.class, mc.player.getBoundingBox().expand(32.0, 16.0, 32.0), this::isBreedTarget);
         AnimalEntity var2 = var1.stream().min(Comparator.comparingDouble(animal -> animal.squaredDistanceTo(mc.player))).orElse(null);
         if (var2 != null) {
            this.Re = this.locateAnimalPen(var2);
            if (this.Re != null) {
               AnimalEntity var3 = this.getBreedTargetsInPen(this.Re);
               this.Rd = var3 != null ? var3 : var2;
            } else {
               this.Rd = var2;
            }
         }
      }
   }

   private Predicate<ItemStack> ahT(AnimalEntity animal) {
      Set<Item> var2 = FarmingUtils.getBreedItems(animal);
      return stack -> !stack.isEmpty() && var2.stream().anyMatch(item -> stack.isOf(item.getItem()));
   }

   private void cZ() {
      this.Rd = null;
      this.Re = null;
      this.aL.A();
   }

   private BlockPos findWall(BlockPos origin, Direction direction) {
      for (int var3 = 1; var3 <= 5; var3++) {
         BlockPos var4 = origin.offset(direction, var3);
         if (this.isWallBlock(var4)) {
            return var4;
         }
      }

      return null;
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      this.cZ();
   }

   private void ahR(AnimalEntity animal) {
      if (animal instanceof MetadataHolder var2) {
         var2.getMetadata().a(this, "kalama:auto_breed/baby_breed_count", this.getBabyBreedCount(animal) + 1);
      }
   }

   private boolean isWallBlock(BlockPos pos) {
      if (mc.world != null && mc.player != null) {
         BlockState var2 = mc.world.getBlockState(pos);
         if (!var2.isAir() && !var2.isLiquid()) {
            VoxelShape var3 = var2.getCollisionShape(mc.world, pos, ShapeContext.of(mc.player));
            if (var3.isEmpty()) {
               return false;
            } else {
               Box var4 = var3.getBoundingBox();
               double var5 = var4.maxX - var4.minX;
               double var7 = var4.maxY - var4.minY;
               double var9 = var4.maxZ - var4.minZ;
               return Math.max(Math.max(var5, var7), var9) >= 1.0;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private SurvivalSubHelperF locateAnimalPen(AnimalEntity anchor) {
      if (mc.world != null && mc.player != null && anchor != null) {
         BlockPos var2 = anchor.getBlockPos();
         BlockPos var3 = this.findWall(var2, Direction.WEST);
         BlockPos var4 = this.findWall(var2, Direction.EAST);
         BlockPos var5 = this.findWall(var2, Direction.NORTH);
         BlockPos var6 = this.findWall(var2, Direction.SOUTH);
         if (var3 != null && var4 != null && var5 != null && var6 != null) {
            int var7 = var3.getX();
            int var8 = var4.getX();
            int var9 = var5.getZ();
            int var10 = var6.getZ();
            if (var8 > var7 && var10 > var9) {
               int var11 = var8 - var7 + 1;
               int var12 = var10 - var9 + 1;
               if (var11 * var12 <= 256 && (var11 <= 16 || var12 <= 16)) {
                  return !this.isRectangularPen(var7, var8, var9, var10, var2.getY()) ? null : new SurvivalSubHelperF(var7, var8, var9, var10, var2.getY());
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private List<AnimalEntity> ahS(SurvivalSubHelperF pen) {
      return mc.world.getEntitiesByClass(AnimalEntity.class, pen.sP(), this::isBreedTarget);
   }

   private boolean isRectangularPen(int minX, int maxX, int minZ, int maxZ, int y) {
      for (int var6 = minX; var6 <= maxX; var6++) {
         if (!this.isWallBlock(new BlockPos(var6, y, minZ)) || !this.isWallBlock(new BlockPos(var6, y, maxZ))) {
            return false;
         }
      }

      for (int var7 = minZ; var7 <= maxZ; var7++) {
         if (!this.isWallBlock(new BlockPos(minX, y, var7)) || !this.isWallBlock(new BlockPos(maxX, y, var7))) {
            return false;
         }
      }

      return true;
   }

   public AutoBreed() {
      super("AutoBreed");
      this.ae = this.flagBuilder(this.aD.addEnable()).build();
      this.J = this.toggleHotkey(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable()).build();
      this.enableBaritone = this.flagBuilder(this.aD.add("enable-baritone")).build();
      this.enableBreed = this.flagBuilder(this.aD.add("enable-breed")).build();
      this.enableFeedBaby = this.flagBuilder(this.aD.add("enable-feed-baby")).build();
      this.babyFeedLimit = this.builder(this.aD.add("baby-feed-limit"), Integer.class).defaultValue(3).build();
      this.render = this.flagBuilder(this.aD.add("render")).build();
      this.renderColor = this.builder(this.aD.add("render-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.GREEN)).build();
      this.renderPenColor = this.builder(this.aD.add("render-pen-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.YELLOW)).build();
      this.renderOtherAnimalColor = this.builder(this.aD.add("render-other-animal-color"), WrapColor.class)
         .defaultValue(new WrapColor(Formatting.AQUA))
         .build();
      this.entityWhitelist = this.builder(this.aD.add("entity-whitelist"), EntityTypeRegex.class)
         .defaultValue(new EntityTypeRegex(new Regex("^(turtle)$")))
         .build();
      this.aL = new PathingSchedular();
      this.aL
         .D(() -> this.enableBaritone.get() && this.Rd != null)
         .F(this::ahW)
         .G(this::ahX)
         .K((pos, inventory) -> this.Rd != null && InventoryUtils.findItem(inventory, this.ahT(this.Rd), false) != null)
         .E(this::processGoal);
      this.bindFlag(this.ae);
   }

   public void onRender(Event<MatrixStack> event) {
      if (this.ae.get() && this.render.get() && this.Rd != null) {
         float var2 = event.<Float>getArgs(0);
         RenderUtils.startDrawVirtual((MatrixStack)event.b);

         try {
            RenderCollector var3 = RenderCollectors.createBoxCollector(true, false, false);
            var3.submit(RenderUtils.getLerpedBox(this.Rd, var2), this.renderColor.get().withAlpha(255));
            if (this.Re != null && this.Re.sP().contains(this.Rd.getPos())) {
               var3.submit(this.Re.sO(), this.renderPenColor.get().withAlpha(255));

               for (AnimalEntity var5 : this.ahS(this.Re)) {
                  if (var5 != this.Rd) {
                     var3.submit(RenderUtils.getLerpedBox(var5, var2), this.renderOtherAnimalColor.get().withAlpha(255));
                  }
               }
            }

            var3.a((MatrixStack)event.b);
         } finally {
            RenderUtils.stopDrawVirtual((MatrixStack)event.b);
         }
      }
   }

   private KalamaHelperHelperK<ItemStack> ahU(AnimalEntity animal) {
      return InventoryUtils.p(this.ahT(animal), true, false);
   }

   public boolean ahX() {
      return false;
   }

   private boolean isInteractionTarget(AnimalEntity animal) {
      return animal.isBaby()
         ? this.enableFeedBaby.get() && this.getBabyBreedCount(animal) < this.babyFeedLimit.get()
         : this.enableBreed.get() && !animal.isInLove();
   }

   private boolean isBreedTarget(AnimalEntity animal) {
      return EntityUtils.isEntityValid(animal)
         && this.entityWhitelist.get().test(animal.getType())
         && FarmingUtils.e(animal)
         && this.isInteractionTarget(animal);
   }

   private int getBabyBreedCount(AnimalEntity animal) {
      if (animal instanceof MetadataHolder var2) {
         Integer var3 = var2.getMetadata().b(this, "kalama:auto_breed/baby_breed_count");
         return var3 == null ? 0 : var3;
      } else {
         return 0;
      }
   }

   private boolean ahV(AnimalEntity animal) {
      return this.ahU(animal) == null;
   }

   public void onPreInputEvent(Event<Void> event) {
      if (!checkNull() && this.ae.get()) {
         this.refreshTarget();
         this.tickBreed();
         this.aL.tickPathing(mc.player);
      }
   }

   public IPathGoal processGoal() {
      if (this.Rd == null) {
         return null;
      } else {
         if (this.Re != null) {
            BlockPos var1 = this.Re.selectPerimeterStop(mc.player.getPos());
            if (var1 != null) {
               return PathingSchedular.pathToOrNearStopGoal(var1, 1.5);
            }
         }

         return PathingSchedular.pathToOrNearStopGoal(this.Rd.getBlockPos(), 1.5);
      }
   }
}
