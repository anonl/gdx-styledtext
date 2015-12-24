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

        extendProperties(properties, ext.properties);
	}

    public void removeProperty(ETextAttribute key) {
		properties.remove(key);
	}

    public void setProperty(ETextAttribute key, Object value) {
		if (key.isValidType(value)) {
			properties.put(key, value);
		}
	}

    public void setFontName(String name) {
        setProperty(ETextAttribute.FONT_NAME, name);
	}
	public void setFontStyle(EFontStyle style) {
        setProperty(ETextAttribute.FONT_STYLE, style);
	}
	public void setFontSize(float size) {
        setProperty(ETextAttribute.FONT_SIZE, size);
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
        setProperty(ETextAttribute.COLOR, argb);
	}
	public void setAlign(ETextAlign a) {
        setProperty(ETextAttribute.ALIGN, a);
	}
	public void setUnderlined(boolean u) {
        setProperty(ETextAttribute.UNDERLINE, u);
	}
	public void setOutlineSize(float s) {
        setProperty(ETextAttribute.OUTLINE_SIZE, s);
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
        setProperty(ETextAttribute.OUTLINE_COLOR, argb);
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
        setProperty(ETextAttribute.SHADOW_COLOR, argb);
	}
	public void setShadowDx(float dx) {
        setProperty(ETextAttribute.SHADOW_DX, dx);
	}
	public void setShadowDy(float dy) {
        setProperty(ETextAttribute.SHADOW_DY, dy);
	}
	public void setSpeed(float spd) {
        setProperty(ETextAttribute.SPEED, spd);
	}

}
