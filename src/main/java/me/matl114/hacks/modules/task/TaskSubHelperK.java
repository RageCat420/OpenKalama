package me.matl114.hacks.modules.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import me.matl114.utils.CodecUtils;

public record TaskSubHelperK(
        String name, ConnectionProxy$Type type, String address, int port, String userName, String password) {
    public static final TaskSubHelperK EMPTY = new TaskSubHelperK("", ConnectionProxy$Type.SOCKS, "", 0, "", "");
    public static final List<String> ds = List.of("name", "type", "address", "port", "userName", "password");
    public static Codec<TaskSubHelperK> dt = RecordCodecBuilder.create(oinstance -> oinstance
            .group(
                    Codec.STRING.fieldOf("name").forGetter(TaskSubHelperK::name),
                    CodecUtils.enumCodec(ConnectionProxy$Type.class)
                            .fieldOf("type")
                            .forGetter(TaskSubHelperK::type),
                    Codec.STRING.fieldOf("address").forGetter(TaskSubHelperK::address),
                    Codec.INT.fieldOf("port").forGetter(TaskSubHelperK::port),
                    Codec.STRING.fieldOf("userName").forGetter(TaskSubHelperK::userName),
                    Codec.STRING.fieldOf("password").forGetter(TaskSubHelperK::password))
            .apply(oinstance, TaskSubHelperK::new));
}
