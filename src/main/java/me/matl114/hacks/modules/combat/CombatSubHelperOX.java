package me.matl114.hacks.modules.combat;


import net.minecraft.client.MinecraftClient;
import me.matl114.events.Event;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.hacks.utils.move.FlightVelocity;
import me.matl114.managers.Tasks;
import me.matl114.utils.Debug;
import me.matl114.utils.algorithms.StateMachine;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;

public abstract class CombatSubHelperOX extends CombatSubHelperSX implements CombatSubHelperG {
   static final int j = 1;
   boolean attackFlag;
   public StateMachine stateMachine;
   int r;
   double s;
   static final int b = 0;
   int q;
   static final int c = 2;
   int o;
   int l = -1;
   double m = Double.MIN_VALUE;
   double n = Double.MIN_VALUE;
   static final int k = 3;

   protected abstract void e(Vec3d var1);

   @Override
   public void j() {
   }

   public boolean F() {
      return Attack.shouldUseAntiShield(this.t.uI);
   }

   public boolean G() {
      return this.t.maceCombatConsiderUse.get() && CombatTasks.o().aao();
   }

   @Override
   public synchronized void L() {
      this.stateMachine.c(0);
   }

   public abstract int onStatePullUp(StateMachine var1);

   public boolean J() {
      boolean var1 = this.r < Tasks.b() || Attack.INSTANCE.willUseMaceAttack(false);
      return var1 || PlayerStateManager.INSTANCE.jh > 1.5;
   }

   public int onStateNone(StateMachine machine) {
      if (this.t.uI != null) {
         return PlayerStateManager.INSTANCE.jh > 4.0 && MinecraftClient.getInstance().player.getPos().getY() > this.t.uI.getPos().getY() + 4.0 ? 2 : 1;
      } else {
         this.u = Vec3d.ZERO;
         machine.e();
         return 0;
      }
   }

   @Override
   public void i() {
      this.stateMachine.c(0);
   }

   @Override
   public void f(Entity var1) { }

   public void D(boolean on) {
      this.l = 0;
   }

   public void onStartPullUp(boolean on) {
      if (on) {
         this.n = MinecraftClient.getInstance().player.getY();
         this.o = Tasks.b();
      } else {
         this.n = Double.MIN_VALUE;
         this.o = 0;
      }
   }

   public abstract int onStateFollow(StateMachine var1);

   public int onCondition(StateMachine machine, int state) {
      if (this.t.uI == null) {
         machine.e();
         this.u = Vec3d.ZERO;
         return 0;
      } else {
         return state;
      }
   }

   public boolean H() {
      boolean var1 = this.F();
      if (var1) {
         return true;
      } else if (!this.t.maceCombatUseExtraAttack.get()) {
         return false;
      } else if (MinecraftClient.getInstance().player.getAttackCooldownProgress(0.5F) > 0.95F) {
         return this.t.maceCombatConsiderUse.get() && CombatTasks.o().aaq() ? false : !Attack.INSTANCE.willUseMaceAttack(false);
      } else {
         return false;
      }
   }

   @Override
   public synchronized void onHit(int type) {
      if (this.q > Tasks.b() - 5 && this.stateMachine.getState() != 1) {
         this.r = Tasks.b();
         this.stateMachine.c(1);
      }
   }

   public synchronized void onUpdate() {
      super.h();
      if (!MinecraftClient.getInstance().player.isFallFlying() && !MinecraftClient.getInstance().player.getAbilities().flying) {
         this.stateMachine.c(0);
         this.u = Vec3d.ZERO;
      } else {
         this.m = Math.max(this.m, MinecraftClient.getInstance().player.getY());
         this.stateMachine.f();
         if (this.attackFlag) {
            if (this.t.uI != null) {
               boolean var1 = this.H();
               boolean var2 = this.J();
               if (var1) {
                  CombatSubHelperPX var3 = CombatTasks.n().createAttackSettings().yX(false);
                  if (this.F()) {
                     var3 = var3.za(true);
                  }

                  CombatTasks.n().Yi(this.t.uI, var3);
               }

               if (var2) {
                  CombatSubHelperPX var4 = CombatTasks.n().createAttackSettings();
                  CombatTasks.n().Yi(this.t.uI, var4.yX(true).yY(false).za(false));
                  this.q = Tasks.b();
                  Debug.g("[ElytraBot] Do mace attack here", Tasks.b());
               }
            }

            this.m = MinecraftClient.getInstance().player.getY();
            this.attackFlag = false;
         }
      }

      this.s = PlayerStateManager.INSTANCE.jh;
   }

   public CombatSubHelperOX() {
      this.o = 0;
      this.attackFlag = false;
      this.stateMachine = new StateMachine(0, this::onCondition, this::onStateNone, this::onStatePullUp, this::onStateFollow, this::onStateWaitAttack);
      this.stateMachine.registerListener(3, this::D);
      this.stateMachine.registerListener(1, this::onStartPullUp);
   }

   public boolean I() {
      boolean var1 = this.r < Tasks.b() || Attack.INSTANCE.willUseMaceAttack(false);
      return var1 && PlayerStateManager.INSTANCE.jh > this.t.maceMinFallDistance.get().orElse(1.5);
   }

   @Override
   public void onElytra(Event<KalamaHelperHelperI<FlightVelocity>> event) {
      if (this.u != null && this.u.lengthSquared() > 1.0E-9) {
         Vec3d var2 = this.u;
         OptionalPrimitive var3 = this.t.maceCustomPullUpAngle.get();
         if (var3.isPresent() && this.stateMachine.getState() == 1) {
            double var4 = var2.horizontalLength();
            if (var4 > 1.0E-6) {
               var2 = var2.withAxis(Axis.Y, Math.tan(Math.toRadians((Double)var3.getValue())) * var4);
            }
         }

         double var8 = var2.length();
         double var6 = Math.min(var8, ((FlightVelocity)((KalamaHelperHelperI)event.b).b()).f() * this.t.uT);
         ((FlightVelocity)((KalamaHelperHelperI)event.b).b()).velocity(var2.normalize().multiply(var6));
      }
   }

   protected abstract void d(Vec3d var1);

   public void M() {
      this.attackFlag = true;
   }

   public int onStateWaitAttack(StateMachine machine) {
      if (this.t.uQ != CombatSubHelperV.Rp && this.t.uQ != CombatSubHelperV.Ro) {
         machine.e();
         return 1;
      } else if (++this.l > 1) {
         return this.t.uS ? 0 : 2;
      } else {
         machine.e();
         Vec3d var2 = this.t.macePullUpUsePredictor.get()
            ? PositionPredict.INSTANCE.attackPredictArgument.get().predict(this.t.uI).withAxis(Axis.Y, this.t.uI.getY())
            : this.t.uI.getPos();
         var2 = var2.withAxis(Axis.Y, this.t.uI.getY());
         this.e(var2);
         return 3;
      }
   }
}
