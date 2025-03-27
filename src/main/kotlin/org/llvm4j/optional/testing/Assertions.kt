package org.llvm4j.optional.testing

import org.llvm4j.optional.Option
import org.llvm4j.optional.Result

public fun <T> assertNone(opt: Option<T>): Unit = assert(opt.isNone()) { "Expected $opt to be None" }
public fun <T> assertSome(opt: Option<T>): Unit = assert(opt.isSome()) { "Expected $opt to be Some" }
public fun <T, E> assertOk(res: Result<T, E>): Unit = assert(res.isOk()) { "Expected $res to be Ok" }
public fun <T, E> assertErr(res: Result<T, E>): Unit = assert(res.isErr()) { "Expected $res to be Err" }