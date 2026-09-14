package me.matl114.hacks;

import java.util.List;
import me.matl114.events.annotations.Modifiable;
import me.matl114.hacks.api.ModuleGroup;
import me.matl114.hacks.api.ModuleManager;
import me.matl114.hacks.modules.HackModules;
import me.matl114.hacks.modules.combat.AnchorAura;
import me.matl114.hacks.modules.combat.Attack;
import me.matl114.hacks.modules.combat.AttackAura;
import me.matl114.hacks.modules.combat.AutoCity;
import me.matl114.hacks.modules.combat.AutoTotem;
import me.matl114.hacks.modules.combat.AutoWeb;
import me.matl114.hacks.modules.combat.BackTrack;
import me.matl114.hacks.modules.combat.Blink;
import me.matl114.hacks.modules.combat.BowEnhance;
import me.matl114.hacks.modules.combat.BowTp;
import me.matl114.hacks.modules.combat.CombatExtra;
import me.matl114.hacks.modules.combat.CombatLog;
import me.matl114.hacks.modules.combat.CombatManager;
import me.matl114.hacks.modules.combat.Criticals;
import me.matl114.hacks.modules.combat.CrystalAura;
import me.matl114.hacks.modules.combat.ElytraBot;
import me.matl114.hacks.modules.combat.PearlFly;
import me.matl114.hacks.modules.combat.PositionPredict;
import me.matl114.hacks.modules.combat.ProjectileEnhance;
import me.matl114.hacks.modules.combat.SpearAttack;
import me.matl114.hacks.modules.combat.SpearEnhance;
import me.matl114.hacks.modules.combat.TargetSelector;
import me.matl114.hacks.modules.combat.TotemLog;
import me.matl114.hacks.modules.combat.TransactionBlocker;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.versioned.api.VItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus.Experimental;

public class CombatTasks {
   private static Criticals i;
   private static AutoCity u;
   private static ElytraBot y;
   private static PositionPredict f;
   private static AutoTotem n;
   private static TotemLog o;
   private static BackTrack s;
   @Modifiable
   public static final ModuleGroup b = new ModuleGroup("Combat");
   private static SpearAttack q;
   private static BowTp l;
   private static CombatManager d;
   private static final MinecraftClient a = MinecraftClient.getInstance();
   private static Blink r;
   private static BowEnhance j;
   @Experimental
   private static TransactionBlocker z;
   private static PearlFly t;
   private static Attack g;
   private static CrystalAura v;
   private static AttackAura h;
   private static ProjectileEnhance k;
   private static AutoWeb w;
   private static CombatExtra c;
   private static AnchorAura x;
   private static TargetSelector e;
   private static CombatLog m;
   private static SpearEnhance p;

   public static CombatExtra j() {
      return c;
   }

   public static boolean isWeaponForMCPlayer(ItemStack itemStack) {
      AttributeModifiersComponent var1 = (AttributeModifiersComponent)itemStack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      if (var1 != null && !var1.modifiers().isEmpty()) {
         return true;
      } else {
         ItemEnchantmentsComponent var2 = (ItemEnchantmentsComponent)itemStack.get(DataComponentTypes.ENCHANTMENTS);
         return var2 != null
            && (ItemStackUtils.getEnchantmentLevel(var2, Enchantments.SHARPNESS) > 0 || ItemStackUtils.getEnchantmentLevel(var2, Enchantments.SMITE) > 0);
      }
   }

   public static AutoTotem u() {
      return n;
   }

   public static PositionPredict m() {
      return f;
   }

   public static BowTp s() {
      return l;
   }

   public static AnchorAura E() {
      return x;
   }

   public static ElytraBot F() {
      return y;
   }

   static {
      b.registerFactories(CombatTasks::initModules);
      HackModules.registerModuleGroup(b);
   }

   public static TargetSelector l() {
      return e;
   }

   public static ProjectileEnhance r() {
      return k;
   }

   public static PearlFly A() {
      return t;
   }

   @Modifiable
   public static boolean attackEntity(PlayerEntity player, Entity target) {
      return g.Yh(target);
   }

   public static CombatManager k() {
      return d;
   }

   public static Criticals p() {
      return i;
   }

   public static SpearEnhance w() {
      return p;
   }

   public static TransactionBlocker G() {
      return z;
   }

   public static BackTrack z() {
      return s;
   }

   public static SpearAttack x() {
      return q;
   }

   public static boolean isHoldingWeapon(ClientPlayerEntity player) {
      ItemStack var1 = player.getStackInHand(Hand.MAIN_HAND);
      return var1 != null && isWeaponForMCPlayer(var1);
   }

   public static AttackAura o() {
      return h;
   }

   @Modifiable
   public static List<Entity> e() {
      return l().akF(true);
   }

   public static void init() {
   }

   public static Vec2f calculatePitchYawPredict(float velocity, Vec3d extraVector, Vec3d targetVec) {
      double var3 = extraVector.length();
      float var5 = 0.05F;
      if (!(var3 > 10.0) && !(velocity > 10.0F)) {
         double var6 = targetVec.horizontalLength();
         double var8 = var6 * var6;
         float var10 = velocity * velocity;
         float var11 = var10 * var10;
         double var12 = targetVec.y - var6 * extraVector.y / velocity;
         Vec3d var14 = targetVec.normalize();
         Vec2f var15 = new Vec2f(
            (float)(-Math.toDegrees(Math.atan((var10 - Math.sqrt(var11 - 0.05F * (0.05F * var8 + 2.0 * var12 * var10))) / (0.05F * var6)))),
            (float)Math.toDegrees(Math.atan2(-var14.x, var14.z))
         );
         if (var3 < 1.0E-4) {
            return var15;
         } else {
            double var16 = 1.0E-4;
            byte var18 = 30;
            double var19 = Math.sqrt(targetVec.x * targetVec.x + targetVec.z * targetVec.z);
            Vec3d var21 = var19 > 1.0E-4 ? new Vec3d(targetVec.x / var19, 0.0, targetVec.z / var19) : new Vec3d(1.0, 0.0, 0.0);
            if (var19 < 1.0E-4) {
               return var15;
            } else {
               double var22 = 0.0;
               double var24 = 0.0;
               double var26 = extraVector.x;
               double var28 = extraVector.z;
               double var30 = extraVector.y;
               double var32 = var21.x;
               double var34 = var21.z;
               boolean var36 = false;

               for (int var37 = 0; var37 < 30; var37++) {
                  double var38 = Math.cos(var22);
                  double var40 = Math.sin(var22);
                  if (Math.abs(velocity * var38) < 1.0E-5) {
                     break;
                  }

                  double var42 = var26 * var32 + var28 * var34;
                  double var44 = var26 * var26 + var28 * var28 - velocity * var38 * (velocity * var38);
                  double var46 = var42 * var42 - var44;
                  if (var46 < 0.0) {
                     break;
                  }

                  double var48 = var42 + Math.sqrt(var46);
                  if (var48 <= 1.0E-5) {
                     break;
                  }

                  double var50 = (var48 * var32 - var26) / (velocity * var38);
                  double var52 = (var48 * var34 - var28) / (velocity * var38);
                  double var54 = Math.sqrt(var50 * var50 + var52 * var52);
                  if (var54 < 1.0E-5) {
                     break;
                  }

                  var50 /= var54;
                  var52 /= var54;
                  var24 = Math.atan2(var52, var50);
                  double var56 = var19 / var48;
                  double var58 = (targetVec.y + 0.025F * var56 * var56) * var48 / var19;
                  var58 = (var58 - var30) / velocity;
                  if (Math.abs(var58) > 1.0) {
                     break;
                  }

                  double var60 = Math.asin(var58);
                  if (Math.abs(var60 - var22) < 1.0E-4) {
                     var36 = true;
                     var22 = var60;
                     break;
                  }

                  var22 = var60;
               }

               return var36 ? new Vec2f((float)(-Math.toDegrees(var22)), (float)(Math.toDegrees(var24) - 90.0)) : var15;
            }
         }
      } else {
         return EntityUtils.q(targetVec.normalize());
      }
   }

   public static CrystalAura C() {
      return v;
   }

   private static void initModules(ModuleManager m) {
      c = new CombatExtra().register(m);
      d = new CombatManager().register(m);
      e = new TargetSelector().register(m);
      f = new PositionPredict().register(m);
      g = new Attack().register(m);
      h = new AttackAura().register(m);
      i = new Criticals().register(m);
      j = new BowEnhance().register(m);
      l = new BowTp().register(m);
      k = new ProjectileEnhance().register(m);
      CombatTasks.m = new CombatLog().register(m);
      n = new AutoTotem().register(m);
      o = new TotemLog().register(m);
      p = new SpearEnhance().register(m);
      q = new SpearAttack().register(m);
      r = new Blink().register(m);
      s = new BackTrack().register(m);
      t = new PearlFly().register(m);
      u = new AutoCity().register(m);
      v = new CrystalAura().register(m);
      w = new AutoWeb().register(m);
      x = new AnchorAura().register(m);
      y = new ElytraBot().register(m);
   }

   public static CombatLog t() {
      return m;
   }

   public static Blink y() {
      return r;
   }

   public static BowEnhance q() {
      return j;
   }

   public static AutoWeb D() {
      return w;
   }

   public static boolean notSuitableForAttack(ItemStack item) {
      return item.isEmpty() || !ItemStackUtils.g(item, DataComponentTypes.ATTRIBUTE_MODIFIERS) && !VItem.w().c(item) || VItem.w().e(item);
   }

   public static Attack n() {
      return g;
   }

   public static AutoCity B() {
      return u;
   }

   public static ModuleGroup i() {
      return b;
   }

   public static TotemLog v() {
      return o;
   }
}
