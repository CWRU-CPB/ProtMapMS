/*

Copyright (C) Case Western Reserve University, 2018. All rights reserved. Please
read the LICENSE file carefully before using this source code.
 

 CASE WESTERN RESERVE UNIVERSITY EXPRESSLY DISCLAIMS ANY
 AND ALL WARRANTIES CONCERNING THIS SOURCE CODE AND DOCUMENTATION,
 INCLUDING ANY WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 FOR ANY PARTICULAR PURPOSE, AND WARRANTIES OF PERFORMANCE,
 AND ANY WARRANTY THAT MIGHT OTHERWISE ARISE FROM COURSE OF
 DEALING OR USAGE OF TRADE. NO WARRANTY IS EITHER EXPRESS OR
 IMPLIED WITH RESPECT TO THE USE OF THE SOFTWARE OR
 DOCUMENTATION.
 
Under no circumstances shall University be liable for incidental, special,
indirect, direct or consequential damages or loss of profits, interruption
of business, or related expenses which may arise from use of source code or 
documentation, including but not limited to those resulting from defects in
source code and/or documentation, or loss or inaccuracy of data of any kind.

*/
package edu.cwru.protmapms.math;

/**
 * Aligns two arrays of values, returning either the aligned values, or
 * pairs of indices for each aligned value.
 * 
 * @author Sean Maxwell
 */
public class SortedArraysAligner {

    /**
     * Aligns values X[i] to a best value Y[j] using three criteria:
     * <ol>
     * <li>abs(X[i]-Y[j]) is less than maxDifference</li>
     * <li>X[i] is closer to Y[j] than to Y[j+1]</li>
     * <li>The dependent value Xd[i] is greater than Xd[i+1]</li>
     * </ol>
     * 
     * The values stored in X and Y must be in ascending order.
     * 
     * @param X Values to align to Y
     * @param Xd Dependent variable corresponding to each X[i]
     * @param Y Values to be aligned to
     * @param maxDifference Maximum difference between X and Y that is allowed
     * @return The aligned arrays
     * @throws Exception If either X or Y is not in ascending order
     */
    public static ArrayAlignment alignClosestDependent(double[] X, double[] Xd, double[] Y, double maxDifference) throws Exception {
        /* Initialize an array of matches to -1, which will be filled in with an
         * index of array Y if a match is made*/
        int[] matches = new int[X.length];
        for(int i=0;i<matches.length;i++) {
            matches[i] = -1;
        }
        
        int lastMatch = 0;
        int bestCandidate;
        double d1,d3;
        for(int j=0;j<Y.length;j++) {
            bestCandidate = -1;
            /* Make sure the array is sorted */
            if(j<Y.length-1 && Y[j] > Y[j+1])
                throw new Exception("Argument array Y is not sorted");
            
            for(int i=lastMatch;i<X.length;i++) {
                /* Make sure argument arrays are sorted */
                if(i<X.length-1 && X[i] > X[i+1])
                    throw new Exception("Argument array X is not sorted");
                
                /* d1 is how close the current theoretcial m/z ion Y[j] is to
                 * the experimental m/z ion X[i] */
                d1 = Math.abs(Y[j]-X[i]);
                
                /* Last match was made far away from this value, so fast-forward
                 * until we are within maxDifference */
                if(Y[j]-X[i] > 0 && d1 > maxDifference) continue;
                
                /* We have exceeded the current value to match by more than
                 * maxDifference, so stop searching */
                else if(Y[j]-X[i] < 0 && d1 > maxDifference) break;
                
                /* d3 is the distance between the NEXT theoretical ion Y[j+1]
                 * and the current experimental ion X[i]. We compute this 
                 * because the arrays are sorted, so if the experimental ion
                 * is closer to the next theoretical ion, a better match for the
                 * current theoretical ion cannot be made.
                 *
                 * If the current theoretical ion is the last, then d3 is Inf */
                if(j == Y.length-1) {
                    d3 = Double.POSITIVE_INFINITY;
                }
                else {
                    d3 = Math.abs(Y[j+1]-X[i]);
                }
                
                /* We consider everything in the window, up to the values that
                 * are closer to the next X ion */
                if(d1 <= d3) {
                    /* Of the values that satisfy the proximity requirements, 
                     * select the candidate with the greatest dependent variable
                     * value.
                     *
                     * This selects the experimental ion with the greatest
                     * intensity that matches the theoretical ion */
                    if(bestCandidate == -1 || Xd[i] > Xd[bestCandidate]) {
                        bestCandidate = i;
                    }
                }
            }
            
            /* Assign match */
            if(bestCandidate != -1) {
                lastMatch = bestCandidate+1;
                matches[bestCandidate] = j;
            }
        }
             
        /* Extract the alignment for those values in Y and X that were 
         * matched. We want to "snap" the values of Y onto X where a match was
         * made, which is why below we add the value of X[i] to both alignments.
         * */
        ArrayAlignment alignment = new ArrayAlignment();
        for(int i=0;i<matches.length;i++) {
            if(matches[i] != -1) 
                //alignment.add(Y[matches[i]], 1.0, X[i], Xd[i]);
                alignment.add(X[i], 1.0, X[i], Xd[i]);
            else
                alignment.add(0.0, 0.0, X[i], Xd[i]);
        }
        return alignment;
    }
    
    /**
     * Identify elements Y[j] such that for some i, abs(X[i]-Y[j]) is less than
     * or equal to a threshold value.
     * @param X Values to match
     * @param Y Value to match
     * @param maxDistance Maximum difference to be considered while matching
     * @return Indices i,j of matching pairs between X and Y
     * @throws Exception if X or Y is not in ascending order
     */
    public static int[][] getInRangePairs(double[] X, double[] Y, double maxDistance) throws Exception {
        /* Initialize an array matches to -1, which will be filled in for
         * each match to value X[j] := matches[j]=Y[i] if a match is made */
        int[] matches = new int[X.length];
        for(int i=0;i<matches.length;i++) {
            matches[i] = -1;
        }
        
        int nMatches=0;
        double d1;
        for(int j=0;j<Y.length;j++) {
            /* Make sure the array is sorted */
            if(j<Y.length-1 && Y[j] > Y[j+1])
                throw new Exception("Argument array Y is not sorted");
            
            for(int i=0;i<X.length;i++) {
                /* Make sure argument arrays are sorted */
                if(i<X.length-1 && X[i] > X[i+1])
                    throw new Exception("Argument array X is not sorted");
                
                d1 = Math.abs(Y[j]-X[i]);
                               
                /* We have exceeded the current value to match by more than
                 * the window, so stop searching */
                if(Y[j]-X[i] < 0 && d1 > maxDistance) break;
                
                /* Some of these will overwite previous matches, but we
                 * just need to know something matched */
                else if(d1 < maxDistance || Math.abs(d1-maxDistance) < 0.000000001) matches[i]=j;
            }
        }
        
        for(int i=0;i<matches.length;i++) {
            if(matches[i] != -1) nMatches++;
        }
             
        /* Extract the alignment for those values in Y and X that were 
         * matched */
        int[][] pairs = new int[2][nMatches];
        int j=0;
        for(int i=0;i<matches.length;i++) {
            if(matches[i] != -1) {
                pairs[0][j]=i;
                pairs[1][j]=matches[i];
                j++;
            }
        }
        return pairs;
    }
    
    /**
     * Utility method to print matched pairs.
     * @param pairs Pairs computed by #getInRangePairs
     * @param x Data X passed to #getInRangePairs
     * @param y Data Y passed to #getInRangePairs
     */
    public static void printPairs(int[][] pairs, double[] x, double[] y) {
        System.out.printf("Pair\tX\tY\n");
        for(int i=0;i<pairs[0].length;i++) {
            System.out.printf("%d,%d\t%.2f\t%.2f\n",pairs[0][i],pairs[1][i],x[pairs[0][i]],y[pairs[1][i]]);
        }
    }
    
}
