package nl.weeaboo.styledtext.layout;

/** Contains attributes related to underlining text */
public final class UnderlineMetrics {

    public static final UnderlineMetrics NONE = new UnderlineMetrics(0f, 0f);

    private final float position;
    private final float thickness;

    public UnderlineMetrics(float position, float thickness) {
        this.position = position;
        this.thickness = thickness;
    }

    public UnderlineMetrics scaledCopy(float scale) {
        return new UnderlineMetrics(position * scale, thickness * scale);
    }

    /**
     * The center position of the underline line, relative to the baseline. Negative for positions under the
     * baseline.
     */
    public float getUnderlinePosition() {
        return position;
    }

    /** Thickness of the underline line. */
    public float getUnderlineThickness() {
        return thickness;
    }

}
