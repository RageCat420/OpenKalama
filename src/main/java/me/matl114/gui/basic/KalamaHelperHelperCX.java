package me.matl114.gui.basic;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import me.matl114.utils.collections.UnmodifiableListMappingIterator;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.apache.commons.compress.utils.Lists;

public class KalamaHelperHelperCX extends DrawableWidget implements SubSelectable {
    protected DrawableWidget C;

    @Deprecated
    protected int D;

    private final List<me.matl114.utils.collections.KalamaHelperHelperK<DrawableWidget>> A;
    private final List<me.matl114.utils.collections.KalamaHelperHelperK<DrawableWidget>> z = Lists.newArrayList();
    protected DrawableWidget B;
    private int E;

    public void S() {
        int var1 = 0;
        int var2 = 0;

        for (DrawableWidget var4 : this.P()) {
            var1 = Math.max(var1, var4.getX() + var4.getWidth());
            var2 = Math.max(var2, var4.getY() + var4.getHeight());
        }

        this.setWidth(var1);
        this.setHeight(var2);
    }

    public Iterable<DrawableWidget> P() {
        return () ->
                new UnmodifiableListMappingIterator<>(this.A, me.matl114.utils.collections.KalamaHelperHelperK::val);
    }

    @Override
    public void renderInDefaultMatrix(
            VDrawContext context, int mouseX, int mouseY, float delta, boolean disableSelect) {
        super.renderInDefaultMatrix(context, mouseX, mouseY, delta, disableSelect);
        int var6 = mouseX - this.getX();
        int var7 = mouseY - this.getY();
        float var8 = this.getTextureScale();
        if (var8 != 1.0F) {
            var6 = (int) (var6 / var8);
            var7 = (int) (var7 / var8);
        }

        DrawableWidget var9 = null;
        if (this.isSelected()) {
            for (DrawableWidget var11 : this.P()) {
                if (var11.canSelect() && var11.isMouseOver(var6, var7)) {
                    var9 = var11;
                    break;
                }
            }
        }

        for (DrawableWidget var14 : this.O()) {
            boolean var12 = var14 != var9;
            var14.render0(context, var6, var7, delta, var12);
        }
    }

    @Deprecated
    public void T(int basicDepth) {
        this.D = basicDepth;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        double var9 = mouseX - this.getX();
        double var11 = mouseY - this.getY();
        float var13 = this.getTextureScale();
        if (var13 != 1.0F) {
            var9 = (int) (var9 / var13);
            var11 = (int) (var11 / var13);
        }

        for (DrawableWidget var15 : this.P()) {
            if (var15.mouseScrolled(var9, var11, horizontalAmount, verticalAmount)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void releaseDrag(Screen screen, double mouseX, double mouseY) {
        if (this.C != null) {
            double var6 = mouseX - this.getX();
            double var8 = mouseY - this.getY();
            float var10 = this.getTextureScale();
            if (var10 != 1.0F) {
                var6 = (int) (var6 / var10);
                var8 = (int) (var8 / var10);
            }

            this.C.releaseDrag(screen, var6, var8);
        }
    }

    public KalamaHelperHelperCX Q(DrawableWidget widget) {
        this.M(widget);
        widget.setSubWidget(true);
        return this;
    }

    @Override
    public boolean isDragging() {
        return this.C != null && this.C.isDragging();
    }

    public Iterable<DrawableWidget> O() {
        return () ->
                new UnmodifiableListMappingIterator<>(this.z, me.matl114.utils.collections.KalamaHelperHelperK::val);
    }

    private boolean N(DrawableWidget child) {
        this.z.removeIf(s -> Objects.equals(s.val(), child));
        return this.A.removeIf(s -> Objects.equals(s.val(), child));
    }

    public boolean R(DrawableWidget widget) {
        widget.setSubWidget(false);
        return this.N(widget);
    }

    public static KalamaHelperHelperCX H(int x, int y, int dx, int dy) {
        return new KalamaHelperHelperCX(x, y, dx, dy);
    }

    public KalamaHelperHelperCX(int x, int y, int dx, int dy) {
        super(x, y, dx, dy);
        this.A = Lists.newArrayList();
        this.B = null;
        this.C = null;
        this.D = 0;
        this.E = 0;
    }

    @Override
    public boolean canSelect() {
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (DrawableWidget var5 : this.P()) {
            if (var5.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        return false;
    }

    private void K() {
        this.z.sort(Comparator.<me.matl114.utils.collections.KalamaHelperHelperK<DrawableWidget>>comparingInt(
                        s -> s.val().priority)
                .thenComparingInt(me.matl114.utils.collections.KalamaHelperHelperK::index));
        this.A.sort(Comparator.<me.matl114.utils.collections.KalamaHelperHelperK<DrawableWidget>>comparingInt(
                        s -> -s.val().priority)
                .thenComparingInt(me.matl114.utils.collections.KalamaHelperHelperK::index));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (DrawableWidget var5 : this.P()) {
            if (var5.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double var6 = mouseX - this.getX();
        double var8 = mouseY - this.getY();
        float var10 = this.getTextureScale();
        if (var10 != 1.0F) {
            var6 = (int) (var6 / var10);
            var8 = (int) (var8 / var10);
        }

        for (DrawableWidget var12 : this.P()) {
            if (var12.mouseClicked(var6, var8, button)) {
                this.setSelected(var12);
                return true;
            }
        }

        this.setSelected(null);
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        float var10 = this.getTextureScale();
        return this.C != null
                && this.C.isDragging()
                && this.C.mouseDragged(
                        (mouseX - this.getX()) / var10,
                        (mouseY - this.getY()) / var10,
                        button,
                        deltaX / var10,
                        deltaY / var10);
    }

    @Override
    public boolean isFocused() {
        return this.B != null && this.B.isFocused();
    }

    @Deprecated
    public int U() {
        return this.D;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (DrawableWidget var4 : this.P()) {
            if (var4.charTyped(chr, modifiers)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (this.B != null) {
            this.B.setFocused(focused);
        }
    }

    @Override
    public boolean startDrag(Screen screen, double mouseX, double mouseY) {
        double var6 = mouseX - this.getX();
        double var8 = mouseY - this.getY();
        float var10 = this.getTextureScale();
        if (var10 != 1.0F) {
            var6 = (int) (var6 / var10);
            var8 = (int) (var8 / var10);
        }

        for (DrawableWidget var12 : this.P()) {
            if (var12.startDrag(screen, var6, var8)) {
                this.C = var12;
                return true;
            }
        }

        return false;
    }

    @Override
    public DrawableWidget getSelected() {
        return this.B;
    }

    public void L() {
        this.z.clear();
        this.A.clear();
    }

    @Override
    public <T extends SubSelectable> T setSelected(DrawableWidget subWidget) {
        if (this.B != null) {
            this.B.setFocused(false);
        }

        this.B = subWidget;
        if (this.B != null && super.isFocused()) {
            this.B.setFocused(true);
        }

        return (T) (Object) this;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        double var6 = mouseX - this.getX();
        double var8 = mouseY - this.getY();
        float var10 = this.getTextureScale();
        if (var10 != 1.0F) {
            var6 = (int) (var6 / var10);
            var8 = (int) (var8 / var10);
        }

        for (DrawableWidget var12 : this.P()) {
            if (var12.mouseReleased(var6, var8, button)) {
                return true;
            }
        }

        return false;
    }

    private void M(DrawableWidget child) {
        me.matl114.utils.collections.KalamaHelperHelperK var2 =
                new me.matl114.utils.collections.KalamaHelperHelperK<>(++this.E, child);
        this.z.add(var2);
        this.A.add(var2);
        this.K();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        float var5 = this.getTextureScale();

        for (DrawableWidget var7 : this.P()) {
            if (var7.isMouseOver((mouseX - this.getX()) / var5, (mouseY - this.getY()) / var5)) {
                return true;
            }
        }

        return false;
    }
}
