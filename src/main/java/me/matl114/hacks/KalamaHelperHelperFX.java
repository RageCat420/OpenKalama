package me.matl114.hacks;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

public class KalamaHelperHelperFX implements KalamaHelperHelperMX {
    List<Box> d;
    List<VoxelShape> e;
    Vec3d c;
    List<Box> f;
    Entity a;
    boolean ignoreChunkBorder;
    Vec3d b;

    public KalamaHelperHelperFX(Entity entity, Vec3d startPos, Vec3d endPos, boolean ignoreChunkBorder) {
        this.a = entity;
        this.b = startPos;
        this.c = endPos;
        this.ignoreChunkBorder = ignoreChunkBorder;
        this.d = new ArrayList<>();
        this.e = new ArrayList<>();
        MovTasks.collectBoxInvolvingInMovements(
                entity, startPos, endPos.subtract(startPos), this.d, this.e, ignoreChunkBorder);
        this.f = new ArrayList<>();
        this.f.addAll(this.d);

        for (VoxelShape var6 : this.e) {
            this.f.addAll(var6.getBoundingBoxes());
        }
    }

    @Override
    public List<Vec3d> generateTpSequence(
            Vec3d current, Vec3d target, boolean command, double farawayTp, boolean considerEnvironment) {
        return KalamaHelperHelperMX.super.generateTpSequence(current, target, command, farawayTp, considerEnvironment);
    }

    @Override
    public Vec3d simulateMovement(Entity entity, Vec3d currentPos, Vec3d currentTry) {
        Entity var4 = entity.getRootVehicle();
        RenderTasks.g = true;
        Vec3d var5 = MovTasks.collideWithTrustedList(
                var4.dimensions.getBoxAt(currentPos),
                currentTry,
                this.e,
                this.d,
                var4.getStepHeight(),
                var4.isOnGround());
        RenderTasks.g = false;
        return var5;
    }

    @Override
    public boolean checkEnvironmentCollision(Entity entity, Vec3d vec, boolean checkLiquid) {
        return MovTasks.checkEnvironmentCollision(entity, vec, checkLiquid, this.ignoreChunkBorder);
    }
}
