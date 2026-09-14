package me.matl114.gui.complex.itemEdit;

import java.util.function.Consumer;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.presets.choices.ConfirmingBigScreen;
import me.matl114.hacks.ChatTasks;
import me.matl114.hacks.InvTasks;
import me.matl114.hacks.SlimefunTasks;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ItemEditScreen extends ConfirmingBigScreen {
   protected static final Identifier cf = new Identifier("kalama", "gui/list_tag");
   protected static final MinecraftClient mc = MinecraftClient.getInstance();
   ExecutableWidget bY;
   protected static final Identifier ce = new Identifier("kalama", "gui/snbt_editor");
   ExecutableWidget bZ;
   ExecutableWidget bX;
   protected KalamaHelperHelperJ cg;
   ExecutableWidget bW;
   protected ItemStack v;
   protected static final Identifier cc = new Identifier("kalama", "gui/copy_command");
   ContentDelegateWidget<EditBoxWidget> ch;
   protected KalamaHelperHelperI bU = null;
   protected static int CONTENT_START_X = 20;
   protected static final Identifier cd = new Identifier("kalama", "gui/editor");
   protected static final Identifier cb = new Identifier("kalama", "gui/save");
   protected Consumer<ItemStack> c;
   ExecutableWidget ca;
   ContentDelegateWidget<KalamaHelperHelperJ> ci;

   @Override
   protected void c() {
      this.setState(null);
      this.close();
      if (this.c == null) {
         this.applyChangeToInventory();
      } else {
         this.c.accept(this.v);
      }
   }

   public void di() {
      KalamaHelperHelperJ var1 = this.ci.ef();
      if (var1 != null) {
         var1.ag();
      }
   }

   protected boolean canConfirm() {
      KalamaHelperHelperJ var1 = this.ci.ef();
      return var1 == null || var1.canConfirm();
   }

   protected void setState(KalamaHelperHelperI state) {
      if (this.cg != null) {
         this.cg.ag();
      }

      if (state == null) {
         this.setTitleLabel(Text.translatable("widget.gui.item-edit-screen.state.error-title").formatted(Formatting.RED));
         this.bU = null;
         this.cg = null;
      } else {
         KalamaHelperHelperI var2 = this.bU;
         this.bU = state;
         if (var2 != this.bU) {
            this.cg = this.generateCurrentStateScreen();
         }
      }

      this.ah();
   }

   protected KalamaHelperHelperJ generateCurrentStateScreen() {
      try {
         return (KalamaHelperHelperJ)(switch (this.bU) {
            case DR -> new KalamaHelperHelperE(this);
            case DP -> null;
            case DQ -> new KalamaHelperHelperL(this);
         });
      } catch (Throwable var2) {
         this.close();
         Debug.chat(Text.translatable("widget.gui.item-edit-screen.open.error").formatted(Formatting.RED), var2.getMessage());
         Debug.f(var2);
         return null;
      }
   }

   @Override
   protected void init() {
      super.init();
      this.ch = new me.matl114.gui.KalamaHelperHelperE(0, 0, null).addTo(this);
      this.ci = new ContentDelegateWidget(
            this.x + CONTENT_START_X, this.y + CONTENT_START_Y + 20, this.backgroundWidth - 2 * CONTENT_START_X, this.content_end_y - CONTENT_START_Y - 20
         )
         .addTo(this);
      this.bW = ExecutableWidget.instance(this.x + CONTENT_START_X + 1, this.y + CONTENT_START_Y + 1, 18, 18)
         .<ExecutableWidget>eV(
            IconElement.cm(cb, ButtonAction.a(this::dj))
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.item-edit-screen.operate.save-item.tooltips", "")))
         )
         .addTo(this);
      this.bX = ExecutableWidget.instance(this.x + CONTENT_START_X + 21, this.y + CONTENT_START_Y + 1, 18, 18)
         .<ExecutableWidget>eV(
            IconElement.cm(cc, ButtonAction.a(this::executeSave))
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.item-edit-screen.operate.copy-give-command.tooltips", "")))
         )
         .addTo(this);
      this.bY = ExecutableWidget.instance(this.x + CONTENT_START_X + 41, this.y + CONTENT_START_Y + 1, 18, 18)
         .<ExecutableWidget>eV(
            IconElement.cm(cd, ButtonAction.a(() -> this.setState(KalamaHelperHelperI.DR)))
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.item-edit-screen.operate.switch-to-nbt-editor.tooltips", "")))
         )
         .addTo(this);
      this.bZ = ExecutableWidget.instance(this.x + CONTENT_START_X + 61, this.y + CONTENT_START_Y + 1, 18, 18)
         .<ExecutableWidget>eV(
            IconElement.cm(ce, ButtonAction.a(() -> this.setState(KalamaHelperHelperI.DP)))
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.item-edit-screen.operate.switch-to-snbt-editor.tooltips", "")))
         )
         .addTo(this);
      this.ca = ExecutableWidget.instance(this.x + CONTENT_START_X + 81, this.y + CONTENT_START_Y + 1, 18, 18)
         .<ExecutableWidget>eV(
            IconElement.cm(cf, ButtonAction.a(SlimefunTasks.w()::adZ))
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.gui.item-edit-screen.operate.open-slimefun-guide.tooltips", "")))
         )
         .addTo(this);
      this.setState(this.bU == null ? KalamaHelperHelperI.DR : this.bU);
   }

   public ItemEditScreen(Text title, ItemStack itemStack, Consumer<ItemStack> callback) {
      super(title);
      this.v = itemStack.copy();
      this.c = callback;
   }

   @Override
   protected void dh() {
      this.setState(null);
      this.close();
   }

   @Override
   protected boolean canConfirm(ElementHandler elementHandler) {
      return this.canConfirm();
   }

   protected void ah() {
      this.ch.setContentDelegate(null);
      this.ci.setContentDelegate(this.cg);
      if (this.cg != null) {
         this.cg.ah();
      }
   }

   public void applyChangeToInventory() {
      if (mc.player != null) {
         if (mc.interactionManager != null && mc.interactionManager.getCurrentGameMode().isCreative()) {
            Debug.b(Text.translatable("widget.gui.item-edit-screen.save.apply-changes.creative").formatted(Formatting.GREEN));
            int var2 = InventoryUtils.getSelectedSlot();
            InvTasks.setCreativeInventory(this.v, var2);
         } else {
            Debug.b(Text.translatable("widget.gui.item-edit-screen.save.apply-changes.command").formatted(Formatting.YELLOW));
            String var1 = InvTasks.createGiveCommand(this.v.copy());
            if (var1.length() >= 256) {
               Debug.b(Text.translatable("widget.gui.item-edit-screen.save.apply-changes.command.too-long").formatted(Formatting.RED));
               mc.keyboard.setClipboard(var1);
            } else {
               ChatTasks.sayMessage(var1, true);
            }
         }
      }
   }

   public void executeSave() {
      this.di();
      if (this.canConfirm()) {
         InvTasks.copyGiveCommand(this.v.copy());
      } else {
         Debug.b(Text.translatable("widget.gui.item-edit-screen.save.error"));
      }
   }

   public void dj() {
      this.di();
      if (this.canConfirm()) {
         InvTasks.aq().addSaveItem(this.v.copy());
      } else {
         Debug.b(Text.translatable("widget.gui.item-edit-screen.save.error"));
      }
   }
}
