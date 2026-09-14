package me.matl114.jsApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import me.matl114.events.annotations.Modifiable;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.util.crash.CrashException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

public class FileHelper {
    private static final Yaml c = createDefaultYaml();
    private static final Gson a = createDefaultGsonBuilder().create();
    private static final Yaml d = ae();
    private static final Gson b = createCompactGsonBuilder().create();

    public static void i(Class<?> clazz, String resource, String to) throws IOException {
        File var3 = new File(to);
        ensureParentDir(var3);
        Files.copy(clazz.getResourceAsStream("/" + resource), var3.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @Modifiable
    public static <T, W> String dumpYamlAsMap(T object) {
        return c.dumpAsMap(object);
    }

    private static GsonBuilder createCompactGsonBuilder() {
        return new GsonBuilder().disableHtmlEscaping().serializeNulls();
    }

    @Modifiable
    public NbtElement S(String filepath, boolean compressed) throws IOException {
        return this.readNbtFile(new File(filepath), compressed);
    }

    @Modifiable
    public static void Y(File file, byte[] data, boolean append) {
        try {
            ensureParentDir(file);
            StandardOpenOption[] var3 = append
                    ? new StandardOpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                    : new StandardOpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
            Files.write(file.toPath(), data, var3);
        } catch (IOException var4) {
            throw new RuntimeException("Failed to write binary file: " + file, var4);
        }
    }

    @Modifiable
    public static void appendJson(File file, JsonElement json, boolean pretty) throws IOException {
        JsonElement var3 = file.exists() ? readResourceJson(file) : null;
        if (var3 == null || var3.isJsonNull()) {
            B(file, json, pretty);
        } else if (var3.isJsonArray() && json.isJsonArray()) {
            var3.getAsJsonArray().addAll(json.getAsJsonArray());
            B(file, var3, pretty);
        } else if (var3.isJsonObject() && json.isJsonObject()) {
            json.getAsJsonObject().entrySet().forEach(entry -> var3.getAsJsonObject()
                    .add((String) entry.getKey(), (JsonElement) entry.getValue()));
            B(file, var3, pretty);
        } else {
            B(file, json, pretty);
        }
    }

    @Modifiable
    public static JsonElement y(String str) {
        return readResourceJson(new File(str));
    }

    @Modifiable
    public static void x(File str, String string, boolean append) {
        try {
            Path var3 = str.toPath();
            if (!Files.exists(var3.getParent())) {
                Files.createDirectories(var3.getParent());
            }

            if (append) {
                Files.writeString(var3, string, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } else {
                Files.writeString(var3, string, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    @Modifiable
    public static JsonElement readResourceJson(File str) {
        try {
            JsonElement var3;
            try (InputStream var1 = p(str)) {
                JsonReader var2 = new JsonReader(new InputStreamReader(var1));
                var3 = JsonParser.parseReader(var2);
            }

            return var3;
        } catch (IOException var6) {
            throw new RuntimeException(var6);
        }
    }

    public static void h(String resource, String to) throws IOException {
        i(FileHelper.class, resource, to);
    }

    @Modifiable
    public static void writeJson(File file, JsonElement json, Gson gson) {
        try {
            ensureParentDir(file);

            try (FileOutputStream var3 = new FileOutputStream(file);
                    OutputStreamWriter var4 = new OutputStreamWriter(var3, StandardCharsets.UTF_8); ) {
                gson.toJson(json, var4);
            }
        } catch (IOException var11) {
            throw new RuntimeException(var11);
        }
    }

    private static Yaml createPrettyYaml() {
        DumperOptions var0 = new DumperOptions();
        var0.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        var0.setIndent(4);
        var0.setPrettyFlow(true);
        var0.setAllowUnicode(true);
        var0.setSplitLines(true);
        var0.setDefaultScalarStyle(DumperOptions.ScalarStyle.DOUBLE_QUOTED);
        var0.setWidth(80);
        var0.setLineBreak(DumperOptions.LineBreak.UNIX);
        LoaderOptions var1 = new LoaderOptions();
        var1.setAllowDuplicateKeys(false);
        var1.setMaxAliasesForCollections(50);
        var1.setAllowRecursiveKeys(false);
        Representer var2 = new Representer(var0);
        var2.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        var2.setDefaultScalarStyle(DumperOptions.ScalarStyle.DOUBLE_QUOTED);
        return new Yaml(new KalamaHelperHelperB(var1), var2, var0, var1);
    }

    @Modifiable
    public static File d(String path) throws IOException {
        File var1 = new File(path);
        return e(var1);
    }

    @Modifiable
    public static boolean r(File file) {
        return file.exists() && file.isFile();
    }

    public static InputStream p(File file) {
        if (!r(file)) {
            throw new RuntimeException("File does not exists: " + file);
        } else {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException var2) {
                throw new RuntimeException(var2);
            }
        }
    }

    @Modifiable
    public static <T> String dumpYaml(T object) {
        return c.dump(object);
    }

    @Modifiable
    public static void A(File file, JsonElement json) {
        B(file, json, true);
    }

    private static Yaml createDefaultYaml() {
        DumperOptions var0 = new DumperOptions();
        var0.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        var0.setIndent(2);
        var0.setPrettyFlow(true);
        var0.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
        LoaderOptions var1 = new LoaderOptions();
        Representer var2 = new Representer(var0);
        var2.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(new KalamaHelperHelperB(var1), var2, var0, var1);
    }

    public static InputStream n(Class<?> clazz, String resource) {
        return clazz.getResourceAsStream("/" + resource);
    }

    @Modifiable
    public static void writeBinaryFile(File file, byte[] data) {
        Y(file, data, false);
    }

    public static <T> T K(String resource) {
        try {
            Object var2;
            try (InputStream var1 = m(resource)) {
                var2 = H(var1);
            }

            return (T) var2;
        } catch (IOException var6) {
            throw new RuntimeException(var6);
        }
    }

    public static <T> T O(String resource, Class<T> tClass) {
        try {
            Object var3;
            try (InputStream var2 = m(resource)) {
                var3 = readYaml(var2, tClass);
            }

            return (T) var3;
        } catch (IOException var7) {
            throw new RuntimeException(var7);
        }
    }

    @Modifiable
    public static void D(String path, JsonElement json, boolean pretty) {
        B(new File(path), json, pretty);
    }

    @Modifiable
    public static File a() {
        return FabricLoader.getInstance().getConfigDirectory();
    }

    @Modifiable
    public static String v(File str) {
        try {
            String var2;
            try (InputStream var1 = p(str)) {
                var2 = new String(var1.readAllBytes(), StandardCharsets.UTF_8);
            }

            return var2;
        } catch (IOException var6) {
            throw new RuntimeException(var6);
        }
    }

    @Modifiable
    public static String u(String path) {
        return v(new File(path));
    }

    @Modifiable
    public static void g(File from, String to) throws IOException {
        if (from.exists()) {
            File var2 = new File(to);
            ensureParentDir(var2);
            Files.copy(from.toPath(), var2.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            throw new IOException(from + " does not exist");
        }
    }

    @Modifiable
    public static File b() {
        return FabricLoader.getInstance().getGameDirectory();
    }

    @Modifiable
    public static File c(String path) {
        return new File(path);
    }

    @Modifiable
    public static void w(File str, String string) {
        x(str, string, false);
    }

    @Modifiable
    public static <T> T readYamlString(String yamlString) {
        return c.load(yamlString);
    }

    @Modifiable
    public static void ensureParentDir(File file) throws IOException {
        if (!file.getParentFile().exists()) {
            Files.createDirectories(file.getParentFile().toPath());
        }
    }

    public static InputStream m(String resource) {
        return FileHelper.class.getResourceAsStream("/" + resource);
    }

    @Modifiable
    public static void aa(String path, byte[] data, boolean append) {
        Y(new File(path), data, append);
    }

    @Modifiable
    public NbtElement readNbtFile(File file, boolean compressed) throws IOException {
        if (!file.exists()) {
            throw new IOException("NBT file does not exist: " + file);
        } else {
            try {
                NbtCompound var5;
                try (FileInputStream var3 = new FileInputStream(file)) {
                    if (compressed) {
                        return NbtIo.readCompressed(var3, NbtSizeTracker.ofUnlimitedBytes());
                    }

                    try (DataInputStream var4 = new DataInputStream(var3)) {
                        var5 = NbtIo.readCompound(var4, NbtSizeTracker.ofUnlimitedBytes());
                    }
                }

                return var5;
            } catch (CrashException var11) {
                throw new IOException("Failed to parse NBT file: " + file, var11.getCause());
            }
        }
    }

    @Modifiable
    public static <T> T readYaml(InputStream inputStream, Class<T> clazz) {
        return c.loadAs(inputStream, clazz);
    }

    private static Yaml ae() {
        DumperOptions var0 = new DumperOptions();
        LoaderOptions var1 = new LoaderOptions();
        return new Yaml(new KalamaHelperHelperB(var1), new Representer(var0), var0, var1);
    }

    public static String q(String resource) {
        try {
            String var2;
            try (InputStream var1 = m(resource)) {
                var2 = new String(var1.readAllBytes(), StandardCharsets.UTF_8);
            }

            return var2;
        } catch (IOException var6) {
            throw new RuntimeException(var6);
        }
    }

    public static void k(Class<?> clazz, String from, String toPath) throws IOException {
        ClassLoader var3 = clazz.getClassLoader();
        Object var4 = null;

        try {
            var4 = var3.getResource(from).toURI();
        } catch (URISyntaxException var14) {
            throw new IOException(var14.getMessage());
        } catch (NullPointerException var15) {
            throw new IOException(var15.getMessage());
        }

        if (var4 == null) {
            throw new IOException("something is wrong directory or files missing");
        } else {
            URL var5 = clazz.getProtectionDomain().getCodeSource().getLocation();
            Path var6 = Paths.get(
                    URLDecoder.decode(var5.toString(), StandardCharsets.UTF_8).substring("file:".length()));
            FileSystem var7 = FileSystems.newFileSystem(var6, Map.of());
            DirectoryStream<Path> var8 = Files.newDirectoryStream(var7.getPath(from));
            Path var9 = new File(toPath).toPath();

            for (Path var11 : var8) {
                InputStream var12 = clazz.getResourceAsStream("/" + var11.toString());
                Path var13 = var9.resolve(var11.toString());
                if (!Files.exists(var13)) {
                    if (!Files.exists(var13.getParent())) {
                        Files.createDirectories(var13.getParent());
                    }

                    Files.copy(var12, var13);
                }
            }
        }
    }

    @Modifiable
    public static <T> void writeYaml(File path, T object, boolean pretty) {
        try {
            ensureParentDir(path);

            try (FileOutputStream var3 = new FileOutputStream(path);
                    OutputStreamWriter var4 = new OutputStreamWriter(var3, StandardCharsets.UTF_8); ) {
                (pretty ? c : d).dump(object, var4);
            }
        } catch (IOException var11) {
            throw new RuntimeException(var11);
        }
    }

    @Modifiable
    public void writeNbtFile(File file, NbtCompound nbt, boolean compressed) throws IOException {
        ensureParentDir(file);

        try (FileOutputStream var4 = new FileOutputStream(file)) {
            if (compressed) {
                NbtIo.writeCompressed(nbt, var4);
            } else {
                try (DataOutputStream var5 = new DataOutputStream(var4)) {
                    NbtIo.writeCompound(nbt, var5);
                }
            }
        }
    }

    public static void j(String from, String toPath) throws IOException {
        k(FileHelper.class, from, toPath);
    }

    @Modifiable
    public static <T> T I(File file) {
        try {
            Object var2;
            try (InputStream var1 = p(file)) {
                var2 = H(var1);
            }

            return (T) var2;
        } catch (IOException var6) {
            throw new RuntimeException(var6);
        }
    }

    @Modifiable
    public static <T> T J(String filePath) {
        return I(new File(filePath));
    }

    private static Yaml createSafeYaml() {
        DumperOptions var0 = new DumperOptions();
        var0.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        var0.setIndent(2);
        var0.setAllowUnicode(true);
        var0.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        LoaderOptions var1 = new LoaderOptions();
        var1.setAllowDuplicateKeys(false);
        var1.setMaxAliasesForCollections(10);
        var1.setAllowRecursiveKeys(false);
        var1.setProcessComments(false);
        SafeConstructor var2 = new SafeConstructor(var1);
        Representer var3 = new Representer(var0);
        var3.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(var2, var3, var0, var1);
    }

    @Modifiable
    public static <T> T M(File file, Class<T> clazz) {
        try {
            Object var3;
            try (InputStream var2 = p(file)) {
                var3 = readYaml(var2, clazz);
            }

            return (T) var3;
        } catch (IOException var7) {
            throw new RuntimeException(var7);
        }
    }

    public static InputStream o(String path) {
        File var1 = new File(path);
        return p(var1);
    }

    @Modifiable
    public static void B(File file, JsonElement json, boolean pretty) {
        try {
            ensureParentDir(file);

            try (FileOutputStream var3 = new FileOutputStream(file);
                    OutputStreamWriter var4 = new OutputStreamWriter(var3, StandardCharsets.UTF_8); ) {
                Gson var5 = pretty ? a : b;
                var5.toJson(json, var4);
            }
        } catch (IOException var11) {
            throw new RuntimeException(var11);
        }
    }

    @Modifiable
    public static byte[] X(String path) {
        return W(new File(path));
    }

    @Modifiable
    public void U(String file, NbtCompound nbt, boolean compressed) throws IOException {
        this.writeNbtFile(new File(file), nbt, compressed);
    }

    private static GsonBuilder createDefaultGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls();
    }

    public static JsonElement t(String resource) {
        try {
            JsonElement var3;
            try (InputStream var1 = m(resource)) {
                JsonReader var2 = new JsonReader(new InputStreamReader(var1));
                var3 = JsonParser.parseReader(var2);
            }

            return var3;
        } catch (IOException var6) {
            throw new RuntimeException(var6);
        }
    }

    public static <T> T H(InputStream inputStream) {
        return c.load(inputStream);
    }

    @Modifiable
    public static boolean s(File file) {
        return file.exists() && file.isDirectory();
    }

    @Modifiable
    public static boolean l(File folder) {
        if (folder.isDirectory()) {
            File[] var1 = folder.listFiles();
            if (var1 != null) {
                for (File var5 : var1) {
                    if (!l(var5)) {
                        return false;
                    }
                }
            }
        }

        return folder.delete();
    }

    @Modifiable
    public static File e(File file) throws IOException {
        if (!file.getParentFile().exists()) {
            Files.createDirectories(file.getParentFile().toPath());
        }

        if (!file.exists()) {
            if (file.createNewFile()) {
                return file;
            } else {
                throw new IOException(file.toPath().toString() + " create failed");
            }
        } else {
            return file;
        }
    }

    @Modifiable
    public static <T> T N(String filePath, Class<T> clazz) {
        return M(new File(filePath), clazz);
    }

    @Modifiable
    public static void C(String path, JsonElement json) {
        B(new File(path), json, true);
    }

    @Modifiable
    public static byte[] W(File file) {
        if (!r(file)) {
            throw new RuntimeException("File does not exist: " + file);
        } else {
            try {
                return Files.readAllBytes(file.toPath());
            } catch (IOException var2) {
                throw new RuntimeException("Failed to read binary file: " + file, var2);
            }
        }
    }
}
