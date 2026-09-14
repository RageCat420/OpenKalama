package me.matl114.hacks.modules.move;


import net.minecraft.client.MinecraftClient;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.commands.params.api.CommandExecution;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3d;

public class MoveSubHelperC implements CommandExecution {
   static final MoveSubHelperC instance = new MoveSubHelperC();

   public void sendMessage(@NotNull String message) {
      if (MinecraftClient.getInstance().player != null) {
         Debug.sendPlayer(ChatUtils.textFromLegacyString(message));
      }
   }

   @Override
   public Vector2f so() {
      return new Vector2f(PlayerStateManager.INSTANCE.jl, PlayerStateManager.INSTANCE.jm);
   }

   @NotNull
   @Override
   public World sr() {
      return MinecraftClient.getInstance().world;
   }

   @Override
   public void sn(Text message) {
      if (MinecraftClient.getInstance().player != null) {
         Debug.sendPlayer(message);
      }
   }

   @NotNull
   @Override
   public Vector3d sp() {
      return new Vector3d(PlayerStateManager.INSTANCE.ji, PlayerStateManager.INSTANCE.jk, PlayerStateManager.INSTANCE.jj);
   }

   @Nullable
   public PlayerEntity getExecutor() {
      return MinecraftClient.getInstance().player;
   }

   @Override
   public boolean hasPermission(String permission) {
      return true;
   }



   @Override
   public PlayerEntity si() { return null; }


   @Override
   public void sm(String arg0) { }

}
