package me.matl114.jsApi;

import java.util.Locale;
import me.matl114.accessors.events.EntityAccess;
import me.matl114.events.annotations.Modifiable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;

@Modifiable
public class EntityHelper {
   public static Vec3d getEntityVelocity(Entity entity) {
      return entity.getVelocity();
   }

   public static void setEntityVelocity(Entity entity, Vec3d velocity) {
      entity.setVelocity(velocity);
   }

   public static EntityType getEntityTypeByName(String name) {
      return (EntityType)EntityType.get(name).orElse(null);
   }

   public static void setEntityFlag(Entity entity, int flag, boolean value) {
      EntityAccess.of(entity).setDataFlag(flag, value);
   }

   public static String getEntityTypeName(EntityType entityType) {
      return EntityType.getId(entityType).toString();
   }

   public static boolean getEntityFlag(Entity entity, int flag) {
      return EntityAccess.of(entity).getDataFlag(flag);
   }

   public static EntityType getEntityType(Entity entity) {
      return entity.getType();
   }

   public static EntityPose getEntityPose(Entity entity) {
      return entity.getPose();
   }

   public static int getEntityId(Entity entity) {
      return entity.getId();
   }

   public static void setEntityPose(Entity entity, String pose) {
      entity.setPose(EntityPose.valueOf(pose.toUpperCase(Locale.ROOT)));
   }
}
