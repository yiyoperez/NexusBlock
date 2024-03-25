package xhyrom.nexusblock.structures.nexusConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NexusConfigHologram {
    public String[] main;
    public String[] positions;
    public HashMap<Integer, String> healthVariablesPositions;
    public HashMap<Integer, String> positionsHologramPositions;

    public NexusConfigHologram( Map<String, Object> other) {
        if (!(other.get("hologram") instanceof HashMap)) return;

        this.positionsHologramPositions = new HashMap<>();
        this.healthVariablesPositions = new HashMap<>();
        this.main = ((List<String>) ((HashMap<?, ?>) other.get("hologram")).get("main")).toArray(new String[0]);
        this.positions = ((List<String>) ((HashMap<?, ?>) other.get("hologram")).get("positions")).toArray(new String[0]);

        for (int i = 0; i < this.main.length; i++) {
            String line = this.main[i];
            if (line.contains("{hologram:positions}")) {
                this.main[i] = String.join("\n", this.positions);
            }
        }
    }
}
