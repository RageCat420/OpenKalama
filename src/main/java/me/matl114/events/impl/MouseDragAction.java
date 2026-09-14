package me.matl114.events.impl;

import net.minecraft.client.Mouse;

public record MouseDragAction(Mouse mouse, double mouseX, double mouseY, double deltaX, double deltaY) {
    public double deltaX() {
        return this.deltaX;
    }

    public double mouseX() {
        return this.mouseX;
    }

    public Mouse mouse() {
        return this.mouse;
    }

    public double deltaY() {
        return this.deltaY;
    }

    public double mouseY() {
        return this.mouseY;
    }
}
