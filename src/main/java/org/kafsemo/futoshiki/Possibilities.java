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
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;

/**
 * Moves that are still possible given the current numbers.
 * 
 * @author Joseph Walton
 */
public class Possibilities extends Grid
{
    protected final BitSet possibilities;

    private Collection<GtRule> rules = Collections.emptyList();
    
    public Possibilities(int length)
    {
        super(length);
        this.possibilities = new BitSet(length * length * length);
    }
    
    protected Possibilities(int length, BitSet p)
    {
        super(length);
        this.possibilities = p;
    }

    int bit(int column, int row, int value)
    {
        return (((column - 1) * length) + row - 1) * length + value - 1;
    }
    
    protected void setImpossible(int column, int row, int value)
    {
        possibilities.set(bit(column, row, value));
    }
    
    public void use(int column, int row, int value)
    {
        if (value < 1 || value > length)
            throw new IllegalArgumentException("Bad cell value " + value);
        
        for (int c = 1; c <= length; c++) {
            if (c != column) {
                setImpossible(c, row, value);
            }
        }
        
        for (int r = 1; r <= length; r++) {
            if (r != row) {
                setImpossible(column, r, value);
            }
        }
        
        for (int v = 1; v <= length; v++) {
            if (v != value) {
                setImpossible(column, row, v);
            }
        }
        
        for (GtRule r : this.rules) {
            int greatestMoreThan = minPossible(r.getLesserColumn(), r.getLesserRow());
            for (int v = 1; v <=  greatestMoreThan; v++) {
                setImpossible(r.getGreaterColumn(), r.getGreaterRow(), v);
            }
            
            int leastLessThan = maxPossible(r.getGreaterColumn(), r.getGreaterRow());
            for (int v = leastLessThan; v <= length; v++) {
                setImpossible(r.getLesserColumn(), r.getLesserRow(), v);
            }
        }
    }
    
    int minPossible(int column, int row)
    {
        for (int v = 1; v < length; v++) {
            if (isPossible(column, row, v)) {
                return v;
            }
        }
        
        return 0;
    }
    
    int maxPossible(int column, int row)
    {
        for (int v = length; v >= 1; v--) {
            if (isPossible(column, row, v)) {
                return v;
            }
        }
        
        return 0;
    }
    
    public void use(Futoshiki f)
    {
        for (int r = 1; r <= length; r++) {
            for (int c = 1; c <= length; c++) {
                int v = f.get(c, r);
                if (v != 0) {
                    use(c, r, v);
                }
            }
        }
        
        this.rules = new ArrayList<GtRule>();
        
        for (GtRule r : f.getRules()) {
            this.rules.add(r);
            
            int greatestMoreThan = minPossible(r.getLesserColumn(), r.getLesserRow());
            for (int v = 1; v <=  greatestMoreThan; v++) {
                setImpossible(r.getGreaterColumn(), r.getGreaterRow(), v);
            }
            
            int leastLessThan = maxPossible(r.getGreaterColumn(), r.getGreaterRow());
            for (int v = leastLessThan; v <= length; v++) {
                setImpossible(r.getLesserColumn(), r.getLesserRow(), v);
            }
        }
    }
    
    public boolean isPossible(int column, int row, int value)
    {
        return !possibilities.get(bit(column, row, value));
    }
    
    public Possibilities clone()
    {
        return new Possibilities(length, (BitSet) possibilities.clone());
    }

    public BigInteger size()
    {
        BigInteger total = BigInteger.ONE;
        
        for (int r = 1; r <= length; r++) {
            for (int c = 1; c <= length; c++) {
                int available = possibleCount(c, r);
                
                total = total.multiply(BigInteger.valueOf(available));
            }
        }

        return total;
    }
    
    public int possibleCount(int column, int row)
    {
        int available = 0;
        
        for (int v = 1; v <= length; v++) {
            if (isPossible(column, row, v)) {
                available++;
            }
        }
        
        return available;
    }
    
    public int possibleCount(CellPos cell)
    {
        return possibleCount(cell.column, cell.row);
    }
}
