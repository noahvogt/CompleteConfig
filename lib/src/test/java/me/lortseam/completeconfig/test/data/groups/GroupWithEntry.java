package me.lortseam.completeconfig.test.data.groups;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;

public class GroupWithEntry implements ConfigGroup {

    @ConfigEntry
    private boolean entry;

}