/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2021 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.cloud

import com.demonwav.mcdev.MinecraftSettings
import com.demonwav.mcdev.facet.MinecraftFacet
import com.intellij.ide.FileIconProvider
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

class CloudFileIconProvider : FileIconProvider {

    override fun getIcon(file: VirtualFile, @Iconable.IconFlags flags: Int, project: Project?): Icon? {
        project ?: return null

        if (!MinecraftSettings.instance.isShowProjectPlatformIcons) {
            return null
        }

        val module = ModuleUtilCore.findModuleForFile(file, project) ?: return null
        val cloudModule = MinecraftFacet.getInstance(module, CloudModuleType) ?: return null

        if (file == cloudModule.mcmod) {
            return cloudModule.icon
        }
        return null
    }
}
