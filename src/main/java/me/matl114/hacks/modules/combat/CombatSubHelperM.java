package me.matl114.hacks.modules.combat;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public record CombatSubHelperM(List<String> friends, Map<String, String> alias) {
    public static Codec<CombatSubHelperM> CODEC = RecordCodecBuilder.create(oinstance -> oinstance
            .group(
                    Codec.list(Codec.STRING).fieldOf("friend-list").forGetter(CombatSubHelperM::aal),
                    Codec.unboundedMap(Codec.STRING, Codec.STRING)
                            .optionalFieldOf("friend-alias", Map.of())
                            .forGetter(CombatSubHelperM::aam))
            .apply(oinstance, CombatSubHelperM::new));

    public boolean aaf(String friendName) {
        return this.friends.contains(friendName);
    }

    public CombatSubHelperM withAdd(String friendName, @Nullable String alias) {
        if (this.contains(friendName, alias)) {
            return this;
        } else {
            ArrayList<String> var3 = new ArrayList<>(this.friends);
            var3.add(friendName);
            Map<String, String> var4 = this.alias;
            if (alias != null && !alias.trim().isEmpty()) {
                var4 = new LinkedHashMap<>(var4);
                var4.put(friendName, alias);
            }

            return new CombatSubHelperM(var3, var4);
        }
    }

    public String aad(String friendName) {
        return this.alias.getOrDefault(friendName, "F");
    }

    public List<String> aal() {
        return this.friends;
    }

    public Map<String, String> aam() {
        return this.alias;
    }

    public boolean contains(String friendName, @Nullable String alias) {
        return this.friends.contains(friendName)
                && Objects.equals(
                        alias != null && !alias.trim().isEmpty() ? alias.trim() : null, this.alias.get(friendName));
    }

    public static CombatSubHelperM create(List<Pair<String, String>> data) {
        LinkedHashMap var1 = new LinkedHashMap();

        for (Pair var3 : data) {
            if (!((String) var3.getSecond()).trim().isEmpty()) {
                var1.put((String) var3.getFirst(), ((String) var3.getSecond()).trim());
            }
        }

        return new CombatSubHelperM(data.stream().<String>map(Pair::getFirst).toList(), var1);
    }

    public Stream<String> aak() {
        return this.friends.stream();
    }

    public List<Pair<String, String>> toPairList() {
        ArrayList var1 = new ArrayList();

        for (String var3 : this.friends) {
            var1.add(Pair.of(var3, this.alias.getOrDefault(var3, "")));
        }

        return var1;
    }

    public CombatSubHelperM aai(String friendName) {
        if (this.aaf(friendName)) {
            ArrayList<String> var2 = new ArrayList<>(this.friends);
            var2.remove(friendName);
            Map<String, String> var3 = this.alias;
            if (this.alias.containsKey(friendName)) {
                var3 = new LinkedHashMap<>(var3);
                var3.remove(friendName);
            }

            return new CombatSubHelperM(var2, var3);
        } else {
            return this;
        }
    }
}
