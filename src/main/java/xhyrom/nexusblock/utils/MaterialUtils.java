package xhyrom.nexusblock.utils;

import org.bukkit.Material;

public class MaterialUtils {

    public static boolean isValidMaterial(String materialName) {
        Material material = Material.matchMaterial(materialName);
        return material != null;
    }
}
