package me.matl114.hacks.modules.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public record ChatSubHelperK(int selected, List<ChatSubHelperH> entries) {
   public static final Codec<ChatSubHelperK> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.INT.fieldOf("selected").forGetter(ChatSubHelperK::selected), Codec.list(ChatSubHelperH.b).fieldOf("entries").forGetter(ChatSubHelperK::entries)
         )
         .apply(instance, ChatSubHelperK::new)
   );

   public int selected() {
      return this.selected;
   }


   public List<ChatSubHelperH> entries() {
      return this.entries;
   }

   public ChatSubHelperH Ri() {
      return this.selected >= 0 && this.selected < this.entries.size() ? this.entries.get(this.selected) : null;
   }
}
