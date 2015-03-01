package bridgettorch;

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Set<Person> leftSide = new HashSet<>();
        leftSide.add(new Person("A", 1));
        leftSide.add(new Person("B", 2));
        leftSide.add(new Person("C", 5));
        leftSide.add(new Person("D", 8));

        Set<Person> rightSide = new HashSet<>();

        TorchSide torchSide = TorchSide.LEFT;

        Integer elapsedTimeMinutes = 0;

        State initialState = new State(leftSide, rightSide, torchSide, elapsedTimeMinutes);

        Set<State> initialStatesSet = new HashSet<>();
        initialStatesSet.add(initialState);

        States initialStates = new States(initialStatesSet);
        Integer minTime = initialStates.minTime();

        System.out.println(minTime);
    }

}
