package com.realeldho.totemalert.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

public class TotemAlertClient implements ClientModInitializer {

	/*
	 * Load saved configuration when the mod starts.
	 */
	private static final TotemAlertConfig CONFIG =
			TotemAlertConfig.load();

	/*
	 * Current enabled state.
	 */
	private static boolean enabled =
			CONFIG.enabled;

	/*
	 * Toggle key.
	 *
	 * Default: Right Shift
	 */
	private static final KeyMapping TOGGLE_KEY =
			KeyBindingHelper.registerKeyBinding(
					new KeyMapping(
							"key.totem-alert.toggle",
							InputConstants.Type.KEYSYM,
							GLFW.GLFW_KEY_RIGHT_SHIFT,
							KeyMapping.Category.MISC
					)
			);

	/*
	 * Settings key.
	 *
	 * Default: O
	 */
	private static final KeyMapping SETTINGS_KEY =
			KeyBindingHelper.registerKeyBinding(
					new KeyMapping(
							"key.totem-alert.settings",
							InputConstants.Type.KEYSYM,
							GLFW.GLFW_KEY_O,
							KeyMapping.Category.MISC
					)
			);

	@Override
	public void onInitializeClient() {

		/*
		 * Keybind handling.
		 */
		ClientTickEvents.END_CLIENT_TICK.register(
				client -> {

					/*
					 * Toggle Totem Alert.
					 */
					while (TOGGLE_KEY.consumeClick()) {

						enabled = !enabled;

						CONFIG.enabled =
								enabled;

						CONFIG.save();

						System.out.println(
								"[Totem Alert] "
										+ (
										enabled
												? "Enabled"
												: "Disabled"
								)
						);
					}

					/*
					 * Open settings.
					 */
					while (SETTINGS_KEY.consumeClick()) {

						client.setScreen(
								new TotemAlertScreen()
						);
					}
				}
		);

		/*
		 * Register the HUD element.
		 */
		HudElementRegistry.attachElementAfter(
				VanillaHudElements.CHAT,
				Identifier.fromNamespaceAndPath(
						"totem-alert",
						"totem_alert"
				),
				TotemAlertClient::renderAlert
		);
	}

	/*
	 * Give other Totem Alert screens access
	 * to the configuration.
	 */
	public static TotemAlertConfig getConfig() {
		return CONFIG;
	}

	/*
	 * Render the alert.
	 */
	private static void renderAlert(
			GuiGraphics graphics,
			net.minecraft.client.DeltaTracker deltaTracker
	) {
		Minecraft client =
				Minecraft.getInstance();

		/*
		 * Don't render if disabled or
		 * there is no player.
		 */
		if (!enabled
				|| client.player == null) {
			return;
		}

		/*
		 * Don't show the warning when
		 * a Totem is already equipped.
		 */
		if (client.player
				.getOffhandItem()
				.is(Items.TOTEM_OF_UNDYING)) {
			return;
		}

		/*
		 * Calculate position from the
		 * saved normalized coordinates.
		 */
		float x =
				(float) (
						graphics.guiWidth()
								* CONFIG.positionX
				);

		float y =
				(float) (
						graphics.guiHeight()
								* CONFIG.positionY
				);

		float scale =
				(float) CONFIG.scale;

		graphics.pose().pushMatrix();

		graphics.pose().translate(
				x,
				y
		);

		graphics.pose().scale(
				scale,
				scale
		);

		/*
		 * TEXT MODE
		 */
		if (CONFIG.textMode) {

			String text =
					"Totem Not Equipped";

			int textWidth =
					client.font.width(text);

			graphics.drawString(
					client.font,
					text,
					-textWidth / 2,
					0,
					0xFFFF0000,
					true
			);
		}

		/*
		 * ICON MODE
		 */
		else {

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
			 * Thin red slash.
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
}