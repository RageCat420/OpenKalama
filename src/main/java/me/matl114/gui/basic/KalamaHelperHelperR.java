package me.matl114.gui.basic;

public class KalamaHelperHelperR implements KalamaHelperHelperP {
    KalamaHelperHelperM Z;
    KalamaHelperHelperP Y;

    public KalamaHelperHelperR(KalamaHelperHelperP handler, KalamaHelperHelperM type) {
        this.Y = handler;
        this.Z = type;
    }

    public boolean a(ExecutableWidget element, double mouseX, double mouseY, int button) {
        throw new IllegalStateException();
    }

    public boolean b(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
        return type == this.Z ? this.Y.a(element, mouseX, mouseY, button) : false;
    }
}
