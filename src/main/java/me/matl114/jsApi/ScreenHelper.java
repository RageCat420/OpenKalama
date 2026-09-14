package me.matl114.jsApi;

import java.util.List;
import javax.annotation.Nonnull;
import me.matl114.accessors.access.ClientPlayerAccess;
import me.matl114.events.annotations.Modifiable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.CartographyTableScreen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.gui.screen.ingame.SmokerScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

@Modifiable
public class ScreenHelper {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static Slot getScreenSlot(Object handled, int index) {
        return (Slot) unwrapHandler(handled).slots.get(index);
    }

    public static ScreenHandler e(Object handled) {
        return unwrapHandler(handled);
    }

    public static boolean isServerScreenOpen() {
        return mc.player.currentScreenHandler != mc.player.playerScreenHandler;
    }

    public static boolean isInPlayerInventory(Object handled, int slotIndex) {
        return ((Slot) unwrapHandler(handled).slots.get(slotIndex)).inventory instanceof PlayerInventory;
    }

    public static boolean i(Object handled, int slotIndex) {
        return !isInPlayerInventory(handled, slotIndex);
    }

    public static String getScreenName(Screen s) {
        if (s == null) {
            return null;
        } else if (s instanceof HandledScreen) {
            if (s instanceof GenericContainerScreen) {
                return String.format(
                        "%d Row Chest",
                        ((GenericContainerScreenHandler) ((GenericContainerScreen) s).getScreenHandler()).getRows());
            } else if (s instanceof Generic3x3ContainerScreen) {
                return "3x3 Container";
            } else if (s instanceof AnvilScreen) {
                return "Anvil";
            } else if (s instanceof BeaconScreen) {
                return "Beacon";
            } else if (s instanceof BlastFurnaceScreen) {
                return "Blast Furnace";
            } else if (s instanceof BrewingStandScreen) {
                return "Brewing Stand";
            } else if (s instanceof CraftingScreen) {
                return "Crafting Table";
            } else if (s instanceof EnchantmentScreen) {
                return "Enchanting Table";
            } else if (s instanceof FurnaceScreen) {
                return "Furnace";
            } else if (s instanceof GrindstoneScreen) {
                return "Grindstone";
            } else if (s instanceof HopperScreen) {
                return "Hopper";
            } else if (s instanceof LoomScreen) {
                return "Loom";
            } else if (s instanceof MerchantScreen) {
                return "Villager";
            } else if (s instanceof ShulkerBoxScreen) {
                return "Shulker Box";
            } else if (s instanceof SmithingScreen) {
                return "Smithing Table";
            } else if (s instanceof SmokerScreen) {
                return "Smoker";
            } else if (s instanceof CartographyTableScreen) {
                return "Cartography Table";
            } else if (s instanceof StonecutterScreen) {
                return "Stonecutter";
            } else if (s instanceof InventoryScreen) {
                return "Survival Inventory";
            } else if (s instanceof HorseScreen) {
                return "Horse";
            } else {
                return s instanceof CreativeInventoryScreen
                        ? "Creative Inventory"
                        : s.getClass().getName();
            }
        } else if (s instanceof ChatScreen) {
            return "Chat";
        } else {
            Text var1 = s.getTitle();
            String var2 = "";
            if (var1 != null) {
                var2 = var1.getString();
            }

            if (var2.equals("")) {
                var2 = "unknown";
            }

            return var2;
        }
    }

    public static List<Slot> getScreenSlots(Object handled) {
        return unwrapHandler(handled).slots;
    }

    public static ItemStack getScreenStack(Object handled, int index) {
        return ((Slot) unwrapHandler(handled).slots.get(index)).getStack();
    }

    @Nonnull
    public static Object c(HandledScreen s) {
        return JsMacrosBridge.i().b(s);
    }

    public static boolean isScreenOpen() {
        return mc.currentScreen instanceof HandledScreen;
    }

    public static int getSyncId(Object screen) {
        return unwrapHandler(screen).syncId;
    }

    private static ScreenHandler unwrapHandler(Object obj) {
        return obj instanceof ScreenHandler var1
                ? var1
                : JsHelper.a(obj, HandledScreen.class).getScreenHandler();
    }

    @Nonnull
    public static Object createServerInventoryView() {
        ClientPlayerEntity var0 = mc.player;
        HandledScreen var1 = ClientPlayerAccess.of(var0).getServerOpeningScreen();
        return var1 != null ? c(var1) : JsMacrosBridge.i().d();
    }

    public static boolean canTakeFromSlot(Object handled, int slotIndex) {
        Slot var2 = (Slot) unwrapHandler(handled).slots.get(slotIndex);
        return var2.canTakeItems(mc.player);
    }

    public static void setScreenStack(Object handled, int index, ItemStack stack) {
        ((Slot) unwrapHandler(handled).slots.get(index)).setStack(stack == null ? ItemStack.EMPTY : stack);
    }

    public static boolean canPlaceInSlot(Object handled, int slotIndex, Object itemStack) {
        Slot var3 = (Slot) unwrapHandler(handled).slots.get(slotIndex);
        ItemStack var4 = JsHelper.a(itemStack, ItemStack.class);
        return var3.canInsert(var4);
    }

    public static void setSlotItem(Slot slot, ItemStack stack) {
        slot.setStack(stack == null ? ItemStack.EMPTY : stack);
    }

    public static ItemStack getSlotItem(Slot slot) {
        return slot.getStack();
    }
}
