package xhyrom.nexusblock.structures.nexusConfig;

import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NexusConfigRewards {

    private final HashMap<Integer, ArrayList<String>> rewards = new HashMap<>();

    public NexusConfigRewards(Map<String, Object> other) {
        if (!(other.get("REWARDS") instanceof Section)) return;

        Section section = (Section) other.get("REWARDS");
        // TODO: accept any kind of value.
//        section.getStringRouteMappedValues(false).forEach((entry, value) ->
//                this.rewards.put(Integer.parseInt(entry) - 1, (ArrayList<String>) value));

    }

    public List<String> getReward(int s) {
        if (!rewards.containsKey(s)) return Collections.emptyList();

        return rewards.get(s);
    }

    public HashMap<Integer, ArrayList<String>> getRewards() {
        return rewards;
    }
}
