package com.iafenvoy.thinkbeforedrop;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class DropManager {
    private static long lastDropTime = 0;
    private static int lastSlot = -1;
    private static boolean dropped = false;

    private static boolean shouldHandleDrop(ItemStack stack) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (!config.enabled) return false;
        Item item = stack.getItem();
        Block block = null;
        if (item instanceof BlockItem)
            block = ((BlockItem) item).getBlock();
        ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
        assert location != null;
        String name = location.toString();
        if (config.custom.excludeItems.contains(name))
            return false;
        if (config.internal.weapon)
            if (item instanceof SwordItem || item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem || item instanceof ArrowItem)
                return true;
        if (config.internal.tool)
            if (item instanceof AxeItem || item instanceof PickaxeItem || item instanceof ShovelItem || item instanceof HoeItem)
                return true;
        if (config.internal.shulkerBox)
            if (block != null)
                if (block instanceof ShulkerBoxBlock)
                    return true;
        if (config.internal.armor)
            if (item instanceof ArmorItem || item instanceof ElytraItem)
                return true;
        if (config.internal.disc)
            if (item instanceof RecordItem)
                return true;
        if (config.internal.uncommon)
            if (item.getRarity(stack) == Rarity.UNCOMMON)
                return true;
        if (config.internal.rare)
            if (item.getRarity(stack) == Rarity.RARE)
                return true;
        if (config.internal.epic)
            if (item.getRarity(stack) == Rarity.EPIC)
                return true;
        if (config.internal.enchanted) {
            if (stack.isEnchanted())
                return true;
        }
        if (config.internal.hasNbt) {
            CompoundTag tag = stack.getTag();
            if (tag != null)
                if (tag.contains("display") || tag.getBoolean("Unbreakable") || tag.contains("CanDestroy") || tag.contains("CanPlaceOn") || tag.contains("StoredEnchantments") || tag.contains("AttributeModifiers"))
                    return true;
        }
        if (config.internal.enchantedBook)
            if (item instanceof EnchantedBookItem)
                return true;
        if (config.internal.book)
            if (item instanceof WritableBookItem || item instanceof WrittenBookItem)
                return true;
        return config.custom.customItems.contains(name);
    }

    public static boolean shouldThrow(ItemStack stack, int slot) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (slot != lastSlot) {
            lastDropTime = 0;
            dropped = false;
        }
        if (!shouldHandleDrop(stack) || dropped) return true;
        long now = System.currentTimeMillis();
        if (now - lastDropTime >= config.time.minSecond * 1000 && now - lastDropTime <= config.time.maxSecond * 1000) {
            if (stack.getCount() != 1)
                dropped = true;
            lastDropTime = 0;
            return true;
        }
        lastDropTime = now;
        lastSlot = slot;
        return false;
    }

    public static Component getWarningText() {
        return Component.translatable("tbt.warning");
    }
}
