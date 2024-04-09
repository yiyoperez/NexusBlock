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
import java.util.Map;

public class Nexus {

    private final String id;
    private Material material;
    private World world;
    private Location location;
    private Long respawnDelay;
    private NexusConfigRewards rewardsConfig;
    private NexusConfigHologram hologramConfig;
    private NexusConfigHealthStatus healthStatus;
    private Map<String, Integer> destroyers = new HashMap<>();

    public Nexus(
            String id,
            Material material,
            long respawnDelay,
            NexusConfigHologram hologramConfig,
            NexusConfigLocation locationConfig,
            NexusConfigHealthStatus healthStatus,
            NexusConfigRewards rewardsConfig
    ) {
        this.id = id;
        this.material = material;
        this.respawnDelay = respawnDelay;
        this.world = Bukkit.getWorld(locationConfig.getWorld());
        this.location = new Location(world, locationConfig.getX(), locationConfig.getY(), locationConfig.getZ(), 0, 0);
        this.rewardsConfig = rewardsConfig;
        this.hologramConfig = hologramConfig;
        this.healthStatus = healthStatus;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public World getWorld() {
        return world;
    }

    public Location getLocation() {
        return location;
    }

    public Long getRespawnDelay() {
        return respawnDelay;
    }

    public void setRespawnDelay(Long respawnDelay) {
        this.respawnDelay = respawnDelay;
    }

    public NexusConfigRewards getRewardsConfig() {
        return rewardsConfig;
    }

    public NexusConfigHologram getHologramConfig() {
        return hologramConfig;
    }

    public NexusConfigHealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(NexusConfigHealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    public Map<String, Integer> getDestroyers() {
        return destroyers;
    }

}
