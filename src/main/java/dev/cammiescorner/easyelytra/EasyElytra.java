package dev.cammiescorner.easyelytra;

import dev.cammiescorner.easyelytra.events.EventListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Random;

public final class EasyElytra extends JavaPlugin {
    public static Random random;

    @Override
    public void onEnable() {
        random = new Random();
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        BukkitScheduler scheduler = this.getServer().getScheduler();
        scheduler.runTaskTimer(this, new RunnableTax(this), 1, 1);
    }

    @Override
    public void onDisable() {

    }
}
