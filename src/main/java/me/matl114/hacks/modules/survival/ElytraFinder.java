package me.matl114.hacks.modules.survival;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.WorldTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.entity.HackUtilHelperD;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.versioned.api.VRender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class ElytraFinder extends BaseModule implements HackUtilHelperJ {
   Direction iS;
   Map<String, Set<BlockPos>> iQ;
   SurvivalSubHelperX iU;
   Map<Vec3i, Block> iT;
   public final ModulePath iN = makePath(Configs.o, "travelling-control");
   public FlagRef autoPilot;
   public FlagRef render;
   public FlagRef enable;
   public final ModulePath iO = this.iN.add("elytra-finder");
   BlockPos iR;
   static HackUtilHelperD instance;

   @Override
   public void onEnableModule() {
      super.onEnableModule();
      if (!checkNull()) {
         WorldTasks.restartWorldScanner();
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.aY(), this::mB);
      this.registerListener(Listener.ba(), this::mD);
      this.registerListener(Listener.bb().c(Blocks.DRAGON_WALL_HEAD), this::mE);
      this.registerListener(Listener.bc(), this::onDragonHeadChunkLoad);
      this.registerListener(RenderListener.q(), this::B);
   }

   public void bl(Event<Void> event) {
   }

   public void mD(Event<List<BiPredicate<BlockPos, BlockState>>> event) {
      if (this.shouldLocateDragonHead()) {
         ((List<BiPredicate<BlockPos, BlockState>>)event.e()).add((s, b) -> b.getBlock() == Blocks.DRAGON_WALL_HEAD);
      }
   }

   public void updateStore(String json) {
      this.iQ = new LinkedHashMap<>();
      JsonObject var2 = JsonParser.parseString(json).getAsJsonObject();

      for (Entry var4 : var2.entrySet()) {
         String var5 = (String)var4.getKey();
         JsonArray var6 = ((JsonElement)var4.getValue()).getAsJsonArray();
         LinkedHashSet var7 = new LinkedHashSet();

         for (JsonElement var9 : var6) {
            long var10 = var9.getAsLong();
            BlockPos var12 = BlockPos.fromLong(var10);
            var7.add(var12);
         }

         this.iQ.put(var5, var7);
      }
   }

   public void onLocateShipStructure(BlockState state, BlockPos pos) {
      if (mc.world.getBlockState(pos) == state) {
         Direction var3 = (Direction)state.get(WallSkullBlock.FACING);
         Debug.b("Locate Head");
         Direction var4 = var3.getOpposite();

         for (Entry var6 : this.iT.entrySet()) {
            Vec3i var7 = this.rotateOffset(var4, (Vec3i)var6.getKey());
            BlockPos var8 = pos.add(var7);
            RenderTasks.drawBox(Box.from(new BlockBox(var8)), 200, Color.MAGENTA);
            BlockState var9 = mc.world.getBlockState(var8);
            if (var9.getBlock() != var6.getValue()) {
               return;
            }
         }

         Debug.b("Locate EndShip");
         this.iR = pos;
         this.iS = var4;
      }
   }

   private Vec3i rotateOffset(Direction direction, Vec3i offset) {
      switch (direction) {
         case NORTH:
            return offset;
         case EAST:
            return new Vec3i(-offset.getZ(), offset.getY(), offset.getX());
         case SOUTH:
            return new Vec3i(-offset.getX(), offset.getY(), -offset.getZ());
         case WEST:
            return new Vec3i(offset.getZ(), offset.getY(), -offset.getX());
         default:
            throw new IllegalArgumentException("Only horizontal directions supported");
      }
   }

   public void mB(Event<Boolean> event) {
      if (this.shouldLocateDragonHead()) {
         event.context(Boolean.TRUE);
      }
   }

   public boolean shouldLocateDragonHead() {
      return this.enable.get() && (this.iU == null || this.iU == SurvivalSubHelperX.Va || this.iU == SurvivalSubHelperX.Vb);
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      this.iU = null;
      this.iR = null;
      this.iS = null;
   }

   @Override
   public boolean postModify(Event<LegalMovementManager> movementManagerEvent, boolean enabledThisTick) {
      if (this.enable.get()) {
         ((LegalMovementManager)movementManagerEvent.b).c.d();
      }

      return true;
   }

   public void applyPreTickModify(Event<LegalMovementManager> movementManagerEvent) {
      if (this.enable.get()) {
         boolean var2 = false;
         if (this.iR != null && mc.player.getY() < this.iR.getY()) {
            var2 = true;
         } else if (this.iU != null && mc.player.getY() < 64.0) {
            var2 = true;
         }

         if (var2) {
            this.iU = SurvivalSubHelperX.Vf;
         } else {
            if (this.iU == SurvivalSubHelperX.Vf) {
               this.iU = null;
            }

            if (this.iR != null) {
               if (this.iU == null || this.iU == SurvivalSubHelperX.Va || this.iU == SurvivalSubHelperX.Vb) {
                  this.iU = SurvivalSubHelperX.Vc;
               }

               if (this.iU == SurvivalSubHelperX.Vc) {
                  if (mc.player.isFallFlying()) {
                     Vec3d var3 = this.iR.toCenterPos().add(0.0, 2.0, 0.0);
                     Vec3d var4 = mc.player.getPos();
                     Vec3d var5 = var3.subtract(var4);
                     if (MathUtils.isInBox(var5, 0.6)) {
                        this.iU = SurvivalSubHelperX.Vd;
                     } else {
                        double var6 = var5.horizontalLengthSquared();
                        EntityUtils.setEntityPitchSafe(mc.player, 0.0F);
                        if (var6 > 0.36) {
                           float var8 = EntityUtils.s(var5);
                           PlayerStateManager.nT(mc.player, var8);
                        } else {
                           PlayerStateManager.nT(mc.player, mc.player.getYaw() + 180.0F);
                        }
                     }
                  } else {
                     this.iU = null;
                  }
               }

               if (this.iU == SurvivalSubHelperX.Vd) {
                  BlockPos var11 = this.iR.offset(this.iS, 3);
                  Vec3d var12 = var11.toCenterPos();
                  Vec3d var13 = mc.player.getPos();
                  Vec3d var9 = var12.subtract(var13);
                  if (MathUtils.isInBox(var9, 0.6)) {
                     this.iU = SurvivalSubHelperX.Ve;
                     this.iR = null;
                  } else {
                     EntityUtils.setEntityPitchSafe(mc.player, 0.0F);
                     if (var9.horizontalLengthSquared() > 0.36) {
                        float var10 = EntityUtils.s(var9);
                        PlayerStateManager.nT(mc.player, var10);
                     } else {
                        PlayerStateManager.nT(mc.player, mc.player.getYaw() + 180.0F);
                     }
                  }
               }
            }
         }
      }
   }

   public void mE(Event<BlockState> scann) {
      if (this.shouldLocateDragonHead()) {
         this.mG((BlockState)scann.b, scann.getArgs(0));
      }
   }

   public void onDragonHeadChunkLoad(Event<Map<BlockPos, BlockState>> chunk) {
      if (this.shouldLocateDragonHead()) {
         for (Entry var3 : ((java.util.Set<Entry>)(((Map)chunk.b)).entrySet())) {
            if (((BlockState)var3.getValue()).getBlock() == Blocks.DRAGON_WALL_HEAD) {
               this.mG((BlockState)var3.getValue(), (BlockPos)var3.getKey());
               return;
            }
         }
      }
   }

   public ElytraFinder() {
      super("ElytraFinder");
      this.enable = this.flagBuilder(this.iO.add("enable")).build();
      this.autoPilot = this.flagBuilder(this.iO.add("auto-pilot")).build();
      this.render = this.flagBuilder(this.iO.add("render")).build();
      this.iQ = new LinkedHashMap<>();
      this.iT = new LinkedHashMap<>();
      this.iT.put(new Vec3i(0, 0, -1), Blocks.PURPUR_STAIRS);
      this.iT.put(new Vec3i(0, -1, 0), Blocks.AIR);
      this.iT.put(new Vec3i(0, 0, -2), Blocks.AIR);
      this.iT.put(new Vec3i(0, 0, -3), Blocks.AIR);
      this.iT.put(new Vec3i(0, -1, -2), Blocks.PURPUR_PILLAR);
      this.iT.put(new Vec3i(0, -1, -3), Blocks.PURPUR_PILLAR);
      this.iT.put(new Vec3i(0, 3, 0), Blocks.AIR);
      this.iU = null;
      if (instance == null) {
         instance = new HackUtilHelperD(this::cast);
         MovTasks.j.SJ(() -> instance);
      }

      instance.mN(this::cast);
      this.bindFlag(this.enable);
   }

   public void mG(BlockState state, BlockPos pos) {
      Set var3 = this.iQ.get(CommonUtils.getWorldName());
      if (var3 == null || !var3.contains(pos)) {
         Tasks.l(() -> this.onLocateShipStructure(state, pos), 0);
      }
   }

   public void B(Event<MatrixStack> event) {
      if (this.enable.get() && this.render.get()) {
         MatrixStack var2 = (MatrixStack)event.e();
         RenderUtils.startDrawVirtual(var2);

         try {
            if (this.iR != null) {
               Vec3d var3 = RenderUtils.getCameraPos();
               VRender.getInstance()
                  .h(
                     (operation, vertexConsumer) -> {
                        operation.a(
                           var2,
                           vertexConsumer,
                           this.iR.toCenterPos().add(RenderTasks.l).subtract(var3),
                           this.iR.toCenterPos().add(RenderTasks.n).subtract(var3),
                           ColorUtils.j(Color.MAGENTA.getRGB(), 255)
                        );
                        Vec3d var5 = this.iR.toCenterPos().add(0.0, 3.0, 0.0);
                        operation.a(
                           var2,
                           vertexConsumer,
                           var5.add(RenderTasks.l).subtract(var3),
                           var5.add(RenderTasks.n).subtract(var3),
                           ColorUtils.j(Color.MAGENTA.getRGB(), 255)
                        );
                     }
                  );
            }
         } finally {
            RenderUtils.stopDrawVirtual(var2);
         }
      }
   }
}
