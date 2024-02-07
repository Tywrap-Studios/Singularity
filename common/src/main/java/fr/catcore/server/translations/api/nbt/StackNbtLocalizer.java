package fr.catcore.server.translations.api.nbt;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizableText;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class StackNbtLocalizer {
    private static final String TRANSLATED_TAG = "server_translated";

    // While stack is unused, it's kept for backward compatibility and future proofing
    public static CompoundTag localize(ItemStack stack, CompoundTag tag, LocalizationTarget target) {
        if (tag == null) {
            return null;
        }

        try {
            NbtLocalizer nbt = new NbtLocalizer(tag);

            translateDisplay(target, nbt);
            translateBook(target, nbt);

            CompoundTag revertTag = nbt.getRevertNbtElement();

            if (revertTag != null) {
                tag.put(TRANSLATED_TAG, revertTag);
            }

            return nbt.getResultTag();
        } catch (Exception e) {
            return tag;
        }
    }

    public static CompoundTag unlocalize(CompoundTag tag) {
        if (tag != null && tag.contains(TRANSLATED_TAG, Tag.TAG_COMPOUND)) {
            CompoundTag revert = tag.getCompound(TRANSLATED_TAG);

            NbtLocalizer.applyRevert(tag, revert);
            tag.remove(TRANSLATED_TAG);
        }

        return tag;
    }

    private static void translateDisplay(LocalizationTarget target, NbtLocalizer nbt) {
        if (nbt.contains("display", Tag.TAG_COMPOUND)) {
            CompoundTag display = nbt.getCompound("display");
            
            translateCustomName(display, target);
            translateLore(display, target);

            nbt.set("display", display);
        }
    }

    private static void translateBook(LocalizationTarget target, NbtLocalizer nbt) {
        if (nbt.contains("pages", Tag.TAG_LIST)) {
            ListTag pages = nbt.getList("pages", Tag.TAG_STRING);
            
            for (int i = 0; i < pages.size(); i++) {
                String pageJson = pages.getString(i);
            
                pageJson = localizeTextJson(pageJson, target);
                pages.setTag(i, StringTag.valueOf(pageJson));
            }

            nbt.set("pages", pages);
        }
    }

    private static void translateCustomName(CompoundTag display, LocalizationTarget target) {
        if (display.contains("Name", Tag.TAG_STRING)) {
            display.putString("Name", localizeTextJson(display.getString("Name"), target));
        }
    }

    private static void translateLore(CompoundTag display, LocalizationTarget target) {
        if (display.contains("Lore", Tag.TAG_LIST)) {
            ListTag loreList = display.getList("Lore", Tag.TAG_STRING);
            
            for (int i = 0; i < loreList.size(); i++) {
                loreList.set(i, StringTag.valueOf(localizeTextJson(loreList.getString(i), target)));
            }
        }
    }

    private static String localizeTextJson(String json, LocalizationTarget target) {
        Component text;
        
        try {
            text = Component.Serializer.fromJsonLenient(json);
        } catch (Exception e) {
            text = null;
        }

        if (text == null) {
            return json;
        }

        Component localized = LocalizableText.asLocalizedFor(text, target);
        
        if (!localized.equals(text)) {
            return Component.Serializer.toJson(localized);
        } else {
            return json;
        }
    }
}
