package me.matl114.bukkit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

class KalamaHelperHelperY extends SafeConstructor.ConstructYamlMap {
   @Override
   public void construct2ndStep(@NotNull Node node, @NotNull Object object) {
      throw new YAMLException("Unexpected referential mapping structure. Node: " + node);
   }

   public KalamaHelperHelperY(final KalamaHelperHelperQ param1) {
      param1.super();
   }

   @Nullable
   @Override
   public Object construct(@NotNull Node node) {
      if (node.isTwoStepsConstruction()) {
         throw new YAMLException("Unexpected referential mapping structure. Node: " + node);
      } else {
         Map<?, ?> var2 = (Map<?, ?>)super.construct(node);
         if (!var2.containsKey("==")) {
            return var2;
         } else {
            LinkedHashMap var3 = new LinkedHashMap(var2.size());

            for (Entry<?, ?> var5 : var2.entrySet()) {
               var3.put(var5.getKey().toString(), var5.getValue());
            }

            try {
               return BukkitSerializationMock.h(var3);
            } catch (IllegalArgumentException var7) {
               throw new YAMLException("Could not deserialize object", var7);
            }
         }
      }
   }
}
