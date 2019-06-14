package net.silexpvp.tdm.listeners

import net.silexpvp.tdm.TeamDeathmatch
import net.silexpvp.tdm.profile.Profile
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import java.util.*

class PlayerListener(var plugin: TeamDeathmatch): Listener {
    val random: Random = Random()

    @EventHandler
    fun onPlayerSpawnLocation(event: PlayerSpawnLocationEvent) {
        event.spawnLocation = plugin.server.getWorld("world").spawnLocation.add(Vector(random.nextInt(10), 10, random.nextInt(10)))
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        val profile: Profile = plugin.profileManager.getLoadedPlayer(player) ?: return

        player.sendMessage("${ChatColor.YELLOW} Welcome to the FFA of ${ChatColor.GOLD} SilexPvP ${ChatColor.YELLOW}.")
        profile.prepareForFight()
    }

    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity is Player && event.cause == EntityDamageEvent.DamageCause.FALL) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val profile: Profile = plugin.profileManager.getLoadedPlayer(event.player) ?: return

        plugin.server.scheduler.runTaskLater(plugin, profile::prepareForFight, 5L)
    }
}