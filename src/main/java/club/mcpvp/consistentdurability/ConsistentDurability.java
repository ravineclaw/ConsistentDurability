package club.mcpvp.consistentdurability;

import club.mcpvp.consistentdurability.listeners.PlayerItemDamageListener;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ConsistentDurability extends JavaPlugin {
    public static final NamespacedKey DURABILITY_KEY = new NamespacedKey("consistentdurability", "durability");
    private static final double ELYTRA_DAMAGE_PER_TICK = 0.05D;
    private static final double ELYTRA_MINIMUM_DURABILITY = 1D;

    @Override
    public void onEnable() {
        scheduleElytraTimer();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerItemDamageListener(), this);
    }

    private void scheduleElytraTimer() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.isGliding()) continue;

                ItemStack item = player.getEquipment().getItem(EquipmentSlot.CHEST);
                if (item.getType() != Material.ELYTRA) continue;
                if (item.hasData(DataComponentTypes.UNBREAKABLE)) continue;

                Integer maxDurability = item.getData(DataComponentTypes.MAX_DAMAGE);
                if (maxDurability == null) continue;

                double currentDurability = DamageCalc.getCurrentDurability(item, maxDurability);
                if (currentDurability <= ELYTRA_MINIMUM_DURABILITY) continue;

                int unbreakingLevel = item.getEnchantmentLevel(Enchantment.UNBREAKING);
                double damage = DamageCalc.calculateToolDamage(ELYTRA_DAMAGE_PER_TICK, unbreakingLevel);
                double newDurability = currentDurability - damage;

                item.setData(DataComponentTypes.DAMAGE, maxDurability - (int) Math.ceil(newDurability));
                item.editPersistentDataContainer(pdc -> pdc.set(DURABILITY_KEY, PersistentDataType.DOUBLE, newDurability));
            }
        }, 1L, 1L);
    }
}
