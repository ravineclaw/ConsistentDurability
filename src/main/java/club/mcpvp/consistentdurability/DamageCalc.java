package club.mcpvp.consistentdurability;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public abstract class DamageCalc {
    public static double calculateToolDamage(double originalDamage, int unbreakingLevel) {
        return originalDamage * (1.0d / (unbreakingLevel + 1));
    }

    public static double calculateArmorDamage(double originalDamage, int unbreakingLevel) {
        return originalDamage * (0.6d + 0.4d / (unbreakingLevel + 1));
    }

    public static double getCurrentDurability(ItemStack item, int maxDurability) {
        Double currentDurability = item.getPersistentDataContainer().get(ConsistentDurability.DURABILITY_KEY, PersistentDataType.DOUBLE);
        if (currentDurability == null) {
            int currentDamage = item.getDataOrDefault(DataComponentTypes.DAMAGE, 0);
            currentDurability = (double) (maxDurability - currentDamage);
        }

        return currentDurability;
    }
}
