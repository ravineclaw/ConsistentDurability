package club.mcpvp.consistentdurability;

import club.mcpvp.consistentdurability.listeners.PlayerItemDamageListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ConsistentDurability extends JavaPlugin {
    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerItemDamageListener(), this);
    }
}
