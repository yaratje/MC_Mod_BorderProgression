package com.yara.borderprogression;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import betterquesting.api.questing.party.IParty;
import betterquesting.api2.storage.DBEntry;
import betterquesting.questing.party.PartyManager;
import serverutils.lib.data.ForgePlayer;
import serverutils.lib.data.ForgeTeam;
import serverutils.lib.data.Universe;

public class Team_Sync {

    public static boolean areTeamsSynced(EntityPlayer player) {

        Set<UUID> claimMembers = getClaimMembers(player);
        Set<UUID> questMembers = getQuestMembers(player);

        return claimMembers.equals(questMembers);
    }

    public static Set<UUID> getClaimMembers(EntityPlayer player) {
        Set<UUID> claimMembers = new HashSet<>();

        if (player == null) return claimMembers;

        ForgePlayer fp = Universe.get()
            .getPlayer(player.getUniqueID());
        if (fp == null || !fp.hasTeam()) return claimMembers;

        ForgeTeam team = fp.team;

        for (ForgePlayer member : team.getMembers()) {
            claimMembers.add(member.getId());
        }

        return claimMembers;
    }

    public static Set<UUID> getQuestMembers(EntityPlayer player) {
        UUID uuid = player.getUniqueID();

        DBEntry<IParty> entry = PartyManager.INSTANCE.getParty(uuid);

        if (entry == null) {
            return Collections.singleton(uuid);
        }

        IParty party = entry.getValue();

        List<UUID> members = party.getMembers();

        return new HashSet<>(members);
    }

}
