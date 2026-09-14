package me.matl114.utils.entity;

import me.matl114.utils.EntityUtils;
import me.matl114.versioned.accessors.PlayerInputAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;

public class PlayerInputUtils {
   public static final MinecraftClient b = MinecraftClient.getInstance();
   public static final PlayerInputUtils$Input a = new PlayerInputUtils$Input(false, false, false, false, false, false, false);

   public static PlayerInputUtils$Input of(GameOptions options) {
      return new PlayerInputUtils$Input(
         options.forwardKey.isPressed(),
         options.backKey.isPressed(),
         options.leftKey.isPressed(),
         options.rightKey.isPressed(),
         options.jumpKey.isPressed(),
         options.sneakKey.isPressed(),
         options.sprintKey.isPressed()
      );
   }

   public static PlayerInputUtils$Input d(PlayerInputC2SPacket packet) {
      return new PlayerInputUtils$Input(
         packet.getForward() > 0.0F,
         packet.getForward() < 0.0F,
         packet.getSideways() > 0.0F,
         packet.getSideways() < 0.0F,
         packet.isJumping(),
         packet.isSneaking(),
         MinecraftClient.getInstance().options.sprintKey.isPressed()
      );
   }

   public static PlayerInputUtils$Input b(Input input) {
      return new PlayerInputUtils$Input(
         input.pressingForward,
         input.pressingBack,
         input.pressingLeft,
         input.pressingRight,
         input.jumping,
         input.sneaking,
         PlayerInputAccess.of(input).isPressingSprint()
      );
   }

   public static PlayerInputUtils$Input tryCorrectMovementInput(PlayerInputUtils$Input input, float originalYaw, float currentYaw) {
      float var3 = EntityUtils.j(originalYaw, currentYaw);
      int var4 = input.ro();
      int var5 = input.rp();
      if (var3 < 22.5 && var3 >= -22.5) {
         return input;
      } else {
         int var6;
         int var7;
         if (var3 < 67.5 && var3 >= 22.5) {
            var6 = var4 - var5;
            var7 = var4 + var5;
         } else if (var3 >= 67.5 && var3 < 112.5F) {
            var6 = -var5;
            var7 = var4;
         } else if (var3 >= 112.5F && var3 < 157.5F) {
            var6 = -var4 - var5;
            var7 = var4 - var5;
         } else if (var3 >= 157.5F || var3 < -157.5F) {
            var6 = -var4;
            var7 = -var5;
         } else if (var3 >= -157.5F && var3 < -112.5F) {
            var6 = -var4 + var5;
            var7 = -var4 - var5;
         } else if (var3 >= -112.5F && var3 < -67.5F) {
            var6 = var5;
            var7 = -var4;
         } else {
            if (!(var3 >= -67.5F) || !(var3 < -22.5F)) {
               return input;
            }

            var6 = var4 + var5;
            var7 = -var4 + var5;
         }

         return new PlayerInputUtils$Input(var6 > 0, var6 < 0, var7 > 0, var7 < 0, input.rI(), input.rJ(), input.rK());
      }
   }

   public static PlayerInputUtils$Input a(ClientPlayerEntity pl) {
      return b(pl.input);
   }
}
