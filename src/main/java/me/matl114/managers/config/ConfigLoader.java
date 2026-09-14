package me.matl114.managers.config;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import me.matl114.SlimefunHelper;
import me.matl114.utils.Debug;
import net.fabricmc.loader.api.FabricLoader;
import org.yaml.snakeyaml.Yaml;

public class ConfigLoader {
    public static Config SERVER_CONFIG;

    public static void copyFile(File file, String internalFileName) {
        if (!file.exists()) {
            try {
                if (!file.getParentFile().exists()) {
                    Files.createDirectories(file.toPath().getParent());
                }

                Files.copy(
                        SlimefunHelper.getInstance().getClass().getResourceAsStream("/" + internalFileName),
                        file.toPath());
            } catch (Throwable var5) {
                Debug.a("创建配置文件时找不到相关默认配置文件,即将生成空文件");

                try {
                    Files.createDirectories(file.toPath().getParent());
                    Files.createFile(file.toPath());
                } catch (IOException var4) {
                    Debug.a("创建空配置文件失败!");
                }
            }
        }
    }

    public static Config loadInternalConfig(String internalFileName, String customName) {
        try {
            return new Config(
                    customName,
                    null,
                    loadYamlConfig(new InputStreamReader(
                            SlimefunHelper.getInstance().getClass().getResourceAsStream("/" + internalFileName),
                            Charsets.UTF_8)));
        } catch (Throwable var3) {
            Debug.a("failed to load internal config " + internalFileName + ".yml, Error: " + var3.getMessage());
            return null;
        }
    }

    public static Config loadExternalConfig(String name, String customName) {
        File cfgFile = FabricLoader.getInstance().getConfigDir().resolve(name).toFile();
        String fileName = cfgFile.getName();
        copyFile(cfgFile, fileName);
        return new Config(customName, cfgFile);
    }

    public static String loadExternalJson(String name) {
        File cfg = FabricLoader.getInstance().getConfigDir().resolve(name).toFile();
        if (!cfg.exists()) {
            try {
                if (!cfg.getParentFile().exists()) {
                    Files.createDirectories(cfg.toPath().getParent());
                }

                Files.createFile(cfg.toPath());
                Files.writeString(cfg.toPath(), "{}");
            } catch (Throwable var4) {
                Debug.e("创建新json文件失败: 文件:", cfg, "错误:");
                Debug.f(var4);
                return "{}";
            }
        }

        try {
            return Files.readString(cfg.toPath(), StandardCharsets.UTF_8);
        } catch (Throwable var3) {
            Debug.e("读取json文件失败: 文件:", cfg, "错误:");
            Debug.f(var3);
            return "{}";
        }
    }

    public static void saveToFile(String name, String data) throws IOException {
        File cfg = FabricLoader.getInstance().getConfigDir().resolve(name).toFile();
        if (!cfg.exists()) {
            if (!cfg.getParentFile().exists()) {
                Files.createDirectories(cfg.toPath().getParent());
            }

            Files.createFile(cfg.toPath());
        }

        Files.writeString(cfg.toPath(), data);
    }

    public static HashMap<String, Object> loadYamlConfig(File file) {
        try {
            HashMap var2;
            try (InputStreamReader fileInput =
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                var2 = loadYamlConfig(fileInput);
            }

            return var2;
        } catch (Throwable var6) {
            Debug.a("failed to load yaml config " + file.getName() + ".yml, Error: " + var6.getMessage());
            throw new RuntimeException(var6);
        }
    }

    public static HashMap<String, Object> loadYamlConfig(Reader reader) {
        Yaml yaml = new Yaml();
        HashMap obj = yaml.load(reader);
        return (HashMap<String, Object>) (obj == null ? new LinkedHashMap<>() : obj);
    }
}
