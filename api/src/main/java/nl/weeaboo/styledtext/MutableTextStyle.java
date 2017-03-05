package nl.weeaboo.styledtext;

public final class MutableTextStyle extends AbstractTextStyle {

    private static final long serialVersionUID = 1L;

    public MutableTextStyle() {
        super();
    }

    public MutableTextStyle(String fontName, float fontSize) {
        super(fontName, fontSize);
    }

    public MutableTextStyle(String fontName, EFontStyle fontStyle, float fontSize) {
        super(fontName, fontStyle, fontSize);
    }

    public MutableTextStyle(AbstractTextStyle m) {
        super(m);
    }

    public MutableTextStyle copy() {
        return new MutableTextStyle(this);
    }
    public TextStyle immutableCopy() {
        return new TextStyle(this);
    }

    public void extend(AbstractTextStyle ext) {
        if (ext == null || equals(ext)) {
            return;
        }

        extendAttributes(attributes, ext.attributes);
    }

    public void removeAttribute(ETextAttribute key) {
        attributes.remove(key);
    }

    public void setAttribute(ETextAttribute attr, Object value) {
        if (attr.isValidValue(value)) {
            attributes.put(attr, value);
        }
    }

    public void setFontName(String name) {
        setAttribute(ETextAttribute.FONT_NAME, name);
    }
    public void setFontStyle(EFontStyle style) {
        setAttribute(ETextAttribute.FONT_STYLE, style);
    }
    public void setFontSize(float size) {
        setAttribute(ETextAttribute.FONT_SIZE, size);
    }
    public void setFont(String name, EFontStyle style, float size) {
        setFontName(name);
        setFontStyle(style);
        setFontSize(size);
    }
    public void setColor(float r, float g, float b) {
        setColor(r, g, b, 1f);
    }
    public void setColor(float r, float g, float b, float a) {
        setColor(TextColor.packColorFloat(r, g, b, a));
    }
    public void setColor(int r, int g, int b) {
        setColor(r, g, b, 255);
    }
    public void setColor(int r, int g, int b, int a) {
        setColor(TextColor.packColorInt(r, g, b, a));
    }
    public void setColor(int argb) {
        setAttribute(ETextAttribute.COLOR, argb);
    }
    public void setAlign(ETextAlign a) {
        setAttribute(ETextAttribute.ALIGN, a);
    }
    public void setUnderlined(boolean u) {
        setAttribute(ETextAttribute.UNDERLINE, u);
    }
    public void setOutlineSize(float s) {
        setAttribute(ETextAttribute.OUTLINE_SIZE, s);
    }
    public void setOutlineColor(float r, float g, float b) {
        setOutlineColor(r, g, b, 1f);
    }
    public void setOutlineColor(float r, float g, float b, float a) {
        setOutlineColor(TextColor.packColorFloat(r, g, b, a));
    }
    public void setOutlineColor(int r, int g, int b) {
        setOutlineColor(r, g, b, 255);
    }
    public void setOutlineColor(int r, int g, int b, int a) {
        setOutlineColor(TextColor.packColorInt(r, g, b, a));
    }
    public void setOutlineColor(int argb) {
        setAttribute(ETextAttribute.OUTLINE_COLOR, argb);
    }
    public void setShadowColor(float r, float g, float b) {
        setShadowColor(r, g, b, 1f);
    }
    public void setShadowColor(float r, float g, float b, float a) {
        setShadowColor(TextColor.packColorFloat(r, g, b, a));
    }
    public void setShadowColor(int r, int g, int b) {
        setShadowColor(r, g, b, 255);
    }
    public void setShadowColor(int r, int g, int b, int a) {
        setShadowColor(TextColor.packColorInt(r, g, b, a));
    }
    public void setShadowColor(int argb) {
        setAttribute(ETextAttribute.SHADOW_COLOR, argb);
    }
    public void setShadowDx(float dx) {
        setAttribute(ETextAttribute.SHADOW_DX, dx);
    }
    public void setShadowDy(float dy) {
        setAttribute(ETextAttribute.SHADOW_DY, dy);
    }
    public void setSpeed(float spd) {
        setAttribute(ETextAttribute.SPEED, spd);
    }

}
