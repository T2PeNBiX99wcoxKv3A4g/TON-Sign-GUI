package io.github.t2penbix99wcoxkv3a4g.tonsign.roundType

import io.github.t2penbix99wcoxkv3a4g.tonsign.exception.UnknownRoundTypeException
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18n

object RoundTypeConvert {
    val ENRoundTypes: List<String> = listOf(
        "Classic",// 1
        "Fog",// 2
        "Punished",// 3
        "Sabotage",// 4
        "Among Us",// 5
        "Cracked",// 6
        "Alternate",// 7
        "Bloodbath",// 8
        "Midnight",// 9
        "Mystic Moon",// 10
        "Twilight",// 11
        "Solstice",// 12
        "8 Pages",// 13
        "Blood Moon",// 14
        "Run",// 15
        "Cold Night",// 16
        "Unbound",// 17
        "Double Trouble",// 18
        "Ghost",// 19
        "Custom"// 20
    )

    val JPRoundTypes: List<String> = listOf(
        "クラシック",// 1
        "霧",// 2
        "パニッシュ",// 3
        "サボタージュ",// 4
        "アモングアス",// 5
        "狂気",// 6
        "オルタネイト",// 7
        "ブラッドバス",// 8
        "ミッドナイト",// 9
        "ミスティックムーン",// 10
        "トワイライト",// 11
        "ソルスティス",// 12
        "8ページ",// 13
        "ブラッドムーン",// 14
        "走れ！",// 15
        "コールドナイト",// 16
        "アンバウンド",// 17
        "ダブル・トラブル",// 18
        "ゴースト",// 19
        "カスタム"// 20
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