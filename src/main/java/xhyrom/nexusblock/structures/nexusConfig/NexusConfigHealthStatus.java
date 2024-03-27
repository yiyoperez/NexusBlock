package xhyrom.nexusblock.structures.nexusConfig;

import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.util.Map;

public class NexusConfigHealthStatus {

    private int damage;
    private int maximumHealth;

    public NexusConfigHealthStatus(Map<String, Object> other) {
        if (!(other instanceof Section)) return;

        this.damage = 0;
        this.maximumHealth = ((Section) other).getInt("healths");
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void increaseDamage() {
        setDamage(damage++);
    }

    public int getMaximumHealth() {
        return maximumHealth;
    }

    public void setMaximumHealth(int maximumHealth) {
        this.maximumHealth = maximumHealth;
    }
}
