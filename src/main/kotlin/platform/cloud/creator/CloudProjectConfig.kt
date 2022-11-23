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

import com.demonwav.mcdev.creator.ProjectConfig
import com.demonwav.mcdev.creator.ProjectCreator
import com.demonwav.mcdev.creator.buildsystem.BuildSystemType
import com.demonwav.mcdev.creator.buildsystem.gradle.GradleBuildSystem
import com.demonwav.mcdev.creator.buildsystem.gradle.GradleCreator
import com.demonwav.mcdev.platform.PlatformType
import com.demonwav.mcdev.platform.cloud.CloudModuleType
import com.demonwav.mcdev.platform.mcp.McpVersionPair
import com.demonwav.mcdev.util.License
import com.demonwav.mcdev.util.MinecraftVersions
import com.demonwav.mcdev.util.SemanticVersion
import com.demonwav.mcdev.util.VersionRange
import com.demonwav.mcdev.util.until
import com.intellij.openapi.module.Module
import com.intellij.util.lang.JavaVersion
import java.nio.file.Path

class CloudProjectConfig : ProjectConfig(), GradleCreator {

    lateinit var mainClass: String

    var updateUrl: String? = null

    override var type: PlatformType = PlatformType.CLOUD

    var mcpVersion = McpVersionPair("", SemanticVersion.release())
    var cloudVersionText: String = ""
    var cloudVersion: SemanticVersion = SemanticVersion.release()
    var forgeVersionText: String = ""
    var forgeVersion: SemanticVersion = SemanticVersion.release()
    var fabricLoaderVersion: SemanticVersion = SemanticVersion.release()
    var fabricApiVersion: SemanticVersion = SemanticVersion.release()
    var loomVersion = SemanticVersion.release()
    var fabricApi = true
    var mcVersion: SemanticVersion = SemanticVersion.release()
    var modRepo: String? = null
    fun hasRepo() = !modRepo.isNullOrBlank()
    var modIssue: String? = null
    fun hasIssue() = !modIssue.isNullOrBlank()

//    var mixins = false
    var license = License.ALL_RIGHTS_RESERVED

    override val preferredBuildSystem = BuildSystemType.GRADLE

    override val javaVersion: JavaVersion
        get() = MinecraftVersions.requiredJavaVersion(mcVersion)

    override val compatibleGradleVersions: VersionRange
        get() = when {
            isFg3(mcVersion, cloudVersion) -> Fg3ProjectCreator.FG5_WRAPPER_VERSION until null
            else -> VersionRange.fixed(Fg2ProjectCreator.FG_WRAPPER_VERSION)
        }

    override fun buildGradleCreator(
        rootDirectory: Path,
        module: Module,
        buildSystem: GradleBuildSystem
    ): ProjectCreator {
        return if (isFg3(mcVersion, cloudVersion)) {
            if (mcVersion >= CloudModuleType.FG3_MC_VERSION) {
                Fg3ProjectCreator(rootDirectory, module, buildSystem, this)
            } else {
                Fg3Mc112ProjectCreator(rootDirectory, module, buildSystem, this)
            }
        } else {
            Fg2ProjectCreator(rootDirectory, module, buildSystem, this, mcVersion)
        }
    }

    override fun configureRootGradle(
        rootDirectory: Path,
        buildSystem: GradleBuildSystem
    ) {
        buildSystem.gradleVersion = if (isFg3(mcVersion, cloudVersion)) {
            Fg3ProjectCreator.FG5_WRAPPER_VERSION
        } else {
            Fg2ProjectCreator.FG_WRAPPER_VERSION
        }
    }

    private fun isFg3(mcVersion: SemanticVersion, cloudVersion: SemanticVersion): Boolean {
        return mcVersion >= CloudModuleType.FG3_MC_VERSION || cloudVersion >= CloudModuleType.FG3_CLOUD_VERSION
    }
}
