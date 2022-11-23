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

import com.demonwav.mcdev.asset.PlatformAssets
import com.demonwav.mcdev.facet.MinecraftFacetConfiguration
import com.demonwav.mcdev.platform.PlatformType
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.ui.components.JBRadioButton
import com.intellij.util.ui.UIUtil
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class HybridLoaderSelectorWizardStep(private val configuration: MinecraftFacetConfiguration) : FacetEditorTab() {

    private lateinit var panel: JPanel

    private lateinit var bukkitEnabledCheckBox: JCheckBox
    private lateinit var bukkitMainButton: JBRadioButton
    private lateinit var spigotEnabledCheckBox: JCheckBox
    private lateinit var spigotMainButton: JBRadioButton
    private lateinit var paperEnabledCheckBox: JCheckBox
    private lateinit var paperMainButton: JBRadioButton
    private lateinit var spongeEnabledCheckBox: JCheckBox
    private lateinit var spongeMainButton: JBRadioButton
    private lateinit var forgeEnabledCheckBox: JCheckBox
    private lateinit var forgeMainButton: JBRadioButton
    private lateinit var fabricEnabledCheckBox: JCheckBox
    private lateinit var fabricMainButton: JBRadioButton
    private lateinit var architecturyEnabledCheckBox: JCheckBox
    private lateinit var architecturyMainButton: JBRadioButton
    private lateinit var liteloaderEnabledCheckBox: JCheckBox
    private lateinit var liteloaderMainButton: JBRadioButton
    private lateinit var bungeecordEnabledCheckBox: JCheckBox
    private lateinit var bungeecordMainButton: JBRadioButton
    private lateinit var waterfallEnabledCheckBox: JCheckBox
    private lateinit var waterfallMainButton: JBRadioButton
    private lateinit var velocityEnabledCheckBox: JCheckBox
    private lateinit var velocityMainButton: JBRadioButton

    private lateinit var spongeIcon: JLabel

    private val enableCheckBoxArray: Array<JCheckBox> by lazy {
        arrayOf(
            bukkitEnabledCheckBox,
            spigotEnabledCheckBox,
            paperEnabledCheckBox,
            spongeEnabledCheckBox,
            forgeEnabledCheckBox,
            fabricEnabledCheckBox,
            architecturyEnabledCheckBox,
            liteloaderEnabledCheckBox,
            bungeecordEnabledCheckBox,
            waterfallEnabledCheckBox,
            velocityEnabledCheckBox
        )
    }

    private val mainButtonArray: Array<JBRadioButton> by lazy {
        arrayOf(
            bukkitMainButton,
            spigotMainButton,
            paperMainButton,
            spongeMainButton,
            forgeMainButton,
            fabricMainButton,
            architecturyMainButton,
            liteloaderMainButton,
            bungeecordMainButton,
            waterfallMainButton,
            velocityMainButton
        )
    }

    override fun createComponent(): JComponent {
        if (UIUtil.isUnderDarcula()) {
            spongeIcon.icon = PlatformAssets.SPONGE_ICON_2X_DARK
        }

        runOnAll { enabled, auto, platformType, _, _ ->
            auto.addActionListener { checkAuto(auto, enabled, platformType) }
        }

        bukkitEnabledCheckBox.addActionListener {
            unique(
                bukkitEnabledCheckBox,
                spigotEnabledCheckBox,
                paperEnabledCheckBox
            )
        }
        spigotEnabledCheckBox.addActionListener {
            unique(
                spigotEnabledCheckBox,
                bukkitEnabledCheckBox,
                paperEnabledCheckBox
            )
        }
        paperEnabledCheckBox.addActionListener {
            unique(
                paperEnabledCheckBox,
                bukkitEnabledCheckBox,
                spigotEnabledCheckBox
            )
        }

        bukkitMainButton.addActionListener {
        }
        spigotMainButton.addActionListener {
        }
        paperMainButton.addActionListener {
        }

        forgeEnabledCheckBox.addActionListener {
            unique(forgeEnabledCheckBox, architecturyEnabledCheckBox)
        }
        fabricEnabledCheckBox.addActionListener {
            unique(fabricEnabledCheckBox, architecturyEnabledCheckBox)
        }
        architecturyEnabledCheckBox.addActionListener {
            unique(
                architecturyEnabledCheckBox,
                fabricEnabledCheckBox,
                forgeEnabledCheckBox
            )
        }

        forgeMainButton.addActionListener {
        }

        fabricMainButton.addActionListener {
        }

        architecturyMainButton.addActionListener {
        }

        liteloaderEnabledCheckBox.addActionListener { }

        bungeecordEnabledCheckBox.addActionListener { unique(bungeecordEnabledCheckBox, waterfallEnabledCheckBox) }
        waterfallEnabledCheckBox.addActionListener { unique(waterfallEnabledCheckBox, bungeecordEnabledCheckBox) }

        return panel
    }

    override fun getDisplayName() = "Minecraft Module Settings"

    override fun isModified(): Boolean {
        var modified = false

        runOnAll { enabled, auto, platformType, userTypes, _ ->
            modified += auto.isSelected == platformType in userTypes
            modified += !auto.isSelected && enabled.isSelected != userTypes[platformType]
        }

        return modified
    }

    override fun reset() {
        runOnAll { enabled, auto, platformType, userTypes, autoTypes ->
            auto.isSelected = platformType !in userTypes
            enabled.isSelected = userTypes[platformType] ?: (platformType in autoTypes)

            if (auto.isSelected) {
                enabled.isEnabled = false
            }
        }
    }

    override fun apply() {
        configuration.state.userChosenTypes.clear()
        runOnAll { enabled, auto, platformType, userTypes, _ ->
            if (!auto.isSelected) {
                userTypes[platformType] = enabled.isSelected
            }
        }
    }

    private inline fun runOnAll(
        run: (JCheckBox, JBRadioButton, PlatformType, MutableMap<PlatformType, Boolean>, Set<PlatformType>) -> Unit
    ) {
        val state = configuration.state
        for (i in indexes) {
            run(
                enableCheckBoxArray[i],
                mainButtonArray[i],
                platformTypes[i],
                state.userChosenTypes,
                state.autoDetectTypes
            )
        }
    }

    private fun unique(vararg checkBoxes: JCheckBox) {
        if (checkBoxes.size <= 1) {
            return
        }

        if (checkBoxes[0].isSelected) {
            for (i in 1 until checkBoxes.size) {
                checkBoxes[i].isSelected = false
            }
        }
    }

    private fun also(vararg checkBoxes: JCheckBox) {
        if (checkBoxes.size <= 1) {
            return
        }

        if (checkBoxes[0].isSelected) {
            for (i in 1 until checkBoxes.size) {
                checkBoxes[i].isSelected = true
            }
        }
    }

    private fun all(vararg checkBoxes: JCheckBox): Invoker {
        if (checkBoxes.size <= 1) {
            return Invoker()
        }

        for (i in 1 until checkBoxes.size) {
            checkBoxes[i].isSelected = checkBoxes[0].isSelected
        }

        return object : Invoker() {
            override fun invoke(vararg indexes: Int) {
                for (i in indexes) {
                    checkAuto(mainButtonArray[i], enableCheckBoxArray[i], platformTypes[i])
                }
            }
        }
    }

    private fun checkAuto(main: JBRadioButton, enabled: JCheckBox, type: PlatformType) {
        if (main.isSelected) {
            enabled.isEnabled = false
            enabled.isSelected = type in configuration.state.autoDetectTypes
        } else {
            enabled.isEnabled = true
        }
    }

    private operator fun Boolean.plus(n: Boolean) = this || n

    // This is here so we can use vararg. Can't use parameter modifiers in function type definitions for some reason
    open class Invoker {
        open operator fun invoke(vararg indexes: Int) {}
    }

    companion object {
        private const val BUKKIT = 0
        private const val SPIGOT = BUKKIT + 1
        private const val PAPER = SPIGOT + 1
        private const val SPONGE = PAPER + 1
        private const val FORGE = SPONGE + 1
        private const val FABRIC = FORGE + 1
        private const val ARCHITECTURY = FABRIC + 1
        private const val LITELOADER = ARCHITECTURY + 1
        private const val BUNGEECORD = LITELOADER + 1
        private const val WATERFALL = BUNGEECORD + 1
        private const val VELOCITY = WATERFALL + 1

        private val platformTypes = arrayOf(
            PlatformType.BUKKIT,
            PlatformType.SPIGOT,
            PlatformType.PAPER,
            PlatformType.SPONGE,
            PlatformType.FORGE,
            PlatformType.FABRIC,
            PlatformType.ARCHITECTURY,
            PlatformType.LITELOADER,
            PlatformType.BUNGEECORD,
            PlatformType.WATERFALL,
            PlatformType.VELOCITY
        )

        private val indexes = intArrayOf(
            BUKKIT,
            SPIGOT,
            PAPER,
            SPONGE,
            FORGE,
            FABRIC,
            ARCHITECTURY,
            LITELOADER,
            BUNGEECORD,
            WATERFALL,
            VELOCITY
        )
    }
}
