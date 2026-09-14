package me.matl114.gui.complex.task;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.presets.index.IndexedScreen;
import me.matl114.gui.presets.lists.ListEntryWidgetController;
import me.matl114.gui.presets.lists.ListUnmodifiableWidget;
import me.matl114.managers.task.TaskManager;
import me.matl114.utils.CollectionUtils;
import net.minecraft.text.Text;

public class TaskManageScreen extends IndexedScreen<Pair<String, TaskManager>, ListUnmodifiableWidget> {
    protected Map<String, TaskManager> tasks;
    private static String selectingTaskManager;

    @Override
    protected ElementHandler be(Pair<String, TaskManager> val) {
        return new ButtonElement(
                        TextProvider.c(Text.literal((String) val.getFirst())),
                        ButtonAction.a(() -> this.setGlobal(val)))
                .cA(ButtonElement.bH)
                .cC(ButtonElement.bI)
                .cw(el -> Objects.equals(selectingTaskManager, val.getFirst()));
    }

    public Pair<String, TaskManager> getGlobal() {
        return Pair.of(selectingTaskManager, this.tasks.get(selectingTaskManager));
    }

    public TaskManageScreen(Map<String, TaskManager> list, int backgroundWidth, int backgroundHeight) {
        super(list.entrySet().stream().map(CollectionUtils::entryToPair).toList(), backgroundWidth, backgroundHeight);
    }

    @Override
    protected ListUnmodifiableWidget bd(Pair<String, TaskManager> val) {
        ListEntryWidgetController var2 = ListEntryWidgetController.immutable(
                ((TaskManager) val.getSecond())
                        .b().entrySet().stream()
                                .map(CollectionUtils::entryToPair)
                                .toList(),
                pair -> new KalamaHelperHelperCX(0, 0, 0, 0),
                this.ax,
                this.backgroundWidth - this.au - 20);
        return new ListUnmodifiableWidget(var2, 0, 0, this.backgroundWidth - this.au - 20, this.backgroundHeight);
    }

    public void saveSelected() {}

    public void setGlobal(Pair<String, TaskManager> config) {
        selectingTaskManager = (String) config.getFirst();
    }
}
