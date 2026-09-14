package me.matl114.bukkit;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class KalamaHelperHelperQ extends SafeConstructor {
    @Nullable
    public Object a(@NotNull Node node) {
        return this.constructObject(node);
    }

    @Override
    protected Map<Object, Object> newMap(MappingNode node) {
        return this.createDefaultMap(node.getValue().size());
    }

    @Override
    public void flattenMapping(@NotNull MappingNode node) {
        super.flattenMapping(node);
    }

    @Override
    protected List<Object> newList(SequenceNode node) {
        return this.createDefaultList(node.getValue().size());
    }

    @Deprecated
    public KalamaHelperHelperQ(final BukkitYaml this$0) {
        this(this$0, new LoaderOptions());
    }

    public KalamaHelperHelperQ(@NotNull final BukkitYaml this$0, LoaderOptions loaderOptions) {
        super(loaderOptions);
        this.yamlConstructors.put(Tag.MAP, new KalamaHelperHelperY(this));
    }
}
