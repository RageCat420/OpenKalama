package me.matl114.gui.presets.lists;

import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.ScrollableListWidget;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;

public class ListUnmodifiableWidget extends ScrollableListWidget {
    ListEntryWidgetController controller;

    protected <T extends Element & Drawable & Selectable> ContentDelegateWidget<T> wrapWidget(
            T widget, int listIndex, int startX, int startY) {
        int var5 = this.controller.b();
        int var6 = startY + var5 * listIndex;
        return new ContentDelegateWidget<T>(startX, var6, this.controller.c(), var5).setContentDelegate((T) widget);
    }

    public ListUnmodifiableWidget(ListEntryWidgetController controller, int x, int y, int dx, int dy) {
        super(x, y, dx, dy);
        this.controller = controller;
        this.refreshList();
    }

    protected void refreshList() {
        this.aU();
        int var1 = this.controller.a();

        for (int var2 = 0; var2 < var1; var2++) {
            ContentDelegateWidget var3 = this.wrapWidget(this.controller.getEntryWidget(var2), var2, 0, 0);
            this.addScrollingWidget(var3);
        }
    }
}
