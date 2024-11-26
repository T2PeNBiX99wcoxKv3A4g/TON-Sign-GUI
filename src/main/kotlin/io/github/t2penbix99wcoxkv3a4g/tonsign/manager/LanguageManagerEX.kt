@file:Suppress("unused")

package io.github.t2penbix99wcoxkv3a4g.tonsign.manager

fun String.i18n() = LanguageManager.get(this)
fun String.i18n(vararg objects: Any?) = LanguageManager.get(this, *objects)
fun String.i18nByLang(lang: String) = LanguageManager.getByLang(lang, this)
fun String.i18nByLang(lang: String, vararg objects: Any?) = LanguageManager.getByLang(lang, this, *objects)
fun String.i18nWithEn() = LanguageManager.getWithEn(this)
fun String.i18nWithEn(vararg objects: Any?) = LanguageManager.getWithEn(this, *objects)
fun String.i18nState(vararg objects: Any?) = LanguageManager.getState(this, *objects)
fun String.i18nStateWithEn(vararg objects: Any?) = LanguageManager.getStateWithEn(this, *objects)
fun String.i18nExists() = LanguageManager.exists(this)
fun String.i18nExists(lang: String) = LanguageManager.exists(lang, this)