package me.matl114.utils.commands.params.impl;

import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.InputArgument;

public abstract class AbstractArgumentResult<T> implements InputArgument<T> {
   public int e;
   public boolean f;
   public int d;
   public final T g;
   public final ArgumentType<T> b;
   public boolean h = true;
   public final ArgumentReader c;

   @Override
   public final String h() {
      if (this.d < this.e) {
         return this.c.p(this.e - 1);
      } else if (this.d == this.e) {
         return this.c.q() > this.e ? this.c.p(this.e) : null;
      } else {
         return null;
      }
   }

   @Override
   public boolean k() {
      return this.h;
   }

   @Override
   public int c() {
      return this.e;
   }

   @Override
   public ArgumentType<T> i() {
      return this.b;
   }

   @Override
   public int b() {
      return this.d;
   }

   @Override
   public ArgumentReader f() {
      return new ArgumentReader(this.c).c(this.d);
   }

   @Override
   public ArgumentReader d() {
      return this.c;
   }

   @Override
   public T g() {
      return this.g;
   }

   public AbstractArgumentResult(T result, ArgumentType<T> type, ArgumentReader reader, int startIndex) {
      this.b = type;
      this.c = reader;
      this.d = startIndex;
      this.e = reader.b();
      this.f = this.d == this.e;
      this.g = (T)result;
   }

   @Override
   public ArgumentReader e() {
      return new ArgumentReader(this.c).c(this.d);
   }

   public String[] getParsedArgument() {
      return this.c.getArgsInRange(this.d, this.e);
   }
}
