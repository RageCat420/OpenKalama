package me.matl114.utils.commands.params;

import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import org.jetbrains.annotations.Nullable;

public class ArgumentInputStream {
   int i = 0;
   CommandExecution a;
   List<InputArgument<?>> d;
   ArgumentReader b;
   List<ArgumentType<?>> c;

   private void solveTo(int i) {
      for (int var2 = this.d.size(); var2 <= i; var2++) {
         ArgumentType var3 = this.c.get(var2);
         this.d.add(var3.consume(this.a, this.d, this.b));
      }
   }

   @Nonnull
   public Stream<String> getTabComplete(CommandExecution sender) {
      if (this.d.isEmpty()) {
         return Stream.empty();
      } else {
         int var2 = this.b.q() - 1;
         int var3 = this.d.size();
         int var4 = var3 - 1;
         InputArgument var5 = this.d.get(var4);
         int var6 = var5.b();
         if (var6 != var2) {
            return Stream.empty();
         } else {
            ArrayList<Stream<String>> var7 = new ArrayList<>();

            for (int var8 = var4; var8 >= 0; var8--) {
               InputArgument var9 = this.d.get(var8);
               if (var9.b() < var6) {
                  break;
               }

               Stream<String> var10 = this.c.get(var8).getTab(sender, var8 == var4 ? this.d : this.d.subList(0, var8 + 1));
               if (var10 != null) {
                  var7.add(var10);
               }
            }

            return Streams.concat(var7.toArray(Stream[]::new));
         }
      }
   }

   public <T> InputArgument<T> d() {
      if (this.a()) {
         this.solveTo(this.i);
         return (InputArgument<T>)(Object)this.d.get(this.i);
      } else {
         throw new RuntimeException("Illegal to access undeclared argument");
      }
   }

   @Nonnull
   public <T> T n() {
      return this.<T>e().y();
   }

   public int nextInt() {
      return this.e().n();
   }

   public boolean a() {
      return this.i < this.c.size();
   }

   public int nextClampedInt(int from, int toExclude) {
      return this.e().clampInt(from, toExclude);
   }

   public double nextClampedDouble(double from, double toExclu) {
      return this.e().clampDouble(from, toExclu);
   }

   @Nonnull
   public <T> InputArgument<T> e() {
      if (this.a()) {
         int var1 = this.i;
         this.solveTo(var1);
         this.b();
         return (InputArgument<T>)(Object)this.d.get(var1);
      } else {
         throw new RuntimeException("Illegal to access undeclared argument");
      }
   }

   @Nullable
   public <T> T f() {
      return this.<T>e().g();
   }

   public ArgumentType<?> b() {
      return this.c.get(this.i++);
   }

   public String q(Collection<String> selections) {
      return this.e().B(selections);
   }

   public <T extends Enum<T>> T p(Class<T> type) {
      return this.e().A(type);
   }

   public <T> T s(Supplier<T> def) {
      Object var2 = this.e().g();
      return (T)(var2 == null ? def.get() : var2);
   }

   @Nonnull
   public String o() {
      return this.e().z();
   }

   public boolean h() {
      return this.e().o();
   }

   public double nextDouble() {
      return this.e().getDouble();
   }

   public ArgumentInputStream(CommandExecution execution, ArgumentReader reader, List<ArgumentType<?>> argsSet, List<InputArgument<?>> argsMap) {
      this.a = execution;
      this.b = new ArgumentReader(reader);
      this.c = argsSet;
      this.d = argsMap;
   }

   public float nextFloat() {
      return this.e().getFloat();
   }

   public float nextClampedFloat(float from, float to) {
      return this.e().clampFloat(from, to);
   }
}
