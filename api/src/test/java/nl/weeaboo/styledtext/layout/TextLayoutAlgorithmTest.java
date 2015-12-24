package nl.weeaboo.styledtext.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;

public class TextLayoutAlgorithmTest {

    private final TextLayoutAlgorithm algo = new TextLayoutAlgorithm(new BasicFontStore());

    // Using dummy font store makes each glyph (fontSize x fontSize)
    private final TextStyle style = new TextStyle(null, 10f);

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

    private static void assertLines(ITextLayout layout, String... lines) {
        // First check if line count matches
        Assert.assertEquals(lines.length, layout.getLineCount());

        for (int line = 0; line < lines.length; line++) {
            ITextLayout lineLayout = layout.getLineRange(line, line + 1);
            assertRuns(lineLayout, lines[line].split("\\|"));
        }
    }

    private static void assertRuns(ITextLayout layout, String... runs) {
        List<String> elems = new ArrayList<String>();
        for (ILayoutElement elem : layout.getElements()) {
            if (elem instanceof BasicTextElement) {
                BasicTextElement textElem = (BasicTextElement)elem;
                elems.add(textElem.getText().toString());
            }
        }

        Assert.assertEquals(Arrays.asList(runs), elems);
    }

    private ITextLayout layout(String str) {
        StyledText stext = new StyledText(str, style);
        return algo.layout(stext, params);
    }

}
