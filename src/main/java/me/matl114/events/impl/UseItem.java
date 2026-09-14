package me.matl114.events.impl;

import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class UseItem {
    final Hand hand;
    ActionResult a;

    @Override
    public String toString() {
        return "UseItem(actionResult=" + this.b() + ", hand=" + this.c() + ")";
    }

    public ActionResult b() {
        return this.a;
    }

    public UseItem actionResult(ActionResult actionResult) {
        this.a = actionResult;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof UseItem var2)) {
            return false;
        } else if (!var2.a(this)) {
            return false;
        } else {
            ActionResult var3 = this.b();
            ActionResult var4 = var2.b();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
                Hand var5 = this.c();
                Hand var6 = var2.c();
                return var5 == null ? var6 == null : var5.equals(var6);
            } else {
                return false;
            }
        }
    }

    public UseItem(ActionResult actionResult, Hand hand) {
        this.a = actionResult;
        this.hand = hand;
    }

    public Hand c() {
        return this.hand;
    }

    protected boolean a(Object other) {
        return other instanceof UseItem;
    }

    @Override
    public int hashCode() {
        byte var1 = 59;
        int var2 = 1;
        ActionResult var3 = this.b();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        Hand var4 = this.c();
        return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
    }
}
