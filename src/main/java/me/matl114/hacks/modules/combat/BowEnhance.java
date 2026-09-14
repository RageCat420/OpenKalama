package me.matl114.hacks.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.KalamaHelperHelperFX;
import me.matl114.hacks.KalamaHelperHelperIX;
import me.matl114.hacks.MovTasks;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.managers.Configs;
import me.matl114.managers.Configs$LegalInteractMode;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class BowEnhance extends BaseModule {
    public EnumRef<Configs$LegalInteractMode> targetingMode;
    public FlagRef versionLowerThan121;
    public FlagRef aimEnable;
    public final ModulePath CZ = makePath(Configs.k, "bow-att");
    public FlagRef tpEnable;
    public DoubleRef tpAccelerate;
    public KeyBindRef bowEnhanceHotkey;
    public FlagRef ae = this.flagBuilder(this.CZ.add("bow-enhance")).build();
    public FlagRef renderTarget;
    public FlagRef tpAccelerateExactTp;

    public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
        ModulePreset var2 = (ModulePreset) ((KalamaHelperHelperI) event.e()).b();
        switch (var2) {
            case fd:
            case fe:
                if (this.tpAccelerate.get() < 0.0) {
                    this.tpAccelerate.set(-this.tpAccelerate.get());
                }

                if (this.targetingMode.get() == Configs$LegalInteractMode.DELAY_MOVEMENT) {
                    this.targetingMode.set(Configs$LegalInteractMode.LEGACY_SLIENT_ROT);
                }
                break;
            default:
                if (this.tpAccelerate.get() > 0.0) {
                    this.tpAccelerate.set(-this.tpAccelerate.get());
                }

                if (this.targetingMode.get() == Configs$LegalInteractMode.LEGACY_SLIENT_ROT) {
                    this.targetingMode.set(Configs$LegalInteractMode.DELAY_MOVEMENT);
                }
        }

        switch (var2) {
            case fd:
            case fe:
                this.targetingMode.set(Configs$LegalInteractMode.NONE);
                break;
            case fh:
                this.targetingMode.set(Configs$LegalInteractMode.LEGACY_SLIENT_ROT);
                break;
            default:
                this.targetingMode.set(Configs$LegalInteractMode.USEITEM_PACKET);
        }
    }

    public void bowActionDelayMovement(
            Event<PlayerActionC2SPacket> event, @Nullable Entity entity, float initialVelocity) {
        if (this.canTp()) {
            Debug.b("[BowEh] Arrow Velocity Simulate not enabled in Legal Mode");
        }

        if (entity != null) {
            event.cancel();
            PlayerActionC2SPacket var4 = (PlayerActionC2SPacket) event.e();
            ClientPlayerAccess.of(mc.player)
                    .getLegalMovementManager()
                    .i(new CombatSubHelperAX(this, entity, initialVelocity, var4));
        }
    }

    public BowEnhance() {
        super("BowEnhance");
        this.bowEnhanceHotkey = this.toggleHotkey(
                        this.CZ.add("bow-enhance-hotkey"), new MultiKeyBind(), this.CZ.add("bow-enhance"))
                .build();
        this.aimEnable = this.flagBuilder(this.CZ.add("aim-enable")).build();
        this.tpEnable = this.flagBuilder(this.CZ.add("tp-enable")).build();
        this.targetingMode = this.builder(this.CZ.add("targeting-mode"), Configs$LegalInteractMode.class)
                .defaultValue(Configs$LegalInteractMode.USEITEM_PACKET)
                .build();
        this.tpAccelerate = this.builder(this.CZ.add("tp-accelerate"), DoubleRef.TYPE)
                .defaultValue(150.0)
                .show(this.tpEnable::get)
                .build();
        this.tpAccelerateExactTp = this.flagBuilder(this.CZ.add("tp-accelerate-exact-tp"))
                .show(this.tpEnable::get)
                .build();
        this.versionLowerThan121 =
                this.flagBuilder(this.CZ.add("version-lower-than-121")).build();
        this.renderTarget = this.flagBuilder(this.CZ.add("render-target")).build();
        this.bindFlag(this.ae);
    }

    public void onRenderAimTarget(Event<MatrixStack> stackE) {
        MatrixStack var2 = (MatrixStack) stackE.b;
        if (this.ae.get()
                && this.aimEnable.get()
                && this.renderTarget.get()
                && mc.player != null
                && mc.player.isUsingItem()) {
            float var3 = (Float) stackE.c[0];
            ItemStack var4 = mc.player.getActiveItem();
            if (!var4.isEmpty()
                    && (var4.getItem() instanceof RangedWeaponItem || var4.getItem() instanceof TridentItem)) {
                RenderUtils.startDrawVirtual(var2);

                try {
                    Entity var5 = CombatTasks.l().akN(var4.getItem() instanceof BowItem);
                    if (var5 != null) {
                        Box var6 = RenderUtils.getLerpedBox(var5, var3);
                        RenderUtils.r(var2, var6.getMinPos(), var6.getMaxPos(), ColorUtils.k(Color.GREEN, 0.25F));
                    }
                } finally {
                    RenderUtils.stopDrawVirtual(var2);
                }
            }
        }
    }

    public void bowActionMovement(Event<PlayerActionC2SPacket> event, @Nullable Entity entity, float initialVelocity) {
        Vec2f var4 = new Vec2f(mc.player.getPitch(), mc.player.getYaw());
        Vec3d var5 = entity == null
                ? mc.player.getRotationVector().normalize()
                : CombatTasks.m()
                        .predictAimPositionForEntity(entity, 3600000.0F)
                        .subtract(mc.player.getEyePos());
        Entity var6 = mc.crosshairTarget != null && mc.crosshairTarget.getType() == Type.ENTITY
                ? ((EntityHitResult) mc.crosshairTarget).getEntity()
                : null;
        if (var6 != null && var6.getPos().squaredDistanceTo(mc.player.getEyePos()) > 50.0) {
            var6 = null;
        }

        boolean var7;
        float var8;
        var7 = this.aimEnable.get();
        var8 = initialVelocity;
        label122:
        if (this.canTp()) {
            boolean var9 = this.tpAccelerateExactTp.get();
            double var10 = this.tpAccelerate.get();
            Vec3d var12 = var5.normalize();
            Vec3d var13 = Vec3d.ZERO.subtract(var12);
            Vec3d var14 = Vec3d.ZERO;
            Vec3d var15 = mc.player.getPos();
            boolean var16 = true;
            KalamaHelperHelperFX var17 =
                    new KalamaHelperHelperFX(mc.player, var15, var15.add(var13.multiply(var10 + 1.0)), true);
            double var18 = var10;

            label119:
            while (true) {
                if (!(var18 > 10.0)) {
                    var18 = 10.0;

                    while (true) {
                        if (!(var18 > 0.0)) {
                            break label119;
                        }

                        Vec3d var31 = var13.multiply(var18);
                        if (var9) {
                            if (MovTasks.validMoveTo(var17, var15.add(var31), Vec3d.ZERO.subtract(var31))) {
                                var14 = var31;
                                break label119;
                            }
                        } else if (MovTasks.validMoveToAndBack(var17, var15, var31)) {
                            var14 = var31;
                            break label119;
                        }

                        Vec3d var21 = new Vec3d(var31.x, 0.0, var31.z);
                        Vec3d var22 = var17.simulateMovement(mc.player, var15, var21);
                        if (MovTasks.validMovementAsServer(var21, var22)) {
                            Vec3d var23 =
                                    var17.simulateMovement(mc.player, var15.add(var22), new Vec3d(0.0, var31.y, 0.0));
                            Vec3d var24 = var22.add(var23);
                            if (MovTasks.validMoveTo(var17, var15.add(var24), var24.multiply(-1.0))) {
                                var14 = var24;
                                break label119;
                            }
                        }

                        var18 -= 0.5;
                    }
                }

                if (var9) {
                    Vec3d var20 = var13.multiply(var18);
                    if (MovTasks.validMoveTo(var17, var15.add(var20), Vec3d.ZERO.subtract(var20))) {
                        var14 = var20;
                        break;
                    }
                } else if (MovTasks.validMoveToAndBack(var17, var15, var13.multiply(var18))) {
                    var14 = var13.multiply(var18);
                    break;
                }

                var18--;
            }

            if (var14.lengthSquared() > 1.0E-4) {
                double var25 = var14.length();
                var8 = (float) (initialVelocity + var25);
                var16 = var25 < 10.0;
                List var27 = MovTasks.z(var15, var15.add(var14), false, 161.0, true);
                if (!var27.isEmpty()) {
                    Vec2f var32 = null;
                    Debug.b(Text.literal("[Bow TP] Projectile Velocity Simulate %.2f".formatted(var14.length()))
                            .formatted(Formatting.GREEN));
                    ArrayList var33 = new ArrayList();
                    int var34 = var27.size();

                    for (int var35 = 0; var35 < var34; var35++) {
                        var33.add(
                                var35 == 0
                                        ? MovTasks$MovInfo.adA((Vec3d) var27.get(var35))
                                        : MovTasks$MovInfo.adz((Vec3d) var27.get(var35)));
                    }

                    if (var16) {
                        var32 = CombatTasks.calculatePitchYawPredict(var8, var14, var5);
                        if (Float.isNaN(var32.x) || Float.isInfinite(var32.x)) {
                            Debug.b("[Bow Aim] Arrow failed to reach the target");
                            var16 = false;
                        }
                    }

                    var33.add(
                            var16
                                    ? new MovTasks$MovInfo(var15.add(0.0, 9.0E-8, 0.0), null, true, var32)
                                    : MovTasks$MovInfo.adz(var15.add(0.0, 9.0E-8, 0.0)));
                    MovTasks.scheduleFarawayMoveInternal(var33, false, KalamaHelperHelperIX.create(var15), true);
                    MovTasks.Y();
                    var7 = false;
                    break label122;
                }
            }

            Debug.b(Text.literal("[Bow TP] Projectile Velocity fail to simulate"));
        }

        if (var7 && entity != null && entity != var6) {
            Vec2f var28 = CombatTasks.calculatePitchYawPredict(var8, Vec3d.ZERO, var5);
            if (!Float.isNaN(var28.x)
                    && !Float.isInfinite(var28.x)
                    && !Float.isNaN(var28.y)
                    && !Float.isInfinite(var28.y)) {
                LegacySnapRotManager.INSTANCE.snapAt(var28.x, var28.y, false);
            } else {
                Debug.b("[Bow Aim] Arrow failed to reach the target");
            }
        }

        mc.player.setPitch(var4.x);
        mc.player.setYaw(var4.y);
    }

    public void onBowAction(Event<PlayerActionC2SPacket> actionEvent) {
        if (!actionEvent.d()) {
            if (this.ae.get()) {
                PlayerActionC2SPacket var2 = (PlayerActionC2SPacket) actionEvent.e();
                if (var2.getAction() == Action.RELEASE_USE_ITEM && mc.player != null) {
                    if (!mc.player.isUsingItem()) {
                        return;
                    }

                    ItemStack var3 = mc.player.getActiveItem();
                    if (var3.isEmpty()) {
                        return;
                    }

                    if (var3.getItem() instanceof BowItem || var3.getItem() instanceof TridentItem) {
                        boolean var4 = this.aimEnable.get();
                        Entity var7;
                        if (var4) {
                            Entity var5 = TargetSelector.INSTANCE.akN(var3.getItem() instanceof BowItem);
                            if (var5 != null) {
                                Debug.b(Text.literal("[Bow Aim] Aim at %s"
                                                .formatted(var5 instanceof PlayerEntity var6 ? "player " : "entity "))
                                        .append(EntityUtils.getEntityDisplayable(var5))
                                        .formatted(Formatting.GREEN));
                                var7 = var5;
                            } else {
                                var7 = null;
                            }
                        } else {
                            var7 = null;
                        }

                        float var10;
                        if (var3.getItem() instanceof BowItem) {
                            var10 = (72000 - mc.player.getItemUseTimeLeft()) / 20.0F;
                            var10 = (var10 * var10 + var10 * 2.0F) / 3.0F;
                            if (var10 > 1.0F) {
                                var10 = 1.0F;
                            }

                            var10 *= 3.0F;
                        } else if (var3.getItem() instanceof TridentItem) {
                            var10 = 2.5F;
                        } else {
                            var10 = 3.0F;
                        }

                        switch ((Configs$LegalInteractMode) this.targetingMode.get()) {
                            case LEGACY_SLIENT_ROT:
                                this.bowActionMovement(actionEvent, var7, var10);
                                break;
                            case DELAY_MOVEMENT:
                            case MOVEMENT_POST:
                                this.bowActionDelayMovement(actionEvent, var7, var10);
                                break;
                            case USEITEM_PACKET:
                                this.bowActionInteractItem(actionEvent, var7, var10);
                                break;
                            default:
                                this.bowActionInteractItem(actionEvent, var7, var10);
                        }
                    }
                }
            }
        }
    }

    public boolean canTp() {
        return this.tpEnable.get() && this.tpAccelerate.get() > 1.0E-7;
    }

    public void bowActionInteractItem(
            Event<PlayerActionC2SPacket> event, @Nullable Entity entity, float initialVelocity) {
        Vec2f var4 = new Vec2f(mc.player.getPitch(), mc.player.getYaw());
        Vec3d var5 = entity == null
                ? mc.player.getRotationVector().normalize()
                : CombatTasks.m()
                        .predictAimPositionForEntity(entity, 3600000.0F)
                        .subtract(mc.player.getEyePos());
        Entity var6 = mc.crosshairTarget != null && mc.crosshairTarget.getType() == Type.ENTITY
                ? ((EntityHitResult) mc.crosshairTarget).getEntity()
                : null;
        if (var6 != null && var6.getPos().squaredDistanceTo(mc.player.getEyePos()) > 50.0) {
            var6 = null;
        }

        boolean var7;
        float var8;
        var7 = this.aimEnable.get();
        var8 = initialVelocity;
        label101:
        if (this.canTp()) {
            boolean var9 = this.tpAccelerateExactTp.get();
            double var10 = this.tpAccelerate.get();
            Vec3d var12 = var5.normalize();
            Vec3d var13 = Vec3d.ZERO.subtract(var12);
            Vec3d var14 = Vec3d.ZERO;
            Vec3d var15 = mc.player.getPos();
            boolean var16 = true;
            KalamaHelperHelperFX var17 =
                    new KalamaHelperHelperFX(mc.player, var15, var15.add(var13.multiply(var10 + 1.0)), true);
            double var18 = var10;

            label98:
            while (true) {
                if (!(var18 > 10.0)) {
                    var18 = 10.0;

                    while (true) {
                        if (!(var18 > 0.0)) {
                            break label98;
                        }

                        Vec3d var30 = var13.multiply(var18);
                        if (var9) {
                            if (MovTasks.validMoveTo(var17, var15.add(var30), Vec3d.ZERO.subtract(var30))) {
                                var14 = var30;
                                break label98;
                            }
                        } else if (MovTasks.validMoveToAndBack(var17, var15, var30)) {
                            var14 = var30;
                            break label98;
                        }

                        Vec3d var21 = new Vec3d(var30.x, 0.0, var30.z);
                        Vec3d var22 = var17.simulateMovement(mc.player, var15, var21);
                        if (MovTasks.validMovementAsServer(var21, var22)) {
                            Vec3d var23 =
                                    var17.simulateMovement(mc.player, var15.add(var22), new Vec3d(0.0, var30.y, 0.0));
                            Vec3d var24 = var22.add(var23);
                            if (MovTasks.validMoveTo(var17, var15.add(var24), var24.multiply(-1.0))) {
                                var14 = var24;
                                break label98;
                            }
                        }

                        var18 -= 0.5;
                    }
                }

                if (var9) {
                    Vec3d var20 = var13.multiply(var18);
                    if (MovTasks.validMoveTo(var17, var15.add(var20), Vec3d.ZERO.subtract(var20))) {
                        var14 = var20;
                        break;
                    }
                } else if (MovTasks.validMoveToAndBack(var17, var15, var13.multiply(var18))) {
                    var14 = var13.multiply(var18);
                    break;
                }

                var18--;
            }

            if (var14.lengthSquared() > 1.0E-4) {
                double var25 = var14.length();
                var8 = (float) (initialVelocity + var25);
                List var27 = MovTasks.z(var15, var15.add(var14), false, 161.0, true);
                if (!var27.isEmpty()) {
                    Debug.b(Text.literal("[Bow TP] Projectile Velocity Simulate %.2f".formatted(var14.length()))
                            .formatted(Formatting.GREEN));
                    ArrayList var31 = new ArrayList();
                    int var32 = var27.size();

                    for (int var33 = 0; var33 < var32; var33++) {
                        var31.add(
                                var33 == 0
                                        ? MovTasks$MovInfo.adA((Vec3d) var27.get(var33))
                                        : MovTasks$MovInfo.adz((Vec3d) var27.get(var33)));
                    }

                    var31.add(MovTasks$MovInfo.adz(var15.add(0.0, 9.0E-8, 0.0)));
                    MovTasks.scheduleFarawayMoveInternal(var31, false, KalamaHelperHelperIX.create(var15), true);
                    MovTasks.Y();
                    break label101;
                }
            }

            Debug.b(Text.literal("[Bow TP] Projectile Velocity fail to simulate"));
        }

        if (var7 && entity != null && entity != var6) {
            Vec2f var28 = CombatTasks.calculatePitchYawPredict(var8, mc.player.getVelocity(), var5);
            if (!Float.isNaN(var28.x)
                    && !Float.isInfinite(var28.x)
                    && !Float.isNaN(var28.y)
                    && !Float.isInfinite(var28.y)) {
                mc.interactionManager.sendSequencedPacket(
                        mc.world, s -> new PlayerInteractItemC2SPacket(mc.player.getActiveHand(), s, var28.y, var28.x));
            } else {
                Debug.b("[Bow Aim] Arrow failed to reach the target");
            }
        }

        mc.player.setPitch(var4.x);
        mc.player.setYaw(var4.y);
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ap().getChannel(PlayerActionC2SPacket.class), this::onBowAction, 999);
        this.registerListener(RenderListener.q(), this::onRenderAimTarget);
        this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
    }
}
