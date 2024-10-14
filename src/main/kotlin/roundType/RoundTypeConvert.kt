package roundType

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
}