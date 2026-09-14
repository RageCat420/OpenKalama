package me.matl114.events.impl;

public record CharTypedAction(char chr, int codepoint, int modifiers) {
    public int modifiers() {
        return this.modifiers;
    }

    public int codepoint() {
        return this.codepoint;
    }

    public char chr() {
        return this.chr;
    }
}
