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

package org.kitteh.vanish.metrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * <p>
 * The metrics class obtains data about a plugin and submits statistics about it to the metrics backend.
 * </p>
 * <p>
 * Public methods provided by this class:
 * </p>
 * <code>
 * Graph createGraph(String name); <br/>
 * void addCustomData(Metrics.Plotter plotter); <br/>
 * void start(); <br/>
 * </code>
 */
public class Metrics {

    /**
     * Represents a custom graph on the website
     */
    public static class Graph {

        /**
         * The graph's name, alphanumeric and spaces only :)
         * If it does not comply to the above when submitted, it is rejected
         */
        private final String name;

        /**
         * The set of plotters that are contained within this graph
         */
        private final Set<Plotter> plotters = new LinkedHashSet<Plotter>();

        private Graph(final String name) {
            this.name = name;
        }

        /**
         * Add a plotter to the graph, which will be used to plot entries
         * 
         * @param plotter
         */
        public void addPlotter(final Plotter plotter) {
            this.plotters.add(plotter);
        }

        @Override
        public boolean equals(final Object object) {
            if (!(object instanceof Graph)) {
                return false;
            }

            final Graph graph = (Graph) object;
            return graph.name.equals(this.name);
        }

        /**
         * Gets the graph's name
         * 
         * @return
         */
        public String getName() {
            return this.name;
        }

        /**
         * Gets an <b>unmodifiable</b> set of the plotter objects in the graph
         * 
         * @return
         */
        public Set<Plotter> getPlotters() {
            return Collections.unmodifiableSet(this.plotters);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        /**
         * Remove a plotter from the graph
         * 
         * @param plotter
         */
        public void removePlotter(final Plotter plotter) {
            this.plotters.remove(plotter);
        }

    }

    /**
     * Interface used to collect custom data for a plugin
     */
    public static abstract class Plotter {

        /**
         * The plot's name
         */
        private final String name;

        /**
         * Construct a plotter with the default plot name
         */
        public Plotter() {
            this("Default");
        }

        /**
         * Construct a plotter with a specific plot name
         * 
         * @param name
         */
        public Plotter(final String name) {
            this.name = name;
        }

        @Override
        public boolean equals(final Object object) {
            if (!(object instanceof Plotter)) {
                return false;
            }

            final Plotter plotter = (Plotter) object;
            return plotter.name.equals(this.name) && (plotter.getValue() == this.getValue());
        }

        /**
         * Get the column name for the plotted point
         * 
         * @return the plotted point's column name
         */
        public String getColumnName() {
            return this.name;
        }

        /**
         * Get the current value for the plotted point
         * 
         * @return
         */
        public abstract int getValue();

        @Override
        public int hashCode() {
            return this.getColumnName().hashCode() + this.getValue();
        }

        /**
         * Called after the website graphs have been updated
         */
        public void reset() {
        }

    }

    /**
     * The current revision number
     */
    private final static int REVISION = 5;

    /**
     * The base url of the metrics domain
     */
    private static final String BASE_URL = "http://stats.kitteh.org";

    /**
     * The url used to report a server's status
     */
    private static final String REPORT_URL = "/report/%s";

    /**
     * The file where guid and opt out is stored in
     */
    private static final String CONFIG_FILE = "plugins/PluginMetrics/config.yml";

    /**
     * The separator to use for custom data. This MUST NOT change unless you are hosting your own
     * version of metrics and want to change it.
     */
    private static final String CUSTOM_DATA_SEPARATOR = "~~";

    /**
     * Interval of time to ping (in minutes)
     */
    private static final int PING_INTERVAL = 10;

    /**
     * Encode text as UTF-8
     * 
     * @param text
     * @return
     */
    private static String encode(final String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, "UTF-8");
    }

    /**
     * <p>
     * Encode a key/value data pair to be used in a HTTP post request. This INCLUDES a & so the first key/value pair MUST be included manually, e.g:
     * </p>
     * <code>
     * StringBuffer data = new StringBuffer();
     * data.append(encode("guid")).append('=').append(encode(guid));
     * encodeDataPair(data, "version", description.getVersion());
     * </code>
     * 
     * @param buffer
     * @param key
     * @param value
     * @return
     */
    private static void encodeDataPair(final StringBuilder buffer, final String key, final String value) throws UnsupportedEncodingException {
        buffer.append('&').append(Metrics.encode(key)).append('=').append(Metrics.encode(value));
    }

    /**
     * The plugin this metrics submits for
     */
    private final Plugin plugin;

    /**
     * All of the custom graphs to submit to metrics
     */
    private final Set<Graph> graphs = Collections.synchronizedSet(new HashSet<Graph>());

    /**
     * The default graph, used for addCustomData when you don't want a specific graph
     */
    private final Graph defaultGraph = new Graph("Default");

    /**
     * The plugin configuration file
     */
    private final YamlConfiguration configuration;

    /**
     * The plugin configuration file
     */
    private final File configurationFile;

    /**
     * Unique server id
     */
    private final String guid;

    /**
     * Lock for synchronization
     */
    private final Object optOutLock = new Object();

    /**
     * Id of the scheduled task
     */
    private volatile int taskId = -1;

    public Metrics(final Plugin plugin) throws IOException {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }

        this.plugin = plugin;

        // load the config
        this.configurationFile = new File(Metrics.CONFIG_FILE);
        this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);

        // add some defaults
        this.configuration.addDefault("opt-out", false);
        this.configuration.addDefault("guid", UUID.randomUUID().toString());

        // Do we need to create the file?
        if (this.configuration.get("guid", null) == null) {
            this.configuration.options().header("http://stats.kitteh.org").copyDefaults(true);
            this.configuration.save(this.configurationFile);
        }

        // Load the guid then
        this.guid = this.configuration.getString("guid");
    }

    /**
     * Adds a custom data plotter to the default graph
     * 
     * @param plotter
     */
    public void addCustomData(final Plotter plotter) {
        if (plotter == null) {
            throw new IllegalArgumentException("Plotter cannot be null");
        }

        // Add the plotter to the graph o/
        this.defaultGraph.addPlotter(plotter);

        // Ensure the default graph is included in the submitted graphs
        this.graphs.add(this.defaultGraph);
    }

    /**
     * Construct and create a Graph that can be used to separate specific plotters to their own graphs
     * on the metrics website. Plotters can be added to the graph object returned.
     * 
     * @param name
     * @return Graph object created. Will never return NULL under normal circumstances unless bad parameters are given
     */
    public Graph createGraph(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Graph name cannot be null");
        }

        // Construct the graph object
        final Graph graph = new Graph(name);

        // Now we can add our graph
        this.graphs.add(graph);

        // and return back
        return graph;
    }

    /**
     * Disables metrics for the server by setting "opt-out" to true in the config file and canceling the metrics task.
     * 
     * @throws IOException
     */
    public void disable() throws IOException {
        // This has to be synchronized or it can collide with the check in the task.
        synchronized (this.optOutLock) {
            // Check if the server owner has already set opt-out, if not, set it.
            if (!this.isOptOut()) {
                this.configuration.set("opt-out", true);
                this.configuration.save(this.configurationFile);
            }

            // Disable Task, if it is running
            if (this.taskId > 0) {
                this.plugin.getServer().getScheduler().cancelTask(this.taskId);
                this.taskId = -1;
            }
        }
    }

    /**
     * Enables metrics for the server by setting "opt-out" to false in the config file and starting the metrics task.
     * 
     * @throws IOException
     */
    public void enable() throws IOException {
        // This has to be synchronized or it can collide with the check in the task.
        synchronized (this.optOutLock) {
            // Check if the server owner has already set opt-out, if not, set it.
            if (this.isOptOut()) {
                this.configuration.set("opt-out", false);
                this.configuration.save(this.configurationFile);
            }

            // Enable Task, if it is not running
            if (this.taskId < 0) {
                this.start();
            }
        }
    }

    /**
     * Has the server owner denied plugin metrics?
     * 
     * @return
     */
    public boolean isOptOut() {
        synchronized (this.optOutLock) {
            try {
                // Reload the metrics file
                this.configuration.load(Metrics.CONFIG_FILE);
            } catch (final IOException ex) {
                //Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
                return true;
            } catch (final InvalidConfigurationException ex) {
                //Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
                return true;
            }
            return this.configuration.getBoolean("opt-out", false);
        }
    }

    /**
     * Start measuring statistics. This will immediately create an async repeating task as the plugin and send
     * the initial data to the metrics backend, and then after that it will post in increments of
     * PING_INTERVAL * 1200 ticks.
     * 
     * @return True if statistics measuring is running, otherwise false.
     */
    public boolean start() {
        synchronized (this.optOutLock) {
            // Did we opt out?
            if (this.isOptOut()) {
                return false;
            }

            // Is metrics already running?
            if (this.taskId >= 0) {
                return true;
            }

            // Begin hitting the server with glorious data
            this.taskId = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {

                private boolean firstPost = true;

                @Override
                public void run() {
                    try {
                        // This has to be synchronized or it can collide with the disable method.
                        synchronized (Metrics.this.optOutLock) {
                            // Disable Task, if it is running and the server owner decided to opt-out
                            if (Metrics.this.isOptOut() && (Metrics.this.taskId > 0)) {
                                Metrics.this.plugin.getServer().getScheduler().cancelTask(Metrics.this.taskId);
                                Metrics.this.taskId = -1;
                            }
                        }

                        // We use the inverse of firstPost because if it is the first time we are posting,
                        // it is not a interval ping, so it evaluates to FALSE
                        // Each time thereafter it will evaluate to TRUE, i.e PING!
                        Metrics.this.postPlugin(!this.firstPost);

                        // After the first post we set firstPost to false
                        // Each post thereafter will be a ping
                        this.firstPost = false;
                    } catch (final IOException e) {
                        //Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
                    }
                }
            }, 0, Metrics.PING_INTERVAL * 1200).getTaskId();

            return true;
        }
    }

    /**
     * Generic method that posts a plugin to the metrics website
     */
    private void postPlugin(final boolean isPing) throws IOException {
        // Construct the post data
        final StringBuilder data = new StringBuilder();
        data.append(Metrics.encode("guid")).append('=').append(Metrics.encode(this.guid));
        Metrics.encodeDataPair(data, "version", "${vnp-version}");
        Metrics.encodeDataPair(data, "server", Bukkit.getVersion());
        Metrics.encodeDataPair(data, "players", Integer.toString(Bukkit.getServer().getOnlinePlayers().size()));
        Metrics.encodeDataPair(data, "revision", String.valueOf(Metrics.REVISION));

        // If we're pinging, append it
        if (isPing) {
            Metrics.encodeDataPair(data, "ping", "true");
        }

        // Acquire a lock on the graphs, which lets us make the assumption we also lock everything
        // inside of the graph (e.g plotters)
        synchronized (this.graphs) {
            final Iterator<Graph> iter = this.graphs.iterator();

            while (iter.hasNext()) {
                final Graph graph = iter.next();

                for (final Plotter plotter : graph.getPlotters()) {
                    // The key name to send to the metrics server
                    // The format is C-GRAPHNAME-PLOTTERNAME where separator - is defined at the top
                    // Legacy (R4) submitters use the format Custom%s, or CustomPLOTTERNAME
                    final String key = String.format("C%s%s%s%s", Metrics.CUSTOM_DATA_SEPARATOR, graph.getName(), Metrics.CUSTOM_DATA_SEPARATOR, plotter.getColumnName());

                    // The value to send, which for the foreseeable future is just the string
                    // value of plotter.getValue()
                    final String value = Integer.toString(plotter.getValue());

                    // Add it to the http post data :)
                    Metrics.encodeDataPair(data, key, value);
                }
            }
        }

        // Create the url
        final URL url = new URL(Metrics.BASE_URL + String.format(Metrics.REPORT_URL, Metrics.encode("VanishNoPacket")));

        // Connect to the website
        URLConnection connection;

        connection = url.openConnection();

        connection.setDoOutput(true);

        // Write the data
        final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data.toString());
        writer.flush();

        // Now read the response
        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        final String response = reader.readLine();

        // close resources
        writer.close();
        reader.close();

        if ((response == null) || response.startsWith("ERR")) {
            throw new IOException(response); //Throw the exception
        } else {
            // Is this the first update this hour?
            if (response.contains("OK This is your first update this hour")) {
                synchronized (this.graphs) {
                    final Iterator<Graph> iter = this.graphs.iterator();

                    while (iter.hasNext()) {
                        final Graph graph = iter.next();

                        for (final Plotter plotter : graph.getPlotters()) {
                            plotter.reset();
                        }
                    }
                }
            }
        }
    }

}