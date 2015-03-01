class Person:
    def __init__(self, id, timeToCrossMinutes):
        self.__id = id
        self.__timeToCrossMinutes = timeToCrossMinutes

    def __repr__(self):
        return 'Person(' + self.__id + ', ' + str(self.__timeToCrossMinutes) + ')'

    def __eq__(self, other):
        if type(self) != type(other):
            return False
        return self.__id == other.__id

    def __ne__(self, other):
        return not self.__eq__(other)

    def __cmp__(self, other):
        return cmp(self.__id, other.__id)

    def timeToCrossMinutes(self):
        return self.__timeToCrossMinutes

class TorchSide:
    LEFT = 'Left'
    RIGHT = 'Right'

    @staticmethod
    def flip(torchSide):
        if torchSide == TorchSide.LEFT:
            return TorchSide.RIGHT
        elif torchSide == TorchSide.RIGHT:
            return TorchSide.LEFT
        else:
            raise 'Unknown TorchSide ' + torchSide


class PersonsToCross:
    def __init__(self, personsToCross):
        self.__personsToCross = personsToCross

    def __repr__(self):
        return 'PersonsToCross(' + repr(self.__personsToCross) + ')'

    def contains(self, person):
        return any(person == thisPerson for thisPerson in self.__personsToCross)

    def persons(self):
        return self.__personsToCross

    def timeToCrossMinutes(self):
        timesToCrossMinutes = [person.timeToCrossMinutes() for person in self.__personsToCross]
        return max(timesToCrossMinutes)


class State:
    def __init__(self, leftSide, rightSide, torchSide, elapsedTimeMinutes = 0):
        self.__leftSide = leftSide
        self.__rightSide = rightSide
        self.__torchSide = torchSide
        self.__elapsedTimeMinutes = elapsedTimeMinutes

    def __repr__(self):
        repr = 'State(' + str(self.__leftSide)
        repr += ', ' + str(self.__rightSide)
        repr += ', ' + str(self.__torchSide)
        repr += ', ' + str(self.__elapsedTimeMinutes)
        repr += ')'
        return repr

    def nextPossibleStates(self):
        if self.__torchSide == TorchSide.LEFT:
            return self.nextPossibleStatesFromLeft()
        elif self.__torchSide == TorchSide.RIGHT:
            return self.nextPossibleStatesFromRight()
        else:
            raise 'Unknown TorchSide ' + self.__torchSide

    def nextPossibleStatesFromLeft(self):
        possiblePersonsToCross = self.possiblePersonsToCrossFromLeft()
        nextStates = [self.nextStateFromLeft(personsToCross) for personsToCross in possiblePersonsToCross]
        return nextStates

    def nextPossibleStatesFromRight(self):
        possiblePersonsToCross = self.possiblePersonsToCrossFromRight()
        nextStates = [self.nextStateFromRight(personsToCross) for personsToCross in possiblePersonsToCross]
        return nextStates

    def nextStateFromLeft(self, personsToCross):
        nextLeftSide = [person for person in self.__leftSide if not personsToCross.contains(person)]
        nextRightSide = self.__rightSide + personsToCross.persons()
        nextTorchSide = TorchSide.flip(self.__torchSide)
        nextElapsedTimeMinutes = self.__elapsedTimeMinutes + personsToCross.timeToCrossMinutes()
        return State(nextLeftSide, nextRightSide, nextTorchSide, nextElapsedTimeMinutes)

    def nextStateFromRight(self, personsToCross):
        nextLeftSide = self.__leftSide + personsToCross.persons()
        nextRightSide = [person for person in self.__rightSide if not personsToCross.contains(person)]
        nextTorchSide = TorchSide.flip(self.__torchSide)
        nextElapsedTimeMinutes = self.__elapsedTimeMinutes + personsToCross.timeToCrossMinutes()
        return State(nextLeftSide, nextRightSide, nextTorchSide, nextElapsedTimeMinutes)

    def possiblePersonsToCrossFromLeft(self):
        possiblePersonsToCross = [PersonsToCross([person1, person2])for person1 in self.__leftSide for person2 in self.__leftSide if person2 > person1]
        return possiblePersonsToCross

    def possiblePersonsToCrossFromRight(self):
        possiblePersonsToCross = [PersonsToCross([person]) for person in self.__rightSide]
        return possiblePersonsToCross

    def done(self):
        return len(self.__leftSide) == 0

    def elapsedTimeMinutes(self):
        return self.__elapsedTimeMinutes


class States:
    def __init__(self, states):
        self.__states = states

    def __repr__(self):
        return 'States(' + repr(self.__states) + ')'

    def nextStates(self):
        return States([nextState for state in self.__states for nextState in state.nextPossibleStates()])

    def finalStates(self):
        currentStates = self
        while True:
            if currentStates.allDone():
                return currentStates
            currentStates = currentStates.nextStates()

    def minTimeToCross(self):
        finalStates = self.finalStates()
        finalTimesToCross = [finalState.elapsedTimeMinutes() for finalState in finalStates.__states]
        return min(finalTimesToCross)

    def allDone(self):
        return all(state.done() for state in self.__states)


leftSide = [Person('A', 1), Person('B', 2), Person('C', 5), Person('D', 8)]
rightSide = []
torchSide = TorchSide.LEFT
initialState = State(leftSide, rightSide, torchSide)
initialStates = States([initialState])
minTimeToCross = initialStates.minTimeToCross()
print(minTimeToCross)
