package me.matl114.bukkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

class KalamaHelperHelperF implements KalamaHelperHelperZ {
    private final Gson loader = new GsonBuilder().create();

    @Override
    public String format(JsonElement jsonElement) {
        return this.loader.toJson(jsonElement);
    }
}
