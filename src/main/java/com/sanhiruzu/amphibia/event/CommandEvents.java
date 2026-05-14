package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.command.AmphibiaCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class CommandEvents {
    public static void register() {
        NeoForge.EVENT_BUS.register(CommandEvents.class);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        AmphibiaCommand.register(event.getDispatcher());
    }
}
