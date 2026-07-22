package main.java.me.dniym.checks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class CreativeItemCheckTest {

    @Test
    void allowsAnvilDisplayNameWhenNoUnsafeSignalExists() {

        final ItemStack stack = mock(ItemStack.class);
        final ItemMeta meta = mock(ItemMeta.class);
        when(stack.getType()).thenReturn(Material.DIAMOND_SWORD);
        when(stack.hasItemMeta()).thenReturn(true);
        when(stack.getItemMeta()).thenReturn(meta);
        when(meta.hasDisplayName()).thenReturn(true);

        try (MockedStatic<BadAttributeCheck> attributes = mockStatic(BadAttributeCheck.class)) {

            attributes.when(() -> BadAttributeCheck.hasNonDefaultAttributeModifiers(stack)).thenReturn(false);
            assertFalse(CreativeItemCheck.hasUnsafePayload(stack));
            attributes.verify(() -> BadAttributeCheck.hasNonDefaultAttributeModifiers(stack));

        }

    }

    @Test
    void allowsOtherVanillaMetadataWhenNoUnsafeSignalExists() {

        final ItemStack stack = mock(ItemStack.class);
        final LeatherArmorMeta meta = mock(LeatherArmorMeta.class);
        when(stack.getType()).thenReturn(Material.LEATHER_CHESTPLATE);
        when(stack.hasItemMeta()).thenReturn(true);
        when(stack.getItemMeta()).thenReturn(meta);

        try (MockedStatic<BadAttributeCheck> attributes = mockStatic(BadAttributeCheck.class)) {

            attributes.when(() -> BadAttributeCheck.hasNonDefaultAttributeModifiers(stack)).thenReturn(false);
            assertFalse(CreativeItemCheck.hasUnsafePayload(stack));

        }

    }

    @Test
    void blocksNonDefaultAttributePayload() {

        final ItemStack stack = mock(ItemStack.class);
        when(stack.getType()).thenReturn(Material.STICK);

        try (MockedStatic<BadAttributeCheck> attributes = mockStatic(BadAttributeCheck.class)) {

            attributes.when(() -> BadAttributeCheck.hasNonDefaultAttributeModifiers(stack)).thenReturn(true);
            assertTrue(CreativeItemCheck.hasUnsafePayload(stack));

        }

    }

    @Test
    void ignoresNullAndAirWithoutInspectingAttributes() {

        final ItemStack air = mock(ItemStack.class);
        when(air.getType()).thenReturn(Material.AIR);

        try (MockedStatic<BadAttributeCheck> attributes = mockStatic(BadAttributeCheck.class)) {

            assertFalse(CreativeItemCheck.hasUnsafePayload(null));
            assertFalse(CreativeItemCheck.hasUnsafePayload(air));
            attributes.verifyNoInteractions();

        }

    }

}
