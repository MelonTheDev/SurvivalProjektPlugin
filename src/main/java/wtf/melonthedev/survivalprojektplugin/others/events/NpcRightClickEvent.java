package wtf.melonthedev.survivalprojektplugin.others.events;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NpcRightClickEvent extends Event implements Cancellable {

    private final Player p;
    private final EntityPlayer npc;
    private boolean isCancelled;
    private static final HandlerList HANDLERS = new HandlerList();

    public NpcRightClickEvent(Player p, EntityPlayer npc) {
        this.p = p;
        this.npc = npc;
    }

    public Player getPlayer() {
        return p;
    }
    public EntityPlayer getNpc() {
        return npc;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
