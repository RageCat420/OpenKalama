package me.matl114.utils.itemdb;

import com.google.gson.JsonElement;
import java.util.Objects;
import javax.annotation.Nonnull;
import me.matl114.utils.ItemStackUtils;
import net.minecraft.item.ItemStack;

public final class KalamaHelperHelperD implements ItemStackData {
    Integer rs;
    JsonElement rT;
    boolean rU;
    ItemStack stack;
    boolean resolve = false;

    @Override
    public ItemStack ge() {
        this.resolveItemStack();
        if (this.rU) {
            return this.stack;
        } else {
            throw new UnsupportedOperationException("Invalid data");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ItemStackData var2) {
            boolean var3 = this.rU;
            boolean var4 = var2.isValid();
            if (var3 && var4) {
                return ItemStack.areItemsAndComponentsEqual(this.stack, var2.ge());
            } else {
                return !var3 && !var4 ? Objects.equals(this.rT, var2.gb()) : false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void resolveItemStack() {
        if (!this.resolve) {
            this.resolve = true;

            try {
                this.stack = ItemStackData.deserialize(this.rT).copyWithCount(1);
                this.rU = true;
            } catch (Throwable var4) {
                try {
                    ItemStackUtils.registry();
                } catch (Throwable var3) {
                }

                this.rU = false;
            }
        }
    }

    public KalamaHelperHelperD(@Nonnull JsonElement jsonRaw) {
        this.rU = false;
        this.rT = jsonRaw;
    }

    @Override
    public boolean isValid() {
        this.resolveItemStack();
        return this.rU;
    }

    @Override
    public int hashCode() {
        if (this.rs == null) {
            this.resolveItemStack();
            if (this.rU) {
                this.rs = ItemStack.hashCode(this.stack);
            } else {
                this.rs = this.rT.hashCode();
            }
        }

        return this.rs;
    }

    @Override
    public JsonElement gb() {
        return this.rT;
    }

    KalamaHelperHelperD(@Nonnull JsonElement jsonRaw, ItemStack itemStack) {
        this.rU = false;
        this.rT = jsonRaw;
        this.stack = itemStack;
        this.resolve = true;
        this.rU = true;
    }

    @Override
    public ItemStack gf() {
        this.resolveItemStack();
        return this.rU ? this.stack : Ix;
    }
}
