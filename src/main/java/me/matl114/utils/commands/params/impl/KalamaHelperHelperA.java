package me.matl114.utils.commands.params.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import me.matl114.utils.commands.params.types.ExecutePos;
import me.matl114.utils.commands.params.types.KalamaHelperHelperE;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class KalamaHelperHelperA extends AbstractArgumentType<ExecutePos> implements ArgumentType<ExecutePos> {
   public Stream<String> f(CommandExecution sender, List<InputArgument<?>> args) {
      InputArgument var3 = (InputArgument)args.get(args.size() - 1);
      if (!(var3 instanceof PosArgumentResult var4)) {
         return Stream.empty();
      } else {
         String[] var5 = var4.getParsedArgument();
         int var6 = var5.length;
         if (var6 != 0 && !var5[0].isEmpty()) {
            String var7 = var5[0];
            int var8 = 3 - var6;
            if (var7.startsWith("^")) {
               boolean var17 = true;

               for (int var20 = 0; var20 < var6 - 1; var20++) {
                  var17 &= var5[var20].startsWith("^") && this.b(var5[var20]);
               }

               var17 &= var5[var6 - 1].startsWith("^") && this.b(var5[var6 - 1]) || var5[var6 - 1].isEmpty();
               if (var17) {
                  String var21 = " ^".repeat(var8);
                  var21 = (var5[var6 - 1].isEmpty() ? "^" : var5[var6 - 1]) + var21;
                  return Stream.of(var21);
               } else {
                  return Stream.empty();
               }
            } else {
               boolean var9 = true;

               for (int var10 = 0; var10 < var6; var10++) {
                  var9 &= !var5[var10].startsWith("^") && this.b(var5[var10]);
               }

               if (var9) {
                  Vector3d var19 = sender.sp();
                  String var11 = "%.1f".formatted(var19.x);
                  String var12 = "%.1f".formatted(var19.y);
                  String var13 = "%.1f".formatted(var19.z);
                  String[] var14 = new String[]{var11, var12, var13};
                  String[] var15 = new String[]{"~", "~", "~"};
                  if (var5[var6 - 1].isEmpty()) {
                     int var16 = var8 + 1;
                     return Stream.of(var14, var15).map(s -> String.join(" ", Arrays.copyOfRange(s, 3 - var16, 3)));
                  } else {
                     return Stream.of(var14, var15).map(s -> {
                        ArrayList var4x = new ArrayList();
                        var4x.add(var5[var6 - 1]);

                        for (int var5x = 3 - var8; var5x < 3; var5x++) {
                           var4x.add(s[var5x]);
                        }

                        return String.join(" ", var4x);
                     });
                  }
               } else {
                  return Stream.empty();
               }
            }
         } else {
            return Stream.of(
                  new me.matl114.utils.commands.params.types.KalamaHelperHelperG(sender.sp()),
                  new me.matl114.utils.commands.params.types.KalamaHelperHelperA(7, new Vector3d(0.0, 0.0, 0.0)),
                  new KalamaHelperHelperE(new Vector3d(0.0, 0.0, 0.0))
               )
               .map(rec$ -> ((ExecutePos)rec$).asString());
         }
      }
   }

   public KalamaHelperHelperA(String argsName) {
      super(argsName);
   }

   protected ExecutePos d(String xStr, String yStr, String zStr) {
      if (xStr.startsWith("^")) {
         if (yStr.startsWith("^") && zStr.startsWith("^")) {
            double var4 = this.e(xStr, "^");
            double var6 = this.e(yStr, "^");
            double var8 = this.e(zStr, "^");
            return !Double.isNaN(var4) && !Double.isNaN(var6) && !Double.isNaN(var8) ? new KalamaHelperHelperE(new Vector3d(var4, var6, var8)) : null;
         } else {
            return null;
         }
      } else {
         byte var10 = 0;
         double var11 = 0.0;
         double var13 = 0.0;
         double var15 = 0.0;
         if (xStr.startsWith("~")) {
            var10 |= 1;
            String var17 = xStr.substring(1);
            if (!var17.isEmpty()) {
               double var18 = Double.parseDouble(var17);
               if (Double.isNaN(var18)) {
                  return null;
               }

               var11 = var18;
            }
         } else {
            double var20 = Double.parseDouble(xStr);
            if (Double.isNaN(var20)) {
               return null;
            }

            var11 = var20;
         }

         if (yStr.startsWith("~")) {
            var10 |= 2;
            String var22 = yStr.substring(1);
            if (!var22.isEmpty()) {
               double var24 = Double.parseDouble(var22);
               if (Double.isNaN(var24)) {
                  return null;
               }

               var13 = var24;
            }
         } else {
            double var26 = Double.parseDouble(yStr);
            if (Double.isNaN(var26)) {
               return null;
            }

            var13 = var26;
         }

         if (zStr.startsWith("~")) {
            var10 |= 4;
            String var23 = zStr.substring(1);
            if (!var23.isEmpty()) {
               double var25 = Double.parseDouble(var23);
               if (Double.isNaN(var25)) {
                  return null;
               }

               var15 = var25;
            }
         } else {
            double var27 = Double.parseDouble(zStr);
            if (Double.isNaN(var27)) {
               return null;
            }

            var15 = var27;
         }

         return new me.matl114.utils.commands.params.types.KalamaHelperHelperA(var10, new Vector3d(var11, var13, var15));
      }
   }

   protected double e(String s, String prefix) {
      String var3 = s.substring(prefix.length());
      return var3.isEmpty() ? 0.0 : Double.parseDouble(var3);
   }

   @Override
   public Stream<String> getTab(CommandExecution sender, List<InputArgument<?>> args) {
      return Stream.concat(super.getTab(sender, args), this.f(sender, args));
   }

   @Nullable
   @Override
   public InputArgument<ExecutePos> consume(CommandExecution execution, List<InputArgument<?>> args, ArgumentReader reader) {
      if (!reader.hasNext()) {
         return new PosArgumentResult(Optional.ofNullable(this.m), this, reader, reader.b());
      } else {
         int var4 = reader.b();
         String var5 = reader.f();
         Object var6 = null;
         if (reader.hasNext()) {
            String var7 = reader.f();
            if (reader.hasNext()) {
               String var8 = reader.f();

               try {
                  var6 = this.d(var5, var7, var8);
                  return new PosArgumentResult(Optional.ofNullable((ExecutePos)var6), this, reader, var4);
               } catch (Throwable var10) {
               }
            }
         }

         reader.c(var4);
         return new PosArgumentResult(Optional.ofNullable(this.m), this, reader, var4);
      }
   }

   public boolean b(String str) {
      if (str.startsWith("^") || str.startsWith("~")) {
         str = str.substring(1);
      }

      if (str.startsWith("-")) {
         str = str.substring(1);
      }

      try {
         if (str.isEmpty()) {
            return true;
         } else {
            Double.parseDouble(str);
            return true;
         }
      } catch (NumberFormatException var3) {
         return false;
      }
   }
}
