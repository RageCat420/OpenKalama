package me.matl114.hacks.modules.render;

import java.util.UUID;
import net.minecraft.entity.EntityPose;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RenderSubHelperY {
   ChunkPos d;
   Text f;
   RegistryKey<World> h;
   UUID a;
   EntityPose e;
   String g;
   Box b;
   Vec3d c;
   int exitCode;

   public RenderSubHelperY(
      UUID uuid,
      Box leaveBox,
      Vec3d leavePos,
      ChunkPos leaveChunk,
      EntityPose leavePose,
      Text displayName,
      String scoreboardName,
      RegistryKey<World> leaveWorld,
      int exitCode
   ) {
      this.a = uuid;
      this.b = leaveBox;
      this.c = leavePos;
      this.d = leaveChunk;
      this.e = leavePose;
      this.f = displayName;
      this.g = scoreboardName;
      this.h = leaveWorld;
      this.exitCode = exitCode;
   }
}
