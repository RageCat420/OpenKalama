package me.matl114.managers.input;

import it.unimi.dsi.fastutil.ints.IntList;

public interface IHotKey {
   boolean handleKeyInput(IInputManager var1, int var2, boolean var3, boolean var4);

   void addRegisteredManager(IInputManager var1);

   String kG();

   IntList kH();
}
