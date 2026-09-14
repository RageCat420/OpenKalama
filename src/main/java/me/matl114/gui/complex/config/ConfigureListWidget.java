package me.matl114.gui.complex.config;

import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.matl114.gui.FilterService;
import me.matl114.gui.McWidgetHelpers;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ContentDelegateWidget;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.presets.index.IndexedSubScreen;
import me.matl114.gui.presets.lists.ListEntryWidgetController;
import me.matl114.gui.presets.lists.ListUnmodifiableWidget;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.Ref;
import me.matl114.managers.config.StringRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.CollectionUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.utils.config.PropertyTracker;
import me.matl114.utils.containers.ArgsMap;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ConfigureListWidget extends IndexedSubScreen<Pair<String, Map<String, KalamaHelperHelperC<?>>>, ListUnmodifiableWidget> {
   private ContentDelegateWidget<TextFieldWidget> fa;
   protected int aB;
   public static final String eY = "kalama:configure_list_widget/filter_text_widget";
   protected Config dx;
   private ArgsMap eZ;
   private boolean initialized = false;
   private static final Map<String, String> eX = new HashMap<>();
   protected Map<String, ListEntryWidgetController> eW;
   protected int fd;
   protected int fc;

   protected static List<Pair<String, Map<String, KalamaHelperHelperC<?>>>> gq(Config config) {
      Map<String, Map<String, KalamaHelperHelperC<?>>> var1 = new LinkedHashMap<>();

      for (String var3 : config.getVisiblePaths()) {
         if (!ChatUtils.hasTranslation(var3)) {
            Debug.e("Missing translation key for", var3);
         }

         String[] var4 = Config.cutToPath(var3);
         Ref<?> var5 = config.get(var4);
         AttrKeyValue<?> var6 = var5.createKeyValue(var3);
         String var7 = var4[0];
         var1.computeIfAbsent(var7, k -> new LinkedHashMap<>()).put(var3, new KalamaHelperHelperC(var5, var6));
      }

      return var1.entrySet().stream().map(CollectionUtils::entryToPair).toList();
   }

   protected Pair<String, Map<String, KalamaHelperHelperC<?>>> go(String str) {
      return this.b.stream().filter(s -> Objects.equals(str, s.getFirst())).findFirst().orElse(null);
   }

   protected void gt(String key) {
      this.eW.remove(key);
   }

   private ConfigureListWidget(Config config, int x, int y, int dx, int dy, int indexDx, int indexDy, int blankDx, int inputDx, int buttonDx, ArgsMap args) {
      super(gq(config), x, y, dx, dy, indexDx, indexDy);
      this.dx = config;
      this.fc = blankDx;
      this.aB = inputDx;
      this.fd = buttonDx;
      this.initialized = true;
      this.eZ = args;
      this.af();
   }

   protected boolean applyFilter(KalamaHelperHelperC<?> keyValue) {
      String var2 = ((TextFieldWidget)(Object)this.fa.ef()).getText();
      return var2.isEmpty()
         ? true
         : FilterService.nameMatch(Text.translatableWithFallback(keyValue.keyValue().getKeyName(), keyValue.keyValue().getKeyName()).getString(), var2);
   }

   public void setGlobal(Pair<String, Map<String, KalamaHelperHelperC<?>>> config) {
      eX.put(this.dx.getConfigName(), (String)config.getFirst());
      this.selectIndexToDisplay(config, false);
   }

   public static ConfigureListWidget createConfigConfigure(
      Config config, int x, int y, int indexDx, int buttonDx, int blankDx, int inputDx, int dy, int dx, int maxDy, StringRef filterWidget
   ) {
      return new ConfigureListWidget(
         config, x, y, dx, maxDy, indexDx, dy, blankDx, inputDx, buttonDx, new ArgsMap().a("kalama:configure_list_widget/filter_text_widget", filterWidget)
      );
   }

   @Override
   protected void af() {
      if (this.initialized) {
         this.eW = new LinkedHashMap<>();
         StringRef var1 = this.eZ.get("kalama:configure_list_widget/filter_text_widget");
         this.fa = McWidgetHelpers.c(this.eT + 20, 1, this.aB + this.fc + this.fd + 20, this.eU - 2, PropertyTracker.event(this::gs), var1.get())
            .addToSub(this);
         super.af();
      }
   }

   protected Pair<String, Map<String, KalamaHelperHelperC<?>>> getFromKeyOr(String str, Map<String, KalamaHelperHelperC<?>> map) {
      return this.b.stream().filter(s -> Objects.equals(str, s.getFirst())).findFirst().orElseGet(() -> new Pair<>(str, map));
   }

   public Pair<String, Map<String, KalamaHelperHelperC<?>>> getGlobal() {
      return this.go(eX.get(this.dx.getConfigName()));
   }

   @Override
   public Pair<String, Map<String, KalamaHelperHelperC<?>>> bf() {
      return this.getGlobal();
   }

    public void saveSelected() {
      for (Pair<String, Map<String, KalamaHelperHelperC<?>>> var2 : this.b) {
         for (KalamaHelperHelperC<?> var4 : var2.getSecond().values()) {
            var4.save();
         }
      }

      this.dx.markForSave();
      Config.launchSaveTasks();
   }

   @Override
   public void bc() {
      this.saveSelected();
   }

   @Override
   protected ElementHandler be(Pair<String, Map<String, KalamaHelperHelperC<?>>> str) {
      return new ButtonElement(TextProvider.c(Text.translatable("config.index." + (String)str.getFirst())), ButtonAction.a(() -> this.setGlobal(str)))
         .cA(ButtonElement.bH)
         .cC(ButtonElement.bI)
         .cw(el -> Objects.equals(eX.get(this.dx.getConfigName()), str.getFirst()))
         .aO(TooltipHandler.ap(ChatUtils.parseTranslation("config.index." + str + ".tooltips", "暂无介绍")));
   }

   @Override
   protected ListUnmodifiableWidget bd(Pair<String, Map<String, KalamaHelperHelperC<?>>> val) {
      String var2 = (String)val.getFirst();
      return new ListUnmodifiableWidget(
         ListEntryWidgetController.immutable(
            this.getFromKeyOr(var2, Map.of()).getSecond().values().stream().filter(this::applyFilter).toList(),
            b -> new KalamaHelperHelperA<>(this.fc, 0, this.fd + this.fc + this.aB, this.eU, this.fd, this.fc, this.aB, (Ref)b.ref(), (AttrKeyValue)b.keyValue()),
            this.eU,
            this.fd + this.fc + this.aB
         ),
         20,
         this.eU,
         this.fd + this.fc + this.aB + 10,
         this.dy - this.eU
      );
   }

   protected void gs(String filter) {
      StringRef var2 = this.eZ.get("kalama:configure_list_widget/filter_text_widget");
      var2.set(filter);
      String var3 = eX.get(this.dx.getConfigName());
      if (var3 != null) {
         this.gt(var3);
         this.selectIndexToDisplay(this.go(var3), true);
      }
   }

}
