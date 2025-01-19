package io.github.t2penbix99wcoxkv3a4g.tonsign.event

import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.GuessRoundType
import io.github.t2penbix99wcoxkv3a4g.tonsign.roundType.RoundFlags

class OnNextPredictionEvent(val nextPrediction: GuessRoundType, val roundFlags: RoundFlags) : Event