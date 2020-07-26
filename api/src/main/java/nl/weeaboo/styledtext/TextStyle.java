package nl.weeaboo.styledtext;

import com.google.errorprone.annotations.CheckReturnValue;

public final class TextStyle extends AbstractTextStyle {

    public static TextStyle BOLD = new TextStyle(ETextAttribute.FONT_STYLE, EFontStyle.BOLD);
    public static TextStyle ITALIC = new TextStyle(ETextAttribute.FONT_STYLE, EFontStyle.ITALIC);
    public static TextStyle BOLD_ITALIC = new TextStyle(ETextAttribute.FONT_STYLE, EFontStyle.BOLD_ITALIC);

    private static final long serialVersionUID = 1L;
    private static final TextStyle DEFAULT_INSTANCE = new TextStyle();

    private TextStyle() {
        super();
    }

    public TextStyle(String fontName, float fontSize) {
        super(fontName, fontSize);
    }

    public TextStyle(String fontName, EFontStyle fontStyle, float fontSize) {
        super(fontName, fontStyle, fontSize);
    }

    public TextStyle(ETextAttribute attr, Object value) {
        this();

        if (attr.isValidValue(value)) {
            attributes.put(attr, value);
        }
    }

    TextStyle(AbstractTextStyle m) {
        super(m);
    }

    public static TextStyle defaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public MutableTextStyle mutableCopy() {
        return new MutableTextStyle(this);
    }

    /**
     * @throws StyleParseException If the string could not be parsed
     * @see AbstractTextStyle#toString()
     */
    public static TextStyle fromString(String string) throws StyleParseException {
        if (string.length() == 0) {
            return TextStyle.defaultInstance();
        }

        TextStyle style = new TextStyle();

        for (String part : string.split("\\|", -1)) {
            int index = part.indexOf('=');
            if (index < 0) {
                throw new StyleParseException("Segment doesn't contain a key=value pair: " + part);
            }

            String keyS = part.substring(0, index).trim();
            ETextAttribute key = ETextAttribute.fromId(keyS);
            if (key == null) {
                throw new StyleParseException("Unknown attribute: " + keyS);
            }

            String valueS = part.substring(index + 1).trim();
            if (valueS.length() > 0) {
                Object value = key.valueFromString(valueS);
                if (value != null) {
                    style.attributes.put(key, value);
                }
            }
        }

        return style;
    }

    public TextStyle extend(TextStyle ts) {
        return combine(this, ts);
    }

    @CheckReturnValue
    public static TextStyle combine(TextStyle base, TextStyle ext) {
        if (ext == null) {
            return base;
        } else if (base == null || base.equals(ext)) {
            return ext; // Styles are equal, nothing to do
        }

        TextStyle result = new TextStyle(base);
        extendAttributes(result.attributes, ext.attributes);
        return result;
    }

    public static void extend(TextStyle[] out, TextStyle[] base, TextStyle[] ext) {
        if (out.length != base.length || base.length != ext.length) {
            throw new IllegalArgumentException(String.format(
                    "Inconsistent lengths: out=%d, base=%d, ext=%d", out.length, base.length, ext.length));
        }
        extend(out, 0, base, 0, ext, 0, out.length);
    }

    public static void extend(TextStyle[] out, int roff, TextStyle[] base, int boff, TextStyle[] ext, int eoff, int len) {
        TextStyle lastBase = null;
        TextStyle lastExt = null;
        TextStyle lastResult = null;
        for (int n = 0; n < len; n++) {
            TextStyle bs = base[boff++];
            TextStyle es = ext[eoff++];

            TextStyle result;
            if (lastBase == bs && lastExt == es) {
                // Optimization for when (base, ext) is the same as for the previous index
                result = lastResult;
            } else {
                result = TextStyle.combine(bs, es);

                lastBase = bs;
                lastExt = es;
                lastResult = result;
            }
            out[roff++] = result;
        }
    }

}
