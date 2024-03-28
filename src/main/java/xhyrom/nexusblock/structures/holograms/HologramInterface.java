package xhyrom.nexusblock.structures.holograms;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface HologramInterface {

    Object createHologram(Location location, String id, double hologramOffset);

    void insertTextLine(Object hologram, Integer line, String desc);

    void insertItemLine(Object hologram, Integer line, ItemStack item);

    void editTextLine(Object hologram, Integer line, String desc);

    void editItemLine(Object hologram, Integer line, ItemStack item);

    void deleteHologram(Object hologram);
}
