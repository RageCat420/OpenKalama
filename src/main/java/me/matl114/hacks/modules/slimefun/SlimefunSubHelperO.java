package me.matl114.hacks.modules.slimefun;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import me.matl114.hacks.utils.multiblock.BlockMatcher;
import me.matl114.hacks.utils.multiblock.HackUtilHelperB;
import me.matl114.hacks.utils.multiblock.HackUtilHelperC;
import me.matl114.utils.collections.Point;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.registry.tag.BlockTags;

public record SlimefunSubHelperO(String id, BlockMatcher[] blockTypes, Collection<Point> optionalActionBlock, SlimefunSubHelperN lookup, boolean symm)
   implements BlockMatcher {
   private static final HackUtilHelperC Vx = new HackUtilHelperC(BlockTags.LOGS);
   private static final HackUtilHelperC VA = new HackUtilHelperC(BlockTags.WOODEN_FENCES);
   private static final HackUtilHelperC Vy = new HackUtilHelperC(BlockTags.WOODEN_TRAPDOORS);
   private static final BlockMatcher VB = new SlimefunSubHelperL();
   private static final HackUtilHelperC Vz = new HackUtilHelperC(BlockTags.WOODEN_SLABS);

   public BlockMatcher[] blockTypes() {
      return this.blockTypes;
   }

   public boolean symm() {
      return this.symm;
   }

   public SlimefunSubHelperN lookup() {
      return this.lookup;
   }

   public static SlimefunSubHelperO of(ItemStack[] inputs, String id) {
      if (inputs.length != 9) {
         return null;
      } else {
         BlockMatcher[] var2 = new BlockMatcher[9];

         for (int var3 = 0; var3 < 9; var3++) {
            Item var4 = inputs[var3].getItem();
            Block var5 = null;
            if (var4 == Items.AIR) {
               var5 = Blocks.AIR;
            } else if (var4 == Items.FLINT_AND_STEEL) {
               var5 = Blocks.FIRE;
            } else {
               var5 = Block.getBlockFromItem(var4);
            }

            Reference var6 = var5.getRegistryEntry();
            int var7 = (2 - var3 / 3) * 3 + var3 % 3;
            if (var6.isIn(BlockTags.LOGS)) {
               var2[var7] = Vx;
            } else if (var6.isIn(BlockTags.WOODEN_TRAPDOORS)) {
               var2[var7] = Vy;
            } else if (var6.isIn(BlockTags.WOODEN_SLABS)) {
               var2[var7] = Vz;
            } else if (var6.isIn(BlockTags.WOODEN_FENCES)) {
               var2[var7] = VA;
            } else if (var6.isIn(BlockTags.FIRE)) {
               var2[var7] = VB;
            } else if (var4 == Items.AIR) {
               var2[var7] = Wf;
            } else {
               var2[var7] = (BlockMatcher)(var5 == Blocks.AIR ? Wg : new HackUtilHelperB(var5));
            }
         }

         boolean var8 = true;

         for (int var9 = 0; var9 < 3; var9++) {
            if (!Objects.equals(var2[3 * var9], var2[3 * var9 + 2])) {
               var8 = false;
               break;
            }
         }

         HashSet var10 = new HashSet();
         if (var2[7].match(Blocks.DISPENSER)) {
            if (var2[4].match(Blocks.DISPENSER)) {
               if (var2[1].match(Blocks.DISPENSER)) {
                  var10.add(new Point(1, 1));
                  var10.add(new Point(1, 2));
               } else {
                  var10.add(new Point(1, 0));
               }
            } else {
               var10.add(new Point(1, 1));
            }
         } else {
            var10.add(new Point(1, 2));
         }

         SlimefunSubHelperN var12 = new SlimefunSubHelperN(var2, var8);
         return new SlimefunSubHelperO(id, var2, var10, var12, var8);
      }
   }

   public Collection<Point> optionalActionBlock() {
      return this.optionalActionBlock;
   }

   public SlimefunSubHelperO(String id, BlockMatcher[] blockTypes, Collection<Point> optionalActionBlock, SlimefunSubHelperN lookup, boolean symm) {
      this.id = id;
      this.blockTypes = blockTypes;
      this.optionalActionBlock = optionalActionBlock;
      this.lookup = lookup;
      this.symm = symm;
   }

   @Override
   public Set<Block> getPotentials() {
      HashSet var1 = new HashSet();
      var1.addAll(this.blockTypes[1].getPotentials());
      var1.addAll(this.blockTypes[4].getPotentials());
      var1.addAll(this.blockTypes[7].getPotentials());
      return var1;
   }
}
