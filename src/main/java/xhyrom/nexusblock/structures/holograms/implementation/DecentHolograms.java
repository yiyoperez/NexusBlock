package xhyrom.nexusblock.structures.holograms.implementation;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DecentHolograms implements HologramInterface {
    public Object createHologram(Location location, String id, double hologramOffset) {
        Location hdLocation = location.clone().add(0.5, hologramOffset, 0.5);

        return DHAPI.createHologram(id, hdLocation, new ArrayList<>());
    }

    public void insertTextLine(Object hd, Integer line, String desc) {
        DHAPI.addHologramLine((Hologram) hd, desc);
    }

    public void insertItemLine(Object hd, Integer line, ItemStack item) {
        DHAPI.addHologramLine((Hologram) hd, "#ICON: " + item.getType().name());
    }

    public void editTextLine(Object hd, Integer line, String desc) {
        DHAPI.setHologramLine((Hologram) hd, line, desc);
    }

    public void editItemLine(Object hd, Integer line, ItemStack item) {
        DHAPI.setHologramLine((Hologram) hd, line, "#ICON: " + item.getType().name());
    }

    @Override
    public void updateLocation(Object hologram, Location location) {
        ((Hologram) hologram).setLocation(location);
        ((Hologram) hologram).updateAll();
    }

    public void deleteHologram(Object hologram) {
        ((Hologram) hologram).delete();
    }
}