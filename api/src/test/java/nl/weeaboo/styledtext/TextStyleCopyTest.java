package nl.weeaboo.styledtext;

import org.junit.Assert;
import org.junit.Test;

public class TextStyleCopyTest {

    private final StyledTextTestData testData = new StyledTextTestData();
    private final TextStyle fullStyle = testData.fullStyle;

    @Test
    public void mutableImmutable() {
        MutableTextStyle mts = fullStyle.mutableCopy();
        // Mutable style still equals original
        Assert.assertEquals(mts, fullStyle);

        mts.setColor(1, 2, 3);
        Assert.assertNotEquals(mts, fullStyle);
        TextStyle unequalCopy = mts.immutableCopy();

        mts.setColor(StyledTextTestData.TEXT_COLOR);
        Assert.assertEquals(mts, fullStyle);
        TextStyle equalCopy = mts.immutableCopy();

        Assert.assertNotEquals(fullStyle, unequalCopy);
        Assert.assertEquals(fullStyle, equalCopy);
    }

    @Test
    public void mutableCopyConstructor() {
        MutableTextStyle alpha = fullStyle.mutableCopy();
        MutableTextStyle beta = alpha.copy();
        Assert.assertEquals(alpha, beta);

        // Assert that the two mutable styles are independent
        alpha.removeAttribute(ETextAttribute.ALIGN);
        Assert.assertNotEquals(alpha, beta);
    }

}
