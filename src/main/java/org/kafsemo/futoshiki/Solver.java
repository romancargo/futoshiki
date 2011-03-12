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

import java.math.BigInteger;
import java.util.logging.Logger;


/**
 * Recursive solver for Futoshiki puzzles.
 * 
 * @author Joseph Walton
 */
public class Solver
{
    private static final Logger log = Logger.getLogger(Solver.class.getName());
    private static final CellPos[] CELLPOS_ARRAY = {};
    private static final BigInteger FIVE_BY_FIVE_COMBINATIONS =
        new BigInteger("298023223876953125");
    
    private final SolutionTarget target;
    
    public Solver(SolutionTarget target)
    {
        this.target = target;
    }
    
    public void solve(Futoshiki f)
    {
        solve(f, new Possibilities(f.getLength()));
    }
    
    public void solve(Futoshiki f, Possibilities poss)
    {
        CellPos[] blanks = f.blankCells().toArray(CELLPOS_ARRAY);
        poss.use(f);
        BigInteger count = poss.size();
        log.fine("Solution possibilities: " + poss.size());
        if (count.compareTo(FIVE_BY_FIVE_COMBINATIONS) > 0) {
            log.fine("This may take an extremely long time");
        }
        if (!target.remainingPossibilities(count)) {
            return;
        }
        if (solve(f, blanks, 0, poss, BigInteger.ZERO)) {
            target.remainingPossibilities(BigInteger.ZERO);
        }
    }
    
    /**
     * Accept a puzzle state and, if it is valid and if there are still blank
     * squares, try every number. Recurse for all attempts. If there are no
     * blanks remaining then print what must be a solution.
     * 
     * @param f
     * @param blank
     * @param nb the index of the next remaining blank
     */
    private boolean solve(Futoshiki f, CellPos[] blank, int nb,
            Possibilities poss, BigInteger possibilitiesAfter)
    {
        if (!f.isValid()) {
            return true;
        }
        
        if (nb >= blank.length) {
            return target.solution(f);
        }
        
        blank = moveBlankWithLeastPossibilitiesIntoPlace(blank, nb, poss);
        
        CellPos p = blank[nb];

        // Generate a collection of possibilities
        // Calculate the remaining number if each choice was made
        // Before each recursion, report back the total
        
        int[] possibleValues = new int[f.getLength()];
        BigInteger[] possibilitiesForValue = new BigInteger[f.getLength()];
        Possibilities[] possibilities = new Possibilities[f.getLength()];
        
        int i = 0;
        
        for (int v = 1; v <= f.getLength(); v++) {
            if (poss.isPossible(p.column, p.row, v)) {
                possibleValues[i] = v;
                Possibilities ps = poss.clone();
                ps.use(p.column, p.row, v);
                possibilities[i] = ps;
                possibilitiesForValue[i] = ps.size();
                i++;
            }
        }

        BigInteger remainingPossibilities = sum(possibilitiesForValue, i);
        remainingPossibilities = remainingPossibilities.add(possibilitiesAfter);
        
        for (int j = 0; j < i; j++) {
            if (!target.remainingPossibilities(remainingPossibilities)) {
                return false;
            }
            int v = possibleValues[j];
            f.set(p.column, p.row, v);
            remainingPossibilities =
                remainingPossibilities.subtract(possibilitiesForValue[j]);
            boolean more = solve(f.clone(), blank, nb + 1, possibilities[j],
                                    remainingPossibilities);
            if (!more) {
                return false;
            }
        }
        
        return true;
    }

    private CellPos[] moveBlankWithLeastPossibilitiesIntoPlace(CellPos[] blanks,
            int p, Possibilities poss)
    {
        int fewestIdx = -1;
        
        for (int i = p; i < blanks.length; i++) {
            if (fewestIdx >= 0) {
                int p1 = poss.possibleCount(blanks[fewestIdx]);
                int p2 = poss.possibleCount(blanks[i]);
                
                if (p2 < p1) {
                    fewestIdx = i;
                }
                
                
            } else {
                fewestIdx = i;
            }
        }
        
        if (fewestIdx >= 0 && fewestIdx != p) {
            // Only necessary with multiple threads
//            blanks = blanks.clone();
            CellPos cp = blanks[p];
            blanks[p] = blanks[fewestIdx];
            blanks[fewestIdx] = cp;
        }
        
        return blanks;
    }
    
    private static BigInteger sum(BigInteger[] a, int maxIndex)
    {
        BigInteger total = BigInteger.ZERO;
        
        for (int i = 0; i < maxIndex; i++) {
            total = total.add(a[i]);
        }
        
        return total;
    }
    
    /**
     * A callback interface to receive complete puzzle solutions.
     */
    public interface SolutionTarget
    {
        /**
         * @param f
         * @return whether or not more solutions are required
         */
        boolean solution(Futoshiki f);

        boolean remainingPossibilities(BigInteger count);
    }
}
