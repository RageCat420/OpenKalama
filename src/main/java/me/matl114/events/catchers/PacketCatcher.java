package me.matl114.events.catchers;

import java.util.Iterator;
import me.matl114.events.channels.KalamaHelperHelperA;
import me.matl114.events.channels.KalamaHelperHelperE;

public class PacketCatcher<W> extends KalamaHelperHelperE<W> {
   public boolean catchEvent(W express) {
      Iterator var2 = this.d.iterator();

      while (var2.hasNext()) {
         KalamaHelperHelperA var3 = (KalamaHelperHelperA)var2.next();
         if (!var3.test(express)) {
            var2.remove();
         }
      }

      return true;
   }
   @Override
   public boolean a(W var1) {
      return this.catchEvent(var1);
   }

}
