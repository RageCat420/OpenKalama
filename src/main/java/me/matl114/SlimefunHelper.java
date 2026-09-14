package me.matl114;

import com.google.common.base.Preconditions;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import me.matl114.bridge.BridgeMain;
import me.matl114.commands.MainCommand;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.gui.GuiMain;
import me.matl114.hacks.MainTasks;
import me.matl114.jsApi.KalamaHelperHelperG;
import me.matl114.managers.Configs;
import me.matl114.managers.TaskManagers;
import me.matl114.managers.Tasks;
import me.matl114.utils.Debug;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingPluginManager;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.resource.ResourceType;

public class SlimefunHelper implements ModInitializer, PreLaunchEntrypoint {
    public static Set<String> DEV_NAME =
            Set.of("matl114", "matl_test", "matl_test2", "mtl", "||matl_test", "||matl_test2", "||mtl");
    public static final String MOD_ID = "kalama";
    public static boolean DEV_ENV = false;
    public static SlimefunHelper instance;
    public static ModContainer modContainer;

    public static SlimefunHelper getInstance() {
        return instance;
    }

    public static ModContainer getModContainer() {
        return modContainer;
    }

    public void onPreLaunch() {}

    public static void initializeEnvironment() {
        for (EntrypointContainer<ModInitializer> re :
                FabricLoaderImpl.INSTANCE.getEntrypointContainers("main", ModInitializer.class)) {
            if (re.getEntrypoint() == instance) {
                modContainer = re.getProvider();
                break;
            }
        }

        Preconditions.checkNotNull(modContainer);
    }

    public void onInitialize() {
        instance = this;
        authentication();
        initializeEnvironment();
        Debug.a("Kalama, start!");
        Debug.a("Kalama start loading!");
        reloadModConfig();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new KalamaHelperHelperA(this));
        ModelLoadingPluginManager.registerPlugin(
                (resourceManager, executor) -> CompletableFuture.supplyAsync(() -> {
                    Debug.a("check model plugin work");
                    reloadModConfig();
                    return RenderListener.l(resourceManager);
                }),
                (data, pluginContext) -> pluginContext.addModels(data));
        TaskManagers.init();
        Tasks.a();
        Listener.init();
        RenderListener.init();
        MainCommand.aE();
        GuiMain.init();
        MainTasks.a();
        BridgeMain.init();
        KalamaHelperHelperG.d();
        Debug.a("Kalama loading finish");
    }

    public static void authentication() {
        try {
            Class.forName("net.minecraft.client.MinecraftClient");
            DEV_ENV = true;
            Debug.a("Dev Environment Detected !");
        } catch (Throwable var1) {
        }
    }

    public static void reloadModConfig() {
        Debug.a("Reloading Mod Config");
        Configs.loadConfigs();
    }
}
