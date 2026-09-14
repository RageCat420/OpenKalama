package me.matl114.hacks.modules.render;

import java.util.UUID;
import net.minecraft.client.network.PlayerListEntry;

public class RenderSubHelperFX {
    int d;
    final boolean initialize;
    final PlayerListEntry b;
    final UUID a;
    int c;
    final int f;

    public RenderSubHelperFX(
            UUID uuid, PlayerListEntry entry, int order, int lastOrder, boolean initialize, int startTick) {
        this.a = uuid;
        this.b = entry;
        this.c = order;
        this.d = lastOrder;
        this.initialize = initialize;
        this.f = startTick;
    }
}
