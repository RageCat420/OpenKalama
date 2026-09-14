package me.matl114.mixins.events;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.io.File;
import java.util.Objects;
import me.matl114.events.Event;
import me.matl114.events.GlobalEventVars;
import me.matl114.events.Listener;
import me.matl114.utils.collections.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.util.Hand;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({MinecraftClient.class})
@Environment(EnvType.CLIENT)
public abstract class MinecraftClientEvents {
    @Shadow
    @Nullable
    public Screen field_1755;

    @Shadow
    private int field_1752;

    @Shadow
    public HitResult field_1765;

    @Shadow
    private Profiler field_16240;

    @Shadow
    @Nullable
    public ClientPlayerEntity field_1724;

    @Shadow
    public int field_1771;

    @Unique
    private HitResult cacheHitResult = null;

    @Shadow
    public abstract Window method_22683();

    @Shadow
    protected abstract void method_1590(boolean var1);

    @Shadow
    protected abstract void method_1508();

    @Inject(
            method = {"setScreen"},
            at = {@At("HEAD")},
            cancellable = true)
    public void onPreSetScreen(Screen screen, CallbackInfo ci, @Local(argsOnly = true) LocalRef<Screen> screenRef) {
        if (!Listener.ae().d()) {
            Event<Screen> screenEvent = new Event<>(screen, true, true);
            Listener.ae().b(screenEvent);
            if (screenEvent.d()) {
                ci.cancel();
            } else if (screenEvent.b != screen) {
                screenRef.set(screenEvent.b);
            }
        }
    }

    @Inject(
            method = {"setScreen"},
            at = {
                @At(
                        value = "FIELD",
                        target =
                                "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
                        ordinal = 3,
                        shift = Shift.BEFORE)
            },
            cancellable = true)
    public void onPostSetScreen(Screen screen, CallbackInfo ci) {
        if (!Listener.af().d()) {
            Event<Screen> screenEvent = new Event<>(this.field_1755, true, false);
            Listener.af().b(screenEvent);
            if (screenEvent.d()) {
                ci.cancel();
                if (this.field_1755 != null) {
                    this.field_1755.init(
                            MinecraftClient.getInstance(),
                            this.method_22683().getScaledWidth(),
                            this.method_22683().getScaledHeight());
                }

                Listener.ag().broadcast(this.field_1755);
                return;
            }
        }
    }

    @Inject(
            method = {"setScreen"},
            at = {@At("RETURN")})
    public void onPreSetScreen(Screen screen, CallbackInfo ci) {
        Listener.ag().broadcast(this.field_1755);
    }

    @Inject(
            method = {"disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V"},
            at = {
                @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;unloadWorld()V")
            })
    public void onServerDisconnect(Screen disconnectionScreen, boolean transferring, CallbackInfo ci) {
        Listener.O().catchEvent(new Event<>(null, false, false, true));
        Listener.P().catchEvent(new Event<>(null, false, false, transferring));
    }

    @Inject(
            method = {"enterReconfiguration"},
            at = {@At("HEAD")})
    public void onServerReconfiguration(Screen reconfigurationScreen, CallbackInfo ci) {
        Listener.O().catchEvent(new Event<>(null, false, false, false));
    }

    @WrapWithCondition(
            method = {"render"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/render/GameRenderer;render(Lnet/minecraft/client/render/RenderTickCounter;Z)V")
            })
    private boolean onGameRenderer(GameRenderer instance, RenderTickCounter tickCounter, boolean tick) {
        Event<GameRenderer> rendererEvent = new Event<>(instance, true, false, tickCounter, tick);
        Listener.W().catchEvent(rendererEvent);
        return !rendererEvent.d();
    }

    @WrapOperation(
            method = {"printCrashReport(Lnet/minecraft/util/crash/CrashReport;)V"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;cleanUpAfterCrash()V")})
    private void onSystemPreExitClearGameContent(
            MinecraftClient client, Operation<Void> original, @Local(argsOnly = true) CrashReport report) {
        if (client != null) {
            GlobalEventVars.a = report;
            GlobalEventVars.c = new Event<>(client, client.isRunning(), false, report);
            if (!Listener.Y().d()) {
                Event<MinecraftClient> exitEvent = GlobalEventVars.c;
                Listener.Y().catchEvent(exitEvent);
            }

            if (!GlobalEventVars.c.d()) {
                original.call(new Object[] {client});
            }
        }
    }

    @Inject(
            method = {
                "printCrashReport(Lnet/minecraft/client/MinecraftClient;Ljava/io/File;Lnet/minecraft/util/crash/CrashReport;)V"
            },
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/Bootstrap;println(Ljava/lang/String;)V",
                        shift = Shift.AFTER)
            },
            cancellable = true)
    private static void onSystemExit(
            MinecraftClient client, File runDirectory, CrashReport crashReport, CallbackInfo ci) {
        if (client != null) {
            GlobalEventVars.a = crashReport;
            if (GlobalEventVars.c == null) {
                GlobalEventVars.c = new Event<>(client, client.isRunning(), false, crashReport);
                if (!Listener.Y().d()) {
                    Event<MinecraftClient> exitEvent = GlobalEventVars.c;
                    Listener.Y().catchEvent(exitEvent);
                }
            }

            if (GlobalEventVars.c.d()) {
                ci.cancel();
            } else {
                GlobalEventVars.c = null;
                GlobalEventVars.a = null;
            }
        }
    }

    @Inject(
            method = {"doItemUse"},
            at = {@At("HEAD")})
    private void onItemUseTargetStore(CallbackInfo ci) {
        this.cacheHitResult = this.field_1765;
    }

    @Inject(
            method = {"doItemUse"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;")
            },
            cancellable = true)
    private void onItemUseEvent(CallbackInfo ci, @Local Hand currentHand) {
        this.field_1765 = this.cacheHitResult;
        Event<HitResult> hitResultEvent = new Event<>(this.field_1765, true, true, currentHand);
        Listener.bn().catchEvent(hitResultEvent);
        if (!hitResultEvent.d() && hitResultEvent.b != null) {
            this.field_1765 = hitResultEvent.b;
        } else {
            this.field_1765 = this.cacheHitResult;
            ci.cancel();
        }
    }

    @Inject(
            method = {"doItemUse"},
            at = {@At("RETURN")})
    private void onItemUseTargetRestore(CallbackInfo ci) {
        this.field_1765 = this.cacheHitResult;
    }

    @Inject(
            method = {"doItemUse"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z",
                        shift = Shift.BEFORE)
            })
    private void onItemCooldown(CallbackInfo ci) {
        Event<Integer> event = new Event<>(null, true, true);
        Listener.aB().catchEvent(event);
        if (event.d()) {
            this.field_1752 = 0;
        } else if (event.e() != null) {
            this.field_1752 = event.e();
        }
    }

    @Inject(
            method = {"handleBlockBreaking"},
            at = {
                @At(
                        value = "FIELD",
                        target =
                                "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;",
                        ordinal = 0,
                        shift = Shift.BEFORE)
            })
    private void onMineBlock(boolean breaking, CallbackInfo ci) {
        if (breaking) {
            Event<HitResult> hitResultEvent = new Event<>(this.field_1765, true, true, false);
            Listener.bl().catchEvent(hitResultEvent);
            if (hitResultEvent.d() || hitResultEvent.b != this.field_1765) {
                this.cacheHitResult = this.field_1765;
                this.field_1765 = hitResultEvent.d() ? null : hitResultEvent.b;
            }
        }
    }

    @Inject(
            method = {"handleBlockBreaking"},
            at = {@At("RETURN")})
    private void onRestoreHitResultAfterBreak(CallbackInfo ci) {
        if (this.cacheHitResult != null) {
            this.field_1765 = this.cacheHitResult;
        }

        this.cacheHitResult = null;
    }

    @Inject(
            method = {"doAttack"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/util/hit/BlockHitResult;getBlockPos()Lnet/minecraft/util/math/BlockPos;",
                        shift = Shift.BEFORE)
            },
            cancellable = true)
    private void onAttackBlock(CallbackInfoReturnable<Boolean> cir, @Local LocalRef<BlockHitResult> hitResultLocalRef) {
        BlockHitResult re = (BlockHitResult) hitResultLocalRef.get();
        Event<HitResult> hitResultEvent = new Event<>(re, true, true, true);
        Listener.bl().catchEvent(hitResultEvent);
        if (hitResultEvent.d() || !(hitResultEvent.b instanceof BlockHitResult)) {
            cir.setReturnValue(false);
        }

        if (hitResultEvent.b != re) {
            hitResultLocalRef.set((BlockHitResult) hitResultEvent.b);
        }
    }

    @WrapOperation(
            method = {"tick"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V")})
    private void onInputEvent(MinecraftClient instance, Operation<Void> original) {
        Event<Void> re = new Event<>(null, true, false);
        Listener.bd().catchEvent(re);
        if (!re.d()) {
            original.call(new Object[] {instance});
        }

        Listener.be().catchEvent(new Event<>(null, false, false));
    }

    @Inject(
            method = {"tick"},
            at = {
                @At(
                        value = "FIELD",
                        target =
                                "Lnet/minecraft/client/MinecraftClient;overlay:Lnet/minecraft/client/gui/screen/Overlay;",
                        shift = Shift.BEFORE,
                        ordinal = 0)
            })
    public void onInputEventIfScreenOpen(CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen != null
                || MinecraftClient.getInstance().getOverlay() != null) {
            this.field_16240.swap("Keybindings");
            if (MinecraftClient.getInstance().player != null) {
                Event<Void> re = new Event<>(null, true, false);
                re.cancel();
                Listener.bd().catchEvent(re);
                if (!re.d()) {
                    this.method_1508();
                } else {
                    this.method_1590(false);
                    if (this.field_1771 > 0) {
                        this.field_1771--;
                    }
                }

                Listener.be().catchEvent(new Event<>(null, false, false));
            }
        }
    }

    @Inject(
            method = {"tick"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/tutorial/TutorialManager;tick(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/hit/HitResult;)V",
                        shift = Shift.AFTER)
            })
    public void onPreTick(CallbackInfo ci) {
        Listener.S().broadcast(null);
        if (this.field_1724 != null) {
            Listener.U().broadcast(this.field_1724);
        }
    }

    @Inject(
            method = {"tick"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
                        ordinal = 6,
                        shift = Shift.BEFORE)
            })
    private void onPostGameTick(CallbackInfo ci) {
        if (this.field_1724 != null) {
            this.field_16240.swap("post-game-tick");
            Listener.V().broadcast(this.field_1724);
        }
    }

    @Inject(
            method = {"tick"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
                        shift = Shift.BEFORE,
                        ordinal = 1)
            },
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onPostTick(CallbackInfo ci) {
        this.field_16240.swap("post-tick");
        Listener.T().broadcast(null);
    }

    @Inject(
            method = {"doAttack"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;",
                        shift = Shift.BEFORE)
            },
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    public void onAttackAction(CallbackInfoReturnable<Boolean> cir) {
        if (this.field_1765 != null) {
            Event<HitResult> resultEvent = new Event<>(this.field_1765, true, true);
            Listener.bm().catchEvent(resultEvent);
            if (resultEvent.d()) {
                cir.setReturnValue(false);
            } else if (!Objects.equals(resultEvent.e(), this.field_1765)) {
                this.cacheHitResult = this.field_1765;
                this.field_1765 = resultEvent.e();
            } else {
                this.cacheHitResult = null;
            }
        }
    }

    @Inject(
            method = {"doAttack"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V",
                        shift = Shift.BEFORE)
            })
    private void restoreAttackTarget(CallbackInfoReturnable<Boolean> cir) {
        if (this.cacheHitResult != null) {
            this.field_1765 = this.cacheHitResult;
            this.cacheHitResult = null;
        }
    }

    @Inject(
            method = {"onResolutionChanged"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/MinecraftClient;getFramebuffer()Lnet/minecraft/client/gl/Framebuffer;",
                        shift = Shift.BEFORE)
            })
    public void onResolutionChanged(CallbackInfo ci) {
        Listener.bo()
                .catchEvent(new Event<>(
                        new Point(
                                MinecraftClient.getInstance().getWindow().getScaledWidth(),
                                MinecraftClient.getInstance().getWindow().getScaledHeight()),
                        false,
                        false));
    }
}
