/*
 * VanishNoPacket
 * Copyright (C) 2011-2022 Matt Baxter
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
package org.kitteh.vanish;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.Callable;

public final class VanishCheck implements Callable<Object> {
    private final VanishManager manager;
    private final String name;

    public VanishCheck(@NonNull VanishManager manager, @NonNull String name) {
        this.manager = manager;
        this.name = name;
    }

    @Override
    public @NonNull Object call() {
        try {
            return this.manager.isVanished(this.name);
        } catch (final Exception e) {
            return false;
        }
    }
}
