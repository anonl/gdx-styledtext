package nl.weeaboo.styledtext;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

abstract class AbstractTextStyle implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final float EPSILON = .001f;

    protected final Map<ETextAttribute, Object> attributes =
            new EnumMap<ETextAttribute, Object>(ETextAttribute.class);

    protected AbstractTextStyle() {
    }

    protected AbstractTextStyle(String fontName, float fontSize) {
        this(fontName, null, fontSize);
    }

    protected AbstractTextStyle(String fontName, EFontStyle fontStyle, float fontSize) {
        this();

        if (fontName != null) {
            attributes.put(ETextAttribute.FONT_NAME, fontName);
        }
        if (fontStyle != null) {
            attributes.put(ETextAttribute.FONT_STYLE, fontStyle);
        }
        attributes.put(ETextAttribute.FONT_SIZE, fontSize);
    }

    protected AbstractTextStyle(AbstractTextStyle m) {
        attributes.putAll(m.attributes);
    }

    @Override
    public final int hashCode() {
        return attributes.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof AbstractTextStyle)) {
            return false;
        }

        AbstractTextStyle other = (AbstractTextStyle)obj;
        return attributes.equals(other.attributes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<ETextAttribute, Object> entry : attributes.entrySet()) {
            ETextAttribute key = entry.getKey();
            String value = key.valueToString(entry.getValue());
            if (value != null) {
                if (sb.length() > 0) {
                    sb.append('|');
                }
                sb.append(key.getId()).append("=").append(value);
            }
        }
        return sb.toString();
    }

    public boolean hasAttribute(ETextAttribute attr) {
        return attributes.containsKey(attr);
    }

    public Object getAttribute(ETextAttribute attr, Object defaultValue) {
        Object val = attributes.get(attr);
        return (val != null ? val : defaultValue);
    }

    public boolean getAttribute(ETextAttribute attr, boolean defaultValue) {
        Object val = attributes.get(attr);
        return (val != null ? (Boolean)val : defaultValue);
    }

    public int getAttribute(ETextAttribute attr, int defaultValue) {
        Object val = attributes.get(attr);
        return (val != null ? (Integer)val : defaultValue);
    }

    public float getAttribute(ETextAttribute attr, float defaultValue) {
        Object val = attributes.get(attr);
        return (val != null ? (Float)val : defaultValue);
    }

    public String getFontName() {
        return getFontName(null);
    }
    public String getFontName(String fallback) {
        return (String)getAttribute(ETextAttribute.FONT_NAME, fallback);
    }

    public EFontStyle getFontStyle() {
        return getFontStyle(EFontStyle.PLAIN);
    }
    public EFontStyle getFontStyle(EFontStyle fallback) {
        return (EFontStyle)getAttribute(ETextAttribute.FONT_STYLE, fallback);
    }

    public float getFontSize() {
        return getFontSize(12f);
    }
    public float getFontSize(float fallback) {
        return getAttribute(ETextAttribute.FONT_SIZE, fallback);
    }

    public ETextAlign getAlign() {
        return getAlign(ETextAlign.NORMAL);
    }
    public ETextAlign getAlign(ETextAlign fallback) {
        return (ETextAlign)getAttribute(ETextAttribute.ALIGN, fallback);
    }

    /**
     * @see #getColor(int)
     */
    public int getColor() {
        return getColor(0xFFFFFFFF);
    }
    /**
     * @return The text color packed into an int ({@code AARRGGBB})
     */
    public int getColor(int fallback) {
        return getAttribute(ETextAttribute.COLOR, fallback);
    }

    public boolean isUnderlined() {
        return isUnderlined(false);
    }
    public boolean isUnderlined(boolean fallback) {
        return getAttribute(ETextAttribute.UNDERLINE, fallback);
    }

    public float getOutlineSize() {
        return getOutlineSize(0f);
    }
    public float getOutlineSize(float fallback) {
        return getAttribute(ETextAttribute.OUTLINE_SIZE, fallback);
    }

    public int getOutlineColor() {
        return getOutlineColor(0);
    }
    public int getOutlineColor(int fallback) {
        return getAttribute(ETextAttribute.OUTLINE_COLOR, fallback);
    }

    public int getShadowColor() {
        return getShadowColor(0);
    }
    public int getShadowColor(int fallback) {
        return getAttribute(ETextAttribute.SHADOW_COLOR, fallback);
    }

    public float getShadowDx() {
        return getShadowDx(0f);
    }
    public float getShadowDx(float fallback) {
        return getAttribute(ETextAttribute.SHADOW_DX, fallback);
    }

    public float getShadowDy() {
        return getShadowDy(0f);
    }
    public float getShadowDy(float fallback) {
        return getAttribute(ETextAttribute.SHADOW_DY, fallback);
    }

    public float getSpeed() {
        return getSpeed(1f);
    }
    public float getSpeed(float fallback) {
        return getAttribute(ETextAttribute.SPEED, fallback);
    }

    public boolean hasOutline() {
        // Outline size is > 0 and outline color is not transparent
        return getOutlineSize() > EPSILON && !TextColor.isTransparent(getOutlineColor());
    }

    public boolean hasShadow() {
        // Shadow offset != 0 and shadow color is not transparent
        return (Math.abs(getShadowDx()) > EPSILON || Math.abs(getShadowDy()) > EPSILON)
                && !TextColor.isTransparent(getShadowColor());
    }

    protected static void extendAttributes(Map<ETextAttribute, Object> out, Map<ETextAttribute, Object> ext) {
        for (Entry<ETextAttribute, Object> entry : ext.entrySet()) {
            ETextAttribute key = entry.getKey();
            Object oldval = out.get(key);
            Object extval = entry.getValue();
            out.put(key, key.extendValue(oldval, extval));
        }
    }

    static TextStyle[] replicate(TextStyle style, int times) {
        TextStyle[] result = new TextStyle[times];
        Arrays.fill(result, style);
        return result;
    }

}
