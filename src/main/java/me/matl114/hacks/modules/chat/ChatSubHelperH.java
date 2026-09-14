package me.matl114.hacks.modules.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.matl114.utils.CodecUtils;

public final class ChatSubHelperH {
    private EncryptChat$EncryptAlgorithm d;
    private String e;
    public static final Codec<ChatSubHelperH> b = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(ChatSubHelperH::c),
                    CodecUtils.enumCodec(EncryptChat$EncryptAlgorithm.class)
                            .fieldOf("algorithm")
                            .forGetter(ChatSubHelperH::e),
                    Codec.STRING.fieldOf("phase").forGetter(ChatSubHelperH::g),
                    Codec.STRING.fieldOf("key").forGetter(ChatSubHelperH::i))
            .apply(instance, ChatSubHelperH::new));
    public static final ChatSubHelperH a = new ChatSubHelperH("", EncryptChat$EncryptAlgorithm.NONE, "", "");
    private String c;
    private String f;

    public String c() {
        return this.c;
    }

    public void h(String phase) {
        this.e = phase == null ? "" : phase;
        this.refreshDerivedKey();
    }

    public void f(EncryptChat$EncryptAlgorithm algorithm) {
        this.d = algorithm == null ? EncryptChat$EncryptAlgorithm.NONE : algorithm;
        this.refreshDerivedKey();
    }

    public void j(String key) {
        if (this.e == null || this.e.isEmpty()) {
            this.f = key == null ? "" : key;
        }
    }

    public ChatSubHelperH b() {
        return new ChatSubHelperH(this.c, this.d, this.e, this.f);
    }

    public String i() {
        return this.f;
    }

    public ChatSubHelperH(String name, EncryptChat$EncryptAlgorithm algorithm, String phase, String key) {
        this.c = name;
        this.d = algorithm;
        this.e = phase;
        this.f = key;
        this.refreshDerivedKey();
    }

    public String g() {
        return this.e;
    }

    private void refreshDerivedKey() {
        if (this.e == null) {
            this.e = "";
        }

        if (!this.e.isEmpty() && this.d != null && this.d != EncryptChat$EncryptAlgorithm.NONE) {
            this.f = this.d.getEncryption().d(this.e);
        } else {
            if (this.f == null) {
                this.f = "";
            }
        }
    }

    public void d(String name) {
        this.c = name;
    }

    public EncryptChat$EncryptAlgorithm e() {
        return this.d;
    }

    public static ChatSubHelperH a() {
        return new ChatSubHelperH("", EncryptChat$EncryptAlgorithm.NONE, "", "");
    }
}
