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

import java.util.logging.Logger;

public final class Debuggle {
    private final Logger logger;
    private static Debuggle instance = null;

    public static void log(@NonNull String message) {
        if (Debuggle.instance != null) {
            Debuggle.instance.logger.info("[DEBUG] " + message);
        }
    }

    static void itsGoTime(@NonNull VanishPlugin plugin) {
        Debuggle.instance = new Debuggle(plugin);
    }

    static void nah() {
        Debuggle.instance = null;
    }

    private Debuggle(@NonNull VanishPlugin plugin) {
        this.logger = plugin.getLogger();
        this.logger.info("Debug enabled. Disable in config.yml");
    }
}
