package nl.weeaboo.styledtext;

import java.io.Serializable;
import java.text.Bidi;
import java.util.Arrays;

/**
 * @param <S> Self type. Used to automatically provide the correct subclass for methods that return a copy of
 *        this object.
 */
abstract class AbstractStyledText<S extends AbstractStyledText<S>> implements CharSequence, Serializable {

    private static final long serialVersionUID = 2L;

    protected int len;

    protected char[] text;
    protected int toff;

    protected TextStyle[] styles;
    protected int soff;

    protected AbstractStyledText(String str, TextStyle style) {
        checkNotNull(style);

        len = str.length();
        text = str.toCharArray();

        styles = new TextStyle[len];
        Arrays.fill(styles, style);
    }

    /**
     * For internal use only -- doesn't copy input arrays
     */
    AbstractStyledText(int len, char[] text, int toff, TextStyle[] styles, int soff) {
        this.len = len;
        this.text = text;
        this.toff = toff;
        this.styles = styles;
        this.soff = soff;
    }

    /**
     * For internal use only -- doesn't copy input arrays
     * <p>
     * Creates a new styled text object with the same type as this object.
     */
    abstract S newInstance(int len, char[] text, int toff, TextStyle[] styles, int soff);

    @Override
    public final int hashCode() {
        int hash = 0;
        for (int n = 0; n < len; n++) {
            hash = 31 * hash + text[toff + n];
        }
        return hash;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof AbstractStyledText)) {
            return false;
        }

        AbstractStyledText<?> other = (AbstractStyledText<?>)obj;
        int len = length();
        if (len != other.length()) {
            return false;
        }

        for (int n = 0; n < len; n++) {
            if (getChar(n) != other.getChar(n)) {
                return false;
            }

            TextStyle s0 = getStyle(n);
            TextStyle s1 = other.getStyle(n);
            if (s0 != s1 && (s0 == null || !s0.equals(s1))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return The string represented by this styled text object, stripped of its styling information.
     */
    @Override
    public String toString() {
        return new String(text, toff, len);
    }

    protected final void checkBounds(int index) {
        if (index < 0 || index >= len) {
            throw new ArrayIndexOutOfBoundsException("index=" + index + ", length=" + len);
        }
    }

    protected final void checkBounds(int from, int to) {
        if (from < 0) {
            throw new ArrayIndexOutOfBoundsException(from);
        }
        if (to > len) {
            throw new ArrayIndexOutOfBoundsException(to);
        }
        if (from > to) {
            throw new ArrayIndexOutOfBoundsException(to - from);
        }
    }

    protected final <T> T checkNotNull(T val) {
        if (val == null) {
            throw new NullPointerException("Value should not be null");
        }
        return val;
    }

    @Override
    public final int length() {
        return len;
    }

    @Override
    public final char charAt(int index) {
        checkBounds(index);
        return text[toff + index];
    }

    /**
     * @see #charAt(int)
     */
    public final char getChar(int index) {
        return charAt(index);
    }

    /**
     * @return The {@link TextStyle} at the specified index, or {@code null} if no style exists at the
     *         specified index.
     */
    public final TextStyle getStyle(int index) {
        checkBounds(index);
        return styles[soff + index];
    }

    /**
     * @see #getChars(char[], int, int)
     */
    protected final char[] getChars() {
        char[] out = new char[len];
        getChars(out, 0, len);
        return out;
    }

    /**
     * Copies characters from this styled text to the given output array.
     *
     * @param out The output array.
     * @param off Offset into the output array.
     * @param len Desired number of characters to copy.
     *
     * @throws ArrayIndexOutOfBoundsException If the given offset/length are invalid.
     *
     * @see #getStyles(TextStyle[], int, int)
     */
    protected final void getChars(char[] out, int off, int len) {
        System.arraycopy(text, toff, out, off, len);
    }

    /**
     * @see #getStyles(TextStyle[], int, int)
     */
    protected final TextStyle[] getStyles() {
        TextStyle[] out = new TextStyle[len];
        getStyles(out, 0, len);
        return out;
    }

    /**
     * Copies styles from this styled text to the given output array.
     *
     * @param out The output array.
     * @param off Offset into the output array.
     * @param len Desired number of objects to copy.
     *
     * @throws ArrayIndexOutOfBoundsException If the given offset/length are invalid.
     *
     * @see #getChars(char[], int, int)
     */
    protected final void getStyles(TextStyle[] out, int off, int len) {
        System.arraycopy(styles, soff, out, off, len);
    }

    public boolean isBidi() {
        return Bidi.requiresBidi(text, toff, len);
    }

    /**
     * @param isRightToLeft If {@code true}, uses right-to-left as the default direction.
     */
    public Bidi getBidi(boolean isRightToLeft) {
        int flags = Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT;
        if (isRightToLeft) {
            flags = Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT;
        }

        return new Bidi(text, toff, null, 0, len, flags);
    }

    @Override
    public S subSequence(int start, int end) {
        return substring(start, end);
    }

    /**
     * @see #substring(int, int)
     * @see String#substring(int)
     */
    public S substring(int from) {
        return substring(from, length());
    }

    /**
     * @see String#substring(int, int)
     */
    public S substring(int from, int to) {
        if (to < from) {
            throw new IllegalArgumentException(
                "Can't have a substring of negative size, from=" + from + " to=" + to);
        }

        // Check if to and from lie within the acceptable range
        checkBounds(from, to);

        return newInstance(to - from, text, toff + from, styles, soff + from);
    }

}
