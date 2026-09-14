package me.matl114.utils.commands.params.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import me.matl114.utils.commands.params.types.EntitySelector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class EntityArgumentType extends AbstractArgumentType<EntitySelector> implements ArgumentType<EntitySelector> {
    private static final Set<String> h = Set.of("type", "tag", "predicate");
    private static final List<String> i = List.of("nearest", "furthest", "random", "arbitrary");
    private static final List<String> f = List.of("@p", "@a", "@r", "@s", "@e", "@n");
    private static final MinecraftClient e = MinecraftClient.getInstance();
    private static final List<String> g = List.of(
            "x=",
            "y=",
            "z=",
            "distance=",
            "dx=",
            "dy=",
            "dz=",
            "x_rotation=",
            "y_rotation=",
            "limit=",
            "sort=",
            "name=",
            "type=",
            "tag=",
            "nbt=",
            "scores=",
            "team=",
            "gamemode=",
            "advancements=",
            "predicate=",
            "level=");
    private static final List<String> j = List.of("survival", "creative", "adventure", "spectator");

    private static Stream<String> W(String raw) {
        if (raw == null || raw.isEmpty()) {
            return getEntityTabs();
        } else if (!raw.startsWith("@")) {
            return ag(getEntityTabs(), raw);
        } else if (raw.length() == 1) {
            return f.stream().filter(tab -> tab.startsWith(raw));
        } else if (raw.length() == 2 && f.contains(raw)) {
            return Stream.of(raw + "[");
        } else if (raw.length() == 2) {
            return f.stream().filter(tab -> tab.startsWith(raw));
        } else if (!f.contains(raw.substring(0, 2))) {
            return Stream.empty();
        } else if (raw.charAt(2) != '[') {
            return Stream.empty();
        } else {
            return raw.endsWith("]") ? Stream.empty() : X(raw);
        }
    }

    @Nullable
    private static EntitySelector D(String raw) {
        if (raw.length() < 2) {
            return null;
        } else {
            KalamaHelperHelperB var1 =
                    switch (raw.charAt(1)) {
                        case 'a' -> KalamaHelperHelperB.a(raw);
                        default -> null;
                        case 'e' -> KalamaHelperHelperB.b(raw);
                        case 'n' -> KalamaHelperHelperB.c(raw);
                        case 'p' -> KalamaHelperHelperB.d(raw);
                        case 'r' -> KalamaHelperHelperB.e(raw);
                        case 's' -> KalamaHelperHelperB.f(raw);
                    };
            if (var1 == null) {
                return null;
            } else if (raw.length() == 2) {
                return var1.u();
            } else if (raw.length() >= 4 && raw.charAt(2) == '[' && raw.charAt(raw.length() - 1) == ']') {
                String var2 = raw.substring(3, raw.length() - 1);
                return !var1.g(var2) ? null : var1.u();
            } else {
                return null;
            }
        }
    }

    @Nullable
    public static KalamaHelperHelperL U(String raw, boolean allowNegative) {
        if (raw.isEmpty()) {
            return null;
        } else {
            int var2 = raw.indexOf("..");

            try {
                if (var2 < 0) {
                    int var8 = Integer.parseInt(raw);
                    return !allowNegative && var8 < 0 ? null : new KalamaHelperHelperL(var8, var8);
                } else if (raw.indexOf("..", var2 + 2) >= 0) {
                    return null;
                } else {
                    String var3 = raw.substring(0, var2);
                    String var4 = raw.substring(var2 + 2);
                    Integer var5 = var3.isEmpty() ? null : Integer.parseInt(var3);
                    Integer var6 = var4.isEmpty() ? null : Integer.parseInt(var4);
                    return allowNegative || (var5 == null || var5 >= 0) && (var6 == null || var6 >= 0)
                            ? new KalamaHelperHelperL(var5, var6)
                            : null;
                }
            } catch (NumberFormatException var7) {
                return null;
            }
        }
    }

    @Override
    public Stream<String> getTab(CommandExecution sender, List<InputArgument<?>> args) {
        Stream var3 = super.getTab(sender, args);
        Stream var4 = W(y(args));
        return Stream.<String>concat(var3, var4).distinct();
    }

    private static Stream<String> aa(String prefix, String option, String value) {
        if (!g.contains(option + "=")) {
            return Stream.empty();
        } else {
            Stream var5 =
                    switch (option) {
                        case "sort" -> ag(i.stream(), value);
                        case "gamemode" -> ab(j.stream(), value);
                        case "type" -> entityTypeTabs(value);
                        case "name" -> ab(ad(), value);
                        case "team" -> ab(teamTabs(), value);
                        case "tag" -> ab(af(), value);
                        case "limit" -> ag(Stream.of("1"), value);
                        case "distance", "level" -> ag(Stream.of(".."), value);
                        default -> Stream.empty();
                    };
            Stream var3 = var5.map(tab -> prefix + option + "=" + tab);
            return ah(option, value)
                    ? Stream.concat(
                            var3, Stream.of(prefix + option + "=" + value + ",", prefix + option + "=" + value + "]"))
                    : var3;
        }
    }

    private static Stream<String> ad() {
        return allEntities().stream()
                .map(EntityArgumentType::entityName)
                .filter(name -> name != null && !name.isBlank() && !name.contains(" "))
                .distinct();
    }

    private static Stream<String> ab(Stream<String> values, String value) {
        return value.startsWith("!")
                ? Stream.concat(Stream.of("!"), ag(values, value.substring(1)).map(tab -> "!" + tab))
                : Stream.concat(Stream.of("!"), ag(values, value));
    }

    @Nullable
    public static KalamaHelperHelperC T(String raw, boolean allowNegative) {
        if (raw.isEmpty()) {
            return null;
        } else {
            int var2 = raw.indexOf("..");

            try {
                if (var2 < 0) {
                    double var3 = Double.parseDouble(raw);
                    return !allowNegative && var3 < 0.0 ? null : new KalamaHelperHelperC(var3, var3);
                } else if (raw.indexOf("..", var2 + 2) >= 0) {
                    return null;
                } else {
                    String var5 = raw.substring(0, var2);
                    String var6 = raw.substring(var2 + 2);
                    Double var7 = var5.isEmpty() ? null : Double.parseDouble(var5);
                    Double var8 = var6.isEmpty() ? null : Double.parseDouble(var6);
                    return allowNegative || (var7 == null || !(var7 < 0.0)) && (var8 == null || !(var8 < 0.0))
                            ? new KalamaHelperHelperC(var7, var8)
                            : null;
                }
            } catch (NumberFormatException var9) {
                return null;
            }
        }
    }

    private static int S(String value) {
        int var1 = 0;
        char var2 = 0;
        boolean var3 = false;

        for (int var4 = 0; var4 < value.length(); var4++) {
            char var5 = value.charAt(var4);
            if (var2 != 0) {
                if (var3) {
                    var3 = false;
                } else if (var5 == '\\') {
                    var3 = true;
                } else if (var5 == var2) {
                    var2 = 0;
                }
            } else if (var5 == '"' || var5 == '\'') {
                var2 = var5;
            } else if (var5 == '{' || var5 == '[' || var5 == '(') {
                var1++;
            } else if (var5 != '}' && var5 != ']' && var5 != ')') {
                if (var5 == '=' && var1 == 0) {
                    return var4;
                }
            } else {
                var1--;
            }
        }

        return -1;
    }

    static boolean isPlayer(Entity entity) {
        return entity instanceof PlayerEntity;
    }

    @Nullable
    @Override
    public InputArgument<EntitySelector> consume(
            CommandExecution execution, List<InputArgument<?>> args, ArgumentReader reader) {
        if (!reader.hasNext()) {
            return new OptionalArgumentResult(null, this, reader, reader.b(), false);
        } else {
            int var4 = reader.b();
            String var5 = reader.f();
            EntitySelector var6 = A(var5);
            if (var6 == null) {
                reader.c(var4);
                return new OptionalArgumentResult(null, this, reader, var4, false);
            } else {
                return new OptionalArgumentResult(var6, this, reader, var4, true);
            }
        }
    }

    private static Stream<String> teamTabs() {
        return e.world == null
                ? Stream.empty()
                : allEntities().stream()
                        .map(EntityArgumentType::teamName)
                        .filter(s -> !s.isEmpty())
                        .distinct();
    }

    private static Set<String> Z(String options) {
        List<KalamaHelperHelperT> var1 =
                Q(options.endsWith(",") ? options.substring(0, options.length() - 1) : options);
        return var1 != null && !var1.isEmpty()
                ? var1.stream().map(KalamaHelperHelperT::key).collect(Collectors.toSet())
                : Set.of();
    }

    public static EntitySelector A(String raw) {
        if (raw != null && !raw.isEmpty() && raw.startsWith("@")) {
            return raw.length() >= 2 && isSelectorHead(raw.charAt(1)) ? D(raw) : C(raw.substring(1));
        } else {
            return null;
        }
    }

    static List<Entity> selfEntity(CommandExecution execution) {
        PlayerEntity var1 = execution.si();
        return var1 != null && !var1.isRemoved() ? List.of(var1) : List.of();
    }

    private static int ai(String value) {
        int var1 = 0;
        char var2 = 0;
        boolean var3 = false;
        int var4 = -1;

        for (int var5 = 0; var5 < value.length(); var5++) {
            char var6 = value.charAt(var5);
            if (var2 != 0) {
                if (var3) {
                    var3 = false;
                } else if (var6 == '\\') {
                    var3 = true;
                } else if (var6 == var2) {
                    var2 = 0;
                }
            } else if (var6 == '"' || var6 == '\'') {
                var2 = var6;
            } else if (var6 == '{' || var6 == '[' || var6 == '(') {
                var1++;
            } else if (var6 == '}' || var6 == ']' || var6 == ')') {
                var1--;
            } else if (var6 == ',' && var1 == 0) {
                var4 = var5;
            }
        }

        return var4;
    }

    private static boolean ah(String option, String value) {
        if (value == null) {
            return false;
        } else {
            return switch (option) {
                case "team", "tag" -> true;
                default -> !value.isEmpty();
            };
        }
    }

    public EntityArgumentType(String argsName) {
        super(argsName);
    }

    private static String y(List<InputArgument<?>> args) {
        if (args.isEmpty()) {
            return "";
        } else {
            InputArgument var1 = (InputArgument) args.get(args.size() - 1);
            return var1 != null && var1.h() != null ? var1.h() : "";
        }
    }

    public static List<Entity> allEntities() {
        return e.world == null
                ? List.of()
                : StreamSupport.<Entity>stream(e.world.getEntities().spliterator(), false)
                        .toList();
    }

    private static Stream<String> entityTypeTabs(String value) {
        if (value.startsWith("!")) {
            return Stream.concat(
                    Stream.of("!", "!#"), entityTypeTabs(value.substring(1)).map(tab -> "!" + tab));
        } else if (value.startsWith("#")) {
            return Stream.of("#").filter(tab -> tab.startsWith(value));
        } else {
            Stream var1 = Registries.ENTITY_TYPE.stream()
                    .<Identifier>map(Registries.ENTITY_TYPE::getId)
                    .filter(Objects::nonNull)
                    .map(Identifier::toString);
            return Stream.concat(Stream.of("#"), ag(var1, value));
        }
    }

    public static boolean matchesGameMode(Entity entity, GameMode gameMode) {
        if (!(entity instanceof PlayerEntity)) {
            return false;
        } else if (e.getNetworkHandler() == null) {
            return false;
        } else {
            PlayerListEntry var2 = e.getNetworkHandler().getPlayerListEntry(entity.getUuid());
            return var2 != null && var2.getGameMode() == gameMode;
        }
    }

    private static Stream<String> af() {
        return e.world == null
                ? Stream.empty()
                : allEntities().stream()
                        .flatMap(entity -> entity.getCommandTags().stream())
                        .distinct();
    }

    public static Identifier parseIdentifier(String raw) {
        return Identifier.tryParse(raw.contains(":") ? raw : "minecraft:" + raw);
    }

    public static Vec3d I(CommandExecution execution) {
        Vector3d var1 = execution.sp();
        return new Vec3d(var1.x, var1.y, var1.z);
    }

    private static boolean isSelectorHead(char head) {
        return switch (head) {
            case 'a', 'e', 'n', 'p', 'r', 's' -> true;
            default -> false;
        };
    }

    private static Stream<String> Y(String previousOptions, String partial) {
        Set var2 = Z(previousOptions);
        return g.stream()
                .filter(option -> option.startsWith(partial))
                .filter(option -> h.contains(option.substring(0, option.length() - 1))
                        || !var2.contains(option.substring(0, option.length() - 1)));
    }

    @Nullable
    private static KalamaHelperHelperT R(String token) {
        int var1 = S(token);
        return var1 < 1
                ? null
                : new KalamaHelperHelperT(
                        token.substring(0, var1).trim(),
                        token.substring(var1 + 1).trim());
    }

    public static List<KalamaHelperHelperT> Q(String options) {
        if (options.isEmpty()) {
            return List.of();
        } else {
            ArrayList var1 = new ArrayList();
            int var2 = 0;
            int var3 = 0;
            char var4 = 0;
            boolean var5 = false;

            for (int var6 = 0; var6 < options.length(); var6++) {
                char var7 = options.charAt(var6);
                if (var4 != 0) {
                    if (var5) {
                        var5 = false;
                    } else if (var7 == '\\') {
                        var5 = true;
                    } else if (var7 == var4) {
                        var4 = 0;
                    }
                } else if (var7 == '"' || var7 == '\'') {
                    var4 = var7;
                } else if (var7 == '{' || var7 == '[' || var7 == '(') {
                    var3++;
                } else if (var7 == '}' || var7 == ']' || var7 == ')') {
                    if (--var3 < 0) {
                        return null;
                    }
                } else if (var7 == ',' && var3 == 0) {
                    String var8 = options.substring(var2, var6).trim();
                    if (var8.isEmpty()) {
                        return null;
                    }

                    KalamaHelperHelperT var9 = R(var8);
                    if (var9 == null) {
                        return null;
                    }

                    var1.add(var9);
                    var2 = var6 + 1;
                }
            }

            if (var4 == 0 && var3 == 0) {
                String var10 = options.substring(var2).trim();
                if (!var10.isEmpty()) {
                    KalamaHelperHelperT var11 = R(var10);
                    if (var11 == null) {
                        return null;
                    }

                    var1.add(var11);
                }

                return var1;
            } else {
                return null;
            }
        }
    }

    @Nullable
    public static GameMode parseGameMode(String raw) {
        String var1 = raw.toLowerCase(Locale.ROOT);

        return switch (var1) {
            case "survival" -> GameMode.SURVIVAL;
            case "creative" -> GameMode.CREATIVE;
            case "adventure" -> GameMode.ADVENTURE;
            case "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    private static Stream<String> ag(Stream<String> stream, String token) {
        String var2 = token.toLowerCase(Locale.ROOT);
        return stream.filter(value -> value.toLowerCase(Locale.ROOT).contains(var2));
    }

    private static String entityName(Entity entity) {
        return entity instanceof PlayerEntity var1
                ? var1.getNameForScoreboard()
                : entity.getName().getString();
    }

    private static List<Entity> resolveUuid(UUID uuid) {
        if (e.world == null) {
            return List.of();
        } else {
            Entity var1 = (Entity) e.world.getEntityLookup().get(uuid);
            return var1 == null ? List.of() : List.of(var1);
        }
    }

    private static List<Entity> resolveNamed(String raw) {
        return e.world == null
                ? List.of()
                : allEntities().stream()
                        .filter(entity -> matchesName(entity, raw))
                        .toList();
    }

    public static boolean matchesName(Entity entity, String name) {
        return Objects.equals(entityName(entity), name)
                || Objects.equals(entity.getName().getString(), name);
    }

    private static EntitySelector C(String raw) {
        try {
            UUID var1 = UUID.fromString(raw);
            return new KalamaHelperHelperM(raw, execution -> resolveUuid(var1));
        } catch (IllegalArgumentException var2) {
            return raw.isEmpty() ? null : new KalamaHelperHelperM(raw, execution -> resolveNamed(raw));
        }
    }

    public static Stream<String> getEntityTabs() {
        List<Entity> var0 = allEntities();
        Stream<String> var1 = var0.stream()
                .flatMap(entity -> Stream.of("@" + entityName(entity), "@" + entity.getUuidAsString()))
                .filter(token -> token != null && !token.isBlank() && !token.contains(" "))
                .distinct();
        Stream<String> var2 = e.crosshairTarget != null && e.crosshairTarget.getType() == Type.ENTITY
                ? Stream.of(
                        "@" + ((EntityHitResult) e.crosshairTarget).getEntity().getUuidAsString())
                : Stream.empty();
        return Stream.concat(Stream.concat(f.stream(), var1), var2).distinct();
    }

    public static boolean rotationMatches(KalamaHelperHelperC range, float value) {
        float var2 = MathHelper.wrapDegrees((float) range.tu(0.0));
        float var3 = MathHelper.wrapDegrees((float) range.tv(359.0));
        float var4 = MathHelper.wrapDegrees(value);
        return var2 > var3 ? var4 >= var2 || var4 <= var3 : var4 >= var2 && var4 <= var3;
    }

    private static Stream<String> X(String raw) {
        String var1 = raw.substring(3);
        int var2 = ai(var1) + 1;
        String var3 = var1.substring(var2);
        String var4 = raw.substring(0, 3 + var2);
        int var5 = S(var3);
        if (var5 < 0) {
            Stream var8 = Y(var1.substring(0, var2), var3).map(option -> var4 + option);
            return var3.isEmpty() ? Stream.concat(Stream.of(var4 + "]"), var8) : var8;
        } else {
            String var6 = var3.substring(0, var5).trim();
            String var7 = var3.substring(var5 + 1).trim();
            return aa(var4, var6, var7);
        }
    }

    public static String teamName(Entity entity) {
        return entity instanceof PlayerEntity var1 && var1.getScoreboardTeam() != null
                ? var1.getScoreboardTeam().getName()
                : "";
    }
}
