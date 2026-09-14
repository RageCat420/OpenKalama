package me.matl114.gui.presets.choices;

import net.minecraft.text.Text;

public class KalamaHelperHelperA {
   Text solutionLabel;

   public void execution() { }

   public Text getSolutionLabel() {
      return this.solutionLabel;
   }

   public static KalamaHelperHelperA of(Text label, Runnable task) {
      return new KalamaHelperHelperH(label, task);
   }

   public KalamaHelperHelperA(Text solutionLabel) {
      this.solutionLabel = solutionLabel;
   }
}
