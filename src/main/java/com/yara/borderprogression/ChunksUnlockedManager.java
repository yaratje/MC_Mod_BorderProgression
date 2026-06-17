package com.yara.borderprogression;

import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.ChunkCoordIntPair;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import serverutils.data.ClaimedChunk;
import serverutils.data.ClaimedChunks;
import serverutils.events.chunks.ChunkModifiedEvent;
import serverutils.lib.data.ForgePlayer;
import serverutils.lib.data.ForgeTeam;
import serverutils.lib.data.Universe;
import serverutils.lib.math.ChunkDimPos;

public class ChunksUnlockedManager {

    @SubscribeEvent
    public void onChunkClaim(ChunkModifiedEvent.Claim event) {

        // #1 check if player has a team and questteam
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

        // #2 check if the chunk neightbours another chunk
        ChunkDimPos pos = event.getChunkDimPos();
        ForgeTeam team = fp.team;

        // Allow first claim (no chunks yet)
        if (getPlayerClaimCount(player) > 0) {
            if (!hasAdjacentClaim(team, pos)) {
                event.setCanceled(true);
                player.addChatMessage(
                    new ChatComponentText("You can only claim chunks adjacent to your existing claims."));
                return;
            }
        }

        // #3 check if player has enough chunks allowed
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

    private boolean hasAdjacentClaim(ForgeTeam team, ChunkDimPos pos) {

        // checks neightbouring chunks if there are any claimed. if not return false
        boolean includePending = false;

        Set<ClaimedChunk> chunks = ClaimedChunks.instance.getTeamChunks(team, OptionalInt.of(pos.dim), includePending);

        ChunkCoordIntPair xz = pos.getChunkPos();

        int x = xz.chunkXPos;
        int z = xz.chunkZPos;

        for (ClaimedChunk cc : chunks) {
            ChunkDimPos cpos = cc.getPos();
            ChunkCoordIntPair xz_n = cpos.getChunkPos();

            int x_n = xz_n.chunkXPos;
            int z_n = xz_n.chunkZPos;

            int dx = Math.abs(x_n - x);
            int dz = Math.abs(z_n - z);

            if ((dx == 1 && dz == 0) || (dx == 0 && dz == 1)) {
                return true;
            }
        }

        return false;
    }

}
