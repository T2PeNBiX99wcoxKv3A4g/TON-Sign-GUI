package io.github.t2penbix99wcoxkv3a4g.tonsign.event

import io.github.t2penbix99wcoxkv3a4g.tonsign.Utils
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.EventBusScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.EventCollectScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.coroutineScope.EventPushScope
import io.github.t2penbix99wcoxkv3a4g.tonsign.ex.firstPath
import io.github.t2penbix99wcoxkv3a4g.tonsign.exception.WrongFunctionException
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.Logger
import io.github.t2penbix99wcoxkv3a4g.tonsign.logger.debug
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.coroutineContext
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.isAccessible

object EventBus {
    private val classFunctions = ConcurrentHashMap<Any, List<KFunction<*>>>()
    private val functions = CopyOnWriteArrayList<KFunction<*>>()
    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    init {
        EventBusScope.launch {
            events.collect {
                call(it)
            }
        }
    }

    suspend fun publishSuspend(event: Event) {
        _events.emit(event)
    }

    fun publish(event: Event) = EventPushScope.launch {
        publishSuspend(event)
    }

    private fun <T : Event> call(event: T) {
        val name = event::class.simpleName!!.firstPath("Event")

        classFunctions.forEach {
            val eventScope = EventCollectScope(it.key::class.simpleName!!)

            eventScope.launch {
                it.value.filter { a -> a.findAnnotation<Subscribe>()?.event == name }.forEach { f ->
                    f.isAccessible = true
                    runCatching { f.call(it.key, event) }.getOrElse { throwable ->
                        Logger.debug<EventBus> { "Call: ${it.key} ${it.value} ${it::class.simpleName}" }
                        Logger.error(
                            throwable,
                            { "exception.something_is_not_right" },
                            throwable.localizedMessage,
                            throwable.stackTraceToString()
                        )
                    }
                }
            }
        }
        functions.filter { it.findAnnotation<Subscribe>()?.event == name }.forEach {
            val eventScope = EventCollectScope("Function(${it.name})")

            eventScope.launch {
                it.isAccessible = true
                runCatching { it.call(event) }.getOrElse { throwable ->
                    Logger.debug<EventBus> { "Call: $it ${it.name}" }
                    Logger.error(
                        throwable,
                        { "exception.something_is_not_right" },
                        throwable.localizedMessage,
                        throwable.stackTraceToString()
                    )
                }
            }
        }
    }

    @Suppress("unused")
    suspend inline fun <reified T : Event> subscribe(crossinline onEvent: (T) -> Unit) {
        events.filterIsInstance<T>()
            .collect { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun subscribe(func: KFunction<*>) {
        if (!func.hasAnnotation<Subscribe>())
            throw WrongFunctionException(func.name)
        Utils.logger.debug { "Register function: ${func.name} $func" }
        functions.add(func)
    }

    @Suppress("unused")
    fun subscribe(funcs: List<KFunction<*>>) {
        funcs.filter { it.hasAnnotation<Subscribe>() }.forEach {
            subscribe(it)
        }
    }

    @Suppress("unused")
    fun unsubscribe(func: KFunction<*>) {
        if (!func.hasAnnotation<Subscribe>())
            throw WrongFunctionException(func.name)
        if (!functions.contains(func)) return
        functions.remove(func)
    }

    fun <T : Any> register(clazz: T) {
        Utils.logger.debug { "Register $clazz" }
        EventPushScope.launch {
            val functions = mutableListOf<KFunction<*>>()

            clazz::class.declaredMemberFunctions.forEach {
                if (!it.hasAnnotation<Subscribe>()) return@forEach
                Utils.logger.debug { "Register function: ${it.name} $it" }
                functions.add(it)
            }

            classFunctions[clazz] = functions
        }
    }

    @Suppress("unused")
    fun <T : Any> unregister(clazz: T) {
        if (!classFunctions.containsKey(clazz)) return
        EventPushScope.launch {
            classFunctions.remove(clazz)
        }
    }
}