package me.matl114.hacks.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ModuleManager extends AbstractManager<BaseModule> {
   public List<Consumer<ModuleManager>> registeringFunctions = new ArrayList<>();

   public void unregisterFactories(Predicate<Consumer<ModuleManager>> function) {
      this.registeringFunctions.removeIf(function);
   }

   public List<BaseModule> getModules() {
      return Collections.unmodifiableList(this.registered);
   }

   @Override
   public void loadModules() {
      this.registeringFunctions.forEach(consumer -> consumer.accept(this));
   }

   public void registerFactories(Consumer<ModuleManager> function) {
      this.registeringFunctions.add(function);
      function.accept(this);
   }

   public BaseModule getModule(String name) {
      return this.registered.stream().filter(s -> name.equalsIgnoreCase(s.getName())).findFirst().orElse(null);
   }

   public void registerModule(BaseModule module) {
      super.registerModule(module);
      module.onCreate();
   }

   public void unregisterModule(BaseModule module) {
      super.unregisterModule(module);
      module.onRemove();
   }

   public abstract String getName();
}
