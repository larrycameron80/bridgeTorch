package cambillaum

fun main(args: Array<String>) {
    val initialState = State(setOf(Person("A", 1), Person("B", 2), Person("C", 5), Person("D", 8)), emptySet(), TorchSide.LeftSide, 0)
    val initialStates = States(setOf(initialState))
    val minTimeMinutes = initialStates.minTimeMinutes()
    println(minTimeMinutes)
}

enum class TorchSide {
    LeftSide {
        override fun flip(): TorchSide = RightSide
    },
    RightSide {
        override fun flip(): TorchSide = LeftSide
    };

    abstract fun flip(): TorchSide
}

data class Person(val id: String, val timeToCrossMinutes: Int)

data class PersonsToCross(val personsToCross: Set<Person>) {
    fun contains(person: Person): Boolean = personsToCross.contains(person)
    fun elapsedTimeMinutes(): Int = personsToCross.map { it.timeToCrossMinutes }.max()?:(throw IllegalStateException("personsToCross is empty!"))
}

data class State(val leftSide: Set<Person>, val rightSide: Set<Person>, val torchSide: TorchSide, val elapsedTimeMinutes: Int) {
    fun nextPossibleStates(): Set<State> = when(torchSide) {
        TorchSide.LeftSide -> nextPossibleStatesFromLeft()
        TorchSide.RightSide -> nextPossibleStatesFromRight()
    }

    private fun nextPossibleStatesFromLeft(): Set<State> {
        val possiblePersonsToCross = possiblePersonsToCrossFromLeft()
        val nextPossibleStates = possiblePersonsToCross.map { nextStateFromLeft(it) }.toSet()
        return nextPossibleStates
    }

    private fun nextPossibleStatesFromRight(): Set<State> {
        val possiblePersonsToCross = possiblePersonsToCrossFromRight()
        val nextPossibleStates = possiblePersonsToCross.map { nextStateFromRight(it) }.toSet()
        return nextPossibleStates
    }

    private fun possiblePersonsToCrossFromLeft(): Set<PersonsToCross> {
        val personsToCross = leftSide.flatMap { person1 ->
            leftSide.flatMap { person2 ->
                if(person1 == person2) {
                    emptyList()
                } else {
                    listOf(PersonsToCross(setOf(person1, person2)))
                }
            }
        }
        return personsToCross.toSet()
    }

    private fun possiblePersonsToCrossFromRight(): Set<Person> = rightSide

    private fun nextStateFromLeft(personsToCross: PersonsToCross): State {
        val newLeftSide = leftSide.filter { !personsToCross.contains(it) }.toSet()
        val newRightSide = rightSide.plus(personsToCross.personsToCross)
        val newTorchSide = torchSide.flip()
        val newElapsedTimeMinutes = elapsedTimeMinutes + personsToCross.elapsedTimeMinutes()
        return State(newLeftSide, newRightSide, newTorchSide, newElapsedTimeMinutes)
    }

    private fun nextStateFromRight(personToCross: Person): State {
        val newLeftSide = leftSide.plus(personToCross)
        val newRightSide = rightSide.filter { it != personToCross }.toSet()
        val newTorchSide = torchSide.flip()
        val newElapsedTimeMinutes = elapsedTimeMinutes + personToCross.timeToCrossMinutes
        return State(newLeftSide, newRightSide, newTorchSide, newElapsedTimeMinutes)
    }

    fun  isFinal(): Boolean = leftSide.isEmpty()
}

data class States(val states: Set<State>) {
    fun minTimeMinutes(): Int = finalStates().map { it.elapsedTimeMinutes }.min()?:(throw IllegalStateException("finalStates is empty!"))

    private fun finalStates(): Set<State> = recursiveFinalStates(states)

    private tailrec fun recursiveFinalStates(states: Set<State>): Set<State> {
        return if(states.all { it.isFinal() }) {
            states
        } else {
            val (finalStates, nonFinalStates) = states.partition { it.isFinal() }
            val newStates = nonFinalStates.flatMap { it.nextPossibleStates() }.toSet().plus(finalStates)
            recursiveFinalStates(newStates)
        }
    }
}