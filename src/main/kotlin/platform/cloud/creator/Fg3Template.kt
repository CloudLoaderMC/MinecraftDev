/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2021 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.cloud.creator

import com.demonwav.mcdev.creator.buildsystem.BuildSystem
import com.demonwav.mcdev.platform.BaseTemplate
import com.demonwav.mcdev.platform.cloud.util.CloudConstants
import com.demonwav.mcdev.platform.cloud.util.CloudPackAdditionalData
import com.demonwav.mcdev.platform.cloud.util.CloudPackDescriptor
import com.demonwav.mcdev.util.MinecraftTemplates.Companion.FG3_1_17_MAIN_CLASS_TEMPLATE
import com.demonwav.mcdev.util.MinecraftTemplates.Companion.FG3_1_18_MAIN_CLASS_TEMPLATE
import com.demonwav.mcdev.util.MinecraftTemplates.Companion.FG3_1_19_MAIN_CLASS_TEMPLATE
import com.demonwav.mcdev.util.MinecraftTemplates.Companion.FG3_BUILD_GRADLE_TEMPLATE
import com.demonwav.mcdev.util.MinecraftTemplates.Companion.FG3_GRADLE_PROPERTIES_TEMPLATE
import com.demonwav.mcdev.util.MinecraftTemplates.Companion.FG3_MAIN_CLASS_TEMPLATE
import com.demonwav.mcdev.util.MinecraftTemplates.Companion.FG3_SETTINGS_GRADLE_TEMPLATE
import com.demonwav.mcdev.util.MinecraftTemplates.Companion.FG3_SUBMODULE_BUILD_GRADLE_TEMPLATE
import com.demonwav.mcdev.util.MinecraftTemplates.Companion.FORGE_MIXINS_JSON_TEMPLATE
import com.demonwav.mcdev.util.MinecraftTemplates.Companion.MODS_TOML_TEMPLATE
import com.demonwav.mcdev.util.MinecraftTemplates.Companion.PACK_MCMETA_TEMPLATE
import com.demonwav.mcdev.util.MinecraftVersions
import com.demonwav.mcdev.util.SemanticVersion
import com.demonwav.mcdev.util.toPackageName
import com.intellij.openapi.project.Project

object Fg3Template : BaseTemplate() {

    fun applyMainClass(
        project: Project,
        buildSystem: BuildSystem,
        config: CloudProjectConfig,
        packageName: String,
        className: String
    ): String {
        val props = mapOf(
            "PACKAGE_NAME" to packageName,
            "CLASS_NAME" to className,
            "ARTIFACT_ID" to buildSystem.artifactId,
            "MOD_NAME" to config.pluginName,
            "MOD_VERSION" to buildSystem.version
        )

        return project.applyTemplate(FG3_MAIN_CLASS_TEMPLATE, props)
    }

    fun apply1_17MainClass(
        project: Project,
        buildSystem: BuildSystem,
        config: CloudProjectConfig,
        packageName: String,
        className: String
    ): String {
        val props = mapOf(
            "PACKAGE_NAME" to packageName,
            "CLASS_NAME" to className,
            "ARTIFACT_ID" to buildSystem.artifactId,
            "MOD_NAME" to config.pluginName,
            "MOD_VERSION" to buildSystem.version
        )

        return project.applyTemplate(FG3_1_17_MAIN_CLASS_TEMPLATE, props)
    }

    fun apply1_18MainClass(
        project: Project,
        buildSystem: BuildSystem,
        config: CloudProjectConfig,
        packageName: String,
        className: String
    ): String {
        val props = mapOf(
            "PACKAGE_NAME" to packageName,
            "CLASS_NAME" to className,
            "ARTIFACT_ID" to buildSystem.artifactId,
            "MOD_NAME" to config.pluginName,
            "MOD_VERSION" to buildSystem.version
        )

        return project.applyTemplate(FG3_1_18_MAIN_CLASS_TEMPLATE, props)
    }

    fun apply1_19MainClass(
        project: Project,
        buildSystem: BuildSystem,
        config: CloudProjectConfig,
        packageName: String,
        className: String
    ): String {
        val props = mapOf(
            "PACKAGE_NAME" to packageName,
            "CLASS_NAME" to className,
            "ARTIFACT_ID" to buildSystem.artifactId,
            "MOD_NAME" to config.pluginName,
            "MOD_VERSION" to buildSystem.version
        )

        return project.applyTemplate(FG3_1_19_MAIN_CLASS_TEMPLATE, props)
    }

    fun applyBuildGradle(
        project: Project,
        buildSystem: BuildSystem,
        config: CloudProjectConfig,
        modName: String,
        hasData: Boolean
    ): String {
        val (channel, version) = config.mcpVersion.mcpVersion.split('_', limit = 2)
        val props = mutableMapOf<String, Any>(
            "MOD_NAME" to modName,
            "MCP_CHANNEL" to channel,
            "MCP_VERSION" to version,
            "MCP_MC_VERSION" to config.mcpVersion.mcVersion.toString(),
            "CLOUD_VERSION" to config.cloudVersionText,
            "FORGE_VERSION" to config.forgeVersionText,
            "FABRIC_VERSION" to config.fabricLoaderVersion.toString(),
            "FABRIC_API_VERSION" to config.fabricApiVersion.toString(),
            "GROUP_ID" to buildSystem.groupId,
            "ARTIFACT_ID" to buildSystem.artifactId,
            "MOD_VERSION" to buildSystem.version,
            "JAVA_VERSION" to config.javaVersion.feature
        )
        if (hasData) {
            props["HAS_DATA"] = "true"
        }
        if (config.hasAuthors()) {
            props["AUTHOR_LIST"] = config.authors.joinToString(", ")
        }
        /*if (config.mixins) {
            props["MIXINS"] = "true"
        }*/
        props["MIXINS"] = "true"
        if (config.cloudVersion >= SemanticVersion.release(39, 0, 88)) {
            props["GAME_TEST_FRAMEWORK"] = "true"
        }

        return project.applyTemplate(FG3_BUILD_GRADLE_TEMPLATE, props)
    }

    fun applyGradleProp(project: Project): String {
        return project.applyTemplate(FG3_GRADLE_PROPERTIES_TEMPLATE)
    }

    fun applySettingsGradle(project: Project, artifactId: String): String {
        val props = mapOf(
            "ARTIFACT_ID" to artifactId
        )

        return project.applyTemplate(FG3_SETTINGS_GRADLE_TEMPLATE, props)
    }

    fun applySubBuildGradle(
        project: Project,
        buildSystem: BuildSystem,
        config: CloudProjectConfig,
        modName: String,
        hasData: Boolean
    ): String {
        val (channel, version) = config.mcpVersion.mcpVersion.split('_', limit = 2)
        val props = mutableMapOf<String, Any>(
            "MOD_NAME" to modName,
            "MCP_CHANNEL" to channel,
            "MCP_VERSION" to version,
            "MCP_MC_VERSION" to config.mcpVersion.mcVersion.toString(),
            "CLOUD_VERSION" to config.cloudVersionText,
            "FORGE_VERSION" to config.forgeVersionText,
            "FABRIC_VERSION" to config.fabricLoaderVersion.toString(),
            "FABRIC_API_VERSION" to config.fabricApiVersion.toString(),
            "ARTIFACT_ID" to buildSystem.artifactId,
            "JAVA_VERSION" to if (config.mcVersion < MinecraftVersions.MC1_17) 8 else 16,
            "COMMON_PROJECT_NAME" to buildSystem.commonModuleName
        )
        if (hasData) {
            props["HAS_DATA"] = "true"
        }
        if (config.hasAuthors()) {
            props["AUTHOR_LIST"] = config.authors.joinToString(", ")
        }
        /*if (config.mixins) {
            props["MIXINS"] = "true"
        }*/
        props["MIXINS"] = "true"

        return project.applyTemplate(FG3_SUBMODULE_BUILD_GRADLE_TEMPLATE, props)
    }

    fun applyModsToml(project: Project, buildSystem: BuildSystem, config: CloudProjectConfig): String {
        val hasDisplayTestInManifest = config.cloudVersion >= CloudConstants.DISPLAY_TEST_MANIFEST_VERSION
        val nextMcVersion = when (val part = config.mcVersion.parts.getOrNull(1)) {
            // Mimics the code used to get the next Minecraft version in Forge's MDK
            // https://github.com/MinecraftForge/MinecraftForge/blob/0ff8a596fc1ef33d4070be89dd5cb4851f93f731/build.gradle#L884
            is SemanticVersion.Companion.VersionPart.ReleasePart -> (part.version + 1).toString()
            null -> "?"
            else -> part.versionString
        }
        val props = mutableMapOf(
            "ARTIFACT_ID" to buildSystem.artifactId,
            "MOD_NAME" to config.pluginName,
            "DISPLAY_TEST" to hasDisplayTestInManifest,
            "FORGE_SPEC_VERSION" to config.forgeVersion.parts[0].versionString,
            "MC_VERSION" to config.mcVersion.toString(),
            "MC_NEXT_VERSION" to "1.$nextMcVersion",
            "LICENSE" to config.license.toString()
        )
        props["DESCRIPTION"] = config.description ?: ""
        config.updateUrl?.let { url ->
            if (url.isNotBlank()) {
                props["UPDATE_URL"] = url
            }
        }
        if (config.hasAuthors()) {
            props["AUTHOR_LIST"] = config.authors.joinToString(", ")
        }

        return project.applyTemplate(MODS_TOML_TEMPLATE, props)
    }

    fun applyPackMcmeta(
        project: Project,
        artifactId: String,
        pack: CloudPackDescriptor,
        additionalData: CloudPackAdditionalData?
    ): String {
        val props = mapOf(
            "ARTIFACT_ID" to artifactId,
            "PACK_FORMAT" to pack.format.toString(),
            "PACK_COMMENT" to pack.comment,
            "CLOUD_DATA" to additionalData,
        )

        return project.applyTemplate(PACK_MCMETA_TEMPLATE, props)
    }

    fun applyMixinConfigTemplate(
        project: Project,
        buildSystem: BuildSystem
    ): String {
        val groupId = buildSystem.groupId.toPackageName()
        val artifactId = buildSystem.artifactId.toPackageName()
        val packageName = "$groupId.$artifactId.mixin"
        val props = mapOf(
            "PACKAGE_NAME" to packageName,
            "ARTIFACT_ID" to artifactId
        )
        return project.applyTemplate(FORGE_MIXINS_JSON_TEMPLATE, props)
    }
}
