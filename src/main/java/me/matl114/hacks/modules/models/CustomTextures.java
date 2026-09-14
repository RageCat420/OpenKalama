package me.matl114.hacks.modules.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import me.matl114.events.Event;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.ListRef;
import me.matl114.utils.Debug;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class CustomTextures extends BaseModule {
   private static Identifier LI = new Identifier("minecraft", "blocks");
   public final ModulePath LG = makePath(Configs.q, "texture-config");
   private static final String iD = "kalama";
   public final FlagRef ae = this.flagBuilder(this.LG.add("enable")).build();
   public final ListRef LH = this.builder(this.LG.add("namespace-for-custom-textures"), ListRef.TYPE)
      .defaultValue(List.of("ae2", "infinityexpansion", "avaritia"))
      .build();

   public Collection<Identifier> loadOurselvesCustomModelTexture(ResourceManager manager) {
      ArrayList<Identifier> var2 = new ArrayList<>();
      HashSet<String> var3 = new HashSet<>(this.LH.get());
      List<Predicate<String>> var4 = var3.stream().map(s -> {
         if (s.contains(":")) {
            try {
               return Pattern.compile(s).asMatchPredicate();
            } catch (Throwable var2x) {
               Debug.e("Illegal format of texture path : ", s);
               return null;
            }
         } else {
            String var1 = s + ":";
            return v -> v.startsWith(var1);
         }
      }).filter(Objects::nonNull).toList();
      Debug.a("Custom Atlas load start");

      for (ResourcePack var6 : manager.streamResourcePacks().toList()) {
         String var7 = var6.getId();
         if (!var7.equals("minecraft") && !var7.equals("realms") && !var7.startsWith("fabric-") && !var7.equals("fabric") && !var7.equals("vanilla")) {
            if (var7.equals("kalama")) {
               var6.findResources(ResourceType.CLIENT_RESOURCES, "kalama", "textures/slimefunitem", (i, j) -> {
                  String var3x = i.getNamespace();
                  if (i.getPath().endsWith(".png")) {
                     String var4x = i.getPath().replaceFirst("^textures/", "").replaceAll(".png$", "");
                     Identifier var5 = new Identifier(var3x, var4x);
                     var2.add(var5);
                  }
               });
            } else if (this.ae.get()) {
               for (String var10 : var6.getNamespaces(ResourceType.CLIENT_RESOURCES)) {
                  var6.findResources(ResourceType.CLIENT_RESOURCES, var10, "textures", (i, j) -> {
                     String var4x = i.getNamespace();
                     if (i.getPath().endsWith(".png")) {
                        String var5 = i.getPath().replaceFirst("^textures/", "").replaceAll(".png$", "");
                        Identifier var6x = new Identifier(var4x, var5);
                        String var7x = var6x.toString();
                        if (var4.stream().anyMatch(p -> p.test(var7x))) {
                           var2.add(var6x);
                        }
                     }
                  });
               }
            }
         }
      }

      return var2;
   }

   public CustomTextures() {
      super("CustomTextures");
      this.bindFlag(this.ae);
   }

   public void onAtlasSupply(Event<Set<Identifier>> event) {
      if (LI.equals(event.getArgs(1))) {
         Debug.a("Loading blocks atlases");
         Debug.a("Appending our textures automatically");
         ((Set)event.e()).addAll(this.loadOurselvesCustomModelTexture(event.getArgs(0)));
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(RenderListener.w(), this::onAtlasSupply);
   }
}
