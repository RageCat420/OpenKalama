package me.matl114.hacks.modules.move;

import java.awt.Color;
import java.util.List;
import me.matl114.commands.MainCommand;
import me.matl114.hacks.KalamaHelperHelperCX;
import me.matl114.hacks.KalamaHelperHelperDX;
import me.matl114.hacks.KalamaHelperHelperM;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.impl.DispatchArgumentType;
import me.matl114.utils.commands.params.types.ExecutePos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

public class TpaCommand extends BaseModule {
    public boolean su(CommandExecution p, ArgumentInputStream re, ArgumentReader reader) {
        ExecutePos var4 = re.f();
        if (var4 != null) {
            Vector3d var5 = var4.wu(p);
            this.sw(new Vec3d(var5.x, var5.y, var5.z));
        } else {
            p.sm("输入了无效坐标!");
        }

        return true;
    }

    public boolean onMark(CommandExecution var1, ArgumentInputStream re, ArgumentReader reader) {
        String var4 = re.n();
        PlayerEntity var5 = var1.ss();
        Vec3d var8;
        switch (var4) {
            case "this":
                var8 = var5.getPos();
                break;
            case "camera":
                var8 = RenderUtils.getCameraEntityPos();
                break;
            case "cross":
                var8 = mc.crosshairTarget.getPos();
                break;
            case "player":
                String var12 = re.n();
                PlayerEntity var14 = EntityUtils.getPlayerByName(var12);
                if (var14 == null) {
                    var1.sn(Text.literal("找不到实体或者玩家: " + var12).formatted(Formatting.RED));
                    return true;
                }

                var8 = var14.getPos();
                break;
            case "pos":
                ExecutePos var11 = re.f();
                if (var11 == null) {
                    var1.sn(Text.literal("无效的坐标").formatted(Formatting.RED));
                    return true;
                }

                Vector3d var13 = var11.wu(var1);
                var8 = new Vec3d(var13.x, var13.y, var13.z);
                break;
            case "target":
                ExecutePos var9 = re.f();
                if (var9 == null) {
                    var1.sn(Text.literal("无效的特殊位置").formatted(Formatting.RED));
                    return true;
                }

                Vector3d var10 = var9.wu(var1);
                var8 = new Vec3d(var10.x, var10.y, var10.z);
                break;
            case "clear":
                MovTasks.R = null;
                return true;
            default:
                var1.sn(Text.literal("不存在的mark类型: " + var4).formatted(Formatting.RED));
                return true;
        }

        MovTasks.R = var8;
        Debug.chat("标记成功: ", ChatUtils.getDisplayedLocation(var8));
        RenderTasks.i(
                new KalamaHelperHelperCX(new KalamaHelperHelperDX(var5.dimensions.getBoxAt(MovTasks.R), Color.GREEN))
                        .d(() -> MovTasks.R != var8));
        return true;
    }

    public void st(MainCommand mainCommand) {
        TreeSubCommand var2 = mainCommand.bC().a("tpa").k();
        var2.subBuilder(SubCommand.bo())
                .u("tp")
                .x("message.command.tpa.tp.help")
                .A(KalamaHelperHelperA.<me.matl114.utils.commands.params.impl.KalamaHelperHelperA, ExecutePos>b(
                                me.matl114.utils.commands.params.impl.KalamaHelperHelperA::new)
                        .B("position")
                        .v())
                .z(e -> e.executor(this::su))
                .r();
        var2.subBuilder(SubCommand.bo())
                .u("mark")
                .x("message.command.tpa.mark.help")
                .A(KalamaHelperHelperA.a()
                        .B("type")
                        .m(List.of("player", "camera", "this", "pos", "target", "cross", "clear"), "camera")
                        .v())
                .A(new DispatchArgumentType<Object>("extra")
                        .registerArgumentDispatcher(
                                0,
                                "pos",
                                KalamaHelperHelperA
                                        .<me.matl114.utils.commands.params.impl.KalamaHelperHelperA, ExecutePos>b(
                                                me.matl114.utils.commands.params.impl.KalamaHelperHelperA::new)
                                        .B("dispatch_pos")
                                        .v())
                        .registerArgumentDispatcher(
                                0,
                                "target",
                                KalamaHelperHelperA.<KalamaHelperHelperM, ExecutePos>b(KalamaHelperHelperM::new)
                                        .B("dispatch_tpa")
                                        .v())
                        .registerArgumentDispatcher(
                                0,
                                "player",
                                KalamaHelperHelperA.a()
                                        .B("dispatch_player")
                                        .d(() -> EntityUtils.getWorldPlayerNames(false))
                                        .v())
                        .registerDispatcher(
                                (p, args) -> true,
                                KalamaHelperHelperA.a().B("dispatch_default").v()))
                .z(e -> e.executor(this::onMark))
                .r();
        var2.subBuilder(SubCommand.bo())
                .u("tpa")
                .x("message.command.tpa.tpa.help")
                .A(KalamaHelperHelperA.<KalamaHelperHelperM, ExecutePos>b(KalamaHelperHelperM::new)
                        .B("tpa_target")
                        .v())
                .z(e -> e.executor(this::sv))
                .r();
    }

    public TpaCommand() {
        super("TpaCommand");
    }

    public boolean sv(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
        ExecutePos var4 = streamArgs.f();
        if (var4 != null) {
            Vector3d var5 = var4.wu(var1);
            this.sw(new Vec3d(var5.x, var5.y, var5.z));
        } else {
            var1.sm("输入了无效目标位置!");
        }

        return true;
    }

    public void sw(Vec3d pos) {
        MovTasks.executeTp(pos, 320.0, true, true);
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerCommandBootstrap(this::st);
    }
}
