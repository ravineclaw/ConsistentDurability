package club.mcpvp.consistentdurability.listeners;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PlayerItemDamageListener implements Listener {
    public static final NamespacedKey DURABILITY_KEY = new NamespacedKey("consistentdurability", "durability");

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        int originalDamage = event.getOriginalDamage();
        if (originalDamage <= 0) return;

        ItemStack item = event.getItem();
        boolean isElytra = item.getType() != Material.ELYTRA;
        if (!Tag.ITEMS_ENCHANTABLE_ARMOR.isTagged(item.getType()) && !isElytra) return;

        int level = item.getEnchantmentLevel(Enchantment.UNBREAKING);
        if (level <= 0) return;

        Integer maxDurability = item.getData(DataComponentTypes.MAX_DAMAGE);
        if (maxDurability == null) return;

        int currentRawDamage = item.getDataOrDefault(DataComponentTypes.DAMAGE, 0);
        int currentRawDurability = maxDurability - currentRawDamage;
        double damage = isElytra ? calculateToolDamage(originalDamage, level) : calculateArmorDamage(originalDamage, level);
        PersistentDataContainerView pdcView = item.getPersistentDataContainer();
        Double durability = pdcView.get(DURABILITY_KEY, PersistentDataType.DOUBLE);
        if (durability == null) {
            durability = (double) (maxDurability - currentRawDamage);
        }

        durability -= damage;

        if (durability < 1E-7) {
            event.setDamage(maxDurability);
        } else {
            event.setDamage(currentRawDurability - (int) Math.ceil(durability));

            double finalDurability = durability;
            item.editPersistentDataContainer(pdc -> pdc.set(DURABILITY_KEY, PersistentDataType.DOUBLE, finalDurability));
        }
    }

    private static double calculateToolDamage(int originalDamage, int unbreakingLevel) {
        return originalDamage * (1.0d / (unbreakingLevel + 1));
    }

    private static double calculateArmorDamage(int originalDamage, int unbreakingLevel) {
        return originalDamage * (0.6d + 0.4d / (unbreakingLevel + 1));
    }
}
