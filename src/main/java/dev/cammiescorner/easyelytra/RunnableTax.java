package dev.cammiescorner.easyelytra;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class RunnableTax implements Runnable {
	private final EasyElytra instance;

	public RunnableTax(EasyElytra instance) {
		this.instance = instance;
	}

	@Override
	public void run() {
		for(Player player : instance.getServer().getOnlinePlayers()) {
			if(player.isGliding()) {
				World world = player.getWorld();
				Block block = world.getBlockAt(player.getLocation());
				int i = 0;

				while(world.getBlockAt(player.getLocation().subtract(0, i, 0)).getType() == Material.AIR)
					i++;

				if(player.isSneaking() && player.getFoodLevel() > 6) {
					Vector velocity = player.getVelocity();
					Vector rotation = player.getLocation().getDirection();
					AttributeInstance armorAttribute = player.getAttribute(Attribute.GENERIC_ARMOR);
					PotionEffect speedEffect = player.getPotionEffect(PotionEffectType.SPEED);
					float speed = 0.01F;
					float acceleration = 1.5F;
					float armorAmount = armorAttribute != null ? (float) armorAttribute.getValue() : 0;
					float armorModifier = Math.max(1F, (armorAmount / 20F) * 5);
					float speedModifier = speedEffect != null ? 1 + (0.3F * (1 + speedEffect.getAmplifier())) : 1F;
					float modifiedSpeed = (player.getLocation().getYaw() < -75 && player.getLocation().getYaw() > -105 ? speed * 2F : speed) * speedModifier / armorModifier;

					player.setVelocity(velocity.add(new Vector(rotation.getX() * modifiedSpeed + (rotation.getX() * acceleration - velocity.getX()) * modifiedSpeed, rotation.getY() * modifiedSpeed + (rotation.getY() * acceleration - velocity.getY()) * modifiedSpeed, rotation.getZ() * modifiedSpeed + (rotation.getZ() * acceleration - velocity.getZ()) * modifiedSpeed)));

					if(player.getGameMode() != GameMode.CREATIVE)
						player.setExhaustion(player.getExhaustion() + 0.06667F);
				}

				Block blockUnderPlayer = world.getBlockAt(player.getLocation().subtract(0, i, 0));
				double distance = player.getLocation().getY() - blockUnderPlayer.getY();
				double velocity = player.getVelocity().length() * 20;
				double gravity = 7.13;
				int color = velocity < gravity && Math.sin(player.getTicksLived() / (Math.PI * 2) * 3) < 0 ? 0xaa0000 : velocity < 30 ? 0xff5555 : velocity < 45 ? 0xffff55 : 0x55ff55;

				if(block.getType() == Material.AIR) {
					spawnAirParticles(player);

					if(blockUnderPlayer.getType() == Material.WATER && distance < 5)
						spawnWaterParticles(player, distance);
				}

				player.sendActionBar(Component.text(String.format("%.2f", velocity) + " m/s").color(TextColor.color(color)));
			}
		}
	}

	public void spawnAirParticles(Player player) {

	}

	public void spawnWaterParticles(Player player, double distance) {
		double factor = player.getVelocity().length() / distance;
		double floor = Math.floor(10 * factor);

		if(EasyElytra.random.nextDouble() < 10 * factor - floor)
			player.getWorld().spawnParticle(
					Particle.WATER_WAKE,
					player.getLocation().getX(),
					player.getLocation().getY() + 1 - distance,
					player.getLocation().getZ(),
					1, 0.25 / factor, 0.1 / factor, 0.25 / factor, 0.1
			);

		if(floor > 0)
			player.getWorld().spawnParticle(
					Particle.WATER_WAKE,
					player.getLocation().getX(), player.getLocation().getY() + 1 - distance, player.getLocation().getZ(),
					(int) (floor), 0.25 / factor, 0.1 / factor, 0.25 / factor, 0.1
			);
	}
}
