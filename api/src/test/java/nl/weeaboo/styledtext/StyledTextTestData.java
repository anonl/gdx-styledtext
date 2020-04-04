package nl.weeaboo.styledtext;

public class StyledTextTestData {

    public static final String FONT_NAME = "Font-Name";
    public static final EFontStyle FONT_STYLE = EFontStyle.BOLD_ITALIC;
    public static final float FONT_SIZE = 14f;

    public static final ETextAlign ALIGN = ETextAlign.CENTER;
    public static final int TEXT_COLOR = 0x11223344;
    public static final boolean UNDERLINE = true;

    public static final float OUTLINE_SIZE = 2f;
    public static final int OUTLINE_COLOR = 0x22334455;

    public static final int SHADOW_COLOR = 0x33445566;
    public static final float SHADOW_DX = 3f;
    public static final float SHADOW_DY = 4f;

    public static final float SPEED = 5f;

    public final TextStyle fullStyle;

    public StyledTextTestData() {
        this.fullStyle = createFullStyle();
    }

    private TextStyle createFullStyle() {
        MutableTextStyle mts = new MutableTextStyle();
        mts.setFont(FONT_NAME, FONT_STYLE, FONT_SIZE);

        mts.setAlign(ALIGN);
        mts.setColor(TEXT_COLOR);
        mts.setUnderlined(UNDERLINE);

        mts.setOutlineSize(OUTLINE_SIZE);
        mts.setOutlineColor(OUTLINE_COLOR);

        mts.setShadowColor(SHADOW_COLOR);
        mts.setShadowDx(SHADOW_DX);
        mts.setShadowDy(SHADOW_DY);

        return mts.immutableCopy();
    }

}
