package me.matl114.hacks.modules.combat;

public class CombatSubHelperMX {
   boolean b;
   boolean a;
   boolean c;

   public boolean a() {
      return !this.a && !this.b && !this.c;
   }

   public CombatSubHelperMX f(boolean enableBlockSearch) {
      this.a = enableBlockSearch;
      return this;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + (this.c() ? 79 : 97);
      var2 = var2 * 59 + (this.d() ? 79 : 97);
      return var2 * 59 + (this.e() ? 79 : 97);
   }

   public CombatSubHelperMX h(boolean enableHoleSearch) {
      this.c = enableHoleSearch;
      return this;
   }

   protected boolean b(Object other) {
      return other instanceof CombatSubHelperMX;
   }

   public CombatSubHelperMX g(boolean enableExplosiveSearch) {
      this.b = enableExplosiveSearch;
      return this;
   }

   public boolean e() {
      return this.c;
   }

   @Override
   public String toString() {
      return "CombatManager.Service(enableBlockSearch=" + this.c() + ", enableExplosiveSearch=" + this.d() + ", enableHoleSearch=" + this.e() + ")";
   }

   public boolean c() {
      return this.a;
   }

   public boolean d() {
      return this.b;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof CombatSubHelperMX var2)) {
         return false;
      } else if (!var2.b(this)) {
         return false;
      } else if (this.c() != var2.c()) {
         return false;
      } else {
         return this.d() != var2.d() ? false : this.e() == var2.e();
      }
   }
}
