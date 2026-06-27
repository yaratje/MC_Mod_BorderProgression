package com.yara.borderprogression;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import serverutils.data.ClaimedChunk;
import serverutils.data.ClaimedChunks;
import serverutils.lib.math.ChunkDimPos;

// CLASS NOT USED ANYMORE BESIDES LOGIN MESSAGE< MOVED TO BorderOutsideProtection.

public class BorderChunkUpdater {

    private static final boolean ENABLE_GAMEMODE2_MODE = false;
    private static final boolean ENABLE_TELEPORT_BACK_MODE = false;

    // last safe position per player
    private static final HashMap<UUID, Double[]> lastSafePos = new HashMap<>();
    private static final HashMap<UUID, Boolean> isOutside = new HashMap<>();

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;

        if (!player.worldObj.isRemote) {
            player.addChatMessage(new ChatComponentText("§a[Border] System active."));
        }
    }

    // @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        World world = player.worldObj;

        if (world.isRemote) return;

        // ServerUtil not there
        if (ClaimedChunks.instance == null) return;

        ChunkDimPos pos = new ChunkDimPos(player);
        ClaimedChunk chunk = ClaimedChunks.instance.getChunk(pos);

        UUID id = player.getUniqueID();

        boolean hasAnyClaims = !ClaimedChunks.instance.getAllChunks()
            .isEmpty();

        // ifno claims exist yet do nothing to prevent ehhh idk fuckiewuckies
        if (!hasAnyClaims) {
            saveSafe(player, id);
            return;
        }

        // claim does exist, player is in chunk / enters chunk
        if (chunk != null) {

            saveSafe(player, id);

            boolean wasOutside = isOutside.getOrDefault(id, false);

            if (wasOutside && ENABLE_GAMEMODE2_MODE) {
                if (player instanceof EntityPlayerMP) {
                    EntityPlayerMP mp = (EntityPlayerMP) player;
                    mp.setGameType(GameType.SURVIVAL);
                }

                player.addChatMessage(
                    new ChatComponentText(
                        "§aYou are back in a claimed chunk, gamemode has been put back to survival."));
            }

            isOutside.put(id, false);
            return;
        }

        // if player is outside claim / crosses border / keep inside
        Double[] safe = lastSafePos.get(id);

        if (safe != null) {

            boolean wasOutside = isOutside.getOrDefault(id, false);

            // trigger single time to avoid spamm, set boolean that player is outside
            if (!wasOutside) {

                if (ENABLE_GAMEMODE2_MODE) {
                    if (player instanceof EntityPlayerMP) {
                        EntityPlayerMP mp = (EntityPlayerMP) player;
                        mp.setGameType(GameType.ADVENTURE);
                    }

                    player.addChatMessage(
                        new ChatComponentText(
                            "§cYou left claimed chunks, be careful as gamemode has been changed to adventure."));
                }

                if (ENABLE_TELEPORT_BACK_MODE) {
                    player.setPositionAndUpdate(safe[0], safe[1], safe[2]);
                    System.out.println("[BORDER] Teleported player back to safe chunk");
                }
            }

            isOutside.put(id, true);
        }
    }

    private void saveSafe(EntityPlayer player, UUID id) {
        lastSafePos.put(id, new Double[] { player.posX, player.posY, player.posZ });
    }
}
