package me.matl114.gui.basic;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface KalamaHelperHelperP {
    static KalamaHelperHelperP aw(KalamaHelperHelperQ handler) {
        return handler;
    }

    static KalamaHelperHelperP aC(Runnable task) {
        return new KalamaHelperHelperR(aA(task), KalamaHelperHelperM.nS);
    }

    default boolean e(ExecutableWidget widget, char chr, int modifiers) {
        return false;
    }

    default boolean b(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
        return type != KalamaHelperHelperM.nP ? false : this.a(element, mouseX, mouseY, button);
    }

    boolean a(ExecutableWidget var1, double var2, double var4, int var6);

    static KalamaHelperHelperP az(Runnable task) {
        return new KalamaHelperHelperR(aA(task), KalamaHelperHelperM.nP);
    }

    default boolean c(
            ExecutableWidget widget, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }

    static KalamaHelperHelperP ax(KalamaHelperHelperU scrollerHandler) {
        return scrollerHandler;
    }

    static KalamaHelperHelperP aB(BooleanSupplier task) {
        return (element, mouseX, mouseY, button) -> task.getAsBoolean();
    }

    static KalamaHelperHelperP ay(KalamaHelperHelperY handler) {
        return handler;
    }

    default boolean d(ExecutableWidget widget, int keyCode, int scanCode, int modifiers, boolean isPress) {
        return false;
    }

    static KalamaHelperHelperP at(Predicate<Boolean> isLeft) {
        return (element, mouseX, mouseY, button) -> {
            if (button == 0) {
                return isLeft.test(true);
            } else {
                return button == 1 ? isLeft.test(false) : false;
            }
        };
    }

    static KalamaHelperHelperP aA(Runnable task) {
        return (element, mouseX, mouseY, button) -> {
            task.run();
            return true;
        };
    }

    default KalamaHelperHelperP as(Predicate<KalamaHelperHelperP> condition) {
        return new KalamaHelperHelperAX(this, condition, this);
    }

    static KalamaHelperHelperP au(Consumer<Boolean> isLeft) {
        return (element, mouseX, mouseY, button) -> {
            if (button == 0) {
                isLeft.accept(true);
                return true;
            } else if (button == 1) {
                isLeft.accept(false);
                return true;
            } else {
                return false;
            }
        };
    }

    static KalamaHelperHelperP av(KalamaHelperHelperK handler) {
        return handler;
    }
}
