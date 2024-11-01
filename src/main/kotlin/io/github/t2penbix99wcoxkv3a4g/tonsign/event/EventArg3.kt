@file:Suppress("unused")

package io.github.t2penbix99wcoxkv3a4g.tonsign.event

class EventArg3<T, B, H> {
    private val observers = mutableSetOf<(T, B, H) -> Unit>()

    operator fun plusAssign(observer: (T, B, H) -> Unit) {
        observers.add(observer)
    }

    operator fun minusAssign(observer: (T, B, H) -> Unit) {
        observers.remove(observer)
    }

    operator fun invoke(value: T, value2: B, value3: H) {
        for (observer in observers)
            observer(value, value2, value3)
    }
}