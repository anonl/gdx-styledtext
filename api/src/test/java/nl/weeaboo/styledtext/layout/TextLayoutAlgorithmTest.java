package nl.weeaboo.styledtext.layout;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.styledtext.ETextAlign;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;

public class TextLayoutAlgorithmTest {

    private final TextLayoutAlgorithm algo = new TextLayoutAlgorithm(new BasicFontStore());

    // Using dummy font store makes each glyph (fontSize x fontSize)
    private static final TextStyle style = new TextStyle(null, 10f);

    private LayoutParameters params;

    @Before
    public void before() {
        params = new LayoutParameters();
        params.wrapWidth = 100f;
    }

    @Test
    public void simpleLayout() {
        // Line wrap because 'ghi' doesn't fit
        ITextLayout layout = layout("abc def ghi");
        assertLines(layout, "abc|def", "ghi");

        // Line wrap boundary occurs in the middle of whitespace
        layout = layout("abc def           ghi");
        assertLines(layout, "abc|def", "ghi");
    }

    /** Layout of text where the TextStyle is null */
    @Test
    public void nullStyle() {
        // If no style is provided, a default style is used (with size=12 instead of 10)
        ITextLayout layout = layout(new StyledText("abcd efghi"));
        assertLines(layout, "abcd", "efghi");
    }

    @Test
    public void newLines() {
        // Manual newlines cause line breaks
        ITextLayout layout = layout("abc\n \n\n def");
        assertLines(layout, "abc", "", "", "def");

        // Whitespace segment would cause a line wrap anyway, but also contains a newline
        // Check that this case doesn't result in double newlines.
        layout = layout("abc def          \nghi");
        assertLines(layout, "abc|def", "ghi");
    }

    @Test
    public void textAlign() {
        // left-right
        ITextLayout layout = layout(left("abc"), right("def"));
        assertLayout(layout, "abc    def");

        // left-center
        layout = layout(left("abc"), center("def"));
        assertLayout(layout, "abc  def  ");

        // right-center (spacing is equally divided)
        layout = layout(right("ab"), center("cd"));
        assertLayout(layout, "  ab  cd  ");

        // right-right (adjacent sections with the same layout are merged)
        layout = layout(right("ab"), right("cd"));
        assertLayout(layout, "      abcd");

        // right-left is pretty much the same as center-aligning the concatenated string
        layout = layout(right("abc"), left("def"));
        assertLayout(layout, "  abcdef  ");

        // Basic test with spacing
        layout = layout(left(" abc "), left("def"));
        assertLayout(layout, " abc def  ");

        // Spaces aren't collapsed
        layout = layout(left("  abc "), left(" def"));
        assertLayout(layout, "  abc  def");

        // \t takes up four spaces
        layout = layout(left("\tabc"));
        assertLayout(layout, "    abc  ");
    }

    private static void assertLayout(ITextLayout layout, String... lines) {
        // First check if line count matches
        Assert.assertEquals(lines.length, layout.getLineCount());

        for (int line = 0; line < lines.length; line++) {
            ITextLayout lineLayout = layout.getLineRange(line, line + 1);
            String message = "line[" + line + "]";
            assertLineLayout(message, lineLayout, lines[line]);
        }
    }

    private static void assertLineLayout(String message, ITextLayout layout, String expected) {
        char[] actual = new char[expected.length()];
        Arrays.fill(actual, ' ');

        for (ITextElement elem : layout.getElements()) {
            // Assume each glyph is square
            int ix = Math.round(elem.getX() / elem.getLayoutHeight());
            for (int n = 0; n < elem.getGlyphCount(); n++) {
                actual[ix + n] = (char)elem.getGlyphId(n);
            }
        }
        Assert.assertEquals(message, expected, new String(actual));
    }

    private static void assertLines(ITextLayout layout, String... lines) {
        // First check if line count matches
        Assert.assertEquals(lines.length, layout.getLineCount());

        for (int line = 0; line < lines.length; line++) {
            ITextLayout lineLayout = layout.getLineRange(line, line + 1);
            String message = "line[" + line + "]";
            assertRuns(message, lineLayout, lines[line]);
        }
    }

    private static void assertRuns(String message, ITextLayout layout, String runString) {
        StringBuilder sb = new StringBuilder();
        for (ILayoutElement elem : layout.getElements()) {
            if (elem instanceof BasicTextElement) {
                BasicTextElement textElem = (BasicTextElement)elem;
                if (sb.length() > 0) {
                    sb.append("|");
                }
                sb.append(textElem.getText().toString());
            }
        }

        Assert.assertEquals(message, runString, sb.toString());
    }

    private ITextLayout layout(String str) {
        return layout(new StyledText(str, style));
    }

    private ITextLayout layout(StyledText... stexts) {
        return algo.layout(StyledText.concat(Arrays.asList(stexts)), params);
    }

    private static StyledText left(String string) {
        return new StyledText(string, alignStyle(ETextAlign.LEFT));
    }
    private static StyledText center(String string) {
        return new StyledText(string, alignStyle(ETextAlign.CENTER));
    }
    private static StyledText right(String string) {
        return new StyledText(string, alignStyle(ETextAlign.RIGHT));
    }

    private static TextStyle alignStyle(ETextAlign align) {
        MutableTextStyle mts = style.mutableCopy();
        mts.setAlign(align);
        return mts.immutableCopy();
    }

}
