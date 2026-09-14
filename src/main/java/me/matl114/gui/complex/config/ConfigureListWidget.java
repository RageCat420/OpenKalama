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
      LinkedHashMap var1 = new LinkedHashMap();

      for (String var3 : config.getVisiblePaths()) {
         if (!ChatUtils.hasTranslation(var3)) {
            Debug.e("Missing translation key for", var3);
         }

         String[] var4 = Config.cutToPath(var3);
         Ref var5 = config.get(var4);
         BaseAttrKeyValue var6 = var5.createKeyValue(var3);
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
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index: 0
      //   at java.base/java.util.Collections$EmptyList.get(Collections.java:4808)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.getInferredExprType(NewExprent.java:170)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInferredExprType(InvocationExprent.java:505)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getInferredExprType(FunctionExprent.java:242)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:962)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ExitExprent.toJava(ExitExprent.java:86)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:891)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:91)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1306)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield me/matl114/gui/complex/config/ConfigureListWidget.b Ljava/util/List;
      // 04: invokeinterface java/util/List.stream ()Ljava/util/stream/Stream; 1
      // 09: aload 1
      // 0a: invokedynamic test (Ljava/lang/String;)Ljava/util/function/Predicate; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Z, me/matl114/gui/complex/config/ConfigureListWidget.gw (Ljava/lang/String;Lcom/mojang/datafixers/util/Pair;)Z, (Lcom/mojang/datafixers/util/Pair;)Z ]
      // 0f: invokeinterface java/util/stream/Stream.filter (Ljava/util/function/Predicate;)Ljava/util/stream/Stream; 2
      // 14: invokeinterface java/util/stream/Stream.findFirst ()Ljava/util/Optional; 1
      // 19: aload 1
      // 1a: aload 2
      // 1b: invokedynamic get (Ljava/lang/String;Ljava/util/Map;)Ljava/util/function/Supplier; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, me/matl114/gui/complex/config/ConfigureListWidget.getFromKeyOr (Ljava/lang/String;Ljava/util/Map;)Lcom/mojang/datafixers/util/Pair;, ()Lcom/mojang/datafixers/util/Pair; ]
      // 20: invokevirtual java/util/Optional.orElseGet (Ljava/util/function/Supplier;)Ljava/lang/Object;
      // 23: checkcast com/mojang/datafixers/util/Pair
      // 26: areturn
   }

   public Pair<String, Map<String, KalamaHelperHelperC<?>>> getGlobal() {
      return this.go(eX.get(this.dx.getConfigName()));
   }

   public void saveSelected() {
      for (Pair var2 : this.b) {
         for (KalamaHelperHelperC var4 : ((Map)var2.getSecond()).values()) {
            var4.save();
         }
      }

      this.dx.markForSave();
      Config.launchSaveTasks();
   }

   protected ElementHandler createIndexHandler(Pair<String, Map<String, KalamaHelperHelperC<?>>> str) {
      return new ButtonElement(TextProvider.c(Text.translatable("config.index." + (String)str.getFirst())), ButtonAction.a(() -> this.setGlobal(str)))
         .cA(ButtonElement.bH)
         .cC(ButtonElement.bI)
         .cw(el -> Objects.equals(eX.get(this.dx.getConfigName()), str.getFirst()))
         .aO(TooltipHandler.ap(ChatUtils.parseTranslation("config.index." + str + ".tooltips", "暂无介绍")));
   }

   protected ListUnmodifiableWidget createSelectingDisplayWidget(Pair<String, Map<String, KalamaHelperHelperC<?>>> val) {
      String var2 = (String)val.getFirst();
      return new ListUnmodifiableWidget(
         ListEntryWidgetController.immutable(
            ((Map)(Object)this.getFromKeyOr(var2, Map.of()).getSecond()).values().stream().filter(this::applyFilter).toList(),
            b -> new KalamaHelperHelperA<>(this.fc, 0, this.fd + this.fc + this.aB, this.eU, this.fd, this.fc, this.aB, b.uW(), b.uX()),
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



   @Override
   public ElementHandler be(Object arg0, Object arg1, Object arg2) { return null; }

}
