package me.matl114.hacks.modules.slimefun;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import me.matl114.accessors.access.ClientAccess;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.accessors.access.HandledScreenAccess;
import me.matl114.accessors.interfaces.TileInventory;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.CombatPlayer;
import me.matl114.events.impl.KalamaHelperHelperH;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.complex.slimefun.SlimefunDispensorSuggestBookWidget;
import me.matl114.hacks.SlimefunTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.hacks.utils.multiblock.BlockMatcher;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.collections.Point;
import me.matl114.utils.containers.KalamaHelperHelperB;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;

public class MultiBlockHelper extends BaseModule {
   public final IntRef rate;
   static Direction[] in = new Direction[]{Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST};
   private int ie;
   public final FlagRef ia;
   private SlimefunDispensorSuggestBookWidget ii;
   private static final int[] AVAILABLE_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
   private List<Pair<BlockPos, TileInventory>> il;
   private int im;
   static Direction[] io = new Direction[]{Direction.NORTH, Direction.WEST};
   public final ModulePath hZ = makePath(Configs.p, "multi-block-clicker");
   public final EnumRef<Configs$LegalInteractMode> bypassTargetingMode;
   public final FlagRef enableCrafterGui;
   private static final String KEY_BOOK_WIDGET = "kalama:multiblock_suggestion_book_widget";
   private final Random ih;
   private long lastAutoTick;
   private int clickCooldown;

   public static List<BlockPos> getPositionByDirection(SlimefunSubHelperO entry, BlockPos blockPos, CombatPlayer direcion) {
      blockPos.add(0, -direcion.popCnt, 0);
      int var3 = direcion.popCnt;
      Direction var4 = direcion.player;
      BlockMatcher[] var5 = entry.blockTypes();
      ArrayList var6 = new ArrayList(9);
      BlockMatcher[] var7 = entry.blockTypes();

      for (int var8 = -1; var8 <= 1; var8++) {
         for (int var9 = 0; var9 <= 2; var9++) {
            if (var5[3 * var9 + var8 + 1] != BlockMatcher.Wf) {
               var6.add(blockPos.add(var8 * var4.getOffsetX(), var9 - var3, var8 * var4.getOffsetZ()));
            }
         }
      }

      return var6;
   }

   public static boolean anyMatchMiddle(SlimefunSubHelperO entry, World world, BlockPos blockPos) {
      return matchDirection(entry, world, blockPos) != null;
   }

   public void toggleMultiBlockAutoExecuteState(TileInventory screen, boolean val) {
      if (screen.isVirtual()) {
         Debug.b(Text.literal("[自动多方块] 找不到该屏幕对应的方块位置"));
      } else {
         BlockPos var3 = screen.getPos();
         this.il.removeIf(i -> Objects.equals(i.getFirst(), var3));
         if (val) {
            this.il.add(Pair.of(var3, screen));
         }

         Debug.b(Text.literal("[自动多方块] 已切换该屏幕的自动执行状态,目前有 %d 个自动执行中(长按下蹲以全部关闭)".formatted(this.il.size())).formatted(Formatting.GREEN));
      }
   }

   public void clickSnap(BlockHitResult result, Vec2f pitchYaw, int clickRate) {
      LegacySnapRotManager.INSTANCE.snapAt(pitchYaw.x, pitchYaw.y, false);

      for (int var4 = 0; var4 < clickRate; var4++) {
         mc.interactionManager.sendSequencedPacket(mc.world, sequence -> new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, sequence));
      }
   }

   public void onTick(Event<ClientPlayerEntity> player) {
      if (!this.il.isEmpty()) {
         long var2 = System.currentTimeMillis();
         if (var2 > this.lastAutoTick + (null == mc.currentScreen ? 2 : 1) * 300) {
            if (mc.player != null && mc.player.isSneaking()) {
               Debug.b(Text.literal("[自动多方块] 检测到长按下蹲,清除全部的执行中多方块"));
               this.clearMultiBlockExecuteTasks();
            } else {
               this.lastAutoTick = var2;
               int var4 = ++this.im % this.il.size();
               Pair var5 = this.il.get(var4);
               if (!(var5 != null && var5.getSecond() instanceof TileInventory var7)) {
                  Debug.b(Text.literal("[自动多方块] 当前执行的界面并没有位置记录,已自动移除"));
                  this.il.remove(var4);
                  this.im--;
               } else if (!var7.isVirtual() && var7.getBlockType() == Blocks.DISPENSER) {
                  this.onMultiBlockExecute(var7.castHandled(), true, false);
               }
            }
         }
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.bk(), this::lt);
      this.registerListener(Listener.U(), this::onTick);
      this.registerListener(Listener.O(), this::lu);
      this.registerListener(Listener.ai().c(HandledScreen.class), this::lv);
   }

   public void clearMultiBlockExecuteTasks() {
      Debug.b(Text.literal("[自动多方块] 已清除 %d 个执行中多方块".formatted(this.il.size())).formatted(Formatting.GREEN));
      this.il.clear();
      this.im = 0;
   }

   public void clickUsePacket(BlockHitResult result, Vec2f pitchYaw, int clickRate) {
      Hand var4;
      if (!mc.player.getMainHandStack().isEmpty()) {
         var4 = Hand.MAIN_HAND;
      } else if (!mc.player.getOffHandStack().isEmpty()) {
         var4 = Hand.OFF_HAND;
      } else {
         var4 = null;
      }

      if (var4 != null) {
         for (int var5 = 0; var5 < clickRate; var5++) {
            mc.interactionManager.sendSequencedPacket(mc.world, z -> new PlayerInteractItemC2SPacket(var4, z, pitchYaw.y, pitchYaw.x));
            mc.interactionManager.sendSequencedPacket(mc.world, sequence -> new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, sequence));
         }

         ClientAccess.of(mc).setItemUseCooldown(0);
      } else {
         this.clickSnap(result, pitchYaw, clickRate);
      }
   }

   public void clickDelayMovement(BlockHitResult result, Vec2f pitchYaw, int clickRate) {
      ClientPlayerAccess.of(mc.player).getLegalMovementManager().i(new SlimefunSubHelperI(this, pitchYaw, clickRate, result));
   }

   public void lu(Event<Void> event) {
      this.il.clear();
      this.im = 0;
   }

   private void onClickBlockExecute(BlockHitResult result, boolean delayClick, boolean clickMany) {
      if (result != null) {
         BlockPos var4 = result.getBlockPos();
         Block var5 = mc.world.getBlockState(var4).getBlock();
         if (var5 != Blocks.DISPENSER && var5 != Blocks.DROPPER) {
            Set var6 = SlimefunTasks.u().WM(var5);
            if (var6 != null && !var6.isEmpty()) {
               Optional var7 = var6.stream().filter(m -> anyMatchMiddle(m, mc.world, var4)).findFirst();
               if (!var7.isEmpty()) {
                  if (this.ie + 100 < Tasks.b()) {
                     Debug.chat(Text.literal("[MBHelper] Interacting with multiblock: ").formatted(Formatting.RED), ((SlimefunSubHelperO)var7.get()).id());
                     this.ie = Tasks.b();
                  }

                  int var8 = clickMany ? this.rate.get() : 1;
                  boolean var9 = false;
                  if (mc.crosshairTarget != null
                     && mc.crosshairTarget.getType() == Type.BLOCK
                     && Objects.equals(((BlockHitResult)mc.crosshairTarget).getBlockPos(), result.getBlockPos())) {
                     var9 = true;
                  }

                  if (!var9 && this.bypassTargetingMode.get().isLegal()) {
                     if (clickMany && this.clickCooldown + 5 >= Tasks.b()) {
                        Debug.b(Text.literal("[AC] 你点的太快了,可能无法通过反作弊"));
                     } else {
                        this.clickCooldown = Tasks.b();
                        Vec3d var15 = result.getBlockPos().toCenterPos();
                        Vec3d var11 = var15.add(this.ih.nextDouble(-0.05, 0.05), this.ih.nextDouble(-0.05, 0.05), this.ih.nextDouble(-0.05, 0.05));
                        Vec3d var12 = var11.subtract(mc.player.getEyePos()).normalize();
                        Vec2f var13 = EntityUtils.q(var12);
                        switch ((Configs$LegalInteractMode)this.bypassTargetingMode.get()) {
                           case USEITEM_PACKET:
                              this.clickUsePacket(result, var13, var8);
                              break;
                           case LEGACY_SLIENT_ROT:
                              this.clickSnap(result, var13, var8);
                              break;
                           case DELAY_MOVEMENT:
                           case MOVEMENT_POST:
                              this.clickDelayMovement(result, var13, var8);
                        }
                     }
                  } else {
                     for (int var10 = 0; var10 < var8; var10++) {
                        mc.interactionManager.sendSequencedPacket(mc.world, sequence -> new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, sequence));
                     }

                     if (delayClick && clickMany) {
                        AtomicInteger var14 = new AtomicInteger(2);
                        Tasks.m(
                           () -> {
                              for (int var3 = 0; var3 < var8; var3++) {
                                 mc.interactionManager
                                    .sendSequencedPacket(mc.world, sequence -> new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, sequence));
                              }

                              return var14.decrementAndGet() <= 0;
                           },
                           3,
                           4
                        );
                     }

                     ClientAccess.of(mc).setItemUseCooldown(0);
                  }
               }
            }
         }
      }
   }

   private void lv(Event<HandledScreen<?>> event) {
      if (this.enableCrafterGui.get() && event.e() instanceof TileInventory var3 && event.e() instanceof Generic3x3ContainerScreen var4) {
         HandledScreenAccess var9 = HandledScreenAccess.of(var4);
         KalamaHelperHelperB var5 = var9.getMetadata();
         SlimefunDispensorSuggestBookWidget var6 = var5.b(this, "kalama:multiblock_suggestion_book_widget");
         if (var6 == null) {
            Collection var7 = var3.isVirtual()
               ? null
               : SlimefunTasks.k(var3.getWorld(), var3.getPos())
                  .stream()
                  .map(SlimefunTasks::i)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .map(SlimefunSubHelperQ::id)
                  .collect(Collectors.toSet());
            var6 = new SlimefunDispensorSuggestBookWidget(
               var3, 3, 3, var7, (bol, entry) -> SlimefunTasks.moveSlimefunRecipePatternToContainer(entry, var4.getScreenHandler(), bol, true, AVAILABLE_SLOTS)
            );
            var5.a(this, "kalama:multiblock_suggestion_book_widget", var6);
         }

         var6.gA();
         new ContentDelegateWidget<SlimefunDispensorSuggestBookWidget>(var9.getScreenX(), var9.getScreenY(), 0, 0).setContentDelegate(var6).addTo(var4);
      }
   }

   public void lt(Event<KalamaHelperHelperH> result) {
      if (this.ia.get()) {
         this.onClickBlockExecute(((KalamaHelperHelperH)result.b).b(), false, true);
      }
   }

   public MultiBlockHelper() {
      super("MultiBlockHelper");
      this.ia = this.flagBuilder(this.hZ.addEnable()).build();
      this.enableCrafterGui = this.flagBuilder(this.hZ.add("enable-crafter-gui")).build();
      this.rate = this.builder(this.hZ.add("rate"), IntRef.TYPE).defaultValue(9).validator(Configs.e).build();
      this.bypassTargetingMode = this.builder(this.hZ.add("bypass-targeting-mode"), Configs$LegalInteractMode.class)
         .defaultValue(Configs$LegalInteractMode.USEITEM_PACKET)
         .build();
      this.ie = 0;
      this.clickCooldown = 0;
      this.ih = new Random();
      this.il = new ArrayList<>();
      this.im = 0;
      this.bindFlag(this.ia);
   }

   public static CombatPlayer matchDirection(SlimefunSubHelperO entry, World world, BlockPos blockPos) {
      BlockMatcher[] var3 = entry.blockTypes();
      boolean var4 = entry.symm();

      for (int var5 = 0; var5 <= 2; var5++) {
         Mutable var6 = blockPos.mutableCopy().move(0, -var5, 0);
         int var7 = 0;

         while (true) {
            if (var7 <= 2) {
               Block var8 = world.getBlockState(var6).getBlock();
               if (var3[1 + 3 * var7].match(var8)) {
                  var6.move(0, 1, 0);
                  var7++;
                  continue;
               }
            } else {
               Object var16 = null;

               label55:
               for (Direction var11 : var4 ? io : in) {
                  Mutable var12 = blockPos.mutableCopy().move(0, -var5, 0).move(var11);

                  for (int var13 = 0; var13 <= 2; var13++) {
                     Block var14 = world.getBlockState(var12).getBlock();
                     if (!var3[3 * var13].match(var14)) {
                        continue label55;
                     }

                     var12.move(0, 1, 0);
                  }

                  if (var4) {
                     return new CombatPlayer(var5, var11);
                  }

                  Mutable var18 = blockPos.mutableCopy().move(0, -var5, 0).move(var11, -1);

                  for (int var19 = 0; var19 <= 2; var19++) {
                     Block var15 = world.getBlockState(var18).getBlock();
                     if (!var3[3 * var19 + 2].match(var15)) {
                        continue label55;
                     }

                     var18.move(0, 1, 0);
                  }

                  return new CombatPlayer(var5, var11);
               }
            }
            break;
         }
      }

      return null;
   }

   public static Collection<BlockPos> lF(SlimefunSubHelperO entry, ClientWorld world, BlockPos pos) {
      SlimefunSubHelperN var3 = entry.lookup();
      Collection<Point> var4 = entry.optionalActionBlock();
      Collection<SlimefunSubHelperF> var5 = var3.lookup(world, pos);
      if (var5 != null && !var5.isEmpty()) {
         HashSet var6 = new HashSet();

         for (SlimefunSubHelperF var8 : var5) {
            for (Point var10 : var4) {
               var6.add(var8.getComponentBlock(var10.a, var10.b));
            }
         }

         return var6;
      } else {
         return Set.of();
      }
   }

   public void onMultiBlockExecute(Screen executingScreen, boolean clickMany, boolean clickDouble) {
      if (mc.player != null && executingScreen instanceof TileInventory var4 && !var4.isVirtual() && var4.getWorld() == mc.world) {
         BlockPos var5 = var4.getPos();
         Block var6 = var4.getBlockType();
         if (var5.toCenterPos().squaredDistanceTo(mc.player.getPos()) > 50.0) {
            Debug.b(Text.literal("[多方块执行] 你离着自动执行的多方块太远了,已关闭自动执行"));
            this.toggleMultiBlockAutoExecuteState(var4, false);
         } else {
            if (mc.currentScreen instanceof TileInventory var8 && Objects.equals(var5, var8.getPos())) {
               boolean var14 = false;

               for (Slot var10 : var8.castHandled().getScreenHandler().slots) {
                  if (var10.inventory instanceof PlayerInventory) {
                     break;
                  }

                  if (!var10.getStack().isEmpty()) {
                     var14 = true;
                     break;
                  }
               }

               if (!var14) {
                  return;
               }
            }

            boolean var16 = false;
            if (var6 == Blocks.DISPENSER || var6 == Blocks.DROPPER) {
               for (SlimefunSubHelperO var17 : SlimefunTasks.u().WL().values()) {
                  Collection<BlockPos> var18 = lF(var17, mc.world, var5);
                  if (!var18.isEmpty()) {
                     var16 = true;

                     for (BlockPos var12 : var18) {
                        BlockHitResult var13 = RaycastUtils.f(var12);
                        this.onClickBlockExecute(var13, clickDouble, clickMany);
                     }
                  }
               }
            }

            if (!var16) {
               Debug.b(Text.literal("[多方块执行] 多方块结构与已记录的多方块无法匹配").formatted(Formatting.RED));
               this.toggleMultiBlockAutoExecuteState(var4, false);
            }
         }
      }
   }

   public boolean isMultiBlockExecuting(TileInventory screen) {
      return this.il.stream().anyMatch(i -> Objects.equals(screen.getPos(), i.getFirst()));
   }
}
