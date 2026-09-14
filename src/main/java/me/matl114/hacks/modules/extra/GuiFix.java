package me.matl114.hacks.modules.extra;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.Debug;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class GuiFix extends BaseModule {
    private static final Set<Text> VANILLA_BUTTON_TEXT;
    public final ModulePath zm = makePath(Configs.j, "other.gui-fix");
    public final FlagRef optimizeServerScreen;
    public final FlagRef zn =
            this.flagBuilder(this.zm.add("disable-terrain-load-screen")).build();
    public final FlagRef zo = this.builder(this.zm.add("optimize-game-menu"), FlagRef.TYPE)
            .defaultValue(true)
            .build();

    private void onResize(List<Widget> widgets) {
        widgets.sort(Comparator.comparingInt(s -> -(s.getX() + s.getWidth())));
        int var2 = Integer.MAX_VALUE;
        ArrayDeque var3 = new ArrayDeque();

        for (Widget var5 : widgets) {
            if (var5.getY() < 5) {
                var5.setY(5);
            }

            int var6 = var5.getX() + var5.getWidth();
            if (var6 < var2) {
                if (var3.isEmpty()) {
                    var2 = var5.getX();
                } else {
                    while (!var3.isEmpty()) {
                        Widget var7 = (Widget) var3.peekFirst();
                        int var8 = var7.getWidth();
                        if (var6 >= var2 - var8) {
                            if (var5.getX() < var2 - var8) {
                                var2 -= var8;
                                var7.setX(var2);
                                var3.removeFirst();
                                var3.addLast(var5);
                            } else {
                                var2 = var5.getX();
                            }
                            break;
                        }

                        var2 -= var8;
                        var7.setX(var2);
                        var3.removeFirst();
                    }
                }
            } else {
                var3.addLast(var5);
            }
        }

        while (!var3.isEmpty()) {
            Widget var9 = (Widget) var3.removeFirst();
            int var10 = var9.getWidth();
            var2 -= var10;
            var9.setX(var2);
        }
    }

    public void onGameMenuScreenRelocateWurstButton(Event<GameMenuScreen> screenEvent) {
        if (this.zo.get()) {
            GameMenuScreen var2 = (GameMenuScreen) screenEvent.b;
            List<? extends Element> var3 = var2.children();
            int var4 = 0;
            int var5 = 0;
            ArrayList<ButtonWidget> var6 = new ArrayList();

            for (Element var8 : var3) {
                if (var8 instanceof ButtonWidget var9) {
                    Text var10 = var9.getMessage();
                    if (VANILLA_BUTTON_TEXT.contains(var10)) {
                        if (!var9.visible) {
                            var9.visible = true;
                        }

                        var5 = Math.max(var5, var9.getY());
                    } else {
                        var6.add(var9);
                    }
                }
            }

            if (var5 > 0 && !var6.isEmpty()) {
                for (ButtonWidget var12 : var6) {
                    if (var12.getWidth() > 100) {
                        var12.setY(var5 + 24 * ++var4);
                    }
                }
            }
        }
    }

    public GuiFix() {
        super("GuiFix");
        this.optimizeServerScreen = this.builder(this.zm.add("optimize-server-screen"), FlagRef.TYPE)
                .defaultValue(true)
                .build();
    }

    public void onServerListMenuRelocateButtons(Event<MultiplayerScreen> screenEvent) {
        if (this.optimizeServerScreen.get()) {
            MultiplayerScreen var2 = (MultiplayerScreen) screenEvent.b;

            try {
                List var3 = var2.children().stream()
                        .filter(s -> s instanceof Widget && s instanceof Selectable && !(s instanceof TextWidget))
                        .map(Widget.class::cast)
                        .filter(s -> s.getY() < 20)
                        .collect(Collectors.toCollection(ArrayList::new));
                this.onResize(var3);
            } catch (Throwable var4) {
                Debug.e(var4, "Error while rescheduling server list screen");
            }
        }
    }

    public void Nq(Event<Screen> event) {
        if (this.zn.get()) {
            event.context(null);
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ae().c(LevelLoadingScreen.class), this::Nq);
        this.registerListener(
                Listener.ai().c(GameMenuScreen.class), this::onGameMenuScreenRelocateWurstButton, 2147483646);
        this.registerListener(
                Listener.ai().c(MultiplayerScreen.class), this::onServerListMenuRelocateButtons, 2147483646);
    }

    static {
        LinkedHashSet var0 = new LinkedHashSet();
        Field[] var1 = GameMenuScreen.class.getDeclaredFields();

        for (Field var5 : var1) {
            try {
                if (Modifier.isStatic(var5.getModifiers()) && Text.class.isAssignableFrom(var5.getType())) {
                    var5.setAccessible(true);
                    Text var6 = (Text) var5.get(null);
                    if (var6 instanceof MutableText var7 && var7.getContent() instanceof TranslatableTextContent var9) {
                        var0.add(var6);
                    }
                }
            } catch (Throwable var17) {
            }
        }

        VANILLA_BUTTON_TEXT = var0;
    }
}
