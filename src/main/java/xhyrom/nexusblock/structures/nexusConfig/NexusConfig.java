package xhyrom.nexusblock.structures.nexusConfig;

import java.util.Map;

public class NexusConfig {
    private String id;
    private long respawn;
    private String material;
    private Double hologramLocation;
    private NexusConfigHologram hologram;
    private NexusConfigLocation location;
    private NexusConfigHealthStatus healths;
    private NexusConfigRewards rewards;

    public NexusConfig(Map<String, Object> other) {
        this.id = String.valueOf(other.get("id").toString());
        this.respawn = Long.parseLong(other.get("respawn").toString());
        this.material = other.get("material").toString();
        this.hologramLocation = Double.parseDouble(other.get("hologramLocation").toString());
        this.hologram = new NexusConfigHologram(other);
        this.location = new NexusConfigLocation(other);
        this.healths = new NexusConfigHealthStatus(other);
        this.rewards = new NexusConfigRewards(other);
    }

    public String getId() {
        return id;
    }

    public long getRespawn() {
        return respawn;
    }

    public String getMaterial() {
        return material;
    }

    public Double getHologramLocation() {
        return hologramLocation;
    }

    public NexusConfigHologram getHologram() {
        return hologram;
    }

    public NexusConfigLocation getLocation() {
        return location;
    }

    public NexusConfigHealthStatus getHealths() {
        return healths;
    }

    public NexusConfigRewards getRewards() {
        return rewards;
    }
}

