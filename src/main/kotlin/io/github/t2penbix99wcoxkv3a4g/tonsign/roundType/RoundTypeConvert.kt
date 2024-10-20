package io.github.t2penbix99wcoxkv3a4g.tonsign.roundType

import io.github.t2penbix99wcoxkv3a4g.tonsign.exception.UnknownRoundTypeException
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager

object RoundTypeConvert {
    val ENRoundTypes: List<String> = listOf(
        "Classic",
        "Fog",
        "Punished",
        "Sabotage",
        "Cracked",
        "Alternate",
        "Bloodbath",
        "Midnight",
        "Mystic Moon",
        "Twilight",
        "Solstice",
        "8 Pages",
        "Blood Moon",
        "RUN",
        "Cold Night",
        "Unbound",
        "Double Trouble",  // TODO: not sure
        "Ghost"
    )

    val JPRoundTypes: List<String> = listOf(
        "クラシック",
        "霧",
        "パニッシュ",
        "サボタージュ",
        "狂気",
        "オルタネイト",
        "ブラッドバス",
        "ミッドナイト",
        "ミスティックムーン",
        "トワイライト",
        "ソルスティス",
        "8ページ",
        "ブラッドバス",
        "走れ！",
        "寒い夜",
        "アンバウンド",
        "ダブル・トラブル",
        "ゴースト"
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
        RoundType.RUN
    )

    fun getTypeOfRound(round: String): RoundType {
        when (round) {
            "Classic" -> return RoundType.Classic
            "Fog" -> return RoundType.Fog
            "Punished" -> return RoundType.Punished
            "Sabotage" -> return RoundType.Sabotage
            "Cracked" -> return RoundType.Cracked
            "Alternate" -> return RoundType.Alternate
            "Bloodbath" -> return RoundType.Bloodbath
            "Midnight" -> return RoundType.Midnight
            "Mystic Moon" -> return RoundType.MysticMoon
            "Twilight" -> return RoundType.Twilight
            "Solstice" -> return RoundType.Solstice
            "8 Pages" -> return RoundType.EightPages
            "Blood Moon" -> return RoundType.BloodMoon
            "RUN" -> return RoundType.RUN
            "Cold Night" -> return RoundType.ColdNight
            "Unbound" -> return RoundType.Unbound
            "Double Trouble" -> return RoundType.DoubleTrouble
            "Ghost" -> return RoundType.Ghost
            else -> return RoundType.Unknown
        }
    }

    fun getTextOfRound(round: RoundType): String {
        when (round) {
            RoundType.Classic -> return LanguageManager.get("log.round_classic")
            RoundType.Fog -> return LanguageManager.get("log.round_fog")
            RoundType.Punished -> return LanguageManager.get("log.round_punished")
            RoundType.Sabotage -> return LanguageManager.get("log.round_sabotage")
            RoundType.Cracked -> return LanguageManager.get("log.round_cracked")
            RoundType.Alternate -> return LanguageManager.get("log.round_alternate")
            RoundType.Bloodbath -> return LanguageManager.get("log.round_bloodbath")
            RoundType.Midnight -> return LanguageManager.get("log.round_midnight")
            RoundType.MysticMoon -> return LanguageManager.get("log.round_mystic_moon")
            RoundType.Twilight -> return LanguageManager.get("log.round_twilight")
            RoundType.Solstice -> return LanguageManager.get("log.round_solstice")
            RoundType.EightPages -> return LanguageManager.get("log.round_8_pages")
            RoundType.BloodMoon -> return LanguageManager.get("log.round_blood_moon")
            RoundType.RUN -> return LanguageManager.get("log.round_run")
            RoundType.ColdNight -> return LanguageManager.get("log.round_cold_night")
            RoundType.Unbound -> return LanguageManager.get("log.round_unbound")
            RoundType.DoubleTrouble -> return LanguageManager.get("log.round_double_trouble")
            RoundType.Ghost -> return LanguageManager.get("log.round_ghost")
            else -> throw UnknownRoundTypeException()
        }
    }

    fun classifyRound(round: RoundType): GuessRoundType {
        if (round in ExemptRounds)
            return GuessRoundType.Exempt
        else if (round in SpecialRounds)
            return GuessRoundType.Special
        else if (round in ClassicRounds)
            return GuessRoundType.Classic
        return GuessRoundType.NIL
    }

    fun isSpecialOrClassic(round: RoundType): GuessRoundType {
        return when (round) {
            RoundType.Classic -> return GuessRoundType.Classic
            RoundType.Fog -> return GuessRoundType.Special
            RoundType.Punished -> return GuessRoundType.Special
            RoundType.Sabotage -> return GuessRoundType.Special
            RoundType.Cracked -> return GuessRoundType.Special
            RoundType.Alternate -> return GuessRoundType.Special
            RoundType.Bloodbath -> return GuessRoundType.Special
            RoundType.Midnight -> return GuessRoundType.Special
            RoundType.MysticMoon -> return GuessRoundType.Special
            RoundType.Twilight -> return GuessRoundType.Special
            RoundType.Solstice -> return GuessRoundType.Special
            RoundType.EightPages -> return GuessRoundType.Special
            RoundType.BloodMoon -> return GuessRoundType.Classic
            RoundType.RUN -> return GuessRoundType.Classic
            RoundType.ColdNight -> return GuessRoundType.Special
            RoundType.Unbound -> return GuessRoundType.Special
            RoundType.DoubleTrouble -> return GuessRoundType.Special
            RoundType.Ghost -> return GuessRoundType.Special
            else -> throw UnknownRoundTypeException()
        }
    }

    fun isCorrectGuess(lastGuess: GuessRoundType, round: RoundType): Boolean {
        if (lastGuess == GuessRoundType.NIL)
            return true
        return lastGuess == isSpecialOrClassic(round)
    }
}