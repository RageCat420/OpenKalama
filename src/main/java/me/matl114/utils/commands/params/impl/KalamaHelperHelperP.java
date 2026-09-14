package me.matl114.utils.commands.params.impl;

record KalamaHelperHelperP(boolean inverted, String value) {
    public String value() {
        return this.value;
    }

    public boolean inverted() {
        return this.inverted;
    }

    public static KalamaHelperHelperP read(String raw) {
        return raw.startsWith("!")
                ? new KalamaHelperHelperP(true, raw.substring(1))
                : new KalamaHelperHelperP(false, raw);
    }

    KalamaHelperHelperP(boolean inverted, String value) {
        this.inverted = inverted;
        this.value = value;
    }
}
