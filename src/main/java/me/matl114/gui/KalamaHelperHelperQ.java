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

   

   public int blankWidth() {
      return this.blankWidth;
   }

    public int Nj() {
       return this.indexWidth + this.blankWidth + this.buttonWidth;
    }

   public int buttonBlank() {
      return this.buttonBlank;
   }
}
