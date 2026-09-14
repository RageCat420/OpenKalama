package me.matl114.managers.input;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.CharTypedAction;
import me.matl114.events.impl.KeyboardAction;
import me.matl114.events.impl.MouseClickAction;
import me.matl114.events.impl.MouseScrollAction;
import me.matl114.managers.InputState;
import net.minecraft.client.MinecraftClient;

public class SimpleInputManager implements IInputManager {
   protected Map<Integer, InputState> e;
   protected static final SimpleInputManager instance = new SimpleInputManager();
   protected MinecraftClient d;
   protected final Map<String, IHotKey> b = new HashMap<>();
   protected final Multimap<Integer, IHotKey> c = LinkedHashMultimap.create();

   public boolean onMouseScroll(double horizontal, double vertical) {
      Event var5 = new Event<>(new MouseScrollAction(this.d.mouse, horizontal, vertical), true, false);
      Listener.br().catchEvent(var5);
      return var5.d();
   }

   @Override
   public IHotKey getHotkey(String id) {
      return this.b.get(id);
   }

   public boolean onKeyInput(int keyCode, int scanCode, int modifiers, int action) {
      boolean var5 = this.j(keyCode, scanCode, modifiers, action);
      Event var6 = new Event<>(new KeyboardAction(this.d.keyboard, keyCode, scanCode, action, modifiers), true, false);
      Listener.bp().catchEvent(var6);
      boolean var7 = var6.d();
      boolean var8 = action != 0;
      return this.checkKeyBindsForChanges(keyCode, var5, var8) || var7;
   }

   public void unregisterHotKeys(IHotKey key) {
      this.b.remove(key.kG());
      this.c.entries().removeIf(e -> e.getValue() == key);
   }

   protected SimpleInputManager() {
      this.e = new ConcurrentHashMap<>();
      this.d = MinecraftClient.getInstance();
   }

   public boolean onMouseClick(int mouseX, int mouseY, int eventButton, int action, int mode) {
      boolean var6 = false;
      int var7 = KeyCode.getKeyCodeFromMouseAction(eventButton);
      if (eventButton != -1) {
         boolean var8 = action == 1;
         boolean var9 = this.j(var7, 0, 0, action);
         Event var10 = new Event<>(new MouseClickAction(this.d.mouse, eventButton, action, mode), true, false);
         Listener.bq().catchEvent(var10);
         var6 = this.checkKeyBindsForChanges(var7, var9, var8) || var10.d();
      }

      return var6;
   }

   public void registerHotKeys(IHotKey key) {
      this.b.put(key.kG(), key);
      IntListIterator var2 = key.kH().iterator();

      while (var2.hasNext()) {
         Integer var3 = (Integer)var2.next();
         this.c.put(var3, key);
      }

      key.addRegisteredManager(this);
   }

   public boolean checkKeyBindsForChanges(int eventKey, boolean stateChange, boolean isClicked) {
      boolean var4 = false;
      Collection<IHotKey> var5 = this.c.get(eventKey);
      if (!var5.isEmpty()) {
         for (IHotKey var7 : var5) {
            boolean var8 = var7.handleKeyInput(this, eventKey, stateChange, isClicked);
            var4 |= var8;
         }
      }

      return var4;
   }

   public boolean j(int keyCode, int scanCode, int modifiers, int action) {
      if (keyCode != -1) {
         boolean var5 = action != 0;
         InputState var6 = this.e(keyCode);
         if (!var5) {
            var6.setPressed(false);
            return true;
         }

         if (!var6.isPressed() && !this.i(keyCode)) {
            var6.setPressed(true);
            return true;
         }
      }

      return false;
   }

   @Override
   public InputState d(int t) {
      return this.e.get(t);
   }

   @Override
   public boolean isKeyPressed(int key) {
      InputState var2 = this.e.get(key);
      return var2 != null && var2.isPressed();
   }

   public static SimpleInputManager h() {
      return instance;
   }

   public boolean onCharTyped(int codePoint, int modifiers) {
      if (Character.charCount(codePoint) == 1) {
         Event var3 = new Event<>(new CharTypedAction((char)codePoint, codePoint, modifiers), true, false);
         Listener.bu().catchEvent(var3);
         if (var3.d()) {
            return true;
         }
      } else {
         for (char var6 : Character.toChars(codePoint)) {
            Event var7 = new Event<>(new CharTypedAction(var6, codePoint, modifiers), true, false);
            Listener.bu().catchEvent(var7);
            if (var7.d()) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public MinecraftClient getClient() {
      return this.d;
   }

   @Override
   public InputState e(int t) {
      return this.e.computeIfAbsent(t, s -> new InputState());
   }

   public boolean i(int keyCode) {
      return false;
   }



   @Override
   public void a(Object arg0) { }

}
