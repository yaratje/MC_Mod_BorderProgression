package com.yara.borderprogression;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import serverutils.data.ClaimedChunk;
import serverutils.data.ClaimedChunks;
import serverutils.lib.math.ChunkDimPos;

public class BorderHandler {

    private static final boolean DEBUG = true;
    private int tickCounter = 0;

    // particleborder
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (ClaimedChunks.instance == null) return;

        if (event.side != Side.CLIENT) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        World world = mc.theWorld;
        EntityPlayer player = mc.thePlayer;

        int playerChunkX = ((int) player.posX) >> 4;
        int playerChunkZ = ((int) player.posZ) >> 4;

        int renderRadius = 2;

        for (int dx = -renderRadius; dx <= renderRadius; dx++) {
            for (int dz = -renderRadius; dz <= renderRadius; dz++) {

                int cx = playerChunkX + dx;
                int cz = playerChunkZ + dz;

                ChunkDimPos pos = new ChunkDimPos(cx, cz, world.provider.dimensionId);
                ClaimedChunk chunk = ClaimedChunks.instance.getChunk(pos);

                if (chunk == null) continue;

                // Check 4 directions
                checkEdge(world, player, cx, cz, 1, 0); // EAST
                checkEdge(world, player, cx, cz, -1, 0); // WEST
                checkEdge(world, player, cx, cz, 0, 1); // SOUTH
                checkEdge(world, player, cx, cz, 0, -1); // NORTH
            }
        }
    }

    private void checkEdge(World world, EntityPlayer player, int cx, int cz, int dx, int dz) {

        ChunkDimPos neighborPos = new ChunkDimPos(cx + dx, cz + dz, world.provider.dimensionId);
        ClaimedChunk neighbor = ClaimedChunks.instance.getChunk(neighborPos);

        // If neighbor chunk is not claimed show the border
        if (neighbor != null) return;

        int xStart = cx * 16;
        int zStart = cz * 16;

        int y = (int) player.posY + 1;

        // EAST/WEST edges
        if (dx != 0) {
            int x = (dx > 0) ? xStart + 16 : xStart;

            for (int z = zStart; z < zStart + 16; z += 2) {
                spawnBorderParticle(world, x, y, z);
            }
        }

        // NORTH/SOUTH edges
        if (dz != 0) {
            int z = (dz > 0) ? zStart + 16 : zStart;

            for (int x = xStart; x < xStart + 16; x += 2) {
                spawnBorderParticle(world, x, y, z);
            }
        }
    }

    // spawn particles but not too many
    private void spawnBorderParticle(World world, int x, double y, int z) {

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        double dx = player.posX - x;
        double dy = player.posY - y;
        double dz = player.posZ - z;

        double distSq = dx * dx + dy * dy + dz * dz;

        // show why particles are being stupid
        if (DEBUG && distSq > 5000) return;

        // test particles
        world.spawnParticle("reddust", x + 0.5, y, z + 0.5, 0.0, 0.1, 0.0);
    }
}
