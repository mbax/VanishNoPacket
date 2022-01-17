/*
 * VanishNoPacket
 * Copyright (C) 2011-2021 Matt Baxter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.kitteh.vanish.hooks;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.HookManager.HookType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HookTest {
    @Test
    public void hookConstructors() {
        for (HookType hook : HookManager.HookType.values()) {
            boolean hasDesiredConstructor = false;
            for (Constructor<?> constructor : hook.get().getConstructors()) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0].equals(VanishPlugin.class)) {
                    hasDesiredConstructor = true;
                    break;
                }
            }
            assertTrue(hasDesiredConstructor, "Hook " + hook + " does not have a VanishPlugin constructor");
        }
    }

    @Test
    public void hookUniqueness() {
        Map<Class<?>, HookType> classes = new HashMap<>();
        for (HookType hook : HookManager.HookType.values()) {
            assertFalse(classes.containsKey(hook.get()), "Hooks " + hook + " and " + classes.get(hook.get()) + " have the same class");
            classes.put(hook.get(), hook);
        }
    }
}