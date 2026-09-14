package me.matl114.hacks.modules.combat;


import net.minecraft.client.MinecraftClient;
import me.matl114.events.Event;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.utils.move.FlightVelocity;
import me.matl114.managers.Tasks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class CombatSubHelperU extends CombatSubHelperSX implements CombatSubHelperG {
   private CombatSubHelperSX B;
   private boolean C;
   private final CombatSubHelperIX A;
   private int D;
   private final CombatSubHelperFX y = new CombatSubHelperFX();
   private int E;
   private final CombatSubHelperDX z = new CombatSubHelperDX();

   @Override
   public void i() {
      this.B = null;
      this.C = true;
      this.D = -1;
      this.E = -1;
      this.X();
   }

   public CombatSubHelperU() {
      this.A = new CombatSubHelperIX();
      this.C = true;
      this.D = -1;
      this.E = -1;
   }

   private void ab(int type) {
      if (this.C && this.D >= 0 && this.t.uI != null && this.E == this.t.uI.getId() && (type == 1 || type == 0)) {
         this.D = -1;
         this.E = -1;
      }
   }

   @Override
   public void L() {
      if (this.B != null) {
         this.B.L();
         this.u = this.B.u;
      }
   }

   @Override
   public Entity searchTarget() {
      return this.B == null ? super.searchTarget() : this.B.searchTarget();
   }

   private void aa(Entity entity) {
      if (this.C && this.D < 0 && entity == this.t.uI) {
         this.D = Tasks.b();
         this.E = entity.getId();
      }
   }

   @Override
   public void j() {
      if (this.B != null) {
         this.B.j();
      }

      this.B = null;
      this.D = -1;
      this.E = -1;
      this.u = Vec3d.ZERO;
   }

   private void Z(CombatSubHelperSX nextBehaviour, boolean useMace) {
      boolean var3 = this.B != null && this.C != useMace;
      if (this.B != null) {
         this.B.j();
      }

      this.B = nextBehaviour;
      this.B.R(this.t);
      this.C = useMace;
      this.D = -1;
      this.E = -1;
      this.B.i();
      this.u = this.B.u;
      if (var3 && this.t.mxsModeSwitchNotification.get()) {
         this.t
            .logI18N(
               "message.module.elytra-bot.mxs-mode-switch", new Object[]{(useMace ? ElytraBot$Mode.MACE_ARUA : ElytraBot$Mode.SPEAR_ARUA).resultAsString()}
            );
      }
   }

   public boolean V() {
      return this.C;
   }

   @Override
   public synchronized void f(Entity entity) {
      this.aa(entity);
      if (this.B != null) {
         this.B.f(entity);
         this.u = this.B.u;
      }
   }

   @Override
   public synchronized void onHit(int type) {
      if (this.B instanceof CombatSubHelperG var3) {
         var3.onHit(type);
      }

      this.ab(type);
   }

   private CombatSubHelperOX W() {
      return (CombatSubHelperOX)(this.t.DB() ? this.z : this.y);
   }

   private void Y() {
      if (this.C || this.B != this.A) {
         this.Z(this.A, false);
      }
   }

   @Override
   public void O(Event<Void> eventInput) {
      if (this.B != null) {
         this.B.O(eventInput);
      }
   }

   @Override
   public void onElytra(Event<KalamaHelperHelperI<FlightVelocity>> event) {
      super.onElytra(event);
   }

   private void X() {
      CombatSubHelperOX var1 = this.W();
      if (!this.C || this.B != var1) {
         this.Z(var1, true);
      }
   }

   @Override
   public synchronized void h() {
      super.h();
      if (!this.C && this.t.uI != null) {
         double var1 = this.t.uI.getY() - MinecraftClient.getInstance().player.getY();
         if (var1 > 0.0 && var1 > this.t.mxsSpearSwitchMaceHeight.get()) {
            this.X();
         }
      } else if (this.C) {
         this.X();
      }

      if (this.B == null) {
         this.X();
      }

      if (this.t.uI == null || this.t.uI.getId() != this.E) {
         this.D = -1;
         this.E = -1;
      }

      this.B.h();
      this.u = this.B.u;
      if (this.C && this.D >= 0 && Tasks.b() >= this.D + this.t.mxsMaceHitWaitTicks.get()) {
         this.Y();
      }
   }
}
