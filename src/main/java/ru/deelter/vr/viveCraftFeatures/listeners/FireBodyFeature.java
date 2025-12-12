package ru.deelter.vr.viveCraftFeatures.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vivecraft.ViveMain;
import org.vivecraft.VivePlayer;
import org.vivecraft.api.VRAPI;
import org.vivecraft.api.data.VRBodyPart;
import org.vivecraft.api.data.VRBodyPartData;
import org.vivecraft.api.data.VRPose;
import ru.deelter.vr.viveCraftFeatures.ViveCraftFeatures;
import ru.deelter.vr.viveCraftFeatures.data.HapticPulseData;

import java.util.List;


public class FireBodyFeature implements Listener {

	private static final List<Material> FIRE_BLOCKS = List.of(Material.LAVA, Material.FIRE);
	private final @NotNull ViveCraftFeatures plugin;
	private final HapticPulseData hapticPulseData;

	public FireBodyFeature(@NotNull ViveCraftFeatures plugin) {
		this.plugin = plugin;
		FileConfiguration config = plugin.getConfig();
		hapticPulseData = new HapticPulseData(config.getConfigurationSection("features.fire.haptic"));

		if (config.getBoolean("features.fire.enabled")) {
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
			runTaskTimer();
		}
	}

	private void runTaskTimer() {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			if (ViveMain.VIVE_PLAYERS.isEmpty()) return;

			ViveMain.VIVE_PLAYERS.values().forEach(vivePlayer -> {

				VRBodyPart bodyPart = getBodyPartInFire(vivePlayer);
				if (bodyPart == null) return;

				Player player = vivePlayer.player;
				player.setFireTicks(100);

				VRAPI.instance().sendHapticPulse(
						player,
						bodyPart,
						hapticPulseData.getDuration(),
						hapticPulseData.getFrequency(),
						hapticPulseData.getAmplitude(),
						hapticPulseData.getDelay());
			});
		}, 0, 5);
	}

	private @Nullable VRBodyPart getBodyPartInFire(@NotNull VivePlayer vivePlayer) {
		VRPose pose = vivePlayer.asVRPose();
		if (pose == null) return null;

		for (VRBodyPart bodyPart : VRBodyPart.values()) {
			VRBodyPartData bodyPartData = pose.getBodyPartData(bodyPart);
			if (bodyPartData == null) continue;

			Player player = vivePlayer.player;
			if (player.isDead() || !player.isOnline()) {
				return null;
			}
			Block block = bodyPartData.getPos()
					.toLocation(player.getWorld())
					.getBlock();
			if (FIRE_BLOCKS.contains(block.getType())) {
				return bodyPart;
			}
		}
		return null;
	}
}
