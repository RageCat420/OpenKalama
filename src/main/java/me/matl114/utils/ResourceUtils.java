package me.matl114.utils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class ResourceUtils {
   public static Set<Identifier> c(ResourceManager m, String prefix) {
      return lookupResources(m, "kalama", "kalama", "textures", ".png", s -> s.startsWith(prefix));
   }

   public static Identifier ofAtlasTexture(String type) {
      return Identifier.ofVanilla("textures/atlas/" + type + ".png");
   }

   public static Set<Identifier> lookupResources(
      ResourceManager resourceManager, String packId, String namespace, String prefix, String fileType, Predicate<String> pathPredicate
   ) {
      LinkedHashSet var6 = new LinkedHashSet();

      for (ResourcePack var8 : resourceManager.streamResourcePacks().toList()) {
         if (var8.getId().equals(packId)) {
            var8.findResources(ResourceType.CLIENT_RESOURCES, namespace, prefix, (i, j) -> {
               String var6x = i.getNamespace();
               if (i.getPath().endsWith(fileType)) {
                  String var7 = i.getPath().replaceFirst("^" + prefix + "/", "").replaceAll(fileType + "$", "");
                  if (pathPredicate.test(var7)) {
                     var6.add(new Identifier(var6x, var7));
                  }
               }
            });
         }
      }

      return var6;
   }

   public static Set<Identifier> b(ResourceManager m, String prefix) {
      return lookupResources(m, "kalama", "kalama", "models", ".json", s -> s.startsWith(prefix));
   }
}
