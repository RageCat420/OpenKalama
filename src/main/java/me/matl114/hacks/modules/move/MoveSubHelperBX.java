package me.matl114.hacks.modules.move;

import me.matl114.utils.EntityUtils;
import me.matl114.utils.entity.PlayerInputUtils$Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;

public record MoveSubHelperBX(
        int priority,
        @Nullable Boolean forward,
        @Nullable Boolean backward,
        @Nullable Boolean left,
        @Nullable Boolean right,
        @Nullable Boolean jump,
        @Nullable Boolean sneak,
        @Nullable Boolean sprint,
        @Nullable Float pitch,
        @Nullable Float yaw) {
    public static final MoveSubHelperBX EMPTY =
            new MoveSubHelperBX(0, null, null, null, null, null, null, null, null, null);

    public static MoveSubHelperBX gI(int priority) {
        return EMPTY.gV(priority);
    }

    @Nullable
    public Boolean hk() {
        return this.sneak;
    }

    public MoveSubHelperBX gO(boolean value) {
        return new MoveSubHelperBX(
                this.priority,
                this.forward,
                this.backward,
                this.left,
                this.right,
                this.jump,
                value,
                this.sprint,
                this.pitch,
                this.yaw);
    }

    public int ba() {
        return this.priority;
    }

    public MoveSubHelperBX gM(boolean value) {
        return new MoveSubHelperBX(
                this.priority,
                this.forward,
                this.backward,
                this.left,
                value,
                this.jump,
                this.sneak,
                this.sprint,
                this.pitch,
                this.yaw);
    }

    public boolean isEmpty() {
        return this.forward == null
                && this.backward == null
                && this.left == null
                && this.right == null
                && this.jump == null
                && this.sneak == null
                && this.sprint == null
                && this.pitch == null
                && this.yaw == null;
    }

    public MoveSubHelperBX gP(boolean value) {
        return new MoveSubHelperBX(
                this.priority,
                this.forward,
                this.backward,
                this.left,
                this.right,
                this.jump,
                this.sneak,
                value,
                this.pitch,
                this.yaw);
    }

    @Nullable
    public Boolean hg() {
        return this.backward;
    }

    @Nullable
    public Float hm() {
        return this.pitch;
    }

    public MoveSubHelperBX gX(@Nullable Boolean backward) {
        return this.backward == backward
                ? this
                : new MoveSubHelperBX(
                        this.priority,
                        this.forward,
                        backward,
                        this.left,
                        this.right,
                        this.jump,
                        this.sneak,
                        this.sprint,
                        this.pitch,
                        this.yaw);
    }

    public MoveSubHelperBX hd(@Nullable Float pitch) {
        return this.pitch == pitch
                ? this
                : new MoveSubHelperBX(
                        this.priority,
                        this.forward,
                        this.backward,
                        this.left,
                        this.right,
                        this.jump,
                        this.sneak,
                        this.sprint,
                        pitch,
                        this.yaw);
    }

    public MoveSubHelperBX gY(@Nullable Boolean left) {
        return this.left == left
                ? this
                : new MoveSubHelperBX(
                        this.priority,
                        this.forward,
                        this.backward,
                        left,
                        this.right,
                        this.jump,
                        this.sneak,
                        this.sprint,
                        this.pitch,
                        this.yaw);
    }

    public void gS(PlayerInputUtils$Input input) {
        if (this.forward != null) {
            input.rx(this.forward);
        }

        if (this.backward != null) {
            input.ry(this.backward);
        }

        if (this.left != null) {
            input.rz(this.left);
        }

        if (this.right != null) {
            input.rA(this.right);
        }

        if (this.jump != null) {
            input.rB(this.jump);
        }

        if (this.sneak != null) {
            input.rC(this.sneak);
        }

        if (this.sprint != null) {
            input.rD(this.sprint);
        }
    }

    public MoveSubHelperBX ha(@Nullable Boolean jump) {
        return this.jump == jump
                ? this
                : new MoveSubHelperBX(
                        this.priority,
                        this.forward,
                        this.backward,
                        this.left,
                        this.right,
                        jump,
                        this.sneak,
                        this.sprint,
                        this.pitch,
                        this.yaw);
    }

    @Nullable
    public Boolean hf() {
        return this.forward;
    }

    public void gT(ClientPlayerEntity player) {
        if (player != null) {
            if (this.pitch != null) {
                EntityUtils.setEntityPitchSafe(player, this.pitch);
            }

            if (this.yaw != null) {
                EntityUtils.setEntityYawSafe(player, this.yaw);
            }
        }
    }

    public MoveSubHelperBX gN(boolean value) {
        return new MoveSubHelperBX(
                this.priority,
                this.forward,
                this.backward,
                this.left,
                this.right,
                value,
                this.sneak,
                this.sprint,
                this.pitch,
                this.yaw);
    }

    public MoveSubHelperBX he(@Nullable Float yaw) {
        return this.yaw == yaw
                ? this
                : new MoveSubHelperBX(
                        this.priority,
                        this.forward,
                        this.backward,
                        this.left,
                        this.right,
                        this.jump,
                        this.sneak,
                        this.sprint,
                        this.pitch,
                        yaw);
    }

    @Nullable
    public Boolean hh() {
        return this.left;
    }

    @Nullable
    public Float hn() {
        return this.yaw;
    }

    public MoveSubHelperBX hc(@Nullable Boolean sprint) {
        return this.sprint == sprint
                ? this
                : new MoveSubHelperBX(
                        this.priority,
                        this.forward,
                        this.backward,
                        this.left,
                        this.right,
                        this.jump,
                        this.sneak,
                        sprint,
                        this.pitch,
                        this.yaw);
    }

    public MoveSubHelperBX(
            int priority,
            @Nullable Boolean forward,
            @Nullable Boolean backward,
            @Nullable Boolean left,
            @Nullable Boolean right,
            @Nullable Boolean jump,
            @Nullable Boolean sneak,
            @Nullable Boolean sprint,
            @Nullable Float pitch,
            @Nullable Float yaw) {
        this.priority = priority;
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.jump = jump;
        this.sneak = sneak;
        this.sprint = sprint;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public MoveSubHelperBX gV(int priority) {
        return this.priority == priority
                ? this
                : new MoveSubHelperBX(
                        priority,
                        this.forward,
                        this.backward,
                        this.left,
                        this.right,
                        this.jump,
                        this.sneak,
                        this.sprint,
                        this.pitch,
                        this.yaw);
    }

    public MoveSubHelperBX gQ(float value) {
        return new MoveSubHelperBX(
                this.priority,
                this.forward,
                this.backward,
                this.left,
                this.right,
                this.jump,
                this.sneak,
                this.sprint,
                value,
                this.yaw);
    }

    public MoveSubHelperBX gK(boolean value) {
        return new MoveSubHelperBX(
                this.priority,
                this.forward,
                value,
                this.left,
                this.right,
                this.jump,
                this.sneak,
                this.sprint,
                this.pitch,
                this.yaw);
    }

    public MoveSubHelperBX gL(boolean value) {
        return new MoveSubHelperBX(
                this.priority,
                this.forward,
                this.backward,
                value,
                this.right,
                this.jump,
                this.sneak,
                this.sprint,
                this.pitch,
                this.yaw);
    }

    public MoveSubHelperBX gW(@Nullable Boolean forward) {
        return this.forward == forward
                ? this
                : new MoveSubHelperBX(
                        this.priority,
                        forward,
                        this.backward,
                        this.left,
                        this.right,
                        this.jump,
                        this.sneak,
                        this.sprint,
                        this.pitch,
                        this.yaw);
    }

    public MoveSubHelperBX gR(float value) {
        return new MoveSubHelperBX(
                this.priority,
                this.forward,
                this.backward,
                this.left,
                this.right,
                this.jump,
                this.sneak,
                this.sprint,
                this.pitch,
                value);
    }

    @Nullable
    public Boolean hj() {
        return this.jump;
    }

    @Nullable
    public Boolean hi() {
        return this.right;
    }

    public MoveSubHelperBX gZ(@Nullable Boolean right) {
        return this.right == right
                ? this
                : new MoveSubHelperBX(
                        this.priority,
                        this.forward,
                        this.backward,
                        this.left,
                        right,
                        this.jump,
                        this.sneak,
                        this.sprint,
                        this.pitch,
                        this.yaw);
    }

    public boolean gU() {
        return this.pitch != null || this.yaw != null;
    }

    public MoveSubHelperBX hb(@Nullable Boolean sneak) {
        return this.sneak == sneak
                ? this
                : new MoveSubHelperBX(
                        this.priority,
                        this.forward,
                        this.backward,
                        this.left,
                        this.right,
                        this.jump,
                        sneak,
                        this.sprint,
                        this.pitch,
                        this.yaw);
    }

    @Nullable
    public Boolean hl() {
        return this.sprint;
    }

    public MoveSubHelperBX gJ(boolean value) {
        return new MoveSubHelperBX(
                this.priority,
                value,
                this.backward,
                this.left,
                this.right,
                this.jump,
                this.sneak,
                this.sprint,
                this.pitch,
                this.yaw);
    }
}
