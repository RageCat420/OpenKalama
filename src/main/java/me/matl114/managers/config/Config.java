package me.matl114.managers.config;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Lifecycle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.managers.KalamaHelperHelperG;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.managers.input.SimpleHotKey;
import me.matl114.managers.input.SimpleHotKey$InputHandler;
import me.matl114.managers.input.SimpleInputManager;
import me.matl114.utils.FileUtils;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.Identifier;
import org.lwjgl.system.NonnullDefault;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class Config implements RefMap {
    private final File file;
    private static final Logger logger = Logger.getLogger("kalama");
    protected Map<String, Object> fileMap;
    protected MapRef ref;
    protected LinkedHashSet<String> buildOrder = new LinkedHashSet<>();
    private static final Set<Config> configs = new LinkedHashSet<>();
    public static final SimpleRegistry<Config> REGISTRY =
            new SimpleRegistry(RegistryKey.ofRegistry(Identifier.of("kalama", "configs")), Lifecycle.stable());
    private static final Set<Config> allConfigInternal = new LinkedHashSet<>();
    RegistryKey<Config> registryKey;
    private static final long SAVE_TIME = 15000L;
    private String configName;
    private final boolean autoSave = true;
    private boolean markForSave = false;

    public void registerGlobal() {
        configs.add(this);
        if (this.registryKey == null) {
            RegistryKey<Config> registryKey = RegistryKey.of(
                    REGISTRY.getKey(),
                    Identifier.of(
                            "kalama", this.configName.toLowerCase(Locale.ROOT).replace(" ", "_")));
            this.registryKey = registryKey;
            REGISTRY.add(this.registryKey, this, RegistryEntryInfo.DEFAULT);
        }
    }

    public String getTranslationKey() {
        return "config.index." + this.registryKey.getValue().getPath();
    }

    public static void reloadAll() {
        allConfigInternal.forEach(v -> {
            if (v.markForSave) {
                v.save(v.file);
            } else {
                v.reload();
            }
        });
    }

    public static void launchSaveTasks() {
        KalamaHelperHelperG.c(Config::configSaveTasks, 1000L);
    }

    public static void configSaveTasks() {
        for (Config config : allConfigInternal) {
            if (config.file != null && config.markForSave) {
                config.save(config.file);
            }
        }
    }

    public Config(String name, @Nonnull File file, @Nonnull Map<String, Object> fileConfig) {
        this.configName = name;
        this.file = file;
        this.fileMap = new LinkedHashMap<>(fileConfig);
        this.ref = Refs.transferConfig(fileConfig);
        this.ref.setConfigReference(this);
        allConfigInternal.add(this);
    }

    public Config(String name, @Nonnull File file) {
        this(name, file, ConfigLoader.loadYamlConfig(file));
    }

    @Nonnull
    public File getFile() {
        return this.file;
    }

    public void clear() {
        for (String key : this.getKeys()) {
            this.setValue(null, key);
        }
    }

    private boolean setValue(Object value, @Nonnull String... path) {
        Ref refo = Refs.wrapInstance(value);
        boolean update = this.setValue(refo, path);
        if (update) {
            this.markForSave();
        }

        return update;
    }

    public void setValueNoNew(Object value, @Nonnull String... path) {
        this.setValue(value, path);
    }

    @Override
    public Ref<?> get(@Nonnull String... path) {
        Ref<?> re = this.ref.get(path);
        if (re != null) {
            re.setConfigReference(this);
        }

        return re;
    }

    public <T> Config validator(Predicate<T> validator, String... path) {
        Ref<T> ref = (Ref<T>) (Object) this.get(path);
        ref.addValidator(validator);
        return this;
    }

    @NonnullDefault
    private Ref getOrCreate(Ref defaultValue, @Nonnull String... path) {
        Ref result = this.ref.getOrCreate(defaultValue, path);
        if (result != null) {
            result.setConfigReference(this);
        }

        if (result == defaultValue) {
            this.markForSave();
            return defaultValue;
        } else if (result == null) {
            throw new IllegalArgumentException(
                    "create fail ref validation: " + Arrays.stream(path).toList());
        } else {
            return result;
        }
    }

    public Config markForSave() {
        this.markForSave = true;
        return this;
    }

    public void save(@Nonnull File file) {
        File absoluteFile = file.getAbsoluteFile();
        File parentDir = absoluteFile.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs() && !parentDir.exists()) {
            logger.log(
                    Level.SEVERE,
                    "Exception while saving a Config file: failed to create parent directories for {0}",
                    absoluteFile);
        } else {
            Map savedData = (Map) (Object) this.ref.getAsPrimitive();
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(options);
            File tempFile = new File(absoluteFile.getPath() + ".tmp");
            boolean saved = false;

            try (FileOutputStream fout = new FileOutputStream(tempFile);
                    OutputStreamWriter owrite = new OutputStreamWriter(fout, StandardCharsets.UTF_8); ) {
                yaml.dump(savedData, owrite);
                owrite.flush();
                fout.getFD().sync();
            } catch (IOException var26) {
                logger.log(Level.SEVERE, "Exception while saving a Config file", (Throwable) var26);
            }

            if (tempFile.exists()) {
                try {
                    FileUtils.b(tempFile, file);
                    saved = true;
                } catch (IOException var21) {
                    logger.log(Level.SEVERE, "Exception while replacing a Config file", (Throwable) var21);
                } finally {
                    if (saved) {
                        this.markForSave = false;
                    }
                }
            }
        }
    }

    public boolean contains(@Nonnull String... path) {
        return this.get(path) != null;
    }

    @Nullable
    @Override
    public StringRef getString(@Nonnull String... path) {
        return this.get(path) instanceof StringRef ref ? ref : null;
    }

    @Override
    public KeyBindRef getKeyBind(String... path) {
        return this.get(path) instanceof KeyBindRef ref ? ref : null;
    }

    private boolean setValue(Ref<?> value, String... path) {
        return this.ref.setValue(value, path);
    }

    @Override
    public ListRef getList(String... path) {
        return this.get(path) instanceof ListRef ref ? ref : null;
    }

    @Override
    public <T extends ConfigEnum> EnumRef<T> getEnum(@Nonnull String... path) {
        return (EnumRef<T>) (this.get(path) instanceof EnumRef<?> enumRef ? enumRef : null);
    }

    @Override
    public IntRef getInt(@Nonnull String... path) {
        return this.get(path) instanceof IntRef ref ? ref : null;
    }

    @Override
    public DoubleRef getDouble(String... path) {
        return this.get(path) instanceof DoubleRef ref ? ref : null;
    }

    @Override
    public FlagRef getBoolean(@Nonnull String... path) {
        return this.get(path) instanceof FlagRef ref ? ref : null;
    }

    public ObjectRef getObject(@Nonnull String... path) {
        return this.get(path) instanceof ObjectRef ref ? ref : null;
    }

    public boolean createFile() {
        try {
            return this.file.createNewFile();
        } catch (IOException var3) {
            logger.log(Level.SEVERE, "Exception while creating a Config file", (Throwable) var3);
            return false;
        }
    }

    @Nonnull
    public Set<String> getKeys() {
        return this.ref.getKeys();
    }

    @Nonnull
    public Set<String> getKeys(@Nonnull String... path) {
        return this.ref.get(path) instanceof MapRef mapRef ? mapRef.getKeys() : Set.of();
    }

    public void reload() {
        if (this.file != null) {
            this.ref.copyValueFrom(Refs.transferConfig(ConfigLoader.loadYamlConfig(this.file)));
        }
    }

    public Set<String> getPaths() {
        return this.ref.getPaths();
    }

    public Set<String> getVisiblePaths() {
        return this.buildOrder;
    }

    public static String[] cutToPath(String rawPath) {
        return rawPath.split("\\.");
    }

    public static Set<String> getPaths(Map<String, Object> map, String parent) {
        Set<String> paths = new LinkedHashSet<>();

        for (Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map map2) {
                Set<String> p = getPaths(map2, entry.getKey());
                paths.addAll(p.stream().map(str -> parent + "." + str).collect(Collectors.toSet()));
            } else {
                paths.add(parent + "." + entry.getKey());
            }
        }

        return paths;
    }

    public <T> Config.SettingBuilder<T> builder(Class<T> clazz) {
        return new Config.SettingBuilder<>(this.asRef(), this, clazz);
    }

    public static void registerClassSupport(Class<?> clazz) {
        if (AutoRegisterType.class.isAssignableFrom(clazz) && !AutoRegisterType.registered.contains(clazz)) {
            try {
                if (NBTParsable.class.isAssignableFrom(clazz)) {
                    NBTParsable.onLoad(clazz);
                } else {
                    if (!ConfigEnum.class.isAssignableFrom(clazz)) {
                        throw new IllegalArgumentException("Unsupported AutoRegisterType: " + clazz.getName());
                    }

                    ConfigEnum.onLoad(clazz);
                }

                AutoRegisterType.registered.add((Class<? extends AutoRegisterType>) clazz);
            } catch (Throwable var2) {
                throw new RuntimeException("Failed to register AutoRegisterType " + clazz.getName(), var2);
            }
        }
    }

    public MapRef asRef() {
        return this.ref;
    }

    public static Set<Config> getConfigs() {
        return configs;
    }

    public RegistryKey<Config> getRegistryKey() {
        return this.registryKey;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigName() {
        return this.configName;
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(Config::configSaveTasks, "Config-Shutdown-Save"));
        KalamaHelperHelperG.b(Config::configSaveTasks, 15000L, 15000L);
    }

    public interface CustomSerializableConfig {}

    public static class SettingBuilder<T> {
        protected final RefMap root;
        protected final Config rootConfig;
        protected final Class<T> clazz;
        protected String[] path;
        protected Ref<T> ref;

        @Nullable
        protected Optional<T> defaultValue;

        Runnable postTask;

        public SettingBuilder(MapRef ref, Config rootConfig, Class<T> clazz) {
            this.root = ref;
            this.clazz = clazz;
            this.rootConfig = rootConfig;
            Config.registerClassSupport(clazz);
        }

        protected Ref<T> getRef() {
            Preconditions.checkNotNull(this.ref);
            return this.ref;
        }

        public Config.SettingBuilder<T> path(String... path) {
            this.path = path;
            this.rootConfig.buildOrder.add(String.join(".", path));
            return this;
        }

        public Config.SettingBuilder<T> defaultValue(T val) {
            this.defaultValue = Optional.ofNullable(val);
            if (this.ref != null) {
                Ref<?> instance = Refs.wrapInstance(val);
                if (!instance.isSameTypeWith(this.ref)) {
                    this.ref = null;
                }
            }

            if (this.ref == null) {
                Ref<?> instance = Refs.wrapInstance(val);
                this.ref = this.rootConfig.getOrCreate(instance, this.path);
                if (this.ref == instance) {
                    this.rootConfig.markForSave();
                }
            }

            this.ref.setDefaultValue(val);
            return this;
        }

        public Config.SettingBuilder<T> validator(Predicate<T> va) {
            this.addPost(() -> {
                Preconditions.checkArgument(
                        va.test(this.defaultValue.orElse(null)),
                        "config abstract value validation failure: {0}",
                        String.join(".", this.path));
                if (!va.test(this.getRef().getValue())) {
                    this.getRef().setValue(this.defaultValue.orElse(null));
                }

                this.getRef().addValidator(va);
            });
            return this;
        }

        public Config.SettingBuilder<T> updateListener(Consumer<T> va) {
            this.addPost(() -> this.getRef().addUpdateListenerWithUpdate(va));
            return this;
        }

        public Config.SettingBuilder<T> registerHotkey(SimpleHotKey$InputHandler handler) {
            if (this.ref instanceof KeyBindRef keyBindRef) {
                String pathHotkey = String.join(".", this.path);
                if (SimpleInputManager.h().getHotkey(pathHotkey) instanceof SimpleHotKey simple) {
                    simple.abS(handler);
                    ((Config.SettingBuilder<MultiKeyBind>) this).updateListener(simple::abL);
                    return this;
                } else {
                    MultiKeyBind defaultKeyBind = this.defaultValue == null
                            ? new MultiKeyBind("")
                            : (MultiKeyBind) (Object) this.defaultValue.orElse(null);
                    SimpleHotKey hotKey1 = new SimpleHotKey(this.path, defaultKeyBind);
                    hotKey1.abS(handler);
                    SimpleInputManager.h().registerHotKeys(hotKey1);
                    ((Config.SettingBuilder<MultiKeyBind>) this).updateListener(hotKey1::abL);
                    return this;
                }
            } else {
                throw new IllegalArgumentException("Not a hotkey");
            }
        }

        public <W extends Ref<T>> Config.SettingBuilder<T> apply(Consumer<W> va) {
            this.addPost(() -> va.accept((W) Objects.requireNonNull(this.getRef())));
            return this;
        }

        public <W extends Ref<T>> W build() {
            Objects.requireNonNull(this.defaultValue);
            W ref1 = (W) Objects.requireNonNull((W) (Object) this.getRef());
            ref1.setConfigReference(this.rootConfig);
            if (this.postTask != null) {
                this.postTask.run();
            }

            return ref1;
        }

        protected void addPost(Runnable runnable) {
            if (this.postTask == null) {
                this.postTask = runnable;
            } else {
                Runnable currentPost = this.postTask;
                this.postTask = () -> {
                    currentPost.run();
                    runnable.run();
                };
            }
        }
    }
}
