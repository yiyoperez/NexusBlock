package xhyrom.nexusblock.structures.nexusConfig;

import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.util.Map;

public class NexusConfigLocation {

    private Double x;
    private Double y;
    private Double z;
    private String world;

    public NexusConfigLocation(Map<String, Object> other) {
        if (!(other.get("LOCATION") instanceof Section)) return;
        Section section = (Section) other.get("LOCATION");

        this.x = section.getDouble("X");
        this.y = section.getDouble("Y");
        this.z = section.getDouble("Z");
        this.world = section.getString("WORLD");
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }
}
