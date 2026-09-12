package com.smartcity.structures;

import com.smartcity.model.Place;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComparatorsTest {
    @Test
    public void testSortByName() {
        Place a = new Place(0, "Zoo", "Park", "A", "");
        Place b = new Place(1, "Apple Park", "Park", "B", "");
        Place c = new Place(2, "Media", "Culture", "C", "");
        Place d = new Place(3, "apple cafe", "Restaurant", "D", "");

        List<Place> places = new ArrayList<>();
        places.add(a);
        places.add(b);
        places.add(c);
        places.add(d);

        Collections.sort(places, Comparators.byName());
        assertEquals(3, places.get(0).getId());
        assertEquals(1, places.get(1).getId());
        assertEquals(2, places.get(2).getId());
        assertEquals(0, places.get(3).getId());
    }

    @Test
    public void testSortByCategory() {
        Place a = new Place(0, "Zoo", "Park", "A", "");
        Place b = new Place(1, "Apple Park", "Park", "B", "");
        Place c = new Place(2, "Media", "Culture", "C", "");
        Place d = new Place(3, "apple cafe", "Restaurant", "D", "");

        List<Place> places = new ArrayList<>();
        places.add(a);
        places.add(b);
        places.add(c);
        places.add(d);

        Collections.sort(places, Comparators.byCategory());
        assertEquals(2, places.get(0).getId());
        assertEquals(1, places.get(1).getId());
        assertEquals(0, places.get(2).getId());
        assertEquals(3, places.get(3).getId());        
    }

    @Test
    public void testSortByNameWithNullLast() {
        Place a = new Place(0, null, "Park", "A", "");
        Place b = new Place(1, "Apple Park", "Park", "B", "");
        Place c = new Place(2, "Zoo", "Culture", "C", "");

        List<Place> places = new ArrayList<>();
        places.add(a);
        places.add(b);
        places.add(c);

        Collections.sort(places, Comparators.byName());
        assertEquals(1, places.get(0).getId());
        assertEquals(2, places.get(1).getId());
        assertEquals(0, places.get(2).getId());
    }
}
