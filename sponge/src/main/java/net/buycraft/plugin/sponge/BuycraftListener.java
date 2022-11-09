package net.buycraft.plugin.sponge;

import net.buycraft.plugin.data.QueuedPlayer;
import net.buycraft.plugin.data.ServerEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import java.util.Date;

public class BuycraftListener {
    private final BuycraftPlugin plugin;

    public BuycraftListener(final BuycraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onPlayerJoinEvent(ClientConnectionEvent.Join event) {
        final Player player = event.getTargetEntity();

        if (plugin.getApiClient() == null || player.getConnection().getAddress().getAddress() == null) {
            return;
        }

        plugin.getServerEventSenderTask().queueEvent(new ServerEvent(
                player.getUniqueId().toString().replace("-", ""),
                player.getName(),
                player.getConnection().getAddress().getAddress().getHostAddress(),
                ServerEvent.JOIN_EVENT,
                new Date()
        ));

        QueuedPlayer qp = plugin.getDuePlayerFetcher().fetchAndRemoveDuePlayer(player.getName());
        if (qp != null) {
            plugin.getPlayerJoinCheckTask().queue(qp);
        }
    }

    @Listener
    public void onPlayerQuitEvent(ClientConnectionEvent.Disconnect event) {
        final Player player = event.getTargetEntity();

        if (plugin.getApiClient() == null || player.getConnection().getAddress().getAddress() == null) {
            return;
        }

        plugin.getServerEventSenderTask().queueEvent(new ServerEvent(
                player.getUniqueId().toString().replace("-", ""),
                player.getName(),
                player.getConnection().getAddress().getAddress().getHostAddress(),
                ServerEvent.LEAVE_EVENT,
                new Date()
        ));
    }
}
