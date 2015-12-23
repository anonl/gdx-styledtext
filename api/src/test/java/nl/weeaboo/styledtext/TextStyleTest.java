package nl.weeaboo.styledtext;

import org.junit.Assert;
import org.junit.Test;

public class TextStyleTest {

    private final StyledTextTestData testData = new StyledTextTestData();
    private final TextStyle fullStyle = testData.fullStyle;

    /** Conversion from/to string */
    @Test
    public void fromString() {
        final String storedString = "fontName=font-name|fontStyle=bolditalic|fontSize=14.0|align=center|color=11223344|underline=true|outlineSize=2.0|outlineColor=22334455|shadowColor=33445566|shadowDx=3.0|shadowDy=4.0";
        TextStyle storedStyle = TextStyle.fromString(storedString);
        Assert.assertEquals(fullStyle, storedStyle);
        Assert.assertEquals(storedString, fullStyle.toString());
    }

}
