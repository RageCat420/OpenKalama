package me.matl114.hacks.modules.inv;

import java.util.function.Consumer;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.events.annotations.Modifiable;
import me.matl114.gui.complex.itemEdit.ItemEditScreen;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.managers.Configs;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.Debug;
import me.matl114.utils.ScreenUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ItemEditor extends BaseModule {
   public final KeyBindRef el;
   public final ModulePath RB = makePath(Configs.l, "item-editor");

   public ItemEditor() {
      super("ItemEditor");
      this.el = this.hotkey(Configs.l, this.RB.add("open-editor").toPath())
         .defaultValue(new MultiKeyBind(341, 73))
         .registerHotkey(HotKeyUtils.e(this::openEditor))
         .build();
   }

   @Modifiable
   public void openEditScreen(ItemStack item, Consumer<ItemStack> callback) {
      if (item.isEmpty()) {
         Debug.b(Text.literal("你不能打开空物品的编辑器!"));
      } else {
         ScreenAccess.of(new ItemEditScreen(Text.empty(), item, callback)).openFromCurrent();
      }
   }

   public boolean openEditor() {
      if (mc.player != null) {
         this.openEditorForPlayer(mc.player);
         return true;
      } else {
         return false;
      }
   }

   public void openEditorForPlayer(ClientPlayerEntity entity) {
      ItemStack var2 = ScreenUtils.getSelectingOrHandItem();
      if (var2 != null) {
         this.openEditScreen(var2, null);
      } else {
         Debug.b(Text.literal("你必须选择一个物品以打开").formatted(Formatting.RED));
      }
   }
}
