package xhyrom.nexusblock.structures.nexusConfig;

import xhyrom.nexusblock.structures.nexus.NexusState;

import java.util.Map;

public class NexusConfig {

    private final String id;
    private boolean enabled;
    private long respawnInterval;
    private NexusState state;
    private String blockMaterial;
    private final NexusHologramConfig hologram;
    private final NexusLocationConfig location;
    private final NexusHealthConfig healths;
    private final NexusRewardsConfig rewards;

    public NexusConfig(String id, String material) {
        this.id = id;
        this.enabled = false;
        this.respawnInterval = 10;
        this.blockMaterial = material;
        this.state = NexusState.UNKNOWN;
        this.hologram = new NexusHologramConfig(id);
        this.location = new NexusLocationConfig();
        this.healths = new NexusHealthConfig();
        this.rewards = new NexusRewardsConfig();
    }

    public NexusConfig(Map<String, Object> other) {
        this.id = String.valueOf(other.get("ID").toString());
        this.enabled = Boolean.parseBoolean(other.getOrDefault("ENABLED", false).toString());
        this.respawnInterval = Long.parseLong(other.get("RESPAWN_INTERVAL").toString());
        this.state = NexusState.matchState(other.getOrDefault("STATE", "UNKNOWN").toString());
        this.blockMaterial = other.getOrDefault("MATERIAL", "COAL_ORE").toString();
        this.hologram = new NexusHologramConfig(other);
        this.location = new NexusLocationConfig(other);
        this.healths = new NexusHealthConfig(other);
        this.rewards = new NexusRewardsConfig(other);
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getRespawnInterval() {
        return respawnInterval;
    }

    public void setRespawnInterval(long respawnInterval) {
        this.respawnInterval = respawnInterval;
    }

    public NexusState getState() {
        return state;
    }

    public void setState(NexusState state) {
        this.state = state;
    }

    public String getBlockMaterial() {
        return blockMaterial;
    }

    public void setBlockMaterial(String blockMaterial) {
        this.blockMaterial = blockMaterial;
    }

    public NexusHologramConfig getHologram() {
        return hologram;
    }

    public NexusLocationConfig getLocation() {
        return location;
    }

    public NexusHealthConfig getHealths() {
        return healths;
    }

    public NexusRewardsConfig getRewards() {
        return rewards;
    }
}

