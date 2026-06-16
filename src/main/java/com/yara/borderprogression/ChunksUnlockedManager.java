package com.yara.borderprogression;

import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import serverutils.data.ClaimedChunk;
import serverutils.data.ClaimedChunks;
import serverutils.events.chunks.ChunkModifiedEvent;
import serverutils.lib.data.ForgePlayer;
import serverutils.lib.data.ForgeTeam;
import serverutils.lib.data.Universe;

public class ChunksUnlockedManager {

    @SubscribeEvent
    public void onChunkClaim(ChunkModifiedEvent.Claim event) {
        ForgePlayer fp = event.getPlayer();

        if (fp == null) {
            return;
        }

        EntityPlayer player = fp.getPlayer();
        UUID uuid = player.getUniqueID();

        boolean same = Team_Sync.areTeamsSynced(player);
        if (!same) {
            event.setCanceled(true);
            player.addChatMessage(
                new ChatComponentText(
                    "Your quest team and claim team are not the same. Make sure both teams contain the same members"));
            return;
        }

        int allowedChunks = QuestCounter.questAmount(player);

        int usedChunks = getPlayerClaimCount(player);

        player.addChatMessage(new ChatComponentText("Allowed: " + allowedChunks + " | Used: " + usedChunks));

        if (usedChunks == -1) {
            player.addChatMessage(
                new ChatComponentText("You need to be in a ftb/serverutil team to be able to claim chunks!"));
        }

        if (usedChunks >= allowedChunks) {
            event.setCanceled(true);
            player.addChatMessage(new ChatComponentText("You need to complete more quests to claim more chunks."));
        }
        return;
    }

    public static int getPlayerClaimCount(EntityPlayer player) {

        ForgePlayer fp = Universe.get()
            .getPlayer(player.getUniqueID());
        if (fp == null || !fp.hasTeam()) {
            return -1;
        }

        ForgeTeam team = fp.team;

        boolean includePending = false;

        Set<ClaimedChunk> claimcountSet = ClaimedChunks.instance
            .getTeamChunks(team, OptionalInt.empty(), includePending);
        int count = claimcountSet.size();

        return count;
    }

}
