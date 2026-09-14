package me.matl114.hacks;

public @interface Module {
   String[] extra() default {};

   String value();
}
