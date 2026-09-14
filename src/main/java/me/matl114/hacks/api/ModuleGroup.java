package me.matl114.hacks.api;

public class ModuleGroup extends ModuleManager {
   String metaData;

   public String getName() {
      return this.metaData;
   }

   public ModuleGroup(String name) {
      this.metaData = name;
   }
}
