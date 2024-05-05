package xhyrom.nexusblock.structures.nexusConfig;

import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NexusRewardsConfig {

    private final List<String> destroyerRewards = new ArrayList<>();
    private final Map<Integer, List<String>> rewards = new HashMap<>();

    // Default reward values constructor.
    public NexusRewardsConfig() {
        destroyerRewards.addAll(Arrays.asList(
                "give %player% golden_apple 1",
                "say %player% destroyed block!",
                "say %player% broke the nexus %destroys% times."
        ));

        rewards.put(1, Arrays.asList("give %player% apple 1", "say %player% broke the nexus %destroys% times."));
        rewards.put(2, Arrays.asList("give %player% book 1", "say %player% broke the nexus %destroys% times."));
    }

    public NexusRewardsConfig(Map<String, Object> other) {
        if (!(other.get("REWARDS") instanceof Section)) return;

        Section section = (Section) other.get("REWARDS");
        if (section == null) return;

        // Add destroyer rewards
        destroyerRewards.addAll(section.getStringList("DESTROYER"));

        Section destroyersSection = section.getSection("DESTROYERS");
        if (destroyersSection.isEmpty(false)) return;

        // Get destroyers and their corresponding rewards
        destroyersSection
                .getStringRouteMappedValues(false)
                .forEach((entry, value) -> {
                    if (entry instanceof String && value instanceof List) {
                        try {
                            int topValue = Integer.parseInt(entry);
                            List<String> rewardCommands = (List<String>) value;
                            this.rewards.put(topValue, rewardCommands);
                        } catch (NumberFormatException | ClassCastException e) {
                            e.printStackTrace();
                        }
                    }
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
