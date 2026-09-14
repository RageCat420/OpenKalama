package me.matl114.hacks.modules.inv;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public record InvSubHelperF(int index, List<InvSubHelperB> kitList) {
   public static Codec<InvSubHelperF> CODEC = RecordCodecBuilder.create(
      oinstance -> oinstance.group(
            Codec.INT.fieldOf("index").forGetter(InvSubHelperF::vC), Codec.list(InvSubHelperB.dt).fieldOf("kit-map").forGetter(InvSubHelperF::vD)
         )
         .apply(oinstance, InvSubHelperF::new)
   );

   public InvSubHelperF() {
      this(-1, List.of());
   }

   public int vC() {
      return this.index;
   }

   public InvSubHelperB vB() {
      return this.index >= 0 && this.index < this.kitList.size() ? this.kitList.get(this.index) : null;
   }

   public List<InvSubHelperB> vD() {
      return this.kitList;
   }
}
