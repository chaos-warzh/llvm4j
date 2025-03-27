package org.llvm4j.optional

import java.util.Optional

/**
 * An implementation of an optional value, similar to [Optional] with extra utilities
 *
 * An [Option] represents a value of type [T] which may or may not be present. presence is represented through child
 * classes [Some] which represents presence and [None] which represents absence. This interface is similar to Scala
 * or Rust's optional types.
 *
 * Option types are a nice alternative to nullable types for code which is expected to be called from Java. If we had
 * no intentions of support a Java ecosystem a nullable value would do just fine.
 *
 * Optionals also allow for basic error handling without usage of Java/Kotlin exceptions. One can simply return
 * [None] if an action failed to signal the absence of a result. If we need a more thorough and descriptive error
 * type which conveys more information we can use [Result].
 *
 * Because of the way sealed classes in Kotlin work we are able to get a very clean and easy-to-use interface for
 * optional values.
 *
 * @see Result
 */
public sealed class Option<out T>(protected open val value: T? = null) {
    /**
     * Determines if the value in this optional is present.
     *
     * This is a very lightweight operation implemented through a null check. There is no instanceof involved.
     */
    public fun isSome(): Boolean = value != null

    /**
     * Determines if the value in this optional is absent
     *
     * This is a very lightweight operation implemented through a null check. There is no instanceof involved.
     */
    public fun isNone(): Boolean = !isSome()

    /**
     * Return the contained [value] inside this option, ignoring any absence of the value
     *
     * Because this operation may fail, the user should check for the presence of the optional value using the
     * [isSome] or [isNone]
     *
     * @throws IllegalStateException if called on [None]
     */
    public fun unwrap(): T {
        return value ?: throw IllegalStateException("Illegal option access")
    }

    /**
     * Maps this optional value into an option of [R] through an [operation]
     *
     * The [operation] is only invoked if the optional value is present.
     *
     * This method can be used to nicely chain operations on nullable values.
     *
     * ```
     * val opt = Some(100).map {
     *   it * 2
     * }.map {
     *   it + 4
     * }
     * ```
     */
    public fun <R> map(operation: (T) -> R): Option<R> {
        return if (isSome()) {
            Some(operation.invoke(unwrap()))
        } else {
            None
        }
    }

    /**
     * Get the option as a nullable Kotlin type
     */
    public fun toNullable(): T? = value

    public companion object {
        /**
         * Converts a [Option] into a Java Optional.
         *
         * Implementation detail: this function has to be a companion function to satisfy [Option]s `in` type
         * parameter.
         */
        @JvmStatic
        public fun <R> toJavaOptional(opt: Option<R>): Optional<R & Any> {
            return Optional.ofNullable(opt.toNullable())
        }

        /**
         * Create a new optional value of a nullable value
         */
        @JvmStatic
        public fun <T> of(value: T?): Option<T> {
            return if (value != null) {
                Some(value)
            } else {
                None
            }
        }

        /**
         * Create None
         */
        @JvmStatic
        public fun <T> empty(): Option<T> {
            return None
        }
    }
}

/**
 * A data class representing the presence of a value in an [Option]
 */
public data class Some<out T>(public override val value: T) : Option<T>(value) {
    public override fun toString(): String = "Some($value)"
}

/**
 * An object representing the absence of a value in an [Option]
 */
public object None : Option<Nothing>() {
    public override fun toString(): String = "None"
}

