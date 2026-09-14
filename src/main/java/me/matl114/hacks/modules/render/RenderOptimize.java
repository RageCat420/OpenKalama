package me.matl114.hacks.modules.render;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.matl114.accessors.access.ChunkAccess;
import me.matl114.accessors.interfaces.MetadataHolder;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.containers.KalamaHelperHelperB;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.function.BooleanConsumer;

public class RenderOptimize extends BaseModule {
   private int tickCounter;
   public final NBTRef<EntrySet<ParticleType<?>>> xN;
   public final FlagRef optimizeEntityLabelRender;
   public final FlagRef optimizeItemTick;
   public final DoubleRef blockLabelRenderCullingDistance;
   public final ModulePath xB = makePath(Configs.i, "render-optimize");
   public final FlagRef optimizeCullingEnable;
   public volatile ConcurrentHashMap<Long, Boolean> xR;
   public final NBTRef<EntrySet<BlockEntityType<?>>> xM;
   public final DoubleRef optimizeCullingRadius;
   public final FlagRef optimizeParticleTick;
   public final DoubleRef itemCullingDistance;
   public final FlagRef optimizeBlockLabelRender;
   public final DoubleRef entityLabelRenderCullingDistance;
   public final FlagRef optimizeArmorStandTick;
   public final NBTRef<EntrySet<EntityType<?>>> xL;
   public final FlagRef optimizeCullingUseRaycast;
   public final KeyBindRef optimizeCullingEnableHotkey;
   ExecutorService xQ;
   public static final String KEY_RENDER_CONTROL = "kalama:render_optimize/render_controller";

   public void canChunkBeSeen(int chunkX, int chunkZ, Vec3d cameraPos, Vec3d cameraLook) {
   }

   public boolean raycastFullBlockAsync(Box box, Vec3d cameraPos, RenderOptimize$RenderController controller) {
      boolean var4 = box.getMaxPos().subtract(box.getMinPos()).lengthSquared() < 0.01;
      Vec3d[] var5 = var4
         ? new Vec3d[]{box.getCenter()}
         : new Vec3d[]{
            new Vec3d(box.minX, box.minY, box.minZ),
            new Vec3d(box.maxX, box.minY, box.minZ),
            new Vec3d(box.minX, box.maxY, box.minZ),
            new Vec3d(box.maxX, box.maxY, box.minZ),
            new Vec3d(box.minX, box.minY, box.maxZ),
            new Vec3d(box.maxX, box.minY, box.maxZ),
            new Vec3d(box.minX, box.maxY, box.maxZ),
            new Vec3d(box.maxX, box.maxY, box.maxZ)
         };
      ConcurrentHashMap var6 = this.xR;
      ClientWorld var7 = mc.world;
      if (var7 == null) {
         return false;
      } else {
         for (Vec3d var11 : var5) {
            Iterator var12 = RaycastUtils.t(var11, cameraPos);
            int var13 = 0;
            long var14 = BlockPos.ofFloored(var11).asLong();

            while (var12.hasNext()) {
               BlockPos var16 = (BlockPos)var12.next();
               long var17 = var16.asLong();
               if (var14 != var17) {
                  Boolean var19 = (Boolean)var6.get(var17);
                  boolean var21;
                  if (var19 == null) {
                     BlockState var20 = var7.getBlockState(var16);
                     var21 = !var20.isAir() && var20.isOpaque() && var20.isFullCube(var7, var16);
                     var6.put(var17, var21 ? Boolean.TRUE : Boolean.FALSE);
                  } else {
                     var21 = var19;
                  }

                  if (var21) {
                     if (++var13 >= 1) {
                        break;
                     }
                  }
               }
            }

            if (var13 < 1) {
               return false;
            }
         }

         return true;
      }
   }

   public void onEntityTick(Event<Entity> event) {
      Entity var2 = (Entity)event.e();
      if (var2 instanceof ItemEntity var3 && this.optimizeItemTick.get()) {
         boolean var4 = var3.isInFluid();
         boolean var5 = !var3.isOnGround() && var3.getFinalGravity() > 0.0;
         if (!var4 && !var5) {
            event.cancel();
            return;
         }

         ClientPlayerEntity var6 = MinecraftClient.getInstance().player;
         if (var6 != null && var6.getPos().squaredDistanceTo(var3.getPos()) > MathUtils.a(this.itemCullingDistance.get())) {
            event.cancel();
            return;
         }
      } else if (var2 instanceof ArmorStandEntity var7 && this.optimizeArmorStandTick.get()) {
         event.cancel();
         return;
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.aO(), this::onEntityTick);
      this.registerListener(Listener.aO(), this::onEntityCullingTick);
      this.registerListener(Listener.aO(), this::onEntityLabelShowTick);
      this.registerListener(Listener.aV(), this::onBlockEntityTick);
      this.registerListener(Listener.V(), this::vb);
      this.registerListener(Listener.U(), this::onBlockEntityCullingTick);
      this.registerListener(RenderListener.y(), this::Kw);
      this.registerListener(RenderListener.z(), this::onBlockEntityRender);
   }

   public RenderOptimize() {
      super("Optimize");
      this.optimizeItemTick = this.flagBuilder(this.xB.add("optimize-item-tick")).build();
      this.itemCullingDistance = this.builder(this.xB.add("item-culling-distance"), DoubleRef.TYPE).defaultValue(40.0).build();
      this.optimizeParticleTick = this.flagBuilder(this.xB.add("optimize-particle-tick")).build();
      this.optimizeArmorStandTick = this.flagBuilder(this.xB.add("optimize-armor-stand-tick")).build();
      this.optimizeEntityLabelRender = this.flagBuilder(this.xB.add("optimize-entity-label-render")).build();
      this.entityLabelRenderCullingDistance = this.builder(this.xB.add("entity-label-render-culling-distance"), DoubleRef.TYPE).defaultValue(64.0).build();
      this.optimizeBlockLabelRender = this.flagBuilder(this.xB.add("optimize-block-label-render")).build();
      this.blockLabelRenderCullingDistance = this.builder(this.xB.add("block-label-render-culling-distance"), DoubleRef.TYPE).defaultValue(20.0).build();
      this.optimizeCullingEnable = this.flagBuilder(this.xB.add("optimize-culling-enable")).build();
      this.optimizeCullingEnableHotkey = this.moduleEntry(
            this.xB.add("optimize-culling-enable-hotkey"), new MultiKeyBind(), this.xB.add("optimize-culling-enable")
         )
         .build();
      this.xL = this.builder(this.xB.add("optimize-culling-entity-types"), EntrySet.<EntityType<?>>parameter())
         .defaultValue(new EntrySet<EntityType<?>>(new Regex("^(item.*)$"), Registries.ENTITY_TYPE))
         .build();
      this.xM = this.builder(this.xB.add("optimize-culling-block-entity-types"), EntrySet.<BlockEntityType<?>>parameter())
         .defaultValue(new EntrySet<BlockEntityType<?>>(new Regex("^((.*sign)|barrel|skull|(.*chest)|enchanting_table)$"), Registries.BLOCK_ENTITY_TYPE))
         .build();
      this.xN = this.builder(this.xB.add("optimize-culling-block-entity-types"), EntrySet.<ParticleType<?>>parameter())
         .defaultValue(new EntrySet<ParticleType<?>>(new Regex("^(.*)$"), Registries.PARTICLE_TYPE))
         .build();
      this.optimizeCullingRadius = this.builder(this.xB.add("optimize-culling-radius"), DoubleRef.TYPE).defaultValue(64.0).build();
      this.optimizeCullingUseRaycast = this.flagBuilder(this.xB.add("optimize-culling-use-raycast")).build();
      this.xR = new ConcurrentHashMap<>();
      this.tickCounter = 0;
   }

   public boolean shouldCancelShowDisplayName(Entity entity) {
      return this.optimizeEntityLabelRender.get()
            && entity instanceof MetadataHolder var2
            && !var2.isMetaEmpty()
            && var2.getMetadata().b(this, "kalama:render_optimize/render_controller") instanceof RenderOptimize$RenderController var4
         ? var4.a
         : false;
   }

   @Override
   public void onCreate() {
      super.onCreate();
      this.xQ = Executors.newFixedThreadPool(4);
   }

   public void onBlockEntityCullingTick(Event<ClientPlayerEntity> event) {
      if (mc.world != null && this.optimizeCullingEnable.get()) {
         double var2 = this.optimizeCullingRadius.get();
         int var4 = (int)((this.optimizeCullingRadius.get() + 1.0) / 16.0 + 1.0);
         Vec3d var5 = RenderUtils.getCameraPos();
         BlockPos var6 = BlockPos.ofFloored(var5);
         int var7 = var6.getX() >> 4;
         int var8 = var6.getZ() >> 4;

         for (Chunk var10 : CommonUtils.chunks(false)) {
            ChunkPos var11 = var10.getPos();
            if (Math.abs(var11.x - var7) <= var4 && Math.abs(var11.z - var8) <= var4) {
               for (Entry var13 : ChunkAccess.of(var10).blockEntityEntries()) {
                  BlockPos var14 = (BlockPos)var13.getKey();
                  Box var15 = Box.from(Vec3d.of(var14));
                  if (var15.squaredMagnitude(var5) <= MathUtils.a(var2)) {
                     BlockEntity var16 = (BlockEntity)var13.getValue();
                     if (var16 instanceof MetadataHolder var17) {
                        BlockEntityType var18 = var16.getType();
                        if (this.xM.get().test(var18)) {
                           KalamaHelperHelperB var19 = var17.getMetadata();
                           RenderOptimize$RenderController var20 = var19.c(
                              this, "kalama:render_optimize/render_controller", RenderOptimize$RenderController::new
                           );
                           if (var15.squaredMagnitude(var5) < 16.0) {
                              var20.c = false;
                           } else {
                              Vec3d var21 = var5.subtract(var14.toCenterPos());
                              Vec3d var22 = RenderUtils.getCameraLookVec(0.0F);
                              if (var22.dotProduct(var21) > 0.0) {
                                 var20.c = true;
                              } else if (this.optimizeCullingUseRaycast.get()) {
                                 this.delayScheduleRaycast(var15, var20, val -> var20.c = val);
                              } else {
                                 var20.c = false;
                              }
                           }
                        } else if (!var17.isMetaEmpty()) {
                           KalamaHelperHelperB var23 = var17.getMetadata();
                           RenderOptimize$RenderController var24 = var23.b(this, "kalama:render_optimize/render_controller");
                           if (var24 != null) {
                              var24.c = false;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void vb(Event<ClientPlayerEntity> event) {
      if (this.optimizeCullingUseRaycast.get()) {
         this.tickCounter++;
         if (this.tickCounter > 1) {
            this.tickCounter = 0;
            this.xR = new ConcurrentHashMap<>();
         }
      }
   }

   public void onBlockEntityRender(Event<BlockEntity> event) {
      if (!event.d() && this.optimizeCullingEnable.get()) {
         BlockEntity var2 = (BlockEntity)event.e();
         BlockEntityType var3 = var2.getType();
         if (this.xM.get().test(var3)) {
            Box var4 = Box.from(Vec3d.of(var2.getPos()));
            double var5 = var4.squaredMagnitude(RenderUtils.getCameraPos());
            if (var5 < 16.0) {
               return;
            }

            if (var5 > MathUtils.a(this.optimizeCullingRadius.get())) {
               event.cancel();
               return;
            }

            if (var2 instanceof MetadataHolder var7
               && !var7.isMetaEmpty()
               && var7.getMetadata().b(this, "kalama:render_optimize/render_controller") instanceof RenderOptimize$RenderController var9
               && var9.c) {
               event.cancel();
            }
         }
      }
   }

   @Override
   public void onRemove() {
      super.onRemove();
      if (this.xQ != null && !this.xQ.isShutdown()) {
         this.xQ.shutdown();
      }
   }

   public void onEntityLabelShowTick(Event<Entity> event) {
      if (this.optimizeEntityLabelRender.get()) {
         Entity var2 = (Entity)event.e();
         if (var2 instanceof PlayerEntity) {
            return;
         }

         if (var2.hasCustomName() && var2 instanceof MetadataHolder var3) {
            KalamaHelperHelperB var4 = var3.getMetadata();
            RenderOptimize$RenderController var5 = var4.c(this, "kalama:render_optimize/render_controller", RenderOptimize$RenderController::new);
            Vec3d var6 = RenderUtils.getCameraPos();
            if (var2.getPos().squaredDistanceTo(var6) > MathUtils.a(this.entityLabelRenderCullingDistance.get())) {
               var5.a = true;
            } else {
               Vec3d var7 = var6.subtract(var2.getPos());
               if (var7.dotProduct(RenderUtils.getCameraLookVec(0.0F)) > 0.0) {
                  var5.a = true;
               } else {
                  var5.a = false;
               }
            }
         }
      }
   }

   public void delayScheduleRaycast(Box box, RenderOptimize$RenderController controller, BooleanConsumer consumer) {
      if (this.xQ == null || this.xQ.isShutdown()) {
         consumer.accept(false);
      } else if (mc.getCameraEntity() != null && mc.getCameraEntity().isSpectator()) {
         consumer.accept(false);
      } else {
         if (controller.e + 2 > Tasks.b()) {
            consumer.accept(controller.d);
            return;
         }

         Vec3d var4 = RenderUtils.getCameraPos();
         controller.e = Tasks.b();
         CompletableFuture.runAsync(() -> {
            controller.d = this.raycastFullBlockAsync(box, var4, controller);
            controller.e = Tasks.b();
            consumer.accept(controller.d);
         }, this.xQ);
      }
   }

   public void onEntityCullingTick(Event<Entity> event) {
      Entity var2 = (Entity)event.e();
      if (var2 instanceof MetadataHolder var3) {
         Vec3d var4 = RenderUtils.getCameraPos();
         EntityType var5 = var2.getType();
         if (this.optimizeCullingEnable.get()) {
            if (this.xL.get().test(var5)) {
               KalamaHelperHelperB var6 = var3.getMetadata();
               RenderOptimize$RenderController var7 = var6.c(this, "kalama:render_optimize/render_controller", RenderOptimize$RenderController::new);
               Box var8 = var2.getBoundingBox();
               if (var8.squaredMagnitude(var4) < 16.0) {
                  var7.c = false;
               } else if (var8.squaredMagnitude(var4) > MathUtils.a(this.optimizeCullingRadius.get())) {
                  var7.c = true;
               } else {
                  Vec3d var9 = var4.subtract(var2.getPos());
                  Vec3d var10 = RenderUtils.getCameraLookVec(0.0F);
                  if (var10.dotProduct(var9) > 0.0) {
                     var7.c = true;
                  } else if (this.optimizeCullingUseRaycast.get()) {
                     this.delayScheduleRaycast(var8, var7, val -> var7.c = val);
                  } else {
                     var7.c = false;
                  }
               }
            } else if (!var3.isMetaEmpty()) {
               KalamaHelperHelperB var11 = var3.getMetadata();
               RenderOptimize$RenderController var12 = var11.b(this, "kalama:render_optimize/render_controller");
               if (var12 != null) {
                  var12.c = false;
               }
            }
         }
      }
   }

   public void Kw(Event<Entity> event) {
      if (!event.d() && this.optimizeCullingEnable.get()) {
         Entity var2 = (Entity)event.e();
         if (var2 instanceof MetadataHolder var3
            && !var3.isMetaEmpty()
            && var3.getMetadata().b(this, "kalama:render_optimize/render_controller") instanceof RenderOptimize$RenderController var5
            && var5.c) {
            event.cancel();
         }
      }
   }

   public void onBlockEntityTick(Event<BlockEntityTickInvoker> event) {
      BlockEntityTickInvoker var2 = (BlockEntityTickInvoker)event.e();
      BlockPos var3 = var2.getPos();
      BlockEntity var4 = mc.world.getBlockEntity(var3);
      if (var4 instanceof MetadataHolder var5 && var4 instanceof SignBlockEntity) {
         KalamaHelperHelperB var6 = var5.getMetadata();
         RenderOptimize$RenderController var7 = var6.c(this, "kalama:render_optimize/render_controller", RenderOptimize$RenderController::new);
         BlockState var8 = mc.world.getBlockState(var2.getPos());
         if (this.optimizeBlockLabelRender.get()) {
            if (var8.getBlock() instanceof AbstractSignBlock var10) {
               Vec3d var26 = RenderUtils.getCameraPos();
               if (var3.getSquaredDistance(var26) > MathUtils.a(this.blockLabelRenderCullingDistance.get())) {
                  var7.b = var7.a = true;
               } else {
                  float var11 = var10.getRotationDegrees(var8);
                  float var12 = var11 * (float) (Math.PI / 180.0);
                  double var13 = -MathHelper.sin(var12);
                  double var15 = MathHelper.cos(var12);
                  Vec3d var17 = new Vec3d(var13, 0.0, var15).normalize();
                  Vec3d var18 = Vec3d.of(var3).add(var10.getCenter(var8));
                  Vec3d var19 = var26.subtract(var18);
                  Vec3d var20 = RenderUtils.getCameraLookVec(0.0F);
                  boolean var21 = true;
                  boolean var22 = true;
                  if (var20.dotProduct(var19) > 0.0) {
                     var21 = false;
                     var22 = false;
                  } else if (var19.dotProduct(var17) > 0.0) {
                     var22 = false;
                  } else {
                     var21 = false;
                  }

                  if ((var22 || var21) && this.optimizeCullingUseRaycast.get()) {
                     boolean var23 = var22;
                     boolean var24 = var21;
                     Box var25 = Box.from(Vec3d.of(var3));
                     this.delayScheduleRaycast(var25, var7, val -> {
                        if (!val) {
                           var7.b = !var23;
                           var7.a = !var24;
                        } else {
                           var7.a = true;
                           var7.b = true;
                        }
                     });
                  } else {
                     var7.b = !var22;
                     var7.a = !var21;
                  }
               }
            } else {
               var7.b = false;
               var7.a = false;
            }
         } else {
            var7.a = false;
            var7.b = false;
         }
      }
   }
}
