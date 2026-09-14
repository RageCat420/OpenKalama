package me.matl114.hacks.modules.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.matl114.events.Event;
import me.matl114.events.RenderListener;
import me.matl114.events.model.GuiModel;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.ResourceUtils;
import me.matl114.versioned.api.VItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.BowItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class EnchantmentDisplay extends BaseModule {
    public final FlagRef weapon;
    public final FlagRef equippable;
    public final Map<Item, List<RegistryKey<Enchantment>>> Cj;
    public static final Map<RegistryKey<Enchantment>, Identifier> Cd = Map.ofEntries(
            Map.entry(Enchantments.BANE_OF_ARTHROPODS, enchantmentIcon("icon_bane_of_arthropods")),
            Map.entry(Enchantments.BLAST_PROTECTION, enchantmentIcon("icon_blast_protection")),
            Map.entry(Enchantments.BREACH, enchantmentIcon("icon_breach")),
            Map.entry(Enchantments.DENSITY, enchantmentIcon("icon_density")),
            Map.entry(Enchantments.FIRE_PROTECTION, enchantmentIcon("icon_fire_protection")),
            Map.entry(Enchantments.FORTUNE, enchantmentIcon("icon_fortune")),
            Map.entry(Enchantments.POWER, enchantmentIcon("icon_power")),
            Map.entry(Enchantments.PROTECTION, enchantmentIcon("icon_protection")),
            Map.entry(Enchantments.SHARPNESS, enchantmentIcon("icon_sharpness")),
            Map.entry(Enchantments.SILK_TOUCH, enchantmentIcon("icon_silk_touch")),
            Map.entry(Enchantments.SMITE, enchantmentIcon("icon_smite")));
    public final FlagRef bow;
    public final ModulePath iv = makePath(Configs.i, "itemstack-display.enchantment-display");
    public final FlagRef tool;
    public final Map<Item, FlagRef> Ck;
    public final FlagRef ae =
            this.builder(this.iv.addEnable(), Boolean.class).defaultValue(false).build();
    public static final String cl = "kalama";
    public static final String cm = "enchantment_icon/";
    public final FlagRef Ce =
            this.builder(this.iv.add("mace"), Boolean.class).defaultValue(true).build();

    public void Qq(Event<Set<Identifier>> event) {
        ((Set) event.e()).addAll(ResourceUtils.b(event.getArgs(0), "enchantment_icon"));
    }

    public void Qr(Event<Set<Identifier>> event) {
        if (event.getArgs(1).equals(new Identifier("minecraft", "blocks"))) {
            ((Set) event.e()).addAll(ResourceUtils.c(event.getArgs(0), "enchantment_icon"));
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(RenderListener.v(), this::Qq);
        this.registerListener(RenderListener.w(), this::Qr);
        this.registerListener(RenderListener.o(), this::onInfoAttached);
    }

    public void onInfoAttached(Event<List<GuiModel>> event) {
        if (this.ae.get()) {
            ItemStack var2 = event.getArgs(0);
            if (var2.isEmpty()) {
                return;
            }

            if (this.Cj.containsKey(var2.getItem())) {
                FlagRef var3 = this.Ck.get(var2.getItem());
                if (var3 == null || !var3.get()) {
                    return;
                }

                List<RegistryKey<Enchantment>> var4 = this.Cj.get(var2.getItem());
                ItemEnchantmentsComponent var5 = (ItemEnchantmentsComponent) var2.get(DataComponentTypes.ENCHANTMENTS);
                if (var5 != null && !var5.isEmpty()) {
                    for (RegistryKey<Enchantment> var7 : var4) {
                        if (ItemStackUtils.getEnchantmentLevel(var5, var7) > 0) {
                            ((List) event.e()).add(GuiModel.of(Cd.get(var7)));
                            return;
                        }
                    }
                }
            }
        }
    }

    public EnchantmentDisplay() {
        super("EnchantDisplay");
        this.weapon = this.builder(this.iv.add("weapon"), Boolean.class)
                .defaultValue(true)
                .build();
        this.equippable = this.builder(this.iv.add("equippable"), Boolean.class)
                .defaultValue(true)
                .build();
        this.tool = this.builder(this.iv.add("tool"), Boolean.class)
                .defaultValue(true)
                .build();
        this.bow = this.builder(this.iv.add("bow"), Boolean.class)
                .defaultValue(false)
                .build();
        this.Cj = new HashMap<>();
        this.Ck = new HashMap<>();

        for (Item var2 : Registries.ITEM) {
            if (var2 instanceof MaceItem var3) {
                this.Cj.put(var2, List.of(Enchantments.DENSITY, Enchantments.BREACH));
                this.Ck.put(var2, this.Ce);
            } else if (VItem.w().c(new ItemStack(var2))) {
                this.Cj.put(var2, List.of(Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS));
                this.Ck.put(var2, this.weapon);
            } else if (var2 instanceof Equipment) {
                this.Cj.put(
                        var2,
                        List.of(Enchantments.PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.FIRE_PROTECTION));
                this.Ck.put(var2, this.equippable);
            } else if (var2.getComponents().contains(DataComponentTypes.TOOL)) {
                this.Cj.put(var2, List.of(Enchantments.FORTUNE, Enchantments.SILK_TOUCH));
                this.Ck.put(var2, this.tool);
            } else if (var2 instanceof BowItem var4) {
                this.Cj.put(var2, List.of(Enchantments.POWER));
                this.Ck.put(var2, this.bow);
            }
        }

        this.bindFlag(this.ae);
    }

    private static Identifier enchantmentIcon(String path) {
        return new Identifier("kalama", "enchantment_icon/" + path);
    }
}
