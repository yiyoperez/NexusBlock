package xhyrom.nexusblock.structures.nexusConfig;

import java.util.Map;

public class NexusConfigHealthStatus {

    private int damage = 0;
    private int maximumHealth;

    public NexusConfigHealthStatus(Map<String, Object> other) {
        this.maximumHealth = Integer.parseInt(other.get("HEALTH").toString());
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void increaseDamage() {
        this.damage++;
    }

    public int getMaximumHealth() {
        return maximumHealth;
    }

    public void setMaximumHealth(int maximumHealth) {
        this.maximumHealth = maximumHealth;
    }
}
