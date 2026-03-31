package seedu.cardcollector.parsing;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DisambiguatorTest {
    private final ArrayList<String> keywords = new ArrayList<>(List.of("share", "shard", "shout", "fire"));
    private final Map<String, Integer> map = new HashMap<>() {{
            put("share", 11);
            put("shard", 22);
            put("shout", 33);
            put("fire", 44);
            }};

    @Test
    public void disambiguate_validStringAgainstArrayList_success() {
        // Test for exact matches
        assertEquals("share", Disambiguator.disambiguate(keywords, "share"));
        assertEquals("shard", Disambiguator.disambiguate(keywords, "shard"));
        assertEquals("shout", Disambiguator.disambiguate(keywords, "shout"));
        assertEquals("fire", Disambiguator.disambiguate(keywords, "fire"));

        // Test for non-exact matches
        assertEquals("shout", Disambiguator.disambiguate(keywords, "sho"));
        assertEquals("fire", Disambiguator.disambiguate(keywords, "f"));
        assertEquals("fire", Disambiguator.disambiguate(keywords, "fi"));
    }

    @Test
    public void disambiguate_invalidString_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Disambiguator.disambiguate(keywords, ""));
        assertThrows(IllegalArgumentException.class, () -> Disambiguator.disambiguate(keywords, "fried"));
        assertThrows(IllegalArgumentException.class, () -> Disambiguator.disambiguate(keywords, "fired"));
        assertThrows(IllegalArgumentException.class, () -> Disambiguator.disambiguate(keywords, "s"));
        assertThrows(IllegalArgumentException.class, () -> Disambiguator.disambiguate(keywords, "sh"));
        assertThrows(IllegalArgumentException.class, () -> Disambiguator.disambiguate(keywords, "sha"));
        assertThrows(IllegalArgumentException.class, () -> Disambiguator.disambiguate(keywords, "shar"));
        assertThrows(IllegalArgumentException.class, () -> Disambiguator.disambiguate(keywords, "boot"));
    }

    @Test
    public void disambiguate_validStringAgainstMap_success() {
        // Test for exact matches
        assertEquals(11, Disambiguator.disambiguate(map, "share"));
        assertEquals(22, Disambiguator.disambiguate(map, "shard"));
        assertEquals(33, Disambiguator.disambiguate(map, "shout"));
        assertEquals(44, Disambiguator.disambiguate(map, "fire"));

        // Test for non-exact matches
        assertEquals(33, Disambiguator.disambiguate(map, "sho"));
        assertEquals(44, Disambiguator.disambiguate(map, "f"));
        assertEquals(44, Disambiguator.disambiguate(map, "fi"));
    }


    private enum SimpleTestEnum {
        SHARE("share"),
        SHARD("shard"),
        SHOUT("shout"),
        FIRE("fire");

        private final String keyword;

        SimpleTestEnum(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }
    }

    @Test
    public void disambiguate_validStringAgainstEnum_success() {
        // Test for exact matches
        assertEquals(SimpleTestEnum.SHARE,
                Disambiguator.disambiguate(SimpleTestEnum.class, SimpleTestEnum::getKeyword, "share"));
        assertEquals(SimpleTestEnum.SHARD,
                Disambiguator.disambiguate(SimpleTestEnum.class, SimpleTestEnum::getKeyword, "shard"));
        assertEquals(SimpleTestEnum.SHOUT,
                Disambiguator.disambiguate(SimpleTestEnum.class, SimpleTestEnum::getKeyword,"shout"));
        assertEquals(SimpleTestEnum.FIRE,
                Disambiguator.disambiguate(SimpleTestEnum.class, SimpleTestEnum::getKeyword, "fire"));

        // Test for non-exact matches
        assertEquals(SimpleTestEnum.SHOUT,
                Disambiguator.disambiguate(SimpleTestEnum.class, SimpleTestEnum::getKeyword, "sho"));
        assertEquals(SimpleTestEnum.FIRE,
                Disambiguator.disambiguate(SimpleTestEnum.class, SimpleTestEnum::getKeyword, "f"));
        assertEquals(SimpleTestEnum.FIRE,
                Disambiguator.disambiguate(SimpleTestEnum.class, SimpleTestEnum::getKeyword, "fi"));
    }
}
