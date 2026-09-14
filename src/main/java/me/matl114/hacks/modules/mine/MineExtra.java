package me.matl114.hacks.modules.mine;

import com.mojang.datafixers.util.Pair;
import java.awt.Color;
import java.util.Objects;
import me.matl114.accessors.access.PlayerMoveC2SPacketAccess;
import me.matl114.accessors.hacks.PlayerInteractionAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.PacketManager;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.NetworkUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Unique;

public class MineExtra extends BaseModule {
   public final FlagRef ghostHandSwapWhenStart;
   BlockPos MU;
   public final FlagRef renderOnlyWhenMine;
   public final IntRef grimPunishmentThreshold;
   BlockPos MW;
   Direction MX;
   public final NBTRef<WrapColor> renderFrameColor;
   public final FlagRef sameBlockOptimize;
   public int Nb;
   public int Nf;
   public final EnumRef<MineExtra$Mode> bypassMode;
   boolean MY;
   public final FlagRef useFakeInstantBreak;
   public final DoubleRef breakThreshold;
   public final NBTRef<WrapColor> renderProgressColor;
   public final FlagRef fixSwingPacket;
   public final FlagRef silentBreak;
   public final KeyBindRef MB;
   public final FlagRef ghostHandMine;
   public static MineExtra INSTANCE;
   public boolean Nd;
   public final IntRef breakCooldown;
   public final FlagRef MA;
   public int MZ;
   public int Na;
   public Pair<Runnable, BlockPos> MV;
   public final FlagRef doubleBreak;
   public int Nc;
   public final FlagRef vanillaBreak;
   public final NBTRef<WrapColor> doubleBreakColor;
   public final FlagRef fixMultiBreak;
   public int Ne;
   public final ModulePath Mz = makePath(Configs.g, "fast-break");
   int MT;
   public int Ng;
   public final FlagRef renderCurrentBreakPos;

   public void adi(BlockPos pos, double speed, double currentProgress) {
      this.Nf = 0;
      if (this.Nc != 0) {
         MineExtra var6 = INSTANCE;
         int var7 = (int)Math.ceil(1.0 / speed);
         int var8 = (int)Math.ceil(currentProgress / speed);
         int var9 = var7 - var8;
         this.Ne += (var9 + 1) * 50;
         int var10 = var6.grimPunishmentThreshold.get();
         this.Ne = MathHelper.clamp(this.Ne, -1000, 1000);
         if (this.Ne > var10 && var6.bypassMode.getValue() == MineExtra$Mode.BYPASS_GRIM_LEGIT) {
            this.Nf = 2;
         }
      }
   }

   public void B(Event<MatrixStack> renderEvent) {
      if (this.renderCurrentBreakPos.get()) {
         RenderUtils.startDrawVirtual((MatrixStack)renderEvent.b);

         try {
            if (mc.interactionManager != null && mc.player != null && mc.world != null) {
               BlockPos var2 = PlayerInteractionAccess.of(mc.interactionManager).getCurrentMiningPos();
               Vec3d var3 = Vec3d.of(var2);
               if (mc.player.getPos().squaredDistanceTo(var3) < 40000.0 && this.adp()) {
                  RenderUtils.drawOutlinedBox(
                     (MatrixStack)renderEvent.b, var3, var3.add(1.0, 1.0, 1.0), ColorUtils.h(this.renderFrameColor.get().color(), 1.0F)
                  );
                  BlockState var4 = mc.world.getBlockState(var2);
                  KalamaHelperHelperK var5 = this.acU(var4);
                  float var6 = PlayerInteractionAccess.of(mc.interactionManager).getCurrentMiningProgress((ItemStack)var5.val());
                  if (var6 > 0.0F) {
                     Box var7;
                     if (var4.isAir()) {
                        var7 = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
                     } else {
                        VoxelShape var8 = var4.getOutlineShape(mc.world, var2);
                        var7 = var8.isEmpty() ? new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0) : var8.getBoundingBox();
                     }

                     Vec3d var19 = var7.getMaxPos().subtract(var7.getMinPos()).multiply(0.5);
                     Vec3d var9 = var3.add(var7.getCenter());
                     float var10 = MathHelper.clamp(var6, 0.0F, 1.0F);
                     RenderUtils.r(
                        (MatrixStack)renderEvent.b,
                        var9.add(var19.multiply(-var10)),
                        var9.add(var19.multiply(var10)),
                        ColorUtils.h(this.renderProgressColor.get().color(), 0.25F)
                     );
                  }
               }

               BlockPos var15 = PlayerInteractionAccess.of(mc.interactionManager).getCurrentFailBreakPos();
               if (var15 != null) {
                  Vec3d var16 = Vec3d.of(var15);
                  if (mc.player.getPos().squaredDistanceTo(var16) < 40000.0 && !Objects.equals(var16, var3)) {
                     float var17 = PlayerInteractionAccess.of(mc.interactionManager).getFailBreakMiningProgress();
                     RenderUtils.drawOutlinedBox(
                        (MatrixStack)renderEvent.b, var16, var16.add(1.0, 1.0, 1.0), ColorUtils.h(this.doubleBreakColor.get().color(), 1.0F)
                     );
                     if (var17 > 0.0F) {
                        BlockState var18 = mc.world.getBlockState(var15);
                        Box var20;
                        if (var18.isAir()) {
                           var20 = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
                        } else {
                           VoxelShape var21 = var18.getOutlineShape(mc.world, var15);
                           var20 = var21.isEmpty() ? new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0) : var21.getBoundingBox();
                        }

                        Vec3d var22 = var20.getMaxPos().subtract(var20.getMinPos()).multiply(0.5);
                        Vec3d var23 = var16.add(var20.getCenter());
                        float var11 = MathHelper.clamp(var17, 0.0F, 1.0F);
                        RenderUtils.r(
                           (MatrixStack)renderEvent.b,
                           var23.add(var22.multiply(-var11)),
                           var23.add(var22.multiply(var11)),
                           ColorUtils.h(this.renderProgressColor.get().color(), 0.25F)
                        );
                     }
                  }
               }
            }
         } finally {
            RenderUtils.stopDrawVirtual((MatrixStack)renderEvent.b);
         }
      }
   }

   public boolean adk(float currentProgress) {
      return this.MA.get() && this.Nf <= 0 ? currentProgress >= this.breakThreshold.get() : currentProgress > 1.0;
   }

   public void acW(Event<PlayerActionC2SPacket> packetEvent) {
      PlayerActionC2SPacket var2 = (PlayerActionC2SPacket)packetEvent.e();
      if (this.MV != null
         && var2.getAction() == Action.START_DESTROY_BLOCK
         && this.ghostHandMine.get()
         && mc.player != null
         && mc.interactionManager != null
         && Objects.equals(var2.getPos(), this.MV.getSecond())) {
         if (this.MV.getFirst() != null) {
            PacketManager.d(var2, (Runnable)(Object)this.MV.getFirst());
         }

         this.MV = null;
      }

      switch (var2.getAction()) {
         case START_DESTROY_BLOCK:
            if (var2.getPos().getY() < 1145) {
               this.MU = var2.getPos();
            }
            break;
         case STOP_DESTROY_BLOCK:
            this.MU = null;
            this.MZ = Tasks.b();
            this.Na = Tasks.b() + this.adf();
            break;
         case ABORT_DESTROY_BLOCK:
            if (!Objects.equals(this.MU, var2.getPos())) {
               packetEvent.cancel();
            } else {
               this.MU = null;
            }
            break;
         default:
            return;
      }

      PlayerInteractionAccess var3 = PlayerInteractionAccess.of(mc.interactionManager);
      if (var2.getAction() == Action.START_DESTROY_BLOCK && Objects.equals(var3.getCurrentMiningPos(), var2.getPos())) {
         this.Nc = Tasks.b();
      }

      if (this.fixSwingPacket.get()
         && var2.getAction() == Action.STOP_DESTROY_BLOCK
         && Tasks.b() != this.MT
         && Objects.equals(var3.getCurrentMiningPos(), var2.getPos())) {
         mc.player.swingHand(Hand.MAIN_HAND);
      }

      if (var2.getAction() != Action.ABORT_DESTROY_BLOCK) {
         if (this.MY && (var2.getDirection() != this.MX || Objects.equals(this.MW, var2.getPos())) && this.fixMultiBreak.get()) {
            PacketManager.d(
               var2,
               () -> Listener.sendPacketNoEvents(
                  new PlayerActionC2SPacket(var2.getAction(), var2.getPos(), var2.getDirection(), NetworkUtils.generateNextSequence())
               )
            );
         }

         this.MX = var2.getDirection();
         this.MW = var2.getPos();
         this.MY = true;
      }
   }

   public void adh(BlockPos pos) {
      MineExtra var2 = INSTANCE;
      int var3 = var2.grimPunishmentThreshold.get();
      this.Nf = 0;
      if (var2.bypassMode.getValue() == MineExtra$Mode.BYPASS_GRIM_BAD_PACKETS) {
         this.Ne = 0;
      } else {
         this.Ne = (int)(this.Ne * 0.9);
         if (this.Ne > var3 && var2.bypassMode.getValue() == MineExtra$Mode.BYPASS_GRIM_LEGIT && mc.player != null) {
            this.Ne = 150;
            if (!mc.player.getAbilities().creativeMode) {
               ClientPlayerEntity var4 = MinecraftClient.getInstance().player;
               Direction var5 = Direction.getFacing(pos.toCenterPos().subtract(var4.getEyePos())).getOpposite();

               for (int var6 = 0; var6 < 20; var6++) {
                  mc.interactionManager.sendSequencedPacket(mc.world, sequence -> new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, pos, var5, sequence));
               }
            }
         }
      }

      this.Nb = MathHelper.clamp(this.Nb, -1000, 1000);
   }

   public void adb() {
      this.MZ = 0;
      this.Na = 0;
      this.Nb = 0;
      this.Nd = false;
      this.Ne = 0;
      this.Nf = 0;
   }

   public void adg(BlockPos pos, float speed, boolean instaBreak) {
      this.Nd = instaBreak || speed > Math.min(1.0, this.breakThreshold.get());
      if (this.MA.get()) {
         if (this.MZ != 0) {
            if (!instaBreak) {
               int var5 = Tasks.b();
               boolean var6 = false;
               if (var5 >= this.MZ + 5) {
                  var6 = true;
                  this.Nb = (int)(this.Nb * 0.9);
               } else {
                  this.Nb = this.Nb + (300 - (var5 - this.MZ) * 50);
               }

               int var7 = this.grimPunishmentThreshold.get();
               if (this.Nb > var7 && var6 && this.bypassMode.getValue() == MineExtra$Mode.BYPASS_GRIM_LEGIT && mc.player != null) {
                  this.Nb = 150;
                  if (!mc.player.getAbilities().creativeMode) {
                     ClientPlayerEntity var8 = MinecraftClient.getInstance().player;
                     Direction var9 = Direction.getFacing(pos.toCenterPos().subtract(var8.getEyePos())).getOpposite();

                     for (int var10 = 0; var10 < 20; var10++) {
                        mc.interactionManager
                           .sendSequencedPacket(
                              MinecraftClient.getInstance().world, sequence -> new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, pos, var9, sequence)
                           );
                     }
                  }
               }

               this.Nb = MathHelper.clamp(this.Nb, -1000, 1000);
            }
         }
      }
   }

   public boolean adn(float predictedProgress) {
      return this.doubleBreak.get() && predictedProgress <= 1.0F;
   }

   public void Jr(Event<ClientPlayerEntity> gameJoin) {
      this.adb();
   }

   public void acZ(Event<PlayerActionC2SPacket> eventPlayerAction) {
      if (this.silentBreak.get()
         && ((PlayerActionC2SPacket)eventPlayerAction.b).getAction() == Action.START_DESTROY_BLOCK
         && Objects.equals(PlayerInteractionAccess.of(mc.interactionManager).getCurrentMiningPos(), ((PlayerActionC2SPacket)eventPlayerAction.b).getPos())) {
         BlockPos var2 = ((PlayerActionC2SPacket)eventPlayerAction.b).getPos();
         PacketManager.d((PlayerActionC2SPacket)eventPlayerAction.b, () -> {
            PlayerInteractionAccess var1 = PlayerInteractionAccess.of(mc.interactionManager);
            if (Objects.equals(var2, var1.getCurrentMiningPos())) {
               var1.sendAbortBreakPacket();
            }
         });
      }
   }

   @Unique
   public int adf() {
      boolean var1 = this.MA.get();
      int var2 = var1 && this.breakCooldown.get() >= 0 ? this.breakCooldown.get() : 5;
      if (var2 < 5 && this.bypassMode.get().hasAc()) {
         if (this.bypassMode.get() == MineExtra$Mode.BYPASS_GRIM_LEGIT) {
            if (this.Nb > this.grimPunishmentThreshold.get()) {
               return 5;
            }
         } else if (this.bypassMode.get() == MineExtra$Mode.BYPASS_GRIM_BAD_PACKETS) {
            if (this.doubleBreak.get()) {
               if (this.adc(5) || this.Nb > 300) {
                  return 5;
               }
            } else if (this.Nb > 300) {
               return 5;
            }
         }
      }

      return var2;
   }

   public boolean ado() {
      if (this.Nf > 0) {
         this.Nf--;
         if (this.Nf > 0) {
            return false;
         }
      }

      return true;
   }

   public boolean add(int extra) {
      return Tasks.b() - this.MZ >= 5 + extra;
   }

   public float adq(BlockPos pos) {
      if (mc.player.isCreative()) {
         return 10000.0F;
      } else {
         BlockState var2 = mc.world.getBlockState(pos);
         KalamaHelperHelperK var3 = this.acU(var2);
         float var4 = WorldUtils.getPlayerBlockBreakingSpeedWithCanMineMultiply(mc.player, var2, (ItemStack)var3.val());
         return WorldUtils.getPlayerBlockBreakingSpeedAt(var2);
      }
   }

   public void le(Event<KalamaHelperHelperI<ModulePreset>> presetEvent) {
      ModulePreset var2 = (ModulePreset)((KalamaHelperHelperI)presetEvent.e()).b();
      switch (var2) {
         case fg:
         case fh:
            this.bypassMode.set(MineExtra$Mode.BYPASS_GRIM_BAD_PACKETS);
            break;
         default:
            this.bypassMode.set(MineExtra$Mode.NO_BYPASS);
      }
   }

   public boolean adl(float speed) {
      return speed >= 1.0F || speed > this.breakThreshold.get() && this.MA.get();
   }

   public MineExtra() {
      super("MineExtra");
      this.MA = this.flagBuilder(this.Mz.addEnable()).build();
      this.MB = this.moduleEntry(this.Mz.addHotkey(), new MultiKeyBind(), this.Mz.addEnable()).build();
      this.useFakeInstantBreak = this.flagBuilder(this.Mz.add("use-fake-instant-break")).build();
      this.bypassMode = this.builder(this.Mz.add("bypass-mode"), MineExtra$Mode.class).defaultValue(MineExtra$Mode.NO_BYPASS).build();
      this.grimPunishmentThreshold = this.builder(this.Mz.add("grim-punishment-threshold"), Integer.class)
         .defaultValue(500)
         .show(() -> this.bypassMode.get() == MineExtra$Mode.BYPASS_GRIM_LEGIT)
         .build();
      this.breakThreshold = this.builder(this.Mz.add("break-threshold"), Double.class).defaultValue(0.99).validator(Configs.doubleRange(0.0, 1.1)).build();
      this.breakCooldown = this.builder(this.Mz.add("break-cooldown"), Integer.class).defaultValue(5).validator(Configs.d).build();
      this.vanillaBreak = this.builder(this.Mz.add("vanilla-break"), Boolean.class).defaultValue(true).build();
      this.doubleBreak = this.flagBuilder(this.Mz.add("double-break")).build();
      this.sameBlockOptimize = this.flagBuilder(this.Mz.add("same-block-optimize")).build();
      this.ghostHandMine = this.flagBuilder(this.Mz.add("ghost-hand-mine")).build();
      this.ghostHandSwapWhenStart = this.flagBuilder(this.Mz.add("ghost-hand-swap-when-start")).show(this.ghostHandMine::get).build();
      this.fixMultiBreak = this.flagBuilder(this.Mz.add("fix-multi-break")).build();
      this.fixSwingPacket = this.flagBuilder(this.Mz.add("fix-swing-packet")).build();
      this.silentBreak = this.flagBuilder(this.Mz.add("silent-break")).build();
      this.renderCurrentBreakPos = this.flagBuilder(this.Mz.add("render-current-break-pos")).build();
      this.renderFrameColor = this.builder(this.Mz.add("render-frame-color"), WrapColor.class).defaultValue(new WrapColor(Color.BLUE)).build();
      this.renderProgressColor = this.builder(this.Mz.add("render-progress-color"), WrapColor.class).defaultValue(new WrapColor(Color.YELLOW)).build();
      this.doubleBreakColor = this.builder(this.Mz.add("double-break-color"), WrapColor.class).defaultValue(new WrapColor(Color.MAGENTA)).build();
      this.renderOnlyWhenMine = this.flagBuilder(this.Mz.add("render-only-when-mine")).build();
      this.MT = 0;
      this.Nc = 0;
      this.Nd = false;
      this.Nf = 0;
      this.Ng = 0;
      INSTANCE = this;
   }

   public boolean adc(int extra) {
      return Tasks.b() - this.Ng >= 5 + extra;
   }

   public boolean adm(float speed) {
      return this.MA.get() && this.useFakeInstantBreak.get() && speed < 1.0F && speed > this.breakThreshold.get();
   }

   public void acY(Event<PlayerActionC2SPacket> event) {
      if (this.MA.get() && this.bypassMode.get() == MineExtra$Mode.BYPASS_GRIM_BAD_PACKETS && mc.player != null) {
         PlayerActionC2SPacket var2 = (PlayerActionC2SPacket)event.e();
         if (var2.getAction() == Action.START_DESTROY_BLOCK
            && var2.getPos().getY() < 1145
            && Objects.equals(PlayerInteractionAccess.of(mc.interactionManager).getCurrentMiningPos(), var2.getPos())) {
            if (mc.player.getAbilities().creativeMode) {
               this.Nb = 150;
               return;
            }

            BlockState var3 = mc.world.getBlockState(var2.getPos());
            if (var3.isAir()) {
               return;
            }

            double var4 = WorldUtils.i(var3, mc.world, var2.getPos());
            if (var4 > 1.01) {
               return;
            }

            int var6 = this.doubleBreak.get() && this.add(0) && this.Nb > 150 ? 6 : 1;
            PacketManager.d(
               var2,
               () -> {
                  for (int var2x = 0; var2x < var6; var2x++) {
                     mc.interactionManager
                        .sendSequencedPacket(
                           mc.world,
                           seq -> new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, BlockPos.ofFloored(mc.player.getPos()).withY(9178), Direction.DOWN, seq)
                        );
                     if (Tasks.b() - this.MZ >= 5) {
                        this.Nb = (int)(this.Nb * 0.9);
                     } else {
                        this.Nb = this.Nb + (300 - (Tasks.b() - this.MZ) * 50);
                     }
                  }
               }
            );
         }
      }
   }

   public boolean adj() {
      return this.Nc != 0;
   }

   public int ade() {
      return Math.max(0, this.Na - Tasks.b());
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(RenderListener.q(), this::B);
      this.registerListener(Listener.bx().c(ModulePreset.class), this::le);
      this.registerListener(Listener.M(), this::Jr);
      this.registerListener(Listener.ap().getChannel(PlayerActionC2SPacket.class), this::acW);
      this.registerListener(Listener.ap().getChannel(PlayerActionC2SPacket.class), this::acY);
      this.registerListener(Listener.ap().getChannel(PlayerActionC2SPacket.class), this::acZ);
      this.registerListener(Listener.ao().getChannel(HandSwingC2SPacket.class), this::acV);
      this.registerListener(Listener.U(), this::ada);
      this.registerListener(Listener.ap().getChannel(PlayerMoveC2SPacket.class), this::acX);
   }

   private boolean adp() {
      if (!this.renderOnlyWhenMine.get()) {
         return true;
      } else {
         if (!mc.interactionManager.isBreakingBlock() && !PacketMine.INSTANCE.I.get()) {
            if (!this.sameBlockOptimize.get()) {
               return false;
            }

            BlockPos var1 = PlayerInteractionAccess.of(mc.interactionManager).getCurrentMiningPos();
            BlockState var2 = mc.world.getBlockState(var1);
            if (var2.isAir()) {
               return false;
            }
         }

         return true;
      }
   }

   public KalamaHelperHelperK<ItemStack> acU(BlockState currentState) {
      KalamaHelperHelperK var2 = InventoryUtils.getSelectedItem();
      if (!this.ghostHandMine.get()) {
         return var2;
      } else {
         BlockState var3 = !currentState.isAir() && !currentState.isLiquid() ? currentState : Blocks.OBSIDIAN.getDefaultState();
         double var4 = WorldUtils.getPlayerBlockBreakingSpeedWithCanMineMultiply(mc.player, var3, (ItemStack)var2.val());
         KalamaHelperHelperK var6 = InventoryUtils.v(
            item -> {
               if (!item.isEmpty()
                  && item.getMaxDamage() >= 10
                  && !item.contains(DataComponentTypes.UNBREAKABLE)
                  && item.getDamage() >= item.getMaxDamage() - 10) {
                  return null;
               } else {
                  double var4x = WorldUtils.getPlayerBlockBreakingSpeedWithCanMineMultiply(mc.player, var3, item);
                  return var4x >= var4 ? var4x : null;
               }
            },
            true,
            true
         );
         return var6 != null ? var6 : var2;
      }
   }

   public void acV(Event<HandSwingC2SPacket> handSwingC2SPacketEvent) {
      this.MT = Tasks.b();
   }

   public void acX(Event<PlayerMoveC2SPacket> packetEvent) {
      PlayerMoveC2SPacketAccess var2 = PlayerMoveC2SPacketAccess.of((PlayerMoveC2SPacket)packetEvent.b);
      if (var2.getCause() != PlayerMoveC2SPacketAccess.Cause.SET_BACK
         && var2.getCause() != PlayerMoveC2SPacketAccess.Cause.LEGACY_SNAP
         && var2.getCause() != PlayerMoveC2SPacketAccess.Cause.TRIGGER_SIMULATION) {
         this.MY = false;
      }
   }

   public void ada(Event<ClientPlayerEntity> tickEvent) {
      if (this.MA.get() && this.bypassMode.get() == MineExtra$Mode.BYPASS_GRIM_BAD_PACKETS && mc.player != null && this.MU != null && Tasks.b() - this.MZ == 6) {
         if (mc.player.getAbilities().creativeMode) {
            this.Nb = 150;
            return;
         }

         do {
            mc.interactionManager
               .sendSequencedPacket(
                  mc.world,
                  seq -> new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, BlockPos.ofFloored(mc.player.getPos()).withY(9178), Direction.DOWN, seq)
               );
            this.Nb = (int)(this.Nb * 0.9);
         } while (this.Nb > 150);
      }
   }
}
