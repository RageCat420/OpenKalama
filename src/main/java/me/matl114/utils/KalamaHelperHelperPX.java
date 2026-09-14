package me.matl114.utils;

public record KalamaHelperHelperPX(float azimuth) implements KalamaHelperHelperAd {
   public float azimuth() {
      return this.azimuth;
   }


   @Override
   public String type() {
      return "Direction";
   }
}
