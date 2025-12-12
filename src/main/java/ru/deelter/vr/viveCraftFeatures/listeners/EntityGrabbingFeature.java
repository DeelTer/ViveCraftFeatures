package ru.deelter.vivecraft.listeners;

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
import ru.deelter.vivecraft.ViveCraftPaper;
import ru.deelter.vivecraft.data.BodyData;
import ru.deelter.vivecraft.data.VivePlayer;
import ru.deelter.vivecraft.items.ViveItems;

import java.util.HashMap;
import java.util.Map;


public class FeatureGrabbingMechanic implements Listener {

	private static final TeleportFlag[] TELEPORT_FLAGS = new TeleportFlag[]{
			TeleportFlag.Relative.VELOCITY_ROTATION,
			TeleportFlag.Relative.VELOCITY_X, TeleportFlag.Relative.VELOCITY_Y,
			TeleportFlag.Relative.VELOCITY_Z
	};
	private final ViveCraftPaper plugin;
	private final Map<VivePlayer, Entity> grabbed = new HashMap<>();


	public FeatureGrabbingMechanic(@NotNull ViveCraftPaper plugin) {
		this.plugin = plugin;

		if (plugin.getViveConfig().isFeaturesGrabbingEnabled()) {
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
			runGrabTask();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGrabEntity(@NotNull PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		if (!ViveItems.isClimbingClaws(player.getInventory().getItemInMainHand())) return;

		VivePlayer vivePlayer = VivePlayer.of(player);
		if (vivePlayer == null || !vivePlayer.isInHeadset()) return;

		Entity entity = event.getRightClicked();

		if (entity instanceof Vehicle) return;
		if (grabbed.containsValue(entity)) return;
		if (!player.canSee(entity)) return;

		if (grabbed.remove(vivePlayer) == null) {
			grabbed.put(vivePlayer, entity);
		}
	}

	private void runGrabTask() {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			if (grabbed.isEmpty()) return;

			grabbed.entrySet().removeIf(entry -> {
				VivePlayer vivePlayer = entry.getKey();
				Player player = vivePlayer.getPlayer();
				Entity entity = entry.getValue();

				BodyData controller = vivePlayer.getMainController();
				if (controller == null) return true;

				Location controllerLocation = controller.getPosition();
				AttributeInstance attribute = player.getAttribute(Attribute.SCALE);

				int distance = 3;
				double grabRadius = 0.2;

				if (attribute != null && attribute.getValue() > 5) {
					grabRadius = 1.0;
					distance = 6;
				}

				boolean inTarget = entity.equals(player.getTargetEntity(distance));
				boolean inSquare = controllerLocation.getNearbyEntities(grabRadius, grabRadius, grabRadius)
						.stream()
						.anyMatch(entity1 -> entity1.equals(entity));

				if (inTarget || inSquare) {
					entity.teleport(controllerLocation, TELEPORT_FLAGS);
//					entity.setVelocity(controllerLocation.toVector());
					return false;
				}

				Vector controllerDirection = controller.getDirection();
				Vector throwDirection = controllerDirection.clone().normalize().multiply(1.3);
				entity.setVelocity(throwDirection);
				return true;
			});

		}, 0, 1);
	}
}
