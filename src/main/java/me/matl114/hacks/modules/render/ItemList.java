package me.matl114.hacks.modules.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import me.matl114.events.Event;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.PrimitiveList;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.inventory.ItemStackSample;
import me.matl114.versioned.api.VDrawContext;
import me.matl114.versioned.api.VItem;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ItemList extends IRender2DColoredModule {
   public NBTRef<EntrySet<Item>> dx;
   public List<NbtPredicate> dy;
   public NBTRef<PrimitiveList<NbtCompound>> nbtPredicate;
   List<Text> dz = new ArrayList<>();
   FlagRef renderSimple;
   List<Text> dA = new ArrayList<>();
   FlagRef renderImportant;

   @Override
   protected void hJ() {
      super.hJ();
      this.renderSimple = this.flagBuilder(this.he.add("render-simple")).build();
      this.renderImportant = this.flagBuilder(this.he.add("render-important")).build();
      this.nbtPredicate = this.builder(this.he.add("nbt-predicate"), PrimitiveList.<NbtCompound>uA())
         .defaultValue(new PrimitiveList<NbtCompound>(NBTTypes.n, List.of()))
         .updateListener(s -> this.hG(s.list()))
         .<NBTRef<PrimitiveList<NbtCompound>>>build();
      Class<EntrySet<Item>> varT = EntrySet.parameter();
      me.matl114.hacks.api.WrapperSettingBuilder varB = this.builder(this.he.add("item-type"), varT);
      varB.defaultValue(
         new EntrySet(
            new Regex("^(.*ton_skull|netherite.*|.*_star|.*_apple|.*potion|tot.*|end_c.*l|obsi.*|.*anchor|expe.*|mace|ely.*|.*shulker.*|trident)$"),
            Registries.ITEM
         )
      );
      this.dx = (NBTRef<EntrySet<Item>>)(Object)varB.build();
   }

   public boolean testItem(ItemStack stack) {
      return this.dx.get().test(stack.getItem()) || this.dy != null && this.testItemData(stack);
   }

   public ItemList() {
      super("ItemList");
   }

   private boolean testItemData(ItemStack stack) {
      ComponentChanges var2 = stack.getComponentChanges();

      try {
         NbtCompound var3 = var2.isEmpty()
            ? new NbtCompound()
            : (NbtCompound)ComponentChanges.CODEC.encodeStart(ItemStackUtils.registry().getOps(NbtOps.INSTANCE), var2).getOrThrow();

         for (NbtPredicate var5 : this.dy) {
            if (var5.test(var3)) {
               return true;
            }
         }

         return false;
      } catch (Throwable var6) {
         return false;
      }
   }

   @Override
   public void hK(Event<Void> event) {
      this.dz.clear();
      this.dA.clear();
      if (!checkNull()) {
         if (this.enable2.get()) {
            HashMap<ItemStackSample, Integer> var2 = new HashMap<>();

            for (Entity var4 : mc.world.getEntities()) {
               if (var4 instanceof ItemEntity var5) {
                  ItemStack var6 = var5.getStack();
                  var2.merge(ItemStackSample.of(var6), var6.getCount(), Integer::sum);
               }
            }

            for (Entry<ItemStackSample, Integer> var8 : var2.entrySet()) {
               MutableText var9 = ChatUtils.builder()
                  .withColorString("&f")
                  .appendText(VItem.w().l(((ItemStackSample)var8.getKey()).fS()))
                  .withColorString("&f x" + var8.getValue())
                  .end()
                  .build();
               if (this.renderImportant.get() && this.testItem(((ItemStackSample)var8.getKey()).fS())) {
                  this.dA.add(var9);
               } else if (this.renderSimple.get()) {
                  this.dz.add(var9);
               }
            }
         }
      }
   }

   public void hG(List<NbtCompound> compound) {
      if (compound != null && !compound.isEmpty()) {
         this.dy = compound.stream().<NbtPredicate>map(NbtPredicate::new).toList();
      } else {
         this.dy = null;
      }
   }

   @Override
   protected ModulePath createRoot() {
      return makePath(Configs.i, "detect-entity.item-list");
   }

   @Override
   public void render2D(VDrawContext vdraw, float partialTicks) {
      if (this.enable2.get()) {
         if (!this.dA.isEmpty() && this.renderImportant.get()) {
            this.gj(vdraw, "重要物品:");

            for (Text var4 : this.dA) {
               this.drawText(vdraw, var4);
            }
         }

         if (!this.dz.isEmpty() && this.renderSimple.get()) {
            this.gj(vdraw, "物品");

            for (Text var6 : this.dz) {
               this.drawText(vdraw, var6);
            }
         }
      }
   }
}
