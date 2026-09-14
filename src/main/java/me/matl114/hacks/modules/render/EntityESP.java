package me.matl114.hacks.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.matl114.accessors.hacks.EntityInternalAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.EntryPrimitiveMap;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.hacks.utils.render.RenderMode;
import me.matl114.managers.Configs;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.render.RenderCollector;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

public class EntityESP extends BaseModule {
    public final NBTRef<EntryPrimitiveMap<EntityType<?>, Boolean>> traceOption;
    public final NBTRef<EntryPrimitiveMap<EntityType<?>, TextColor>> color;
    List<Entity> HI;
    public final NBTRef<EntryPrimitiveMap<EntityType<?>, Boolean>> highlightOption;
    public final ModulePath HC = makePath(Configs.i, "detect-entity");
    public final EnumRef<RenderMode> renderMode;
    public final NBTRef<EntrySet<EntityType<?>>> cN;
    public final KeyBindRef pv;
    public final ModulePath HD = this.HC.add("entity-esp");
    public final NBTRef<EntryPrimitiveMap<EntityType<?>, Boolean>> boxingOption;
    public final FlagRef ae = this.flagBuilder(this.HD.add("enable")).build();

    private Color getShaderColorByEntityType(Entity entity) {
        EntityType var2 = entity.getType();
        TextColor var3 = this.color.get().uJ(var2);
        return var3 != null ? new Color(var3.getRgb()) : null;
    }

    public void render(Object object, float tickDelta) {
        EntryPrimitiveMap<EntityType<?>, Boolean> var3 = this.traceOption.get();
        EntryPrimitiveMap<EntityType<?>, Boolean> var4 = this.boxingOption.get();
        RenderCollector var5 = RenderCollectors.createBoxCollector(true, false, false);
        RenderCollector var6 = RenderCollectors.d();

        for (Entity var9 : this.HI) {
            Color var10 = this.getShaderColorByEntityType(var9);
            if (var10 != null) {
                Box var11 = RenderUtils.getLerpedBox(var9, tickDelta);
                if (var4.getEntryValueOr(var9.getType(), false)) {
                    var5.submit(var11, var10.getRGB());
                }

                if (var3.getEntryValueOr(var9.getType(), false)) {
                    var6.submit(var11.getCenter(), var10.getRGB());
                }
            }
        }

        if (object instanceof MatrixStack var12) {
            var5.a(var12);
            var6.a(var12);
        } else if (object instanceof VDrawContext var13) {
            var5.b(var13);
            var6.b(var13);
        }

        var5.clear();
        var6.clear();
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(RenderListener.q(), this::Gn);
        this.registerListener(RenderListener.r(), this::sg);
        this.registerListener(Listener.V(), this::onTick);
    }

    public void onTick(Event<ClientPlayerEntity> event) {
        this.HI = new ArrayList<>();
        boolean var2 = this.ae.get();
        Set<EntityType<?>> var3 = this.cN.get().set();
        EntryPrimitiveMap<EntityType<?>, Boolean> var4 = this.highlightOption.get();

        for (Entity var6 : mc.world.getEntities()) {
            if (var6 != mc.gameRenderer.getCamera().getFocusedEntity() && var6 != null && !var6.isRemoved()) {
                EntityInternalAccess var7 = EntityInternalAccess.of(var6);
                byte var8 = var7.renderTrackedLevel();
                if (!var4.getEntryValueOr(var6.getType(), false)) {
                    var7.setGlow0(false);
                }

                if (var8 == 1 && !var3.contains(var6.getType())) {
                    var7.setGlow0(false);
                    var7.markRenderTracked((byte) 0);
                } else {
                    if (var8 == 1 && var2 || var8 == 2) {
                        if (var4.getEntryValueOr(var6.getType(), false) && !var6.isGlowing()) {
                            var7.setGlow0(true);
                        }

                        this.HI.add(var6);
                    }

                    if (var8 == 0 && var3.contains(var6.getType())) {
                        var7.markRenderTracked((byte) 1);
                    }
                }
            }
        }
    }

    public EntityESP() {
        super("EntityESP");
        this.pv = this.moduleEntry(
                        Configs.i,
                        this.HD.add("hotkey").toPath(),
                        new MultiKeyBind(),
                        this.HD.add("enable").toPath())
                .build();
        this.renderMode = this.builder(this.HD.add("render-mode"), RenderMode.class)
                .defaultValue(RenderMode.RENDER_3D)
                .build();
        this.cN = this.builder(Configs.i, EntrySet.<EntityType<?>>parameter())
                .path(this.HD.add("whitelist").toPath())
                .defaultValue(new EntrySet<EntityType<?>>(new Regex("player,wither"), Registries.ENTITY_TYPE))
                .build();
        this.color = this.builder(
                        this.HD.add("color"),
                        NBTType.<EntryPrimitiveMap<EntityType<?>, TextColor>>parameter(EntryPrimitiveMap.class))
                .defaultValue(new EntryPrimitiveMap<EntityType<?>, TextColor>(
                        Registries.ENTITY_TYPE,
                        NBTTypes.h,
                        Map.of(
                                EntityType.PLAYER,
                                Objects.requireNonNull(TextColor.fromFormatting(Formatting.YELLOW)),
                                EntityType.ARMOR_STAND,
                                Objects.requireNonNull(TextColor.fromFormatting(Formatting.GREEN))),
                        TextColor.fromFormatting(Formatting.RED)))
                .build();
        this.boxingOption = this.builder(
                        this.HD.add("boxing-option"),
                        NBTType.<EntryPrimitiveMap<EntityType<?>, Boolean>>parameter(EntryPrimitiveMap.class))
                .defaultValue(new EntryPrimitiveMap<>(
                        Registries.ENTITY_TYPE,
                        NBTTypes.f,
                        Map.of(EntityType.PLAYER, true, EntityType.END_CRYSTAL, true, EntityType.WITHER, true),
                        false))
                .build();
        this.traceOption = this.builder(
                        this.HD.add("trace-option"),
                        NBTType.<EntryPrimitiveMap<EntityType<?>, Boolean>>parameter(EntryPrimitiveMap.class))
                .defaultValue(new EntryPrimitiveMap<>(
                        Registries.ENTITY_TYPE,
                        NBTTypes.f,
                        Map.of(EntityType.PLAYER, true, EntityType.END_CRYSTAL, false, EntityType.WITHER, true),
                        false))
                .build();
        this.highlightOption = this.builder(
                        this.HD.add("highlight-option"),
                        NBTType.<EntryPrimitiveMap<EntityType<?>, Boolean>>parameter(EntryPrimitiveMap.class))
                .defaultValue(new EntryPrimitiveMap<>(
                        Registries.ENTITY_TYPE,
                        NBTTypes.f,
                        Map.of(EntityType.PLAYER, true, EntityType.END_CRYSTAL, true, EntityType.WITHER, true),
                        true))
                .build();
        this.HI = new ArrayList<>();
        this.bindFlag(this.ae);
    }

    public void sg(Event<VDrawContext> event) {
        if (!checkNull()) {
            if (this.ae.get() && this.renderMode.get().isIn(new ConfigEnum[] {RenderMode.RENDER_2D})) {
                float var2 = event.<Float>getArgs(0);
                this.render(event.b, var2);
            }
        }
    }

    public void Gn(Event<MatrixStack> stackE) {
        if (!checkNull()) {
            if (this.ae.get() && this.renderMode.get().isIn(new ConfigEnum[] {RenderMode.RENDER_3D})) {
                MatrixStack var2 = (MatrixStack) stackE.b;
                float var3 = stackE.<Float>getArgs(0);
                RenderUtils.startDrawVirtual(var2);

                try {
                    this.render(var2, var3);
                } finally {
                    RenderUtils.stopDrawVirtual(var2);
                }
            }
        }
    }
}
