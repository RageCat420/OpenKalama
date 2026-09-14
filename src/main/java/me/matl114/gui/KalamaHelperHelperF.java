package me.matl114.gui;

import java.util.function.IntConsumer;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperM;
import me.matl114.gui.basic.KalamaHelperHelperP;

class KalamaHelperHelperF implements KalamaHelperHelperP {
    public boolean b(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
        if (type == KalamaHelperHelperM.nR) {
            return element.isMouseOver(mouseX, mouseY);
        } else {
            return type == KalamaHelperHelperM.nS
                    ? this.a(element, mouseX, mouseY, button)
                    : KalamaHelperHelperP.super.b(element, mouseX, mouseY, button, type);
        }
    }

    KalamaHelperHelperF(IntConsumer var1, IntConsumer var2) {
        this.u = var1;
        this.v = var2;
    }

    public boolean a(ExecutableWidget element, double mouseX, double mouseY, int button) {
        boolean var7 = false;
        if (mouseX >= element.getX() && mouseX <= element.getX() + element.getWidth()) {
            this.u.accept((int) mouseX - element.getX());
            var7 = true;
        }

        if (mouseY >= element.getY() && mouseY <= element.getY() + element.getHeight()) {
            this.v.accept((int) mouseY - element.getY());
            var7 = true;
        }

        return var7;
    }

    IntConsumer u;
    IntConsumer v;
}
