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
import com.demonwav.mcdev.insight.generation.GenerationData
import com.demonwav.mcdev.inspection.IsCancelled
import com.demonwav.mcdev.platform.AbstractModule
import com.demonwav.mcdev.platform.PlatformType
import com.demonwav.mcdev.platform.cloud.util.CloudConstants
import com.demonwav.mcdev.platform.forge.inspections.sideonly.SidedProxyAnnotator
import com.demonwav.mcdev.platform.mcp.McpModuleSettings
import com.demonwav.mcdev.util.SemanticVersion
import com.demonwav.mcdev.util.SourceType
import com.demonwav.mcdev.util.extendsOrImplements
import com.demonwav.mcdev.util.nullable
import com.demonwav.mcdev.util.waitForAllSmart
import com.intellij.lang.jvm.JvmModifier
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbService
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiType
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UIdentifier
import org.jetbrains.uast.toUElementOfType

class CloudModule internal constructor(facet: MinecraftFacet) : AbstractModule(facet) {

    var mcmod by nullable { facet.findFile(CloudConstants.MCMOD_INFO, SourceType.RESOURCE) }
        private set

    override val moduleType = CloudModuleType
    override val type = PlatformType.CLOUD
    override val icon = PlatformAssets.CLOUD_ICON

    override fun init() {
        ApplicationManager.getApplication().executeOnPooledThread {
            waitForAllSmart()
            // Set mcmod.info icon
            /*runWriteTaskLater {
                FileTypeManager.getInstance().associatePattern(JsonFileType.INSTANCE, CloudConstants.MCMOD_INFO)
                FileTypeManager.getInstance().associatePattern(JsonFileType.INSTANCE, CloudConstants.PACK_MCMETA)
            }*/

            if (project.isDisposed) {
                return@executeOnPooledThread
            }

            // Index @SideOnly
            val service = DumbService.getInstance(project)
            service.runReadActionInSmartMode runSmart@{
                if (service.isDumb || project.isDisposed) {
                    return@runSmart
                }

                val scope = GlobalSearchScope.projectScope(project)
                val sidedProxy = JavaPsiFacade.getInstance(project)
                    .findClass(CloudConstants.SIDED_PROXY_ANNOTATION, scope) ?: return@runSmart
                val annotatedFields = AnnotatedElementsSearch.searchPsiFields(sidedProxy, scope).findAll()

                for (field in annotatedFields) {
                    if (service.isDumb || project.isDisposed) {
                        return@runSmart
                    }

                    SidedProxyAnnotator.check(field)
                }
            }
        }
    }

    override fun isEventClassValid(eventClass: PsiClass, method: PsiMethod?): Boolean {
        if (method == null) {
            return CloudConstants.FML_EVENT == eventClass.qualifiedName ||
                CloudConstants.EVENT == eventClass.qualifiedName ||
                CloudConstants.EVENTBUS_EVENT == eventClass.qualifiedName
        }

        var annotation = method.modifierList.findAnnotation(CloudConstants.EVENT_HANDLER_ANNOTATION)
        if (annotation != null) {
            return CloudConstants.FML_EVENT == eventClass.qualifiedName
        }

        annotation = method.modifierList.findAnnotation(CloudConstants.SUBSCRIBE_EVENT_ANNOTATION)
        if (annotation != null || method.hasAnnotation(CloudConstants.EVENTBUS_SUBSCRIBE_EVENT_ANNOTATION)) {
            return CloudConstants.EVENT == eventClass.qualifiedName ||
                CloudConstants.EVENTBUS_EVENT == eventClass.qualifiedName
        }

        // just default to true
        return true
    }

    override fun writeErrorMessageForEventParameter(eventClass: PsiClass, method: PsiMethod): String {
        val mcVersion = McpModuleSettings.getInstance(module).state.minecraftVersion
            ?.let { SemanticVersion.parse(it) }
        if (mcVersion != null && mcVersion >= CloudModuleType.FG3_MC_VERSION) {
            return formatWrongEventMessage(
                CloudConstants.EVENTBUS_EVENT,
                CloudConstants.EVENTBUS_SUBSCRIBE_EVENT_ANNOTATION,
                CloudConstants.EVENTBUS_EVENT == eventClass.qualifiedName
            )
        }

        val annotation = method.modifierList.findAnnotation(CloudConstants.EVENT_HANDLER_ANNOTATION)

        if (annotation != null) {
            return formatWrongEventMessage(
                CloudConstants.FML_EVENT,
                CloudConstants.SUBSCRIBE_EVENT_ANNOTATION,
                CloudConstants.EVENT == eventClass.qualifiedName
            )
        }

        return formatWrongEventMessage(
            CloudConstants.EVENT,
            CloudConstants.EVENT_HANDLER_ANNOTATION,
            CloudConstants.FML_EVENT == eventClass.qualifiedName
        )
    }

    private fun formatWrongEventMessage(expected: String, suggested: String, wrong: Boolean): String {
        val base = "Parameter is not a subclass of $expected\n"
        if (wrong) {
            return base + "This method should be annotated with $suggested"
        }
        return base + "Compiling and running this listener may result in a runtime exception"
    }

    override fun isStaticListenerSupported(method: PsiMethod) = true

    override fun generateEventListenerMethod(
        containingClass: PsiClass,
        chosenClass: PsiClass,
        chosenName: String,
        data: GenerationData?
    ): PsiMethod? {
        val isFmlEvent = chosenClass.extendsOrImplements(CloudConstants.FML_EVENT)

        val method = JavaPsiFacade.getElementFactory(project).createMethod(chosenName, PsiType.VOID)
        val parameterList = method.parameterList

        val qName = chosenClass.qualifiedName ?: return null
        val parameter = JavaPsiFacade.getElementFactory(project)
            .createParameter(
                "event",
                PsiClassType.getTypeByName(qName, project, GlobalSearchScope.allScope(project))
            )

        parameterList.add(parameter)
        val modifierList = method.modifierList

        if (isFmlEvent) {
            modifierList.addAnnotation(CloudConstants.EVENT_HANDLER_ANNOTATION)
        } else {
            val mcVersion = McpModuleSettings.getInstance(module).state.minecraftVersion
                ?.let { SemanticVersion.parse(it) }
            if (mcVersion != null && mcVersion >= CloudModuleType.FG3_MC_VERSION) {
                modifierList.addAnnotation(CloudConstants.EVENTBUS_SUBSCRIBE_EVENT_ANNOTATION)
            } else {
                modifierList.addAnnotation(CloudConstants.SUBSCRIBE_EVENT_ANNOTATION)
            }
        }

        return method
    }

    override fun shouldShowPluginIcon(element: PsiElement?): Boolean {
        val identifier = element?.toUElementOfType<UIdentifier>()
            ?: return false

        val psiClass = identifier.uastParent as? UClass
            ?: return false

        return !psiClass.hasModifier(JvmModifier.ABSTRACT) &&
            psiClass.findAnnotation(CloudConstants.MOD_ANNOTATION) != null
    }

    override fun checkUselessCancelCheck(expression: PsiMethodCallExpression): IsCancelled? = null

    override fun dispose() {
        mcmod = null
        super.dispose()
    }
}
