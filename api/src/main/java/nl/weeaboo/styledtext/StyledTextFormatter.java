package nl.weeaboo.styledtext;

final class StyledTextFormatter {

    private final CharSequence formatString;
    private final AbstractStyledText<?> styledFormatString;
    private final Object[] args;

    private int pos = -1;
    private int argPos;

    /**
     * @see StyledText#format(CharSequence, Object...)
     */
    public StyledTextFormatter(CharSequence formatString, Object... args) {
        this.formatString = formatString;
        this.args = args.clone();

        if (formatString instanceof AbstractStyledText<?>) {
            styledFormatString = (AbstractStyledText<?>)formatString;
        } else {
            styledFormatString = null;
        }
    }

    public StyledText format() throws IllegalArgumentException {
        MutableStyledText result = new MutableStyledText();
        while (hasNext()) {
            char c = next();
            if (c == '\\' && hasNext()) {
                // Escape sequence
                c = next();
                result.append(c, curStyle());
            } else if (c == '{' && hasNext() && formatString.charAt(pos + 1) == '}') {
                next();
                result.append(toStyledText(nextArg()));
            } else {
                result.append(c, curStyle());
            }
        }

        if (argPos != args.length) {
            throw new IllegalArgumentException("Too many parameters given for format string: \""
                    + formatString + "\", expected " + argPos + ", got " + args.length);
        }
        return result.immutableCopy();
    }

    private AbstractStyledText<?> toStyledText(Object object) {
        if (object instanceof AbstractStyledText<?>) {
            return (AbstractStyledText<?>)object;
        }
        return new StyledText(String.valueOf(object));
    }

    private boolean hasNext() {
        return pos < formatString.length() - 1;
    }

    private char next() {
        pos++;
        return formatString.charAt(pos);
    }

    private TextStyle curStyle() {
        if (styledFormatString != null) {
            return styledFormatString.getStyle(pos);
        }
        return TextStyle.defaultInstance();
    }

    private Object nextArg() {
        if (argPos >= args.length) {
            throw new IllegalArgumentException("Not enough parameters given for format string: \""
                    + formatString + "\" (got " + args.length + ")");
        }
        return args[argPos++];
    }

}
