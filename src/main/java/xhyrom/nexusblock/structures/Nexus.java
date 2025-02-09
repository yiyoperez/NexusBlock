package xhyrom.nexusblock.structures;

import org.bukkit.Material;
import xhyrom.nexusblock.structures.nexus.NexusState;
import xhyrom.nexusblock.structures.nexusConfig.NexusConfig;
import xhyrom.nexusblock.structures.nexusConfig.NexusHealthConfig;
import xhyrom.nexusblock.structures.nexusConfig.NexusHologramConfig;
import xhyrom.nexusblock.structures.nexusConfig.NexusLocationConfig;
import xhyrom.nexusblock.structures.nexusConfig.NexusRewardsConfig;

import java.util.HashMap;
import java.util.Map;

public final class Nexus {

    private final String id;
    private Material material;
    private boolean enabled;
    private Long respawnDelay;
    private NexusState state;
    private final NexusRewardsConfig rewardsConfig;
    private final NexusHologramConfig hologramConfig;
    private final NexusLocationConfig locationConfig;
    private final NexusHealthConfig healthStatus;
    private final Map<String, Integer> destroyers = new HashMap<>();

    public Nexus(String id, Material material) {
        this.id = id;
        this.material = material;

        NexusConfig nexusConfig = new NexusConfig(id, material.name());
        this.enabled = nexusConfig.isEnabled();
        this.respawnDelay = nexusConfig.getRespawnInterval();
        this.rewardsConfig = new NexusRewardsConfig();
        this.state = nexusConfig.getState();
        this.hologramConfig = new NexusHologramConfig(id);
        this.locationConfig = new NexusLocationConfig();
        this.healthStatus = new NexusHealthConfig();
    }

    public Nexus(String id, Material material, NexusConfig nexusConfig) {
        this.id = id;
        this.enabled = nexusConfig.isEnabled();
        this.material = material;
        this.respawnDelay = nexusConfig.getRespawnInterval();
        this.state = nexusConfig.getState();
        this.rewardsConfig = nexusConfig.getRewards();
        this.hologramConfig = nexusConfig.getHologram();
        this.locationConfig = nexusConfig.getLocation();
        this.healthStatus = nexusConfig.getHealths();
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public NexusState getState() {
        return state;
    }

    public void setState(NexusState state) {
        this.state = state;
    }

    public NexusRewardsConfig getRewardsConfig() {
        return rewardsConfig;
    }

    public NexusHologramConfig getHologramConfig() {
        return hologramConfig;
    }

    public NexusLocationConfig getLocationConfig() {
        return locationConfig;
    }

    public NexusHealthConfig getHealthStatus() {
        return healthStatus;
    }

    public Map<String, Integer> getDestroyers() {
        return destroyers;
    }

}
