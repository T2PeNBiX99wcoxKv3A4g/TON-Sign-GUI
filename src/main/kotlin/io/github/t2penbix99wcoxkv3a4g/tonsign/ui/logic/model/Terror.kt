package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model

import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.i18nWithEn
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType

class Terror(val id: Int, val terrorId: Int, val roundType: RoundType) {
    companion object {
        const val UNKNOWN = 999
        const val HIDE = 500

        private val normals = listOf(
            "huggy",// 1
            "corrupted_toys",// 2
            "demented_spongebob",// 3
            "specimen_8", // 4
            "her",// 5
            "tails_doll",// 6
            "black_sun",// 7
            "aku_ball",// 8
            "ao_oni",// 9
            "toren_s_shadow",// 10
            "censored",// 11
            "white_night",// 12
            "an_arbiter",// 13
            "specimen_5",// 14
            "comedy",// 15
            "purple_guy",// 16
            "spongefly_swarm",// 17
            "hush",// 18
            "mope_mope",// 19
            "sawrunner",// 20
            "imposter",// 21
            "something",// 22
            "starved&furnace",// 23
            "the_painter",// 24
            "the_guidance",// 25
            "with_many_voices",// 26
            "nextbots",// 27
            "harvest",// 28
            "smileghost",// 29
            "karol_corpse",// 30
            "mx",// 31
            "big_bird",// 32
            "dev_bytes",// 33
            "luigi&luigi_dolls",// 34
            "v2",// 35
            "withered_bonnie",// 36
            "the_boys",// 37
            "something_wicked",// 38
            "seek",// 39
            "rush",// 40
            "sonic",// 41
            "bad_batter",// 42
            "signus",// 43
            "mirror",// 44
            "legs",// 45
            "mona&the_mountain",// 46
            "judgement_bird",// 47
            "slender",// 48
            "maul-a-child",// 49
            "garten_goers",// 50
            "don_t_touch_me",// 51
            "specimen_2",// 52
            "specimen_10",// 53
            "the_life_bringer",// 54
            "pale_association",// 55
            "toy_enforcer",// 56
            "tbh",// 57
            "doom_box",// 58
            "christian_brutal_sniper",// 59
            "nosk",// 60
            "apocrean_harvester",// 61
            "arkus",// 62
            "cartoon_cat",// 63
            "wario_apparition",// 64
            "shinto",// 65
            "hell_bell",// 66
            "security",// 67
            "the_swarm",// 68
            "shiteyanyo",// 69
            "bacteria",// 70
            "tiffany",// 71
            "hoovy_dundy",// 72
            "haket",// 73
            "akumii-kari",// 74
            "lunatic_cultist",// 75
            "sturm",// 76
            "punishing_bird",// 77
            "prisoner",// 78
            "red_bus",// 79
            "waterwraith",// 80
            "astrum_aureus",// 81
            "snarbolax",// 82
            "all-around－helper",// 83
            "lain",// 84
            "arrival",// 85
            "bff",// 86 TODO: Not sure
            "miros_birds",// 87
            "sakuya_izayoi",// 88 TODO: Not sure
            "scavenger",// 89
            "tinky_winky",// 90
            "tricky",// 91
            "yolm",// 92
            "red_fanatic",// 93
            "dr_tox",// 94
            "ink_demon",// 95
            "retep",// 96
            "those_olden_days",// 97
            "s_o_s",// 98
            "bigger_boot",// 99
            "the_pursuer",// 100
            "spamton",// 101
            "immortal_snail",// 102
            "charlotte",// 103
            "herobrine",// 104
            "peepy",// 105
            "the_jester",// 106
            "wild_yet_curious_creature",// 107
            "manti",// 108
            "horseless_headless_horsemann",// 109
            "ghost_girl",// 110
            "cubor_s_revenge",// 111
            "poly",// 112
            "fox_squad",// 113 TODO: Not sure
            "warden",// 114
            "deleted",// 115
            "express_train_to_hell",// 114 TODO: Not sure
            "dog_mimic",// 117
            "killer_fish",// 118 TODO: Not sure
            "terror_of_nowhere",// 119
            "beyond",// 120
            "the_origin",// 121
            "time_ripper",// 122
            "this_killer_does_not_exist", // 123 TODO: Not sure
            "parhelion_s_victims",// 124
            "bed_mecha",// 125
            "killer_rabbit",// 126
            "random_flying_knife",// 127
            "missing_no",// 128
            "living_shadow",// 129
            "the_plague_doctor",// 130
            "the_rat",// 131
            "waldo",// 132
            "clockey",// 133
            "malicious_twins"// 134
        )

        private val alternates = listOf(
            "decayed_sponge",// 1
            "whiteface",// 2
            "joy",// 3
            "parhelion",// 4
            "a005",// 5
            "voidwalker",// 6
            "knight_of_toren",// 7
            "tragedy",// 8
            "apathy",// 9
            "mr_mega",// 10
            "sm64_z64",// 11
            "convict_squad",// 12
            "paradise_bird",// 13
            "angry_munci",// 14
            "lord_s_signal",// 15
            "deddys",// 16
            "tbh_spy",// 17
            "the_observation",// 18
            "lisa",// 19
            "judas",// 20
            "glaggle_gang",// 21
            "try_not_to_touch_me",// 22
            "ambush",// 23
            "teuthida",// 24
            "eggman_s_announcement",// 25
            "s_t_g_m",// 26
            "army_in_black",// 27
            "lone_agent",// 28
            "roblander",// 29
            "fusion_pilot",// 30
            "walpurgisnacht",// 31
            "sanic", // 32 TODO: Maybe is wrong id
            "sakuya_the_ripper",// 33
            "dev_maulers",// 34
            "the_red_mist",// 35
            "restless_creator",// 36
            "overseer" // 37 Should not be show in game but still add it
        )

        private val hideNormals = listOf(
            "hungry_home_invader",
            "atrached",
            "wild_yet_bloodthirsty_creature"
        )

        private val hideUnbounds = listOf(
            "transportation_trio&the_drifter"
        )

        private val hide8Pages = listOf(
            "red_mist_apparition",
            "baldi",
            "shadow_freddy",
            "searchlights",
            "alternates"
        )
    }
    
    val name: String
        get() {
            return "${idToString()} ${nameFromID()}".trim()
        }

    private fun terrorType(): String {
        when {
            roundType == RoundType.Midnight && id == 3 -> return "A"
        }

        return when (roundType) {
            RoundType.Alternate -> "A"
            else -> "T"
        }
    }

    private fun isHideTerror(): Boolean {
        return terrorId >= HIDE
    }

    private fun idToString(): String {
        when {
            roundType == RoundType.MysticMoon || roundType == RoundType.BloodMoon || roundType == RoundType.Twilight || roundType == RoundType.Solstice || roundType == RoundType.Run -> return ""
            terrorId == UNKNOWN -> return "???"
        }
        var idStr = "${terrorId + 1}"
        if (isHideTerror())
            idStr = "${terrorId - HIDE + 1}"
        return "${terrorType()}${idStr.padStart(3, '0')}"
    }

    private fun nameFromID(): String {
        val notFound = "gui.terror.name.not_found".i18nWithEn()

        if (terrorId < 0) return "gui.terror.name.still_in_loading".i18nWithEn()
        if (terrorId == UNKNOWN) return "gui.terror.name.unknown".i18nWithEn()

        if (isHideTerror()) {
            val newId = terrorId - HIDE

            return when (roundType) {
                RoundType.Unbound -> {
                    if (newId >= hideUnbounds.size) return notFound
                    return "gui.terror.name.hide_unbound.${hideNormals[newId]}".i18nWithEn()
                }

                RoundType.EightPages -> {
                    if (newId >= hide8Pages.size) return notFound
                    return "gui.terror.name.hide_8_pages.${hideNormals[newId]}".i18nWithEn()
                }

                else -> {
                    if (newId >= hideNormals.size) return notFound
                    return "gui.terror.name.hide_normal.${hideNormals[newId]}".i18nWithEn()
                }
            }
        }
        
        when {
            roundType == RoundType.Midnight && id == 3 -> {
                if (terrorId >= alternates.size) return notFound
                return "gui.terror.name.alternate.${alternates[terrorId]}".i18nWithEn()
            }
        }

        return when (roundType) {
            RoundType.Alternate -> {
                if (terrorId >= alternates.size) return notFound
                return "gui.terror.name.alternate.${alternates[terrorId]}".i18nWithEn()
            }

            RoundType.MysticMoon -> "gui.terror.name.mystic_moon.psychosis".i18nWithEn()
            RoundType.BloodMoon -> "gui.terror.name.blood_moon.virus".i18nWithEn()
            RoundType.Twilight -> "gui.terror.name.twilight.apocalypse_bird".i18nWithEn()
            RoundType.Solstice -> "gui.terror.name.solstice.pandora".i18nWithEn()
            RoundType.Run -> "gui.terror.name.run.the_meat_ball_man".i18nWithEn()

            else -> {
                if (terrorId >= normals.size) return notFound
                return "gui.terror.name.normal.${normals[terrorId]}".i18nWithEn()
            }
        }
    }
}