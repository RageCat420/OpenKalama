package me.matl114.hacks.modules.combat;

import me.matl114.managers.config.ConfigEnum;
import org.jetbrains.annotations.ApiStatus.Experimental;

public enum PositionPredict$Mode implements ConfigEnum {
   NO_PREDICT,
   LINEAR,
   QUADRATIC,
   PREDICTOR_NV,
   @Experimental
   PREDICTOR_ROTATION;

   @Override
   public String getConfigEnumType() {
      return "predict_mode";
   }
}
