package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.logic.model

import androidx.compose.runtime.snapshots.SnapshotStateList
import io.github.t2penbix99wcoxkv3a4g.tonsign.manager.l10nWithEn
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundFlags
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType

class Terrors {
    @Suppress("MemberVisibilityCanBePrivate")
    val ids: ArrayList<Int>
    val roundType: RoundType

    @Suppress("MemberVisibilityCanBePrivate")
    val terrors: List<Terror>

    constructor(ids: ArrayList<Int>, roundType: RoundType, roundFlags: RoundFlags) {
        this.ids = ids
        this.roundType = roundType

        terrors = listOf(
            Terror(1, ids[0], roundType, roundFlags),
            Terror(2, ids[1], roundType, roundFlags),
            Terror(3, ids[2], roundType, roundFlags)
        )
    }

    constructor(ids: SnapshotStateList<Int>, roundType: RoundType, roundFlags: RoundFlags) {
        this.ids = arrayListOf(ids[0], ids[1], ids[2])
        this.roundType = roundType

        terrors = listOf(
            Terror(1, ids[0], roundType, roundFlags),
            Terror(2, ids[1], roundType, roundFlags),
            Terror(3, ids[2], roundType, roundFlags)
        )
    }

    private companion object {
        private const val LEVEL_TWE = "Lvl 2"

        private val onlyOneRounds = listOf(
            RoundType.MysticMoon,
            RoundType.BloodMoon,
            RoundType.Twilight,
            RoundType.Solstice,
            RoundType.Run
        )

        private val specialRounds = listOf(
            RoundType.Bloodbath,
            RoundType.Midnight
        )

        private val nonStandardRounds = listOf(
            RoundType.EightPages,
            RoundType.Unbound,
            RoundType.DoubleTrouble
        )

        // TODO: 8 pages terror ids
        private val eightPages = mapOf(
            arrayListOf(6, 2) to "tbh",
            arrayListOf(34, 11) to "slendy",
            arrayListOf(44, 8) to "dog_mimic",
            arrayListOf(10, 8) to "mx",
            arrayListOf(26, 4) to "miros_birds",
            arrayListOf(16, 13) to "demented_spongebob",
            arrayListOf(50, 5) to "searchlights",
            arrayListOf(29, 15) to "this_killer_does_not_exist",
            arrayListOf(46, 17) to "baldi",
            arrayListOf(17, 0) to "her",
            arrayListOf(25, 1) to "akumii-kari",
            arrayListOf(49, 17) to "sm64_z64"
        )

        private val unBounds = listOf(
            UnBoundData(
                name = "guidance&the_boo_boo_s",
                infos = listOf(
                    UnBoundInfo("normal.the_guidance", 2),
                    UnBoundInfo("unbound.boo_boo_babies", 3)
                )
            ),// 1
            UnBoundData(
                name = "red_vs_blue",
                infos = listOf(
                    UnBoundInfo("normal.haket"),
                    UnBoundInfo("unbound.blue_haket")
                )
            ),// 2
            UnBoundData(
                name = "third_trumpet",
                infos = listOf(
                    UnBoundInfo("normal.censored"),
                    UnBoundInfo("normal.all-around－helper", 2),
                    UnBoundInfo("unbound.mountain_of_smiling_bodies"),
                    UnBoundInfo("alternate.army_in_black"),
                    UnBoundInfo("normal.big_bird"),
                    UnBoundInfo("normal.express_train_to_hell")
                )
            ),// 3
            UnBoundData(
                name = "forest_gurdians",
                infos = listOf(
                    UnBoundInfo("normal.big_bird"),
                    UnBoundInfo("normal.judgement_bird"),
                    UnBoundInfo("normal.punishing_bird")
                )
            ),// 4
            UnBoundData(
                name = "higher_beings",
                infos = listOf(
                    UnBoundInfo("normal.security"),
                    UnBoundInfo("normal.the_swarm"),
                    UnBoundInfo("normal.prisoner")
                )
            ),// 5
            UnBoundData(
                name = "quadruple_sponge",
                infos = listOf(
                    UnBoundInfo("normal.demented_spongebob"),
                    UnBoundInfo("normal.spongefly_swarm"),
                    UnBoundInfo("alternate.decayed_sponge"),
                    UnBoundInfo("alternate.s_t_g_m")
                )
            ),// 6
            UnBoundData(
                name = "your_best_friends",
                infos = listOf(
                    UnBoundInfo("normal.bff", 2),
                    UnBoundInfo("midnight.nameless")
                )
            ),// 7
            UnBoundData(
                name = "hotel_monsters",
                infos = listOf(
                    UnBoundInfo("normal.seek"),
                    UnBoundInfo("normal.rush"),
                    UnBoundInfo("midnight.eyes")
                )
            ),// 8
            UnBoundData(
                name = "squibb_squad",
                infos = listOf(
                    UnBoundInfo("unbound.dev_bytes_squibbs", 3),
                    UnBoundInfo("unbound.convict_squad_squibbs")
                )
            ),// 9
            UnBoundData(
                name = "garden_rejects",
                infos = listOf(
                    UnBoundInfo("alternate.convict_squad"),
                    UnBoundInfo("midnight.kimera"),
                    UnBoundInfo("midnight.search_and_destroy")
                )
            ),// 10
            UnBoundData(
                name = "judgement_day",
                infos = listOf(
                    UnBoundInfo("normal.white_night"),
                    UnBoundInfo("alternate.paradise_bird")
                )
            ),// 11
            UnBoundData(
                name = "me_and_my_shadow",
                infos = listOf(
                    UnBoundInfo("alternate.roblander"),
                    UnBoundInfo("midnight.inverted_roblander")
                )
            ),// 12
            UnBoundData(
                name = "meltdown",
                infos = listOf(
                    UnBoundInfo("normal.an_arbiter"),
                    UnBoundInfo("alternate.the_red_mist")
                )
            ),// 13
            UnBoundData(
                name = "faceless_mafia",
                infos = listOf(
                    UnBoundInfo("normal.slender"),
                    UnBoundInfo("8_pages.slendy"),
                    UnBoundInfo("hide.normal.hungry_home_invader")
                )
            ),// 14
            UnBoundData(
                name = "mansion_monsters",
                infos = listOf(
                    UnBoundInfo("normal.specimen_2"),
                    UnBoundInfo("normal.specimen_5"),
                    UnBoundInfo("normal.specimen_8"),
                    UnBoundInfo("normal.specimen_10")
                )
            ),// 15
            UnBoundData(
                name = "copyright_infringement",
                infos = listOf(
                    UnBoundInfo("normal.mx"),
                    UnBoundInfo("normal.luigi&luigi_dolls"),
                    UnBoundInfo("normal.wario_apparition")
                )
            ),// 16
            UnBoundData(
                name = "purple_bros",
                infos = listOf(
                    UnBoundInfo("normal.purple_guy", 2)
                )
            ),// 17
            UnBoundData(
                name = "scavengers",
                infos = listOf(
                    UnBoundInfo("normal.scavenger", 3)
                )
            ),// 18
            UnBoundData(
                name = "life&death",
                infos = listOf(
                    UnBoundInfo("normal.the_life_bringer"),
                    UnBoundInfo("midnight.scrapyard_machine")
                )
            ),// 19
            UnBoundData(
                name = "labyrinth",
                infos = listOf(
                    UnBoundInfo("unbound.unknown_witch", 7)
                )
            ),// 20
            UnBoundData(
                name = "spiteful_shadows",
                infos = listOf(
                    UnBoundInfo("unbound.umbra", 2),
                    UnBoundInfo("unbound.spiteful_eye", 2)
                )
            ),// 21
            UnBoundData(
                name = "triple_munci",
                infos = listOf(
                    UnBoundInfo("alternate.angry_munci", 3)
                )
            ),// 22
            UnBoundData(
                name = "daycare",
                infos = listOf(
                    UnBoundInfo("normal.karol_corpse", 3)
                )
            ),// 23
            UnBoundData(
                name = "huggy_horde",
                infos = listOf(
                    UnBoundInfo("normal.huggy", 3)
                )
            ),// 24
            UnBoundData(
                name = "infection",
                infos = listOf(
                    UnBoundInfo("normal.arrival", 3)
                )
            ),// 25
            UnBoundData(
                name = "triple_hush",
                infos = listOf(
                    UnBoundInfo("normal.hush", 3)
                )
            ),// 26
            UnBoundData(
                name = "censored",
                infos = listOf(
                    UnBoundInfo("normal.censored", 3)
                )
            ),// 27
            UnBoundData(
                name = "byte_horde",
                infos = listOf(
                    UnBoundInfo("normal.dev_bytes"),
                    UnBoundInfo("unbound.vana"),
                    UnBoundInfo("unbound.duke")
                )
            ),// 28
            UnBoundData(
                name = "saw_marathon",
                infos = listOf(
                    UnBoundInfo("normal.sawrunner", 3)
                )
            ),// 29
            UnBoundData(
                name = "take_the_nami_challenge",
                infos = listOf(
                    UnBoundInfo("unbound.little_witch", 2),
                    UnBoundInfo("unbound.war"),
                    UnBoundInfo("unbound.convict_squad_nugget")
                )
            ),// 30
            // TODO: 31-84
        )
    }

    val names: List<String>
        get() {
            val list = mutableListOf<String>()

            if (roundType in nonStandardRounds) {
                val notFound = "gui.terror.name.not_found".l10nWithEn()

                if (ids[0] == Terror.UNKNOWN) {
                    val idString = "???"
                    val name = "gui.terror.name.unknown".l10nWithEn()
                    list.add("$idString $name")
                    return list.toList()
                }

                return when (roundType) {
                    RoundType.EightPages -> {
                        val id = arrayListOf(ids[0], ids[1])
                        val idStr = "${id[0] + 1}"
                        val idStr2 = "${id[1] + 1}"
                        val idString = "8PAGE ${idStr.padStart(3, '0')}+${idStr2.padStart(3, '0')}"

                        if (eightPages.containsKey(id)) {
                            val terrorName = "gui.terror.name.8_pages.${eightPages[id]}".l10nWithEn()
                            list.add("$idString $terrorName".trim())
                            return list.toList()
                        }

                        list.add("$idString $notFound".trim())
                        return list.toList()
                    }

                    RoundType.Unbound -> {
                        val id = ids[0]
                        val idStr = "${id + 1}"
                        val idString = "U${idStr.padStart(3, '0')}"

                        when {
                            id < 0 -> {
                                val loading = "gui.terror.name.still_in_loading".l10nWithEn()
                                list.add("$idString $loading".trim())
                                return list.toList()
                            }

                            id > 0 && id < unBounds.size -> {
                                val unBoundData = unBounds[id]
                                val unBoundName = "gui.terror.name.unbound.${unBoundData.name}".l10nWithEn()

                                list.add("$idString $unBoundName".trim())

                                unBoundData.infos.forEach {
                                    var name = "gui.terror.name.${it.name}".l10nWithEn()
                                    if (it.amount > 1)
                                        name += " (x${it.amount})"
                                    list.add(name.trim())
                                }

                                return list.toList()
                            }
                        }

                        list.add("$idString $notFound".trim())
                        return list.toList()
                    }

                    RoundType.DoubleTrouble -> {
                        return when {
                            ids[0] == ids[1] -> {
                                val name = "${terrors[0].name} $LEVEL_TWE"
                                list.add(name)
                                list.add(terrors[2].name)
                                list.toList()
                            }

                            ids[1] == ids[2] -> {
                                val name = "${terrors[1].name} $LEVEL_TWE"
                                list.add(terrors[0].name)
                                list.add(name)
                                list.toList()
                            }

                            ids[0] == ids[2] -> {
                                val name = "${terrors[0].name} $LEVEL_TWE"
                                list.add(name)
                                list.add(terrors[1].name)
                                list.toList()
                            }

                            else -> {
                                list.add(terrors[0].name)
                                list.add(terrors[1].name)
                                list.add(terrors[2].name)
                                list.toList()
                            }
                        }
                    }

                    else -> {
                        val idStr = "${ids[0] + 1}"
                        val idStr2 = "${ids[1] + 1}"
                        val idStr3 = "${ids[2] + 1}"
                        val idString = "${idStr.padStart(3, '0')} ${idStr2.padStart(3, '0')} ${idStr3.padStart(3, '0')}"
                        list.add("$idString $notFound")
                        list.toList()
                    }
                }
            }

            list.add(terrors[0].name)

            if (terrors[0].terrorId != Terror.UNKNOWN && roundType !in onlyOneRounds) {
                if (terrors[1].terrorId > 0 || roundType in specialRounds) list.add(terrors[1].name)

                if (terrors[2].terrorId > 0 || roundType in specialRounds) list.add(terrors[2].name)
            }

            return list.toList()
        }
}