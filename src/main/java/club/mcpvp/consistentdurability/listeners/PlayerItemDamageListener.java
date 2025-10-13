package club.mcpvp.consistentdurability.listeners;

import club.mcpvp.consistentdurability.ConsistentDurability;
import club.mcpvp.consistentdurability.DamageCalc;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerItemDamageListener implements Listener {
    private final ConsistentDurability plugin;

    public PlayerItemDamageListener(ConsistentDurability plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        // Damage calculation for elytra is handled in the main class
        boolean isElytra = item.getType() == Material.ELYTRA;
        if (isElytra && plugin.getConfig().getBoolean("elytra-durability-fix")) {
            event.setCancelled(true);
            return;
        }

        if (!plugin.getConfig().getBoolean("unbreaking-rng-removal")) return;

        int originalDamage = event.getOriginalDamage();
        if (originalDamage <= 0) return;

        if (!Tag.ITEMS_ENCHANTABLE_ARMOR.isTagged(item.getType()) && !isElytra) return;

        int level = item.getEnchantmentLevel(Enchantment.UNBREAKING);
        if (level <= 0) return;

        Integer maxDurability = item.getData(DataComponentTypes.MAX_DAMAGE);
        if (maxDurability == null) return;

        int currentRawDamage = item.getDataOrDefault(DataComponentTypes.DAMAGE, 0);
        int currentRawDurability = maxDurability - currentRawDamage;
        double damage = isElytra ? DamageCalc.calculateToolDamage(originalDamage, level) : DamageCalc.calculateArmorDamage(originalDamage, level);
        double currentDurability = DamageCalc.getCurrentDurability(item, maxDurability);
        double newDurability = currentDurability - damage;

        if (newDurability < 1E-7) {
            event.setDamage(maxDurability);
        } else {
            event.setDamage(currentRawDurability - (int) Math.ceil(newDurability));
            item.editMeta(meta -> {
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                pdc.set(ConsistentDurability.DURABILITY_KEY, PersistentDataType.DOUBLE, newDurability);
            });
        }
    }
}
