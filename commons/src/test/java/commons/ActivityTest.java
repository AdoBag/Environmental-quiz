package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {
    private static final Activity a1 = new Activity("3", "aPath", "title", 301, "imagination");
    private static final Activity a11 = new Activity("3", "aPath", "title", 301, "imagination");
    private static final Activity a2 = new Activity("5", "thePath", "anotherTitle", 420, "imagination");

    @Test
    void testConstructor() {
        Activity activity = new Activity("3", "aPath", "title", 301, "imagination");
        assertNotNull(activity);
        assertEquals("aPath", activity.image_path);
        assertEquals("title", activity.title);
        assertEquals(301, activity.consumption_in_wh);
        assertEquals("imagination", activity.source);
    }

    @Test
    void testEquals() {
        assertEquals(a1, a11);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(a1, a2);
    }

    @Test
    void testHashCode() {
        assertEquals(a1.hashCode(), a11.hashCode());
    }

    @Test
    void testNotHashCode() {
        assertNotEquals(a1.hashCode(), a2.hashCode());
    }


    @Test
    void hasToString() {
        var actual = new Activity("3", "aPath", "title", 301, "imagination").toString();
        assertTrue(actual.contains(Activity.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("image_path"));
    }

    @Test
    void getId() {
        assertEquals("5", a2.getId());
    }

    @Test
    void setId() {
        a2.setId("4");
        assertEquals("4", a2.getId());
    }

    @Test
    void getImagePath() {
        assertEquals("thePath", a2.getImagePath());
    }

    @Test
    void setImagePath() {
        a2.setImagePath("somethingElse");
        assertEquals("somethingElse", a2.getImagePath());
    }

    @Test
    void getTitle() {
        assertEquals("title", a1.getTitle());
    }

    @Test
    void setTitle() {
        a2.setTitle("different");
        assertEquals("different", a2.getTitle());
    }

    @Test
    void getConsumtpion() {
        assertEquals(420, a2.getConsumption());
    }

    @Test
    void setConsumtpion() {
        a2.setConsumption(555);
        assertEquals(555, a2.getConsumption());
    }

    @Test
    void getSource() {
        assertEquals("imagination", a2.getSource());
    }

    @Test
    void setSource() {
        a2.setSource("sourceee");
        assertEquals("sourceee", a2.getSource());
        a2.setSource("imagination");
    }
}