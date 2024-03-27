package xhyrom.nexusblock.structures;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfigHealthStatus;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfigHologram;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfigLocation;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfigRewards;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Nexus {

    private String id;
    private Material material;
    private World world;
    private Location location;
    private Long respawnDelay;
    private NexusConfigRewards rewardsConfig;
    private NexusConfigHologram hologramConfig;
    private NexusConfigHealthStatus healthStatus;
    private CopyOnWriteArrayList<String> destroyers = new CopyOnWriteArrayList<>();
    private HashMap<String, Integer> destroys = new HashMap<>();

    public Nexus(
            String id,
            Material material,
            NexusConfigHologram hologramConfig,
            NexusConfigLocation locationConfig,
            long respawnDelay,
            NexusConfigHealthStatus healthStatus,
            double hologramLocation,
            NexusConfigRewards rewardsConfig,
            CopyOnWriteArrayList<String> destroyers,
            HashMap<String, Integer> destroys,
            int currentHealth
    ) {
        this.id = id;
        this.material = material;
        this.world = Bukkit.getWorld(locationConfig.getWorld());
        this.location = new Location(world, locationConfig.getX(), locationConfig.getY(), locationConfig.getZ(), 0, 0);
        this.hologramConfig = hologramConfig;
        this.respawnDelay = respawnDelay;

        this.healthStatus = healthStatus;
        healthStatus.setDamage(currentHealth);

        this.rewardsConfig = rewardsConfig;
        this.destroyers = destroyers;
        this.destroys = destroys;

        this.location.getBlock().setType(this.material);
    }

    public CopyOnWriteArrayList<String> getDestroyers() {
        return this.destroyers;
    }

    public HashMap<String, Integer> getDestroys() {
        HashMap<String, Integer> tempDestroys = new HashMap<>();

        for (int i = 0; i < this.hologramConfig.getPositionsHologramPositions().size(); i++) {
            if (this.destroyers.size() <= i) break;

            String playerName = this.destroyers.get(i);
            if (playerName == null) continue;

            tempDestroys.put(playerName, this.destroys.get(playerName));
        }

        return tempDestroys;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Long getRespawnDelay() {
        return respawnDelay;
    }

    public void setRespawnDelay(Long respawnDelay) {
        this.respawnDelay = respawnDelay;
    }

    public Location getLocation() {
        return location;
    }

    public NexusConfigHologram getHologramConfig() {
        return hologramConfig;
    }

    public NexusConfigRewards getRewardsConfig() {
        return rewardsConfig;
    }

    public NexusConfigHealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(NexusConfigHealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

}
