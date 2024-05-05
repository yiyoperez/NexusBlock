package xhyrom.nexusblock.structures.holograms.implementation;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import xhyrom.nexusblock.NexusBlock;

public class HolographicDisplays implements HologramInterface {

    private final HolographicDisplaysAPI API;

    public HolographicDisplays(NexusBlock plugin) {
        this.API = HolographicDisplaysAPI.get(plugin);
    }

    public Object createHologram(Location location, String id, double hologramOffset) {
        Location hdLocation = location.clone().add(0.5, hologramOffset, 0.5);

        return API.createHologram(hdLocation);
    }

    public void insertTextLine(Object hd, Integer line, String desc) {
        ((Hologram) hd).getLines().insertText(line, desc);
    }

    public void insertItemLine(Object hd, Integer line, ItemStack item) {
        ((Hologram) hd).getLines().insertItem(line, item);
    }

    public void editTextLine(Object hd, Integer line, String desc) {
        ((Hologram) hd).getLines().remove(line);
        insertTextLine(hd, line, desc);
    }

    public void editItemLine(Object hd, Integer line, ItemStack item) {
        ((Hologram) hd).getLines().remove(line);
        insertItemLine(hd, line, item);
    }

    @Override
    public void updateLocation(Object hologram, Location location) {
        ((Hologram) hologram).setPosition(location);
    }

    public void deleteHologram(Object hologram) {
        ((Hologram) hologram).delete();
    }
}
