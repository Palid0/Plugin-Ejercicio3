package thresholds;

/**
 * There are the thresholds for the test smells detection proposed by Spadini et.al. in
 * the paper _Investigating severity thresholds for test smells_.
 */
public abstract class Thresholds {
    public abstract int getEagerTest();
    public abstract int getAssertionRoulette();
    public abstract int getVerboseTest();
    public abstract int getConditionalTestLogic();
    public abstract int getMagicNumberTest();
    public abstract int getGeneralFixture();
    public abstract int getMysteryGuest();
    public abstract int getResourceOptimism();
    public abstract int getSleepyTest();
    public abstract int getEmptyTest();
    public abstract int getExceptionCatchingThrowing();
    public abstract int getPrintStatement();
    public abstract int getRedundantAssertion();
    public abstract int getSensitiveEquality();
}
