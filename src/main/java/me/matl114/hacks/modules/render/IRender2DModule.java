package me.matl114.hacks.modules.render;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.WidgetPos;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.versioned.api.VDrawContext;

public abstract class IRender2DModule extends BaseModule {
    public FlagRef right2;
    public NBTRef<WidgetPos> pos2;
    public FlagRef enable2;
    public final ModulePath he = this.createRoot();
    public KeyBindRef hotkey2;
    public static final float HEIGHT = 9.0F;

    public void hK(Event<Void> var1) {}

    public void render2D(VDrawContext var1, float var2) {}

    public IRender2DModule(String name) {
        super(name);
        this.enable2 = this.flagBuilder(this.he.add("enable")).build();
        this.hotkey2 = this.moduleEntry(this.he.add("hotkey"), new MultiKeyBind(), this.he.add("enable"))
                .build();
        this.hJ();
        this.right2 = this.flagBuilder(this.he.add("right")).build();
        this.pos2 = this.builder(this.he.add("pos"), WidgetPos.class)
                .defaultValue(new WidgetPos(0, 0.0, 0.0, 0, 0))
                .build();
        this.bindFlag(this.enable2);
    }

    protected abstract ModulePath createRoot();

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(RenderListener.r(), this::B);
        this.registerListener(Listener.T(), this::hK);
    }

    public void B(Event<VDrawContext> event) {
        if (!checkNull()) {
            if (this.enable2.get() && !event.<Boolean>getArgs(1)) {
                VDrawContext var2 = (VDrawContext) event.b;
                var2.b();

                try {
                    this.handleRenderPosition(var2);
                    this.render2D(var2, event.<Float>getArgs(0));
                } finally {
                    var2.c();
                }
            }
        }
    }

    public IRender2DModule() {
        super("IRender2DModule");
        this.enable2 = this.flagBuilder(this.he.add("enable")).build();
        this.hotkey2 = this.moduleEntry(this.he.add("hotkey"), new MultiKeyBind(), this.he.add("enable"))
                .build();
        this.hJ();
        this.right2 = this.flagBuilder(this.he.add("right")).build();
        this.pos2 = this.builder(this.he.add("pos"), WidgetPos.class)
                .defaultValue(new WidgetPos(0, 0.0, 0.0, 0, 0))
                .build();
        this.bindFlag(this.enable2);
    }

    protected void hJ() {}

    public void handleRenderPosition(VDrawContext vdraw) {
        int var2 = mc.getWindow().getScaledWidth();
        WidgetPos var3 = this.pos2.get();
        int var4 = var3.getWindowX(mc.getWindow());
        int var5 = var3.getWindowY(mc.getWindow());
        vdraw.f().translate(var4, var5);
    }
}
