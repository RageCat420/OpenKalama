package me.matl114.hacks.modules.render;

import java.util.Map;
import java.util.Set;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.combat.CombatManager;
import me.matl114.hacks.modules.combat.CombatSubHelperMX;
import me.matl114.hacks.utils.config.TracingOption;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.render.HackUtilHelperB;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ColorUtils;
import me.matl114.utils.ExplosionUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.render.RenderCollector;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ExplosiveESP extends BaseModule {
   public final NBTRef<WrapColor> anchorColor;
   private final RenderCollector<HackUtilHelperB> gP;
   public final IntRef distance;
   public final ModulePath aD = makePath(Configs.i, "combat-render.explosive-esp");
   public final NBTRef<TracingOption> options;
   public final NBTRef<WrapColor> crystalColor;
   public final KeyBindRef J;
   public final FlagRef ae = this.flagBuilder(this.aD.addEnable()).build();
   private final RenderCollector<Vec3d> hS;
   private final RenderCollector<Box> hR;

   private void submitDamageText(Vec3d explosionPos, float power, int color) {
      float var4 = power == 5.0F
         ? ExplosionUtils.respawnAnchorDamage(mc.player.getBoundingBox(), explosionPos, mc.world, ExplosionUtils.g)
         : ExplosionUtils.c(mc.player.getBoundingBox(), explosionPos, mc.world, ExplosionUtils.g);
      this.gP
         .submit(
            new HackUtilHelperB(Text.literal("%.1f/%.1f".formatted(var4, ExplosionUtils.o(mc.world, var4))), explosionPos, 0.66F), ColorUtils.j(color, 255)
         );
   }

   public void B(Event<MatrixStack> event) {
      if (!checkNull() && this.ae.get()) {
         MatrixStack var2 = (MatrixStack)event.e();
         RenderUtils.startDrawVirtual(var2);

         try {
            this.gP.a(var2);
            this.hR.a(var2);
            this.hS.a(var2);
         } finally {
            RenderUtils.stopDrawVirtual(var2);
         }
      }
   }

   public void onTick(Event<ClientPlayerEntity> event) {
      this.hR.clear();
      this.gP.clear();
      this.hS.clear();
      if (!checkNull() && this.ae.get()) {
         Map var2 = CombatManager.INSTANCE.Kt;
         Set<EndCrystalEntity> var3 = CombatManager.INSTANCE.Kx;
         double var4 = MathUtils.b(this.distance.get());
         TracingOption var6 = this.options.get();
         int var7 = this.anchorColor.get().color().getRgb();
         int var8 = this.crystalColor.get().color().getRgb();

         for (BlockPos var10 : var2.keySet()) {
            if (!(var10.getSquaredDistance(mc.player.getPos()) > var4)) {
               Box var11 = new Box(var10);
               if (var6.box()) {
                  this.hR.submit(var11, ColorUtils.j(var7, 255));
               }

               if (var6.line()) {
                  this.hS.submit(var11.getCenter(), ColorUtils.j(var7, 255));
               }

               this.submitDamageText(var10.toCenterPos(), 5.0F, var7);
            }
         }

         for (EndCrystalEntity var13 : var3) {
            if (!(var13.getPos().squaredDistanceTo(mc.player.getPos()) > var4)) {
               if (var6.box()) {
                  this.hR.submit(var13.getBoundingBox(), ColorUtils.j(var8, 160));
               }

               if (var6.line()) {
                  this.hS.submit(var13.getBoundingBox().getCenter(), ColorUtils.j(var8, 255));
               }

               this.submitDamageText(var13.getPos(), 6.0F, var8);
            }
         }
      }
   }

   private void ll(Event<CombatSubHelperMX> event) {
      ((CombatSubHelperMX)event.e()).g(this.ae.get());
   }

   public ExplosiveESP() {
      super("ExplosiveESP");
      this.J = this.moduleEntry(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable()).build();
      this.distance = this.intBuilder(this.aD.add("distance")).defaultValue(16).validator(Configs.e).build();
      this.options = this.builder(this.aD.add("options"), TracingOption.class).defaultValue(new TracingOption(true, false)).build();
      this.anchorColor = this.builder(this.aD.add("anchor-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.GOLD)).build();
      this.crystalColor = this.builder(this.aD.add("crystal-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.LIGHT_PURPLE)).build();
      this.hR = RenderCollectors.createBoxCollector(true, false, false);
      this.gP = RenderCollectors.f();
      this.hS = RenderCollectors.d();
      this.bindFlag(this.ae);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.V(), this::onTick);
      this.registerListener(RenderListener.q(), this::B);
      this.registerListener(CombatManager.getRequestEnableEvent(), this::ll);
   }
}
