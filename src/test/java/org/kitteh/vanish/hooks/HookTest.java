package org.kitteh.vanish.hooks;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.HookManager.HookType;

public class HookTest {
    @Test
    public void hookConstructors() {
        HookType[] hooks = HookManager.HookType.values();
        for (HookType hook : hooks) {
            boolean hasDesiredConstructor = false;
            for (Constructor<?> constructor : hook.get().getDeclaredConstructors()) {
                if (Modifier.isPrivate(constructor.getModifiers()) || Modifier.isProtected(constructor.getModifiers())) {
                    continue; // Must be package-private or public
                }
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0].equals(VanishPlugin.class)) {
                    hasDesiredConstructor = true;
                }
            }
            Assert.assertTrue("Hook " + hook + " does not have a VanishPlugin constructor", hasDesiredConstructor);
        }
        System.out.println("Found " + hooks.length + " hooks");
    }
}