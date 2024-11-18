package io.github.t2penbix99wcoxkv3a4g.tonsign.event

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe(val event: String)