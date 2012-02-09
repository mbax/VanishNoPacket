package org.kitteh.vanish.metrics;

/*
 * Copyright 2011 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Metrics {

    public static abstract class Plotter {

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Plotter)) {
                return false;
            }

            final Plotter plotter = (Plotter) object;
            return plotter.getColumnName().equals(this.getColumnName()) && (plotter.getValue() == this.getValue());
        }

        public abstract String getColumnName();

        public abstract int getValue();

        @Override
        public int hashCode() {
            return this.getColumnName().hashCode() + this.getValue();
        }

        public void reset() {
        }
    }

    private final static int REVISION = 4;

    private static final String BASE_URL = "http://metrics.griefcraft.com";

    private static final String REPORT_URL = "/report/%s";

    private static final String CONFIG_FILE = "plugins/PluginMetrics/config.yml";

    private final static int PING_INTERVAL = 10;

    private static String encode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, "UTF-8");
    }

    private final Map<Plugin, Set<Plotter>> customData = Collections.synchronizedMap(new HashMap<Plugin, Set<Plotter>>());

    private final YamlConfiguration configuration;

    private final String guid;

    public Metrics() throws IOException {
        final File file = new File(Metrics.CONFIG_FILE);
        this.configuration = YamlConfiguration.loadConfiguration(file);
        this.configuration.addDefault("opt-out", false);
        this.configuration.addDefault("guid", UUID.randomUUID().toString());
        if (this.configuration.get("guid", null) == null) {
            this.configuration.options().header("http://metrics.griefcraft.com").copyDefaults(true);
            this.configuration.save(file);
        }
        this.guid = this.configuration.getString("guid");
    }

    public void addCustomData(Plugin plugin, Plotter plotter) {
        Set<Plotter> plotters = this.customData.get(plugin);
        if (plotters == null) {
            plotters = Collections.synchronizedSet(new LinkedHashSet<Plotter>());
            this.customData.put(plugin, plotters);
        }
        plotters.add(plotter);
    }

    public void beginMeasuringPlugin(final Plugin plugin) throws IOException {
        if (this.configuration.getBoolean("opt-out", false)) {
            return;
        }
        this.postPlugin(plugin, false);
        plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    Metrics.this.postPlugin(plugin, true);
                } catch (final IOException e) {
                    System.out.println("[Metrics] " + e.getMessage());
                }
            }
        }, Metrics.PING_INTERVAL * 1200, Metrics.PING_INTERVAL * 1200);
    }

    private void postPlugin(Plugin plugin, boolean isPing) throws IOException {
        String response = "ERR No response";
        String data = Metrics.encode("guid") + '=' + Metrics.encode(this.guid) + '&' + Metrics.encode("version") + '=' + Metrics.encode(plugin.getDescription().getVersion()) + '&' + Metrics.encode("server") + '=' + Metrics.encode(Bukkit.getVersion()) + '&' + Metrics.encode("players") + '=' + Metrics.encode(String.valueOf(Bukkit.getServer().getOnlinePlayers().length)) + '&' + Metrics.encode("revision") + '=' + Metrics.encode(Metrics.REVISION + "");
        if (isPing) {
            data += '&' + Metrics.encode("ping") + '=' + Metrics.encode("true");
        }
        final Set<Plotter> plotters = this.customData.get(plugin);
        if (plotters != null) {
            for (final Plotter plotter : plotters) {
                data += "&" + Metrics.encode("Custom" + plotter.getColumnName()) + "=" + Metrics.encode(Integer.toString(plotter.getValue()));
            }
        }
        final URL url = new URL(Metrics.BASE_URL + String.format(Metrics.REPORT_URL, plugin.getDescription().getName()));
        final URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        response = reader.readLine();
        writer.close();
        reader.close();
        if (response.startsWith("ERR")) {
            throw new IOException(response);
        } else {
            if (response.contains("OK This is your first update this hour")) {
                if (plotters != null) {
                    for (final Plotter plotter : plotters) {
                        plotter.reset();
                    }
                }
            }
        }
    }

}