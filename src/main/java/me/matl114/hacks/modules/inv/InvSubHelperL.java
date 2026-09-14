package me.matl114.hacks.modules.inv;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.matl114.utils.CodecUtils;

public record InvSubHelperL(InvSubHelperS type, int from, int to, boolean dump) {
   public static InvSubHelperL wI = new InvSubHelperL();
   static final MapCodec<InvSubHelperL> wJ = RecordCodecBuilder.mapCodec(
      oinstance -> oinstance.group(
            CodecUtils.enumCodec(InvSubHelperS.class).optionalFieldOf("type", InvSubHelperS.Iv).forGetter(InvSubHelperL::Ir),
            Codec.INT.optionalFieldOf("from", 9).forGetter(InvSubHelperL::Is),
            Codec.INT.optionalFieldOf("to", 36).forGetter(InvSubHelperL::It),
            Codec.BOOL.optionalFieldOf("dump", false).forGetter(InvSubHelperL::dump)
         )
         .apply(oinstance, InvSubHelperL::new)
   );

   public int Is() {
      return this.from;
   }

   public InvSubHelperL() {
      this(InvSubHelperS.Iv, 9, 36, false);
   }

   public boolean dump() {
      return this.dump;
   }

   public InvSubHelperS Ir() {
      return this.type;
   }

   public int It() {
      return this.to;
   }

   public InvSubHelperL Ip(int to) {
      return this.to == to ? this : new InvSubHelperL(this.type, this.from, to, this.dump);
   }

   public InvSubHelperL withDump(boolean dump) {
      return this.dump == dump ? this : new InvSubHelperL(this.type, this.from, this.to, dump);
   }

   public InvSubHelperL Io(int from) {
      return this.from == from ? this : new InvSubHelperL(this.type, from, this.to, this.dump);
   }

   public InvSubHelperL withType(InvSubHelperS type) {
      return this.type == type ? this : new InvSubHelperL(type, this.from, this.to, this.dump);
   }
}
