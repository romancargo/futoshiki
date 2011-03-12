package org.kafsemo.futoshiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestSudokuPossibilities
{
    @Test
    public void sameNumberNotPossibileInSameSection()
    {
        SudokuPossibilities sp = new SudokuPossibilities();
        sp.use(1, 1, 1);
        
        assertTrue(sp.isPossible(1, 1, 1));
        assertFalse(sp.isPossible(2, 2, 1));
        assertFalse(sp.isPossible(3, 3, 1));
        
        assertTrue(sp.isPossible(4, 2, 1));
        assertTrue(sp.isPossible(4, 4, 1));
    }
    
    @Test
    public void cloneReturnsSameType()
    {
        SudokuPossibilities sp = new SudokuPossibilities();
        assertEquals(SudokuPossibilities.class,
                sp.clone().getClass());
    }
}
