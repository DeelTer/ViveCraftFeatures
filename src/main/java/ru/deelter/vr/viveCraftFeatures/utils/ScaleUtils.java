package ru.deelter.vr.viveCraftFeatures.utils;


import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.jetbrains.annotations.NotNull;

public class ScaleUtils {

	public static final double DEFAULT_INTERACTION_RANGE_BLOCK = 4.5;
	public static final double DEFAULT_INTERACTION_RANGE_ENTITY = 3.0;
	public static final double DEFAULT_STEP_HEIGHT = 0.6;
	public static final double DEFAULT_SCALE = 1.0;
	public static final double DEFAULT_FALL_DISTANCE = 3.0;
	public static final double DEFAULT_SPEED = 0.100000001490116120;

	public static final double SCALE_ADD = 0.05;
	public static final double SCALE_MIN = 0.05;
	public static final double SCALE_MAX = 15;

	public static void resetScale(@NotNull Attributable attributable) {
		AttributeInstance entityInteractionRange = attributable.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
		AttributeInstance blockInteractionRange = attributable.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);
		AttributeInstance fallDistance = attributable.getAttribute(Attribute.SAFE_FALL_DISTANCE);
		AttributeInstance stepHeight = attributable.getAttribute(Attribute.STEP_HEIGHT);
		AttributeInstance scale = attributable.getAttribute(Attribute.SCALE);
		AttributeInstance speed = attributable.getAttribute(Attribute.MOVEMENT_SPEED);

		if (scale != null) {
			scale.setBaseValue(DEFAULT_SCALE);
		}
		if (entityInteractionRange != null) {
			entityInteractionRange.setBaseValue(DEFAULT_INTERACTION_RANGE_ENTITY);
		}
		if (blockInteractionRange != null) {
			blockInteractionRange.setBaseValue(DEFAULT_INTERACTION_RANGE_BLOCK);
		}
		if (stepHeight != null) {
			stepHeight.setBaseValue(DEFAULT_STEP_HEIGHT);
		}
		if (fallDistance != null) {
			fallDistance.setBaseValue(DEFAULT_FALL_DISTANCE);
		}
		if (speed != null) {
			speed.setBaseValue(DEFAULT_SPEED);
		}
	}

	public static void setScale(Attributable attributable, double scaleValue) {
		if (scaleValue == DEFAULT_SCALE) {
			resetScale(attributable);
			return;
		}
		AttributeInstance scale = attributable.getAttribute(Attribute.SCALE);
		if (scale == null) return;

		scale.setBaseValue(scaleValue);

		AttributeInstance entityInteractionRange = attributable.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
		AttributeInstance blockInteractionRange = attributable.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);
		AttributeInstance fallDistance = attributable.getAttribute(Attribute.SAFE_FALL_DISTANCE);
		AttributeInstance stepHeight = attributable.getAttribute(Attribute.STEP_HEIGHT);

		if (entityInteractionRange != null) {
			double newRange = 2 + ((scaleValue - SCALE_MIN) * (30 - 2)) / (SCALE_MAX - SCALE_MIN);
			entityInteractionRange.setBaseValue(newRange);
		}
		if (blockInteractionRange != null) {
			double newRange = 1.5 + ((scaleValue - SCALE_MIN) * (30 - 1.5)) / (SCALE_MAX - SCALE_MIN);
			blockInteractionRange.setBaseValue(newRange);
		}
		if (fallDistance != null) {
			double newValue = 3 + ((scaleValue - SCALE_MIN) * (10 - 3)) / (SCALE_MAX - SCALE_MIN);
			fallDistance.setBaseValue(newValue);
		}
		if (stepHeight != null) {
			double newValue = DEFAULT_STEP_HEIGHT + ((scaleValue - SCALE_MIN) * (10 - DEFAULT_STEP_HEIGHT)) / (SCALE_MAX - SCALE_MIN);
			stepHeight.setBaseValue(newValue);
		}
	}
}
