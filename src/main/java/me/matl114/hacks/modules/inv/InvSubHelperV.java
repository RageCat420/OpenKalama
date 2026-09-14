package me.matl114.hacks.modules.inv;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.matl114.accessors.interfaces.TileInventory;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.world.ContainerPosition;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public class InvSubHelperV {
    int size;
    HandledScreen<?> f;
    Optional<Block> h;
    public static final Codec<InvSubHelperV> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.list(InventoryUtils.a).fieldOf("contents").forGetter(InvSubHelperV::toSlots),
                    Codec.INT.fieldOf("size").forGetter(InvSubHelperV::getSize),
                    TextCodecs.CODEC.optionalFieldOf("title").forGetter(InvSubHelperV::g),
                    Registries.BLOCK.getCodec().optionalFieldOf("chest-type").forGetter(InvSubHelperV::h),
                    ContainerPosition.CODEC.optionalFieldOf("container-pos").forGetter(InvSubHelperV::i))
            .apply(instance, InvSubHelperV::new));
    boolean e;
    Optional<Text> g;
    boolean b = false;
    Inventory c;
    Optional<ContainerPosition> i;

    public Inventory d() {
        return this.c;
    }

    public Optional<Text> g() {
        return this.g;
    }

    public InvSubHelperV(
            List<KalamaHelperHelperK<ItemStack>> slots,
            int size,
            Optional<Text> title,
            Optional<Block> chestType,
            Optional<ContainerPosition> containerPosition) {
        this.c = new SimpleInventory(size);
        this.g = title;
        this.h = chestType;
        this.i = containerPosition;
        this.size = size;
        this.e = containerPosition.isPresent() && ((ContainerPosition) containerPosition.get()).isDouble();

        for (KalamaHelperHelperK var7 : slots) {
            if (var7.index() >= 0 && var7.index() < size) {
                this.c.setStack(var7.index(), (ItemStack) var7.val());
            }
        }
    }

    public Optional<ContainerPosition> i() {
        return this.i;
    }

    public HandledScreen<?> f() {
        return this.f;
    }

    public Optional<Block> h() {
        return this.h;
    }

    public List<KalamaHelperHelperK<ItemStack>> toSlots() {
        ArrayList var1 = new ArrayList();

        for (int var2 = 0; var2 < this.c.size(); var2++) {
            ItemStack var3 = this.c.getStack(var2);
            if (!var3.isEmpty()) {
                var1.add(new KalamaHelperHelperK<>(var2, var3));
            }
        }

        return var1;
    }

    public int getSize() {
        return this.size;
    }

    public void update(Inventory inventory) {
        this.c = inventory;
        this.size = inventory.size();
        this.b = true;
    }

    public InvSubHelperV(Inventory inventory) {
        this(List.of(), inventory.size(), Optional.empty(), Optional.empty(), Optional.empty());
        this.update(inventory);
    }

    public void b(HandledScreen<?> handledScreen) {
        this.f = handledScreen;
        Inventory var2 = InventoryUtils.i(handledScreen);
        this.update(var2);
        this.g = Optional.ofNullable(handledScreen.getTitle());
        if (handledScreen instanceof TileInventory var3 && !var3.isVirtual()) {
            this.h = Optional.ofNullable(var3.getBlockType());
        }

        this.b = true;
    }

    public InvSubHelperV(HandledScreen<?> handled, ContainerPosition containerPosition) {
        this(handled);
        this.i = Optional.of(containerPosition);
    }

    public InvSubHelperV(HandledScreen<?> handled) {
        this(InventoryUtils.i(handled));
        this.g = Optional.ofNullable(handled.getTitle());
        if (handled instanceof TileInventory var2 && !var2.isVirtual()) {
            Block var3 = var2.getBlockType();
            this.h = Optional.ofNullable(var3);
        }
    }
}
