package nl.weeaboo.styledtext;

import java.io.Serializable;
import java.util.Locale;

public enum ETextAttribute {

    FONT_NAME("fontName", String.class),
    FONT_STYLE("fontStyle", EFontStyle.class),
    FONT_SIZE("fontSize", Float.class),
    ALIGN("align", ETextAlign.class),
    COLOR("color", Integer.class),
    UNDERLINE("underline", Boolean.class),
    OUTLINE_SIZE("outlineSize", Float.class),
    OUTLINE_COLOR("outlineColor", Integer.class),
    SHADOW_COLOR("shadowColor", Integer.class),
    SHADOW_DX("shadowDx", Float.class),
    SHADOW_DY("shadowDy", Float.class),
    SPEED("speed", Float.class);

    private final String id;
    private final Class<?> type;

    private ETextAttribute(String id, Class<? extends Serializable> type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }

    /**
     * @return The enum value matching the given string representation, or {@code null} if no match is found.
     *         The matching algorithm is case-sensitive.
     */
    public static ETextAttribute fromId(String str) {
        for (ETextAttribute a : values()) {
            if (a.getId().equals(str)) {
                return a;
            }
        }
        return null;
    }

    boolean isValidValue(Object val) {
        return val == null || type.isAssignableFrom(val.getClass());
    }

    Object extendValue(Object base, Object ext) {
        if (base == null) {
            return ext;
        } else if (ext == null) {
            return base;
        }

        // Custom behavior for certain attributes
        if (this == FONT_STYLE) {
            return EFontStyle.combine((EFontStyle)base, (EFontStyle)ext);
        }

        // Default behavior: Overwrite value with ext
        return ext;
    }

    public Object valueFromString(String string) {
        if (string == null) {
            return null;
        }

        // By name
        if (this == COLOR || this == OUTLINE_COLOR || this == SHADOW_COLOR) {
            int val = (int)(Long.parseLong(string, 16) & 0xFFFFFFFFL);
            if (string.length() < 8) {
                val = 0xFF000000 | val;
            }
            return val;
        } else if (this == FONT_NAME) {
            return string.toLowerCase(Locale.ROOT);
        }

        // Enum types
        if (type == EFontStyle.class) {
            return EFontStyle.fromString(string);
        } else if (type == ETextAlign.class) {
            return ETextAlign.fromString(string);
        }

        // Simple types
        try {
            if (type == Boolean.class) {
                return Boolean.parseBoolean(string);
            } else if (type == Byte.class) {
                return Byte.parseByte(string);
            } else if (type == Short.class) {
                return Short.parseShort(string);
            } else if (type == Integer.class) {
                return Integer.parseInt(string);
            } else if (type == Long.class) {
                return Long.parseLong(string);
            } else if (type == Float.class) {
                return Float.parseFloat(string);
            } else if (type == Double.class) {
                return Double.parseDouble(string);
            } else if (type == String.class) {
                return string;
            }
        } catch (NumberFormatException nfe) {
            //Ignore
        }

        return null;
    }

    String valueToString(Object val) {
        if (val == null) {
            return null;
        }

        if (this == COLOR || this == OUTLINE_COLOR || this == SHADOW_COLOR) {
            return String.format(Locale.ROOT, "%08x", (Integer)val);
        } else if (this == FONT_NAME) {
            return String.valueOf(val).toLowerCase(Locale.ROOT);
        }

        return String.valueOf(val);
    }

}
