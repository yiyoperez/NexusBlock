package xhyrom.nexusblock.structures.nexusConfig;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;

public class NexusLocationConfig {

    private World world;
    private Location location;

    public NexusLocationConfig() {
    }

    public NexusLocationConfig(Map<String, Object> other) {
        if (!(other.get("LOCATION") instanceof Section)) return;
        Section section = (Section) other.get("LOCATION");

        Double x = section.getDouble("X");
        Double y = section.getDouble("Y");
        Double z = section.getDouble("Z");
        String worldName = section.getString("WORLD");

        this.world = Bukkit.getWorld(worldName);
        this.location = new Location(world, x, y, z, 0, 0);
    }

    public World getWorld() {
        return world;
    }

    private void setWorld(World world) {
        this.world = world;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        setWorld(location.getWorld());
    }
}
