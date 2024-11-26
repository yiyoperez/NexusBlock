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

    public void insertTextLine(Object hologram, Integer line, String desc) {
        DHAPI.addHologramLine((Hologram) hologram, desc);
    }

    public void insertItemLine(Object hologram, Integer line, ItemStack item) {
        DHAPI.addHologramLine((Hologram) hologram, "#ICON: " + item.getType().name());
    }

    public void editTextLine(Object hologram, Integer line, String desc) {
        DHAPI.setHologramLine((Hologram) hologram, line, desc);
    }

    public void editItemLine(Object hologram, Integer line, ItemStack item) {
        DHAPI.setHologramLine((Hologram) hologram, line, "#ICON: " + item.getType().name());
    }

    @Override
    public void updateLocation(Object hologram, Location location) {
        DHAPI.moveHologram((Hologram) hologram, location);
        ((Hologram) hologram).updateAll();
    }

    public void deleteHologram(Object hologram) {
        ((Hologram) hologram).delete();
    }
}