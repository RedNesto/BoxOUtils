package io.github.rednesto.bou.tests.framework

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import org.junit.jupiter.api.fail

abstract class ConfigHelper {

    protected abstract fun populateSerializers(serializers: TypeSerializerCollection)

    open fun loadNode(configuration: String): ConfigurationNode {
        val typeSerializers = TypeSerializers.newCollection()
        populateSerializers(typeSerializers)

        val loaderOptions = ConfigurationOptions.defaults()
                .setSerializers(typeSerializers)

        val loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(loaderOptions)
                .setSource { configuration.reader().buffered() }
                .build()
        return loader.load()
    }

    open fun <N> loadConfig(configuration: String, rootNodeKey: String, token: TypeToken<N>): N {
        val rootNode = loadNode(configuration)
        return rootNode.getNode(rootNodeKey).getValue(token) ?: fail("Configuration value is null")
    }

    companion object {

        fun create(serializersPopulator: (serializers: TypeSerializerCollection) -> Unit): ConfigHelper {
            return object : ConfigHelper() {
                override fun populateSerializers(serializers: TypeSerializerCollection) = serializersPopulator(serializers)
            }
        }
    }
}
