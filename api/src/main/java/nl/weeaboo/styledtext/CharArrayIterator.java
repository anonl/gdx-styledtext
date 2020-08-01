package nl.weeaboo.styledtext;

import java.text.CharacterIterator;

/**
 * Character iterator implementation that uses a {@code char[]}.
 */
final class CharArrayIterator implements CharacterIterator {

    private final char[] chars;
    private final int off;
    private final int len;

    private int pos;

    /**
     * @param chars The characters to iterate over. This array is not copied, so any changes in the array are
     *        immediately reflected by this character iterator.
     */
    CharArrayIterator(char[] chars, int off, int len) {
        if (chars == null) {
            throw new NullPointerException("chars may not be null");
        }
        if (off < 0 || off > chars.length) {
            throw new IllegalArgumentException("Invalid off: " + off + " for char[" + chars.length + "]");
        }
        if (len < 0 || off + len > chars.length) {
            throw new IllegalArgumentException("Invalid len: " + len + " for char[" + chars.length + "], off=" + off);
        }

        this.chars = chars;
        this.off = off;
        this.len = len;
    }

    @Override
    public CharArrayIterator clone() {
        CharArrayIterator c = new CharArrayIterator(chars, off, len);
        c.pos = pos;
        return c;
    }

    @Override
    public char current() {
        if (pos >= 0 && pos < len) {
            return chars[off + pos];
        }
        return DONE;
    }

    @Override
    public char first() {
        pos = 0;
        return current();
    }

    @Override
    public char last() {
        pos = (len > 0 ? len - 1 : 0);
        return current();
    }

    @Override
    public char previous() {
        if (pos > 0) {
            pos--;
            return chars[off + pos];
        }
        pos = 0;
        return DONE;
    }

    @Override
    public char next() {
        if (pos < len - 1) {
            pos++;
            return chars[off + pos];
        }
        pos = len;
        return DONE;
    }

    @Override
    public char setIndex(int position) {
        if (position < 0 || position > len) {
            throw new IllegalArgumentException("Invalid index: " + position + " (length=" + len + ")");
        }
        pos = position;
        return current();
    }

    @Override
    public int getBeginIndex() {
        return 0;
    }

    @Override
    public int getEndIndex() {
        return len;
    }

    @Override
    public int getIndex() {
        return pos;
    }

}