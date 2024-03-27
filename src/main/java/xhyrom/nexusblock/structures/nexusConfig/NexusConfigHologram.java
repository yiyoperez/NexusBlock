package xhyrom.nexusblock.structures.nexusConfig;

import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NexusConfigHologram {
    private List<String> main;
    private List<String> positions;
    private Map<Integer, String> healthVariablesPositions;
    private Map<Integer, String> positionsHologramPositions;

    public NexusConfigHologram(Map<String, Object> other) {
        if (!(other.get("hologram") instanceof Section)) return;

        Section section = (Section) other.get("hologram");

        this.positionsHologramPositions = new HashMap<>();
        this.healthVariablesPositions = new HashMap<>();
        this.main = section.getStringList("main");
        this.positions = section.getStringList("positions");

        main.addAll(positions);

        //TODO: Replace placeholder into position placeholder "{hologram:positions}" or something like that.
    }

    public List<String> getMain() {
        return main;
    }

    public List<String> getPositions() {
        return positions;
    }

    public Map<Integer, String> getHealthVariablesPositions() {
        return healthVariablesPositions;
    }

    public Map<Integer, String> getPositionsHologramPositions() {
        return positionsHologramPositions;
    }
}
