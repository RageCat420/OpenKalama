package me.matl114.utils.entity;

import me.matl114.versioned.accessors.PlayerInputAccess;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

public class PlayerInputUtils$Input implements Cloneable {
   boolean kU;
   boolean kV;
   boolean kT;
   boolean kW;
   boolean kR;
   boolean kX;
   boolean kS;

   public PlayerInputUtils$Input rP(boolean jump) {
      return this.kV == jump ? this : new PlayerInputUtils$Input(this.kR, this.kS, this.kT, this.kU, jump, this.kW, this.kX);
   }

   public PlayerInputUtils$Input rz(boolean left) {
      this.kT = left;
      return this;
   }

   public PlayerInputUtils$Input rR(boolean sprint) {
      return this.kX == sprint ? this : new PlayerInputUtils$Input(this.kR, this.kS, this.kT, this.kU, this.kV, this.kW, sprint);
   }

   public PlayerInputUtils$Input applyInput(ClientPlayerEntity player) {
      Input var2 = player.input;
      var2.pressingForward = this.kR;
      var2.pressingBack = this.kS;
      var2.pressingLeft = this.kT;
      var2.pressingRight = this.kU;
      var2.jumping = this.kV;
      var2.sneaking = this.kW;
      PlayerInputAccess.of(var2).setPressingSprint(this.kX);
      return this;
   }

   public boolean rI() {
      return this.kV;
   }

   public PlayerInputUtils$Input ry(boolean backward) {
      this.kS = backward;
      return this;
   }

   public PlayerInputUtils$Input rw() {
      try {
         return (PlayerInputUtils$Input)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError();
      }
   }

   public PlayerInputUtils$Input rM(boolean backward) {
      return this.kS == backward ? this : new PlayerInputUtils$Input(this.kR, backward, this.kT, this.kU, this.kV, this.kW, this.kX);
   }

   public boolean ru() {
      return this.kR != this.kS || this.kT != this.kU;
   }

   public PlayerInputUtils$Input rD(boolean sprint) {
      this.kX = sprint;
      return this;
   }

   public int rp() {
      return this.kT == this.kU ? 0 : (this.kT ? 1 : -1);
   }

   public int ro() {
      return this.kR == this.kS ? 0 : (this.kR ? 1 : -1);
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PlayerInputUtils$Input var2)) {
         return false;
      } else if (!var2.canEqual(this)) {
         return false;
      } else if (this.rE() != var2.rE()) {
         return false;
      } else if (this.rF() != var2.rF()) {
         return false;
      } else if (this.rG() != var2.rG()) {
         return false;
      } else if (this.rH() != var2.rH()) {
         return false;
      } else if (this.rI() != var2.rI()) {
         return false;
      } else {
         return this.rJ() != var2.rJ() ? false : this.rK() == var2.rK();
      }
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + (this.rE() ? 79 : 97);
      var2 = var2 * 59 + (this.rF() ? 79 : 97);
      var2 = var2 * 59 + (this.rG() ? 79 : 97);
      var2 = var2 * 59 + (this.rH() ? 79 : 97);
      var2 = var2 * 59 + (this.rI() ? 79 : 97);
      var2 = var2 * 59 + (this.rJ() ? 79 : 97);
      return var2 * 59 + (this.rK() ? 79 : 97);
   }

   public PlayerInputUtils$Input sendPlayerInputAsRiding() {
      PlayerInputUtils.b.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(this.rp() * 0.98F, this.ro() * 0.98F, this.rI(), this.rJ()));
      return this;
   }

   public PlayerInputUtils$Input rl() {
      return this;
   }

   public PlayerInputUtils$Input(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
      this.kR = forward;
      this.kS = backward;
      this.kT = left;
      this.kU = right;
      this.kV = jump;
      this.kW = sneak;
      this.kX = sprint;
   }

   @Override
   public String toString() {
      return "Input{forward="
         + this.kR
         + ", backward="
         + this.kS
         + ", left="
         + this.kT
         + ", right="
         + this.kU
         + ", jump="
         + this.kV
         + ", sneak="
         + this.kW
         + ", sprint="
         + this.kX
         + "}";
   }

   public PlayerInputUtils$Input rx(boolean forward) {
      this.kR = forward;
      return this;
   }

   public PlayerInputUtils$Input rA(boolean right) {
      this.kU = right;
      return this;
   }

   public boolean rE() {
      return this.kR;
   }

   public PlayerInputUtils$Input rL(boolean forward) {
      return this.kR == forward ? this : new PlayerInputUtils$Input(forward, this.kS, this.kT, this.kU, this.kV, this.kW, this.kX);
   }

   protected boolean canEqual(Object other) {
      return other instanceof PlayerInputUtils$Input;
   }

   public PlayerInputUtils$Input(boolean forward, boolean backward, boolean left, boolean right) {
      this(forward, backward, left, right, false, false, false);
   }

   public int rq() {
      return this.kV == this.kW ? 0 : (this.kV ? 1 : -1);
   }

   public boolean rG() {
      return this.kT;
   }

   public boolean rv() {
      return this.kR || this.kS || this.kT || this.kU || this.kV || this.kW;
   }

   public PlayerInputUtils$Input sendPlayerSneakUpdatePacket() {
      if (this.kW) {
         PlayerInputUtils.b.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(PlayerInputUtils.b.player, Mode.PRESS_SHIFT_KEY));
      } else {
         PlayerInputUtils.b.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(PlayerInputUtils.b.player, Mode.RELEASE_SHIFT_KEY));
      }

      return this;
   }

   public boolean rH() {
      return this.kU;
   }

   public PlayerInputUtils$Input rB(boolean jump) {
      this.kV = jump;
      return this;
   }

   public PlayerInputUtils$Input rO(boolean right) {
      return this.kU == right ? this : new PlayerInputUtils$Input(this.kR, this.kS, this.kT, right, this.kV, this.kW, this.kX);
   }

   public PlayerInputUtils$Input rs(GameOptions options) {
      options.forwardKey.setPressed(this.kR);
      options.backKey.setPressed(this.kS);
      options.leftKey.setPressed(this.kT);
      options.rightKey.setPressed(this.kU);
      options.jumpKey.setPressed(this.kV);
      options.sneakKey.setPressed(this.kW);
      options.sprintKey.setPressed(this.kX);
      return this;
   }

   public boolean rF() {
      return this.kS;
   }

   public PlayerInputUtils$Input rC(boolean sneak) {
      this.kW = sneak;
      return this;
   }

   public PlayerInputUtils$Input rQ(boolean sneak) {
      return this.kW == sneak ? this : new PlayerInputUtils$Input(this.kR, this.kS, this.kT, this.kU, this.kV, sneak, this.kX);
   }

   public boolean rK() {
      return this.kX;
   }

   public PlayerInputUtils$Input rN(boolean left) {
      return this.kT == left ? this : new PlayerInputUtils$Input(this.kR, this.kS, left, this.kU, this.kV, this.kW, this.kX);
   }

   public boolean rJ() {
      return this.kW;
   }

   public boolean rt() {
      return this.kR || this.kS || this.kT || this.kU || this.kV;
   }
}
