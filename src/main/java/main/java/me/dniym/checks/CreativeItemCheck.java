package main.java.me.dniym.checks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Classifies payloads received through the creative-slot packet.
 *
 * <p>
 * Vanilla metadata is not itself evidence of an unsafe payload. Other
 * configured protections remain responsible for overstacked items, illegal
 * enchantments, banned types, and name or lore rules.
 * </p>
 */
public final class CreativeItemCheck {

    private CreativeItemCheck() {

    }

    public static boolean hasUnsafePayload(ItemStack stack) {

        if (stack == null || stack.getType() == Material.AIR) {

            return false;

        }

        return BadAttributeCheck.hasNonDefaultAttributeModifiers(stack);

    }

}
