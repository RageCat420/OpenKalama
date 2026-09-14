package me.matl114.gui.presets.choices;

import java.util.List;
import me.matl114.gui.GenericBackGroundScreen;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.LabelElement;
import me.matl114.gui.elements.MultiLineTextElement;
import me.matl114.utils.ChatUtils;
import net.minecraft.text.Text;

public class QuestionScreen extends GenericBackGroundScreen {
    private Text eG;
    private static final Text QUESTION_LABEL = Text.translatable("widget.gui.question-screen.title");
    private List<KalamaHelperHelperA> eH;

    protected Runnable fY(Runnable task) {
        return () -> {
            try {
                task.run();
            } finally {
                this.close();
            }
        };
    }

    @Override
    protected List<Text> provideTitleTooltips(DrawableWidget widget) {
        return ChatUtils.parseTranslation("widget.gui.question-screen.title.tooltips", "");
    }

    public QuestionScreen(Text question, List<KalamaHelperHelperA> solutions) {
        super(QUESTION_LABEL, 240, 320);
        this.eG = question;
        this.eH = solutions;
    }

    @Override
    protected void init() {
        super.init();
        int var1 = this.backgroundWidth - 10;
        List var2 = mc.textRenderer.wrapLines(this.eG, var1);
        int var3 = Math.max(40, 10 * var2.size());
        DisplayWidget.instance(this.x + 5, this.y + 30, var1, var3)
                .<DrawableWidget>setRenderHandler(LabelElement.instance(Text.empty()))
                .addTo(this);
        DisplayWidget.instance(this.x + 5, this.y + 30, var1, var3)
                .<DrawableWidget>setRenderHandler(new MultiLineTextElement(this.eG, -1))
                .addTo(this);
        int var4 = this.eH.size();
        int var5 = var4 / 3;
        int var6 = var4 % 3;
        int var7 = this.y + this.backgroundHeight - 20 - 30 * ((var4 - 1) / 3 + 1);

        for (int var8 = 0; var8 < var5; var8++) {
            for (int var9 = 0; var9 < 3; var9++) {
                KalamaHelperHelperA var10 = this.eH.get(3 * var8 + var9);
                ExecutableWidget.instance(this.x + 5 + 80 * var9, var7 + 30 * var8, 70, 20)
                        .<ExecutableWidget>eV(new ButtonElement(
                                TextProvider.c(var10.getSolutionLabel()), ButtonAction.a(this.fY(var10::execution))))
                        .addTo(this);
            }
        }

        if (var6 != 0) {
            int var11 = this.backgroundWidth / 2 + 5 - 40 * var6;

            for (int var12 = 0; var12 < var6; var12++) {
                KalamaHelperHelperA var13 = this.eH.get(3 * var5 + var12);
                ExecutableWidget.instance(this.x + var11 + 80 * var12, var7 + 30 * var5, 70, 20)
                        .<ExecutableWidget>eV(new ButtonElement(
                                TextProvider.c(var13.getSolutionLabel()), ButtonAction.a(this.fY(var13::execution))))
                        .addTo(this);
            }
        }
    }
}
