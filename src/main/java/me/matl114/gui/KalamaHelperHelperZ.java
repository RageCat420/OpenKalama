package me.matl114.gui;

import java.util.function.Consumer;
import java.util.function.Predicate;
import me.matl114.accessors.gui.TextFieldAccess;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.KalamaHelperHelperIX;
import me.matl114.utils.config.PropertyTracker;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class KalamaHelperHelperZ extends KalamaHelperHelperAX<KalamaHelperHelperZ> {
   public int w;
   public Integer H;
   public PropertyTracker<TextFieldAccess, String> y;
   public Text u = Text.empty();
   public Boolean C;
   public String E;
   public Predicate<String> F;
   public Integer G;
   public Boolean I;
   public TextFieldWidget t;
   public Consumer<String> x;
   public String v = "";
   public Text D;
   public Boolean B;
   public Boolean A;
   public KalamaHelperHelperIX z;

   public KalamaHelperHelperZ Y(Text placeholder) {
      this.D = placeholder;
      return this;
   }

   public KalamaHelperHelperZ Q(String text) {
      this.v = text;
      return this;
   }

   public static KalamaHelperHelperZ builder() {
      return new KalamaHelperHelperZ();
   }

   public KalamaHelperHelperZ T(PropertyTracker<TextFieldAccess, String> listener) {
      this.y = listener;
      return this;
   }

   public KalamaHelperHelperZ() {
      this.w = 32768;
   }

   public KalamaHelperHelperZ U(KalamaHelperHelperIX borderColorProvider) {
      this.z = borderColorProvider;
      return this;
   }

   public KalamaHelperHelperZ ad(Boolean cursorToEnd) {
      this.I = cursorToEnd;
      return this;
   }

   public KalamaHelperHelperZ X(Boolean editable) {
      this.C = editable;
      return this;
   }

   public KalamaHelperHelperZ Z(String suggestion) {
      this.E = suggestion;
      return this;
   }

   public KalamaHelperHelperZ O(TextFieldWidget textFieldWidget) {
      this.t = textFieldWidget;
      return this;
   }

   @Override
   public ElementHandler d(WidgetSupplier factory) {
      return factory.h(this);
   }

   public KalamaHelperHelperZ S(Consumer<String> changedListener) {
      this.x = changedListener;
      return this;
   }

   public KalamaHelperHelperZ maxLength(int maxLength) {
      this.w = maxLength;
      return this;
   }

   public KalamaHelperHelperZ P(Text message) {
      this.u = message;
      return this;
   }

   public KalamaHelperHelperZ W(Boolean focusUnlocked) {
      this.B = focusUnlocked;
      return this;
   }

   public KalamaHelperHelperZ aa(Predicate<String> textPredicate) {
      this.F = textPredicate;
      return this;
   }

   public KalamaHelperHelperZ V(Boolean drawsBackground) {
      this.A = drawsBackground;
      return this;
   }

   public KalamaHelperHelperZ ab(Integer editableColor) {
      this.G = editableColor;
      return this;
   }

   public KalamaHelperHelperZ ac(Integer uneditableColor) {
      this.H = uneditableColor;
      return this;
   }
}
