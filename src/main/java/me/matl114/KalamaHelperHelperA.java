package me.matl114;

import me.matl114.events.RenderListener;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.Debug;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

class KalamaHelperHelperA implements SimpleSynchronousResourceReloadListener {
    public void reload(ResourceManager manager) {
        Debug.a("Resource reload called for Kalama");
        SlimefunHelper.reloadModConfig();
        RenderListener.k(manager);
    }

    KalamaHelperHelperA(final SlimefunHelper this$0) {}

    public Identifier getFabricId() {
        return CommonUtils.b("reload_listener");
    }
}
