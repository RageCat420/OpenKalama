package me.matl114.hacks.api;

public enum ModulePreset {
   fj,
   fi,
   fe,
   ff,
   fd,
   fh,
   fg;
   // $VF: synthetic field

   public boolean hasAC() {
      return this != fe && this != fj;
   }
}
