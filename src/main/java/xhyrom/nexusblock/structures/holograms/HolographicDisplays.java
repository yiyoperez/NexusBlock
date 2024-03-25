package xhyrom.nexusblock.structures.holograms;

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

    public Object createHologram(Location location, int id, double hologramLocation) {
        Location hdLocation = location.clone().add(0.5, 0, 0.5);
        hdLocation.setY(hologramLocation);

        return API.createHologram(hdLocation);
    }

    public void insertTextLine(Object hd, Integer line, String desc) {
        ((Hologram) hd).getLines().insertText(line, desc);
        return;
    }

    public void insertItemLine(Object hd, Integer line, ItemStack item) {
        ((Hologram) hd).getLines().insertItem(line, item);
        return;
    }

    public void editTextLine(Object hd, Integer line, String desc, Boolean save) {
        ((Hologram) hd).getLines().remove(line);
        insertTextLine(hd, line, desc);
        return;
    }

    public void editItemLine(Object hd, Integer line, ItemStack item, Boolean save) {
        ((Hologram) hd).getLines().remove(line);
        insertItemLine(hd, line, item);
        return;
    }

    public void deleteHologram(Object hd) {
        ((Hologram) hd).delete();
        return;
    }
}
