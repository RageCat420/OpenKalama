package me.matl114.utils.commands.params.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.types.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

final class KalamaHelperHelperB {
    public Double h;
    public Double k;
    public final String a;
    public KalamaHelperHelperD n;
    public KalamaHelperHelperC m;
    public Double j;
    public KalamaHelperHelperC l;
    public Double g;
    public Double i;
    public KalamaHelperHelperL e;
    public final List<Predicate<Entity>> o = new ArrayList<>();
    public int limit;
    public Double f;
    public KalamaHelperHelperC d;
    public final Function<CommandExecution, List<Entity>> c;

    public static KalamaHelperHelperB a(String raw) {
        KalamaHelperHelperB var1 = new KalamaHelperHelperB(
                raw, Integer.MAX_VALUE, execution -> EntityArgumentType.allEntities(), KalamaHelperHelperD.mb);
        var1.o.add(EntityArgumentType::isPlayer);
        return var1;
    }

    public boolean n(String value) {
        try {
            int var2 = Integer.parseInt(value);
            if (var2 < 1) {
                return false;
            } else {
                this.limit = var2;
                return true;
            }
        } catch (NumberFormatException var3) {
            return false;
        }
    }

    public boolean l(String value) {
        KalamaHelperHelperC var2 = EntityArgumentType.T(value, true);
        if (var2 == null) {
            return false;
        } else {
            this.l = var2;
            this.o.add(entity -> EntityArgumentType.rotationMatches(var2, entity.getPitch()));
            return true;
        }
    }

    public Vec3d origin(CommandExecution execution) {
        Vec3d var2 = EntityArgumentType.I(execution);
        return new Vec3d(
                this.f == null ? var2.x : this.f, this.g == null ? var2.y : this.g, this.h == null ? var2.z : this.h);
    }

    public boolean g(String options) {
        List<KalamaHelperHelperT> var2 = EntityArgumentType.Q(options);
        if (var2 == null) {
            return false;
        } else {
            for (KalamaHelperHelperT var4 : var2) {
                if (!this.h(var4.key(), var4.key())) {
                    return false;
                }
            }

            return true;
        }
    }

    public static KalamaHelperHelperB c(String raw) {
        return new KalamaHelperHelperB(raw, 1, execution -> EntityArgumentType.allEntities(), KalamaHelperHelperD.mc);
    }

    public Predicate<Entity> w() {
        return this.o.stream().reduce(entity -> true, Predicate::and);
    }

    public static KalamaHelperHelperB e(String raw) {
        KalamaHelperHelperB var1 =
                new KalamaHelperHelperB(raw, 1, execution -> EntityArgumentType.allEntities(), KalamaHelperHelperD.me);
        var1.o.add(EntityArgumentType::isPlayer);
        return var1;
    }

    public boolean q(String value) {
        KalamaHelperHelperP var2 = KalamaHelperHelperP.read(value);
        this.o.add(entity -> Objects.equals(EntityArgumentType.teamName(entity), var2.value()) != var2.inverted());
        return true;
    }

    public EntitySelector u() {
        return new KalamaHelperHelperH(this);
    }

    public boolean r(String value) {
        KalamaHelperHelperP var2 = KalamaHelperHelperP.read(value);
        this.o.add(entity -> var2.value().isEmpty()
                ? entity.getCommandTags().isEmpty() != var2.inverted()
                : entity.getCommandTags().contains(var2.value()) != var2.inverted());
        return true;
    }

    public List<Entity> v(CommandExecution execution) {
        return this.c.apply(execution);
    }

    public boolean o(String value) {
        KalamaHelperHelperD var2 = KalamaHelperHelperD.tM(value);
        if (var2 == null) {
            return false;
        } else {
            this.n = var2;
            return true;
        }
    }

    public boolean m(String value) {
        KalamaHelperHelperC var2 = EntityArgumentType.T(value, true);
        if (var2 == null) {
            return false;
        } else {
            this.m = var2;
            this.o.add(entity -> EntityArgumentType.rotationMatches(var2, entity.getYaw()));
            return true;
        }
    }

    public boolean s(String value) {
        KalamaHelperHelperP var2 = KalamaHelperHelperP.read(value);
        GameMode var3 = EntityArgumentType.parseGameMode(var2.value());
        if (var3 == null) {
            return false;
        } else {
            this.o.add(entity -> EntityArgumentType.matchesGameMode(entity, var3) != var2.inverted());
            return true;
        }
    }

    public static KalamaHelperHelperB f(String raw) {
        return new KalamaHelperHelperB(raw, 1, EntityArgumentType::selfEntity, KalamaHelperHelperD.mb);
    }

    public KalamaHelperHelperB(
            String raw,
            int limit,
            Function<CommandExecution, List<Entity>> initialEntitiesResolver,
            KalamaHelperHelperD sorter) {
        this.a = raw;
        this.limit = limit;
        this.c = initialEntitiesResolver;
        this.n = sorter;
    }

    public static KalamaHelperHelperB d(String raw) {
        KalamaHelperHelperB var1 =
                new KalamaHelperHelperB(raw, 1, execution -> EntityArgumentType.allEntities(), KalamaHelperHelperD.mc);
        var1.o.add(EntityArgumentType::isPlayer);
        return var1;
    }

    public boolean k(String value) {
        KalamaHelperHelperL var2 = EntityArgumentType.U(value, false);
        if (var2 == null) {
            return false;
        } else {
            this.e = var2;
            this.o.add(entity -> entity instanceof PlayerEntity var2x && var2.test(var2x.experienceLevel));
            return true;
        }
    }

    public boolean i(String value, KalamaHelperHelperF setter) {
        try {
            setter.set(Double.parseDouble(value));
            return true;
        } catch (NumberFormatException var4) {
            return false;
        }
    }

    @Nullable
    public Box box(Vec3d origin) {
        if (this.i == null && this.j == null && this.k == null) {
            if (this.d != null && this.d.tx() != null) {
                double var20 = this.d.tx();
                return new Box(
                        origin.x - var20,
                        origin.y - var20,
                        origin.z - var20,
                        origin.x + var20 + 1.0,
                        origin.y + var20 + 1.0,
                        origin.z + var20 + 1.0);
            } else {
                return null;
            }
        } else {
            double var2 = this.i == null ? 0.0 : this.i;
            double var4 = this.j == null ? 0.0 : this.j;
            double var6 = this.k == null ? 0.0 : this.k;
            double var8 = var2 < 0.0 ? var2 : 0.0;
            double var10 = var4 < 0.0 ? var4 : 0.0;
            double var12 = var6 < 0.0 ? var6 : 0.0;
            double var14 = (var2 < 0.0 ? 0.0 : var2) + 1.0;
            double var16 = (var4 < 0.0 ? 0.0 : var4) + 1.0;
            double var18 = (var6 < 0.0 ? 0.0 : var6) + 1.0;
            return new Box(
                    origin.x + var8,
                    origin.y + var10,
                    origin.z + var12,
                    origin.x + var14,
                    origin.y + var16,
                    origin.z + var18);
        }
    }

    public boolean addType(String value) {
        KalamaHelperHelperP var2 = KalamaHelperHelperP.read(value);
        if (var2.value().isEmpty()) {
            return false;
        } else if (var2.value().startsWith("#")) {
            Identifier var5 = EntityArgumentType.parseIdentifier(var2.value().substring(1));
            if (var5 == null) {
                return false;
            } else {
                TagKey var6 = TagKey.of(RegistryKeys.ENTITY_TYPE, var5);
                this.o.add(entity -> entity.getType().isIn(var6) != var2.inverted());
                return true;
            }
        } else {
            Identifier var3 = EntityArgumentType.parseIdentifier(var2.value());
            if (var3 == null) {
                return false;
            } else {
                EntityType var4 =
                        (EntityType) Registries.ENTITY_TYPE.getOrEmpty(var3).orElse(null);
                if (var4 == null) {
                    return false;
                } else {
                    this.o.add(entity -> Objects.equals(entity.getType(), var4) != var2.inverted());
                    return true;
                }
            }
        }
    }

    public boolean h(String key, String value) {
        return switch (key) {
            case "x" -> this.i(value, number -> this.f = number);
            case "y" -> this.i(value, number -> this.g = number);
            case "z" -> this.i(value, number -> this.h = number);
            case "dx" -> this.i(value, number -> this.i = number);
            case "dy" -> this.i(value, number -> this.j = number);
            case "dz" -> this.i(value, number -> this.k = number);
            case "distance" -> this.j(value);
            case "level" -> this.k(value);
            case "x_rotation" -> this.l(value);
            case "y_rotation" -> this.m(value);
            case "limit" -> this.n(value);
            case "sort" -> this.o(value);
            case "name" -> this.p(value);
            case "type" -> this.addType(value);
            case "tag" -> this.r(value);
            case "team" -> this.q(value);
            case "gamemode" -> this.s(value);
            case "nbt", "scores", "advancements", "predicate" -> true;
            default -> false;
        };
    }

    public static KalamaHelperHelperB b(String raw) {
        return new KalamaHelperHelperB(
                raw, Integer.MAX_VALUE, execution -> EntityArgumentType.allEntities(), KalamaHelperHelperD.mb);
    }

    public boolean j(String value) {
        KalamaHelperHelperC var2 = EntityArgumentType.T(value, false);
        if (var2 == null) {
            return false;
        } else {
            this.d = var2;
            return true;
        }
    }

    public boolean p(String value) {
        KalamaHelperHelperP var2 = KalamaHelperHelperP.read(value);
        if (var2.value().isEmpty()) {
            return false;
        } else {
            this.o.add(entity -> EntityArgumentType.matchesName(entity, var2.value()) != var2.inverted());
            return true;
        }
    }
}
