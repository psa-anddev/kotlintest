package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.TestContext
import io.kotlintest.lineNumber

abstract class AbstractFreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  infix operator fun String.minus(init: FreeSpecContext.() -> Unit) =
      addRootScope(TestContainer(rootDescription().append(this), this@AbstractFreeSpec::class, { FreeSpecContext(it).init() }))

  infix operator fun String.invoke(test: TestContext.() -> Unit): TestCase {
    val tc = TestCase(rootDescription().append(this), this@AbstractFreeSpec, test, lineNumber(), defaultTestCaseConfig)
    addRootScope(tc)
    return tc
  }

  inner class FreeSpecContext(val context: TestContext) {

    infix operator fun String.minus(init: FreeSpecContext.() -> Unit) =
        context.executeScope(TestContainer(context.currentScope().description().append(this), this@AbstractFreeSpec::class, { FreeSpecContext(it).init() }))

    infix operator fun String.invoke(test: TestContext.() -> Unit): TestCase {
      val tc = TestCase(context.currentScope().description().append(this), this@AbstractFreeSpec, test, lineNumber(), defaultTestCaseConfig)
      context.executeScope(tc)
      return tc
    }
  }
}