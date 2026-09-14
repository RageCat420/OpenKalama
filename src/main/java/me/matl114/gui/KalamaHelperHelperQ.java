package me.matl114.gui;

public record KalamaHelperHelperQ(int indexWidth, int blankWidth, int buttonWidth, int buttonHeight, int buttonBlank) {
   public int buttonWidth() {
      return this.buttonWidth;
   }

   public int buttonHeight() {
      return this.buttonHeight;
   }

   public int indexWidth() {
      return this.indexWidth;
   }

   public KalamaHelperHelperQ(int indexWidth, int blankWidth, int buttonWidth, int buttonHeight, int buttonBlank) {
      this.blankWidth = indexWidth;
      this.buttonHeight = blankWidth;
      this.buttonBlank = buttonWidth;
      this.buttonWidth = buttonHeight;
      this.indexWidth = buttonBlank;
   }

   public int blankWidth() {
      return this.blankWidth;
   }

   public int Nj() {
      return this.blankWidth + this.buttonHeight + this.buttonBlank;
   }

   public int buttonBlank() {
      return this.buttonBlank;
   }
}
