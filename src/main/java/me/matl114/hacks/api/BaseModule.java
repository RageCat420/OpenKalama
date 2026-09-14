package me.matl114.hacks.api;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import me.matl114.commands.KalamaHelperHelperB;
import me.matl114.commands.MainCommand;
import me.matl114.events.catchers.PacketCatcher;
import me.matl114.events.impl.EventContainer;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.ColorLabelTextElement;
import me.matl114.hacks.modules.task.ClickGui;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.config.StringFormat;
import me.matl114.managers.Configs;
import me.matl114.managers.config.Config;
import me.matl114.managers.config.ConfigEnum;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.EnumRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.ListRef;
import me.matl114.managers.config.Ref;
import me.matl114.managers.input.IHotKey;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.managers.input.SimpleHotKey;
import me.matl114.managers.input.SimpleHotKey$InputHandler;
import me.matl114.managers.input.SimpleInputManager;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.commands.commandGroup.AbstractMainCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

public class BaseModule implements IModule {
   protected static final String REASON_MODULE_BINDING = "module binding";
   Boolean shouldShowInGuiCache;
   protected static final String REASON_COMMAND_BOOTSTRAP = "command bootstrap";
   protected static final String REASON_CUSTOM_WRAPPER = "custom wrapper";
   public final List<ModuleEntry> registeredModuleEntry;
   protected boolean removed;
   private ModuleManager moduleManager;
   protected boolean active = false;
   private final List<WrapperConfigRef<?>> editableConfigRefs;
   protected static final int METADATA_WIDTH = 140;
   protected static final int METADATA_MARGIN = 10;
   protected static final String REASON_CONFIG_VALIDATOR = "config validator";
   protected static final String REASON_CONFIG_UPDATE_LISTENER = "config update listener";
   private FlagRef bindFlag;
   protected String name;
   protected static StringFormat moduleMessageFormat = new StringFormat(List.of("module_name", "message"), "&c[{module_name}] &f{message}", true);
   private final Set<SimpleHotKey> registeredHotkeys;
   protected static final String REASON_EVENT_LISTENER = "event listener";
   protected static final MinecraftClient mc = MinecraftClient.getInstance();
   protected static Map<ModulePath, ModulePath> PORT_CONFIG_MAPS = new ConcurrentHashMap<>();
   private final Set<PacketCatcher<?>> registeredPacketListeners;
   private final ReferenceSet<Object> registeredReasons;
   private final List<WrapperConfigRef<?>> registeredConfigRefs;
   private Consumer<Boolean> bindFlagListener;

   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   private void removeBindFlag() {
      if (this.bindFlag != null) {
         this.bindFlag.removeUpdateListener(s -> this.bindFlagListener == s);
         this.bindFlag = null;
      }
   }

   @MustBeInvokedByOverriders
   public void onDisableModule() {
   }

   public void log(Text text) {
      Debug.b(moduleMessageFormat.formatText(this.getName(), text));
   }

   public static Supplier<Text> moduleMeta(Supplier<EnumRef<?>> enumReff) {
      return new EventContainer(enumReff);
   }

   public void logSub(String subModule, Text text) {
      Debug.b(moduleMessageFormat.formatText(subModule, text));
   }

   public boolean hasEditableConfig() {
      return !this.editableConfigRefs.isEmpty();
   }

   @Override
   public Stream<ModuleEntry> getModuleEntries() {
      return this.registeredModuleEntry.stream();
   }

   public void registerAsSubCommand(String name, Supplier<AbstractMainCommand> factory) {
      this.registerCommandBootstrap(s -> s.aL(name, (AbstractMainCommand)factory.get()));
   }

   public boolean shouldShowInGui() {
      if (this.shouldShowInGuiCache == null) {
         if (this.hasEditableConfig()) {
            this.shouldShowInGuiCache = true;
            return true;
         }

         Class var1 = this.getClass();

         try {
            Method var2 = var1.getMethod("addCustomWidgets", Consumer.class, int.class, int.class, int.class);
            if (var2.getDeclaringClass() != BaseModule.class) {
               this.shouldShowInGuiCache = true;
               return true;
            }
         } catch (Throwable var3) {
         }

         this.shouldShowInGuiCache = false;
      }

      return this.shouldShowInGuiCache;
   }

   public <W> void unregisterAll() {
      this.registeredPacketListeners.forEach(s -> s.q(this::isOwner));
      this.registeredPacketListeners.clear();
      this.registeredConfigRefs.forEach(s -> s.ref.removeUpdateListener(this::isOwner));
      this.registeredConfigRefs.forEach(s -> s.ref.removeValidator(this::isOwner));
      this.registeredConfigRefs.forEach(s -> {
         if (s.ref instanceof ListRef var3) {
            var3.removeElementValidator(this::isOwner);
         }
      });
      this.registeredConfigRefs.clear();
      this.editableConfigRefs.clear();
      this.registeredHotkeys.forEach(s -> s.abS(SimpleHotKey$InputHandler.EMPTY));
      this.registeredHotkeys.clear();
      MainCommand.unregisterCommandBootstrap(this::isOwner);
      this.registeredReasons.clear();
   }

   public WrapperSettingBuilder<MultiKeyBind> toggleConfigHotkey(Config config, String[] path, MultiKeyBind defaultValue) {
      return this.builder(config, MultiKeyBind.class).path(path).defaultValue(defaultValue).registerHotkey(HotKeyUtils.i(Configs.s, path));
   }

   public DrawableWidget createTitleLabel(String translationKey, int x, int y, int dx, int dy) {
      return DisplayWidget.instance(x, y, dx, dy)
         .setRenderHandler(
            new ColorLabelTextElement(
                  TextProvider.c(Text.translatable(translationKey)),
                  () -> ClickGui.INSTANCE.guiTextStyle.get().withAlpha(255),
                  () -> ClickGui.INSTANCE.guiFrameStyle.get().withAlpha(255)
               )
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation(translationKey + ".tooltips", "")))
         );
   }

   public String getName() {
      return this.name;
   }

   public <T> WrapperSettingBuilder<T> builder(Config config, String[] path, Class<T> type) {
      return new WrapperSettingBuilder<T>(config.asRef(), config, type, this).path(path);
   }

   public WrapperSettingBuilder<Boolean> flagBuilder(ModulePath path) {
      return this.flagBuilder(path.getConfig(), path.toPath());
   }

   public DrawableWidget createRefKeyLabel(Supplier<Text> text, Supplier<List<Text>> tooltips, int dx, int dy) {
      return ExecutableWidget.instance(0, 0, dx, dy)
         .eV(
            new ColorLabelTextElement(
                  el -> (Text)text.get(),
                  () -> ClickGui.INSTANCE.guiTextStyle.get().withAlpha(255),
                  () -> ClickGui.INSTANCE.guiConfigStyle.get().withAlpha(255)
               )
               .aO(TooltipHandler.ar(tooltips))
         );
   }

   public void logI18NSub(String subModule, String translationKey, Object... objects) {
      this.logSub(subModule, Text.translatable(translationKey, objects));
   }

   public final void bindFlag(FlagRef flagRef) {
      if (this.bindFlag != null) {
         this.removeBindFlag();
      }

      this.bindFlag = flagRef;
      if (flagRef != null) {
         this.bindFlagListener = this.registerReason(this::updateActiveStatus, "module binding");
         flagRef.addUpdateListenerWithUpdate(this.bindFlagListener);
      }
   }

   public DrawableWidget createRefEditor(String path, Ref<?> ref, int x, int y, int dx, int dy) {
      return new BaseModuleConfigWidget(this, x, y, dx, dy, 140, 10, dx - 140 - 10, ref, path);
   }

   public void registerHotkey(SimpleHotKey register) {
      this.registeredHotkeys.add(register);
   }

   public IHotKey getHotkey(String... path) {
      return SimpleInputManager.h().getHotkey(String.join(".", path));
   }

   public WrapperSettingBuilder<MultiKeyBind> hotkey(ModulePath path) {
      return this.hotkey(path.getConfig(), path.toPath());
   }

   public <W> void registerListener(PacketCatcher<W> listener, Predicate<W> handler, int p) {
      listener.m(this.registerReason(handler, "event listener"), p);
      this.registeredPacketListeners.add(listener);
   }

   public void registerCommandBootstrap(Consumer<MainCommand> handler) {
      KalamaHelperHelperB var2 = this.registerReason(handler::accept, "command bootstrap");
      MainCommand.registerCommandBootstrap(var2);
   }

   public boolean isActive() {
      return this.active;
   }

   @MustBeInvokedByOverriders
   public void registerAll() {
   }

   public static void portConfigs(ModulePath oldPath, ModulePath newPath) {
      PORT_CONFIG_MAPS.put(oldPath, newPath);
      Ref var2 = oldPath.getConfig().get(oldPath.toPath());
      if (var2 != null) {
         oldPath.getConfig().setValueNoNew(null, oldPath.toPath());
         newPath.getConfig().setValueNoNew(var2, newPath.toPath());
      }
   }

   public <T> T cast() {
      return (T)(Object)this;
   }

   public <W> void registerListener(PacketCatcher<W> listener, Consumer<W> handler) {
      this.registerListener(listener, handler, 0);
   }

   public WrapperSettingBuilder<MultiKeyBind> moduleEntry(ModulePath path, MultiKeyBind defaultValue, ModulePath togglePath) {
      return this.moduleEntry(path.getConfig(), path.toPath(), defaultValue, togglePath.toPath());
   }

   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
   }

   public BaseModule(String name) {
      this.removed = false;
      this.bindFlag = null;
      this.registeredReasons = new ReferenceOpenHashSet();
      this.registeredPacketListeners = new LinkedHashSet<>();
      this.registeredModuleEntry = new ArrayList<>();
      this.registeredConfigRefs = new ArrayList<>();
      this.editableConfigRefs = new ArrayList<>();
      this.registeredHotkeys = new LinkedHashSet<>();
      this.name = name;
      this.ensureInstanceSet();
   }

   public WrapperSettingBuilder<Integer> intBuilder(ModulePath path) {
      return this.builder(path.getConfig(), path.toPath(), IntRef.TYPE);
   }

   public WrapperSettingBuilder<MultiKeyBind> hotkey(Config config, String... path) {
      return this.builder(config, MultiKeyBind.class).path(path);
   }

   public WrapperSettingBuilder<Boolean> flagBuilder(Config config, String... path) {
      return this.builder(config, Boolean.class).path(path).defaultValue(false);
   }

   protected <W> boolean isOwner(Object c) {
      return this.registeredReasons.contains(c);
   }

   public static boolean checkNull() {
      return mc.player == null || mc.world == null;
   }

   @MustBeInvokedByOverriders
   public void onCreate() {
      this.registerAll();
   }

   protected <W> Predicate<W> eV(Predicate<W> predicate) {
      return this.registerReason(predicate, "custom wrapper");
   }

   @MustBeInvokedByOverriders
   public void onEnableModule() {
   }

   public WrapperSettingBuilder<MultiKeyBind> moduleEntry(ModulePath hotkeyPath, MultiKeyBind defaultValue, ModulePath togglePath, Supplier<Text> descriptor) {
      return this.moduleEntry(hotkeyPath.getConfig(), hotkeyPath.toPath(), defaultValue, togglePath.toPath(), descriptor);
   }

   public <W> void registerListener(PacketCatcher<W> listener, Predicate<W> handler) {
      this.registerListener(listener, handler, 0);
   }

   public WrapperSettingBuilder<MultiKeyBind> hotkey(ModulePath path, MultiKeyBind defaultValue) {
      return this.hotkey(path.getConfig(), path.toPath(), defaultValue);
   }

   public <T> void registerConfigWrapper(WrapperConfigRef<T> ref) {
      this.registeredConfigRefs.removeIf(ref::f);
      this.registeredConfigRefs.add(ref);
      if (ref.isEditable()) {
         this.editableConfigRefs.removeIf(ref::f);
         this.editableConfigRefs.add(ref);
      }
   }

   public static ModulePath makePath(Config config, String c) {
      return new ModulePath(config, c.split("\\."));
   }

   public WrapperSettingBuilder<MultiKeyBind> toggleHotkey(Config config, String[] hotkeyPath, MultiKeyBind defaultValue, String[] togglePath) {
      return new WrapperModuleSettingBuilder(config.asRef(), config, this, new ModuleEntry(config, togglePath, hotkeyPath))
         .defaultValue(defaultValue)
         .registerHotkey(HotKeyUtils.i(config, togglePath))
         .registerModuleEntry();
   }

   public DrawableWidget createExecuteButton(String translationKey, ButtonAction action, int x, int y, int dx, int dy) {
      return ExecutableWidget.instance(x, y, dx, dy)
         .eV(
            new ButtonElement(TextProvider.c(Text.translatable(translationKey)), action)
               .aO(TooltipHandler.ap(ChatUtils.parseTranslation(translationKey + ".tooltips", "")))
         );
   }

   public final List<WrapperConfigRef<?>> getEditableConfig() {
      return Collections.unmodifiableList(this.editableConfigRefs);
   }

   public <W> void registerListener(PacketCatcher<W> listener, Consumer<W> handler, int p) {
      listener.l(this.registerReason(handler, "event listener"), p);
      this.registeredPacketListeners.add(listener);
   }

   public <T> WrapperSettingBuilder<T> builder(Config config, Class<T> type) {
      return new WrapperSettingBuilder<>(config.asRef(), config, type, this);
   }

   public WrapperSettingBuilder<Double> doubleBuilder(ModulePath path) {
      return this.builder(path.getConfig(), path.toPath(), DoubleRef.TYPE);
   }

   @MustBeInvokedByOverriders
   public final void unregister(ModuleManager manager) {
      manager.unregisterModule(this);
   }

   public <T> WrapperSettingBuilder<T> builder(ModulePath path, Class<T> type) {
      return this.builder(path.getConfig(), path.toPath(), type);
   }

   protected <W> Consumer<W> wrap(Consumer<W> consumer) {
      return this.registerReason(consumer, "custom wrapper");
   }

   public boolean isRemoved() {
      return this.removed;
   }

   private void ensureInstanceSet() {
      try {
         Field var1 = this.getClass().getField("INSTANCE");
         if (Modifier.isStatic(var1.getModifiers()) && !Modifier.isFinal(var1.getModifiers()) && var1.getType() == this.getClass()) {
            var1.setAccessible(true);
            var1.set(null, this);
         }
      } catch (Throwable var2) {
      }
   }

   public void registerCommand(Supplier<AbstractMainCommand> factory) {
      this.registerCommandBootstrap(s -> s.aM((AbstractMainCommand)factory.get()));
   }

   public static String[] makePath(String c) {
      return c.split("\\.");
   }

   public WrapperSettingBuilder<MultiKeyBind> toggleHotkey(ModulePath hotkeyPath, MultiKeyBind defaultValue, ModulePath togglePath) {
      return this.toggleHotkey(hotkeyPath.getConfig(), hotkeyPath.toPath(), defaultValue, togglePath.toPath());
   }

   protected final void updateActiveStatus(boolean active) {
      if (this.active != active) {
         this.active = active;
         if (active) {
            this.onEnableModule();
         } else {
            this.onDisableModule();
         }
      }
   }

   public void logI18N(String translationKey, Object... objects) {
      this.log(Text.translatable(translationKey, objects));
   }

   public WrapperSettingBuilder<MultiKeyBind> moduleEntry(Config config, String[] path, MultiKeyBind defaultValue, String[] togglePath) {
      return new WrapperSettingBuilder<>(config.asRef(), config, KeyBindRef.TYPE, this)
         .path(path)
         .defaultValue(defaultValue)
         .registerHotkey(HotKeyUtils.i(config, togglePath));
   }

   private void clearModuleEntries() {
      this.registeredModuleEntry.clear();
   }

   public WrapperSettingBuilder<MultiKeyBind> hotkey(Config config, String[] path, MultiKeyBind defaultValue) {
      return this.builder(config, MultiKeyBind.class).path(path).defaultValue(defaultValue);
   }

   public final boolean hasBindFlag() {
      return this.bindFlag != null;
   }

   @MustBeInvokedByOverriders
   public void onRemove() {
      if (this.removed) {
         throw new IllegalStateException("Removed twice");
      } else {
         this.removeBindFlag();
         this.clearModuleEntries();
         this.unregisterAll();
         this.moduleManager = null;
         this.removed = true;
      }
   }

   protected <T> T registerReason(T value, String reason) {
      this.registeredReasons.add(value);
      return (T)value;
   }

   @Nullable
   public final FlagRef getBindFlag() {
      return this.bindFlag;
   }

   @MustBeInvokedByOverriders
   public final <T extends BaseModule> T register(ModuleManager manager) {
      manager.registerModule(this);
      this.moduleManager = manager;
      return (T)(Object)this;
   }

   public void logSub(String subModule, String string) {
      Debug.b(moduleMessageFormat.formatText(subModule, string));
   }

   public WrapperSettingBuilder<MultiKeyBind> moduleEntry(
      Config config, String[] hotkeyPath, MultiKeyBind defaultValue, String[] togglePath, Supplier<Text> descriptor
   ) {
      return new WrapperModuleSettingBuilder(config.asRef(), config, this, new KalamaHelperHelperI(config, togglePath, hotkeyPath, descriptor))
         .defaultValue(defaultValue)
         .registerHotkey(HotKeyUtils.i(config, togglePath))
         .registerModuleEntry();
   }

   public static Text getModuleMeta(Enum<?> enumReff) {
      ConfigEnum var1 = (ConfigEnum)enumReff;
      return Text.translatable("module-meta." + var1.getConfigEnumType().replace("_", "-") + "." + enumReff.name().toLowerCase(Locale.ROOT));
   }

   public void log(String string) {
      Debug.b(moduleMessageFormat.formatText(this.getName(), string));
   }
}
