package net.ivan.fishingbotmod;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FishingBotMod.MODID, value = Dist.CLIENT)
public class FishingBot {

    private static final Minecraft mc = Minecraft.getInstance();

    private static boolean isFishing = false;
    private static FishingHook bobber = null;
    private static long castTime = 0;
    private static long specialCaseCastTime = 0;
    private static final long CHECK_DELAY = 2000; // 2 seconds in milliseconds
    private static final long RECAST_DELAY = 1000;
    private static final long SPECIAL_CASE_DELAY = 4000;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.player != null) {
            Player player = mc.player;
            ItemStack mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);

            // Check if the player is holding appropriate items in hands
            if (offHandItem.getItem() == Items.HEART_OF_THE_SEA && mainHandItem.getItem() == Items.FISHING_ROD) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - specialCaseCastTime < SPECIAL_CASE_DELAY) {
                    return;
                }

                // Check if we can cast the fishing rod
                if (!isFishing && currentTime - castTime >= RECAST_DELAY && hasSufficientDurability(mainHandItem)) {
                    mc.gameMode.useItem(player, InteractionHand.MAIN_HAND);
                    isFishing = true;
                    castTime = currentTime;
                }

                if (isFishing && currentTime - castTime >= CHECK_DELAY) {
                    if (player.fishing != null) {
                        bobber = player.fishing;
                        net.minecraft.world.entity.Entity hookedEntity = bobber.getHookedIn();

                        if (hookedEntity != null) {
                            mc.gameMode.useItem(player, InteractionHand.MAIN_HAND);
                            isFishing = false;
                            specialCaseCastTime = currentTime;
                        } else if (bobber.isUnderWater()) {
                            mc.gameMode.useItem(player, InteractionHand.MAIN_HAND);
                            isFishing = false;
                            castTime = currentTime;
                        }
                    }
                }
            } else {
                // Reset if the player is not holding the correct items
                isFishing = false;
                bobber = null;
            }
        }
    }

    private static boolean hasSufficientDurability(ItemStack item) {
        int damage = item.getDamageValue();
        int maxDamage = item.getMaxDamage();
        int remainingDurability = maxDamage - damage;

        // Check if the remaining durability is greater than 16
        return remainingDurability > 16;
    }
}




