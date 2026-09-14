package me.matl114.hacks.modules.render;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Stream;
import me.matl114.events.Event;
import me.matl114.gui.presets.single.RegistryDisplays;
import me.matl114.gui.presets.single.RegistryDisplays$IIcon;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.inventory.ItemStackSample;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class PlayerStatistic extends IRender2DColoredModule {
    public final FlagRef enableItems;
    public final FlagRef enablePotions;
    public final FlagRef enableEffect;
    private static final int ITEM_ROW_HEIGHT = 9;
    public final NBTRef<EntrySet<Item>> itemTypes;
    public final NBTRef<EntrySet<Potion>> potionTypes;
    public final ModulePath lj = makePath(Configs.i, "in-game-hud");
    public final ModulePath he = this.lj.add("player-statistic");
    private static final RegistryDisplays$IIcon<StatusEffect> statusEffectRenderer =
            RegistryDisplays.f(StatusEffect.class);

    public PlayerStatistic() {
        super("Statistic");
        this.enableItems = this.flagBuilder(this.he.add("enable-items")).build();
        this.itemTypes = this.builder(this.he.add("item-types"), EntrySet.<Item>parameter())
                .defaultValue(
                        new EntrySet<Item>(Registries.ITEM, List.of(Items.TOTEM_OF_UNDYING, Items.FIREWORK_ROCKET)))
                .build();
        this.enablePotions = this.flagBuilder(this.he.add("enable-potions")).build();
        this.potionTypes = this.builder(this.he.add("potion-types"), EntrySet.<Potion>parameter())
                .defaultValue(new EntrySet<Potion>(Registries.POTION, List.of((Potion) Potions.TURTLE_MASTER.value())))
                .build();
        this.enableEffect = this.flagBuilder(this.he.add("enable-effect")).build();
    }

    public void handleEffects(VDrawContext vdraw) {
        for (StatusEffectInstance var3 : mc.player.getStatusEffects()) {
            OrderedText var4 = StatusEffectUtil.getDurationText(
                            var3, 1.0F, mc.world.getTickManager().getTickRate())
                    .asOrderedText();
            float var5 = mc.textRenderer.getTextHandler().getWidth(var4);
            vdraw.b();
            if (this.right2.get()) {
                vdraw.f().translate(-var5 - 9.0F, 0.0F);
            }

            vdraw.b();
            vdraw.f().scale(0.5F, 0.5F);
            statusEffectRenderer.a(
                    1, 1, vdraw, (StatusEffect) var3.getEffectType().value());
            int var6 = var3.getAmplifier();
            if (var6 > 0) {
                String var7 = String.valueOf(var6 + 1);
                vdraw.A(mc.textRenderer, var7, 17 - mc.textRenderer.getWidth(var7), 9, -1, true);
            }

            vdraw.c();
            vdraw.z(mc.textRenderer, var4, 9, 0, this.ct.get().withAlpha(255), true);
            vdraw.c();
            vdraw.f().translate(0.0F, 9.0F);
        }
    }

    @Override
    public void render2D(VDrawContext vdraw, float partialTicks) {
        if (this.enableItems.get()) {
            for (Item var4 : this.itemTypes.get().set()) {
                this.AK(vdraw, var4);
            }
        }

        if (this.enablePotions.get()) {
            for (Potion var6 : this.potionTypes.get().set()) {
                this.handleTurtle(vdraw, var6);
            }
        }

        if (this.enableEffect.get()) {
            this.handleEffects(vdraw);
        }
    }

    @Override
    public void hK(Event<Void> event) {}

    @Override
    protected ModulePath createRoot() {
        return makePath(Configs.i, "in-game-hud").add("player-statistic");
    }

    public void handleTurtle(VDrawContext vdraw, Potion potionType) {
        Map<ItemStackSample, Integer> var3 = PlayerStateManager.INSTANCE.jM;
        int var4;
        if (var3 != null) {
            var4 = var3.entrySet().stream()
                    .filter(s -> this.isTurtle(((ItemStackSample) s.getKey()).fS(), potionType))
                    .mapToInt(Entry::getValue)
                    .sum();
        } else {
            var4 = 0;
        }

        this.drawItemStatistic(
                vdraw, PotionContentsComponent.createStack(Items.POTION, Registries.POTION.getEntry(potionType)), var4);
    }

    private void drawItemStatistic(VDrawContext vdraw, ItemStack stack, int count) {
        OrderedText var4 = Text.literal(String.valueOf(count)).asOrderedText();
        vdraw.b();
        if (this.right2.get()) {
            vdraw.f().translate(-9.0F, 0.0F);
        }

        vdraw.f().pushMatrix();
        vdraw.f().scale(0.5F, 0.5F);
        vdraw.K(stack, 0, 0, 0, 0);
        vdraw.f().popMatrix();
        if (!this.right2.get()) {
            vdraw.f().translate(9.0F, 0.0F);
        } else {
            int var5 = mc.textRenderer.getWidth(var4);
            vdraw.f().translate(-var5, 0.0F);
        }

        vdraw.z(mc.textRenderer, var4, 0, 0, this.ct.get().withAlpha(255), true);
        vdraw.c();
        vdraw.f().translate(0.0F, 9.0F);
    }

    public void AK(VDrawContext vdraw, Item itemType) {
        Map<ItemStackSample, Integer> var3 = PlayerStateManager.INSTANCE.jM;
        int var4;
        if (var3 != null) {
            var4 = var3.entrySet().stream()
                    .filter(s -> ((ItemStackSample) s.getKey()).fS().isOf(itemType))
                    .mapToInt(Entry::getValue)
                    .sum();
        } else {
            var4 = 0;
        }

        this.drawItemStatistic(vdraw, new ItemStack(itemType), var4);
    }

    private boolean isTurtle(ItemStack stack, Potion potionType) {
        PotionContentsComponent var3 = (PotionContentsComponent) stack.get(DataComponentTypes.POTION_CONTENTS);
        if (var3 != null) {
            RegistryEntry var4 = (RegistryEntry) var3.potion().orElse(null);
            if (var4 == null) {
                return false;
            } else if (Objects.equals(var4, potionType)) {
                return true;
            } else {
                Stream var5 = potionType.getEffects().stream().map(StatusEffectInstance::getEffectType);
                Stream var6 = ((Potion) var4.value()).getEffects().stream().map(StatusEffectInstance::getEffectType);
                return Objects.equals(var5, var6);
            }
        } else {
            return false;
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
    }
}
