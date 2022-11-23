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

import com.demonwav.mcdev.asset.PlatformAssets
import com.demonwav.mcdev.facet.MinecraftFacet
import com.demonwav.mcdev.platform.AbstractModuleType
import com.demonwav.mcdev.platform.PlatformType
import com.demonwav.mcdev.platform.cloud.util.CloudConstants
import com.demonwav.mcdev.util.SemanticVersion
import com.intellij.psi.PsiClass

object CloudModuleType : AbstractModuleType<CloudModule>("", "") {

    private const val ID = "CLOUD_MODULE_TYPE"

    private val IGNORED_ANNOTATIONS = listOf(
        CloudConstants.MOD_ANNOTATION,
        CloudConstants.EVENT_HANDLER_ANNOTATION,
        CloudConstants.SUBSCRIBE_EVENT_ANNOTATION,
        CloudConstants.EVENTBUS_SUBSCRIBE_EVENT_ANNOTATION
    )
    private val LISTENER_ANNOTATIONS = listOf(
        CloudConstants.EVENT_HANDLER_ANNOTATION,
        CloudConstants.SUBSCRIBE_EVENT_ANNOTATION,
        CloudConstants.EVENTBUS_SUBSCRIBE_EVENT_ANNOTATION
    )

    override val platformType = PlatformType.CLOUD
    override val icon = PlatformAssets.CLOUD_ICON
    override val id = ID
    override val ignoredAnnotations = IGNORED_ANNOTATIONS
    override val listenerAnnotations = LISTENER_ANNOTATIONS
    override val isEventGenAvailable = true

    override fun generateModule(facet: MinecraftFacet) = CloudModule(facet)
    override fun getDefaultListenerName(psiClass: PsiClass): String = defaultNameForSubClassEvents(psiClass)

    val FG23_MC_VERSION = SemanticVersion.release(1, 12)
    val FG3_MC_VERSION = SemanticVersion.release(1, 13)
    val FG3_CLOUD_VERSION = SemanticVersion.release(14, 23, 5, 2851)
}
