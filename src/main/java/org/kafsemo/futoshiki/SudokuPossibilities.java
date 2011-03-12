package org.kafsemo.futoshiki;

import java.util.BitSet;

public class SudokuPossibilities extends Possibilities
{
    public SudokuPossibilities()
    {
        super(9);
    }
    
    private SudokuPossibilities(BitSet possibilities)
    {
        super(9, possibilities);
    }
    
    @Override
    public void use(int column, int row, int value)
    {
        super.use(column, row, value);

        int sc = 1 + ((column - 1) / 3) * 3;
        int sr = 1 + ((row - 1) / 3) * 3;
        
        for (int c = sc; c < sc + 3; c++) {
            for (int r = sr; r < sr + 3; r++) {
                if (c != column || r != row) {
                    setImpossible(c, r, value);
                }
            }
        }
    }
    
    @Override
    public Possibilities clone()
    {
        return new SudokuPossibilities((BitSet) possibilities.clone());
    }
}
