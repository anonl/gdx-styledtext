package nl.weeaboo.styledtext.gdx;

public enum YDir {

    UP(-1),
    DOWN(1);

    private final int intValue;

    private YDir(int intValue) {
        this.intValue = intValue;
    }

    public int toInt() {
        return intValue;
    }

}
