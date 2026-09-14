package me.matl114.bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Map;
import org.bukkit.util.io.Wrapper;

public class KalamaHelperHelperB extends ObjectInputStream {
    private static Map<String, Class> CLASS_MAPPER = Map.of("org.bukkit.util.io.Wrapper", Wrapper.class);

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String var2 = desc.getName();
        Class var3 = CLASS_MAPPER.get(var2);
        return var3 != null ? var3 : super.resolveClass(desc);
    }

    private static IOException newIOException(String string, Throwable cause) {
        IOException var2 = new IOException(string);
        var2.initCause(cause);
        return var2;
    }

    public KalamaHelperHelperB(InputStream in) throws IOException {
        super(in);
        super.enableResolveObject(true);
    }

    protected KalamaHelperHelperB() throws IOException, SecurityException {
        super.enableResolveObject(true);
    }

    @Override
    protected Object resolveObject(Object obj) throws IOException {
        if (obj instanceof Wrapper) {
            try {
                (obj = BukkitSerializationMock.h(((Wrapper) obj).map)).getClass();
            } catch (Throwable var3) {
                throw newIOException("Failed to deserialize object", var3);
            }
        }

        return super.resolveObject(obj);
    }
}
