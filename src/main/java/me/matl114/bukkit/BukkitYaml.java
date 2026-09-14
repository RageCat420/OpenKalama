package me.matl114.bukkit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;

public class BukkitYaml {
    private final LoaderOptions d;
    private final KalamaHelperHelperQ e;

    @Deprecated
    protected static final String b = "{}\n";

    @Deprecated
    protected static final String a = "# ";

    private final DumperOptions c = new DumperOptions();
    private final Yaml f;

    public BukkitYaml() {
        this.c.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.d = new LoaderOptions();
        this.d.setMaxAliasesForCollections(Integer.MAX_VALUE);
        this.e = new KalamaHelperHelperQ(this, this.d);
        this.f = new Yaml(this.e, new Representer(this.c), this.c, this.d);
    }

    private boolean hasSerializedTypeKey(MappingNode node) {
        for (NodeTuple var3 : node.getValue()) {
            Node var4 = var3.getKeyNode();
            if (var4 instanceof ScalarNode) {
                String var5 = ((ScalarNode) var4).getValue();
                if (var5.equals("==")) {
                    return true;
                }
            }
        }

        return false;
    }

    private void adjustNodeComments(MappingNode node) {
        if (node.getBlockComments() == null && !node.getValue().isEmpty()) {
            Node var2 = node.getValue().get(0).getKeyNode();
            List var3 = var2.getBlockComments();
            if (var3 != null) {
                int var4 = -1;

                for (int var5 = 0; var5 < var3.size(); var5++) {
                    if (((CommentLine) var3.get(var5)).getCommentType() == CommentType.BLANK_LINE) {
                        var4 = var5;
                    }
                }

                if (var4 != -1) {
                    node.setBlockComments(var3.subList(0, var4 + 1));
                    var2.setBlockComments(var3.subList(var4 + 1, var3.size()));
                }
            }
        }
    }

    public BukkitItemStack getItemStackFromString(String string) throws KalamaHelperHelperS {
        MappingNode var6;
        try {
            Throwable var2 = null;
            Object var3 = null;

            try {
                UnicodeReader var4 =
                        new UnicodeReader(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));

                try {
                    Node var5 = this.f.compose(var4);

                    try {
                        var6 = (MappingNode) var5;
                    } catch (ClassCastException var13) {
                        throw new KalamaHelperHelperS("Top level is not a Map.");
                    }
                } finally {
                    if (var4 != null) {
                        var4.close();
                    }
                }
            } catch (Throwable var15) {
                if (var2 == null) {
                    var2 = var15;
                } else if (var2 != var15) {
                    var2.addSuppressed(var15);
                }

                throw new IOException(var2);
            }
        } catch (ClassCastException | YAMLException | IOException var16) {
            throw new KalamaHelperHelperS(var16);
        }

        if (var6 == null) {
            throw new KalamaHelperHelperS("this config contains null");
        } else {
            this.adjustNodeComments(var6);
            this.e.flattenMapping(var6);
            Iterator var17 = var6.getValue().iterator();

            while (!var17.hasNext()) {}

            NodeTuple var18 = (NodeTuple) var17.next();
            Node var19 = var18.getKeyNode();
            String var20 = String.valueOf(this.e.a(var19));
            Node var7 = var18.getValueNode();

            while (var7 instanceof AnchorNode) {
                var7 = ((AnchorNode) var7).getRealNode();
            }

            if (var7 instanceof MappingNode && !this.hasSerializedTypeKey((MappingNode) var7)) {
                throw new UnsupportedOperationException();
            } else {
                return (BukkitItemStack) (Object) this.e.a(var7);
            }
        }
    }
}
