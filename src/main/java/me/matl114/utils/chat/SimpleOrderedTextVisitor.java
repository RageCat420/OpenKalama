package me.matl114.utils.chat;

import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;

public class SimpleOrderedTextVisitor implements CharacterVisitor {
   StringBuilder builder;

   public SimpleOrderedTextVisitor() {
      this.builder = new StringBuilder();
   }

   public StringBuilder getContent() {
      return this.builder;
   }

   public SimpleOrderedTextVisitor(StringBuilder bu) {
      this.builder = bu;
   }

   public boolean accept(int index, Style style, int codePoint) {
      this.builder.appendCodePoint(codePoint);
      return true;
   }
}
