package me.matl114.utils.commands.params.api;

import me.matl114.utils.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3d;

public record KalamaHelperHelperE(boolean sout) implements CommandExecution {

   @Override
   public void sn(Text message) {
      if (this.sout) {
         Debug.f(message);
      }
   }

   public boolean sout() {
      return this.sout;
   }

   @Override
   public void sm(@NotNull String message) {
      if (this.sout) {
         Debug.a(message);
      }
   }

   @Nullable
   @Override
   public PlayerEntity si() {
      return null;
   }

   @Override
   public Vector2f so() {
      return new Vector2f(0.0F, 0.0F);
   }

   @Override
   public Vector3d sp() {
      return new Vector3d(0.0, 0.0, 0.0);
   }

   public World getExecuteWorld() {
      return MinecraftClient.getInstance().world;
   }

   @Override
   public boolean hasPermission(String permission) {
      return true;
   }



   @Override
   public World sr() { return null; }

}
