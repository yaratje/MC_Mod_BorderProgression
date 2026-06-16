package com.yara.borderprogression;

import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.party.IParty;
import betterquesting.api2.storage.DBEntry;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.party.PartyManager;

public class QuestCounter {

    public static int questAmount(EntityPlayer player) {

        UUID playerUUID = player.getUniqueID();
        int count = 0;

        DBEntry<IParty> entryParty = PartyManager.INSTANCE.getParty(playerUUID);

        if (entryParty == null) {
            player.addChatMessage(new ChatComponentText("You need to be in a quest team to unlock chunks!"));
            return 0;
        }

        for (Map.Entry<UUID, IQuest> entry : QuestDatabase.INSTANCE.entrySet()) {

            IQuest quest = entry.getValue();

            if (quest.isComplete(playerUUID)) {
                count++;
            }
        }

        return 1 + count;
    }
}
