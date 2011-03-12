package org.kafsemo.futoshiki.sample;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kafsemo.futoshiki.Futoshiki;
import org.kafsemo.futoshiki.FutoshikiPrinter;
import org.kafsemo.futoshiki.Possibilities;
import org.kafsemo.futoshiki.Solver;
import org.kafsemo.futoshiki.SudokuPossibilities;

public class NineSudokuSample
{
    public static void main(String[] args) throws IOException
    {
        Logger.getLogger(Solver.class.getName()).setLevel(Level.ALL);
        
        InputStream in = NineSudokuSample.class.getResourceAsStream("sample-9x9-sudoku.txt");
        
        Futoshiki f = NineSample.fromStream(in);
        System.out.println(FutoshikiPrinter.toString(f));

        Possibilities p = new SudokuPossibilities();
        new Solver(new SolverSample.PrintingSolutionTarget()).solve(f, p);
    }
}
