package io.github.t2penbix99wcoxkv3a4g.tonsign.event

import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundType
import java.time.ZonedDateTime

class OnRoundStartEvent(val time: ZonedDateTime, val roundType: RoundType, val map: String, val mapId: Int) : Event