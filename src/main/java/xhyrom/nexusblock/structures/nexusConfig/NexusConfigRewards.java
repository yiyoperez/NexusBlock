package xhyrom.nexusblock.structures.nexusConfig;

import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NexusConfigRewards {

    private final List<String> destroyerRewards = new ArrayList<>();
    private final Map<Integer, List<String>> rewards = new HashMap<>();

    public NexusConfigRewards(Map<String, Object> other) {
        if (!(other.get("REWARDS") instanceof Section)) return;

        Section section = (Section) other.get("REWARDS");
        destroyerRewards.addAll(section.getStringList("DESTROYER"));

        section
                .getSection("DESTROYERS")
                .getStringRouteMappedValues(false)
                .forEach((entry, value) -> {

                    int topValue;
                    try {
                        topValue = Integer.parseInt(entry);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return;
                    }

                    List<String> rewardCommands = new ArrayList<>();
                    try {
                        rewardCommands = (List<String>) value;
                    } catch (ClassCastException ce) {
                        ce.printStackTrace();
                    }

                    this.rewards.put(topValue, rewardCommands);
                });

    }

    public List<String> getReward(int s) {
        if (!rewards.containsKey(s)) return Collections.emptyList();

        return rewards.get(s);
    }

    public Map<Integer, List<String>> getRewards() {
        return rewards;
    }

    public List<String> getDestroyerRewards() {
        return destroyerRewards;
    }
}
