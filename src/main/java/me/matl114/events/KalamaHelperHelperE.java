package me.matl114.events;

public record KalamaHelperHelperE(Listener$ExceptionType type, Throwable exception) {
    public Listener$ExceptionType Wx() {
        return this.type;
    }

    public Throwable exception() {
        return this.exception;
    }
}
