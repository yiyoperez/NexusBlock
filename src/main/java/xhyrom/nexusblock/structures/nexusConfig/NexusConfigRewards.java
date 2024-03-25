package xhyrom.nexusblock.structures.nexusConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NexusConfigRewards {

    private final HashMap<Integer, ArrayList<String>> rewards = new HashMap<>();

    public NexusConfigRewards(Map<String, Object> other) {
        if (!(other.get("rewards") instanceof HashMap)) return;

        for (Map.Entry<?, ?> entry : ((HashMap<?, ?>) other.get("rewards")).entrySet()) {
            this.rewards.put(Integer.parseInt(entry.getKey().toString()) - 1, (ArrayList<String>) entry.getValue());
        }
    }

    public List<String> getReward(int s) {
        if (!rewards.containsKey(s)) return Collections.emptyList();

        return rewards.get(s);
    }

    public HashMap<Integer, ArrayList<String>> getRewards() {
        return rewards;
    }
}
