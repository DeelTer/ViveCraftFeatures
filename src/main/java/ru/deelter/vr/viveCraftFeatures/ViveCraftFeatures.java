package ru.deelter.vr.viveCraftFeatures;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.vr.viveCraftFeatures.commands.ScaleCommand;
import ru.deelter.vr.viveCraftFeatures.listeners.EntityGrabbingFeature;
import ru.deelter.vr.viveCraftFeatures.listeners.FireBodyFeature;

public final class ViveCraftFeatures extends JavaPlugin {

	@Override
	public void onEnable() {
		saveDefaultConfig();

		new EntityGrabbingFeature(this);
		new FireBodyFeature(this);

		new ScaleCommand(this);
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
