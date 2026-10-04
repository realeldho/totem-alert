package com.realeldho.totemalert.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class TotemAlertPositionScreen extends Screen {

    private boolean dragging = false;

    private final TotemAlertConfig config;

    public TotemAlertPositionScreen() {
        super(Component.literal("Set Alert Position"));
        this.config = TotemAlertClient.getConfig();
    }

    @Override
    protected void init() {
        super.init();

        /*
         * DONE BUTTON
         */
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Done"),
                        button -> {
                            TotemAlertClient.getConfig().save();

                            Minecraft.getInstance().setScreen(
                                    new TotemAlertScreen()
                            );
                        }
                ).bounds(
                        this.width / 2 - 50,
                        this.height - 35,
                        100,
                        20
                ).build()
        );
    }

    /*
     * Get the current alert position
     * in actual screen coordinates.
     */
    private double getAlertX() {
        return config.positionX * this.width;
    }

    private double getAlertY() {
        return config.positionY * this.height;
    }

    /*
     * Check whether the mouse is over
     * the alert.
     *
     * The hitbox is intentionally larger
     * than the visible alert to make dragging
     * easier.
     */
    private boolean isMouseOverAlert(
            double mouseX,
            double mouseY
    ) {
        double alertX = getAlertX();
        double alertY = getAlertY();

        double scale = config.scale;

        double halfWidth;
        double halfHeight;

        if (config.textMode) {

            String text = "Totem Not Equipped";

            int textWidth =
                    this.font.width(text);

            halfWidth =
                    (textWidth / 2.0) * scale + 12;

            halfHeight =
                    12 * scale + 12;

        } else {

            halfWidth =
                    16 * scale + 10;

            halfHeight =
                    16 * scale + 10;
        }

        return mouseX >= alertX - halfWidth
                && mouseX <= alertX + halfWidth
                && mouseY >= alertY - halfHeight
                && mouseY <= alertY + halfHeight;
    }

    /*
     * START DRAGGING
     */
    @Override
    public boolean mouseClicked(
            MouseButtonEvent event,
            boolean doubleClick
    ) {
        if (event.button() == 0) {

            double mouseX = event.x();
            double mouseY = event.y();

            if (isMouseOverAlert(
                    mouseX,
                    mouseY
            )) {

                dragging = true;

                return true;
            }
        }

        return super.mouseClicked(
                event,
                doubleClick
        );
    }

    /*
     * DRAGGING
     *
     * IMPORTANT:
     *
     * In Minecraft 1.21.11 these two
     * parameters are MOVEMENT OFFSETS,
     * not absolute mouse coordinates.
     */
    @Override
    public boolean mouseDragged(
            MouseButtonEvent event,
            double offsetX,
            double offsetY
    ) {
        if (dragging) {

            /*
             * Convert the mouse movement into
             * normalized screen coordinates.
             */
            config.positionX +=
                    offsetX / this.width;

            config.positionY +=
                    offsetY / this.height;

            /*
             * Keep the alert on-screen.
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

            return true;
        }

        return super.mouseDragged(
                event,
                offsetX,
                offsetY
        );
    }

    /*
     * STOP DRAGGING
     */
    @Override
    public boolean mouseReleased(
            MouseButtonEvent event
    ) {
        if (event.button() == 0) {

            dragging = false;

            return true;
        }

        return super.mouseReleased(event);
    }

    /*
     * RENDER THE ACTUAL ALERT
     */
    private void renderAlert(
            GuiGraphics graphics
    ) {
        double x = getAlertX();
        double y = getAlertY();

        float scale =
                (float) config.scale;

        graphics.pose().pushMatrix();

        graphics.pose().translate(
                (float) x,
                (float) y
        );

        graphics.pose().scale(
                scale,
                scale
        );

        if (config.textMode) {

            String text =
                    "Totem Not Equipped";

            int textWidth =
                    this.font.width(text);

            graphics.drawString(
                    this.font,
                    text,
                    -textWidth / 2,
                    0,
                    0xFFFF0000,
                    true
            );

        } else {

            graphics.renderItem(
                    Items.TOTEM_OF_UNDYING
                            .getDefaultInstance(),
                    -8,
                    -8
            );

            graphics.pose().pushMatrix();

            graphics.pose().rotate(
                    (float) Math.toRadians(-45)
            );

            /*
             * Thin 2-pixel red slash.
             */
            graphics.fill(
                    -1,
                    -12,
                    1,
                    12,
                    0xFFFF0000
            );

            graphics.pose().popMatrix();
        }

        graphics.pose().popMatrix();
    }

    /*
     * SCREEN RENDER
     */
    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        /*
         * Dark transparent background.
         */
        graphics.fill(
                0,
                0,
                this.width,
                this.height,
                0x66000000
        );

        /*
         * Instructions.
         */
        graphics.drawCenteredString(
                this.font,
                Component.literal(
                        "Drag the alert anywhere on the screen"
                ),
                this.width / 2,
                15,
                0xFFFFFFFF
        );

        graphics.drawCenteredString(
                this.font,
                Component.literal(
                        "Click and hold the alert, then move your mouse"
                ),
                this.width / 2,
                30,
                0xFFAAAAAA
        );

        /*
         * Actual alert.
         */
        renderAlert(graphics);

        /*
         * Buttons.
         */
        super.render(
                graphics,
                mouseX,
                mouseY,
                delta
        );
    }
}