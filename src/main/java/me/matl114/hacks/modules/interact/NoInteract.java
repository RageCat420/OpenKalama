package me.matl114.hacks.modules.interact;

import java.awt.Color;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.KalamaHelperHelperH;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.InteractionTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.InteractUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.collections.FlagEntry;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

public class NoInteract extends BaseModule {
   public final NBTRef<WrapColor> renderFailInteractColor;
   public final ModulePath pU = makePath(Configs.n, "interaction-tweaks.no-interact");
   public final FlagRef sneakIfMayInstant;
   public final FlagRef renderFailInteract;
   public final FlagRef autoCorrectPlacement;
   public final FlagRef autoDisableSameTickOffhand;
   int qe;
   public final FlagRef noInteractVanillaOnly;
   public final FlagRef ae = this.flagBuilder(this.pU.addEnable()).build();
   public final FlagRef autoCorrectState;
   RenderCollector<Box> qf;
   public final KeyBindRef J = this.toggleHotkey(this.pU.addHotkey(), new MultiKeyBind(), this.pU.addEnable()).build();
   public final NBTRef<EntrySet<Item>> pW;
   public final FlagRef swingHand;
   public final FlagRef autoCorrectAirPlace;
   public final NBTRef<EntrySet<Block>> pV = this.builder(this.pU.add("no-interact-block"), EntrySet.<Block>parameter())
      .defaultValue(new EntrySet<Block>(new Regex("^(.*chest|.*pot)$"), Registries.BLOCK))
      .build();
   public final EnumRef<Configs$LegalInteractMode> autoCorrectMode;
   int qg;

   public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
      this.autoCorrectMode.set(Configs$LegalInteractMode.getFromPreset((ModulePreset)((KalamaHelperHelperI)event.b).b()));
      this.autoCorrectAirPlace.set(!((ModulePreset)((KalamaHelperHelperI)event.b).b()).hasAC());
   }

   public BlockHitResult handleMayAutoCorrect(BlockHitResult hitResult) {
      BlockPos var2 = InteractUtils.getCurrentPlacePos(mc.player, hitResult);
      FlagEntry var3 = InteractionTasks.l(var2, this.autoCorrectAirPlace.get(), !this.autoCorrectMode.get().isLegal());
      return var3 != null && !var3.flag() ? (BlockHitResult)var3.val() : null;
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bj(), this::onPreInteractBlock);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
      this.registerListener(RenderListener.q(), this::yB);
   }

   public void onPreInteractBlock(Event<KalamaHelperHelperH> event) {
      if (this.ae.get() && (!this.noInteractVanillaOnly.get() || InteractManager.INSTANCE.Lq)) {
         Hand var2 = ((KalamaHelperHelperH)event.b).d();
         ItemStack var3 = mc.player.getStackInHand(var2);
         BlockHitResult var4 = ((KalamaHelperHelperH)event.b).b();
         if (this.autoDisableSameTickOffhand.get() && var2 == Hand.OFF_HAND && InteractManager.INSTANCE.Lq && Tasks.b() == this.qe) {
            event.cancel();
            ((KalamaHelperHelperH)event.b).f(ActionResult.PASS);
            return;
         }

         if (var4 != null && !var3.isEmpty() && !this.pW.get().test(var3.getItem())) {
            BlockPos var5 = var4.getBlockPos();
            BlockState var6 = mc.world.getBlockState(var5);
            if (!this.pV.get().test(var6.getBlock())) {
               return;
            }

            boolean var7 = InteractUtils.t(mc.world, mc.player, var5, var6, var3);
            if (!InteractUtils.canInteractAndPlace(mc.player, var7)) {
               this.onFailOriginalInteract(var5, var6);
               if (var3.getItem() instanceof BlockItem) {
                  if (this.sneakIfMayInstant.get() && ViaFabricPlusHooks.isSupportInstaSneak() && !mc.player.isSneaking()) {
                     PlayerInputUtils.a(mc.player).rC(true).sendPlayerSneakUpdatePacket().applyInput(mc.player);
                     ClientPlayerAccess.of(mc.player).setLastSneakFlag(true);
                     return;
                  }

                  if (this.autoCorrectPlacement.get()) {
                     BlockPos var8 = InteractUtils.getCurrentPlacePos(mc.player, var4);
                     BlockState var9 = InteractUtils.c(mc.player, var2, var3, var4);
                     if (var9 != null) {
                        BlockHitResult var10 = this.handleMayAutoCorrect(var4);
                        if (var10 != null) {
                           if (this.autoCorrectState.get()) {
                              BlockRotate.INSTANCE.OO(var8, var9);
                           }

                           InteractionTasks.g(this.autoCorrectMode.get(), var10, var2, this.swingHand.get());
                           event.cancel();
                           ((KalamaHelperHelperH)event.b).f(ActionResult.SUCCESS);
                           return;
                        }
                     }
                  }
               }

               event.cancel();
               ((KalamaHelperHelperH)event.b).f(ActionResult.PASS);
               if (InteractManager.INSTANCE.Lq && var2 == Hand.MAIN_HAND) {
                  this.qe = Tasks.b();
               }
            }
         }
      }
   }

   public NoInteract() {
      super("NoInteract");
      this.pW = this.builder(this.pU.add("no-interact-ignore-item"), EntrySet.<Item>parameter())
         .defaultValue(new EntrySet<Item>(new Regex("^()$"), Registries.ITEM))
         .build();
      this.noInteractVanillaOnly = this.builder(this.pU.add("no-interact-vanilla-only"), Boolean.class).defaultValue(true).build();
      this.autoDisableSameTickOffhand = this.flagBuilder(this.pU.add("auto-disable-same-tick-offhand")).build();
      this.sneakIfMayInstant = this.flagBuilder(this.pU.add("sneak-if-may-instant")).build();
      this.autoCorrectPlacement = this.flagBuilder(this.pU.add("auto-correct-placement")).build();
      this.autoCorrectMode = this.builder(this.pU.add("auto-correct-mode"), Configs$LegalInteractMode.class)
         .defaultValue(Configs$LegalInteractMode.NONE)
         .build();
      this.autoCorrectAirPlace = this.flagBuilder(this.pU.add("auto-correct-air-place")).build();
      this.autoCorrectState = this.flagBuilder(this.pU.add("auto-correct-state")).build();
      this.swingHand = this.builder(this.pU.add("swing-hand"), Boolean.class).defaultValue(true).build();
      this.renderFailInteract = this.flagBuilder(this.pU.add("render-fail-interact")).build();
      this.renderFailInteractColor = this.builder(this.pU.add("render-fail-interact-color"), WrapColor.class).defaultValue(new WrapColor(Color.RED)).build();
      this.qe = 0;
      this.qf = RenderCollectors.createBoxCollector(true, false, false);
      this.qg = 0;
      this.bindFlag(this.ae);
   }

   public void yB(Event<MatrixStack> stackEvent) {
      if (this.ae.get() && this.renderFailInteract.get()) {
         if (Tasks.b() > this.qg + 200) {
            this.qf.clear();
         } else {
            RenderUtils.startDrawVirtual((MatrixStack)stackEvent.b);

            try {
               this.qf.a((MatrixStack)stackEvent.b);
            } finally {
               RenderUtils.stopDrawVirtual((MatrixStack)stackEvent.b);
            }
         }
      }
   }

   public void onFailOriginalInteract(BlockPos interactAt, BlockState state) {
      if (this.renderFailInteract.get()) {
         VoxelShape var3 = state.getCollisionShape(mc.world, interactAt);
         this.qf.clear();
         this.qg = Tasks.b();
         if (!var3.isEmpty()) {
            for (Box var5 : var3.getBoundingBoxes()) {
               this.qf.submit(var5.offset(interactAt), this.renderFailInteractColor.get().withAlpha(255));
            }
         }
      }
   }
}
