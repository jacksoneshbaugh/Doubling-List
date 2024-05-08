import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.NoSuchElementException;
import java.util.ListIterator;

/**
 * Test class for {@link DoublingList}.
 * 
 * @author Jackson Eshbaugh
 * @version 04/10/2024
 */
public class DoublingListTest {
    
    public DoublingList<String> list, iteratorTest;
    
    public DoublingListTest() { }
    
    @BeforeEach
    public void setUp() {
        list = new DoublingList<String>();
        iteratorTest = new DoublingList<String>();
        iteratorTest.add("A");
        iteratorTest.add("B");
        iteratorTest.add("C");
        iteratorTest.add("D");
        iteratorTest.add("E");
        iteratorTest.add("F");
        iteratorTest.add("G");
    }
    
    
    /**
     * Tests the {@link DoublingList#add(E)} method.
     * <ol>
     *  <li>Adds to an empty list.</li>
     *  <li>Adds to the end of a list.</li>
     *  <li>Attempts to add {@code null} to the list, catches the exception.</li>
     * </ol>
     */
    @Test
    @DisplayName("add(E)")
    public void testAddE() {
        
        // Add to an empty list
        list.add("A");
        assertEquals("[(A)]", list.toStringInternal());
        
        // Add to the end of the list
        
        // This one also ensures that adding a new node works.
        list.add("B");
        assertEquals("[(A), (B, —)]", list.toStringInternal());
        
        list.add("C");
        assertEquals("[(A), (B, C)]", list.toStringInternal());
        
        // NullPointer test
        
        boolean nullPointer = false;
        
        try {
            list.add(null);
        } catch (NullPointerException e) {
            nullPointer = true;
        }
        
        assertTrue(nullPointer);
    }
    
    /**
     * Tests the {@link DoublingList#add(int, E)} method.
     * <ol>
     *  <li>Adds to an empty list.</li>
     *  <li>Adds to the end of a list.</li>
     *  <li>Adds to the front of a list.</li>
     *  <li>Adds to the middle of a list.</li>
     *  <li>Tests the scenarios from figures 4 — 9 in the project spec.</li>
     *  <li>Attempts to add {@code null} to the list, catches the exception.</li>
     * </ol>
     */
    @Test
    @DisplayName("add(int, E)")
    public void testAddIntE() {
        
        // Add to an empty list (figure 4)
        list.add(0, "A");
        assertEquals("[(A)]", list.toStringInternal());
        
        // Add to the end of the list
        
        // This one also ensures that adding a new node works.
        list.add(1, "B");
        assertEquals("[(A), (B, —)]", list.toStringInternal());
        
        list.add(2, "C");
        assertEquals("[(A), (B, C)]", list.toStringInternal());
        
        // Setup figure 5 from the spec
        
        list.add(3, "D");
        list.add(4, "E");
        list.add(5, "F");
        list.add(6, "G");
        list.add(7, "F");
        list.add(8, "G");
        
        list.remove(5);
        list.remove(5);
        assertEquals("[(A), (B, C), (D, E, —, —), (F, G, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Test figure 5
        
        list.add(3, "X");
        assertEquals("[(A), (B, C), (X, D, E, —), (F, G, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Setup Figure 6 from spec
        
        list = new DoublingList<String>();
        
        list.add("A");
        list.add("B");
        list.add("B");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        list.add("H");
        list.add("I");
        list.add("J");
        list.add("K");
        list.add("L");
        list.add("M");
        
        assertEquals("[(A), (B, B), (B, C, D, E), (F, G, H, I, J, K, L, M)]", list.toStringInternal());
        
        list.remove(1);
        list.remove(1);
        
        assertEquals("[(A), (—, —), (B, C, D, E), (F, G, H, I, J, K, L, M)]", list.toStringInternal());
        
        // Test figure 6
        list.add(9, "X");
        assertEquals("[(A), (B, —), (C, D, E, F), (G, H, I, X, J, K, L, M)]", list.toStringInternal());
        
        // Setup figure 7 from spec
        
        list = new DoublingList<String>();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        list.add("H");
        list.add("I");
        
        assertEquals("[(A), (B, C), (D, E, F, G), (H, I, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Test figure 7
        
        list.add(2, "X");
        assertEquals("[(A), (B, X), (C, D, E, F), (G, H, I, —, —, —, —, —)]", list.toStringInternal());
        
        // Setup figure 8 from spec
        
        list = new DoublingList<String>();
        list.add("B");
        list.add("B");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("E");
        list.add("E");
        
        list.remove(1);
        list.remove(1);
        list.remove(4);
        list.remove(4);
        
        assertEquals("[(B), (—, —), (C, D, E, —), (—, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Test figure 8
        
        list.add(4, "F");
        assertEquals("[(B), (—, —), (C, D, E, F), (—, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        list.add(5, "G");
        assertEquals("[(B), (C, —), (D, E, F, G), (—, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        list.add(6, "H");
        assertEquals("[(B), (C, D), (E, F, G, H), (—, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        list.add(7, "I");
        assertEquals("[(B), (C, D), (E, F, G, H), (I, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Setup figure 9 from spec
        
        list = new DoublingList<String>();
        list.add("F");
        list.add("I");
        list.add("J");
        list.add("M");
        list.add("P");
        list.add("Q");
        list.add("R");
        
        assertEquals("[(F), (I, J), (M, P, Q, R)]", list.toStringInternal());
        
        // Test figure 9
        list.add("T");
        assertEquals("[(F), (I, J), (M, P, Q, R), (T, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        // NullPointer test
        
        boolean nullPointer = false;
        
        try {
            list.add(null);
        } catch (NullPointerException e) {
            nullPointer = true;
        }
        
        assertTrue(nullPointer);
    }
    
    /**
     * Tests the {@link DoublingList#remove(int)} method:
     * 
     * <ol>
     *  <li>Tests figure 10 from the project spec:
     *      <ul>
     *        <li>Deleting from the beginning of a node.</li>
     *        <li>Deleting from the end of a node.</li>
     *      </ul>
     *  </li>
     *  <li>Tests figure 11 from the project spec:
     *      <ul>
     *        <li>Deleting resulting in list resizing</li>
     *      </ul>
     *  </li>
     *  <li>Tests removing an out-of-bounds index (both {@code -1} and some value {@code >= size})</li>
     * </ol>
     */
    @Test
    @DisplayName("remove(int)")
    public void testRemove() {
        // Setup figure 10
        
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        list.add("H");
        list.add("I");
        
        assertEquals("[(A), (B, C), (D, E, F, G), (H, I, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Test figure 10 (b)
        assertEquals("E", list.remove(4));
        assertEquals("[(A), (B, C), (D, F, G, —), (H, I, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Test figure 10 (c)
        assertEquals("B", list.remove(1));
        assertEquals("[(A), (C, —), (D, F, G, —), (H, I, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Setup figure 11
        
        list = new DoublingList<String>();
        
        list.add("A");
        list.add("B");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("E");
        list.add("F");
        list.remove(1);
        list.remove(1);
        list.remove(3);
        list.remove(3);
        
        assertEquals("[(A), (—, —), (C, D, —, —), (F, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Test figure 11
        
        assertEquals("D", list.remove(2));
        assertEquals("[(A), (C, F), (—, —, —, —)]", list.toStringInternal());
        
        // Out-of-bounds tests
        
        boolean outOfBounds = false;
        
        try {
            list.remove(-1);
        } catch (IndexOutOfBoundsException e) {
            outOfBounds = true;
        }
        
        assertTrue(outOfBounds);
        outOfBounds = false;
        
        try {
            list.remove(5);
        } catch (IndexOutOfBoundsException e) {
            outOfBounds = true;
        }
        assertTrue(outOfBounds);
    }
    
    /**
     * Tests the {@link DoublingList#size()} method.
     * <ol>
     *  <li>Adds to a list and checks that the size has increased by 1</li>
     *  <li>Removes from a list and checks that the size has decreased by 1</li>
     * </ol>
     */
    @Test
    @DisplayName("size()")
    public void testSize() {
        
        
        // Add to an empty list (figure 4)
        list.add(0, "A");
        assertEquals(1, list.size());
        
        // Add to the end of the list
        
        // This one also ensures that adding a new node works.
        list.add(1, "B");
        assertEquals(2, list.size());
        
        list.add(2, "C");
        assertEquals(3, list.size());
        
        // Setup figure 5 from the spec
        
        list.add(3, "D");
        list.add(4, "E");
        list.add(5, "F");
        list.add(6, "G");
        list.add(7, "F");
        list.add(8, "G");
        assertEquals(9, list.size());
        
        list.remove(5);
        assertEquals(8, list.size());
        list.remove(5);
        assertEquals(7, list.size());
        
        // Test figure 5
        
        list.add(3, "X");
        assertEquals(8, list.size());
        
        // Setup Figure 6 from spec
        
        list = new DoublingList<String>();
        
        list.add("A");
        list.add("B");
        list.add("B");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        list.add("H");
        list.add("I");
        list.add("J");
        list.add("K");
        list.add("L");
        list.add("M");
        
        assertEquals(15, list.size());
        
        list.remove(1);
        assertEquals(14, list.size());
        list.remove(1);
        assertEquals(13, list.size());
        
        // Test figure 6
        list.add(9, "X");
        assertEquals(14, list.size());
        
        // Setup figure 7 from spec
        
        list = new DoublingList<String>();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        list.add("H");
        list.add("I");
        
        assertEquals(9, list.size());
        
        // Test figure 7
        
        list.add(2, "X");
        assertEquals(10, list.size());
        
        // Setup figure 8 from spec
        
        list = new DoublingList<String>();
        list.add("B");
        list.add("B");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("E");
        list.add("E");
        assertEquals(8, list.size());
        
        list.remove(1);
        assertEquals(7, list.size());
        list.remove(1);
        assertEquals(6, list.size());
        list.remove(4);
        assertEquals(5, list.size());
        list.remove(4);
        assertEquals(4, list.size());
        
        // Test figure 8
        
        list.add(4, "F");
        assertEquals(5, list.size());
        
        list.add(5, "G");
        assertEquals(6, list.size());
        
        list.add(6, "H");
        assertEquals(7, list.size());
        
        list.add(7, "I");
        assertEquals(8, list.size());
        
        // Setup figure 9 from spec
        
        list = new DoublingList<String>();
        list.add("F");
        list.add("I");
        list.add("J");
        list.add("M");
        list.add("P");
        list.add("Q");
        list.add("R");
        assertEquals(7, list.size());
        
        // Test figure 9
        list.add("T");
        assertEquals(8, list.size());
        
        
        // Setup figure 10
        list = new DoublingList<String>();
        
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        list.add("H");
        list.add("I");
        assertEquals(9, list.size());
        
        // Test figure 10 (b)
        assertEquals("E", list.remove(4));
        assertEquals(8, list.size());
        
        // Test figure 10 (c)
        assertEquals("B", list.remove(1));
        assertEquals(7, list.size());
        
        // Setup figure 11
        
        list = new DoublingList<String>();
        
        list.add("A");
        list.add("B");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("E");
        list.add("F");
        assertEquals(8, list.size());
        list.remove(1);
        assertEquals(7, list.size());
        list.remove(1);
        assertEquals(6, list.size());
        list.remove(3);
        assertEquals(5, list.size());
        list.remove(3);
        assertEquals(4, list.size());
        
        // Test figure 11
        
        assertEquals("D", list.remove(2));
        assertEquals(3, list.size());
        
    }
    
    /**
     * Tests the {@link DoublingList#toStringInternal()} method.
     * Adds and removes various elements to/from the list and ensures that the list's structure is correctly
     * represented by the returned String.
     */
    @Test
    @DisplayName("toStringInternal()")
    public void testToStringInternal() {
        
        // Add to an empty list (figure 4)
        list.add(0, "A");
        assertEquals("[(A)]", list.toStringInternal());
        
        // Add to the end of the list
        
        // This one also ensures that adding a new node works.
        list.add(1, "B");
        assertEquals("[(A), (B, —)]", list.toStringInternal());
        
        list.add(2, "C");
        assertEquals("[(A), (B, C)]", list.toStringInternal());
        
        // Setup figure 5 from the spec
        
        list.add(3, "D");
        list.add(4, "E");
        list.add(5, "F");
        list.add(6, "G");
        list.add(7, "F");
        list.add(8, "G");
        
        list.remove(5);
        list.remove(5);
        assertEquals("[(A), (B, C), (D, E, —, —), (F, G, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Test figure 5
        
        list.add(3, "X");
        assertEquals("[(A), (B, C), (X, D, E, —), (F, G, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Setup Figure 6 from spec
        
        list = new DoublingList<String>();
        
        list.add("A");
        list.add("B");
        list.add("B");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        list.add("H");
        list.add("I");
        list.add("J");
        list.add("K");
        list.add("L");
        list.add("M");
        
        assertEquals("[(A), (B, B), (B, C, D, E), (F, G, H, I, J, K, L, M)]", list.toStringInternal());
        
        list.remove(1);
        list.remove(1);
        
        assertEquals("[(A), (—, —), (B, C, D, E), (F, G, H, I, J, K, L, M)]", list.toStringInternal());
        
        // Test figure 6
        list.add(9, "X");
        assertEquals("[(A), (B, —), (C, D, E, F), (G, H, I, X, J, K, L, M)]", list.toStringInternal());
        
        // Setup figure 7 from spec
        
        list = new DoublingList<String>();
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        list.add("H");
        list.add("I");
        
        assertEquals("[(A), (B, C), (D, E, F, G), (H, I, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Test figure 7
        
        list.add(2, "X");
        assertEquals("[(A), (B, X), (C, D, E, F), (G, H, I, —, —, —, —, —)]", list.toStringInternal());
        
        // Setup figure 8 from spec
        
        list = new DoublingList<String>();
        list.add("B");
        list.add("B");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("E");
        list.add("E");
        
        list.remove(1);
        list.remove(1);
        list.remove(4);
        list.remove(4);
        
        assertEquals("[(B), (—, —), (C, D, E, —), (—, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Test figure 8
        
        list.add(4, "F");
        assertEquals("[(B), (—, —), (C, D, E, F), (—, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        list.add(5, "G");
        assertEquals("[(B), (C, —), (D, E, F, G), (—, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        list.add(6, "H");
        assertEquals("[(B), (C, D), (E, F, G, H), (—, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        list.add(7, "I");
        assertEquals("[(B), (C, D), (E, F, G, H), (I, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Setup figure 9 from spec
        
        list = new DoublingList<String>();
        list.add("F");
        list.add("I");
        list.add("J");
        list.add("M");
        list.add("P");
        list.add("Q");
        list.add("R");
        
        assertEquals("[(F), (I, J), (M, P, Q, R)]", list.toStringInternal());
        
        // Test figure 9
        list.add("T");
        assertEquals("[(F), (I, J), (M, P, Q, R), (T, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Setup figure 10
        list = new DoublingList<String>();
        
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");
        list.add("G");
        list.add("H");
        list.add("I");
        
        assertEquals("[(A), (B, C), (D, E, F, G), (H, I, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Test figure 10 (b)
        assertEquals("E", list.remove(4));
        assertEquals("[(A), (B, C), (D, F, G, —), (H, I, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Test figure 10 (c)
        assertEquals("B", list.remove(1));
        assertEquals("[(A), (C, —), (D, F, G, —), (H, I, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Setup figure 11
        
        list = new DoublingList<String>();
        
        list.add("A");
        list.add("B");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("E");
        list.add("F");
        list.remove(1);
        list.remove(1);
        list.remove(3);
        list.remove(3);
        
        assertEquals("[(A), (—, —), (C, D, —, —), (F, —, —, —, —, —, —, —)]", list.toStringInternal());
        
        // Test figure 11
        
        assertEquals("D", list.remove(2));
        assertEquals("[(A), (C, F), (—, —, —, —)]", list.toStringInternal());
        
    }
    
    /**
     * Tests the {@link DoublingList#toStringInternal(ListIterator)} method.
     * Iterates through the list, ensuring that the cursor is correctly represented. Ensures
     * these new elements are represented in the String representation returned.
     */
    @Test
    @DisplayName("toStringInternal(ListIterator)")
    public void testToStringInternalListIterator() {
        
        ListIterator<String> iter = iteratorTest.listIterator();
        
        assertEquals("[(| A), (B, C), (D, E, F, G)]", iteratorTest.toStringInternal(iter));
        assertEquals("A", iter.next());
        assertEquals("[(A), (| B, C), (D, E, F, G)]", iteratorTest.toStringInternal(iter));
        assertEquals("B", iter.next());
        assertEquals("[(A), (B, | C), (D, E, F, G)]", iteratorTest.toStringInternal(iter));
        assertEquals("C", iter.next());
        assertEquals("[(A), (B, C), (| D, E, F, G)]", iteratorTest.toStringInternal(iter));
        assertEquals("D", iter.next());
        assertEquals("[(A), (B, C), (D, | E, F, G)]", iteratorTest.toStringInternal(iter));
        assertEquals("E", iter.next());
        assertEquals("[(A), (B, C), (D, E, | F, G)]", iteratorTest.toStringInternal(iter));
        assertEquals("F", iter.next());
        assertEquals("[(A), (B, C), (D, E, F, | G)]", iteratorTest.toStringInternal(iter));
        assertEquals("G", iter.next());
        assertEquals("[(A), (B, C), (D, E, F, G)|]", iteratorTest.toStringInternal(iter));

        iter = iteratorTest.listIterator(6);
        
        assertEquals("[(A), (B, C), (D, E, F, | G)]", iteratorTest.toStringInternal(iter));
        assertEquals("F", iter.previous());
        assertEquals("[(A), (B, C), (D, E, | F, G)]", iteratorTest.toStringInternal(iter));
        assertEquals("E", iter.previous());
        assertEquals("[(A), (B, C), (D, | E, F, G)]", iteratorTest.toStringInternal(iter));
        assertEquals("D", iter.previous());
        assertEquals("[(A), (B, C), (| D, E, F, G)]", iteratorTest.toStringInternal(iter));
        assertEquals("C", iter.previous());
        assertEquals("[(A), (B, | C), (D, E, F, G)]", iteratorTest.toStringInternal(iter));
        assertEquals("B", iter.previous());
        assertEquals("[(A), (| B, C), (D, E, F, G)]", iteratorTest.toStringInternal(iter));
        assertEquals("A", iter.previous());
        assertEquals("[(| A), (B, C), (D, E, F, G)]", iteratorTest.toStringInternal(iter));
        
        iter = iteratorTest.listIterator();
        
    }
    
    // LIST ITERATOR TESTS
    // All the listIterator() and iterator() methods return an instance of DoublingListIterator.
    // Testing that class here handles all those methods.
    
    /**
     * Tests the {@code DoublingList.DoublingListIterator.next()} and the
     * {@code DoublingList.DoublingListIterator.hasNext()} methods.
     * 
     * Iterates through the list, checking that each call to {@code next()} returns the expected value from the list.
     * Also ensures {@code hasNext()} is accurate. Checks for a {@code NoSuchElementException} when expected.
     */
    @Test
    @DisplayName("DoublingListIterator.next() and DoublingListIterator.hasNext()")
    public void testListIteratorNextAndHasNext() {
        
        ListIterator<String> iter = iteratorTest.listIterator();
        
        assertTrue(iter.hasNext());
        assertEquals("A", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("B", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("C", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("D", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("E", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("F", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("G", iter.next());
        assertFalse(iter.hasNext());
        
        boolean noSuch = false;
        
        try {
            iter.next();
        } catch(NoSuchElementException e) {
            noSuch = true;
        }
        
        assertTrue(noSuch);
        
        
    }
    
    /**
     * Tests the {@code DoublingList.DoublingListIterator.previous()} and the
     * {@code DoublingList.DoublingListIterator.hasPrevious()} methods.
     * 
     * Iterates backwards through the list, checking that each call to {@code previous()} returns the expected value from the list.
     * Also ensures {@code hasPrevious()} is accurate. Checks for a {@code NoSuchElementException} when expected.
     */
    @Test
    @DisplayName("DoublingListIterator.prevous() and DoublingListIterator.hasPrevious()")
    public void testListIteratorPreviousAndHasPrevious() {
        
        ListIterator<String> iter = iteratorTest.listIterator(6);
        
        assertTrue(iter.hasPrevious());
        assertEquals("F", iter.previous());
        assertTrue(iter.hasPrevious());
        assertEquals("E", iter.previous());
        assertTrue(iter.hasPrevious());
        assertEquals("D", iter.previous());
        assertTrue(iter.hasPrevious());
        assertEquals("C", iter.previous());
        assertTrue(iter.hasPrevious());
        assertEquals("B", iter.previous());
        assertTrue(iter.hasPrevious());
        assertEquals("A", iter.previous());
        assertFalse(iter.hasPrevious());
        
        boolean noSuch = false;
        
        try {
            iter.previous();
        } catch(NoSuchElementException e) {
            noSuch = true;
        }
        
        assertTrue(noSuch);
        
        
    }
    
    /**
     * Tests the {@code DoublingList.DoublingListIterator.nextIndex()} and the
     * {@code DoublingList.DoublingListIterator.previousIndex()} methods.
     * 
     * Iterates through the list, checking that each call to {@code nextIndex()} returns the expected index.
     * Also ensures {@code previousIndex()} is accurate.
     */
    @Test
    @DisplayName("DoublingListIterator.nextIndex() and DoublingListIterator.previousIndex()")
    public void testListIteratorNextIndexAndPreviousIndex() {
        
        ListIterator<String> iter = iteratorTest.listIterator();
        
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.nextIndex());
        iter.next();
        
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.nextIndex());
        iter.next();
        
        assertEquals(1, iter.previousIndex());
        assertEquals(2, iter.nextIndex());
        iter.next();
        
        assertEquals(2, iter.previousIndex());
        assertEquals(3, iter.nextIndex());
        iter.next();
        
        assertEquals(3, iter.previousIndex());
        assertEquals(4, iter.nextIndex());
        iter.next();
        
        assertEquals(4, iter.previousIndex());
        assertEquals(5, iter.nextIndex());
        iter.next();
        
        assertEquals(5, iter.previousIndex());
        assertEquals(6, iter.nextIndex());
        
    }
    
    
    /**
     * Tests the {@code DoublingList.DoublingListIterator.add(E)} method.
     * 
     * Adds items to the list, ensuring that {@code previous()} returns them.
     * Tries adding a {@code null} value.
     */
    @Test
    @DisplayName("DoublingListIterator.add()")
    public void testListIteratorAdd() {

        
        ListIterator<String> iter = iteratorTest.listIterator();
        
        iter.add("X");
        assertEquals("X", iter.previous());
        iter.next();
        
        iter.next();
        
        iter.add("Z");
        assertEquals("Z", iter.previous());
        iter.previous();
        
        iter.add("U");
        assertEquals("U", iter.previous());
        
        // NullPointer test
        
        boolean nullPointer = false;
        
        try {
            list.add(null);
        } catch (NullPointerException e) {
            nullPointer = true;
        }
        
        assertTrue(nullPointer);
    }
    
    /**
     * Tests the {@code DoublingList.DoublingListIterator.remove()} method.
     * 
     * Removes items from the list, ensuring that the correct items were removed based on
     * which method was called first. Also tries to call {@code remove()} without calling a required
     * method before.
     */
    @Test
    @DisplayName("DoublingListIterator.remove()")
    public void testListIteratorRemove() {
        ListIterator<String> iter = iteratorTest.listIterator();
        iter.next();
        iter.remove();
        assertFalse(iter.hasPrevious());
        
        iter.next();
        iter.next();
        iter.previous();
        iter.remove();
        assertNotEquals("C", iter.next());
        
        iter.remove();
        assertNotEquals("D", iter.previous());
        
        // Edge Cases
        
        iter = iteratorTest.listIterator();
        
        boolean flag = false;
        
        try {
            iter.remove();
        } catch(IllegalStateException e) {
            flag = true;
        }
        
        assertTrue(flag);
        
        iter.next();
        iter.add("X");
        
        try {
            iter.remove();
        } catch(IllegalStateException e) {
            flag = true;
        }
        
        assertTrue(flag);
        
        iter.previous();
        iter.remove();
        
        try {
            iter.remove();
        } catch(IllegalStateException e) {
            flag = true;
        }
        
        assertTrue(flag);
    }
    
    /**
     * Tests the {@code DoublingList.DoublingListIterator.set(E)} method.
     * 
     * Invokes {@code set()} after calling {@code next()} and {@code previous()}, ensuring
     * the element is set in the correct place. Also tries to call {@code set(E)} without calling a required
     * method before.
     */
    @Test
    @DisplayName("DoublingListIterator.set(E)")
    public void testListIteratorSet() { 
        
        ListIterator<String> iter = iteratorTest.listIterator();
        iter.next();
        iter.set("Z");
        assertEquals("Z", iter.previous());
        
        iter.next();
        iter.next();
        iter.previous();
        iter.set("Y");
        assertEquals("Y", iter.next());
        
        iter.set("X");
        iter.set("W");
        assertEquals("W", iter.previous());
        
        // Edge Cases
        
        iter = iteratorTest.listIterator();
        
        boolean flag = false;
        
        try {
            iter.set("S");
        } catch(IllegalStateException e) {
            flag = true;
        }
        
        assertTrue(flag);
        
        iter.next();
        iter.add("X");
        
        try {
            iter.set("S");
        } catch(IllegalStateException e) {
            flag = true;
        }
        
        assertTrue(flag);
        
        iter.previous();
        iter.remove();
        
        try {
            iter.set("S");
        } catch(IllegalStateException e) {
            flag = true;
        }
        
        assertTrue(flag);
        
    }
}