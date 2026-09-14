package me.matl114.bukkit;

import com.google.common.base.Preconditions;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import me.matl114.events.annotations.Dispatch;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed class BukkitItemStack implements Cloneable, Dispatch permits CraftItemStack {
    private BukkitMetaItem bb;
    private Item aZ = Items.AIR;
    private static final String VERSION_1_21_10_FLAG = "schema_version";
    private int amount = 0;

    public BukkitItemStack(@NotNull Item type, int amount, short damage) {
        this(type, amount, damage, (Byte) null);
    }

    public boolean dz(@Nullable BukkitMetaItem itemMeta) {
        return this.setItemMeta0(itemMeta, this.aZ);
    }

    public BukkitItemStack(@NotNull Item type, int amount, short damage, @Nullable Byte data) {
        Preconditions.checkArgument(type != null, "Material cannot be null");
        this.aZ = type;
        this.amount = amount;
    }

    @NotNull
    public Map<String, Object> serialize() {
        LinkedHashMap var1 = new LinkedHashMap();
        var1.put("v", 0);
        var1.put("type", Registries.ITEM.getId(this.dq()).getPath().toUpperCase(Locale.ROOT));
        if (this.getAmount() != 1) {
            var1.put("amount", this.getAmount());
        }

        BukkitMetaItem var2 = this.dx();
        if (var2 != null) {
            var1.put("meta", var2);
        }

        return var1;
    }

    public void setType(@NotNull Item type) {
        Preconditions.checkArgument(type != null, "Material cannot be null");
        this.aZ = type;
        if (this.bb != null) {
            this.bb = BukkitSerializationMock.b().asMetaFor(this.bb, type);
        }
    }

    public BukkitItemStack(@NotNull Item type) {
        this(type, 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return !(obj instanceof BukkitItemStack var2)
                    ? false
                    : this.getAmount() == var2.getAmount() && this.du(var2);
        }
    }

    public boolean du(@Nullable BukkitItemStack stack) {
        if (stack == null) {
            return false;
        } else if (stack == this) {
            return true;
        } else {
            Item var2 = this.aZ;
            return var2 == stack.dq()
                    && this.hasItemMeta() == stack.hasItemMeta()
                    && (!this.hasItemMeta() || BukkitSerializationMock.b().equals(this.dx(), stack.dx()));
        }
    }

    public BukkitItemStack(@NotNull BukkitItemStack stack) throws IllegalArgumentException {
        Preconditions.checkArgument(stack != null, "Cannot copy null stack");
        this.aZ = stack.dq();
        this.amount = stack.getAmount();
        if (stack.hasItemMeta()) {
            this.setItemMeta0(stack.dx(), this.aZ);
        }
    }

    @NotNull
    public Item dq() {
        return this.aZ;
    }

    public BukkitItemStack(@NotNull Item type, int amount) {
        this(type, amount, (short) 0);
    }

    public boolean hasItemMeta() {
        return !BukkitSerializationMock.b().equals(this.bb, null);
    }

    @Nullable
    public BukkitMetaItem dx() {
        return this.bb == null ? BukkitSerializationMock.b().getItemMeta(this.aZ) : this.bb.MN();
    }

    private boolean setItemMeta0(@Nullable BukkitMetaItem itemMeta, @NotNull Item material) {
        this.bb = itemMeta;
        return true;
    }

    @NotNull
    public static BukkitItemStack deserialize(@NotNull Map<String, Object> args) {
        if (args.containsKey("schema_version")) {
            return CraftItemStack.Pa(args);
        } else {
            short damage = 0;
            int amount = 1;
            if (args.containsKey("damage")) {
                damage = ((Number) args.get("damage")).shortValue();
            }

            Item type;
            try {
                type = (Item) Registries.ITEM.get(
                        new Identifier("minecraft", ((String) args.get("type")).toLowerCase(Locale.ROOT)));
            } catch (Throwable var6) {
                type = Items.BARRIER;
            }

            if (args.containsKey("amount")) {
                amount = ((Number) args.get("amount")).intValue();
            }

            BukkitItemStack result = new BukkitItemStack(type, amount, damage);
            if (args.containsKey("meta")) {
                Object raw = args.get("meta");
                if (raw instanceof BukkitMetaItem) {
                    result.dz((BukkitMetaItem) raw);
                }
            }

            return result;
        }
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    protected BukkitItemStack() {}

    @Override
    public String toString() {
        StringBuilder var1 = new StringBuilder("ItemStack{")
                .append(Registries.ITEM.getId(this.dq()).getPath().toUpperCase(Locale.ROOT))
                .append(" x ")
                .append(this.getAmount());
        if (this.hasItemMeta()) {
            var1.append(", ").append(this.dx());
        }

        return var1.append('}').toString();
    }

    @NotNull
    public BukkitItemStack dv() {
        try {
            BukkitItemStack var1 = (BukkitItemStack) super.clone();
            if (this.bb != null) {
                var1.bb = this.bb.MN();
            }

            return var1;
        } catch (CloneNotSupportedException var3) {
            throw new Error(var3);
        }
    }
}
