package me.proiezrush.oneskyblock.dependencies;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.listeners.PlayerListeners;
import org.bukkit.Bukkit;

public class ProtocolLib {

    public static void ListenForNPCPackets(Main m) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(m, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                try {
                    PacketContainer packet = event.getPacket();
                    if(packet.getEntityUseActions().getValues().get(0).equals(EnumWrappers.EntityUseAction.INTERACT)) {
                        if (event.getPlayer() != null) {
                            PlayerListeners.I = 0;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(m, () -> {
                                if (PlayerListeners.I == 0) {
                                    event.getPlayer().chat("/ob");
                                }
                            }, 1);
                        }
                    }
                }
                catch(Exception ignored){
                }
            }
        });

    }
}
