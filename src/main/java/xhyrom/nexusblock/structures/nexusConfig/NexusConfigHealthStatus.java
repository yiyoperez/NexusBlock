package xhyrom.nexusblock.structures.nexusConfig;

import java.util.Map;

public class NexusConfigHealthStatus {

    private int damage;
    private int maximumHealth;

    public NexusConfigHealthStatus(Map<String, Object> other) {

        this.damage = 0;
        this.maximumHealth = Integer.parseInt(other.get("healths").toString());
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
