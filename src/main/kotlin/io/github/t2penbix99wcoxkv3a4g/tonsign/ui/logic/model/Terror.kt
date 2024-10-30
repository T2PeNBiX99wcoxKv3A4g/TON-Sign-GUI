package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model

import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.LanguageManager
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType

class Terror(terrorId: Int, val roundType: RoundType) {
    companion object {
        const val UNKNOWN = 999
        const val HIDE = 500

        private val normals = listOf(
            "huggy",
            "corrupted_toys",
            "demented_spongebob",
            "specimen_8",
            "her",
            "tails_doll",
            "black_sun",
            "aku_ball",
            "ao_oni",
            "toren_s_shadow",
            "censored",
            "white_night",
            "an_arbiter",
            "specimen_5",
            "comedy",
            "purple_guy",
            "spongefly_swarm",
            "hush",
            "mope_mope",
            "sawrunner",
            "imposter",
            "something",
            "starved&furnace",
            "the_painter",
            "the_guidance",
            "with_many_voices",
            "nextbots",
            "harvest",
            "smileghost",
            "karol_corpse",
            "mx",
            "big_bird",
            "dev_bytes",
            "luigi&luigi_dolls",
            "v2",
            "withered_bonnie",
            "the_boys",
            "something_wicked",
            "seek",
            "rush",
            "sonic",
            "bad_batter",
            "signus",
            "mirror",
            "legs",
            "mona&the_mountain",
            "judgement_bird",
            "slender",
            "maul-a-child",
            "garten_goers",
            "don_t_touch_me",
            "specimen_2",
            "specimen_10",
            "the_life_bringer",
            "pale_association",
            "toy_enforcer",
            "tbh",
            "doom_box",
            "christian_brutal_sniper",
            "nosk",
            "apocrean_harvester",
            "arkus",
            "cartoon_cat",
            "wario_apparition",
            "shinto",
            "hell_bell",
            "security",
            "the_swarm",
            "shiteyanyo",
            "bacteria",
            "tiffany",
            "hoovy_dundy",
            "haket",
            "akumii-kari",
            "lunatic_cultist",
            "sturm",
            "punishing_bird",
            "prisoner",
            "red_bus",
            "waterwraith",
            "astrum_aureus",
            "snarbolax",
            "all-around－helper",
            "sakuya_izayoi",
            "arrival",
            "miros_birds",
            "bff",
            "scavenger",
            "lain",
            "tinky_winky",
            "tricky",
            "yolm",
            "red_fanatic",
            "dr_tox",
            "ink_demon",
            "retep",
            "those_olden_days",
            "s_o_s",
            "bigger_boot",
            "the_pursuer",
            "spamton",
            "immortal_snail",
            "charlotte",
            "herobrine",
            "peepy",
            "the_jester",
            "wild_yet_curious_creature",
            "manti",
            "horseless_headless_horsemann",
            "ghost_girl",
            "cubor_s_revenge",
            "the_origin",
            "beyond",
            "terror_of_nowhere",
            "deleted",
            "poly",
            "dog_mimic",
            "warden",
            "express_train_to_hell",
            "fox_squad",
            "killer_fish",
            "time_ripper",
            "malicious_twins",
            "parhelion_s_victims",
            "bed_mecha",
            "killer_rabbit",
            "random_flying_knife",
            "missing_no",
            "living_shadow",
            "the_plague_doctor",
            "the_rat",
            "waldo",
            "clockey",
            "this_killer_does_not_exist"
        )

        private val alternates = listOf(
            "decayed_sponge",
            "whiteface",
            "joy",
            "sanic", // TODO: Maybe is wrong id
            "parhelion",
            "a005",
            "voidwalker",
            "knight_of_toren",
            "tragedy",
            "apathy",
            "mr_mega",
            "sm64_z64",
            "convict_squad",
            "paradise_bird",
            "angry_munci",
            "lord_s_signal",
            "deddys",
            "tbh_spy",
            "the_observation",
            "lisa",
            "judas",
            "glaggle_gang",
            "try_not_to_touch_me",
            "ambush",
            "teuthida",
            "eggman_s_announcement",
            "s_t_g_m",
            "army_in_black",
            "lone_agent",
            "roblander",
            "fusion_pilot",
            "walpurgisnacht",
            "sakuya_the_ripper",
            "dev_maulers",
            "the_red_mist",
            "restless_creator",
            "overseer" // Should not be show in game but still add it
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

    val id = terrorId
    val name: String
        get() {
            return "${idToString()} ${nameFromID()}".trim()
        }

    private fun terrorType(): String {
        if (isHideTerror())
            return "HIDE"

        return when (roundType) {
            RoundType.Alternate -> "A"
            else -> "T"
        }
    }

    private fun isHideTerror(): Boolean {
        return id >= HIDE
    }

    private fun idToString(): String {
        if (roundType == RoundType.MysticMoon || roundType == RoundType.BloodMoon || roundType == RoundType.Twilight || roundType == RoundType.Solstice || roundType == RoundType.RUN)
            return ""
        var idStr = "${id + 1}"
        if (id == UNKNOWN)
            return "${terrorType()}???"
        if (isHideTerror())
            idStr = "${id - HIDE + 1}"
        return "${terrorType()}${idStr.padStart(3, '0')}"
    }

    private fun nameFromID(): String {
        val notFound = LanguageManager.getWithEn("gui.terror.name.not_found")

        if (id < 0) return LanguageManager.getWithEn("gui.terror.name.still_in_loading")
        if (id == UNKNOWN) return LanguageManager.getWithEn("gui.terror.name.unknown")

        if (isHideTerror()) {
            val newId = id - HIDE

            return when (roundType) {
                RoundType.Unbound -> {
                    if (newId >= hideUnbounds.size) return notFound
                    return LanguageManager.getWithEn("gui.terror.name.hide_unbound.${hideNormals[newId]}")
                }

                RoundType.EightPages -> {
                    if (newId >= hide8Pages.size) return notFound
                    return LanguageManager.getWithEn("gui.terror.name.hide_8_pages.${hideNormals[newId]}")
                }

                else -> {
                    if (newId >= hideNormals.size) return notFound
                    return LanguageManager.getWithEn("gui.terror.name.hide_normal.${hideNormals[newId]}")
                }
            }
        }

        return when (roundType) {
            RoundType.Alternate -> {
                if (id >= alternates.size) return notFound
                return LanguageManager.getWithEn("gui.terror.name.alternate.${alternates[id]}")
            }

            RoundType.MysticMoon -> LanguageManager.getWithEn("gui.terror.name.mystic_moon.psychosis")
            RoundType.BloodMoon -> LanguageManager.getWithEn("gui.terror.name.blood_moon.virus")
            RoundType.Twilight -> LanguageManager.getWithEn("gui.terror.name.twilight.apocalypse_bird")
            RoundType.Solstice -> LanguageManager.getWithEn("gui.terror.name.solstice.pandora")
            RoundType.RUN -> LanguageManager.getWithEn("gui.terror.name.run.the_meat_ball_man")

            else -> {
                if (id >= normals.size) return notFound
                return LanguageManager.getWithEn("gui.terror.name.normal.${normals[id]}")
            }
        }
    }
}