package bridgettorch;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

public class PersonsToCross {

    private Set<Person> persons;

    public PersonsToCross(Set<Person> persons) {
        this.persons = Collections.unmodifiableSet(persons);

        if(this.persons.isEmpty()) {
            throw new IllegalArgumentException("Cannot construct from empty set");
        }
    }

    public boolean containsPerson(Person person) {
        return persons.contains(person);
    }

    public Stream<Person> toStream() {
        return persons.stream();
    }

    public Integer timeToCrossMinutes() {
        Integer timeToCrossMinutes = persons.stream().mapToInt((person) -> person.getTimeToCrossMinutes()).max().getAsInt();

        return timeToCrossMinutes;
    }

    @Override
    public String toString() {
        return "PersonsToCross{" +
                "persons=" + persons +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonsToCross that = (PersonsToCross) o;

        if (!persons.equals(that.persons)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return persons.hashCode();
    }

    public Set<Person> getPersons() {
        return Collections.unmodifiableSet(persons);
    }
}
