package xhyrom.nexusblock.structures.nexusConfig;

import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.util.Map;

public class NexusConfigLocation {
    private Double x;
    private Double y;
    private Double z;
    private String world;

    public NexusConfigLocation(Map<String, Object> other) {
        if (!(other.get("location") instanceof Section)) return;
        Section section = (Section) other.get("location");

        this.x = section.getDouble("x");
        this.y = section.getDouble("y");
        this.z = section.getDouble("z");
        this.world = section.getString("world");
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
