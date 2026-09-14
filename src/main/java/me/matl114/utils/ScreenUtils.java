package me.matl114.utils;

import java.util.Map;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import me.matl114.accessors.access.HandledScreenAccess;
import me.matl114.events.Listener;
import me.matl114.events.annotations.Modifiable;
import me.matl114.events.catchers.PacketCatcherImpl;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.collections.Point;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.Window;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

@Modifiable
public class ScreenUtils {
    public static final Map<ScreenHandlerType<?>, Integer> b = Map.ofEntries(
            Map.entry(ScreenHandlerType.GENERIC_9X1, 9),
            Map.entry(ScreenHandlerType.GENERIC_9X2, 18),
            Map.entry(ScreenHandlerType.GENERIC_9X3, 27),
            Map.entry(ScreenHandlerType.GENERIC_9X4, 36),
            Map.entry(ScreenHandlerType.GENERIC_9X5, 45),
            Map.entry(ScreenHandlerType.GENERIC_9X6, 54),
            Map.entry(ScreenHandlerType.GENERIC_3X3, 9),
            Map.entry(ScreenHandlerType.CRAFTER_3X3, 9),
            Map.entry(ScreenHandlerType.ANVIL, 3),
            Map.entry(ScreenHandlerType.BEACON, 1),
            Map.entry(ScreenHandlerType.BLAST_FURNACE, 3),
            Map.entry(ScreenHandlerType.BREWING_STAND, 5),
            Map.entry(ScreenHandlerType.CRAFTING, 10),
            Map.entry(ScreenHandlerType.ENCHANTMENT, 2),
            Map.entry(ScreenHandlerType.FURNACE, 3),
            Map.entry(ScreenHandlerType.GRINDSTONE, 3),
            Map.entry(ScreenHandlerType.HOPPER, 5),
            Map.entry(ScreenHandlerType.LOOM, 4),
            Map.entry(ScreenHandlerType.MERCHANT, 3),
            Map.entry(ScreenHandlerType.SHULKER_BOX, 27),
            Map.entry(ScreenHandlerType.SMITHING, 4),
            Map.entry(ScreenHandlerType.SMOKER, 3),
            Map.entry(ScreenHandlerType.CARTOGRAPHY_TABLE, 3),
            Map.entry(ScreenHandlerType.STONECUTTER, 2));
    private static final MinecraftClient a = MinecraftClient.getInstance();

    public static boolean hasCtrlDown() {
        return Screen.hasControlDown();
    }

    public static CompletableFuture<HandledScreen<?>> getOpenScreenFuture() {
        int var0 = a.player.currentScreenHandler.syncId;
        CompletableFuture<HandledScreen<?>> var1 = new CompletableFuture<>();
        Listener.C(new PacketCatcherImpl<OpenScreenS2CPacket>(OpenScreenS2CPacket.class, packetEvent -> {
            OpenScreenS2CPacket var3 = packetEvent.e();
            int var4 = var3.getSyncId();
            if (var0 != var4 && var4 != 0) {
                if (a.currentScreen instanceof HandledScreen var6) {
                    Listener.C(new PacketCatcherImpl<InventoryS2CPacket>(InventoryS2CPacket.class, packet2Event -> {
                        InventoryS2CPacket var4x = packet2Event.e();
                        if (var4x.getSyncId() == var4) {
                            var1.complete(var6);
                            return true;
                        } else {
                            return false;
                        }
                    }));
                } else {
                    var1.complete(null);
                }

                return true;
            } else {
                return false;
            }
        }));
        return var1;
    }

    public static Integer m(ScreenHandlerType<?> type) {
        return b.get(type);
    }

    public static ScreenHandlerType<?> getGenericScreenType(int size) {
        return switch ((size - 1) / 9) {
            case 0 -> ScreenHandlerType.GENERIC_9X1;
            case 1 -> ScreenHandlerType.GENERIC_9X2;
            case 2 -> ScreenHandlerType.GENERIC_9X3;
            case 3 -> ScreenHandlerType.GENERIC_9X4;
            case 4 -> ScreenHandlerType.GENERIC_9X5;
            default -> ScreenHandlerType.GENERIC_9X6;
        };
    }

    public static void simulateMouseButton(@Nonnull Screen screen, int button, int action, int mods) {
        if (screen != null) {
            a.setNavigationType(GuiNavigationType.MOUSE);
        }

        boolean var4 = action == 1;
        if (var4) {
            a.mouse.activeButton = button;
        } else if (a.mouse.activeButton != -1) {
            a.mouse.activeButton = -1;
        }

        boolean[] var6 = new boolean[] {false};
        if (a.getOverlay() == null) {
            double var7 = a.mouse.getX()
                    * a.getWindow().getScaledWidth()
                    / a.getWindow().getWidth();
            double var9 = a.mouse.getY()
                    * a.getWindow().getScaledHeight()
                    / a.getWindow().getHeight();
            if (var4) {
                screen.applyMousePressScrollNarratorDelay();
                Screen.wrapScreenError(
                        () -> var6[0] = screen.mouseClicked(var7, var9, button),
                        "mouseClicked event handler",
                        screen.getClass().getCanonicalName());
            } else {
                Screen.wrapScreenError(
                        () -> var6[0] = screen.mouseReleased(var7, var9, button),
                        "mouseReleased event handler",
                        screen.getClass().getCanonicalName());
            }
        }
    }

    public static Point getMouseCoord(MinecraftClient client) {
        return b(client, client.mouse);
    }

    public static int getCurrentModifiers() {
        long var0 = a.getWindow().getHandle();
        if (var0 == 0L) {
            return 0;
        } else {
            byte var2 = 0;
            if (GLFW.glfwGetKey(var0, 340) == 1 || GLFW.glfwGetKey(var0, 344) == 1) {
                var2 |= 1;
            }

            if (GLFW.glfwGetKey(var0, 341) == 1 || GLFW.glfwGetKey(var0, 345) == 1) {
                var2 |= 2;
            }

            if (GLFW.glfwGetKey(var0, 342) == 1 || GLFW.glfwGetKey(var0, 346) == 1) {
                var2 |= 4;
            }

            if (GLFW.glfwGetKey(var0, 343) == 1 || GLFW.glfwGetKey(var0, 347) == 1) {
                var2 |= 8;
            }

            if (GLFW.glfwGetKey(var0, 280) == 1) {
                var2 |= 16;
            }

            if (GLFW.glfwGetKey(var0, 282) == 1) {
                var2 |= 32;
            }

            return var2;
        }
    }

    public static boolean hasKeyPressed(int keyCode) {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyCode);
    }

    public static Slot getSelectingOrHandSlot() {
        if (a.player == null) {
            return null;
        } else {
            if (a.currentScreen instanceof HandledScreen var1) {
                Point var3 = getMouseCoord(a);
                Slot var2 = HandledScreenAccess.of(var1).reallyGetSlotAt(var3.a, var3.b);
                if (var2 != null) {
                    return var2;
                }
            } else {
                int var4 = InventoryUtils.getSelectedSlot();
                OptionalInt var5 = a.player.playerScreenHandler.getSlotIndex(a.player.getInventory(), var4);
                if (var5.isPresent()) {
                    return a.player.playerScreenHandler.getSlot(var5.getAsInt());
                }
            }

            return null;
        }
    }

    public static void simulateKeyAction(Screen screen, int key, int scancode, int action, int modifiers) {
        if (screen != null) {
            switch (key) {
                case 258:
                    a.setNavigationType(GuiNavigationType.KEYBOARD_TAB);
                case 259:
                case 260:
                case 261:
                default:
                    break;
                case 262:
                case 263:
                case 264:
                case 265:
                    a.setNavigationType(GuiNavigationType.KEYBOARD_ARROW);
            }
        }

        if (action == 1
                && (!(screen instanceof KeybindsScreen)
                        || ((KeybindsScreen) screen).lastKeyCodeUpdateTime <= Util.getMeasuringTimeMs() - 20L)
                && a.options.fullscreenKey.matchesKey(key, scancode)) {
            a.getWindow().toggleFullscreen();
            a.options.getFullscreen().setValue(a.getWindow().isFullscreen());
        } else {
            if (screen != null) {
                boolean[] var5 = new boolean[] {false};
                Screen.wrapScreenError(
                        () -> {
                            if (action == 1 || action == 2) {
                                screen.applyKeyPressNarratorDelay();
                                var5[0] = screen.keyPressed(key, scancode, modifiers);
                            } else if (action == 0) {
                                var5[0] = screen.keyReleased(key, scancode, modifiers);
                            }
                        },
                        "keyPressed event handler",
                        screen.getClass().getCanonicalName());
                if (var5[0]) {
                    return;
                }
            }

            Key var10 = InputUtil.fromKeyCode(key, scancode);
            boolean var6 = screen == null;
            if (var6 || screen instanceof GameMenuScreen var8 && !var8.shouldShowMenu()) {
                boolean var9 = true;
            } else {
                boolean var12 = false;
            }

            if (action == 0) {
                KeyBinding.setKeyPressed(var10, false);
            } else {
                boolean var11 = InputUtil.isKeyPressed(
                        MinecraftClient.getInstance().getWindow().getHandle(), 292);
                if (var6) {
                    if (var11) {
                        KeyBinding.setKeyPressed(var10, false);
                    } else {
                        KeyBinding.setKeyPressed(var10, true);
                        KeyBinding.onKeyPressed(var10);
                    }
                }
            }
        }
    }

    public static boolean hasAltDown() {
        return Screen.hasAltDown();
    }

    public static void simulateMouseScroll(@Nonnull Screen screen, double horizontal, double vertical) {
        boolean var5 = (Boolean) a.options.getDiscreteMouseScroll().getValue();
        double var6 = (Double) a.options.getMouseWheelSensitivity().getValue();
        double var8 = (var5 ? Math.signum(horizontal) : horizontal) * var6;
        double var10 = (var5 ? Math.signum(vertical) : vertical) * var6;
        if (a.getOverlay() == null) {
            if (screen != null) {
                double var12 = a.mouse.getX()
                        * a.getWindow().getScaledWidth()
                        / a.getWindow().getWidth();
                double var14 = a.mouse.getY()
                        * a.getWindow().getScaledHeight()
                        / a.getWindow().getHeight();
                screen.mouseScrolled(var12, var14, var8, var10);
                screen.applyMousePressScrollNarratorDelay();
            } else if (a.player != null) {
                if (a.mouse.eventDeltaHorizontalWheel != 0.0
                        && Math.signum(var8) != Math.signum(a.mouse.eventDeltaHorizontalWheel)) {
                    a.mouse.eventDeltaHorizontalWheel = 0.0;
                }

                if (a.mouse.eventDeltaVerticalWheel != 0.0
                        && Math.signum(var10) != Math.signum(a.mouse.eventDeltaVerticalWheel)) {
                    a.mouse.eventDeltaVerticalWheel = 0.0;
                }

                a.mouse.eventDeltaHorizontalWheel += var8;
                a.mouse.eventDeltaVerticalWheel += var10;
                int var16 = (int) a.mouse.eventDeltaHorizontalWheel;
                int var17 = (int) a.mouse.eventDeltaVerticalWheel;
                if (var16 == 0 && var17 == 0) {
                    return;
                }

                a.mouse.eventDeltaHorizontalWheel -= var16;
                a.mouse.eventDeltaVerticalWheel -= var17;
                int var18 = var17 == 0 ? -var16 : var17;
                if (a.player.isSpectator()) {
                    if (a.inGameHud.getSpectatorHud().isOpen()) {
                        a.inGameHud.getSpectatorHud().cycleSlot(-var18);
                    } else {
                        float var19 =
                                MathHelper.clamp(a.player.getAbilities().getFlySpeed() + var17 * 0.005F, 0.0F, 0.2F);
                        a.player.getAbilities().setFlySpeed(var19);
                    }
                } else {
                    a.player.getInventory().scrollInHotbar(var18);
                }
            }
        }
    }

    public static KalamaHelperHelperK<Slot> getSlot(ScreenHandler handler, Inventory inventory, int index) {
        for (int var3 = 0; var3 < handler.slots.size(); var3++) {
            Slot var4 = (Slot) handler.slots.get(var3);
            if (var4.inventory == inventory && index == var4.getIndex()) {
                return new KalamaHelperHelperK<>(var3, var4);
            }
        }

        return null;
    }

    public static boolean hasShiftDown() {
        return Screen.hasShiftDown();
    }

    public static boolean hasEnterDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 257)
                || InputUtil.isKeyPressed(
                        MinecraftClient.getInstance().getWindow().getHandle(), 355);
    }

    public static Point b(MinecraftClient client, Mouse mouse) {
        Window var2 = client.getWindow();
        int var3 = (int) (mouse.getX() * var2.getScaledWidth() / var2.getWidth());
        int var4 = (int) (mouse.getY() * var2.getScaledHeight() / var2.getHeight());
        return new Point(var3, var4);
    }

    public static ItemStack getSelectingOrHandItem() {
        if (a.player == null) {
            return null;
        } else if (a.currentScreen instanceof HandledScreen var1) {
            Point var3 = getMouseCoord(a);
            Slot var2 = HandledScreenAccess.of(var1).reallyGetSlotAt(var3.a, var3.b);
            return var2 != null ? var2.getStack() : null;
        } else {
            return a.player.getStackInHand(Hand.MAIN_HAND);
        }
    }

    public static boolean l(int keyCode) {
        return keyCode == 257 || keyCode == 32 || keyCode == 335;
    }

    public static void openChatScreen(String originalText) {
        a.openChatScreen(originalText);
    }
}
