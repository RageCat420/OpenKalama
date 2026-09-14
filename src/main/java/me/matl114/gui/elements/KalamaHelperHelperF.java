package me.matl114.gui.elements;

import me.matl114.accessors.gui.TextFieldAccess;
import me.matl114.gui.basic.KalamaHelperHelperIX;
import me.matl114.utils.config.PropertyTracker;

class KalamaHelperHelperF implements TextFieldAccess {
    private final TextFieldElement this$0;

    @Override
    public boolean canStartDrag(double mouseX, double mouseY) {
        return this.this$0.canStartDrag(mouseX, mouseY);
    }

    @Override
    public void dragSelect(int deltaX, int deltaY, boolean shiftDownAction) {
        this.this$0.dragSelect(deltaX, deltaY, shiftDownAction);
    }

    @Override
    public void setBorderColorProvider(KalamaHelperHelperIX provider) {
        this.this$0.bg(provider);
    }

    @Override
    public void resetSelect() {
        this.this$0.bv();
    }

    KalamaHelperHelperF(final TextFieldElement this$0) {
        this.this$0 = this$0;
    }

    @Override
    public void setListener(PropertyTracker<TextFieldAccess, String> tracker) {
        this.this$0.bf(tracker);
    }
}
