package me.matl114.hacks.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.inv.InvExtra;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.tasks.TimerExecutor;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.collections.FlagEntry;
import me.matl114.utils.collections.KalamaHelperHelperK;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class AutoWeb extends BaseModule {
   public final KeyBindRef J;
   public final FlagRef selfWebOnlySlow;
   public final FlagRef airPlace;
   public final FlagRef swingHand;
   public final FlagRef ceiling;
   public final FlagRef ae;
   public final FlagRef otherWeb;
   private static final BlockState Qg = Blocks.COBWEB.getDefaultState();
   public final FlagRef selfWeb;
   public final FlagRef notifySupply;
   private final TimerExecutor Ql;
   public final DoubleRef interactRange;
   public final EnumRef<Configs$LegalInteractMode> mode;
   public final ModulePath aD = makePath(Configs.k, "combat-utils.auto-web");

   private boolean NK(BlockPos pos) {
      return this.ahd(pos) <= MathUtils.a(this.interactRange.get());
   }

   private boolean ahb(PlayerEntity target, BlockPos pos) {
      if (!this.NK(pos)) {
         return false;
      } else {
         BlockState var3 = mc.world.getBlockState(pos);
         if (var3.isOf(Blocks.COBWEB)) {
            return false;
         } else {
            return !var3.isAir() && !var3.isLiquid() && !var3.isReplaceable() ? false : Qg.canPlaceAt(mc.world, pos);
         }
      }
   }

   private List<PlayerEntity> agY() {
      return TargetSelector.INSTANCE == null
         ? List.of()
         : TargetSelector.INSTANCE
            .akC(this.interactRange.get())
            .stream()
            .filter(PlayerEntity.class::isInstance)
            .map(PlayerEntity.class::cast)
            .filter(EntityUtils::isEntityValid)
            .filter(player -> player != mc.player)
            .sorted(Comparator.comparingDouble(mc.player::squaredDistanceTo))
            .toList();
   }

   private double ahd(BlockPos pos) {
      return new Box(pos).squaredMagnitude(mc.player.getEyePos());
   }

   public AutoWeb() {
      super("AutoWeb");
      this.ae = this.flagBuilder(this.aD.addEnable()).build();
      this.J = this.toggleHotkey(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable()).build();
      this.interactRange = this.doubleBuilder(this.aD.add("interact-range")).defaultValue(4.5).build();
      this.mode = this.builder(this.aD.add("mode"), Configs$LegalInteractMode.class).defaultValue(Configs$LegalInteractMode.NONE).build();
      this.airPlace = this.flagBuilder(this.aD.add("air-place")).build();
      this.selfWeb = this.flagBuilder(this.aD.add("self-web")).build();
      this.selfWebOnlySlow = this.flagBuilder(this.aD.add("self-web-only-slow")).build();
      this.otherWeb = this.flagBuilder(this.aD.add("other-web")).build();
      this.ceiling = this.flagBuilder(this.aD.add("ceiling")).build();
      this.notifySupply = this.builder(this.aD.add("notify-supply"), Boolean.class).defaultValue(true).build();
      this.swingHand = this.builder(this.aD.add("swing-hand"), Boolean.class).defaultValue(true).build();
      this.Ql = new TimerExecutor();
      this.bindFlag(this.ae);
   }

   private BlockHitResult agX() {
      if (this.selfWeb.get() && (!this.selfWebOnlySlow.get() || mc.player.hasStatusEffect(StatusEffects.SLOWNESS))) {
         BlockPos var1 = mc.player.getBlockPos();
         if (mc.world.getBlockState(var1) != Qg && !PlayerStateManager.INSTANCE.jE) {
            FlagEntry var2 = this.aha(mc.player, var1);
            if (var2 != null) {
               return (BlockHitResult)var2.val();
            }
         }
      }

      if (this.otherWeb.get()) {
         List<PlayerEntity> var7 = this.agY();
         if (var7.isEmpty()) {
            return null;
         }

         for (PlayerEntity var3 : var7) {
            for (BlockPos var5 : this.agZ(var3)) {
               FlagEntry var6 = this.aha(var3, var5);
               if (var6 != null) {
                  return (BlockHitResult)var6.val();
               }
            }
         }
      }

      return null;
   }

   private KalamaHelperHelperK<ItemStack> agV() {
      return InventoryUtils.p(stack -> stack.isOf(Items.COBWEB), true, false);
   }

   private List<BlockPos> agZ(PlayerEntity target) {
      LinkedHashSet var2 = new LinkedHashSet();
      Box var3 = target.getBoundingBox();
      var2.addAll(MathUtils.getOccupiedBlockPositions(var3.withMaxY(var3.minY + 0.5)));
      if (this.ceiling.get() && !target.isOnGround()) {
         int var4 = target.getBlockY();
         MathUtils.getOccupiedBlockPositions(var3.stretch(0.0, 0.75, 0.0)).stream().filter(pos -> pos.getY() > var4).forEach(var2::add);
      }

      ArrayList var5 = new ArrayList(var2.size());
      var2.stream().<BlockPos>map(BlockPos::toImmutable).sorted(Comparator.comparingDouble(this::ahd)).forEach(var5::add);
      return var5;
   }

   private boolean agW(KalamaHelperHelperK<ItemStack> web, BlockHitResult hitResult) {
      Runnable var3 = InvExtra.INSTANCE.swapInventoryIndexToHand(web.index());
      if (var3 == null) {
         return false;
      } else {
         InteractionTasks.g(this.mode.get(), hitResult, Hand.MAIN_HAND, this.swingHand.get());
         var3.run();
         return true;
      }
   }

   private FlagEntry<BlockHitResult> aha(PlayerEntity target, BlockPos pos) {
      if (!this.ahb(target, pos)) {
         return null;
      } else {
         FlagEntry var3 = InteractionTasks.q(pos, Qg, this.airPlace.get(), !this.mode.get().isLegal());
         if (!InteractUtils.C(mc.player, var3)) {
            return null;
         } else {
            return !this.NK(((BlockHitResult)var3.val()).getBlockPos()) ? null : var3;
         }
      }
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
   }

   private boolean ahc(Entity entity, PlayerEntity target) {
      return entity == target || this.selfWeb.get() && entity == mc.player;
   }

   private boolean ahe() {
      return this.agZ(mc.player).stream().anyMatch(pos -> mc.world.getBlockState(pos).isOf(Blocks.COBWEB));
   }

   private void bl(Event<Void> event) {
      if (!checkNull() && this.ae.get()) {
         BlockHitResult var2 = this.agX();
         if (var2 != null) {
            KalamaHelperHelperK var3 = this.agV();
            if (var3 == null) {
               this.Ql.b(100, () -> {
                  if (this.notifySupply.get()) {
                     this.logI18N("message.module.auto-web.no-item", new Object[0]);
                  }
               });
            } else {
               this.agW(var3, var2);
            }
         }
      }
   }

   private void Oe(Event<KalamaHelperHelperI<ModulePreset>> event) {
      this.mode.set(Configs$LegalInteractMode.getFromPreset((ModulePreset)((KalamaHelperHelperI)event.b).b()));
   }

   @Override
   public void onEnableModule() {
      super.onEnableModule();
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bd(), this::bl);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::Oe);
   }
}
