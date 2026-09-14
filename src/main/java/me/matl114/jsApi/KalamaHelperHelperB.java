package me.matl114.jsApi;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;

public class KalamaHelperHelperB extends Constructor {
   ClassLoader val$factory;

   @Override
   protected Class<?> getClassForName(String name) throws ClassNotFoundException {
      try {
         return Class.forName(name, true, this.val$factory);
      } catch (ClassNotFoundException var3) {
         return super.getClassForName(name);
      }
   }

   public KalamaHelperHelperB(LoaderOptions loadingConfig) {
      this(loadingConfig, KalamaHelperHelperB.class.getClassLoader());
   }

   public KalamaHelperHelperB(LoaderOptions loadingConfig, ClassLoader lookup) {
      super(loadingConfig);
      this.val$factory = lookup;
   }
}
