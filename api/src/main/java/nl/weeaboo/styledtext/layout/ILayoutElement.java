package nl.weeaboo.styledtext.layout;

public interface ILayoutElement {

    float getX();
    void setX(float x);

    float getY();
    void setY(float y);

    /** Horizontal advance */
    float getLayoutWidth();

    /** Line height */
    float getLayoutHeight();

    boolean isWhitespace();

    void setParameters(LayoutParameters params);

}
