@file:Suppress("unused")

package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

fun String.l10n() = LocalizationManager.get(this)
fun String.l10n(vararg objects: Any?) = LocalizationManager.get(this, *objects)
fun String.l10nByLang(lang: String) = LocalizationManager.getByLang(lang, this)
fun String.l10nByLang(lang: String, vararg objects: Any?) = LocalizationManager.getByLang(lang, this, *objects)
fun String.l10nWithEn() = LocalizationManager.getWithEn(this)
fun String.l10nWithEn(vararg objects: Any?) = LocalizationManager.getWithEn(this, *objects)
fun String.l10nState(vararg objects: Any?) = LocalizationManager.getState(this, *objects)
fun String.l10nStateWithEn(vararg objects: Any?) = LocalizationManager.getStateWithEn(this, *objects)
fun String.l10nExists() = LocalizationManager.exists(this)
fun String.l10nExists(lang: String) = LocalizationManager.exists(lang, this)