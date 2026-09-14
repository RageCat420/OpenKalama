package me.matl114.hacks.modules.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.matl114.events.Event;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.HackModules;
import me.matl114.managers.Configs;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.text.Text;

public class ModuleListHud extends IRender2DColoredModule {
    List<RenderSubHelperX> lk;
    public final ModulePath lj = makePath(Configs.i, "in-game-hud");
    List<RenderSubHelperX> ll;
    public final ModulePath he = this.lj.add("module-list-hud");

    @Override
    public void render2D(VDrawContext vdraw, float partialTicks) {
        this.sG(vdraw);
    }

    private void sF() {
        this.lk.sort(Comparator.comparingDouble(
                s -> -mc.textRenderer.getTextHandler().getWidth(s.resultAsString())));
    }

    public ModuleListHud() {
        super("ModuleList");
        this.lk = null;
        this.ll = null;
    }

    @Override
    public void hK(Event<Void> event) {
        if (!checkNull() && this.enable2.get()) {
            if (this.lk == null) {
                this.sE();
                this.ll = this.lk.stream().filter(RenderSubHelperX::ajU).toList();
            }

            boolean var2 = false;

            for (RenderSubHelperX var4 : this.lk) {
                if (var4.tickUpdate()) {
                    var2 = true;
                }
            }

            if (var2) {
                this.sF();
                this.ll = this.lk.stream().filter(RenderSubHelperX::ajU).toList();
            }
        } else {
            this.lk = null;
            this.ll = null;
        }
    }

    public void sG(VDrawContext vdraw) {
        if (this.ll != null) {
            int var2 = 0;
            int var3 = this.ll.size();

            for (int var4 = 0; var4 < var3; var4++) {
                RenderSubHelperX var5 = this.ll.get(var4);
                if (var2 >= 20) {
                    this.gj(vdraw, "...%d more".formatted(this.ll.size() - var2));
                    break;
                }

                double var6 = var5.getAnimationHeight();
                if (!(var6 < 0.0) && var4 != var3 - 1) {
                    vdraw.f().translate(0.0F, (float) var6);
                    var2++;
                } else if (var5.lastState) {
                    Text var8 = var5.resultAsString();
                    this.drawText(vdraw, var8);
                    var2++;
                }
            }
        }
    }

    public void sE() {
        this.lk = new ArrayList<>();

        for (ModuleGroup var2 : HackModules.getModuleGroups()) {
            for (BaseModule var4 : var2.registered) {
                var4.getModuleEntries().map(RenderSubHelperX::new).forEach(this.lk::add);
            }
        }

        for (RenderSubHelperX var6 : this.lk) {
            if (!ChatUtils.hasTranslation(var6.Sb.getToggleKey())) {
                Debug.e("Missing translation key for", var6.Sb.getToggleKey());
            }
        }

        this.sF();
    }

    @Override
    protected ModulePath createRoot() {
        return makePath(Configs.i, "in-game-hud").add("module-list-hud");
    }
}
