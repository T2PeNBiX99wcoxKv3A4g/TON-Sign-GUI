@file:Suppress("unused")

package io.github.t2penbix99wcoxkv3a4g.tonsign.event

class EventArg5<T, B, H, A, E> {
    private val observers = mutableSetOf<(T, B, H, A, E) -> Unit>()

    operator fun plusAssign(observer: (T, B, H, A, E) -> Unit) {
        observers.add(observer)
    }

    operator fun minusAssign(observer: (T, B, H, A, E) -> Unit) {
        observers.remove(observer)
    }

    operator fun invoke(value: T, value2: B, value3: H, value4: A, value5: E) {
        for (observer in observers)
            observer(value, value2, value3, value4, value5)
    }
}