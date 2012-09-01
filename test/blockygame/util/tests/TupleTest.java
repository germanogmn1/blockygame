package blockygame.util.tests;

import static org.junit.Assert.*;

import org.junit.*;

import blockygame.util.Tuple;;

public class TupleTest {
	
	@Test public void testToString() {
		assertEquals("Tuple(5, 5)", new Tuple(5, 5).toString());
	}
	
	@Test public void testEquals() {
		Tuple t = new Tuple(10, 20);
		assertTrue(t.equals(new Tuple(10, 20)));
		assertFalse(t.equals(new Tuple(5, 20)));
		assertFalse(t.equals(new Tuple(10, 5)));
	}
	
	@Test public void testAdd() {
		Tuple t1 = new Tuple(5, 8);
		Tuple t2 = new Tuple(3, -10);
		Tuple t3 = t1.add(t2);
		
		assertNotNull(t3);
		assertNotSame(t1, t3);
		assertNotSame(t2, t3);
		assertEquals(8, t3.x);
		assertEquals(-2, t3.y);
	}
	
	@Test public void testAddInt() {
		Tuple t1 = new Tuple(5, 8);
		Tuple t2 = t1.add(-8, 4);
		
		assertNotNull(t2);
		assertEquals(-3, t2.x);
		assertEquals(12, t2.y);
	}
	
	@Test public void testSubtract() {
		Tuple t1 = new Tuple(5, 8);
		Tuple t2 = new Tuple(3, -10);
		Tuple t3 = t1.subtract(t2);
		
		assertNotNull(t3);
		assertNotSame(t1, t3);
		assertNotSame(t2, t3);
		assertEquals(2, t3.x);
		assertEquals(18, t3.y);
	}
	
	@Test public void testSubtractInt() {
		Tuple t1 = new Tuple(5, 8);
		Tuple t2 = t1.subtract(-8, 4);
		
		assertNotNull(t2);
		assertEquals(13, t2.x);
		assertEquals(4, t2.y);
	}

}
