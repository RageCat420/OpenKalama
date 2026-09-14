package me.matl114.managers.input;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;

public class MultiKeyBind {
   final boolean c;
   final String[] keys;
   final boolean d;
   final int[] keyCodes;

   public MultiKeyBind l(boolean toggleOnRelease) {
      return this.c == toggleOnRelease ? this : new MultiKeyBind(this.keys, this.keyCodes, toggleOnRelease, this.d);
   }

   public String[] getKeys() {
      return this.keys;
   }

   public int getLastKey() {
      return this.keyCodes.length == 0 ? -1 : this.keyCodes[this.keyCodes.length - 1];
   }

   public String c() {
      return String.join(",", this.keys);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof MultiKeyBind var2) ? false : Arrays.equals(this.keyCodes, var2.keyCodes) && var2.c == this.c && var2.d == this.d;
      }
   }

   private MultiKeyBind(String[] keys, int[] keyCodes, boolean toggleOnRelease, boolean allowVanilla) {
      this.keys = keys;
      this.keyCodes = keyCodes;
      this.c = toggleOnRelease;
      this.d = allowVanilla;
   }

   public String b() {
      return "hotkey:" + (this.c ? "T|" : "") + (this.d ? "V|" : "") + String.join(",", this.keys);
   }

   public MultiKeyBind(int... keyCodes) throws RuntimeException {
      Preconditions.checkNotNull(keyCodes);
      this.c = false;
      this.d = false;
      this.keyCodes = Arrays.copyOf(keyCodes, keyCodes.length);
      this.keys = new String[keyCodes.length];

      for (int var2 = 0; var2 < keyCodes.length; var2++) {
         this.keys[var2] = KeyCode.getNameForKey(this.keyCodes[var2]);
      }

      this.validateKeys();
   }

   public boolean j() {
      return this.c;
   }

   public boolean d() {
      if (this.keyCodes.length == 0) {
         return false;
      } else {
         for (int var1 = 0; var1 < this.keyCodes.length; var1++) {
            if (!SimpleInputManager.h().isKeyPressed(this.keyCodes[var1])) {
               return false;
            }
         }

         return true;
      }
   }

   public MultiKeyBind() {
      this(new int[0]);
   }

   public boolean e() {
      return this.keyCodes.length == 0 ? false : SimpleInputManager.h().isKeyPressed(this.keyCodes[this.keyCodes.length - 1]);
   }

   public boolean k() {
      return this.d;
   }

   public MultiKeyBind(List<String> keys, boolean toggleOnRelease) {
      this(keys, toggleOnRelease, false);
   }

   public boolean g() {
      return this.keyCodes.length == 0;
   }

   public MultiKeyBind m(boolean allowVanilla) {
      return this.d == allowVanilla ? this : new MultiKeyBind(this.keys, this.keyCodes, this.c, allowVanilla);
   }

   public int[] getKeyCodes() {
      return this.keyCodes;
   }

   public MultiKeyBind(String[] keys, boolean toggleOnRelease, boolean allowVanilla) {
      this.c = toggleOnRelease;
      this.d = allowVanilla;
      this.keys = Arrays.copyOf(keys, keys.length);
      this.keyCodes = new int[keys.length];
      this.validateKeys();
   }

   public MultiKeyBind(String rawStr) throws RuntimeException {
      if (rawStr.startsWith("hotkey:")) {
         rawStr = rawStr.substring("hotkey:".length());
      }

      if (rawStr.startsWith("T|")) {
         rawStr = rawStr.substring("T|".length());
         this.c = true;
      } else {
         this.c = false;
      }

      if (rawStr.startsWith("V|")) {
         rawStr = rawStr.substring("V|".length());
         this.d = true;
      } else {
         this.d = false;
      }

      this.keys = rawStr.isEmpty() ? new String[0] : rawStr.split(",");
      this.keyCodes = new int[this.keys.length];
      this.validateKeys();
   }

   private void validateKeys() {
      Preconditions.checkNotNull(this.keys);

      for (int var1 = 0; var1 < this.keys.length; var1++) {
         String var2 = this.keys[var1];
         var2 = var2.trim();
         Preconditions.checkArgument(!var2.isEmpty());
         int var3 = KeyCode.getKeyCodeFromName(var2);
         if (var3 == -1) {
            throw new RuntimeException("Invalid key code: " + var2);
         }

         this.keyCodes[var1] = var3;
      }
   }

   public MultiKeyBind(List<String> keys, boolean toggleOnRelease, boolean allowVanilla) {
      this(keys.toArray(String[]::new), toggleOnRelease, allowVanilla);
   }
}
