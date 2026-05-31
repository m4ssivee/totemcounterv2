package com.m4ssive.totemcounterv2;

import com.mojang.authlib.GameProfile;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.UUID;

public class GameProfileHelper {
    private static MethodHandle nameMethodHandle = null;
    private static MethodHandle idMethodHandle = null;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            // Resolve name() vs getName()
            try {
                Method nameMethod = GameProfile.class.getMethod("name");
                nameMethodHandle = lookup.unreflect(nameMethod);
            } catch (NoSuchMethodException e) {
                try {
                    Method getNameMethod = GameProfile.class.getMethod("getName");
                    nameMethodHandle = lookup.unreflect(getNameMethod);
                } catch (NoSuchMethodException ex) {
                    TotemCounterV2Mod.LOGGER.error("Could not find GameProfile name method", ex);
                }
            }
            // Resolve id() vs getId()
            try {
                Method idMethod = GameProfile.class.getMethod("id");
                idMethodHandle = lookup.unreflect(idMethod);
            } catch (NoSuchMethodException e) {
                try {
                    Method getIdMethod = GameProfile.class.getMethod("getId");
                    idMethodHandle = lookup.unreflect(getIdMethod);
                } catch (NoSuchMethodException ex) {
                    TotemCounterV2Mod.LOGGER.error("Could not find GameProfile id method", ex);
                }
            }
        } catch (Exception e) {
            TotemCounterV2Mod.LOGGER.error("Failed to initialize GameProfile method handles", e);
        }
    }

    public static String getName(GameProfile profile) {
        if (profile == null) return "Unknown";
        if (nameMethodHandle != null) {
            try {
                return (String) nameMethodHandle.invoke(profile);
            } catch (Throwable e) {
                // fall through
            }
        }
        UUID id = getId(profile);
        return id != null ? id.toString() : "Unknown";
    }

    public static UUID getId(GameProfile profile) {
        if (profile == null) return null;
        if (idMethodHandle != null) {
            try {
                return (UUID) idMethodHandle.invoke(profile);
            } catch (Throwable e) {
                // fall through
            }
        }
        return null;
    }
}
