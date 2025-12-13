package ru.deelter.vr.viveCraftFeatures.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.vr.viveCraftFeatures.ViveCraftFeatures;
import ru.deelter.vr.viveCraftFeatures.utils.ScaleUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScaleCommand implements CommandExecutor, TabCompleter {

	private static final double SCALE_MIN = 0.0;
	private static final double SCALE_MAX = 100.0;

	public ScaleCommand(@NotNull ViveCraftFeatures plugin) {
		PluginCommand command = plugin.getCommand("scale");
		if (command == null) return;

		command.setExecutor(this);
		command.setTabCompleter(this);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("This command can only be used by players.");
			return true;
		}
		if (args.length != 1) {
			ScaleUtils.resetScale(player);
			return true;
		}
		double input;
		try {
			input = Double.parseDouble(args[0]);
		} catch (NumberFormatException e) {
			player.sendMessage("Invalid number. Use a value between 0 and 100.");
			return true;
		}

		if (input < SCALE_MIN || input > SCALE_MAX) {
			player.sendMessage("Value out of range. Use 0.0–100.");
			return true;
		}
		if (input <= 0.0) {
			ScaleUtils.resetScale(player);
			return true;
		}
		ScaleUtils.setScale(player, input);
		return true;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
		if (args.length == 1) {
			List<String> suggestions = new ArrayList<>();
			suggestions.add("0.5");
			suggestions.add("1");
			suggestions.add("25");
			suggestions.add("50");
			suggestions.add("75");
			suggestions.add("100");
			return suggestions;
		}
		return Collections.emptyList();
	}
}
