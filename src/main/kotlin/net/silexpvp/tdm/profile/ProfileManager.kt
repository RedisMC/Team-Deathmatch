package net.silexpvp.tdm.profile

import net.silexpvp.tdm.TeamDeathmatch
import org.bukkit.event.Listener
import com.mongodb.client.model.Filters
import java.util.UUID
import org.bson.conversions.Bson
import com.google.common.base.Strings
import net.silexpvp.nightmare.util.JavaUtils

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.regex.Pattern


class ProfileManager(private val plugin: TeamDeathmatch) : Listener {

    val profiles: HashMap<UUID, Profile> = HashMap()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun savePlayers() {
        profiles.values.forEach(Profile::save)

        plugin.logger.info("Saved a total of ${profiles.size} profiles to the database.")
    }

    fun getLoadedPlayer(player: Player): Profile? {
        return profiles[player.uniqueId]
    }

    fun getLoadedPlayer(uniqueId: UUID): Profile? {
        return profiles[uniqueId]
    }

    fun getPlayer(search: String): Profile? {
        if (Strings.isNullOrEmpty(search)) return null

        val player =
            if (JavaUtils.isUUID(search)) plugin.server.getPlayer(UUID.fromString(search)) else plugin.server.getPlayer(
                search
            )

        if (player != null) return getLoadedPlayer(player)

        val filter: Bson

        filter = if (JavaUtils.isUUID(search)) {
            Filters.eq(UUID.fromString(search))
        } else {
            Filters.eq("name", Pattern.compile('^'.toString() + search + '$'.toString(), Pattern.CASE_INSENSITIVE))
        }

        val document = plugin.playersCollection.find(filter).first()

        return if (document == null) {
            null
        } else {
            Profile(document)
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onAsyncPlayerPreLogin(event: AsyncPlayerPreLoginEvent) {
        val document = plugin.playersCollection.find(Filters.eq(event.uniqueId)).first()

        val player: Profile

        player = if (document == null) {
            Profile(event.uniqueId)
        } else {
            Profile(document)
        }

        if (player.name == null || !player.name.equals(event.name)) {
            player.name = event.name
            player.save()
        }

        profiles[event.uniqueId] = player
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val profile: Profile = getLoadedPlayer(event.player) ?: return

        profile.asyncSave()
    }
}
