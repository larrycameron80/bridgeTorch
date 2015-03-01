package bridgetorch

import scala.annotation.tailrec

object BridgeTorch extends App {


  val leftSide = Set(Person("A", 1), Person("B", 2), Person("C", 5), Person("D", 8))
  val rightSide = Set[Person]()
  val torchSide = Left
  val initialState = State(leftSide, rightSide, torchSide)
  val initialStates = States(Set(initialState))
  val minTimeMinutes = initialStates.minTimeMinutes

  print(minTimeMinutes)

}

sealed trait TorchSide {
  def flip: TorchSide
}
case object Left extends TorchSide {
  override def flip = Right
}
case object Right extends TorchSide {
  override def flip = Left
}

case class Person(id: String, timeToCrossMinutes: Int) {

  override def equals(o: Any): Boolean = {
    o match {
      case other: Person => other.id.equals(id)
      case _ => false
    }
  }

  override def hashCode: Int = id.hashCode

}

case class PersonsToCross(personsToCross: Set[Person]) {

  def contains(person: Person): Boolean = {
    personsToCross.contains(person)
  }

  def timeToCrossMinutes: Int = {
    val timesToCrossMinutes = for {
      person <- personsToCross
    } yield person.timeToCrossMinutes

    timesToCrossMinutes.max
  }

}

case class State(leftSide: Set[Person], rightSide: Set[Person], torchSide: TorchSide, elapsedTimeMinutes: Int = 0) {

  def nextPossibleStates: Set[State] = {
    torchSide match {
      case Left => nextPossibleStatesFromLeft
      case Right => nextPossibleStatesFromRight
    }
  }

  def done: Boolean = leftSide.isEmpty

  private def nextPossibleStatesFromLeft: Set[State] = {
    val possiblePersonsToCross = possiblePersonsToCrossFromLeft
    val nextPossibleStates = possiblePersonsToCross.map(personsToCross => nextStateFromLeft(personsToCross))
    nextPossibleStates
  }

  private def nextPossibleStatesFromRight: Set[State] = {
    val possiblePersonsToCross = possiblePersonsToCrossFromRight
    val nextPossibleStates = possiblePersonsToCross.map(personsToCross => nextStateFromRight(personsToCross))
    nextPossibleStates
  }

  private def nextStateFromLeft(personsToCross: PersonsToCross): State = {
    val nextLeftSide = for {
    person <- leftSide
    if(!personsToCross.contains(person))
    } yield person

    val nextRightSide = rightSide ++ personsToCross.personsToCross

    val nextTorchSide = torchSide.flip

    val nextElapsedTimeMinutes = elapsedTimeMinutes + personsToCross.timeToCrossMinutes

    State(nextLeftSide, nextRightSide, nextTorchSide, nextElapsedTimeMinutes)
  }

  private def nextStateFromRight(personsToCross: PersonsToCross): State = {
    val nextLeftSide = leftSide ++ personsToCross.personsToCross
    val nextRightSide = for {
      person <- rightSide
      if(!personsToCross.contains(person))
    } yield person
    val nextTorchSide = torchSide.flip
    val nextElapsedTimeMinutes = elapsedTimeMinutes + personsToCross.timeToCrossMinutes

    State(nextLeftSide, nextRightSide, nextTorchSide, nextElapsedTimeMinutes)
  }

  private def possiblePersonsToCrossFromLeft: Set[PersonsToCross] = {
    for {
    person1 <- leftSide
    person2 <- leftSide
    if(!person1.equals(person2))
    } yield PersonsToCross(Set(person1, person2))
  }

  private def possiblePersonsToCrossFromRight: Set[PersonsToCross] = {
    for {
      person <- rightSide
    } yield PersonsToCross(Set(person))
  }

}

case class States(states: Set[State]) {

  def minTimeMinutes: Int = {
    val elapsedTimesMinutes = for {
      finalState <- finalStates.states
    } yield finalState.elapsedTimeMinutes

    elapsedTimesMinutes.min
  }

  private def nextPossibleStates: States = {
    val nextPossibleStates = states.flatMap(state => state.nextPossibleStates)
    States(nextPossibleStates)
  }

  private def finalStates: States = {
    recursiveFinalStates(this)
  }

  @tailrec
  private def recursiveFinalStates(currentStates: States): States = {
    if(currentStates.allDone) {
      return currentStates
    }

    recursiveFinalStates(currentStates.nextPossibleStates)
  }

  private def allDone: Boolean = {
    states.forall(state => state.done)
  }

}

