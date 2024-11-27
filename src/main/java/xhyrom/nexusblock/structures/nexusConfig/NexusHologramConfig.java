package xhyrom.nexusblock.structures.nexusConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NexusHologramConfig {

    private Object hologram;
    private double hologramOffset;
    private List<String> hologramStrings;

    // Hologram default values.
    public NexusHologramConfig(String nexusName) {
        this.hologramOffset = 5D;
        this.hologramStrings = new ArrayList<>();
        hologramStrings.addAll(Arrays.asList(
                "%material%",
                "&b&l" + nexusName.toUpperCase() + " NEXUS",
                "&cDestroy to get reward",
                "",
                "&a1 &7%top_1% &8| &7%value_1%",
                "&a2 &7%top_2% &8| &7%value_2%",
                "&a3 &7%top_3% &8| &7%value_3%",
                "",
                "&c%health%&8/&c%maxHealth%"
        ));
    }

    public NexusHologramConfig(Map<String, Object> other) {
        this.hologramStrings = (List<String>) other.get("HOLOGRAM");
        this.hologramOffset = Double.parseDouble(other.get("HOLOGRAM-HEIGHT").toString());
    }

    public Object getHologram() {
        return hologram;
    }

    public void setHologram(Object hologram) {
        this.hologram = hologram;
    }

    public double getHologramOffset() {
        return hologramOffset;
    }

    public void setHologramOffset(double hologramOffset) {
        this.hologramOffset = hologramOffset;
    }

    public List<String> getHologramStrings() {
        return hologramStrings;
    }

    public void setHologramStrings(List<String> hologramStrings) {
        this.hologramStrings = hologramStrings;
    }
}
