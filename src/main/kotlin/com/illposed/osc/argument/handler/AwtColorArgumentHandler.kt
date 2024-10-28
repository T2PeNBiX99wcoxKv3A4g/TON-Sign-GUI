// SPDX-FileCopyrightText: 2015-2017 C. Ramakrishnan / Illposed Software
// SPDX-FileCopyrightText: 2021 Robin Vobruba <hoijui.quaero@gmail.com>
//
// SPDX-License-Identifier: BSD-3-Clause
// https://github.com/hoijui/JavaOSC/blob/master/modules/java-se-addons/src/main/java/com/illposed/osc/argument/handler/AwtColorArgumentHandler.java
// Remove this file when issue is fixed.

package com.illposed.osc.argument.handler

import com.illposed.osc.BytesReceiver
import com.illposed.osc.OSCParseException
import com.illposed.osc.OSCSerializeException
import com.illposed.osc.argument.ArgumentHandler
import com.illposed.osc.argument.OSCColor
import java.awt.Color
import java.nio.ByteBuffer
import kotlin.jvm.Throws

/**
 * Parses and serializes an OSC 1.1 optional <i>32bit RGBA color</i> type.
 *
 * Note That this class is not in javaosc-core,
 * because it uses <code>java.awt.Color</code>,
 * which is part of Java SE,
 * but not part of the Java implementation on Android (Dalvik),
 * and thus would make JavaOSC unusable on Android,
 * if it were not separated.
 * It was part of javaosc-core until version 0.8.
 *
 * As an alternative on Android,
 * see <code>com.illposed.osc.argument.handler.ColorArgumentHandler</code>
 * in javaosc-core.
 */

open class AwtColorArgumentHandler : ArgumentHandler<Color>, Cloneable {
    companion object {
        @Suppress("unused")
        @JvmField
        val INSTANCE: ArgumentHandler<Color> = AwtColorArgumentHandler()

        @JvmStatic
        fun toAwt(color: OSCColor): Color {
            return Color(
                color.redInt,
                color.greenInt,
                color.blueInt,
                color.alphaInt
            )
        }

        @JvmStatic
        fun toOsc(color: Color): OSCColor {
            return OSCColor(
                color.red,
                color.green,
                color.blue,
                color.alpha
            )
        }
    }

    // Public API
    /** Allow overriding, but somewhat enforce the ugly singleton. */
    protected constructor() {
        // declared only for setting the access level
    }

    override fun getDefaultIdentifier(): Char {
        return 'r'
    }

    override fun getJavaClass(): Class<Color> {
        return Color::class.java
    }

    override fun setProperties(properties: Map<String, Any>) {
        // we make no use of any properties
    }

    override fun isMarkerOnly(): Boolean {
        return false
    }

    @Throws(CloneNotSupportedException::class)
    override fun clone(): ArgumentHandler<Color> {
        return super<Cloneable>.clone() as AwtColorArgumentHandler
    }

    @Throws(OSCParseException::class)
    override fun parse(input: ByteBuffer): Color {
        return toAwt(ColorArgumentHandler.INSTANCE.parse(input))
    }

    @Throws(OSCSerializeException::class)
    override fun serialize(output: BytesReceiver, value: Color) {
        ColorArgumentHandler.INSTANCE.serialize(output, toOsc(value))
    }
}