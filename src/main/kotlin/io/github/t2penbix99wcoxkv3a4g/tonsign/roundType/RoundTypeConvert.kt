package io.github.t2penbix99wcoxkv3a4g.tonsign.roundType

import io.github.t2penbix99wcoxkv3a4g.tonsign.exception.UnknownRoundTypeException
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n

object RoundTypeConvert {
    val ENRoundTypes: List<String> = listOf(
        "Classic",
        "Fog",
        "Punished",
        "Sabotage",
        "Among Us",
        "Cracked",
        "Alternate",
        "Bloodbath",
        "Midnight",
        "Mystic Moon",
        "Twilight",
        "Solstice",
        "8 Pages",
        "Blood Moon",
        "Run",
        "Cold Night",
        "Unbound",
        "Double Trouble",
        "Ghost",
        "Custom"
    )

    val JPRoundTypes: List<String> = listOf(
        "クラシック",
        "霧",
        "パニッシュ",
        "サボタージュ",
        "アモングアス",
        "狂気",
        "オルタネイト",
        "ブラッドバス",
        "ミッドナイト",
        "ミスティックムーン",
        "トワイライト",
        "ソルスティス",
        "8ページ",
        "ブラッドムーン",
        "走れ！",
        "冷たい夜",
        "アンバウンド",
        "ダブル・トラブル",
        "ゴースト",
        "カスタム"
    )

    val ExemptRounds: List<RoundType> = listOf(
        RoundType.MysticMoon,
        RoundType.Twilight,
        RoundType.Solstice
    )

    val SpecialRounds: List<RoundType> = listOf(
        RoundType.Fog,
        RoundType.Punished,
        RoundType.Sabotage,
        RoundType.Cracked,
        RoundType.Alternate,
        RoundType.Bloodbath,
        RoundType.Midnight,
        RoundType.EightPages,
        RoundType.ColdNight,
        RoundType.Unbound,
        RoundType.DoubleTrouble,
        RoundType.Ghost
    )

    val ClassicRounds: List<RoundType> = listOf(
        RoundType.Classic,
        RoundType.BloodMoon,
        RoundType.Run
    )

    fun getTypeOfRound(round: String): RoundType {
        val id = round.replace("8", "Eight").replace("RUN", "Run").replace(" ", "")
        return runCatching { RoundType.valueOf(id) }.getOrElse { RoundType.Unknown }
    }

    fun getTextOfRound(round: RoundType): String {
        if (round == RoundType.Unknown)
            throw UnknownRoundTypeException()

        var langText = ""
        var index = 0

        round.name.forEach {
            var newChar: Char? = null

            if (it.isUpperCase()) {
                if (index > 1) {
                    langText += '_'
                }
                newChar = it.lowercaseChar()
            }

            langText += newChar ?: it
            index++
        }

        if (langText == "eight_pages")
            langText = "8_pages"

        if (langText == "ru_n")
            langText = "run"

        return "log.round_$langText".i18n()
    }

    fun classifyRound(round: RoundType): GuessRoundType {
        return when (round) {
            in ExemptRounds -> GuessRoundType.Exempt
            in SpecialRounds -> GuessRoundType.Special
            in ClassicRounds -> GuessRoundType.Classic
            else -> GuessRoundType.NIL
        }
    }

    fun isSpecialOrClassic(round: RoundType): GuessRoundType {
        return when (round) {
            in ClassicRounds -> GuessRoundType.Classic
            in SpecialRounds, in ExemptRounds -> GuessRoundType.Special
            else -> throw UnknownRoundTypeException()
        }
    }

    fun isCorrectGuess(lastGuess: GuessRoundType, round: RoundType): Boolean {
        if (lastGuess == GuessRoundType.NIL)
            return true
        return lastGuess == isSpecialOrClassic(round)
    }

    fun jpToEn(round: String): String {
        if (round !in JPRoundTypes) return round
        return ENRoundTypes[JPRoundTypes.indexOf(round)]
    }
}

fun String.jpToEn() = RoundTypeConvert.jpToEn(this)
fun String.getTypeOfRound() = RoundTypeConvert.getTypeOfRound(this)
fun RoundType.getTextOfRound() = RoundTypeConvert.getTextOfRound(this)
fun RoundType.classifyRound() = RoundTypeConvert.classifyRound(this)
fun RoundType.isSpecialOrClassic() = RoundTypeConvert.isSpecialOrClassic(this)