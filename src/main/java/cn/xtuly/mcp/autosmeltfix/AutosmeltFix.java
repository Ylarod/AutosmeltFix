package cn.xtuly.mcp.autosmeltfix;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public final class AutosmeltFix extends JavaPlugin implements Listener {
    private static FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        config = this.getConfig();
        initDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void initDefaultConfig() {
        //初始化默认设置
        config.options().copyDefaults(true);
        config.addDefault("Option.method", "default");
        config.addDefault("Option.oreID", "14,15,16,21,56,73,127,153,256,291,292,308,809,832,833,359,1058,1496,1504,1931");
        config.addDefault("Option.debug", false);
        config.addDefault("Message.warn", "[§aAutosmeltFix§f]检测到你正在你用自动冶炼bug,你已被警告");
        config.addDefault("Message.kick", "[§aAutosmeltFix§f]检测到你正在你用自动冶炼bug,你已被kick");
        config.addDefault("Message.ban", "[§aAutosmeltFix§f]检测到你正在你用自动冶炼bug,你已被ban");
        this.saveConfig();
    }

    @EventHandler
    public void onTryingDestroy(PlayerInteractEvent e) {
        if (e.getPlayer().isOp() && config.getBoolean("Option.debug")) {
            e.getPlayer().sendMessage("-------------------------------------------");
            e.getPlayer().sendMessage("Action:" + e.getAction().name());
            e.getPlayer().sendMessage("BlockId:" + e.getClickedBlock().getTypeId());
            e.getPlayer().sendMessage("MainhandItemId:" + e.getPlayer().getInventory().getItemInMainHand().getTypeId());
            e.getPlayer().sendMessage("MainhandItemNBT:" + getNBT(e.getPlayer().getInventory().getItemInMainHand()));
            e.getPlayer().sendMessage("hasAutosmelt:" + hasAutoSmelt(e.getPlayer().getInventory().getItemInMainHand()));
            e.getPlayer().sendMessage("hasOreID" + config.getString("Option.oreID").contains(String.valueOf(e.getClickedBlock().getTypeId())));
            e.getPlayer().sendMessage("-------------------------------------------");
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (config.getString("Option.oreID").contains(String.valueOf(e.getClickedBlock().getTypeId()))) {
                Location loc = e.getClickedBlock().getLocation();
                Player player = e.getPlayer();
                if (hasAutoSmelt(player.getInventory().getItemInMainHand())) {
                    if (!canBreak(loc, player)) {
                        e.setCancelled(true);
                        switch (config.getString("Option.method")) {
                            case "warn":
                                e.getPlayer().sendMessage(config.getString("Message.warn"));
                                break;
                            case "kick":
                                e.getPlayer().kickPlayer(config.getString("Message.kick"));
                                break;
                            case "ban":
                                Bukkit.getBanList(BanList.Type.NAME).addBan(e.getPlayer().getName(), config.getString("Message.ban"), null, "WrenchFix");
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    public boolean hasAutoSmelt(ItemStack itemStack) {
        return getNBT(itemStack).contains("autosmelt");
    }

    public boolean canBreak(Location loc, Player player) {
        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(loc);
        if (res != null) {
            ResidencePermissions perms = res.getPermissions();
            return perms.playerHas(player, Flags.getFlag("destroy"), true);
        }
        return true;
    }

    public String getNBT(ItemStack itemStack) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItem.hasTag()) {
            NBTTagCompound compound = nmsItem.getTag();
            try {
                assert compound != null;
                return compound.toString();
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }
}
