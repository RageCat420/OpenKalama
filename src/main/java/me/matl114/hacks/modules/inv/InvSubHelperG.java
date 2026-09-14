package me.matl114.hacks.modules.inv;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import me.matl114.utils.InventoryUtils;
import me.matl114.utils.collections.KalamaHelperHelperK;
import me.matl114.utils.inventory.ItemStackSample;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class InvSubHelperG {
    int t;
    KalamaHelperHelperK<ItemStack> n;
    static int f = 5;
    static int b = 1;
    BlockPos i;
    static int a = 0;
    String j;
    public CompletableFuture<InvSubHelperG> s;
    Inventory m;
    boolean o;
    static int d = 3;
    static int c = 2;
    static int h = 7;
    static int g = 6;
    static int e = 4;
    Inventory l;
    boolean p;
    InvSubHelperL k = InvSubHelperL.wI;
    int q;
    Map<ItemStackSample, Integer> r;

    public boolean isCompleted() {
        return this.t >= g;
    }

    public void e(boolean success) {
        if (!this.isCompleted()) {
            this.t = success ? g : h;
            this.s.complete(this);
        }
    }

    private void createSummary() {
        int var1 = Math.clamp((long) Math.clamp((long) this.k.Is(), 0, this.l.size()), 0, InventoryUtils.E());
        int var2 = Math.clamp((long) Math.clamp((long) this.k.It(), 0, this.l.size()), 0, InventoryUtils.E());
        this.k = this.k.Io(var1).Ip(var2);
        this.m = InventoryUtils.createSubInventoryView(this.l, var1, var2);
        this.r = new LinkedHashMap<>();

        for (int var3 = var1; var3 < var2; var3++) {
            ItemStack var4 = this.l.getStack(var3);
            if (!var4.isEmpty()) {
                ItemStackSample var5 = ItemStackSample.of(var4);
                this.r.merge(var5, var4.getCount(), Integer::sum);
            }
        }

        PlayerInventory var9 = MinecraftClient.getInstance().player.getInventory();

        for (int var10 = var1; var10 < var2; var10++) {
            ItemStack var11 = var9.getStack(var10);
            if (!var11.isEmpty()) {
                ItemStackSample var6 = ItemStackSample.of(var11);
                Integer var7 = this.r.get(var6);
                if (var7 != null) {
                    int var8 = var7 - var11.getCount();
                    if (var8 <= 0) {
                        this.r.remove(var6);
                    } else {
                        this.r.put(var6, var8);
                    }
                }
            }
        }

        if (this.r.isEmpty()) {
            this.e(true);
        }
    }

    public void setKit(@Nonnull InvSubHelperB kit) {
        this.j = kit.name();
        this.k = kit.hB();
        this.l = KitReplenish.bP(kit);
        this.createSummary();
    }

    public InvSubHelperG() {
        this.o = true;
        this.p = false;
        this.q = -1;
        this.r = new LinkedHashMap<>();
        this.s = new CompletableFuture<>();
    }

    public void d(boolean use) {
        this.p = false;
        this.o = use;
    }

    public void setInventory(@Nonnull Inventory inventory, String name) {
        this.j = name;
        this.k = new InvSubHelperL(InvSubHelperS.Iv, 0, inventory.size(), false);
        this.l = inventory;
        this.createSummary();
    }
}
