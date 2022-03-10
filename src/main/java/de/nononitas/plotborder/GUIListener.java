package de.nononitas.plotborder;

import de.nononitas.plotborder.util.BorderChanger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();

        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if(event.getClickedInventory() == null || event.getCurrentItem() == null) {
            return;
        }

        if(!event.getView().getTitle().equals(PlotBorder.getColoredConfigString("gui-border-title"))
                && !event.getView().getTitle().equals(PlotBorder.getColoredConfigString("gui-wall-title"))) {
            return;
        }

        event.setCancelled(true);

        if((event.getClickedInventory() != event.getView().getTopInventory())) {
            return;
        }

        Gui.Type componentType = (event.getView().getTitle().equals(PlotBorder.getColoredConfigString("gui-border-title"))) ? Gui.Type.BORDER : Gui.Type.WALL;
        String type = componentType.getType();

        if(event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
            if(event.getSlot() > (PlotBorder.getPlugin().getConfig().getInt("gui-" + type + "-rows") * 9 - 1)) {
                int newPage = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName()
                        .replace(PlotBorder.getColoredConfigString("page") + " ", "")) - 1;
                Gui.openGui(componentType, ((Player) event.getWhoClicked()).getPlayer(), newPage);
                PlotBorder.guiPage.put(p.getUniqueId(), newPage);
                return;
            }
        }

        int page = PlotBorder.guiPage.get(p.getUniqueId());
        int invSize = event.getClickedInventory().getSize() - 9;
        int startSlot = page * invSize;

        if(!PlotBorder.hasPlayerCooldown(p)) {
            int i = 0, slot = 0;
            for (String section : PlotBorder.getPlugin().getConfig().getConfigurationSection(type + "-items").getKeys(false)) {
                if(slot >= startSlot) {
                    if(i == event.getSlot()) {
                        section = type + "-items." + section;
                        String materialToSet = PlotBorder.getPlugin().getConfig().getString(section + ".material");
                        String noPerms = PlotBorder.getColoredConfigString("no-permission");
                        if(!p.hasPermission(PlotBorder.getPlugin().getConfig().getString(section + ".permission")) && !p.hasPermission("plotborder.admin")
                                && !p.hasPermission("plotborder." + type + ".")) {
                            p.sendMessage(noPerms);
                            Bukkit.getScheduler().runTask(PlotBorder.getPlugin(), p::closeInventory);
                        } else {
                            if (PlotBorder.getPlugin().getConfig().getString(section + ".category").equals("false")) {
                                BorderChanger.change(componentType, p, materialToSet, event.getCurrentItem().getItemMeta().getDisplayName());
                            } else if (PlotBorder.getPlugin().getConfig().getString(section + ".category").equals("true")) {
                                categoryGui.openGui(p, 0);
                                for (String categorySection : PlotBorder.getPlugin().getConfig().getConfigurationSection("categories.OAK_PLANKS").getKeys(false)) {
                                    int j = 0, categorySlot = 0;
                                    if(categorySlot >= startSlot) {
                                        if(j == event.getSlot()) {
                                            String category = event.getCurrentItem().getItemMeta().getDisplayName();
                                            categorySection = "categories." + category + "." + categorySection;
                                            String categoryMaterialToSet = PlotBorder.getPlugin().getConfig().getString(categorySection + ".material");
                                            BorderChanger.change(componentType, p, categoryMaterialToSet, event.getCurrentItem().getItemMeta().getDisplayName());
                                            break;
                                        }
                                        j++;
                                    }
                                    categorySlot++;
                                }
                            }
                        }
                        break;
                    }
                    i++;
                }
                slot++;
            }
        } else {
            String cooldown = PlotBorder.getColoredConfigString("cooldown-m-border");
            cooldown = cooldown.replaceAll("%time%", String.valueOf(PlotBorder.getCooldown(p)));
            p.sendMessage(cooldown);
            Bukkit.getScheduler().runTask(PlotBorder.getPlugin(), p::closeInventory);
        }
    }
}
