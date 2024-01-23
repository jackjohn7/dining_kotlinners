package dining_kotlinners

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class Philosopher(
  val id: Int,
  var apetite: Int,
  val left_fork: Mutex,
  val right_fork: Mutex
): Thread() {

  private suspend fun grabForks() {
    if (id % 2 == 0) {
      left_fork.lock()
      right_fork.lock()
    } else {
      right_fork.lock()
      left_fork.lock()
    }
  }

  private suspend fun dropForks() {
    left_fork.unlock()
    right_fork.unlock()
  }

  public override fun run() = runBlocking {
    println("Philosopher ${id} in ${Thread.currentThread()} is running!")
    while (apetite > 0) {
      grabForks()
      eat()
      dropForks()
      println("Philosopher ${id} is thinking")
    }
  }

  fun eat() {
    println("Philosopher ${id} is eating")
    val r = Random.nextInt(3, 30) * 10
    Thread.sleep(r.toLong())
    apetite--
  }
}

fun main() {
    val numPhilosophers = 10
    val forks = Array(numPhilosophers) { _ -> Mutex() }
    val apetite = 10
    var philosophers = Array(numPhilosophers)
      { i -> Philosopher(i, apetite, forks.get(i), forks.get(if (i == numPhilosophers - 1) 0 else i + 1)) }
    for (p in philosophers) {
      p.start()
    }
}
