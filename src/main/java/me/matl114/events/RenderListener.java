package me.matl114.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import me.matl114.events.channels.EventChannel;
import me.matl114.events.model.GuiModel;
import me.matl114.utils.Debug;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class RenderListener {
    private static final EventChannel<Particle> q = new EventChannel<>();
    private static Matrix4f r = new Matrix4f().identity();
    private static final EventChannel<DrawContext> j = new EventChannel<>();
    private static final EventChannel<BlockEntity> p = new EventChannel<>();
    private static final EventChannel<Float> u = new EventChannel<>();
    private static final EventChannel<List<GuiModel>> e = new EventChannel<>();
    private static Matrix4f t = new Matrix4f().identity();
    private static final EventChannel<ItemStack> b = new EventChannel<>();
    private static final MinecraftClient a = MinecraftClient.getInstance();
    private static final EventChannel<Set<Identifier>> m = new EventChannel<>();
    private static final EventChannel<BakedModel> c = new EventChannel<>();
    private static final EventChannel<ResourceManager> k = new EventChannel<>();
    private static final EventChannel<Entity> o = new EventChannel<>();
    private static final EventChannel<Set<Identifier>> l = new EventChannel<>();
    private static Matrix4f s = new Matrix4f().identity();
    public static final String d = "fabric_resource";
    private static final EventChannel<MatrixStack> g = new EventChannel<>();
    private static final EventChannel<List<Text>> n = new EventChannel<>();
    private static final EventChannel<MatrixStack> f = new EventChannel<>();
    private static final EventChannel<VDrawContext> h = new EventChannel<>();
    private static final EventChannel<DrawContext> i = new EventChannel<>();

    public static EventChannel<ItemStack> m() {
        return b;
    }

    public static EventChannel<Set<Identifier>> w() {
        return m;
    }

    public static EventChannel<ResourceManager> u() {
        return k;
    }

    public static BakedModel getCustomModelOf(Identifier identifier) {
        BakedModel var1 = a.getBakedModelManager().getModel(identifier);
        return var1 != null && var1 != a.getBakedModelManager().getMissingModel() ? var1 : null;
    }

    public static EventChannel<Entity> y() {
        return o;
    }

    public static EventChannel<BlockEntity> z() {
        return p;
    }

    public static EventChannel<VDrawContext> r() {
        return h;
    }

    public static void k(ResourceManager manager) {
        Event var1 = new Event<>(manager, false, false);
        k.catchEvent(var1);
    }

    public static Matrix4f D() {
        return s;
    }

    public static EventChannel<Float> H() {
        return u;
    }

    public static BakedModel getModelOf(ModelIdentifier modeled) {
        BakedModel var1 = a.getBakedModelManager().getModel(modeled);
        return var1 != null && var1 != a.getBakedModelManager().getMissingModel()
                ? var1
                : getCustomModelOf(modeled.id());
    }

    public static EventChannel<Particle> A() {
        return q;
    }

    public static Optional<BakedModel> d(ModelIdentifier id) {
        return Optional.ofNullable(getModelOf(id));
    }

    public static List<GuiModel> g(ItemStack stack) {
        Event var1 = new Event<>(new ArrayList(), true, false, stack);
        e.catchEvent(var1);
        return var1.d() ? null : (List) var1.e();
    }

    public static EventChannel<DrawContext> s() {
        return i;
    }

    public static EventChannel<List<GuiModel>> o() {
        return e;
    }

    public static EventChannel<MatrixStack> p() {
        return f;
    }

    public static Optional<BakedModel> c(Identifier id) {
        return Optional.ofNullable(getModelOf(b(id)));
    }

    public static Collection<Identifier> l(ResourceManager manager) {
        Event var1 = new Event<>(new LinkedHashSet(), false, false, manager);
        l.catchEvent(var1);
        return (Collection<Identifier>) var1.e();
    }

    public static Matrix4f F() {
        return t;
    }

    public static void renderWorldTasks(MatrixStack stack, float tickDelta) {
        GL11.glEnable(2848);

        try {
            Event var2 = new Event<>(stack, false, false, tickDelta);
            g.catchEvent(var2);
        } catch (NullPointerException | CrashException | ConcurrentModificationException var6) {
            Debug.e("Error while handling Render Event:", var6.getMessage());
        } finally {
            GL11.glDisable(2848);
        }
    }

    public static ModelIdentifier b(Identifier id) {
        return new ModelIdentifier(id, "fabric_resource");
    }

    public static Matrix4f B() {
        return r;
    }

    public static void C(Matrix4f worldModelViewMatrix) {
        r = worldModelViewMatrix;
    }

    public static EventChannel<List<Text>> x() {
        return n;
    }

    public static void renderHandledScreen(
            DrawContext context, HandledScreen<?> screen, int mouseX, int mouseY, float delta) {
        if (!j.d()) {
            Event var5 = new Event<>(context, false, false, screen, mouseX, mouseY, delta);
            j.catchEvent(var5);
        }
    }

    public static void E(Matrix4f worldBasicProjectionMatrix) {
        s = worldBasicProjectionMatrix;
    }

    public static void renderSlotInScreen(DrawContext context, HandledScreen<?> renderer, Slot stack) {
        if (!i.d()) {
            Event var3 = new Event<>(context, false, false, renderer, stack);
            i.catchEvent(var3);
        }
    }

    public static EventChannel<DrawContext> t() {
        return j;
    }

    public static EventChannel<BakedModel> n() {
        return c;
    }

    public static void G(Matrix4f worldProjectionMatrix) {
        t = worldProjectionMatrix;
    }

    public static void init() {}

    public static EventChannel<Set<Identifier>> v() {
        return l;
    }

    public static EventChannel<MatrixStack> q() {
        return g;
    }
}
