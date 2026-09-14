package me.matl114.hooks;

import com.google.common.base.Preconditions;
import fi.dy.masa.litematica.config.Configs.Generic;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.util.EasyPlaceUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LitematicaHooks implements IHooks {
   public static LitematicaHooks instance;

   public BlockHitResult getEasyPlaceClickedPosition(BlockHitResult var1, BlockState var2, BlockState var3) { }

   public World getSchematicWorld() { }

   public boolean isEasyPlaceEnabled() { }

   public boolean isPositionWithinRange(BlockPos var1) { }

   public static LitematicaHooks getInstance() {
      if (instance == null) {
         try {
            instance = new LitematicaHooks.Impl();
         } catch (Throwable var1) {
            instance = new LitematicaHooks.Default();
         }
      }

      return instance;
   }

   public static class Default extends LitematicaHooks {
      @Override
      public boolean isEnabled() {
         return false;
      }

      @Override
      public BlockHitResult getEasyPlaceClickedPosition(BlockHitResult blockHitResult, BlockState blockState, BlockState blockState2) {
         return null;
      }

      @Override
      public World getSchematicWorld() {
         return null;
      }

      @Override
      public boolean isEasyPlaceEnabled() {
         return false;
      }

      @Override
      public boolean isPositionWithinRange(BlockPos pos) {
         return false;
      }
   }

   public static class Impl extends LitematicaHooks {
      public static final MethodHandle easyPlaceHandle;

      public Impl() {
         Class<?> clazz = SchematicWorldHandler.class;
         Preconditions.checkNotNull(DataManager.getRenderLayerRange());
      }

      @Override
      public BlockHitResult getEasyPlaceClickedPosition(BlockHitResult blockHitResult, BlockState blockState, BlockState blockState2) {
         try {
            return (BlockHitResult)easyPlaceHandle.invokeExact((BlockHitResult)blockHitResult, (BlockState)blockState, (BlockState)blockState2);
         } catch (Throwable var5) {
            throw new RuntimeException(var5);
         }
      }

      @Override
      public World getSchematicWorld() {
         return SchematicWorldHandler.getSchematicWorld();
      }

      @Override
      public boolean isEasyPlaceEnabled() {
         return Generic.EASY_PLACE_MODE.getBooleanValue();
      }

      @Override
      public boolean isPositionWithinRange(BlockPos pos) {
         return DataManager.getRenderLayerRange().isPositionWithinRange(pos);
      }

      @Override
      public boolean isEnabled() {
         return true;
      }

      static {
         try {
            Lookup lookup = MethodHandles.privateLookupIn(EasyPlaceUtils.class, MethodHandles.lookup());
            Method method = EasyPlaceUtils.class.getDeclaredMethod("getClickPosition", BlockHitResult.class, BlockState.class, BlockState.class);
            method.setAccessible(true);
            easyPlaceHandle = lookup.unreflect(method);
         } catch (Throwable var2) {
            throw new RuntimeException(var2);
         }
      }
   }
}
