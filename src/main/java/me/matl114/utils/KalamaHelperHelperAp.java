package me.matl114.utils;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;

public class KalamaHelperHelperAp implements VertexConsumer {
   private final Sprite sprite;
   private final VertexConsumer delegate;

   public VertexConsumer overlay(int u, int v) {
      this.delegate.overlay(u, v);
      return this;
   }

   public void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
      this.delegate.vertex(x, y, z, color, this.sprite.getFrameU(u), this.sprite.getFrameV(v), overlay, light, normalX, normalY, normalZ);
   }

   public VertexConsumer color(int argb) {
      this.delegate.color(argb);
      return this;
   }

   public KalamaHelperHelperAp(VertexConsumer delegate, Sprite sprite) {
      this.delegate = delegate;
      this.sprite = sprite;
   }

   public VertexConsumer texture(float u, float v) {
      this.delegate.texture(this.sprite.getFrameU(u), this.sprite.getFrameV(v));
      return this;
   }

   public VertexConsumer light(int u, int v) {
      this.delegate.light(u, v);
      return this;
   }

   public VertexConsumer color(int red, int green, int blue, int alpha) {
      this.delegate.color(red, green, blue, alpha);
      return this;
   }

   public VertexConsumer vertex(float x, float y, float z) {
      this.delegate.vertex(x, y, z);
      return this;
   }

   public VertexConsumer normal(float x, float y, float z) {
      this.delegate.normal(x, y, z);
      return this;
   }
}
