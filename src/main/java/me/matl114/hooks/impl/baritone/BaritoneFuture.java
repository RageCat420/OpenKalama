package me.matl114.hooks.impl.baritone;

import java.util.ArrayList;
import java.util.List;

public class BaritoneFuture {
    final List<Runnable> onCompleteFutures = new ArrayList<>();
    final List<Runnable> onCancelFutures = new ArrayList<>();

    public void onComplete() {
        this.onCompleteFutures.forEach(Runnable::run);
    }

    public void onCancel() {
        this.onCancelFutures.forEach(Runnable::run);
    }

    public List<Runnable> getOnCompleteFutures() {
        return this.onCompleteFutures;
    }

    public List<Runnable> getOnCancelFutures() {
        return this.onCancelFutures;
    }
}
