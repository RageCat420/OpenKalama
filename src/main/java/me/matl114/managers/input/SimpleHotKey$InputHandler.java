package me.matl114.managers.input;

import com.google.common.util.concurrent.Runnables;
import me.matl114.hacks.utils.HotKeyUtils;

public interface SimpleHotKey$InputHandler {
   SimpleHotKey$InputHandler EMPTY = HotKeyUtils.b(Runnables.doNothing());

   boolean handle(IHotKey var1, IInputManager var2);
}
