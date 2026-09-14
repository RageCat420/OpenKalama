package me.matl114.gui.complex.itemEdit;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Optional;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.LabelElement;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.config.AttrKeyValue;
import net.minecraft.component.ComponentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class KalamaHelperHelperA {
   NbtElement b;
   AttrKeyValue<ComponentType<?>> a;

   public DrawableWidget factory() {
      return new KalamaHelperHelperCX(0, 0, 0, 0)
         .Q(new me.matl114.gui.complex.config.KalamaHelperHelperE<>(0, 0, 120, 20, 30, this.a))
         .Q(
            DisplayWidget.instance(120, 0, 30, 20)
               .setRenderHandler(
                  new LabelElement(
                     el -> this.b != null
                        ? Text.translatable("widget.gui.item-edit-screen.nbt-editor.component.component-not-empty").formatted(Formatting.GREEN)
                        : Text.translatable("widget.gui.item-edit-screen.nbt-editor.component.component-empty").formatted(Formatting.YELLOW),
                     -1,
                     0
                  )
               )
         )
         .Q(
            ExecutableWidget.instance(150, 0, 30, 20)
               .eV(
                  new ButtonElement(
                        TextProvider.c(Text.translatable("widget.gui.item-edit-screen.nbt-editor.component.open-component-edit")),
                        ButtonAction.a(this::openThisEditScreen)
                     )
                     .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.item-edit-screen.nbt-editor.component.open-component-edit.tooltips", "")))
                     .ah(e -> this.a.isValidate())
               )
         );
   }

   public <T> KalamaHelperHelperA(ComponentType<T> type, Optional<T> data) {
      this.a = AttrKeyValue.registry("widget.gui.item-edit-screen.nbt-editor.component.type", Registries.DATA_COMPONENT_TYPE, type);
      this.b = data.isPresent()
         ? (NbtElement)type.getCodec().encodeStart(RegistryOps.of(NbtOps.INSTANCE, ItemStackUtils.registry()), data.get()).getOrThrow()
         : null;
   }

   public void applyChanges(Map<ComponentType<?>, Optional<?>> map0) {
      try {
         ComponentType var2 = this.a.getOriginValue();
         if (var2 != null) {
            if (this.b == null) {
               map0.put(var2, Optional.empty());
            } else {
               DataResult var3 = var2.getCodec().decode(RegistryOps.of(NbtOps.INSTANCE, ItemStackUtils.registry()), this.b);
               Object var4 = ((Pair)var3.getOrThrow()).getFirst();
               map0.put(var2, Optional.ofNullable(var4));
            }
         }
      } catch (Throwable var5) {
      }
   }

   public KalamaHelperHelperA(String newId) {
      this.a = AttrKeyValue.openRegistry("widget.gui.item-edit-screen.nbt-editor.component.type", Registries.DATA_COMPONENT_TYPE, newId);
      this.b = null;
   }

   protected void openThisEditScreen() {
      ComponentType<?> var1 = this.a.getOriginValue();
      if (var1 != null) {
         ScreenAccess.of(new KalamaHelperHelperN<>(var1, this.b, nbt -> this.b = nbt == null ? null : nbt.copy())).openFromCurrent();
      }
   }
}
