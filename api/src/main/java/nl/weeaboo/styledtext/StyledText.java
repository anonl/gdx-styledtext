package nl.weeaboo.styledtext;

import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StyledText extends AbstractStyledText<StyledText> {

    public static final StyledText EMPTY_STRING = new StyledText("");

    private static final long serialVersionUID = 1L;

    public StyledText(String text) {
        this(text, TextStyle.defaultInstance());
    }
    public StyledText(String text, TextStyle style) {
        super(text, style);
    }

    /** For internal use only -- doesn't copy input arrays */
    StyledText(int len, char[] text, int toff, TextStyle[] styles, int soff) {
        super(len, text, toff, styles, soff);
    }

    @Override
    StyledText newInstance(int len, char[] text, int toff, TextStyle[] styles, int soff) {
        return new StyledText(len, text, toff, styles, soff);
    }

    /**
     * Simple string format function, converting patterns of the style <code>"abc {} def"</code> by replacing
     * occurrences of <code>{}</code> with supplied arguments. This is chosen to be equivalent with the style
     * of formatting used by SLF4J.
     *
     * @param formatString A string or {@link AbstractStyledText}.
     * @param args Zero or more arguments to splice into the format string. Arguments are converted to
     *             styled text representations.
     */
    public static StyledText format(CharSequence formatString, Object... args) {
        StyledTextFormatter formatter = new StyledTextFormatter(formatString, args);
        return formatter.format();
    }

    public MutableStyledText mutableCopy() {
        // Copy arrays here because the constructor we call doesn't copy
        return new MutableStyledText(len, getChars(), 0, getStyles(), 0);
    }

    /**
     * @see #getCharacterIterator(int, int)
     */
    public CharacterIterator getCharacterIterator() {
        return getCharacterIterator(0, length());
    }

    public CharacterIterator getCharacterIterator(int from, int to) {
        checkBounds(from, to);
        return new CharArrayIterator(text, toff + from, to - from);
    }

    /**
     * @return A new styled text object; this styled text concatenated with an unstyled string.
     *
     * @see #concat(List)
     */
    public StyledText concat(String text) {
        return concat(this, new StyledText(text));
    }

    /**
     * The first two arguments are explicit to make it make it impossible to mistake this function for an
     * overload of {@link #concat(String)}.
     *
     * @see #concat(List)
     */
    public static StyledText concat(StyledText first, StyledText second, StyledText... more) {
        List<StyledText> list = new ArrayList<StyledText>(2 + more.length);
        list.add(first);
        list.add(second);
        Collections.addAll(list, more);
        return concat(list);
    }

    /**
     * Creates a new styled text object by concatenating the given strings.
     */
    public static StyledText concat(List<? extends AbstractStyledText<?>> stexts) {
        int newLen = 0;
        for (AbstractStyledText<?> st : stexts) {
            newLen += st.length();
        }

        char[] newText = new char[newLen];
        TextStyle[] newStyles = new TextStyle[newLen];

        int t = 0;
        for (AbstractStyledText<?> st : stexts) {
            int stLen = st.length();
            st.getChars(newText, t, stLen);
            st.getStyles(newStyles, t, stLen);
            t += stLen;
        }

        return new StyledText(newLen, newText, 0, newStyles, 0);
    }

}
