package xhyrom.nexusblock.structures.nexusConfig;

import java.util.HashMap;
import java.util.Map;

public class NexusConfigHealthStatus {

    private int damage;
    private int maximumHealth;

    public NexusConfigHealthStatus(Map<String, Object> other) {
        Object healths = other.get("healths");
        if (healths instanceof HashMap) {
            healths = Integer.parseInt(((HashMap<?, ?>) other.get("healths")).get("maximumHealth").toString());
        }

        this.damage = 0;
        this.maximumHealth = Integer.parseInt(healths.toString());
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
