package bridgettorch;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class State {

    private Set<Person> leftSide;
    private Set<Person> rightSide;
    private TorchSide torchSide;
    private Integer elapsedTimeMinutes;

    public State(Set<Person> leftSide, Set<Person> rightSide, TorchSide torchSide, Integer elapsedTimeMinutes) {
        this.leftSide = Collections.unmodifiableSet(leftSide);
        this.rightSide = Collections.unmodifiableSet(rightSide);
        this.torchSide = torchSide;
        this.elapsedTimeMinutes = elapsedTimeMinutes;
    }

    public Set<State> nextPossibleStates() {
        return torchSide.accept(new TorchSideVisitor<Set<State>>() {
            @Override
            public Set<State> visitLeft() {
                return nextPossibleStatesFromLeft();
            }

            @Override
            public Set<State> visitRight() {
                return nextPossibleStatesFromRight();
            }
        });
    }

    public boolean done() {
        return leftSide.isEmpty();
    }

    private Set<State> nextPossibleStatesFromLeft() {
        Set<PersonsToCross> possibleTransitions = possibleTransitionsFromLeft();

        Stream<PersonsToCross> possibleTransitionsStream = possibleTransitions.stream();
        Stream<State> nextPossibleStatesStream = possibleTransitionsStream.map(this::nextStateFromLeft);

        return nextPossibleStatesStream.collect(Collectors.toSet());
    }

    private Set<State> nextPossibleStatesFromRight() {
        Set<PersonsToCross> possibleTransitions = possibleTransitionsFromRight();

        Stream<PersonsToCross> possibleTransitionsStream = possibleTransitions.stream();
        Stream<State> nextPossibleStatesStream = possibleTransitionsStream.map(this::nextStateFromRight);

        return nextPossibleStatesStream.collect(Collectors.toSet());
    }

    private Set<PersonsToCross> possibleTransitionsFromLeft() {
        Stream<PersonsToCross> personsToCrossStream = leftSide.stream().flatMap((person1) -> {
            Stream<Person> person2Stream = leftSide.stream().filter((person2) -> !person2.equals(person1));
            return person2Stream.map((person2) -> {
                Set<Person> personsToCrossSet = new HashSet<>();
                personsToCrossSet.add(person1);
                personsToCrossSet.add(person2);
                return new PersonsToCross(personsToCrossSet);
            });
        });

        return personsToCrossStream.collect(Collectors.toSet());
    }

    private Set<PersonsToCross> possibleTransitionsFromRight() {
        Stream<PersonsToCross> possibleTransitionsStream = rightSide.stream().map((person) -> {
            Set<Person> personsToCrossSet = new HashSet<>();
            personsToCrossSet.add(person);
            return new PersonsToCross(personsToCrossSet);
        });

        return possibleTransitionsStream.collect(Collectors.toSet());
    }

    private State nextStateFromLeft(final PersonsToCross personsToCross) {
        Set<Person> nextLeftSide = leftSide.stream().filter((person) -> !personsToCross.containsPerson(person)).collect(Collectors.toSet());
        Set<Person> nextRightSide = Stream.concat(rightSide.stream(), personsToCross.toStream()).collect(Collectors.toSet());
        TorchSide nextTorchSide = torchSide.flip();
        Integer nextElapsedTimeMinutes = elapsedTimeMinutes + personsToCross.timeToCrossMinutes();

        return new State(nextLeftSide, nextRightSide, nextTorchSide, nextElapsedTimeMinutes);
    }


    private State nextStateFromRight(PersonsToCross personsToCross) {
        Set<Person> nextLeftSide = Stream.concat(leftSide.stream(), personsToCross.toStream()).collect(Collectors.toSet());
        Set<Person> nextRightSide = rightSide.stream().filter((person) -> !personsToCross.containsPerson(person)).collect(Collectors.toSet());
        TorchSide nextTorchSide = torchSide.flip();
        Integer nextElapsedTimeMinutes = elapsedTimeMinutes + personsToCross.timeToCrossMinutes();

        return new State(nextLeftSide, nextRightSide, nextTorchSide, nextElapsedTimeMinutes);
    }

    @Override
    public String toString() {
        return "State{" +
                "leftSide=" + leftSide +
                ", rightSide=" + rightSide +
                ", torchSide=" + torchSide +
                ", elapsedTimeMinutes=" + elapsedTimeMinutes +
                '}';
    }

    public Set<Person> getLeftSide() {
        return Collections.unmodifiableSet(leftSide);
    }

    public Set<Person> getRightSide() {
        return Collections.unmodifiableSet(rightSide);
    }

    public TorchSide getTorchSide() {
        return torchSide;
    }

    public Integer getElapsedTimeMinutes() {
        return elapsedTimeMinutes;
    }
}
