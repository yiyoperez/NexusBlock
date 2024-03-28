package xhyrom.nexusblock.structures.nexusConfig;

import java.util.List;
import java.util.Map;

public class NexusConfigHologram {

    private Object hologramInterface;
    private double hologramOffset;
    private final List<String> hologramStrings;

    public NexusConfigHologram(Map<String, Object> other) {
        this.hologramStrings = (List<String>) other.get("HOLOGRAM");
        this.hologramOffset = Double.parseDouble(other.get("HOLOGRAM-HEIGHT").toString());
    }

    public Object getHologramInterface() {
        return hologramInterface;
    }

    public void setHologramInterface(Object hologramInterface) {
        this.hologramInterface = hologramInterface;
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
}
