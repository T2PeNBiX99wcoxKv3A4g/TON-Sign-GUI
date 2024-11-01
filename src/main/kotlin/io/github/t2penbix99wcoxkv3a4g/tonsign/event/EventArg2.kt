@file:Suppress("unused")

package io.github.t2penbix99wcoxkv3a4g.tonsign.event

class EventArg2<T, B> {
    private val observers = mutableSetOf<(T, B) -> Unit>()

    operator fun plusAssign(observer: (T, B) -> Unit) {
        observers.add(observer)
    }

    operator fun minusAssign(observer: (T, B) -> Unit) {
        observers.remove(observer)
    }

    operator fun invoke(value: T, value2: B) {
        for (observer in observers)
            observer(value, value2)
    }
}