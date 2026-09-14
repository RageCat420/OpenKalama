package me.matl114.managers.input;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.managers.Tasks;

public class SimpleHotKey implements IHotKey {
   public MultiKeyBind Lx;
   private SimpleHotKey$InputHandler Ly;
   public String Lv;
   private final IntList Lu = new IntArrayList(4);
   public final MultiKeyBind Lw;
   private final Set<IInputManager> Lz;

   @Override
   public String kG() {
      return this.Lv;
   }

   public void abK() {
      for (IInputManager var2 : this.Lz) {
         var2.b(this);
         var2.a(this);
      }
   }

   public void clearKeys() {
      this.Lu.clear();
   }

   private void abR(MultiKeyBind str) {
      this.clearKeys();

      for (int var5 : str.getKeyCodes()) {
         this.addKey(var5);
      }

      this.abK();
   }

   @Override
   public boolean handleKeyInput(IInputManager manager, int keyCode, boolean isStateChanged, boolean isClicked) {
      if (isStateChanged && isClicked && !this.isEmpty() && keyCode == this.getTriggeredKey()) {
         boolean var5 = true;
         IntListIterator var6 = this.kH().iterator();

         while (var6.hasNext()) {
            int var7 = (Integer)var6.next();
            var5 &= manager.isKeyPressed(var7);
         }

         if (var5) {
            Event var8 = new Event<>(this, true, false, manager);
            Listener.bv().catchEvent(var8);
            if (var8.d()) {
               return false;
            }

            if (this.Ly != null) {
               if (this.Ly.handle(this, manager)) {
                  MultiKeyBind var9 = this.abN();
                  if (var9 != null && var9.j()) {
                     Tasks.q(() -> {
                        if (this.abN() != var9) {
                           return true;
                        } else if (!var9.d()) {
                           this.Ly.handle(this, manager);
                           return true;
                        } else {
                           return false;
                        }
                     }, 1, 1);
                  }

                  return !this.abN().k();
               }

               return false;
            }

            return true;
         }
      }

      return false;
   }

   public MultiKeyBind abN() {
      return this.Lx;
   }

   public void abS(SimpleHotKey$InputHandler inputHandler) {
      this.Ly = inputHandler;
   }

   public MultiKeyBind abM() {
      return this.Lw;
   }

   @Deprecated
   public SimpleHotKey(String name, String defaultKeyCode) {
      this.Ly = SimpleHotKey$InputHandler.EMPTY;
      this.Lz = new LinkedHashSet<>();
      this.Lv = name;
      this.Lw = new MultiKeyBind(defaultKeyCode);
      this.abL(this.Lw);
   }

   public int getTriggeredKey() {
      return this.isEmpty() ? 0 : this.Lu.get(this.Lu.size() - 1);
   }

   public SimpleHotKey(String[] path, MultiKeyBind defaultKeyCode) {
      this.Ly = SimpleHotKey$InputHandler.EMPTY;
      this.Lz = new LinkedHashSet<>();
      this.Lv = String.join(".", path);
      this.Lw = defaultKeyCode;
      this.abL(this.Lw);
   }

   @Override
   public void addRegisteredManager(IInputManager manager) {
      this.Lz.add(manager);
   }

   public boolean isEmpty() {
      return this.Lu == null || this.Lu.isEmpty();
   }

   public void addKey(int keyCode) {
      this.Lu.add(keyCode);
   }

   public void abL(MultiKeyBind keyCode) {
      if (!Objects.equals(keyCode, this.Lx)) {
         this.Lx = keyCode;
         this.abR(this.Lx);
      }
   }

   @Override
   public IntList kH() {
      return this.Lu;
   }
}
