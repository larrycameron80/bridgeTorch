package bridgettorch;

public class Person {

    private String id;
    private Integer timeToCrossMinutes;

    public Person(String id, Integer timeToCrossMinutes) {
        this.id = id;
        this.timeToCrossMinutes = timeToCrossMinutes;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", timeToCrossMinutes=" + timeToCrossMinutes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (!id.equals(person.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    public Integer getTimeToCrossMinutes() {
        return timeToCrossMinutes;
    }
}
