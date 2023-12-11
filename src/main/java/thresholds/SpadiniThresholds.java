package thresholds;

public class SpadiniThresholds extends DefaultThresholds {
    @Override
    public int getEagerTest() {
        return 4;
    }

    @Override
    public int getAssertionRoulette() {
        return 3;
    }

    @Override
    public int getVerboseTest() {
        return 13;
    }
}