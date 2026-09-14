package me.matl114.hooks;

import meteordevelopment.meteorclient.MeteorClient;

public class MeteorHooks implements IHooks {
   private static MeteorHooks INSTANCE;

   public static MeteorHooks getInstance() {
      if (INSTANCE == null) {
         try {
            INSTANCE = new MeteorHooks.Impl();
         } catch (Throwable var1) {
            INSTANCE = new MeteorHooks.Default();
         }
      }

      return INSTANCE;
   }

   public static class Default extends MeteorHooks {
      @Override
      public boolean isEnabled() {
         return false;
      }
   }

   public static class Impl extends MeteorHooks {
      public Impl() {
         Class<?> clazz = MeteorClient.class;
      }

      @Override
      public boolean isEnabled() {
         return true;
      }

      private void setupSettingsBootstrap() {
      }
   }
}
