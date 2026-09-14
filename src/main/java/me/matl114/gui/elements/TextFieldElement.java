package me.matl114.gui.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.matl114.accessors.gui.TextFieldAccess;
import me.matl114.gui.McWidgetHelpers;
import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperIX;
import me.matl114.gui.basic.KalamaHelperHelperM;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.config.PropertyTracker;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class TextFieldElement extends AbstractElement {
   private boolean aE;
   private static final Identifier an = Identifier.ofVanilla("widget/text_field");
   private boolean aY;
   public static final Style at = Style.EMPTY.withColor(Formatting.DARK_GRAY);
   private boolean aA;
   private KalamaHelperHelperIX aZ;
   private final TextFieldAccess aw = new KalamaHelperHelperF(this);
   private boolean aG;
   private boolean aB;
   private int bb;
   private int aH;
   private int aV;
   private static final int ar = -9408400;
   private static final int aq = -2039584;
   private boolean aF;
   private static final long DOUBLE_CLICK_INTERVAL = 250L;
   private boolean aC;
   private long aS;
   private int aJ;
   public static final Style au = Style.EMPTY.withFormatting(new Formatting[]{Formatting.GRAY, Formatting.ITALIC});
   private boolean aX;
   private final TextRenderer av;
   private int aK;
   private Consumer<String> aN;
   private final List<KalamaHelperHelperT> aQ;
   private int aW;
   private String aM;
   private int aU;
   private String ay = "";
   private Text ax;
   private PropertyTracker<TextFieldAccess, String> aO;
   private Predicate<String> aP;
   private static final Identifier ao = Identifier.ofVanilla("widget/text_field_highlighted");
   private static final String as = "_";
   private int aI;
   private int aT;
   private int az = 32;
   private int bc;
   private long ba;
   private int aL;
   private boolean aD;
   private Text aR;
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   public TextFieldElement bd(int maxLength) {
      this.az = Math.max(0, maxLength);
      if (this.ay.length() > this.az) {
         this.ay = this.ay.substring(0, this.az);
         this.aI = Math.min(this.aI, this.ay.length());
         this.aJ = Math.min(this.aJ, this.ay.length());
         this.updateFirstCharacterIndex(this.aI);
         this.bT(this.ay);
      }

      return this;
   }

   protected int bF() {
      return this.aA ? Math.max(0, this.aV - 8) : this.aV;
   }

   protected boolean bD() {
      return this.aY && this.aC;
   }

   public void bJ(int characterOffset) {
      this.bK(this.getCursorPosWithOffset(characterOffset));
   }

   public TextFieldElement() {
      this(Text.empty());
   }

   protected void updateTextPosition() {
      String var1 = this.av.trimToWidth(this.ay.substring(this.aH), this.bF());
      this.aT = this.aD ? (this.aV - this.av.getWidth(var1)) / 2 : (this.aA ? 4 : 0);
      this.aU = this.aA ? (this.aW - 8) / 2 : 0;
   }

   public void bK(int position) {
      if (!this.ay.isEmpty()) {
         if (this.aJ != this.aI) {
            this.write("");
         } else {
            int var2 = Math.min(position, this.aI);
            int var3 = Math.max(position, this.aI);
            if (var2 != var3) {
               String var4 = new StringBuilder(this.ay).delete(var2, var3).toString();
               if (this.aP.test(var4)) {
                  this.ay = var4;
                  this.bQ(var2, false);
               }
            }
         }
      }
   }

   public void drawSelection(DrawContext context, int x1, int y1, int x2, int y2, boolean invert) {
      if (invert) {
         context.fill(RenderLayer.getGui(), x1, y1, x2, y2, -1);
      }

      context.fill(RenderLayer.getGuiTextHighlight(), x1, y1, x2, y2, -16776961);
   }

   protected int bB(DrawableWidget element, int mouseY) {
      return (int)(Object)this.bz(element, mouseY);
   }

   @Override
   public boolean onAction(ExecutableWidget element, double mouseX, double mouseY, int button, KalamaHelperHelperM type) {
      if (super.onAction(element, mouseX, mouseY, button, type)) {
         return true;
      } else {
         this.syncWidgetState(element);
         if (!this.aG) {
            this.aX = false;
            return false;
         } else {
            double var8 = this.by(element, mouseX);
            double var10 = this.bz(element, mouseY);

            return switch (type) {
               case nP -> {
                  if (button == 0 && this.canStartDrag(var8, var10)) {
                     int var17 = this.calculateCursorPos(var8);
                     long var13 = Util.getMeasuringTimeMs();
                     boolean var15 = this.bc == button && this.bb == var17 && var13 - this.ba <= 250L;
                     if (var15) {
                        this.bX(var17);
                     } else {
                        this.bQ(var17, ScreenUtils.hasShiftDown());
                     }

                     this.ba = var13;
                     this.bb = var17;
                     this.bc = button;
                     yield true;
                  } else {
                     yield false;
                  }
               }
               case nQ -> {
                  boolean var16 = this.aX;
                  this.aX = false;
                  yield var16;
               }
               case nR -> {
                  this.aX = button == 0 && this.canStartDrag(var8, var10);
                  yield this.aX;
               }
               case nS -> {
                  if (this.aX) {
                     this.dragSelect((int)var8, (int)var10, true);
                     yield true;
                  } else {
                     yield false;
                  }
               }
               case nT -> {
                  boolean var12 = this.aX;
                  this.aX = false;
                  yield var12;
               }
            };
         }
      }
   }

   protected int bA(DrawableWidget element, int mouseX) {
      return (int)(Object)this.by(element, mouseX);
   }

   @Override
   public boolean onClick(ExecutableWidget element, double mouseX, double mouseY, int button) {
      throw new UnsupportedOperationException("Use onAction(Type.MOUSE_CLICK) instead");
   }

   protected void updateFirstCharacterIndex(int cursor) {
      this.aH = Math.min(this.aH, this.ay.length());
      int var2 = this.bF();
      String var3 = this.av.trimToWidth(this.ay.substring(this.aH), var2);
      int var4 = var3.length() + this.aH;
      if (cursor == this.aH) {
         this.aH = this.aH - this.av.trimToWidth(this.ay, var2, true).length();
      }

      if (cursor > var4) {
         this.aH += cursor - var4;
      } else if (cursor <= this.aH) {
         this.aH = this.aH - (this.aH - cursor);
      }

      this.aH = MathHelper.clamp(this.aH, 0, this.ay.length());
   }

   public TextFieldElement bh(boolean drawsBackground) {
      this.aA = drawsBackground;
      this.updateTextPosition();
      return this;
   }

   protected boolean bC() {
      return this.aC;
   }

   public static TextFieldElement aW(Text message) {
      return new TextFieldElement(message);
   }

   public TextFieldElement bo(boolean shiftKeyPressed) {
      this.bQ(this.ay.length(), shiftKeyPressed);
      return this;
   }

   protected boolean canStartDrag(double mouseX, double mouseY) {
      return mouseX >= 0.0 && mouseY >= 0.0 && mouseX < this.aV && mouseY < this.aW;
   }

   public TextFieldElement setPlaceholder(Text placeholder) {
      if (placeholder == null) {
         this.aR = null;
      } else {
         boolean var2 = placeholder.getStyle().equals(Style.EMPTY);
         this.aR = (Text)(var2 ? placeholder.copy().fillStyle(at) : placeholder);
      }

      return this;
   }

   public String ba() {
      return this.ay;
   }

   public TextFieldElement bf(PropertyTracker<TextFieldAccess, String> tracker) {
      this.aO = tracker;
      return this;
   }

   public TextFieldElement bl(String suggestion) {
      this.aM = suggestion;
      return this;
   }

   public TextFieldElement bt(boolean visible) {
      this.aG = visible;
      return this;
   }

   public TextFieldElement bg(KalamaHelperHelperIX provider) {
      this.aZ = provider;
      return this;
   }

   public TextFieldElement br(boolean textShadow) {
      this.aE = textShadow;
      return this;
   }

   public int bw() {
      return this.aI;
   }

   protected void write(String value) {
      int var2 = Math.min(this.aI, this.aJ);
      int var3 = Math.max(this.aI, this.aJ);
      int var4 = this.az - this.ay.length() - (var2 - var3);
      if (var4 > 0) {
         String var5 = StringHelper.stripInvalidChars(value);
         int var6 = var5.length();
         if (var4 < var6) {
            if (var4 > 0 && Character.isHighSurrogate(var5.charAt(var4 - 1))) {
               var4--;
            }

            var5 = var5.substring(0, Math.max(0, var4));
            var6 = var5.length();
         }

         String var7 = new StringBuilder(this.ay).replace(var2, var3, var5).toString();
         if (this.aP.test(var7)) {
            this.ay = var7;
            this.bR(var2 + var6);
            this.setSelectionStart(this.aI);
            this.bT(this.ay);
         }
      }
   }

   public TextFieldElement bq(boolean centered) {
      this.aD = centered;
      this.updateTextPosition();
      return this;
   }

   protected int calculateCursorPos(double mouseX) {
      int var3 = Math.min(MathHelper.floor(mouseX) - this.aT, this.bF());
      String var4 = this.ay.substring(this.aH);
      return this.aH + this.av.trimToWidth(var4, var3).length();
   }

   protected boolean handleKeyPressed(int keyCode, int scanCode, int modifiers) {
      if (!this.aY) {
         return false;
      } else if (mc.options.inventoryKey.matchesKey(keyCode, scanCode)) {
         return true;
      } else {
         boolean var4 = this.ce(modifiers);
         boolean var5 = this.cf(modifiers);
         switch (keyCode) {
            case 259:
               if (this.aC) {
                  this.bH(-1, var4);
               }

               return true;
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               if (var4 && keyCode == 65) {
                  this.bQ(this.ay.length(), false);
                  this.setSelectionStart(0);
                  return true;
               } else if (var4 && keyCode == 67) {
                  mc.keyboard.setClipboard(this.bb());
                  return true;
               } else if (var4 && keyCode == 86) {
                  if (this.aC) {
                     this.write(mc.keyboard.getClipboard());
                  }

                  return true;
               } else {
                  if (var4 && keyCode == 88) {
                     mc.keyboard.setClipboard(this.bb());
                     if (this.aC) {
                        this.write("");
                     }

                     return true;
                  }

                  return false;
               }
            case 261:
               if (this.aC) {
                  this.bH(1, var4);
               }

               return true;
            case 262:
               if (var4) {
                  this.bQ(this.bL(1), var5);
               } else {
                  this.bO(1, var5);
               }

               return true;
            case 263:
               if (var4) {
                  this.bQ(this.bL(-1), var5);
               } else {
                  this.bO(-1, var5);
               }

               return true;
            case 268:
               this.bQ(0, var5);
               return true;
            case 269:
               this.bQ(this.ay.length(), var5);
               return true;
         }
      }
   }

   public TextFieldElement aZ(String text) {
      String var2 = text == null ? "" : text;
      if (this.aP.test(var2)) {
         this.ay = var2.length() > this.az ? var2.substring(0, this.az) : var2;
         this.bo(false);
         this.setSelectionStart(this.aI);
         this.bT(this.ay);
      }

      return this;
   }

   public TextFieldElement bu(KalamaHelperHelperT formatter) {
      if (formatter != null) {
         this.aQ.add(formatter);
      }

      return this;
   }

   public TextFieldElement bc(Predicate<String> textPredicate) {
      this.aP = textPredicate == null ? Objects::nonNull : textPredicate;
      return this;
   }

   public TextFieldElement(Text message) {
      this.aA = true;
      this.aB = true;
      this.aC = true;
      this.aD = false;
      this.aE = true;
      this.aF = true;
      this.aG = true;
      this.aK = -2039584;
      this.aL = -9408400;
      this.aP = Objects::nonNull;
      this.aQ = new ArrayList<>();
      this.aS = Util.getMeasuringTimeMs();
      this.bb = -1;
      this.bc = -1;
      this.av = mc.textRenderer;
      this.ax = (Text)(message == null ? Text.empty() : message);
      this.updateTextPosition();
   }

   protected void setSelectionStart(int index) {
      this.aJ = MathHelper.clamp(index, 0, this.ay.length());
      this.updateFirstCharacterIndex(this.aJ);
   }

   protected void bR(int cursor) {
      this.aI = MathHelper.clamp(cursor, 0, this.ay.length());
      this.updateFirstCharacterIndex(this.aI);
   }

   protected void bO(int offset, boolean shiftKeyPressed) {
      this.bQ(this.getCursorPosWithOffset(offset), shiftKeyPressed);
   }

   protected boolean bE() {
      return this.aA;
   }

   public void renderCentered0(DrawableWidget element, VDrawContext context, int mouseX, int mouseY, float delta, float alpha, boolean shouldHighlight) {
      this.syncWidgetState(element);
      if (this.aG) {
         int var8 = this.bA(element, mouseX);
         int var9 = this.bB(element, mouseY);
         DrawContext var10 = context.b();

         try {
            if (this.bE()) {
               if (this.aZ != null) {
                  McWidgetHelpers.drawTextWidgetBox(element, var10, 0, 0, this.aV, this.aW, this.aY, this.aZ);
               } else {
                  context.V(this.aY ? ao : an, 0, 0, this.aV, this.aW);
               }
            }

            int var11 = this.aC ? this.aK : this.aL;
            int var12 = this.aI - this.aH;
            String var13 = this.av.trimToWidth(this.ay.substring(this.aH), this.bF());
            boolean var14 = var12 >= 0 && var12 <= var13.length();
            boolean var15 = this.aY && (Util.getMeasuringTimeMs() - this.aS) / 300L % 2L == 0L && var14;
            int var16 = this.aT;
            int var17 = MathHelper.clamp(this.aJ - this.aH, 0, var13.length());
            if (!var13.isEmpty()) {
               String var18 = var14 ? var13.substring(0, var12) : var13;
               OrderedText var19 = this.format(var18, this.aH);
               var10.drawText(this.av, var19, var16, this.aU, var11, this.aE);
               var16 += this.av.getWidth(var19) + 1;
            }

            boolean var24 = this.aI < this.ay.length() || this.ay.length() >= this.az;
            int var25 = var16;
            if (!var14) {
               var25 = var12 > 0 ? this.aT + this.aV : this.aT;
            } else if (var24) {
               var25 = var16 - 1;
               var16--;
            }

            if (!var13.isEmpty() && var14 && var12 < var13.length()) {
               var10.drawText(this.av, this.format(var13.substring(var12), this.aI), var16, this.aU, var11, this.aE);
            }

            if (this.aR != null && var13.isEmpty() && !this.aY) {
               var10.drawTextWithShadow(this.av, this.aR, var16, this.aU, var11);
            }

            if (!var24 && this.aM != null) {
               var10.drawText(this.av, this.aM, var25 - 1, this.aU, -8355712, this.aE);
            }

            if (var17 != var12) {
               int var20 = this.aT + this.av.getWidth(var13.substring(0, var17));
               this.drawSelection(var10, Math.min(var25, this.aV), this.aU - 1, Math.min(var20 - 1, this.aV), this.aU + 10, this.aF);
            }

            if (var15) {
               if (var24) {
                  var10.fill(var25, this.aU - 1, var25 + 1, this.aU + 10, var11);
               } else {
                  var10.drawText(this.av, "_", var25, this.aU, var11, this.aE);
               }
            }
         } finally {
            context.c();
         }
      }
   }

   public TextFieldElement bp(boolean shiftKeyPressed) {
      this.bQ(0, shiftKeyPressed);
      return this;
   }

   protected OrderedText format(String value, int firstCharacterIndex) {
      for (KalamaHelperHelperT var4 : this.aQ) {
         OrderedText var5 = var4.format(value, firstCharacterIndex);
         if (var5 != null) {
            return var5;
         }
      }

      return OrderedText.styledForwardsVisitedString(value, Style.EMPTY);
   }

   protected void bH(int offset, boolean words) {
      if (words) {
         this.bI(offset);
      } else {
         this.bJ(offset);
      }
   }

   protected double bz(DrawableWidget element, double mouseY) {
      return (mouseY - element.getY()) / element.getTextureScale();
   }

   public void bI(int wordOffset) {
      if (!this.ay.isEmpty()) {
         if (this.aJ != this.aI) {
            this.write("");
         } else {
            this.bK(this.bL(wordOffset));
         }
      }
   }

   public String bb() {
      int var1 = Math.min(this.aI, this.aJ);
      int var2 = Math.max(this.aI, this.aJ);
      return this.ay.substring(var1, var2);
   }

   protected void dragSelect(int mouseX, int mouseY, boolean shiftDownAction) {
      int var4 = mouseX;
      if (this.bE()) {
         var4 = mouseX - 4;
      }

      String var5 = this.av.trimToWidth(this.ay.substring(this.aH), this.bF());
      this.bQ(this.av.trimToWidth(var5, var4).length() + this.aH, shiftDownAction);
   }

   protected boolean cf(int modifiers) {
      return (modifiers & 1) != 0;
   }

   public TextFieldElement bs(boolean invertSelectionBackground) {
      this.aF = invertSelectionBackground;
      return this;
   }

   protected int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
      int var4 = cursorPosition;
      boolean var5 = wordOffset < 0;
      int var6 = Math.abs(wordOffset);

      for (int var7 = 0; var7 < var6; var7++) {
         if (!var5) {
            int var8 = this.ay.length();
            var4 = this.ay.indexOf(32, var4);
            if (var4 == -1) {
               var4 = var8;
            } else {
               while (skipOverSpaces && var4 < var8 && this.ay.charAt(var4) == ' ') {
                  var4++;
               }
            }
         } else {
            while (skipOverSpaces && var4 > 0 && this.ay.charAt(var4 - 1) == ' ') {
               var4--;
            }

            while (var4 > 0 && this.ay.charAt(var4 - 1) != ' ') {
               var4--;
            }
         }
      }

      return var4;
   }

   protected void syncWidgetState(DrawableWidget element) {
      this.aV = Math.max(0, element.getTextureWidth());
      this.aW = Math.max(0, element.getTextureHeight());
      boolean var2 = element.isFocused();
      if ((this.aB || var2) && this.aY != var2) {
         this.aY = var2;
         if (var2) {
            this.aS = Util.getMeasuringTimeMs();
         } else {
            this.bv();
         }
      }

      this.updateTextPosition();
   }

   protected boolean handleCharTyped(char chr) {
      if (!this.bD()) {
         return false;
      } else if (!StringHelper.isValidChar(chr)) {
         return false;
      } else {
         this.write(Character.toString(chr));
         return true;
      }
   }

   public Text aX() {
      return this.ax;
   }

   protected void bX(int cursor) {
      int var2 = this.bM(-1, cursor);
      int var3 = this.bM(1, cursor);
      this.bQ(var2, false);
      this.bQ(var3, true);
   }

   public TextFieldElement bn(int uneditableColor) {
      this.aL = uneditableColor;
      return this;
   }

   @Override
   public boolean onKey(ExecutableWidget widget, int keyCode, int scanCode, int modifiers, boolean isPress) {
      this.syncWidgetState(widget);
      return isPress && this.handleKeyPressed(keyCode, scanCode, modifiers) ? true : super.onKey(widget, keyCode, scanCode, modifiers, isPress);
   }

   public static TextFieldElement aV() {
      return new TextFieldElement();
   }

   public TextFieldElement bi(boolean focusUnlocked) {
      this.aB = focusUnlocked;
      return this;
   }

   public TextFieldElement be(Consumer<String> listener) {
      this.aN = listener;
      return this;
   }

   public TextFieldElement bv() {
      this.aJ = this.aI;
      this.updateTextPosition();
      return this;
   }

   public int bL(int wordOffset) {
      return this.bM(wordOffset, this.bw());
   }

   protected int getCursorPosWithOffset(int offset) {
      return Util.moveCursor(this.ay, this.aI, offset);
   }

   protected boolean ce(int modifiers) {
      return (modifiers & 2) != 0 || (modifiers & 8) != 0;
   }

   protected int bM(int wordOffset, int cursorPosition) {
      return this.getWordSkipPosition(wordOffset, cursorPosition, true);
   }

   public TextFieldElement bm(int editableColor) {
      this.aK = editableColor;
      return this;
   }

   protected double by(DrawableWidget element, double mouseX) {
      return (mouseX - element.getX()) / element.getTextureScale();
   }

   public TextFieldElement bj(boolean editable) {
      this.aC = editable;
      return this;
   }

   public TextFieldElement setMessage(Text message) {
      this.ax = (Text)(message == null ? Text.empty() : message);
      return this;
   }

   public TextFieldElement(TextFieldWidget textFieldWidget) {
      this((Text)(textFieldWidget == null ? Text.empty() : textFieldWidget.getMessage()));
      if (textFieldWidget != null) {
         this.ay = textFieldWidget.getText();
         this.aA = textFieldWidget.drawsBackground();
         this.aI = MathHelper.clamp(textFieldWidget.getCursor(), 0, this.ay.length());
         this.aJ = this.aI;
         this.updateFirstCharacterIndex(this.aI);
         this.updateTextPosition();
      }
   }

   protected void bQ(int cursor, boolean select) {
      this.bR(cursor);
      if (!select) {
         this.setSelectionStart(this.aI);
      }

      this.updateTextPosition();
   }

   @Override
   public boolean onTyped(ExecutableWidget widget, char chr, int modifiers) {
      this.syncWidgetState(widget);
      return this.handleCharTyped(chr) ? true : super.onTyped(widget, chr, modifiers);
   }

   protected void bT(String newText) {
      if (this.aN != null) {
         this.aN.accept(newText);
      }

      if (this.aO != null) {
         this.aO.valueChange(this.aw, newText);
      }

      this.updateTextPosition();
   }
}
