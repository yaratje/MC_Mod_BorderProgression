package com.yara.borderprogression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import serverutils.data.ClaimedChunks;
import serverutils.lib.data.ForgePlayer;
import serverutils.lib.math.ChunkDimPos;

// used for unclaim cancel, unclaim and claim cant be in the same tick. :/
public class ServerTickHandler {

    public static class ReclaimTask {

        public ForgePlayer player;
        public ChunkDimPos pos;
        public int delay;

        public ReclaimTask(ForgePlayer player, ChunkDimPos pos, int delay) {
            this.player = player;
            this.pos = pos;
            this.delay = delay;
        }
    }

    public static final List<ReclaimTask> pending = new ArrayList<>();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {

        if (event.phase == TickEvent.Phase.END) {

            Iterator<ReclaimTask> it = pending.iterator();

            while (it.hasNext()) {
                System.out.println("TICK RECLAIM RUNNING");
                ReclaimTask task = it.next();

                task.delay--;

                if (task.delay <= 0) {
                    ClaimedChunks.instance.claimChunk(task.player, task.pos);
                    it.remove();
                    System.out.println("Reclaimed chunk at: " + task.pos);
                }
            }
        }
    }
}
