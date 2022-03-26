package hu.webarticum.miniconnect.rdmsframework.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ImmutableList;

class LikeMatcherTest {

    @Test
    void testFullyFixed() {
        LikeMatcher matcher = new LikeMatcher("lorem");
        ImmutableList<String> input = ImmutableList.of(
                "", "lorem", "llorem", "loremx", "ipsum");
        ImmutableList<String> expectedOutput = ImmutableList.of(
                "lorem");
        assertThat(input.filter(matcher)).isEqualTo(expectedOutput);
    }

    @Test
    void testSingleChar() {
        LikeMatcher matcher = new LikeMatcher("x_z");
        ImmutableList<String> input = ImmutableList.of(
                "", "xz", "xyz", "xuz", "xyyz", "xyzu", "abc");
        ImmutableList<String> expectedOutput = ImmutableList.of(
                "xyz", "xuz");
        assertThat(input.filter(matcher)).isEqualTo(expectedOutput);
    }

    @Test
    void testAnyChar() {
        LikeMatcher matcher = new LikeMatcher("a%z");
        ImmutableList<String> input = ImmutableList.of(
                "", "az", "afz", "abhz", "abzu", "taz", "lorem", "ipsum");
        ImmutableList<String> expectedOutput = ImmutableList.of(
                "az", "afz", "abhz");
        assertThat(input.filter(matcher)).isEqualTo(expectedOutput);
    }

    @Test
    void testComplexPattern() {
        LikeMatcher matcher = new LikeMatcher("l_r%");
        ImmutableList<String> input = ImmutableList.of(
                "", "lr", "lar", "lorem", "ipsum", "larlar", "alert");
        ImmutableList<String> expectedOutput = ImmutableList.of(
                "lar", "lorem", "larlar");
        assertThat(input.filter(matcher)).isEqualTo(expectedOutput);
    }
    
}
