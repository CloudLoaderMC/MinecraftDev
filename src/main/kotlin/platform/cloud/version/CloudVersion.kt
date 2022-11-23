/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2021 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.cloud.version

import com.demonwav.mcdev.util.SemanticVersion
import com.demonwav.mcdev.util.sortVersions
import java.io.IOException
import java.net.URL
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.XMLEvent

class CloudVersion private constructor(val versions: List<String>) {

    val sortedMcVersions: List<SemanticVersion> by lazy {
        val unsortedVersions = versions.asSequence()
            .mapNotNull(
                fun(version: String): String? {
                    val index = version.indexOf('-')
                    if (index == -1) {
                        return null
                    }
                    return version.substring(0, index)
                }
            ).distinct()
            .toList()
        return@lazy sortVersions(unsortedVersions)
    }

    fun getCloudVersions(mcVersion: SemanticVersion): List<SemanticVersion> {
        val versionText = mcVersion.toString()
        return versions.asSequence()
            .filter { it.substringBefore('-') == versionText }
            .mapNotNull {
                try {
                    SemanticVersion.parse(it.substringAfter('-'))
                } catch (ignore: Exception) {
                    null
                }
            }
            .sortedDescending()
            .take(50)
            .toList()
    }

    companion object {
        fun downloadData(): CloudVersion? {
            try {
                val url = URL("https://maven.cloudmc.ml/releases/ml/cloudmc/cloudloader/maven-metadata.xml")
                val result = mutableListOf<String>()
                url.openStream().use { stream ->
                    val inputFactory = XMLInputFactory.newInstance()

                    @Suppress("UNCHECKED_CAST")
                    val reader = inputFactory.createXMLEventReader(stream) as Iterator<XMLEvent>
                    for (event in reader) {
                        if (!event.isStartElement) {
                            continue
                        }
                        val start = event.asStartElement()
                        val name = start.name.localPart
                        if (name != "version") {
                            continue
                        }

                        val versionEvent = reader.next()
                        if (!versionEvent.isCharacters) {
                            continue
                        }
                        val version = versionEvent.asCharacters().data
                        val index = version.indexOf('-')
                        if (index == -1) {
                            continue
                        }

                        result += version
                    }
                }

                return CloudVersion(result)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }
}
