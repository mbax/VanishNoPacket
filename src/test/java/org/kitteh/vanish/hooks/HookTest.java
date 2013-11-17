package org.kitteh.vanish.hooks;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.HookManager.HookType;

public class HookTest {
    @Test
    public void hookConstructors() {
        for (HookType hook : HookManager.HookType.values()) {
            boolean hasDesiredConstructor = false;
            for (Constructor<?> constructor : hook.get().getConstructors()) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0].equals(VanishPlugin.class)) {
                    hasDesiredConstructor = true;
                }
            }
            Assert.assertTrue("Hook " + hook + " does not have a VanishPlugin constructor", hasDesiredConstructor);
        }
    }

    @Test
    public void hookUniqueness() {
        Map<Class<?>, HookType> classes = new HashMap<Class<?>, HookType>();
        for (HookType hook : HookManager.HookType.values()) {
            Assert.assertTrue("Hooks " + hook + " and " + classes.get(hook.get()) + " have the same class", !classes.containsKey(hook.get()));
            classes.put(hook.get(), hook);
        }
    }
}