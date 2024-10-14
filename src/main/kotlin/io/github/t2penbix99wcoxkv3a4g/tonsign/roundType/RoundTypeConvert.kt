package io.github.t2penbix99wcoxkv3a4g.tonsign.roundType

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

    fun getTextOfRound(round: RoundType, logRound: String): String {
        when (round) {
            RoundType.Classic -> return "Classic"
            RoundType.Fog -> return "Fog"
            RoundType.Punished -> return "Punished"
            RoundType.Sabotage -> return "Sabotage"
            RoundType.Cracked -> return "Cracked"
            RoundType.Alternate -> return "Alternate"
            RoundType.Bloodbath -> return "Bloodbath"
            RoundType.Midnight -> return "Midnight"
            RoundType.MysticMoon -> return "Mystic Moon"
            RoundType.Twilight -> return "Twilight"
            RoundType.Solstice -> return "Solstice"
            RoundType.EightPages -> return "8 Pages"
            RoundType.BloodMoon -> return "Blood Moon"
            RoundType.RUN -> return "RUN"
            RoundType.ColdNight -> return "Cold Night"
            RoundType.Unbound -> return "Unbound"
            RoundType.DoubleTrouble -> return "Double Trouble"
            RoundType.Ghost -> return "Ghost"
            else -> return "Unknown Type ($logRound)"
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
}