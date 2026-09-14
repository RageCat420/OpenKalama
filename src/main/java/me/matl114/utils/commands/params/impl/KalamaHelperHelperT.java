package me.matl114.utils.commands.params.impl;

record KalamaHelperHelperT(String key, String value) {
    public String value() {
        return this.value;
    }

    public String key() {
        return this.key;
    }
}
