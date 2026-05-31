package com.m4ssive.totemcounterv2;

import net.minecraft.client.option.KeyBinding;
import java.lang.reflect.Constructor;

public class KeyDump {
    public static void dump() {
        for (Constructor<?> c : KeyBinding.class.getConstructors()) {
            System.out.println(c.toString());
        }
    }
}
