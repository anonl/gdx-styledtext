package nl.weeaboo.styledtext;

import org.junit.Assert;
import org.junit.Test;

public class TextStyleParseTest {

    private final StyledTextTestData testData = new StyledTextTestData();
    private final TextStyle emptyStyle = TextStyle.defaultInstance();
    private final TextStyle fullStyle = testData.fullStyle;

    @Test
    public void allAttributes() throws StyleParseException {
        final String storedString = "fontName=Font-Name|fontStyle=bolditalic|fontSize=14.0|align=center|color=11223344|underline=true|outlineSize=2.0|outlineColor=22334455|shadowColor=33445566|shadowDx=3.0|shadowDy=4.0";
        TextStyle storedStyle = TextStyle.fromString(storedString);
        Assert.assertEquals(fullStyle, storedStyle);
        Assert.assertEquals(storedString, fullStyle.toString());
    }

    @Test(expected = NullPointerException.class)
    public void nullString() throws StyleParseException {
        TextStyle.fromString(null);
    }

    @Test
    public void emptyString() throws StyleParseException {
        Assert.assertEquals(emptyStyle, TextStyle.fromString(""));
    }

    @Test(expected = StyleParseException.class)
    public void whitespaceString() throws StyleParseException {
        TextStyle.fromString(" ");
    }

    @Test
    public void emptyValue() throws StyleParseException {
        Assert.assertEquals(emptyStyle, TextStyle.fromString("color="));
    }

    @Test(expected = StyleParseException.class)
    public void invalidAttribute() throws StyleParseException {
        TextStyle.fromString("invalid=invalid");
    }

}
