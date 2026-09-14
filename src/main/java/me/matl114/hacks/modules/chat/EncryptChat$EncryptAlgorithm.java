package me.matl114.hacks.modules.chat;

import java.util.Locale;
import me.matl114.managers.config.ConfigEnum;
import net.minecraft.text.Text;

public enum EncryptChat$EncryptAlgorithm implements ConfigEnum {
    NONE(message -> EncryptChat$Encryptor.EMPTY),
    AES_CFB8(EncryptChat.kO),
    AES_GCM(EncryptChat.kP),
    AES_ECB(EncryptChat.kQ);

    private final EncryptChat$Encryption encryption;

    private EncryptChat$EncryptAlgorithm(EncryptChat$Encryption encryption) {
        this.encryption = encryption;
    }

    @Override
    public String getConfigEnumType() {
        return "encryptalgorithm";
    }

    @Override
    public Text resultAsString() {
        return Text.translatable("configenum.encryptalgorithm." + this.name().toLowerCase(Locale.ROOT));
    }

    public EncryptChat$Encryption getEncryption() {
        return this.encryption;
    }
}
