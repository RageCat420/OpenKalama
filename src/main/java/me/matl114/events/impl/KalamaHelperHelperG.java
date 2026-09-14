package me.matl114.events.impl;

import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;

public record KalamaHelperHelperG(
        RecipeBookProvider provider, RecipeBookWidget recipeBookWidget, ButtonWidget toggleWidget) {
    public ButtonWidget toggleWidget() {
        return this.toggleWidget;
    }

    public RecipeBookWidget recipeBookWidget() {
        return this.recipeBookWidget;
    }

    public RecipeBookProvider provider() {
        return this.provider;
    }
}
