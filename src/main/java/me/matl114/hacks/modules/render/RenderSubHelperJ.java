package me.matl114.hacks.modules.render;

import me.matl114.hacks.modules.mine.MineSubHelperH;

record RenderSubHelperJ(RenderSubHelperQX state, MineSubHelperH tracker, int progressPercentage) {

    public int UG() {
        return this.progressPercentage;
    }

    public MineSubHelperH UF() {
        return this.tracker;
    }

    public RenderSubHelperQX UE() {
        return this.state;
    }
}
