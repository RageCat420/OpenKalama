package me.matl114.hacks.modules.render;

import java.awt.Color;
import java.util.Map;
import java.util.Map.Entry;
import me.matl114.accessors.access.ChunkAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.EntryPrimitiveMap;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.config.TracingOption;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.chunk.Chunk;

public class ChestESP extends BaseModule {
   public final NBTRef<EntrySet<BlockEntityType<?>>> FA;
   public final NBTRef<EntryPrimitiveMap<BlockEntityType<?>, TextColor>> colorMap;
   public final RenderCollector<Box> hR;
   public final ModulePath Fv = makePath(Configs.i, "detect-block");
   public final NBTRef<TracingOption> espTraceOptions;
   public final RenderCollector<Box> FD;
   public final ModulePath Mf = this.Fv.add("chest-esp");
   public final FlagRef ae = this.flagBuilder(this.Mf.add("enable")).build();
   public final RenderCollector<Vec3d> Mi;

   public Box handleDoubleChestBox(BlockState state, BlockPos pos) {
      VoxelShape var3 = state.getOutlineShape(mc.world, pos);
      if (state.getBlock() instanceof ChestBlock) {
         ChestType var4 = (ChestType)state.get(ChestBlock.CHEST_TYPE);
         if (var4 != ChestType.SINGLE) {
            if (var4 == ChestType.RIGHT) {
               return null;
            }

            Direction var5 = ChestBlock.getFacing(state);
            BlockPos var6 = pos.offset(var5);
            BlockState var7 = mc.world.getBlockState(var6);
            VoxelShape var8 = var7.getOutlineShape(mc.world, var6);
            if (!var8.isEmpty()) {
               Box var9 = var8.getBoundingBox().offset(Vec3d.of(var5.getVector()));
               if (!var3.isEmpty()) {
                  Box var10 = var3.getBoundingBox();
                  return var10.union(var9);
               }

               return var9;
            }
         }
      }

      return var3.isEmpty() ? null : var3.getBoundingBox();
   }

   public void dispatchBlockEntityRender(BlockEntity blockEntity, BlockPos blockPos) {
      TextColor var3 = this.colorMap.get().uJ(blockEntity.getType());
      if (var3 != null) {
         TracingOption var4 = this.espTraceOptions.get();
         if (var4.box()) {
            BlockState var5 = blockEntity.getCachedState();
            Box var6 = this.handleDoubleChestBox(var5, blockPos);
            if (var6 != null) {
               this.FD.submit(var6.offset(blockPos), ColorUtils.withAlphaInt(var3.getRgb(), 0.25F));
               this.hR.submit(var6.offset(blockPos), ColorUtils.withAlphaInt(var3.getRgb(), 0.5F));
            }
         }

         if (var4.line()) {
            this.Mi.submit(blockPos.toCenterPos(), ColorUtils.withAlphaInt(var3.getRgb(), 1.0F));
         }
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(RenderListener.z(), this::KA);
      this.registerListener(Listener.U(), this::onSwapRenderContent);
      this.registerListener(RenderListener.q(), this::B);
   }

   public void KA(Event<BlockEntity> blockEntityEvent) {
   }

   public ChestESP() {
      super("ChestESP");
      this.FA = this.builder(this.Mf.add("enable-types"), EntrySet.<BlockEntityType<?>>parameter())
         .defaultValue(new EntrySet<BlockEntityType<?>>(new Regex("^(.*chest|barrel|.*box)$"), Registries.BLOCK_ENTITY_TYPE))
         .build();
      this.espTraceOptions = this.builder(this.Mf.add("esp-trace-options"), TracingOption.class).defaultValue(new TracingOption(true, false)).build();
      this.colorMap = this.builder(this.Mf.add("color-map"), EntryPrimitiveMap.uA())
         .defaultValue(
            new EntryPrimitiveMap<>(
               Registries.BLOCK_ENTITY_TYPE,
               NBTTypes.h,
               Map.of(
                  BlockEntityType.CHEST,
                  ColorUtils.color(Formatting.GREEN),
                  BlockEntityType.BARREL,
                  ColorUtils.color(Formatting.GREEN),
                  BlockEntityType.SHULKER_BOX,
                  ColorUtils.o(Color.MAGENTA),
                  BlockEntityType.TRAPPED_CHEST,
                  TextColor.fromRgb(16744448),
                  BlockEntityType.FURNACE,
                  ColorUtils.color(Formatting.WHITE),
                  BlockEntityType.ENDER_CHEST,
                  ColorUtils.o(Color.CYAN),
                  BlockEntityType.DROPPER,
                  ColorUtils.color(Formatting.WHITE),
                  BlockEntityType.DISPENSER,
                  ColorUtils.color(Formatting.WHITE),
                  BlockEntityType.HOPPER,
                  ColorUtils.color(Formatting.AQUA)
               ),
               ColorUtils.color(Formatting.GREEN)
            )
         )
         .build();
      this.FD = RenderCollectors.createBoxCollector(false, true, false);
      this.hR = RenderCollectors.createBoxCollector(true, false, false);
      this.Mi = RenderCollectors.d();
      this.bindFlag(this.ae);
   }

   public void B(Event<MatrixStack> render) {
      if (this.ae.get()) {
         MatrixStack var2 = (MatrixStack)render.e();
         RenderUtils.startDrawVirtual(var2);

         try {
            this.FD.a(var2);
            this.hR.a(var2);
            this.Mi.a(var2);
         } finally {
            RenderUtils.stopDrawVirtual(var2);
         }
      }
   }

   public void onSwapRenderContent(Event<ClientPlayerEntity> clientPlayerEntityEvent) {
      if (!checkNull()) {
         this.hR.clear();
         this.Mi.clear();
         this.FD.clear();
         if (this.ae.get()) {
            for (Chunk var3 : CommonUtils.chunks(false)) {
               for (Entry var5 : ChunkAccess.of(var3).blockEntityEntries()) {
                  if (this.FA.get().test(((BlockEntity)var5.getValue()).getType())) {
                     this.dispatchBlockEntityRender((BlockEntity)var5.getValue(), (BlockPos)var5.getKey());
                  }
               }
            }
         }
      }
   }
}
