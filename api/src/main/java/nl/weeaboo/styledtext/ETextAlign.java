package nl.weeaboo.styledtext;

public enum ETextAlign {

    /** Default text alignment */
    NORMAL("normal"),

    /** Reverse text alignment compared to normal */
    REVERSE("reverse"),

    /** Left-aligned no matter if the rendering context is left-to-right or right-to-left */
    LEFT("left"),

    /** Center aligned */
    CENTER("center"),

    /** Right-aligned no matter if the rendering context is left-to-right or right-to-left */
    RIGHT("right");

    private final String id;

    private ETextAlign(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    /**
     * @return The enum value matching the given string representation, or {@code null} if no match is found.
     *         The matching algorithm is case-sensitive.
     */
    public static ETextAlign fromString(String str) {
        for (ETextAlign a : values()) {
            if (a.toString().equals(str)) {
                return a;
            }
        }
        return null;
    }

    /**
     * @return {@code -1} if left-aligned, {@code 0} if center-aligned, {@code 1} if right-aligned.
     */
    public int getHorizontalAlign(boolean isRightToLeft) {
        int defaultDir = (isRightToLeft ? 1 : -1);

        switch (this) {
        case NORMAL:
            return defaultDir;
        case REVERSE:
            return -defaultDir;
        case LEFT:
            return -1;
        case CENTER:
            return 0;
        case RIGHT:
            return 1;
        default:
            return defaultDir;
        }
    }

}
