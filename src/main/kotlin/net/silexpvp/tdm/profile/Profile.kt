package net.silexpvp.tdm.profile

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import net.silexpvp.tdm.TeamDeathmatch
import org.bson.Document
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class Profile {
    private val plugin = JavaPlugin.getPlugin(TeamDeathmatch::class.java)

    val uniqueId: UUID
    var name: String? = null

    var kills: Int = 0
    var deaths: Int = 0

    constructor(uniqueId: UUID) {
        this.uniqueId = uniqueId
    }

    constructor(document: Document) {
        uniqueId = document["_id"] as UUID
        name = document.getString("name")

        kills = document.getInteger("kills", 0)
        deaths = document.getInteger("deaths", 0)
    }

    fun serialize(): Document {
        val document = Document("_id", uniqueId)

        document["name"] = name

        if (kills != 0) {
            document["kills"] = kills
        }

        if (deaths != 0) {
            document["deaths"] = deaths
        }

        return document
    }

    fun save() {
        plugin.playersCollection.replaceOne(Filters.eq(uniqueId), serialize(), UpdateOptions().upsert(true))
    }

    fun asyncSave() {
        plugin.server.scheduler.runTaskAsynchronously(plugin) {
            save()
        }
    }

    fun asPlayer(): Player? {
        return plugin.server.getPlayer(name)
    }

    fun prepareForFight() {
        val player: Player = asPlayer() ?: return

        player.inventory.addItem(ItemStack(Material.DIAMOND_SWORD))
        player.inventory.addItem(ItemStack(Material.FISHING_ROD))

        player.inventory.helmet = ItemStack(Material.IRON_HELMET)
        player.inventory.chestplate = ItemStack(Material.IRON_CHESTPLATE)
        player.inventory.leggings = ItemStack(Material.IRON_LEGGINGS)
        player.inventory.boots = ItemStack(Material.IRON_BOOTS)
    }
}
