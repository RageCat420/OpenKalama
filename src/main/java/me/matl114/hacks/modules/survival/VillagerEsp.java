package me.matl114.hacks.modules.survival;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.render.HackUtilHelperB;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerProfession;

public class VillagerEsp extends BaseModule {
   public final NBTRef<WrapColor> color;
   public final DoubleRef textScale;
   public final ModulePath Wh = makePath(Configs.o, "render-utils");
   public final ModulePath Wi = this.Wh.add("villager-esp");
   public static VillagerEsp INSTANCE;
   public final KeyBindRef J;
   private final RenderCollector<HackUtilHelperB> gP;
   public final FlagRef ae = this.flagBuilder(this.Wi.addEnable()).build();

   public void onTick(Event<ClientPlayerEntity> event) {
      this.gP.clear();
      if (!checkNull() && this.ae.get() && WorldManager.INSTANCE != null) {
         int var2 = this.color.get().withAlpha(255);
         float var3 = (float)this.textScale.get();

         for (Entity var5 : mc.world.getEntities()) {
            if (var5 instanceof VillagerEntity var6 && this.isTrackedLibrarian(var6)) {
               Text var7 = this.buildTradeText(var6);
               if (var7 != null) {
                  Vec3d var8 = var6.getPos().add(0.0, var6.getHeight(), 0.0);
                  this.gP.submit(new HackUtilHelperB(var7, var8, var3), var2);
               }
            }
         }
      }
   }

   private boolean isTrackedLibrarian(VillagerEntity villager) {
      VillagerProfession var2 = villager.getVillagerData().getProfession();
      return Objects.equals(var2, VillagerProfession.LIBRARIAN) && WorldManager.INSTANCE.afb(villager) != null;
   }

   private Text buildEnchantmentTradeText(SurvivalSubHelperS trade) {
      ItemStack var2 = trade.result();
      if (var2.isOf(Items.ENCHANTED_BOOK) && var2.contains(DataComponentTypes.STORED_ENCHANTMENTS)) {
         Entry var3 = (Entry)((ItemEnchantmentsComponent)var2.get(DataComponentTypes.STORED_ENCHANTMENTS))
            .getEnchantmentEntries()
            .stream()
            .findFirst()
            .orElse(null);
         if (var3 == null) {
            return null;
         } else {
            RegistryEntry var4 = (RegistryEntry)var3.getKey();
            int var5 = var3.getIntValue();
            int var6 = Math.max(trade.result().getCount(), trade.buy1().getCount());
            return ((Enchantment)var4.value()).description().copy().append(Text.literal(String.valueOf(var5))).append(Text.literal(" " + var6));
         }
      } else {
         return null;
      }
   }

   public VillagerEsp() {
      super("VillagerEsp");
      this.J = this.moduleEntry(this.Wi.addHotkey(), new MultiKeyBind(), this.Wi.addEnable()).build();
      this.textScale = this.doubleBuilder(this.Wi.add("text-scale")).defaultValue(0.75).validator(Configs.doubleRange(0.1, 4.0)).build();
      this.color = this.builder(this.Wi.add("color"), WrapColor.class).defaultValue(new WrapColor(Formatting.AQUA)).build();
      this.gP = RenderCollectors.f();
      INSTANCE = this;
      this.bindFlag(this.ae);
   }

   public void Gn(Event<MatrixStack> event) {
      if (this.ae.get()) {
         RenderUtils.startDrawVirtual((MatrixStack)event.e());

         try {
            this.gP.a((MatrixStack)event.e());
         } finally {
            RenderUtils.stopDrawVirtual((MatrixStack)event.e());
         }
      }
   }

   private Text buildTradeText(VillagerEntity villager) {
      List<SurvivalSubHelperS> var2 = WorldManager.INSTANCE.afb(villager);
      if (var2 != null && !var2.isEmpty()) {
         ArrayList var3 = new ArrayList();

         for (SurvivalSubHelperS var5 : var2) {
            Text var6 = this.buildEnchantmentTradeText(var5);
            if (var6 != null) {
               var3.add(var6);
            }
         }

         if (var3.isEmpty()) {
            return null;
         } else {
            MutableText var7 = Text.empty();

            for (int var8 = 0; var8 < var3.size(); var8++) {
               var7 = var7.append(Text.literal("\n")).append((Text)var3.get(var8));
            }

            return var7;
         }
      } else {
         return null;
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.V(), this::onTick);
      this.registerListener(RenderListener.q(), this::Gn);
   }

   @Override
   public void onDisableModule() {
      super.onDisableModule();
      this.gP.clear();
   }
}
