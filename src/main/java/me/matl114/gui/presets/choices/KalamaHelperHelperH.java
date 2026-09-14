package me.matl114.gui.presets.choices;

import net.minecraft.text.Text;

class KalamaHelperHelperH extends KalamaHelperHelperA {
    final Runnable val$task;

    @Override
    public void execution() {
        this.val$task.run();
    }

    KalamaHelperHelperH(Text var1, Runnable var2) {
        super(var1);
        this.val$task = var2;
    }
}
