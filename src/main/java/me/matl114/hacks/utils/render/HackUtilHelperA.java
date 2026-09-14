package me.matl114.hacks.utils.render;

import java.util.List;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.render.KalamaHelperHelperG;
import me.matl114.versioned.api.VDrawContext;
import me.matl114.versioned.api.VRender;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

class HackUtilHelperA extends KalamaHelperHelperG<List<Vec3d>> {
    @Override
    public void a(MatrixStack matrices) {
        if (!this.entries.isEmpty()) {
            Vec3d var2 = RenderUtils.getCameraPos().negate();
            VRender.getInstance().h((op, vtx) -> {
                for (KalamaHelperHelperK var6 : this.entries) {
                    List<Vec3d> var7 = ((List<Vec3d>) var6.val())
                            .stream().map(s -> s.add(var2)).toList();
                    op.drawLines(matrices, vtx, var7, var6.index());
                }
            });
        }
    }

    @Override
    public void b(VDrawContext vDrawContext) {}
}
