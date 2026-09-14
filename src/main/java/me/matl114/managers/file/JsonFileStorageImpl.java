package me.matl114.managers.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import me.matl114.utils.Debug;
import me.matl114.utils.FileUtils;

public class JsonFileStorageImpl extends FileStorageImpl {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private JsonElement data;

    @Override
    public void i() {
        this.data = new JsonObject();
        this.file.delete();
        this.f = true;
    }

    public void write() {
        this.ensureParentDir();
        File var1 = new File(this.file.getParent(), this.file.getName() + ".tmp");
        if (var1.exists()) {
            var1.delete();
        }

        try (FileWriter var2 = new FileWriter(var1, StandardCharsets.UTF_8)) {
            GSON.toJson(this.data, var2);
        } catch (IOException var8) {
            throw new RuntimeException("Failed to save " + this.file, var8);
        }

        try {
            FileUtils.b(var1, this.file);
        } catch (IOException var6) {
            throw new RuntimeException("Failed to save " + this.file, var6);
        }

        this.g = false;
    }

    public JsonFileStorageImpl(File file) {
        super(file);
        this.h();
    }

    @Override
    public <W> DataResult<W> e(Codec<W> codec) {
        return codec.parse(JsonOps.INSTANCE, this.data);
    }

    @Override
    public <W> DataResult<?> f(Codec<W> codec, W value) {
        DataResult<JsonElement> var3 = codec.encodeStart(JsonOps.INSTANCE, value);
        var3.result().ifPresent(result -> this.write(result, JsonOps.INSTANCE));
        return var3;
    }

    @Override
    public <T> void write(T value, DynamicOps<T> ops) {
        this.data = (JsonElement) ops.convertTo(JsonOps.INSTANCE, value);
        this.g = true;
    }

    @Override
    public <T, W extends T> W c(DynamicOps<T> ops) {
        return (W) JsonOps.INSTANCE.convertTo(ops, this.data);
    }

    @Override
    public void h() {
        if (!this.file.exists()) {
            Debug.e("Creating new JsonStorage file at", this.file);
            this.data = new JsonObject();
            this.write();
        } else {
            try (FileReader var1 = new FileReader(this.file)) {
                this.data = (JsonElement) GSON.fromJson(var1, JsonElement.class);
            } catch (IOException var6) {
                throw new RuntimeException(var6);
            }

            this.g = false;
        }
    }

    @Override
    public <T, W extends T> W b(DynamicOps<T> ops) {
        return (W) (ops == JsonOps.INSTANCE ? this.data : JsonOps.INSTANCE.convertTo(ops, this.data));
    }

    @Override
    public void g() {}
}
