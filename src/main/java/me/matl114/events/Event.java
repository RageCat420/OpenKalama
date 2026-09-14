package me.matl114.events;

import com.google.common.base.Preconditions;

public class Event<T> {
   public boolean d = false;
   public final boolean e;
   public T b;
   public static final Event<Void> VOID = new Event<>(null, false, false);
   public Object[] c;
   public final boolean f;

   public Event(T context, boolean canCancel) {
      this((T)context, canCancel, false);
   }

   public boolean h() {
      return this.f;
   }

   public <W> W getArgs(int idx) {
      return (W)(Object)this.c[idx];
   }

   public Event<T> j(boolean cancel) {
      this.d = cancel;
      return this;
   }

   public Event<T> context(T val) {
      Preconditions.checkArgument(this.f, "Can not modify");
      this.b = (T)val;
      return this;
   }

   public Event<T> extraArgs(Object[] extraArgs) {
      this.c = extraArgs;
      return this;
   }

   public Event(T context, boolean canCancel, boolean canModifyContext, Object... extraArgs) {
      this.b = (T)context;
      this.c = extraArgs;
      this.e = canCancel;
      this.f = canModifyContext;
   }

   public boolean g() {
      return this.e;
   }

   public boolean d() {
      return this.d;
   }

   public T e() {
      return this.b;
   }

   public Object[] f() {
      return this.c;
   }

   public Event<T> cancel() {
      Preconditions.checkArgument(this.e, "Can not cancel!");
      this.d = true;
      return this;
   }
}
