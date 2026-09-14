package me.matl114.gui.elements;

import java.util.function.Predicate;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class IconElement$SimpleIconElement extends IconElement {
    private Identifier bo;
    private boolean bk = true;
    private Identifier bn;
    private Predicate<IconElement> bl = null;
    private boolean bm;

    @Override
    public IconElement setActive(boolean active) {
        this.bk = active;
        return this;
    }

    public IconElement$SimpleIconElement cy(boolean guiTexture) {
        this.bm = guiTexture;
        return this;
    }

    public IconElement$SimpleIconElement cw(Predicate<IconElement> activePredicate) {
        this.bl = activePredicate;
        return this;
    }

    public IconElement$SimpleIconElement cC(Identifier activeId) {
        this.bo = activeId;
        return this;
    }

    public IconElement$SimpleIconElement cA(Identifier inactiveId) {
        this.bn = inactiveId;
        return this;
    }

    @Override
    public boolean bD() {
        return this.bl == null ? this.bk : this.bl.test(this);
    }

    public Identifier cz() {
        return this.bn;
    }

    @Override
    public boolean cq() {
        return this.bm;
    }

    public boolean cx() {
        return this.bm;
    }

    public Identifier cB() {
        return this.bo;
    }

    @Nullable
    @Override
    public Identifier getTextureId(VDrawContext context, DrawableWidget element, boolean highlight) {
        return this.bD() ? this.bo : this.bn;
    }

    public Predicate<IconElement> cv() {
        return this.bl;
    }

    public IconElement$SimpleIconElement(Identifier inactiveId, Identifier activeId, boolean gui, ButtonAction action) {
        super(action);
        this.bn = inactiveId;
        this.bo = activeId;
        this.bm = gui;
    }
}
