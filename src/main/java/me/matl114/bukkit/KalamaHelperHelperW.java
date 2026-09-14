package me.matl114.bukkit;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import me.matl114.events.annotations.Dispatch;
import org.bukkit.util.io.Wrapper;

public class KalamaHelperHelperW extends ObjectOutputStream {
    protected Object modifyInventoryCheck(Object obj) throws IOException {
        if (!(obj instanceof Serializable) && obj instanceof Dispatch) {
            obj = Wrapper.newWrapper((Dispatch) obj);
        }

        return super.replaceObject(obj);
    }

    protected KalamaHelperHelperW() throws IOException, SecurityException {
        super.enableReplaceObject(true);
    }

    public KalamaHelperHelperW(OutputStream out) throws IOException {
        super(out);
        super.enableReplaceObject(true);
    }
}
