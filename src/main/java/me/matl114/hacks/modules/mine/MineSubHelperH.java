package me.matl114.hacks.modules.mine;

import java.util.Objects;
import me.matl114.managers.Tasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MineSubHelperH {
   public int d;
   public BlockPos e;
   public int c = 0;
   public int f;
   public PlayerEntity a;
   public int g;
   public BlockPos b;

   public MineSubHelperH(PlayerEntity player) {
      this.d = -1;
      this.f = -1;
      this.g = 0;
      this.a = player;
   }

   public BlockPos i() {
      return this.e;
   }

   public void tickWorld(World world) {
      if (this.e != null && world.getBlockState(this.e).isAir()) {
         this.e = null;
         this.f = -1;
      }
   }

   public int k() {
      return this.g;
   }

   private void c(BlockPos pos) {
      if (!Objects.equals(this.e, pos)) {
         this.e = pos;
         this.g = Tasks.b();
      }

      if (this.f >= 10) {
         this.e = null;
         this.f = -1;
      }
   }

   public int j() {
      return this.f;
   }

   public void pushBreakingProgress(BlockPos pos, int breakingProgress) {
      if (Objects.equals(this.b, pos)) {
         this.d = breakingProgress;
         this.b(pos);
      } else if (Objects.equals(this.e, pos)) {
         this.f = breakingProgress;
         this.c(pos);
      } else if (this.b == null) {
         this.d = breakingProgress;
         this.b(pos);
      } else {
         if (this.d != 0 && this.f <= 0) {
            this.f = this.d;
            this.c(this.b);
         }

         this.d = breakingProgress;
         this.b(pos);
      }
   }

   public int h() {
      return this.d;
   }

   public PlayerEntity e() {
      return this.a;
   }

   public BlockPos f() {
      return this.b;
   }

   public int g() {
      return this.c;
   }

   private void b(BlockPos pos) {
      if (!Objects.equals(this.b, pos)) {
         this.b = pos;
         this.c = Tasks.b();
      }

      if (this.d >= 10) {
         this.d = -1;
      }
   }
}
