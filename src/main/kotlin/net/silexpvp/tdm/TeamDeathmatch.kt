package net.silexpvp.tdm

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import net.silexpvp.nightmare.Nightmare
import net.silexpvp.tdm.listeners.PlayerListener
import net.silexpvp.tdm.profile.ProfileManager
import org.bson.Document
import org.bukkit.plugin.java.JavaPlugin

class TeamDeathmatch : JavaPlugin() {

    lateinit var nightmare: Nightmare

    lateinit var mongoDatabase: MongoDatabase
    lateinit var playersCollection: MongoCollection<Document>

    lateinit var profileManager: ProfileManager

    override fun onLoad() {
        nightmare = server.pluginManager.getPlugin("Nightmare") as Nightmare
    }

    override fun onEnable() {
        registerDatabase()
        registerManagers()
        registerListeners()
    }

    private fun registerDatabase() {
        mongoDatabase = nightmare.mongoConnection.client.getDatabase("tdm")
        playersCollection = mongoDatabase.getCollection("profiles")
    }

    private fun registerManagers() {
        profileManager = ProfileManager(this)
    }

    private fun registerListeners() {
        server.pluginManager.registerEvents(PlayerListener(this), this)
    }
}

