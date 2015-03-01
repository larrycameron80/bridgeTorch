package bridgettorch;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class States {

    private Set<State> states;

    public States(Set<State> states) {
        this.states = Collections.unmodifiableSet(states);
    }

    public Integer minTime() {
        States finalStates = finalStates();
        return finalStates.states.stream().mapToInt((state) -> state.getElapsedTimeMinutes()).min().getAsInt();
    }

    private States finalStates() {
        States currentStates = this;
        while (true) {
            if (currentStates.allDone()) {
                return currentStates;
            }
            currentStates = currentStates.nextPossibleStates();
        }
    }

    private States nextPossibleStates() {
        Stream<State> nextPossibleStatesStream = states.stream().flatMap((state) -> state.nextPossibleStates().stream());
        Set<State> nextPossibleStatesSet = nextPossibleStatesStream.collect(Collectors.toSet());

        return new States(nextPossibleStatesSet);
    }

    private boolean allDone() {
        return states.stream().allMatch((state) -> state.done());
    }

    @Override
    public String toString() {
        return "States{" +
                "states=" + states +
                '}';
    }

    public Set<State> getStates() {
        return Collections.unmodifiableSet(states);
    }
}
