package me.matl114.gui.complex.invcache;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import me.matl114.accessors.interfaces.TileInventory;
import me.matl114.gui.GenericBackGroundScreen;
import me.matl114.gui.GridSubScreen;
import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.LabelElement;
import me.matl114.gui.elements.SlotElement;
import me.matl114.hacks.InvTasks;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.ScreenUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.function.Consumers;

public class InventoryViewScreen extends GenericBackGroundScreen {
    protected final GridSubScreen<DrawableWidget> T;
    protected SlotElement V;
    protected static final ItemStack ICON_UNKNOWN = new ItemStack(Items.BARRIER);
    protected BlockPos Q;
    protected final int DATA_OCCUPIED = 20;
    protected boolean modifiable = false;
    protected ClientWorld R;
    protected final GridSubScreen<DrawableWidget> S;
    protected ItemStack Y = ItemStack.EMPTY;

    protected void shiftClickItem(ItemStack stack, Slot slot) {
        if (this.modifiable && !stack.isEmpty()) {
            this.placeItem(slot, ItemStack.EMPTY);
        }
    }

    protected void placeItem(Slot slot, ItemStack stack) {
        if (this.modifiable) {
            try {
                slot.setStack(stack);
            } catch (Throwable var4) {
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        ExecutableWidget.instance(this.x + 40, this.y + 20 + 2, 16, 16)
                .<ExecutableWidget>eV(this.V)
                .addTo(this);
        AbstractElement var1;
        if (this.Q != null) {
            var1 = LabelElement.instance(Text.literal("%s [%d, %d, %d] "
                                    .formatted(
                                            this.R.getRegistryKey().getValue().toString(),
                                            this.Q.getX(),
                                            this.Q.getY(),
                                            this.Q.getZ()))
                            .append(Text.translatable("widget.gui.inventory-view-screen.click-slot")))
                    .cF(KalamaHelperHelperP.aA(() -> MinecraftClient.getInstance()
                            .keyboard
                            .setClipboard("%d %d %d".formatted(this.Q.getX(), this.Q.getY(), this.Q.getZ()))))
                    .aO(TooltipHandler.ap(Streams.concat(new Stream[] {
                                ChatUtils.parseTranslation("widget.gui.inventory-view-screen.click-slot.tooltips", "")
                                        .stream(),
                                ChatUtils.parseTranslation(
                                        "widget.gui.inventory-view-screen.copy-coordinate.tooltips", "")
                                        .stream()
                            })
                            .toList()));
        } else {
            var1 = LabelElement.instance(Text.translatable("widget.gui.inventory-view-screen.virtual-screen")
                            .append(Text.translatable("widget.gui.inventory-view-screen.click-slot")))
                    .aO(TooltipHandler.ap(
                            ChatUtils.parseTranslation("widget.gui.inventory-view-screen.click-slot.tooltips", "")));
        }

        ExecutableWidget.instance(this.x + 60, this.y + 20, 140, 20)
                .<ExecutableWidget>eV(var1)
                .addTo(this);
        new ContentDelegateWidget<GridSubScreen<DrawableWidget>>(this.x, this.y, 0, 0)
                .setContentDelegate(this.S)
                .addTo(this);
        ExecutableWidget.instance(this.x + 40, this.y + 20 + 20 + 120, 160, 20)
                .<ExecutableWidget>eV(LabelElement.instance(
                                Text.translatable("widget.gui.inventory-view-screen.player-inventory-view")
                                        .append(Text.translatable("widget.gui.inventory-view-screen.click-slot")))
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(
                                "widget.gui.inventory-view-screen.player-inventory-view.tooltips", ""))))
                .addTo(this);
        new ContentDelegateWidget<GridSubScreen<DrawableWidget>>(this.x, this.y, 0, 0)
                .setContentDelegate(this.T)
                .addTo(this);
        DisplayWidget.instance(0, 0, this.width, this.height)
                .<DrawableWidget>setRenderHandler((element, context, mouseX, mouseY, delta, alpha, shouldHighlight) -> {
                    if (this.Y != null && !this.Y.isEmpty()) {
                        context.K(this.Y, mouseX - 8, mouseY - 8, 999, 0);
                        context.drawItemInSlot(mc.textRenderer, this.Y, mouseX - 8, mouseY - 8, null);
                    }
                })
                .addTo(this);
        ExecutableWidget.instance(0, 0, this.width, this.height)
                .<ExecutableWidget>eT((element, mouseX, mouseY, button) -> {
                    this.Y = ItemStack.EMPTY;
                    return false;
                })
                .<DrawableWidget>setPriority(-1)
                .addTo(this);
    }

    public InventoryViewScreen(HandledScreen<?> handledScreen) {
        this(
                handledScreen.getScreenHandler().slots.stream()
                        .filter(i -> !(i.inventory instanceof PlayerInventory))
                        .toList(),
                handledScreen.getTitle(),
                InvTasks.generateIconForScreen(handledScreen));
        if (handledScreen instanceof TileInventory var2 && !var2.isVirtual()) {
            this.Q = var2.getPos();
            this.R = var2.getWorld();
        }
    }

    protected DrawableWidget makeIcon(Slot screen) {
        return ExecutableWidget.instance(0, 0, 16, 16)
                .eV(new SlotElement(screen.inventory, screen.getIndex(), (stack, i) -> {
                            if (ScreenUtils.hasShiftDown()) {
                                this.shiftClickItem(stack, screen);
                                return true;
                            } else if (this.Y != null && !this.Y.isEmpty()) {
                                if (this.modifiable) {
                                    this.placeItem(screen, this.Y);
                                    return true;
                                } else {
                                    return false;
                                }
                            } else if (i == 1) {
                                return this.rightClickItem(stack, screen);
                            } else {
                                return i == 0 ? this.au(stack, screen) : false;
                            }
                        })
                        .aN(() -> this.Y == null || this.Y.isEmpty()));
    }

    public InventoryViewScreen(Inventory inventory, Text title, ItemStack icon) {
        this(inventory, title, icon, false);
    }

    protected boolean rightClickItem(ItemStack stack, Slot slot) {
        if (stack.isEmpty()) {
            if (!this.modifiable) {
                return true;
            }

            stack = new ItemStack(Items.STONE);
        }

        InvTasks.Y(stack, this.modifiable ? newStack -> this.placeItem(slot, newStack) : Consumers.nop());
        return true;
    }

    protected DrawableWidget as(Slot screen) {
        return ExecutableWidget.instance(0, 0, 16, 16)
                .eV(new SlotElement(screen.inventory, screen.getIndex(), (stack, i) -> this.pickupItem(screen))
                        .aN(() -> this.Y == null || this.Y.isEmpty()));
    }

    public InventoryViewScreen(Inventory inventory, Text title, ItemStack icon, boolean modifiable) {
        this(streamInventoryToSlot(inventory, inventory.size()), title, icon, modifiable);
    }

    public InventoryViewScreen(List<Slot> list, Text title, ItemStack icon) {
        this(list, title, icon, false);
    }

    public InventoryViewScreen(List<Slot> list, Text title, ItemStack icon, boolean modifiable) {
        super(title, 240, 320);
        this.S = new GridSubScreen<>(40, 40, 160, 120, 16, 16);
        this.S.refreshPage(list, this::makeIcon, 1);
        this.V = SlotElement.aI(icon);
        this.modifiable = modifiable;
        if (mc.player != null) {
            this.T = new GridSubScreen<>(40, 180, 160, 120, 16, 16);
            this.T.refreshPage(streamInventoryToSlot(mc.player.getInventory(), InventoryUtils.F()), this::as, 1);
        } else {
            this.T = null;
        }
    }

    private static List<Slot> streamInventoryToSlot(Inventory inventory, int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> new Slot(inventory, i, 0, 0))
                .toList();
    }

    protected boolean au(ItemStack stack, Slot slot) {
        return this.pickupItem(slot);
    }

    protected boolean pickupItem(Slot slot) {
        if ((this.Y == null || this.Y.isEmpty()) && !slot.getStack().isEmpty()) {
            this.Y = slot.getStack().copy();
            return true;
        } else {
            return false;
        }
    }
}
