package thresholds;

public class DefaultThresholds extends Thresholds {
    @Override
    public int getEagerTest() {
        return 1;
    }

    @Override
    public int getAssertionRoulette() {
        return 1;
    }

    @Override
    public int getVerboseTest() {
        return 1;
    }

    @Override
    public int getConditionalTestLogic() {
        return 0;
    }

    @Override
    public int getMagicNumberTest() {
        return 0;
    }

    @Override
    public int getGeneralFixture() {
        return 0;
    }

    @Override
    public int getMysteryGuest() {
        return 0;
    }

    @Override
    public int getResourceOptimism() {
        return 0;
    }

    @Override
    public int getSleepyTest() {
        return 0;
    }

    @Override
    public int getEmptyTest() {
        return 0;
    }

    @Override
    public int getExceptionCatchingThrowing() {
        return 0;
    }

    @Override
    public int getPrintStatement() {
        return 0;
    }

    @Override
    public int getRedundantAssertion() {
        return 0;
    }

    @Override
    public int getSensitiveEquality() {
        return 0;
    }
}