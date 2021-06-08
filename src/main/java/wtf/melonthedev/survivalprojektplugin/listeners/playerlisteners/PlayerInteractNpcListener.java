package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import net.minecraft.server.v1_16_R3.EntityPose;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.others.NPC;
import wtf.melonthedev.survivalprojektplugin.others.events.NpcRightClickEvent;

public class PlayerInteractNpcListener implements Listener {

    @EventHandler
    public void onNPCRightClick(NpcRightClickEvent e) {
        Player p = e.getPlayer();
        p.sendMessage("<NPC> Click Click");
        e.getNpc().setPose(EntityPose.CROUCHING);
        NPC.updateNpc(e.getNpc());
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            e.getNpc().setPose(EntityPose.STANDING);
            NPC.updateNpc(e.getNpc());
        }, 10);
    }
}
