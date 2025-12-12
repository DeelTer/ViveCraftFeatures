package ru.deelter.vr.viveCraftFeatures.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vivecraft.api.VRAPI;
import org.vivecraft.api.data.VRBodyPart;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class HapticPulseData {

	private VRBodyPart bodyPart = null;
	private float duration = 1f, frequency = 160.0F, amplitude = 1.0F,	delay = 0.0F;

	public HapticPulseData(@Nullable ConfigurationSection config) {
		if (config == null) {
			bodyPart = null;
		} else {
			bodyPart = config.getString("part") == null ? null : VRBodyPart.valueOf(Objects.requireNonNull(config.getString("part")).toUpperCase());
			duration = (float) config.getDouble("duration", 1f);
			frequency = (float) config.getDouble("frequency", 160.0F);
			amplitude = (float) config.getDouble("amplitude", 1.0F);
			delay = (float) config.getDouble("delay", 0.0F);
		}
	}

	public void send(Player player) {
		VRAPI.instance().sendHapticPulse(player, bodyPart, duration, frequency, amplitude, delay);
	}

	@Contract(" -> new")
	public static @NotNull HapticPulseData simple() {
		return new HapticPulseData();
	}
}
