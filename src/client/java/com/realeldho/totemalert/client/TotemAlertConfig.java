package com.realeldho.totemalert.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class TotemAlertConfig {

    /*
     * Default alert position.
     *
     * 0.0 = left / top
     * 1.0 = right / bottom
     */
    public double positionX = 0.5;
    public double positionY = 0.6;

    /*
     * Alert size.
     *
     * 1.0 = 100%
     */
    public double scale = 1.0;

    /*
     * true  = Text
     * false = Icon
     */
    public boolean textMode = true;

    /*
     * Whether Totem Alert is enabled.
     */
    public boolean enabled = true;

    /*
     * Config file location.
     */
    private static final Path CONFIG_FILE =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("totem-alert.json");

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    /*
     * Save the current configuration.
     */
    public void save() {

        try {
            Files.createDirectories(
                    CONFIG_FILE.getParent()
            );

            try (Writer writer =
                         Files.newBufferedWriter(
                                 CONFIG_FILE
                         )) {

                GSON.toJson(
                        this,
                        writer
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "[Totem Alert] Failed to save config:"
            );

            e.printStackTrace();
        }
    }

    /*
     * Load configuration from disk.
     *
     * If the file doesn't exist or cannot be
     * loaded, use the default settings.
     */
    public static TotemAlertConfig load() {

        if (!Files.exists(CONFIG_FILE)) {
            TotemAlertConfig config =
                    new TotemAlertConfig();

            config.save();

            return config;
        }

        try (Reader reader =
                     Files.newBufferedReader(
                             CONFIG_FILE
                     )) {

            TotemAlertConfig config =
                    GSON.fromJson(
                            reader,
                            TotemAlertConfig.class
                    );

            if (config == null) {
                config =
                        new TotemAlertConfig();
            }

            /*
             * Safety checks in case the config
             * contains invalid values.
             */
            config.positionX =
                    Math.max(
                            0.0,
                            Math.min(
                                    1.0,
                                    config.positionX
                            )
                    );

            config.positionY =
                    Math.max(
                            0.0,
                            Math.min(
                                    1.0,
                                    config.positionY
                            )
                    );

            config.scale =
                    Math.max(
                            0.5,
                            Math.min(
                                    2.0,
                                    config.scale
                            )
                    );

            return config;

        } catch (Exception e) {

            System.err.println(
                    "[Totem Alert] Failed to load config. Using defaults."
            );

            e.printStackTrace();

            return new TotemAlertConfig();
        }
    }
}