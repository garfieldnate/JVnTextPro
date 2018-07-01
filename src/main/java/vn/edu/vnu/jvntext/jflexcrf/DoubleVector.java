/*
 Copyright (C) 2010 by
 *
 * 	Cam-Tu Nguyen	ncamtu@ecei.tohoku.ac.jp ncamtu@gmail.com
 *  Xuan-Hieu Phan  pxhieu@gmail.com

 *  College of Technology, Vietnamese University, Hanoi
 *
 * 	Graduate School of Information Sciences
 * 	Tohoku University
 *
 *  JVnTextPro is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JVnTextPro is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  JVnTextPro); if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package vn.edu.vnu.jvntext.jflexcrf;

public class DoubleVector {

    /**
     * The vect.
     */
    public double[] vect = null;

    /**
     * The len.
     */
    public int len = 0;

    /**
     * Instantiates a new double vector.
     */
    public DoubleVector() {
        len = 0;
        vect = null;
    }

    /**
     * Instantiates a new double vector.
     *
     * @param len the len
     */
    public DoubleVector(int len) {
        this.len = len;
        vect = new double[len];
    }

    /**
     * Instantiates a new double vector.
     *
     * @param len  the len
     * @param vect the vect
     */
    public DoubleVector(int len, double[] vect) {
        this.len = len;
        this.vect = new double[len];
        System.arraycopy(vect, 0, this.vect, 0, len);
    }

    /**
     * Instantiates a new double vector.
     *
     * @param dv the dv
     */
    public DoubleVector(DoubleVector dv) {
        len = dv.len;
        vect = new double[len];
        System.arraycopy(dv.vect, 0, vect, 0, len);
    }

    /**
     * Size.
     *
     * @return the int
     */
    public int size() {
        return len;
    }

    /**
     * Assign.
     *
     * @param val the val
     */
    public void assign(double val) {
        for (int i = 0; i < len; i++) {
            vect[i] = val;
        }
    }

    /**
     * Assign.
     *
     * @param dv the dv
     */
    public void assign(DoubleVector dv) {
        System.arraycopy(dv.vect, 0, vect, 0, len);
    }

    /**
     * Sum.
     *
     * @return the double
     */
    public double sum() {
        double res = 0.0;
        for (int i = 0; i < len; i++) {
            res += vect[i];
        }
        return res;
    }

    /**
     * Comp mult.
     *
     * @param val the val
     */
    public void compMult(double val) {
        for (int i = 0; i < len; i++) {
            vect[i] *= val;
        }
    }

    /**
     * Comp mult.
     *
     * @param dv the dv
     */
    public void compMult(DoubleVector dv) {
        for (int i = 0; i < len; i++) {
            vect[i] *= dv.vect[i];
        }
    }

}
