package me.matl114.utils.config.kv;

import java.util.function.Consumer;
import java.util.function.Function;
import me.matl114.gui.McWidgetHelpers;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.utils.config.BaseAttrKeyValue;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.visitor.NbtOrderedStringFormatter;

public class NbtAttrKeyValue<W> extends BaseAttrKeyValue<NbtElement> {
   protected final Function<NbtElement, W> nbtParser;
   protected boolean enableNull = false;

   public NbtAttrKeyValue<W> setEnableNull(boolean val) {
      this.enableNull = val;
      return this;
   }

   public NbtAttrKeyValue(String key, NbtElement value, Function<NbtElement, W> function) {
      super(key, value == null ? null : value.copy(), AttrKeyValues.NBT_FACTORY);
      this.nbtParser = function;
      this.addValidator(s -> s != null ? this.extraParse(s) : this.enableNull);
   }

   private boolean extraParse(NbtElement element) {
      try {
         this.nbtParser.apply(element);
         return true;
      } catch (Throwable var3) {
         return false;
      }
   }

   public void applyFormatting(Consumer<String> callback) {
      if (this.validate) {
         try {
            this.valueChange(null, new NbtOrderedStringFormatter().apply(this.getOriginValue()));
            callback.accept(this.getValue());
         } catch (Throwable var3) {
         }
      }
   }

   public ContentDelegateWidget<EditBoxWidget> generateEditBox(int x, int y, int dx, int dy) {
      return McWidgetHelpers.b(
         x, y, dx, dy, (ed, val) -> this.valueChange(null, val), this.getValue(), McWidgetHelpers.getWrongRedTextBoxColorProvider(() -> this.validate)
      );
   }
}
