package me.matl114.managers.config;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import me.matl114.utils.config.BaseAttrKeyValue;

public class MapRef extends Ref<Map<String, Ref<?>>> implements RefMap {
    public static final Class<Map<String, Ref<?>>> TYPE = (Class<Map<String, Ref<?>>>) (Class<?>) Map.class;
    Map<String, Ref<?>> map = new LinkedHashMap<>();
    boolean section = false;

    private static void syncTo(Map<String, Ref<?>> oldConfig, Map<String, Ref<?>> newConfig) {
        for (Entry<String, Ref<?>> entry : newConfig.entrySet()) {
            if (oldConfig.containsKey(entry.getKey())) {
                Ref<?> oldValue = oldConfig.get(entry.getKey());
                Ref<?> newValue = entry.getValue();
                if (!oldValue.copyValueFrom(newValue)) {
                    oldConfig.remove(entry.getKey());
                    oldConfig.put(entry.getKey(), newValue);
                }
            } else {
                oldConfig.put(entry.getKey(), entry.getValue());
            }
        }

        Iterator<Entry<String, Ref<?>>> iter = oldConfig.entrySet().iterator();

        while (iter.hasNext()) {
            Entry<String, Ref<?>> entryx = iter.next();
            String key = entryx.getKey();
            if (!newConfig.containsKey(key)) {
                iter.remove();
            }
        }
    }

    private static Map<String, Object> transferBack(MapRef config) {
        LinkedHashMap<String, Object> newConfig = new LinkedHashMap<>();

        for (Entry<String, Ref<?>> entry : config.getValue().entrySet()) {
            newConfig.put(entry.getKey(), entry.getValue().getAsPrimitive());
        }

        return newConfig;
    }

    public void markAsSection() {
        this.section = true;
    }

    public void removeSection() {
        this.section = false;
    }

    public Map<String, Ref<?>> getValue() {
        return this.map;
    }

    private void setValueRecursively(Map<String, Ref<?>> map0) {
        syncTo(this.map, map0);
    }

    public void putRaw(String str, Ref ref) {
        this.map.put(str, ref);
    }

    public void setValue(Map<String, Ref<?>> value) {
        if (this.validateUpdateValue(value)) {
            this.setValueRecursively(value);
            this.callUpdate();
        }
    }

    public boolean setValueNoCopy(Map<String, Ref<?>> value) {
        if (this.validateUpdateValue(value)) {
            this.map = value;
            this.callUpdate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object getAsPrimitive() {
        return transferBack(this);
    }

    @Override
    public <W> boolean isSameTypeWith(Ref<W> ref) {
        return ref instanceof MapRef;
    }

    @Override
    public <W> boolean copyValueFrom(Ref<W> otherRef) {
        if (otherRef instanceof MapRef map) {
            this.setValue(map.map);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected BaseAttrKeyValue<Map<String, Ref<?>>> _createKeyValue0(String key) {
        throw new IllegalStateException("Not impl yet");
    }

    public boolean setValue(Ref<?> value, String... path) {
        return this.setValue0(value, path, 0);
    }

    private boolean setValue0(Ref<?> value, String[] path, int index) {
        if (path.length <= index) {
            return false;
        } else if (path.length - 1 == index) {
            if (value != null) {
                if (this.map.get(path[index]) instanceof Ref<?> ref) {
                    if (Objects.equals(ref.getAsPrimitive(), value.getAsPrimitive())) {
                        return false;
                    } else {
                        LinkedHashMap<String, Ref<?>> map0 = new LinkedHashMap<>(this.map);
                        if (!ref.copyValueFrom(value)) {
                            map0.put(path[index], value);
                        }

                        return this.setValueNoCopy(map0);
                    }
                } else {
                    LinkedHashMap<String, Ref<?>> map0 = new LinkedHashMap<>(this.map);
                    map0.put(path[index], value);
                    return this.setValueNoCopy(map0);
                }
            } else {
                boolean contains = this.map.containsKey(path[index]);
                if (contains) {
                    LinkedHashMap<String, Ref<?>> map0 = new LinkedHashMap<>(this.map);
                    map0.remove(path[index]);
                    return this.setValueNoCopy(map0);
                } else {
                    return false;
                }
            }
        } else if (this.map.containsKey(path[index]) && this.map.get(path[index]) instanceof MapRef subMap) {
            return subMap.setValue0(value, path, index + 1);
        } else {
            MapRef mapRef2 = new MapRef();
            LinkedHashMap<String, Ref<?>> map0 = new LinkedHashMap<>(this.map);
            map0.put(path[index], mapRef2);
            mapRef2.setValue0(value, path, index + 1);
            return this.setValueNoCopy(map0);
        }
    }

    @Override
    public Ref<?> get(@Nonnull String... path) {
        return this.get(path, 0);
    }

    public Ref<?> get(@Nonnull String[] path, int index) {
        if (path.length <= index) {
            return null;
        } else if (path.length - 1 == index) {
            return this.map.get(path[index]);
        } else {
            return this.map.get(path[index]) instanceof MapRef mapRef ? mapRef.get(path, index + 1) : null;
        }
    }

    public <T> Ref<T> getOrCreate(Ref<T> ref, String... path) {
        return (Ref<T>) (Object) this.getOrCreate(ref, path, 0);
    }

    public Ref<?> getOrCreate(@Nonnull Ref<?> ref, String[] path, int index) {
        if (path.length <= index) {
            throw new UnsupportedOperationException("path length = 0");
        } else if (path.length - 1 == index) {
            Ref<?> ref0 = this.map.get(path[index]);
            if (ref0 != null && ref.isSameTypeWith(ref0)) {
                return ref0;
            } else {
                LinkedHashMap<String, Ref<?>> map0 = new LinkedHashMap<>(this.map);
                if (ref0 != null) {
                    ref.copyValueFrom(ref0);
                }

                map0.put(path[index], ref);
                return this.setValueNoCopy(map0) ? ref : null;
            }
        } else if (this.map.get(path[index]) instanceof MapRef mapRef) {
            return mapRef.getOrCreate(ref, path, index + 1);
        } else {
            LinkedHashMap<String, Ref<?>> map0 = new LinkedHashMap<>(this.map);
            MapRef mapRef = new MapRef();
            map0.put(path[index], mapRef);
            return mapRef.setValue0(ref, path, index + 1) && this.setValueNoCopy(map0) ? ref : null;
        }
    }

    public Set<String> getKeys() {
        return this.map.keySet();
    }

    public Set<String> getPaths() {
        return this.getPaths("");
    }

    public Set<String> getPaths(String prefix) {
        Set<String> set = new LinkedHashSet<>();
        this.walkPaths(set, prefix);
        return set;
    }

    public void walkPaths(Set<String> str, String prefix) {
        if (this.section) {
            str.add(prefix);
        } else {
            for (Entry<String, Ref<?>> entry : this.map.entrySet()) {
                String nextP = prefix + entry.getKey();
                if (entry.getValue() instanceof MapRef map) {
                    map.walkPaths(str, nextP + ".");
                } else {
                    str.add(nextP);
                }
            }
        }
    }

    public boolean containsPath(String[] path) {
        return this.get(path) != null;
    }

    @Override
    public IntRef getInt(String... path) {
        return null;
    }

    @Override
    public FlagRef getBoolean(String... path) {
        return null;
    }

    @Override
    public DoubleRef getDouble(String... path) {
        return null;
    }

    @Override
    public <T extends ConfigEnum> EnumRef<T> getEnum(String... path) {
        return null;
    }

    @Override
    public StringRef getString(String... path) {
        return null;
    }

    @Override
    public ListRef getList(String... path) {
        return null;
    }

    @Override
    public KeyBindRef getKeyBind(String... path) {
        return null;
    }
}
