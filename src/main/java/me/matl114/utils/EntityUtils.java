package me.matl114.utils;

import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.utils.entity.PlayerInputUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.TridentItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext.FluidHandling;
import org.joml.Vector2d;
import org.spongepowered.include.com.google.common.collect.BiMap;
import org.spongepowered.include.com.google.common.collect.HashBiMap;

public class EntityUtils {
   private static final BiMap<Item, EntityType<?>> b = HashBiMap.create();
   private static final MinecraftClient a = MinecraftClient.getInstance();
   public static final double SQRT_SPEED;

   public static void smoothPlayerInputState() {
   }

   public static double sqrtSpeed(Vec3d vec) {
      return Math.sqrt(vec.x * vec.x + vec.z * vec.z);
   }

   public static float l(float yaw) {
      return yaw > -1.0E-5 && yaw < 360.00001 ? yaw : (yaw % 360.0F + 720.0F + 180.0F) % 360.0F - 180.0F;
   }

   public static Vec3d lookCoordToAbsolutePos(Entity source, double x, double y, double z) {
      Vec2f var7 = source.getRotationClient();
      Vec3d var8 = source.getPos();
      float var9 = MathHelper.cos((var7.y + 90.0F) * (float) (Math.PI / 180.0));
      float var10 = MathHelper.sin((var7.y + 90.0F) * (float) (Math.PI / 180.0));
      float var11 = MathHelper.cos(-var7.x * (float) (Math.PI / 180.0));
      float var12 = MathHelper.sin(-var7.x * (float) (Math.PI / 180.0));
      float var13 = MathHelper.cos((-var7.x + 90.0F) * (float) (Math.PI / 180.0));
      float var14 = MathHelper.sin((-var7.x + 90.0F) * (float) (Math.PI / 180.0));
      Vec3d var15 = new Vec3d(var9 * var11, var12, var10 * var11);
      Vec3d var16 = new Vec3d(var9 * var13, var14, var10 * var13);
      Vec3d var17 = var15.crossProduct(var16).multiply(-1.0);
      double var18 = var15.x * z + var16.x * y + var17.x * x;
      double var20 = var15.y * z + var16.y * y + var17.y * x;
      double var22 = var15.z * z + var16.z * y + var17.z * x;
      return new Vec3d(var8.x + var18, var8.y + var20, var8.z + var22);
   }

   public static int y(float yaw) {
      float var1 = yaw % 360.0F;
      if (var1 < 0.0F) {
         var1 += 360.0F;
      }

      float var2 = 0.001F;
      if (!(Math.abs(var1 - 90.0F) < 0.001F) && !(Math.abs(var1 - 270.0F) < 0.001F)) {
         return var1 > 90.0F && var1 < 270.0F ? -1 : 1;
      } else {
         return 0;
      }
   }

   public static Vec3d withStrafe(Vec3d self, double speed, double strength, PlayerInputUtils$Input input, float yaw) {
      if (input != null && !input.ru()) {
         return new Vec3d(0.0, self.y, 0.0);
      } else {
         double var7 = self.x * (1.0 - strength);
         double var9 = self.z * (1.0 - strength);
         double var11 = speed * strength;
         double var13 = Math.toRadians(yaw);
         double var15 = -Math.sin(var13) * var11 + var7;
         double var17 = Math.cos(var13) * var11 + var9;
         return new Vec3d(var15, self.y, var17);
      }
   }

   public static float m(float newPitch) {
      return newPitch < 90.00001 && newPitch > -90.00001 ? newPitch : (newPitch % 180.0F + 720.0F + 90.0F) % 180.0F - 90.0F;
   }

   public static Direction yawToHorizontalDirection(float yaw) {
      float var1 = yaw + 45.0F;
      float var2 = var1 % 360.0F;
      if (var2 < 0.0F) {
         var2 += 360.0F;
      }

      int var3 = (int)(var2 / 90.0F);
      switch (var3) {
         case 0:
            return Direction.SOUTH;
         case 1:
            return Direction.WEST;
         case 2:
            return Direction.NORTH;
         default:
            return Direction.EAST;
      }
   }

   public static float getMovementDirectionOfInput(float facingYaw, PlayerInputUtils$Input input) {
      boolean var2 = input.rE() && !input.rF();
      boolean var3 = input.rF() && !input.rE();
      boolean var4 = input.rG() && !input.rH();
      boolean var5 = input.rH() && !input.rG();
      float var6 = facingYaw;
      float var7 = 1.0F;
      if (var3) {
         var6 = facingYaw + 180.0F;
         var7 = -0.5F;
      } else if (var2) {
         var7 = 0.5F;
      }

      if (var4) {
         var6 -= 90.0F * var7;
      }

      if (var5) {
         var6 += 90.0F * var7;
      }

      return MathHelper.wrapDegrees(var6);
   }

   public static Text getEntityDisplayable(Entity target) {
      return (Text)(target instanceof PlayerEntity var1 ? Text.literal(var1.getNameForScoreboard()) : target.getDisplayName());
   }

   public static Direction pitchYawToDirection(Vec2f pitchYaw) {
      float var1 = pitchYaw.x;
      float var2 = pitchYaw.y;
      double var3 = Math.toRadians(var1);
      double var5 = Math.toRadians(var2);
      double var7 = Math.cos(var3);
      double var9 = Math.sin(var3);
      double var11 = Math.cos(var5);
      double var13 = Math.sin(var5);
      double var15 = -var7 * var13;
      double var17 = -var9;
      double var19 = var7 * var11;
      double var21 = var15 * var15;
      double var23 = var17 * var17;
      double var25 = var19 * var19;
      if (var21 > var23 && var21 > var25) {
         return var15 > 0.0 ? Direction.EAST : Direction.WEST;
      } else if (var23 > var21 && var23 > var25) {
         return var17 > 0.0 ? Direction.UP : Direction.DOWN;
      } else {
         return var19 > 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   public static void setEntityPitchSafe(Entity entity, float newPitch) {
      entity.setPitch(k(newPitch));
   }

   public static Vec2f q(Vec3d vec) {
      return new Vec2f(rotationToPitch(vec), s(vec));
   }

   public static int x(float yaw) {
      float var1 = yaw % 360.0F;
      if (var1 < 0.0F) {
         var1 += 360.0F;
      }

      float var2 = 0.001F;
      if (!(var1 < 0.001F) && !(Math.abs(var1 - 180.0F) < 0.001F) && !(Math.abs(var1 - 360.0F) < 0.001F)) {
         return var1 > 0.0F && var1 < 180.0F ? -1 : 1;
      } else {
         return 0;
      }
   }

   public static Stream<String> getWorldPlayerNames(boolean containSelf) {
      return a.world.getPlayers().stream().filter(i -> containSelf || i != a.player).map(PlayerEntity::getNameForScoreboard);
   }

   public static float rotationToYaw(Direction direction) {
      switch (direction) {
         case NORTH:
            return 180.0F;
         case SOUTH:
            return 0.0F;
         case WEST:
            return 90.0F;
         case EAST:
            return -90.0F;
         default:
            return 0.0F;
      }
   }

   public static EntityType<?> getSpawnerEntityType(NbtCompound spawnerCompound) {
      return spawnerCompound == null ? null : (EntityType)Registries.ENTITY_TYPE.getOrEmpty(getSpawnedEntityId(spawnerCompound, "SpawnData")).orElse(null);
   }

   private static float getDirectionalMovementSpeedMultiplier(Vec2f vec) {
      float var1 = Math.abs(vec.x);
      float var2 = Math.abs(vec.y);
      float var3 = var2 > var1 ? var1 / var2 : var2 / var1;
      return MathHelper.sqrt(1.0F + MathHelper.square(var3));
   }

   public static Vec3d simulateTravelInFluidVelocity(Vec3d velocity, boolean lastInWater, boolean lastInLava, boolean hasGravity) {
      if (lastInWater) {
         return simulateTravelInWaterVelocity(velocity, hasGravity);
      } else {
         return lastInLava ? simulateTravelInLavaVelocity(velocity, hasGravity) : velocity;
      }
   }

   public static float rotationToPitch(Vec3d vec) {
      return (float)Math.toDegrees(Math.asin(-vec.y));
   }

   public static Vec3d lookCoordToPos(float pitch, float yaw, double x, double y, double z) {
      Vec2f var8 = new Vec2f(pitch, yaw);
      float var9 = MathHelper.cos((var8.y + 90.0F) * (float) (Math.PI / 180.0));
      float var10 = MathHelper.sin((var8.y + 90.0F) * (float) (Math.PI / 180.0));
      float var11 = MathHelper.cos(-var8.x * (float) (Math.PI / 180.0));
      float var12 = MathHelper.sin(-var8.x * (float) (Math.PI / 180.0));
      float var13 = MathHelper.cos((-var8.x + 90.0F) * (float) (Math.PI / 180.0));
      float var14 = MathHelper.sin((-var8.x + 90.0F) * (float) (Math.PI / 180.0));
      Vec3d var15 = new Vec3d(var9 * var11, var12, var10 * var11);
      Vec3d var16 = new Vec3d(var9 * var13, var14, var10 * var13);
      Vec3d var17 = var15.crossProduct(var16).multiply(-1.0);
      double var18 = var15.x * z + var16.x * y + var17.x * x;
      double var20 = var15.y * z + var16.y * y + var17.y * x;
      double var22 = var15.z * z + var16.z * y + var17.z * x;
      return new Vec3d(var18, var20, var22);
   }

   public static Vector2d getEntityLookXZ(Entity entity) {
      float var1 = entity.getYaw();
      float var2 = entity.getPitch();
      float var3 = MathHelper.cos(-var1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var4 = MathHelper.sin(-var1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var5 = -MathHelper.cos(-var2 * (float) (Math.PI / 180.0));
      return new Vector2d(var4 * var5, var3 * var5);
   }

   public static double A(Item item) {
      if (item instanceof RangedWeaponItem) {
         return 0.05;
      } else if (item instanceof ThrowablePotionItem) {
         return 0.4;
      } else if (item instanceof FishingRodItem) {
         return 0.15;
      } else {
         return item instanceof TridentItem ? 0.015 : 0.03;
      }
   }

   public static boolean isEntityValid(@Nullable Entity entity) {
      return entity != null
         && entity.isAlive()
         && !entity.isRemoved()
         && a.world != null
         && a.world == entity.getEntityWorld()
         && a.world.getEntityLookup().get(entity.getUuid()) == entity;
   }

   public static double getEffectiveGravity(ClientPlayerEntity player) {
      boolean var1 = player.getVelocity().y <= 0.0;
      return var1 && player.hasStatusEffect(StatusEffects.SLOW_FALLING) ? Math.min(player.getFinalGravity(), 0.01) : player.getFinalGravity();
   }

   private static Vec3d simulateApplyFluidMovingSpeed(double gravity, boolean falling, Vec3d velocity, boolean hasGravity, boolean isSprinting) {
      if (gravity != 0.0 && hasGravity && !isSprinting) {
         double var6;
         if (falling && Math.abs(velocity.y - 0.005) >= 0.003 && Math.abs(velocity.y - gravity / 16.0) < 0.003) {
            var6 = -0.003;
         } else {
            var6 = velocity.y - gravity / 16.0;
         }

         return new Vec3d(velocity.x, var6, velocity.z);
      } else {
         return velocity;
      }
   }

   public static float k(float newPitch) {
      return m(newPitch);
   }

   public static Vec2f directionToPitchYaw(Direction direction) {
      switch (direction) {
         case DOWN:
            return new Vec2f(89.9F, 0.0F);
         case UP:
            return new Vec2f(-89.9F, 0.0F);
         case NORTH:
            return new Vec2f(0.0F, 180.0F);
         case SOUTH:
            return new Vec2f(0.0F, 0.0F);
         case WEST:
            return new Vec2f(0.0F, 90.0F);
         case EAST:
            return new Vec2f(0.0F, -90.0F);
         default:
            throw new IllegalArgumentException("Unknown direction: " + direction);
      }
   }

   public static boolean isRotationDifferent(float lastPitch, float pitch, float lastYaw, float yaw) {
      return Math.abs(pitch - lastPitch) > 0.01 || Math.abs(j(lastYaw, yaw)) > 0.01;
   }

   public static Identifier getSpawnedEntityId(NbtCompound nbt, String spawnDataKey) {
      if (nbt.contains(spawnDataKey)) {
         if (nbt.get(spawnDataKey) instanceof NbtCompound var3 && var3.get("entity") instanceof NbtCompound var6 && var6.get("id") instanceof NbtString var7) {
            String var8 = var7.asString();
            if (var8 != null && !var8.isEmpty()) {
               return Identifier.tryParse(var8);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static Vec3d rotateVec(Vec3d vec, float pitch, float yaw) {
      Vec3d var3 = vec.normalize();
      double var4 = vec.length();
      Vec2f var6 = q(var3);
      Vec3d var7 = pitchYawToRotation(var6.x + pitch, var6.y + yaw);
      return var7.normalize().multiply(var4);
   }

   public static Vec2f applyMovementFactors(Entity entity, Vec2f vec2f) {
      if (vec2f.lengthSquared() == 0.0F) {
         return vec2f;
      } else if (entity instanceof ClientPlayerEntity var2) {
         vec2f = vec2f.multiply(0.98F);
         if (var2.isUsingItem() && !var2.hasVehicle()) {
            vec2f = vec2f.multiply(0.2F);
         }

         if (var2.shouldSlowDown()) {
            float var3 = (float)var2.getAttributeValue(EntityAttributes.PLAYER_SNEAKING_SPEED);
            vec2f = vec2f.multiply(var3);
         }

         float var8 = vec2f.length();
         vec2f = vec2f.multiply(1.0F / var8);
         float var4 = getDirectionalMovementSpeedMultiplier(vec2f);
         float var5 = Math.min(var8 * var4, 1.0F);
         return vec2f.multiply(var5);
      } else {
         return vec2f;
      }
   }

   public static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
      double var3 = movementInput.lengthSquared();
      if (var3 < 1.0E-7) {
         return Vec3d.ZERO;
      } else {
         Vec3d var5 = (var3 > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);
         float var6 = MathHelper.sin(yaw * (float) (Math.PI / 180.0));
         float var7 = MathHelper.cos(yaw * (float) (Math.PI / 180.0));
         return new Vec3d(var5.x * var7 - var5.z * var6, var5.y, var5.z * var7 + var5.x * var6);
      }
   }

   public static Item c(EntityType<?> entityType) {
      return (Item)b.inverse().getOrDefault(entityType, null);
   }

   public static Vec3d pitchYawToRotation(float pitch, float yaw) {
      float var2 = pitch * (float) (Math.PI / 180.0);
      float var3 = -yaw * (float) (Math.PI / 180.0);
      float var4 = MathHelper.cos(var3);
      float var5 = MathHelper.sin(var3);
      float var6 = MathHelper.cos(var2);
      float var7 = MathHelper.sin(var2);
      return new Vec3d(var5 * var6, -var7, var4 * var6);
   }

   public static EntityType<?> getStoredEntityType(ItemStack stack) {
      if (stack != null
         && stack.getItem() instanceof BlockItem var2
         && var2.getBlock() instanceof SpawnerBlock var3
         && ItemStackUtils.g(stack, DataComponentTypes.BLOCK_ENTITY_DATA)) {
         NbtComponent var5 = ItemStackUtils.getInPatch(stack, DataComponentTypes.BLOCK_ENTITY_DATA);
         return getSpawnerEntityType(var5.getNbt());
      } else {
         return null;
      }
   }

   public static float s(Vec3d vec) {
      return (float)Math.toDegrees(Math.atan2(-vec.x, vec.z));
   }

   public static float j(float oldYaw, float newYaw) {
      float var2 = newYaw - oldYaw;
      float var3 = (var2 % 360.0F + 720.0F + 180.0F) % 360.0F - 180.0F;
      if (oldYaw > 1000.0F && var3 > 179.0F) {
         var3 -= 360.0F;
      } else if (oldYaw < -1000.0F && var3 < -179.0F) {
         var3 += 360.0F;
      }

      return var3;
   }

   public static float i(float oldYaw, float newYaw) {
      float var2 = j(oldYaw, newYaw);
      return oldYaw + var2;
   }

   public static FluidHandling getFluidHandling(Item item) {
      return item instanceof FishingRodItem ? FluidHandling.ANY : FluidHandling.NONE;
   }

   private static Vec3d simulateTravelInWaterVelocity(Vec3d velocity, boolean hasGravity) {
      PlayerInputUtils$Input var2 = PlayerStateManager.INSTANCE.jJ;
      Vec2f var3 = applyMovementFactors(a.player, new Vec2f(var2.rp(), var2.ro()));
      Vec3d var4 = new Vec3d(var3.x, 0.0, var3.y);
      boolean var5 = velocity.y <= 0.0;
      double var6 = a.player.getY();
      double var8 = getEffectiveGravity(a.player);
      float var10 = a.player.isSprinting() ? 0.9F : 0.8F;
      float var11 = 0.02F;
      float var12 = (float)a.player.getAttributeValue(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY);
      if (!a.player.isOnGround()) {
         var12 *= 0.5F;
      }

      if (var12 > 0.0F) {
         var10 += (0.54600006F - var10) * var12;
         var11 += (a.player.getMovementSpeed() - var11) * var12;
      }

      if (a.player.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
         var10 = 0.96F;
      }

      Vec3d var13 = velocity.add(movementInputToVelocity(var4, var11, a.player.getYaw()));
      if (a.player.horizontalCollision && a.player.isClimbing()) {
         var13 = new Vec3d(var13.x, 0.2, var13.z);
      }

      var13 = var13.multiply(var10, 0.8F, var10);
      var13 = simulateApplyFluidMovingSpeed(var8, var5, var13, hasGravity, a.player.isSprinting());
      return simulateResetVerticalVelocityInFluid(var13, var6);
   }

   public static void parseEntityWhiteList(String value, Set<EntityType<?>> collection) {
      collection.clear();

      try {
         for (EntityType var3 : Registries.ENTITY_TYPE) {
            if (Pattern.matches(value, Registries.ENTITY_TYPE.getId(var3).getPath())) {
               collection.add(var3);
            }
         }

         if (Pattern.matches(value, "animal")) {
            for (EntityType var14 : Registries.ENTITY_TYPE) {
               if (var14.getSpawnGroup() == SpawnGroup.CREATURE) {
                  collection.add(var14);
               }
            }
         }

         if (Pattern.matches(value, "monster")) {
            for (EntityType var15 : Registries.ENTITY_TYPE) {
               if (var15.getSpawnGroup() == SpawnGroup.MONSTER && var15 != EntityType.ZOMBIFIED_PIGLIN && var15 != EntityType.ENDERMAN) {
                  collection.add(var15);
               }
            }
         }

         for (SpawnGroup var5 : SpawnGroup.values()) {
            if (var5 != SpawnGroup.MONSTER && Pattern.matches(value, var5.getName())) {
               for (EntityType var7 : Registries.ENTITY_TYPE) {
                  if (var7.getSpawnGroup() == var5) {
                     collection.add(var7);
                  }
               }
            }
         }

         if (Pattern.matches(value, "living_entity")) {
            for (EntityType var17 : Registries.ENTITY_TYPE) {
               if (var17.getSpawnGroup() != SpawnGroup.MISC) {
                  collection.add(var17);
               }
            }
         }

         Iterator var13 = collection.iterator();

         while (var13.hasNext()) {
            EntityType var18 = (EntityType)var13.next();
            if (!Pattern.matches(value, Registries.ENTITY_TYPE.getId(var18).getPath())
               && Pattern.matches(value, "!" + Registries.ENTITY_TYPE.getId(var18).getPath())) {
               var13.remove();
            }
         }
      } catch (Throwable var8) {
         collection.clear();
      }
   }

   public static Vec3d N(Vec3d self, double speed) {
      return X(self, speed, 1.0);
   }

   private static Vec3d simulateTravelInLavaVelocity(Vec3d velocity, boolean hasGravity) {
      PlayerInputUtils$Input var2 = PlayerStateManager.INSTANCE.jJ;
      Vec3d var3 = new Vec3d(var2.rp(), var2.rq(), var2.ro());
      boolean var4 = velocity.y <= 0.0;
      double var5 = a.player.getY();
      double var7 = getEffectiveGravity(a.player);
      Vec3d var9 = velocity.add(movementInputToVelocity(var3, 0.02F, a.player.getYaw()));
      if (a.player.getFluidHeight(FluidTags.LAVA) <= a.player.getSwimHeight()) {
         var9 = var9.multiply(0.5, 0.8F, 0.5);
         var9 = simulateApplyFluidMovingSpeed(var7, var4, var9, hasGravity, a.player.isSprinting());
      } else {
         var9 = var9.multiply(0.5);
      }

      if (var7 != 0.0) {
         var9 = var9.add(0.0, -var7 / 4.0, 0.0);
      }

      return simulateResetVerticalVelocityInFluid(var9, var5);
   }

   public static EntityType<?> b(Item spawner) {
      return (EntityType<?>)b.getOrDefault(spawner, null);
   }

   public static PlayerEntity getPlayerByName(String name) {
      return MinecraftClient.getInstance().world.getPlayers().stream().filter(m -> m.getNameForScoreboard().equals(name)).findFirst().orElse(null);
   }

   public static void setEntityYawSafe(Entity entity, float newYaw) {
      newYaw = i(entity.getYaw(), newYaw);
      entity.setYaw(newYaw);
   }

   private static Vec3d simulateResetVerticalVelocityInFluid(Vec3d velocity, double y) {
      return a.player.horizontalCollision && a.player.doesNotCollide(velocity.x, velocity.y + 0.6F - a.player.getY() + y, velocity.z)
         ? new Vec3d(velocity.x, 0.3F, velocity.z)
         : velocity;
   }

   public static Vec3d calculateGlidingVelocity(ClientPlayerEntity player, Vec3d oldVelocity, Vec3d rotationVector, boolean hasGravity) {
      float var5 = rotationToPitch(rotationVector);
      float var6 = var5 * (float) (Math.PI / 180.0);
      double var7 = Math.sqrt(rotationVector.x * rotationVector.x + rotationVector.z * rotationVector.z);
      double var9 = oldVelocity.horizontalLength();
      double var11 = hasGravity ? getEffectiveGravity(player) : 0.0;
      double var13 = MathHelper.square(Math.cos(var6));
      double var15 = oldVelocity.y + var11 * (var13 * 0.75 - 1.0);
      Vec3d var17 = new Vec3d(oldVelocity.x, var15, oldVelocity.z);
      if (var15 < 0.0 && var7 > 0.0) {
         double var18 = var15 * -0.1 * var13;
         var17 = var17.add(rotationVector.x * var18 / var7, var18, rotationVector.z * var18 / var7);
      }

      if (var6 < 0.0F && var7 > 0.0) {
         double var20 = var9 * -MathHelper.sin(var6) * 0.04;
         var17 = var17.add(-rotationVector.x * var20 / var7, var20 * 3.2, -rotationVector.z * var20 / var7);
      }

      if (var7 > 0.0) {
         double var21 = var9 / var7;
         var17 = var17.add((rotationVector.x * var21 - var17.x) * 0.1, 0.0, (rotationVector.z * var21 - var17.z) * 0.1);
      }

      return var17.multiply(0.99, 0.98, 0.99);
   }

   public static Vec3d X(Vec3d self, double speed, double strength) {
      ClientPlayerEntity var5 = MinecraftClient.getInstance().player;
      PlayerInputUtils$Input var6 = PlayerInputUtils.a(var5);
      float var7 = getMovementDirectionOfInput(var5.getYaw(), var6);
      return withStrafe(self, speed, strength, var6, var7);
   }

   static {
      for (Item var1 : Registries.ITEM) {
         if (var1 instanceof SpawnEggItem var2) {
            b.put(var1, var2.getEntityType(new ItemStack(var1)));
         }
      }

      SQRT_SPEED = Math.sqrt(0.0825);
   }
}
