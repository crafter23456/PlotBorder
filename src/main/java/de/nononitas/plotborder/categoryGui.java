package de.nononitas.plotborder;

import de.nononitas.plotborder.util.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Objects;

public class categoryGui {
    public static void openGui(Player p, int page) {
        FileConfiguration config = PlotBorder.getPlugin().getConfig();

        //String title = PlotBorder.color(config.getString("gui-" + type + "-title"));
        String title = "Custom";
        //int invSize = config.getInt("gui-" + type + "-rows") * 9;
        int invSize = 4 * 9;
        //Inventory inv = Bukkit.createInventory(null, (config.getInt("gui-" + type + "-rows") + 1) * 9, title);
        Inventory inv = Bukkit.createInventory(null, (5 * 9), title);

        int index = 0;
        int startSlot = page * invSize;
        int categorySlot = 0;
        for (String categorySection : config.getConfigurationSection("categories.OAK_PLANKS").getKeys(false)) {
            if(categorySlot >= startSlot) {
                if(index < invSize) {

                    categorySection = "categories.OAK_PLANKS." + categorySection;

                    String material = config.getString(categorySection + ".display-material");
                    String displayname = config.getString(categorySection + ".displayname");
                    List<String> lore;
                    if(p.hasPermission(Objects.requireNonNull(config.getString(categorySection + ".permission")))) {
                        lore = config.getStringList(categorySection + ".lore-with-perm");
                    } else {
                        lore = config.getStringList(categorySection + ".lore-without-perm");
                    }


                    ItemStack item = getItem(material, displayname, lore);
                    inv.setItem(index, item);

                    index++;
                } else break;
            }
            categorySlot++;

        }

        ItemStack lastRowPlaceHolderItem = new ItemStack(Material.valueOf(config.getString("last-row-material")));
        for (int i = invSize; i < invSize + 9; i++){
            inv.setItem(i, lastRowPlaceHolderItem);
        }

        ItemStack arrowRight = Heads.WHITE_ARROW_RIGHT.getItemStack();
        ItemStack arrowLeft = Heads.WHITE_ARROW_LEFT.getItemStack();
        SkullMeta meta;
        if(PlotBorder.getPlugin().getConfig().getConfigurationSection("categories.OAK_PLANKS").getKeys(false).size() - 1 > categorySlot) {
            meta = (SkullMeta) arrowRight.getItemMeta();
            meta.setDisplayName(PlotBorder.getColoredConfigString("page") + " " + (page + 2));
            arrowRight.setItemMeta(meta);
            inv.setItem(invSize - 1 + 7, arrowRight);
        }
        if(page != 0) {
            meta = (SkullMeta) arrowLeft.getItemMeta();
            meta.setDisplayName(PlotBorder.getColoredConfigString("page") + " " + page);
            arrowLeft.setItemMeta(meta);
            inv.setItem(invSize - 1 + 3, arrowLeft);
        }
        p.openInventory(inv);
    }

    private static ItemStack getItem(String materialString, String displayname, List<String> lore) {
        materialString = materialString.toUpperCase();


        if(Material.getMaterial(materialString) == null) {
            Bukkit.getConsoleSender().sendMessage(PlotBorder.PREFIX + "§4" + materialString + "§c is not a valid material");
            Bukkit.getConsoleSender().sendMessage(PlotBorder.PREFIX + "§cPlease check the config.yml");
            materialString = "AIR";
        }
        Material material = Material.getMaterial(materialString);

        ItemStack item = new ItemStack(material);


        ItemMeta meta = item.getItemMeta();
        if(item.getType() != Material.AIR) {
            meta.setDisplayName(PlotBorder.color(displayname));

            for (int index = 0; index < lore.size(); index++) {
                lore.set(index, PlotBorder.color(lore.get(index)));
            }

            meta.setLore(lore);

            item.setItemMeta(meta);
        }

        return item;
    }

    public enum Type {
        WALL("wall"),
        BORDER("border");

        private final String type;

        Type(String type) {
            this.type = type;

        }

        public String getType() {
            return type;
        }
    }
}
