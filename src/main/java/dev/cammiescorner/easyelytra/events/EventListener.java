package dev.cammiescorner.easyelytra.events;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {
	@EventHandler
	public void cancelRiptideFlying(PlayerInteractEvent event) {
		if(event.getAction().isRightClick()) {
			Player player = event.getPlayer();
			ItemStack stack = event.getItem();

			if(player.isGliding() && stack != null && stack.containsEnchantment(Enchantment.RIPTIDE) && player.getCooldown(stack.getType()) <= 0) {
				event.getPlayer().sendMessage(Component.text("Riptide ").color(TextColor.color(0x55ffff)).append(Component.text("is not available while gliding! Hold Shift while gliding to fly.").color(TextColor.color(0xff5555))));
				player.setCooldown(stack.getType(), 20);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void cancelFireworkBoost(PlayerElytraBoostEvent event) {
		Player player = event.getPlayer();
		ItemStack stack = event.getItemStack();

		if(player.isGliding()) {
			event.getPlayer().sendMessage(Component.text("Firework Rockets ").color(TextColor.color(0x55ffff)).append(Component.text("are not available while gliding! Hold Shift while gliding to fly.").color(TextColor.color(0xff5555))));
			player.setCooldown(stack.getType(), 20);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void momentumBasedDamage(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player attacker && attacker.isGliding()) {
			double speed = attacker.getVelocity().length() * 20;

			if(speed >= 45)
				event.setDamage(event.getDamage() * 2);
			else if(speed >= 25)
				event.setDamage(event.getDamage() * 1.5);
		}
	}
}
