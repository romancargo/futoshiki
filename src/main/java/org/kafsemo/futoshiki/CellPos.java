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

/**
 * The position of a single numeric cell on a puzzle.
 * 
 * @author Joseph Walton
 */
public final class CellPos
{
    final int column, row;
    
    public CellPos(int cellColumn, int cellRow)
    {
        this.column = cellColumn;
        this.row = cellRow;
    }

    public String toString()
    {
        return "(" + column + "," + row + ")";
    }
    
    @Override
    public int hashCode()
    {
        return 31 * column + row;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CellPos))
            return false;
        final CellPos other = (CellPos) obj;
        if (column != other.column)
            return false;
        if (row != other.row)
            return false;
        return true;
    }
}
