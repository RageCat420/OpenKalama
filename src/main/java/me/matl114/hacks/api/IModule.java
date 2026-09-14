package me.matl114.hacks.api;

import java.util.stream.Stream;

public interface IModule {
   default Stream<ModuleEntry> jq() {
      return this.getModuleEntries().filter(ModuleEntry::getActiveState);
   }

   Stream<ModuleEntry> getModuleEntries();
}
