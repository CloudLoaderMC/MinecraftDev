/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2021 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.cloud.util

import com.demonwav.mcdev.platform.fabric.util.FabricConstants
import com.demonwav.mcdev.platform.forge.util.ForgeConstants

object CloudConstants {

    // Forge Stuff
    const val SIDED_PROXY_ANNOTATION = ForgeConstants.SIDED_PROXY_ANNOTATION
    const val MOD_ANNOTATION = ForgeConstants.MOD_ANNOTATION
    const val CORE_MOD_INTERFACE = ForgeConstants.CORE_MOD_INTERFACE
    const val EVENT_HANDLER_ANNOTATION = ForgeConstants.EVENT_HANDLER_ANNOTATION
    const val SUBSCRIBE_EVENT_ANNOTATION = ForgeConstants.SUBSCRIBE_EVENT_ANNOTATION
    const val EVENTBUS_SUBSCRIBE_EVENT_ANNOTATION = ForgeConstants.EVENTBUS_SUBSCRIBE_EVENT_ANNOTATION
    const val FML_EVENT = ForgeConstants.FML_EVENT
    const val EVENT = ForgeConstants.EVENT
    const val EVENTBUS_EVENT = ForgeConstants.EVENTBUS_EVENT
    const val NETWORK_MESSAGE = ForgeConstants.NETWORK_MESSAGE
    const val NETWORK_MESSAGE_HANDLER = ForgeConstants.NETWORK_MESSAGE_HANDLER
    const val MCMOD_INFO = ForgeConstants.MCMOD_INFO
    const val MODS_TOML = ForgeConstants.MODS_TOML
    const val PACK_MCMETA = ForgeConstants.PACK_MCMETA

    const val JAR_VERSION_VAR = ForgeConstants.JAR_VERSION_VAR

    // From https://github.com/CloudLoaderMC/CloudLoader/blob/1.19.x/fmlloader/src/main/java/net/minecraftforge/fml/loading/StringSubstitutor.java
    val KNOWN_SUBSTITUTIONS = setOf(
        JAR_VERSION_VAR,
        "\${global.mcVersion}",
        "\${global.cloudVersion}",
        "\${global.forgeVersion}",
        "\${global.fabricVersion}"
    )

    val DISPLAY_TESTS = ForgeConstants.DISPLAY_TESTS
    val DEPENDENCY_SIDES = ForgeConstants.DEPENDENCY_SIDES
    val DEPENDENCY_ORDER = ForgeConstants.DEPENDENCY_ORDER

    // From https://github.com/CloudLoaderMC/CloudLoader/blob/1.19.x/fmlloader/src/main/java/net/minecraftforge/fml/loading/moddiscovery/ModInfo.java#L35
    val FORGE_MOD_ID_REGEX = ForgeConstants.MOD_ID_REGEX

    val DISPLAY_TEST_MANIFEST_VERSION = ForgeConstants.DISPLAY_TEST_MANIFEST_VERSION

    // Fabric Stuff
    const val FABRIC_MOD_JSON = FabricConstants.FABRIC_MOD_JSON

    const val MOD_INITIALIZER = FabricConstants.MOD_INITIALIZER
    const val CLIENT_MOD_INITIALIZER = FabricConstants.CLIENT_MOD_INITIALIZER
    const val ENVIRONMENT_ANNOTATION = FabricConstants.ENVIRONMENT_ANNOTATION
    const val ENV_TYPE = FabricConstants.ENV_TYPE
    const val ENVIRONMENT_INTERFACE_ANNOTATION = FabricConstants.ENVIRONMENT_INTERFACE_ANNOTATION

    // From https://github.com/CloudLoaderMC/CloudLoader/blob/1.19.x/fmlloader/src/main/java/net/fabricmc/loader/impl/metadata/MetadataVerifier.java#L39
    val FABRIC_MOD_ID_REGEX = "[a-z][a-z0-9-_]{1,63}".toRegex()
}
