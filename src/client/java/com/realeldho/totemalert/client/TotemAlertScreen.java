package com.realeldho.totemalert.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TotemAlertScreen extends Screen {

    private Button textButton;
    private Button iconButton;
    private Button positionButton;
    private Button doneButton;

    private AlertSizeSlider sizeSlider;

    public TotemAlertScreen() {
        super(Component.literal("Totem Alert"));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;

        /*
         * ─────────────────────────────
         * ALERT STYLE
         * ─────────────────────────────
         */

        textButton = Button.builder(
                Component.literal(
                        TotemAlertClient.getConfig().textMode
                                ? "✓ Text"
                                : "Text"
                ),
                button -> {
                    TotemAlertClient.getConfig().textMode = true;
                    TotemAlertClient.getConfig().save();
                    updateButtons();
                }
        ).bounds(
                centerX - 105,
                80,
                100,
                20
        ).build();

        iconButton = Button.builder(
                Component.literal(
                        !TotemAlertClient.getConfig().textMode
                                ? "✓ Icon"
                                : "Icon"
                ),
                button -> {
                    TotemAlertClient.getConfig().textMode = false;
                    TotemAlertClient.getConfig().save();
                    updateButtons();
                }
        ).bounds(
                centerX + 5,
                80,
                100,
                20
        ).build();

        this.addRenderableWidget(textButton);
        this.addRenderableWidget(iconButton);

        /*
         * ─────────────────────────────
         * SIZE SLIDER
         * ─────────────────────────────
         */

        sizeSlider = new AlertSizeSlider(
                centerX - 105,
                125,
                210,
                20,
                TotemAlertClient.getConfig().scale
        );

        this.addRenderableWidget(sizeSlider);

        /*
         * ─────────────────────────────
         * POSITION
         * ─────────────────────────────
         */

        positionButton = Button.builder(
                Component.literal("Change Position"),
                button -> {
                    this.minecraft.setScreen(
                            new TotemAlertPositionScreen()
                    );
                }
        ).bounds(
                centerX - 105,
                180,
                210,
                20
        ).build();

        this.addRenderableWidget(positionButton);

        /*
         * ─────────────────────────────
         * DONE
         * ─────────────────────────────
         */

        doneButton = Button.builder(
                Component.literal("Done"),
                button -> {

                    /*
                     * Save one final time when
                     * leaving the settings screen.
                     */
                    TotemAlertClient.getConfig().save();

                    this.minecraft.setScreen(null);
                }
        ).bounds(
                centerX - 50,
                220,
                100,
                20
        ).build();

        this.addRenderableWidget(doneButton);
    }

    /*
     * Update Text/Icon button labels.
     */
    private void updateButtons() {

        textButton.setMessage(
                Component.literal(
                        TotemAlertClient.getConfig().textMode
                                ? "✓ Text"
                                : "Text"
                )
        );

        iconButton.setMessage(
                Component.literal(
                        !TotemAlertClient.getConfig().textMode
                                ? "✓ Icon"
                                : "Icon"
                )
        );
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        /*
         * Background
         */
        graphics.fill(
                0,
                0,
                this.width,
                this.height,
                0xCC101010
        );

        /*
         * ─────────────────────────────
         * TITLE
         * ─────────────────────────────
         */

        graphics.drawCenteredString(
                this.font,
                this.title,
                this.width / 2,
                30,
                0xFFFFFFFF
        );

        /*
         * ─────────────────────────────
         * STATUS
         * ─────────────────────────────
         */

        boolean enabled =
                TotemAlertClient.getConfig().enabled;

        Component statusText =
                Component.literal(
                        enabled
                                ? "Status: ON"
                                : "Status: OFF"
                );

        graphics.drawCenteredString(
                this.font,
                statusText,
                this.width / 2,
                52,
                enabled
                        ? 0xFF55FF55
                        : 0xFFFF5555
        );

        /*
         * ─────────────────────────────
         * STYLE LABEL
         * ─────────────────────────────
         */

        graphics.drawCenteredString(
                this.font,
                Component.literal("Alert Style"),
                this.width / 2,
                65,
                0xFFAAAAAA
        );

        /*
         * ─────────────────────────────
         * SIZE LABEL
         * ─────────────────────────────
         */

        graphics.drawCenteredString(
                this.font,
                Component.literal("Alert Size"),
                this.width / 2,
                110,
                0xFFAAAAAA
        );

        /*
         * ─────────────────────────────
         * POSITION LABEL
         * ─────────────────────────────
         */

        graphics.drawCenteredString(
                this.font,
                Component.literal("Alert Position"),
                this.width / 2,
                165,
                0xFFAAAAAA
        );

        /*
         * Render buttons and slider.
         */
        super.render(
                graphics,
                mouseX,
                mouseY,
                delta
        );
    }

    /*
     * ─────────────────────────────
     * SIZE SLIDER
     * ─────────────────────────────
     *
     * Range:
     * 50% → 200%
     */
    private static class AlertSizeSlider
            extends AbstractSliderButton {

        public AlertSizeSlider(
                int x,
                int y,
                int width,
                int height,
                double currentScale
        ) {
            super(
                    x,
                    y,
                    width,
                    height,
                    Component.empty(),
                    (currentScale - 0.5) / 1.5
            );

            updateMessage();
        }

        @Override
        protected void updateMessage() {

            int percentage =
                    (int) Math.round(
                            50 + (this.value * 150)
                    );

            this.setMessage(
                    Component.literal(
                            "Size: "
                                    + percentage
                                    + "%"
                    )
            );
        }

        @Override
        protected void applyValue() {

            double newScale =
                    0.5 + (this.value * 1.5);

            TotemAlertClient.getConfig().scale =
                    newScale;

            /*
             * Save immediately.
             */
            TotemAlertClient.getConfig().save();
        }
    }
}