package com.devapp.devmain.util.config;

public interface Configuration {
    String get(Key key);

    String get(Key key, String defaultValue);

    interface Key {
    }
}