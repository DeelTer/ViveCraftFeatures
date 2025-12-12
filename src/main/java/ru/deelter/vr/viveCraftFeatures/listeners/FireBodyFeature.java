package ru.deelter.vr.viveCraftFeatures.listeners;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.vivecraft.api.VRAPI;
import org.vivecraft.api.data.VRBodyPart;
import org.vivecraft.api.data.VRBodyPartData;
import org.vivecraft.api.data.VRPose;
import ru.deelter.vr.viveCraftFeatures.ViveCraftFeatures;
import ru.deelter.vr.viveCraftFeatures.ViveItems;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class EntityGrabbingFeature implements Listener {

	private static final TeleportFlag[] TELEPORT_FLAGS = new TeleportFlag[]{
			TeleportFlag.Relative.VELOCITY_ROTATION,
			TeleportFlag.Relative.VELOCITY_X, TeleportFlag.Relative.VELOCITY_Y,
			TeleportFlag.Relative.VELOCITY_Z
	};
	private final Map<UUID, Entity> grabbed = new HashMap<>();
	private final @NotNull ViveCraftFeatures plugin;


	public EntityGrabbingFeature(@NotNull ViveCraftFeatures plugin) {
		this.plugin = plugin;
		if (plugin.getConfig().getBoolean("features.grabbing")) {
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
			runGrabTask();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGrabEntity(@NotNull PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		if (!ViveItems.isClimbingClaws(player.getInventory().getItemInMainHand())) return;

		VRPose pose = VRAPI.instance().getVRPose(player);
		if (pose == null) return;

		Entity entity = event.getRightClicked();

		if (entity instanceof Vehicle) return;
		if (grabbed.containsValue(entity)) return;
		if (!player.canSee(entity)) return;

		UUID playerId = player.getUniqueId();

		if (grabbed.remove(playerId) == null) {
			grabbed.put(playerId, entity);
		}
	}

	private void runGrabTask() {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			if (grabbed.isEmpty()) return;

			grabbed.entrySet().removeIf(entry -> {

				Player player = Bukkit.getPlayer(entry.getKey());

				if (player == null || !player.isOnline() || player.isDead()) {
					return true;
				}
				VRPose pose = VRAPI.instance().getVRPose(player);
				if (pose == null) return true;

				VRBodyPartData controller = pose.getBodyPartData(VRBodyPart.MAIN_HAND);
				if (controller == null) return true;

				Location controllerLocation = controller.getPos().toLocation(player.getWorld());
				AttributeInstance attribute = player.getAttribute(Attribute.SCALE);

				int distance = 3;
				double grabRadius = 0.2;

				if (attribute != null && attribute.getValue() > 5) {
					grabRadius = 1.0;
					distance = 6;
				}

				Entity entity = entry.getValue();
				boolean inTarget = entity.equals(player.getTargetEntity(distance));
				boolean inSquare = controllerLocation.getNearbyEntities(grabRadius, grabRadius, grabRadius)
						.stream()
						.anyMatch(entity1 -> entity1.equals(entity));

				if (inTarget || inSquare) {
					entity.teleport(controllerLocation, TELEPORT_FLAGS);
					return false;
				}

				Vector controllerDirection = controller.getPos();
				Vector throwDirection = controllerDirection.clone().normalize().multiply(1.3);
				entity.setVelocity(throwDirection);

				VRAPI.instance().sendHapticPulse(player, VRBodyPart.MAIN_HAND, 0.3f);
				return true;
			});

		}, 0, 1);
	}
}
