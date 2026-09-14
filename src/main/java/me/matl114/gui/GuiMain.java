package me.matl114.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import me.matl114.events.Event;
import me.matl114.events.RenderListener;
import me.matl114.utils.ResourceUtils;
import net.minecraft.util.Identifier;

public class GuiMain {
    private static final List<Identifier> b = new ArrayList<>();
    private static final List<Identifier> a = new ArrayList<>();

    public static List<Identifier> c() {
        return Collections.unmodifiableList(a);
    }

    public static List<Identifier> d() {
        return Collections.unmodifiableList(b);
    }

    public static void onAtlasLoadGuiElements(Event<Set<Identifier>> loadEvent) {
        if (new Identifier("minecraft", "gui").equals(loadEvent.getArgs(1))) {
            Set<Identifier> var1 = ResourceUtils.c(loadEvent.getArgs(0), "gui");
            ((Set) loadEvent.e()).addAll(var1);
            a.clear();
            a.addAll(var1);
            Set<Identifier> var2 = ResourceUtils.c(loadEvent.getArgs(0), "custom");
            b.clear();
            var2.stream()
                    .map(s -> new Identifier(s.getNamespace(), "textures/" + s.getPath() + ".png"))
                    .forEach(b::add);
        }
    }

    static {
        RenderListener.w().k(GuiMain::onAtlasLoadGuiElements);
    }

    public static void init() {}
}
