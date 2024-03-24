package xhyrom.nexusblock.structures.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DecentHolograms implements HologramInterface {
    public Object createHologram(Location location, int id, double hologramLocation) {
        Location hdLocation = location.clone().add(0.5, 0, 0.5);
        hdLocation.setY(hologramLocation);

        return DHAPI.createHologram(String.valueOf(id), hdLocation, new ArrayList<>());
    }

    public void insertTextLine(Object hd, Integer line, String desc) {
        DHAPI.addHologramLine((Hologram) hd, desc);
    }

    public void insertItemLine(Object hd, Integer line, ItemStack item) {
        DHAPI.addHologramLine((Hologram) hd, "#ICON: " + item.getType().name());
    }

    public void editTextLine(Object hd, Integer line, String desc, Boolean save) {
        DHAPI.setHologramLine((Hologram) hd, line, desc);
    }

    public void editItemLine(Object hd, Integer line, ItemStack item, Boolean save) {
        DHAPI.setHologramLine((Hologram) hd, line, "#ICON: " + item.getType().name());
    }

    public void deleteHologram(Object hd) {
        ((Hologram) hd).delete();
    }
}