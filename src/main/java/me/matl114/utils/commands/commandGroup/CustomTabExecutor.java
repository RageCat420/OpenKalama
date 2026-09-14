package me.matl114.utils.commands.commandGroup;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.utils.a.KalamaHelperHelperA;
import me.matl114.utils.commands.interruption.TypeError;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;

public interface CustomTabExecutor {
   Stream<String> getHelp(String var1);

   static void checkRange(int val, int from, int to) {
      KalamaHelperHelperA.n(null, val, from, to);
   }

   String c();

   Stream<String> f(CommandExecution var1, ArgumentReader var2);

   @Nullable
   String a();

   default boolean be(CommandExecution sender) {
      String var2 = this.a();
      return var2 == null || sender.hasPermission(var2);
   }

   static void enumError(String input) {
      throw new TypeError((String)null, me.matl114.utils.commands.interruption.KalamaHelperHelperH.Qz, input);
   }

   static int gint(String val) {
      return KalamaHelperHelperA.j(val, (String)null);
   }

   @Nonnull
   ArgumentInputStream b(CommandExecution var1, ArgumentReader var2);

   static void bm(double val, double from, double to) {
      KalamaHelperHelperA.p(null, val, from, to);
   }

   static boolean bi(String val) {
      return KalamaHelperHelperA.m(val, (String)null);
   }

   static void bl(float val, float from, float to) {
      KalamaHelperHelperA.p(null, val, from, to);
   }

   boolean onCustomCommand(CommandExecution var1, ArgumentReader var2);

   static float gfloat(String val) {
      return KalamaHelperHelperA.k(val, (String)null);
   }

   List<String> e(CommandExecution var1, ArgumentReader var2);

   static double gdouble(String val) {
      return KalamaHelperHelperA.l(val, (String)null);
   }

}
