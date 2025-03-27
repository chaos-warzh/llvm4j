package org.llvm4j.optional

/**
 * An implementation of successful or failed result returned from an operation
 *
 * A [Result] represents a value returned from an operation. If the operation failed an error value is attached with
 * more information regarding this issue. This interface is similar to Rust's result type.
 *
 * If a result was successful it will be represented by an [Ok] value. If it failed [Err] is returned.
 *
 * Results allow us to implement exception-free error handling which enforces the caller to deal with any errors from
 * the call.
 *
 * If you do not wish to attach any information along with the failed result, look into [Option]
 *
 * @see Option
 */
public sealed class Result<out T, out E>(
    protected open val value: T? = null,
    protected open val error: E? = null
) {
    /**
     * Determines if the value is an error (err)
     *
     * This is a very lightweight operation implemented through a null check. There is no instanceof involved.
     */
    public fun isErr(): Boolean = error != null

    /**
     * Determines if this result is successful (ok)
     *
     * This is a very lightweight operation implemented through a null check. There is no instanceof involved.
     */
    public fun isOk(): Boolean = value != null

    /**
     * Return the contained [value] inside this result, ignoring any errors
     *
     * Because this operation may fail (if this is an [Err]), the user should check whether this is an [Err] or [Ok]
     * using the helper methods [isErr] and [isOk]
     *
     * @throws IllegalStateException if called on [Err]
     */
    public fun unwrap(): T {
        return value ?: throw IllegalStateException("Illegal result access")
    }

    /**
     * Return the contained [error] inside this result, ignoring any success
     *
     * Because this operation may fail (if this is an [Ok]), the user should check whether this is an [Err] or [Ok]
     * using the helper methods [isErr] and [isOk]
     *
     * @throws IllegalStateException if called on [Ok]
     */
    public fun err(): E {
        return error ?: throw IllegalStateException("Illegal result access")
    }

    /**
     * Maps this result into a result of type [R] and [E] through an [operation]
     *
     * The [operation] is only invoked if this is [Ok].
     *
     * This method can be used to nicely chain operations on possibly failed values.
     */
    public fun <R> map(operation: (T) -> R): Result<R, E> {
        return if (isOk()) {
            Ok(operation.invoke(unwrap()))
        } else {
            Err(error!!)
        }
    }

    /**
     * Implementation of [map] for the error branch.
     *
     * The behavior for this identical, except it only invokes [operation] if this is [Err]
     */
    public fun <F> mapErr(operation: (E) -> F): Result<T, F> {
        return if (isErr()) {
            Err(operation.invoke(error!!))
        } else {
            Ok(value!!)
        }
    }
}

/**
 * A data class representing a successful [Result]
 */
public data class Err<out E>(public override val error: E) :
    Result<Nothing, E>(error = error) {
    public override fun toString(): String = "Err($error)"
}

/**
 * A data class representing a failed [Result]
 */
public data class Ok<out T>(public override val value: T) :
    Result<T, Nothing>(value = value) {
    public override fun toString(): String = "Ok($value)"
}