package bridgettorch;

public interface TorchSideVisitor<T> {

    T visitLeft();
    T visitRight();

}
