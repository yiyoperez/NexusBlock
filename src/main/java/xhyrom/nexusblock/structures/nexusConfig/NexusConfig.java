package xhyrom.nexusblock.structures.nexusConfig;

import org.bukkit.Bukkit;

import java.util.Map;

public class NexusConfig {
    private final String id;
    private final long respawn;
    private final String material;
    private final Double hologramLocation;
    private final NexusConfigHologram hologram;
    private final NexusConfigLocation location;
    private final NexusConfigHealthStatus healths;
    private final NexusConfigRewards rewards;

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

