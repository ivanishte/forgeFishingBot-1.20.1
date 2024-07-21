package net.ivan.fishingbotmod;

/*
DESIRED MECHANICS:
when holding nautilus shell in off-hand cast fishing rod
and reel in when player has caught something, repeat.
*/

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FishingBotMod.MODID, value = Dist.CLIENT)
public class FishingBot {

    private static final Minecraft mc = Minecraft.getInstance();
    private static boolean isFishing = false;
    private static FishingHook bobber = null;
    private static long castTime = 0;
    private static final long CHECK_DELAY = 2000; // 2 seconds in milliseconds
    private static final long RECAST_DELAY = 1000;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.player != null) {
            Player player = mc.player;
            ItemStack mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);

            // Check if the player is holding a nautilus shell in their offhand and a fishing rod in their main hand
            if (offHandItem.getItem() == Items.NAUTILUS_SHELL && mainHandItem.getItem() == Items.FISHING_ROD) {
                long currentTime = System.currentTimeMillis();

                if (!isFishing && currentTime - castTime >= RECAST_DELAY) {
                    // Perform right-click action to cast the fishing rod
                    mc.gameMode.useItem(player, InteractionHand.MAIN_HAND);
                    isFishing = true;
                    castTime = currentTime;
                }

                // Check if the delay has passed before checking the bobber
                if (isFishing && currentTime - castTime >= CHECK_DELAY) {
                    if (player.fishing != null) {
                        bobber = player.fishing;
                        if (bobber.isUnderWater()) {
                            // Perform right-click action to reel in the fishing rod
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
}


