package me.matl114.jsApi;

import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import me.matl114.events.annotations.Modifiable;
import me.matl114.versioned.api.VNbt;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

@Modifiable
public class NBTHelper {
   public static NbtElement f(String string) throws CommandSyntaxException {
      return VNbt.getInstance().c(string);
   }

   public static String e(Object element) {
      return VNbt.getInstance().b(JsHelper.a(element, NbtElement.class));
   }

   public static Object convertNbtToJava(Object element) {
      return NbtOps.INSTANCE.convertTo(JavaOps.INSTANCE, JsHelper.a(element, NbtElement.class));
   }

   public static NbtElement convertJavaToNbt(Object object) {
      return (NbtElement)JavaOps.INSTANCE.convertTo(NbtOps.INSTANCE, object);
   }

   public static NbtElement convertJsonToNbt(JsonElement object) {
      return (NbtElement)JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, object);
   }

   public static JsonElement convertNbtToJson(Object element) {
      return (JsonElement)NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, JsHelper.a(element, NbtElement.class));
   }
}
