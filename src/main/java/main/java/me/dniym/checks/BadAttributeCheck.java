package main.java.me.dniym.checks;

import com.google.common.collect.Multimap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import main.java.me.dniym.IllegalStack;
import main.java.me.dniym.enums.Msg;
import main.java.me.dniym.enums.Protections;
import main.java.me.dniym.listeners.fListener;
import main.java.me.dniym.utils.NBTApiStuff;
import main.java.me.dniym.utils.NBTStuff;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BadAttributeCheck {

    public static void CheckStorageInventory(CraftingInventory inventory, Player player) {

        if (Protections.RemoveCustomAttributes.isEnabled(inventory) && IllegalStack.hasStorage()) {

            if (Protections.AllowBypass.isEnabled() && player.hasPermission("illegalstack.enchantbypass")) {

                return;

            }

            for (ItemStack itemStack : inventory.getStorageContents()) {

                if (itemStack != null && itemStack.getType() != Material.AIR && NBTStuff.hasBadCustomData(itemStack)) {

                    fListener.getLog().append2(Msg.GenericItemRemoval.getValue(itemStack,
                            Protections.RemoveCustomAttributes, player, "Crafting Inventory"));

                    inventory.removeItem(itemStack);

                }

            }

        }

    }

    public static boolean hasBadAttributes(ItemStack is, Object obj) {

        if (!Protections.RemoveCustomAttributes.isEnabled(obj)) {

            return false;

        }

        return hasNonDefaultAttributeModifiers(is);

    }

    /**
     * Pure predicate: true iff the item carries at least one AttributeModifier that
     * is NOT part of the vanilla default modifier set for its Material. Vanilla
     * defaults are exposed via ItemMeta on 1.20.5+, so a naive
     * hasAttributeModifiers() check false-positives every vanilla weapon/armor and
     * causes inventory churn when the periodic scanner rewrites the meta.
     */
    public static boolean hasNonDefaultAttributeModifiers(ItemStack is) {

        if (is == null || is.getType() == Material.AIR) {

            return false;

        }

        if (IllegalStack.isHasAttribAPI()) {

            ItemMeta meta = is.getItemMeta();
            return meta != null && hasCustomModifier(meta, is.getType());

        }

        if (IllegalStack.isNbtAPI()) {

            return NBTApiStuff.hasBadCustomDataLegacy(is);

        }

        return false;

    }

    public static boolean checkForBadCustomData(ItemStack itemStack, Object obj) {

        if (IllegalStack.isHasAttribAPI()) {

            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null || !itemMeta.hasAttributeModifiers()) {

                return false;

            }

            Set<UUID> defaultUuids = collectDefaultModifierUuids(itemStack.getType());
            Multimap<Attribute, AttributeModifier> mods = itemMeta.getAttributeModifiers();

            StringBuilder attribs = new StringBuilder();
            boolean anyCustom = false;
            for (Map.Entry<Attribute, AttributeModifier> entry : mods.entries()) {

                AttributeModifier mod = entry.getValue();
                if (!defaultUuids.contains(mod.getUniqueId())) {

                    anyCustom = true;
                    attribs.append(" ").append(mod.getName()).append(" value: ").append(mod.getAmount());

                }

            }

            if (!anyCustom) {

                return false;

            }

            fListener.getLog().append(Msg.CustomAttribsRemoved3.getValue(itemStack, obj, attribs),
                    Protections.RemoveCustomAttributes);

            for (Map.Entry<Attribute, AttributeModifier> entry : mods.entries()) {

                AttributeModifier mod = entry.getValue();
                if (!defaultUuids.contains(mod.getUniqueId())) {

                    itemMeta.removeAttributeModifier(entry.getKey(), mod);

                }

            }

            for (ItemFlag iFlag : itemMeta.getItemFlags()) {

                itemMeta.removeItemFlags(iFlag);

            }

            itemStack.setItemMeta(itemMeta);
            return true;

        } else if (IllegalStack.isNbtAPI()) {

            return NBTApiStuff.checkForBadCustomDataLegacy(itemStack, obj);

        } else {

            fListener.getLog().append(Msg.StaffNoNBTAPI.getValue(Protections.RemoveCustomAttributes.name()),
                    Protections.RemoveCustomAttributes);

        }

        return false;

    }

    private static boolean hasCustomModifier(ItemMeta meta, Material type) {

        if (!meta.hasAttributeModifiers()) {

            return false;

        }

        Set<UUID> defaultUuids = collectDefaultModifierUuids(type);
        Multimap<Attribute, AttributeModifier> mods = meta.getAttributeModifiers();
        for (AttributeModifier mod : mods.values()) {

            if (!defaultUuids.contains(mod.getUniqueId())) {

                return true;

            }

        }

        return false;

    }

    public static Set<UUID> collectDefaultModifierUuidsPublic(Material type) {

        return collectDefaultModifierUuids(type);

    }

    private static Set<UUID> collectDefaultModifierUuids(Material type) {

        Set<UUID> uuids = new HashSet<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {

            try {

                Multimap<Attribute, AttributeModifier> defaults = type.getDefaultAttributeModifiers(slot);
                if (defaults == null) {

                    continue;

                }

                for (AttributeModifier defMod : defaults.values()) {

                    uuids.add(defMod.getUniqueId());

                }

            } catch (Throwable ignored) {

                // Method may not exist on older servers, or the slot is incompatible
                // with this material. Degrade gracefully: treat as no defaults known
                // for that slot.
            }

        }

        return uuids;

    }

}
