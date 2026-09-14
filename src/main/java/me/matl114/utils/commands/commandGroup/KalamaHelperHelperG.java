package me.matl114.utils.commands.commandGroup;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.api.ArgumentType;

public class KalamaHelperHelperG<R extends KalamaHelperHelperD, W extends SubCommand> extends KalamaHelperHelperE<W> {
   R treeSubCommand;

   public KalamaHelperHelperG<R, W> C(KalamaHelperHelperA args) {
      super.j(args);
      return this;
   }

   public KalamaHelperHelperG<R, W> z(Consumer<W> postTask) {
      super.g(postTask);
      return this;
   }

   protected KalamaHelperHelperG(R root, KalamaHelperHelperE<W> builder) {
      super(builder);
      this.treeSubCommand = (R)root;
   }

   public KalamaHelperHelperG<R, W> y(String permission) {
      super.e(permission);
      return this;
   }

   public KalamaHelperHelperG<R, W> helpers(String... helpers) {
      super.c(helpers);
      return this;
   }

   public KalamaHelperHelperG<R, W> A(ArgumentType<?> arg) {
      super.h(arg);
      return this;
   }

   public KalamaHelperHelperG<R, W> x(String helper) {
      super.d(helper);
      return this;
   }

   public KalamaHelperHelperG<R, W> B(UnaryOperator<me.matl114.utils.commands.params.KalamaHelperHelperD<?, ?>> arg) {
      super.i(arg);
      return this;
   }

   public W s() {
      SubCommand var1 = this.k();
      this.treeSubCommand.registerSub(var1);
      return (W)var1;
   }

   public KalamaHelperHelperG<R, W> u(String name) {
      super.a(name);
      return this;
   }

   public KalamaHelperHelperG<R, W> v(List<String> helpers) {
      super.b(helpers);
      return this;
   }

   public R r() {
      SubCommand var1 = this.k();
      this.treeSubCommand.registerSub(var1);
      return this.treeSubCommand;
   }

   public <S extends KalamaHelperHelperD> KalamaHelperHelperG<S, W> t() {
      return (KalamaHelperHelperG<S, W>)(Object)this;
   }
}
