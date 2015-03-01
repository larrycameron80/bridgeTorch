package bridgettorch;

public enum TorchSide {

    LEFT {
        @Override
        public <T> T accept(TorchSideVisitor<T> visitor) {
            return visitor.visitLeft();
        }

        @Override
        public TorchSide flip() {
            return RIGHT;
        }
    },
    RIGHT {
        @Override
        public <T> T accept(TorchSideVisitor<T> visitor) {
            return visitor.visitRight();
        }

        @Override
        public TorchSide flip() {
            return LEFT;
        }
    };

    public abstract <T> T accept(TorchSideVisitor<T> visitor);

    public abstract TorchSide flip();
}
