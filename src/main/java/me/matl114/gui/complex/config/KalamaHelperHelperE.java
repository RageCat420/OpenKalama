package me.matl114.gui.complex.config;

import java.util.List;
import java.util.Locale;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.config.AttrKeyValue;
import net.minecraft.text.Text;

public class KalamaHelperHelperE<T> extends KalamaHelperHelperCX {
    protected int dt;
    List<Text> du;
    protected int ds;
    DrawableWidget dv;
    AttrKeyValue<T> dq;
    DrawableWidget dw;
    protected int dr;

    private String eN(String key) {
        String var2 = this.eK().getString();
        String var3 = key.toLowerCase(Locale.ROOT);
        if (var3.matches(".*(^|[.-])enable(d)?($|[.-]).*")) {
            return "启用后，" + var2 + "会生效；关闭后该功能不会执行。";
        } else if (var3.matches(".*(hotkey|keybind|key-code).*")) {
            return "设置快捷键后，可在游戏中快速切换或触发" + var2 + "，无需打开配置界面。";
        } else if (var3.matches(".*(white.?list|black.?list|filter|regex|pattern|list).*")) {
            return "填写" + var2 + "要匹配的对象或规则，只有符合条件的目标会被处理。";
        } else if (var3.matches(".*(mode|type|choice|version).*")) {
            return "选择" + var2 + "的工作模式，不同选项会改变模块的处理方式和触发条件。";
        } else if (var3.matches(".*(color|colour).*")) {
            return "设置" + var2 + "的显示颜色，便于在世界或界面中区分目标。";
        } else if (var3.matches(".*(render|display|show|visible|hud).*")) {
            return "控制" + var2 + "是否显示，以及显示哪些目标或状态信息。";
        } else {
            return var3.matches(
                            ".*(range|distance|radius|reach|height|width|size|limit|count|amount|ticks?|delay|cooldown|period|speed|rate|threshold|multiply|scale|factor).*")
                    ? "调整" + var2 + "的数值，用于控制处理范围、距离、时长、速度或触发阈值。"
                    : "控制" + var2 + "的具体行为和生效条件。";
        }
    }

    protected void eO() {}

    protected void af() {
        this.dv = this.ab().addToSub(this);
        this.dw = this.dq
                .generateValueWidget(this.dr + this.ds, 0, this.dt, this.dy)
                .addToSub(this);
    }

    public Text eK() {
        String var1 = this.eP();
        return Text.translatableWithFallback(var1, var1);
    }

    public DrawableWidget ab() {
        ButtonElement var1 = new ButtonElement(TextProvider.c(this.eK()), ButtonAction.c());
        ElementHandler var2 = var1.af(TooltipHandler.ar(this::eL));
        return DisplayWidget.instance(1, 1, this.dr - 2, this.dy - 2).setRenderHandler(var2);
    }

    public KalamaHelperHelperE<T> eJ(List<Text> tooltips) {
        this.du = tooltips;
        return this;
    }

    public String eP() {
        return this.dq.getKeyName();
    }

    public List<Text> eL() {
        if (this.du == null) {
            String var1 = this.dq.getKeyName();
            String var2 = var1 + ".tooltips";
            String var3 = ChatUtils.H(var2);
            if (eM(var3, var1)) {
                var3 = this.eN(var1);
            }

            this.du = var3 != null && !var3.isEmpty()
                    ? List.of(var3.split("\\n")).stream()
                            .<Object>map(Text::literal)
                            .map(Text.class::cast)
                            .toList()
                    : List.of(Text.literal("暂无介绍"));
        }

        return this.du;
    }

    public KalamaHelperHelperE(int x, int y, int dx, int dy, int dKey, AttrKeyValue<T> kv) {
        this(x, y, dx, dy, dKey, 0, dx - dKey, kv);
    }

    public KalamaHelperHelperE(int x, int y, int dx, int dy, int dKey, int dblank, int dvalue, AttrKeyValue<T> kv) {
        super(x, y, dx, dy);
        this.dr = dKey;
        this.ds = dblank;
        this.dt = dvalue;
        this.dq = kv;
        this.af();
    }

    private static boolean eM(String tooltip, String key) {
        if (tooltip == null || tooltip.isEmpty() || tooltip.equals("暂无介绍") || tooltip.equals(key + ".tooltips")) {
            return true;
        } else {
            return tooltip.matches("配置“.*”的相关行为。")
                    ? true
                    : tooltip.equals("启用或关闭该功能。")
                            || tooltip.equals("设置该功能的快捷键。")
                            || tooltip.equals("配置该功能的筛选列表或匹配规则。")
                            || tooltip.equals("选择该功能的工作模式或类型。")
                            || tooltip.equals("调整该功能的数值参数。")
                            || tooltip.equals("设置该功能的显示颜色。")
                            || tooltip.equals("控制该功能的显示效果。")
                            || tooltip.equals("控制该功能的行为和触发条件。")
                            || tooltip.contains("控制 tooltips")
                            || tooltip.contains("调整 tooltips")
                            || tooltip.contains("启用后，tooltips");
        }
    }
}
