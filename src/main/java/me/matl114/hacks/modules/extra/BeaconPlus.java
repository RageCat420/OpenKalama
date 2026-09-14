package me.matl114.hacks.modules.extra;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import me.matl114.accessors.access.HandledScreenAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.complex.other.BeaconEffectSelectButton;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.text.Text;

public class BeaconPlus extends BaseModule {
    public final FlagRef enableBeaconEnhance;
    public final ModulePath ad = makePath(Configs.j, "other");

    public BeaconPlus() {
        super("BeaconPlus");
        this.enableBeaconEnhance =
                this.flagBuilder(this.ad.add("enable-beacon-enhance")).build();
        this.bindFlag(this.enableBeaconEnhance);
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ai().c(HandledScreen.class), this::onScreenInitialize);
    }

    public void onScreenInitialize(Event<Screen> e) {
        if (this.enableBeaconEnhance.get() && e.e() instanceof BeaconScreen var3) {
            HandledScreenAccess var9 = HandledScreenAccess.of(var3);
            int var4 = var9.getScreenX();
            int var5 = var9.getScreenY();
            BeaconEffectSelectButton var6 =
                    new BeaconEffectSelectButton(var4 + 167 - 23, var5 + 47 + 26, 22, 22, Text.literal("第一等级: "));
            var9.addDrawableChildTo(var6);
            BeaconEffectSelectButton var7 =
                    new BeaconEffectSelectButton(var4 + 167 + 1, var5 + 47 + 26, 22, 22, Text.literal("第二等级: "));
            var9.addDrawableChildTo(var7);
            AtomicReference var8 = new AtomicReference<>(ButtonWidget.builder(Text.literal("Send packet"), b -> {
                        MinecraftClient.getInstance()
                                .getNetworkHandler()
                                .sendPacket(new UpdateBeaconC2SPacket(
                                        Optional.ofNullable(var6.fL()), Optional.ofNullable(var7.fL())));
                        Debug.b(Text.literal("成功发送了信标设置!"));
                    })
                    .tooltip(Tooltip.of(Text.literal("点击上方选效果,点此强制修改信标")))
                    .dimensions(var4 + 167 - 23, var5 + 47 + 48, 46, 10)
                    .build());
            var9.addDrawableChildTo((ButtonWidget) var8.get());
        }
    }
}
