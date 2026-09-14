package me.matl114.hacks.modules.slimefun;

import java.util.List;
import me.matl114.events.Event;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.ItemStackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ShowIdTooltips extends BaseModule {
    protected static char[] nA = new char[] {'B', 'C', 'D', 'F', 'S', 'W'};
    protected static final String LO = "cultivation:growth_speed";
    protected static String ny = "geneticchickengineering:gce_pocket_chicken_dna";
    public final ModulePath fo = makePath(Configs.p, "slimefun-settings");
    protected static char[] nz = new char[] {'b', 'c', 'd', 'f', 's', 'w'};
    protected static final String LM = "cultivation:seed_instance";
    protected static final Text LL = Text.literal("Slimefun").formatted(Formatting.BLUE);
    public final FlagRef ae = this.builder(this.fo.add("enable-tooltips-display"), Boolean.class)
            .defaultValue(true)
            .build();
    protected static final String LP = "cultivation:strength";
    protected static final String LN = "cultivation:drop_rate";

    public static void handleGCEInfo(String sfid, ItemStack stack, List<Text> lores) {
        if (sfid.startsWith("GCE_") && stack != null && ItemStackUtils.hasCustomData(stack)) {
            try {
                NbtCompound var3 = ItemStackUtils.getCustomDataReadOnly(stack);
                if ((var3 = ItemStackUtils.getBukkitValue(var3)) != null && var3.get(ny) instanceof NbtIntArray var5) {
                    int[] var11 = var5.getIntArray();
                    int var6 = var11.length;
                    StringBuilder var7 = new StringBuilder();

                    for (int var8 = 0; var8 < 6; var8++) {
                        if ((var6 <= var8 || var11[var8] != 0) && var11[var8] != 1 && var11[var8] != 3) {
                            var7.append("??");
                        } else {
                            var7.append(var11[var8] % 2 == 0 ? nz[var8] : nA[var8])
                                    .append(var11[var8] / 2 == 0 ? nz[var8] : nA[var8]);
                        }
                    }

                    lores.add(Text.literal("基因工程: ")
                            .formatted(Formatting.GRAY)
                            .append(Text.literal(var7.toString()).formatted(Formatting.DARK_PURPLE)));
                }
            } catch (Throwable var9) {
            }
        }
    }

    public static void handleCLTInfo(String sfid, ItemStack stack, List<Text> lores) {
        if (sfid.startsWith("CLT_PLANT") && stack != null && ItemStackUtils.hasCustomData(stack)) {
            MutableText var3 = Text.literal("农耕工艺: [").formatted(Formatting.GRAY);
            NbtCompound var4 = ItemStackUtils.U(stack);
            if (var4 != null) {
                try {
                    if (var4.contains("cultivation:seed_instance")) {
                        if (var4.get("cultivation:seed_instance") instanceof NbtCompound var6) {
                            int var12 = var6.get("cultivation:drop_rate") instanceof NbtInt var8 ? var8.intValue() : 0;
                            int var15 =
                                    var6.get("cultivation:growth_speed") instanceof NbtInt var13 ? var13.intValue() : 0;
                            int var14 = var6.get("cultivation:strength") instanceof NbtInt var16 ? var16.intValue() : 0;
                            var3.append(Text.literal("等级: ").formatted(Formatting.YELLOW));
                            var3.append(Text.literal(String.valueOf(var12)).formatted(Formatting.GRAY));
                            var3.append(Text.literal(" 速率: ").formatted(Formatting.YELLOW));
                            var3.append(Text.literal(String.valueOf(var15)).formatted(Formatting.GRAY));
                            var3.append(Text.literal(" 强度: ").formatted(Formatting.YELLOW));
                            var3.append(Text.literal(String.valueOf(var14)).formatted(Formatting.GRAY));
                        }
                    } else {
                        var3.append(Text.literal("未初始化属性").formatted(Formatting.RED));
                    }
                } catch (Throwable var11) {
                    var3.append(Text.literal("数据错误").formatted(Formatting.RED));
                }
            }

            var3.append(Text.literal("]").formatted(Formatting.GRAY));
            lores.add(var3);
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(RenderListener.x(), this::onTooltips);
    }

    public ShowIdTooltips() {
        super("ShowIdTooltips");
        this.bindFlag(this.ae);
    }

    public void onTooltips(Event<List<Text>> event) {
        if (this.isActive()) {
            ItemStack var2 = event.getArgs(0);
            String var3 = ItemStackUtils.aa(var2);
            if (var3 == null) {
                return;
            }

            List var4 = (List) event.e();
            boolean var5 = false;

            for (int var6 = 0; var6 < var4.size(); var6++) {
                String var7 = ((Text) var4.get(var6)).getString();
                if ("§9§oMinecraft".equals(var7)) {
                    var4.set(var6, LL);
                    var5 = true;
                }
            }

            if (!var5) {
                var4.add(LL);
            }

            var4.add(Text.literal("粘液物品ID: ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal(var3).formatted(Formatting.GREEN)));
            handleGCEInfo(var3, var2, var4);
            handleCLTInfo(var3, var2, var4);
        }
    }
}
