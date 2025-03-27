package org.llvm4j.optional

/**
 * Construct a [Result] of [T] and [E] from a closure, transforming any caught exceptions of type [E] into [Err]
 *
 * @throws IllegalStateException when the closure throws an exception which is not of type [E]
 */
@JvmSynthetic
public inline fun <T, reified E : Throwable> result(closure: () -> T): Result<T, E> {
    return try {
        Ok(closure.invoke())
    } catch (exception: Throwable) {
        if (exception is E) {
            Err(exception)
        } else {
            throw IllegalStateException("result closure threw unexpected exception type", exception)
        }
    }
}