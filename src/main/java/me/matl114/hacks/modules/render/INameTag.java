package me.matl114.hacks.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.matl114.SlimefunHelper;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.gui.presets.single.RegistryDisplays;
import me.matl114.gui.presets.single.RegistryDisplays$IIcon;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.combat.TargetSelector;
import me.matl114.hacks.modules.move.MoveSubHelperL;
import me.matl114.hacks.modules.move.MoveSubHelperYX;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.render.ItemStackDisplayUtils$DamageDisplay;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.ChatUtils;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;

public class INameTag extends BaseModule {
   protected static final EquipmentSlot[] SLOTS = new EquipmentSlot[]{
      EquipmentSlot.MAINHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND
   };
   public final NBTRef<WrapColor> popColor;
   public final NBTRef<WrapColor> healthColor;
   public final FlagRef potion;
   public final NBTRef<WrapColor> distanceColor;
   protected static final RegistryDisplays$IIcon<StatusEffect> rH = RegistryDisplays.f(StatusEffect.class);
   public final ModulePath GE = this.createRoot();
   public final FlagRef enchantProtectionSum;
   public final EnumRef<ItemStackDisplayUtils$DamageDisplay> equipmentDamage;
   public final NBTRef<WrapColor> potionColor;
   public final NBTRef<WrapColor> nameColor;
   public final FlagRef pop;
   public final NBTRef<WrapColor> friendNameColor;
   List<RenderSubHelperIX> GU;
   public final FlagRef health;
   public final NBTRef<WrapColor> pingColor;
   protected static final float HEIGHT = 9.0F;
   public final NBTRef<WrapColor> otherInfoColor;
   public final FlagRef distance;
   public final FlagRef equipment;
   public static final Text GW = ChatUtils.textFromLegacyString("§x§e§b§3§3§e§b§l[§x§d§6§2§6§d§6§lD§x§c§1§1§a§c§1§le§x§a§c§0§d§a§c§lv§x§9§7§0§0§9§7§l]");
   public final FlagRef ae = this.flagBuilder(this.GE.addEnable()).build();
   public final FlagRef ping;

   public void onUpdate(Event<Void> eventGameUpdate) {
      if (!checkNull() && this.ae.get()) {
         this.GU = new ArrayList<>();

         for (AbstractClientPlayerEntity var3 : mc.world.getPlayers()) {
            if (var3 instanceof ClientPlayerEntity || var3 instanceof OtherClientPlayerEntity) {
               MutableText var4 = Text.empty();
               String var5 = var3.getNameForScoreboard();
               if (var5 != null) {
                  if (SlimefunHelper.DEV_NAME.contains(var5)) {
                     var4.append(GW);
                  }

                  boolean var6 = TargetSelector.INSTANCE.isInFriendList(var3);
                  if (var6) {
                     var4.append(
                        ChatUtils.textFromLegacyString("&x&F&F&A&C&0&0[" + TargetSelector.INSTANCE.akT().aad(var3.getNameForScoreboard()) + "&x&F&F&A&C&0&0]")
                     );
                  }

                  if (var3.isCreative()) {
                     var4.append(Text.literal("[C]").withColor(Color.RED.getRGB()));
                  }

                  var4.append(var3.getDisplayName().copy().withColor(var6 ? this.friendNameColor.get().asRGB() : this.nameColor.get().asRGB()));
                  if (this.health.get()) {
                     var4.append(Text.literal(" %d♥".formatted((int)var3.getHealth())).withColor(this.healthColor.get().asRGB()));
                  }

                  if (this.ping.get()) {
                     int var7 = 0;
                     PlayerListEntry var8 = mc.getNetworkHandler().getPlayerListEntry(var3.getUuid());
                     if (var8 != null) {
                        var7 = var8.getLatency();
                     }

                     var4.append(Text.literal(" %dms".formatted(var7)).withColor(this.pingColor.get().asRGB()));
                  }

                  if (this.distance.get()) {
                     double var9 = mc.player.getPos().distanceTo(var3.getPos());
                     var4.append(Text.literal(" d:%.1fm".formatted(var9)).withColor(this.distanceColor.get().asRGB()));
                  }

                  if (this.pop.get()) {
                     int var17 = PlayerStateManager.INSTANCE.getPlayerPopCount(var3);
                     if (var17 > 0) {
                        var4.append(Text.literal(" -%d".formatted(var17)).withColor(this.popColor.get().asRGB()));
                     }
                  }

                  ItemStack[] var18 = null;
                  if (this.equipment.get()) {
                     var18 = new ItemStack[6];
                     boolean var19 = false;

                     for (int var11 = 0; var11 < 6; var11++) {
                        ItemStack var12 = var3.getEquippedStack(SLOTS[var11]);
                        var18[var11] = var12;
                        if (!var12.isEmpty()) {
                           var19 = true;
                        }
                     }

                     if (!var19) {
                        var18 = null;
                     }
                  }

                  ArrayList<Text> var20 = new ArrayList();
                  if (this.enchantProtectionSum.get()) {
                     MoveSubHelperL var21 = PlayerStateManager.INSTANCE.nZ(var3);
                     if (var21 != null) {
                        if (var21.c > 0) {
                           var20.add(Text.literal("保护%d".formatted(var21.c)).withColor(this.otherInfoColor.get().asRGB()));
                        }

                        if (var21.d > 0) {
                           var20.add(Text.literal("爆炸%d".formatted(var21.d)).withColor(this.otherInfoColor.get().asRGB()));
                        }
                     }
                  }

                  MutableText var22;
                  if (var20.isEmpty()) {
                     var22 = null;
                  } else {
                     var22 = Text.empty();
                     boolean var23 = true;

                     for (Text var14 : var20) {
                        if (var23) {
                           var23 = false;
                        } else {
                           var22.append(Text.literal(" "));
                        }

                        var22.append(var14);
                     }
                  }

                  LinkedHashMap var24 = null;
                  if (this.potion.get()) {
                     MoveSubHelperL var25 = PlayerStateManager.INSTANCE.nZ(var3);
                     if (var25 != null) {
                        Map var26 = var25.i;
                        var24 = new LinkedHashMap(var26.size());

                        for (Entry var16 : ((java.util.Set<Entry>)(var26).entrySet())) {
                           if (((MoveSubHelperYX)var16.getValue()).visible) {
                              var24.put((RegistryEntry)var16.getKey(), getDurationText(((MoveSubHelperYX)var16.getValue()).getRemainDurations()));
                           }
                        }
                     }
                  }

                  this.GU.add(new RenderSubHelperIX(var3, var4, var18, var22, var24));
               }
            }
         }
      } else {
         this.GU = null;
      }
   }

   public INameTag() {
      this("INameTag");
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(RenderListener.r(), this::B);
      this.registerListener(Listener.T(), this::onUpdate);
   }

   private static Text getDurationText(int duration) {
      if (duration > 2147483646) {
         return Text.translatable("effect.duration.infinite");
      } else {
         int var1 = MathHelper.floor(duration);
         return Text.literal(StringHelper.formatTicks(var1, mc.world.getTickManager().getTickRate()));
      }
   }

   protected abstract void gl();

   public INameTag(String name) {
      super(name);
      this.gl();
      this.health = this.flagBuilder(this.GE.add("health")).build();
      this.ping = this.flagBuilder(this.GE.add("ping")).build();
      this.distance = this.flagBuilder(this.GE.add("distance")).build();
      this.equipment = this.flagBuilder(this.GE.add("equipment")).build();
      this.equipmentDamage = this.builder(this.GE.add("equipment-damage"), ItemStackDisplayUtils$DamageDisplay.class)
         .defaultValue(ItemStackDisplayUtils$DamageDisplay.NONE)
         .build();
      this.pop = this.flagBuilder(this.GE.add("pop")).build();
      this.enchantProtectionSum = this.flagBuilder(this.GE.add("enchant-protection-sum")).build();
      this.potion = this.flagBuilder(this.GE.add("potion")).build();
      this.nameColor = this.builder(this.GE.add("name-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.WHITE)).build();
      this.friendNameColor = this.builder(this.GE.add("friend-name-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.WHITE)).build();
      this.healthColor = this.builder(this.GE.add("health-color"), WrapColor.class).defaultValue(new WrapColor(new Color(20, 170, 170))).build();
      this.pingColor = this.builder(this.GE.add("ping-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.GREEN)).build();
      this.distanceColor = this.builder(this.GE.add("distance-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.RED)).build();
      this.popColor = this.builder(this.GE.add("pop-color"), WrapColor.class).defaultValue(new WrapColor(Color.ORANGE)).build();
      this.otherInfoColor = this.builder(this.GE.add("other-info-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.YELLOW)).build();
      this.potionColor = this.builder(this.GE.add("potion-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.WHITE)).build();
      this.bindFlag(this.ae);
   }

   public void B(Event<VDrawContext> var1) { }

   protected abstract ModulePath createRoot();
}
