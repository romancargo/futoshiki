/*
 *  A Futoshiki puzzle editor and solver.
 *  Copyright © 2007, 2011 Joseph Walton
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kafsemo.futoshiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.TreeSet;

import org.junit.Test;
import org.kafsemo.futoshiki.Futoshiki;
import org.kafsemo.futoshiki.Possibilities;

public class TestPossibilities
{
    @Test
    public void emptyByDefault()
    {
        Possibilities p = new Possibilities(1);
        assertTrue(p.isPossible(1, 1, 1));
    }
    
    @Test
    public void stillPossibleWhenUsed()
    {
        Possibilities p = new Possibilities(1);
        p.use(1, 1, 1);
        assertTrue(p.isPossible(1, 1, 1));
    }
    
    @Test
    public void usedNumberNotAvailableInSameRowOrColumn()
    {
        Possibilities p = new Possibilities(2);
        p.use(1, 1, 1);
        assertTrue(p.isPossible(1, 1, 1));
        assertFalse(p.isPossible(1, 2, 1));
        assertFalse(p.isPossible(2, 1, 1));
        assertTrue(p.isPossible(2, 2, 1));
    }
    
    @Test
    public void usedSquareNotAvailable()
    {
        Possibilities p = new Possibilities(2);
        p.use(1, 1, 1);
        assertFalse(p.isPossible(1, 1, 2));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void mayNotUseZeroValue()
    {
        Possibilities p = new Possibilities(1);
        p.use(1, 1, 0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void mayNotUseInvalidValue()
    {
        Possibilities p = new Possibilities(1);
        p.use(1, 1, 2);
    }
    
    @Test
    public void totalNumberOfPossibilitiesKnown()
    {
        Possibilities p;
        
        p = new Possibilities(1);
        assertEquals(BigInteger.ONE, p.size());
        
        p = new Possibilities(2);
        assertEquals(BigInteger.valueOf(16), p.size());
        
        p = new Possibilities(3);
        assertEquals(BigInteger.valueOf(19683), p.size());
    }

    @Test
    public void totalPossibilitiesIsConstantForOneByOne()
    {
        Possibilities p;
        
        p = new Possibilities(1);
        assertEquals(BigInteger.ONE, p.size());
        p.use(1, 1, 1);
        assertEquals(BigInteger.ONE, p.size());
    }
    
    @Test
    public void totalNumberReducedWhenNumbersAreFixed()
    {
        Possibilities p;
        
        p = new Possibilities(2);
        p.use(1, 1, 1);
        assertFalse(p.isPossible(1, 1, 2));
        assertFalse(p.isPossible(2, 1, 1));
        assertTrue(p.isPossible(2, 1, 2));
        
        assertEquals(BigInteger.valueOf(2), p.size());
        p.use(2, 2, 1);
        assertEquals(BigInteger.ONE, p.size());
        
        p = new Possibilities(3);
        p.use(1, 1, 1);
        assertEquals(BigInteger.valueOf(16 * 81), p.size());
        p.use(2, 1, 2);
        assertEquals(BigInteger.valueOf(16 * 9), p.size());
        p.use(3, 1, 3);
        assertEquals(BigInteger.valueOf(64), p.size());
    }
    
    @Test
    public void useExistingPuzzleToReducePossibilities()
    {
        Possibilities p = new Possibilities(2);
        
        Futoshiki f = new Futoshiki(2);
        
        p.use(f);
        assertEquals(BigInteger.valueOf(16), p.size());
        
        f.set(1, 1, 1);
        p.use(f);
        assertEquals(BigInteger.valueOf(2), p.size());
    }
    
    @Test
    public void useExistingPuzzleRulesToReducePossibilities()
    {
        Possibilities p = new Possibilities(2);
        
        Futoshiki f = new Futoshiki(2);

        f.addGtRule(1, 1, 2, 1);
        p.use(f);
        
        assertFalse(p.isPossible(1, 1, 1));
        assertTrue(p.isPossible(1, 1, 2));
        
        assertTrue(p.isPossible(2, 1, 1));
        assertFalse(p.isPossible(2, 1, 2));
    }
    
    @Test
    public void useExistingPuzzleRulesWithNumbersToReducePossibilities()
    {
        Possibilities p = new Possibilities(3);
        
        Futoshiki f = new Futoshiki(3);

        f.set(1, 1, 2);
        f.addGtRule(2, 1, 1, 1);
        p.use(f);
        
        assertFalse(p.isPossible(2, 1, 1));
        assertFalse(p.isPossible(2, 1, 2));
        assertTrue(p.isPossible(2, 1, 3));
    }
    
    @Test
    public void getPossibilityCountForSpecificCell()
    {
        Possibilities p;
        
        p = new Possibilities(1);
        assertEquals(1, p.possibleCount(1, 1));
        
        p = new Possibilities(9);
        assertEquals(9, p.possibleCount(1, 1));
    }
    
    @Test
    public void specificCellPossibilityCountIsReduced()
    {
        Possibilities p;
        
        p = new Possibilities(2);
        assertEquals(2, p.possibleCount(1, 1));
        
        p.use(1, 1, 1);
        assertEquals(1, p.possibleCount(1, 1));
        assertEquals(1, p.possibleCount(2, 1));
        
        p = new Possibilities(9);
        p.use(1, 1, 1);
        assertEquals(8, p.possibleCount(2, 1));
    }
    
    @Test
    public void usingCellsEliminatesPossibilitiesUsingRules()
    {
        Possibilities p = new Possibilities(4);
        Futoshiki f = new Futoshiki(4);

        f.addGtRule(2, 1, 1, 1);
        p.use(f);
        
        assertFalse(p.isPossible(1, 1, 4));
        assertTrue(p.isPossible(1, 1, 3));
        assertTrue(p.isPossible(1, 1, 2));
        assertTrue(p.isPossible(1, 1, 1));
        
        p.use(2, 1, 2);
        assertFalse(p.isPossible(1, 1, 3));
        assertFalse(p.isPossible(1, 1, 2));
        assertTrue(p.isPossible(1, 1, 1));
    }
    
    public static String toString(Possibilities p)
    {
        StringBuilder sb = new StringBuilder();
        
        for (int r = 1; r <= p.getLength(); r++) {
            for (int c = 1; c <= p.getLength(); c++) {
                TreeSet<Integer> ts = new TreeSet<Integer>();
                for (int v = 1; v <= p.getLength(); v++) {
                    if (p.isPossible(c, r, v)) {
                        ts.add(v);
                    }
                }
                
                sb.append(ts);
                sb.append(" | ");
            }
            sb.append("\n");
        }
        sb.append("--\n");
        
        return sb.toString();
    }
}
