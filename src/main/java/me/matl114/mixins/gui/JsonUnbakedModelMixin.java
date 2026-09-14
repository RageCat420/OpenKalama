package me.matl114.mixins.gui;

import java.util.List;
import java.util.function.Function;
import me.matl114.hacks.ModelTasks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BasicBakedModel.Builder;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel.GuiLight;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(
        value = {JsonUnbakedModel.class},
        priority = 990)
public abstract class JsonUnbakedModelMixin implements UnbakedModel {
    @Inject(
            method = {
                "bake(Lnet/minecraft/client/render/model/Baker;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Z)Lnet/minecraft/client/render/model/BakedModel;"
            },
            at = {@At("HEAD")},
            cancellable = true)
    private void generateCustomBakedModel(
            Baker baker,
            JsonUnbakedModel parent,
            Function<SpriteIdentifier, Sprite> textureGetter,
            ModelBakeSettings settings,
            boolean bl,
            CallbackInfoReturnable<BakedModel> cir) {
        if (ModelTasks.c().enableBlockModelProtect.get()) {
            cir.setReturnValue(this.rewriteSafeBkae(baker, parent, textureGetter, settings, bl));
            cir.cancel();
        }
    }

    public BakedModel rewriteSafeBkae(
            Baker baker,
            JsonUnbakedModel parent,
            Function<SpriteIdentifier, Sprite> textureGetter,
            ModelBakeSettings settings,
            boolean bl) {
        Sprite sprite = textureGetter.apply(this.method_24077("particle"));
        if (this.method_3431() == ModelLoader.BLOCK_ENTITY_MARKER) {
            return new BuiltinBakedModel(
                    this.method_3443(),
                    this.method_3440(baker, parent),
                    sprite,
                    this.method_24298().isSide());
        } else {
            Builder builder = new Builder((JsonUnbakedModel) (Object) this, this.method_3440(baker, parent), bl)
                    .setParticle(sprite);

            for (ModelElement modelElement : this.method_3433()) {
                for (Direction direction : modelElement.faces.keySet()) {
                    ModelElementFace modelElementFace = (ModelElementFace) modelElement.faces.get(direction);
                    Sprite sprite2 = textureGetter.apply(this.method_24077(modelElementFace.textureId()));
                    if (modelElementFace.cullFace() == null) {
                        builder.addQuad(method_3447(modelElement, modelElementFace, sprite2, direction, settings));
                    } else {
                        builder.addQuad(
                                Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace()),
                                method_3447(modelElement, modelElementFace, sprite2, direction, settings));
                    }
                }
            }

            return builder.build();
        }
    }

    @Shadow
    public static BakedQuad method_3447(
            ModelElement element,
            ModelElementFace elementFace,
            Sprite sprite,
            Direction side,
            ModelBakeSettings settings) {
        throw new NullPointerException("not implemented yet");
    }

    @Shadow
    public abstract List<ModelElement> method_3433();

    @Shadow
    public abstract GuiLight method_24298();

    @Shadow
    public abstract ModelOverrideList method_3440(Baker var1, JsonUnbakedModel var2);

    @Shadow
    public abstract ModelTransformation method_3443();

    @Shadow
    public abstract JsonUnbakedModel method_3431();

    @Shadow
    public abstract SpriteIdentifier method_24077(String var1);
}
