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
package edu.cwru.protmapms.modifications;

import java.util.ArrayList;
import java.util.List;

import edu.cwru.protmapms.math.Combinatorics;


/**
 * An enumerator to create all possible combinations of objects of type T that
 * can be created by selecting one object from each list.
 * 
 * @author Sean Maxwell
 */
public class ListOfListsEnumerator<T> {
    private final List<List<T>> stacks;
    private final int N;
    private final int K;
    private int[] indices;
    private int[] pointers;
    private int k;
    private Combinatorics comb;
        
    private int[] advancePointers() {
        int i = pointers.length-1;
        while(i > -1) {
            if(pointers[i] == stacks.get(indices[i]).size()-1) {
                pointers[i] = 0;
                i--;
            }
            else {
                pointers[i]++;
                break;
            }
        }
        
        if(i == -1) {
            return new int[0];
        }
        return pointers;
    }
    
    private List<T> getSubset() {
        List<T> subset = new ArrayList<>();
        for(int i=0;i<indices.length;i++) {
            subset.add(stacks.get(indices[i]).get(pointers[i]));
        }
        return subset;
    }
    
    public ListOfListsEnumerator(List<List<T>> stacks, int K) {
        /* The number of lists to enumerate is our N */
        this.N = stacks.size();
        
        /* The initial size for k is 0 if there are no lists to enumerate,
         * and 1 otherwise */
        this.k = stacks.isEmpty() ? 0 : 1;
        
        /* The maximum K (the largest subset size to consider when enumerating
         * sets of lists) cannot be larger than N (the number of lists) so
         * limit it's value to N here. The initial value of tempK is set to
         * -1 if there are no stacks, so that the first call to getNext()
         * has k > K (0 > -1) to return null (meaning nothing left to enumerate)
         */
        int tempK = stacks.isEmpty() ? -1 : K;
        if(tempK > N) {
            this.K = N;
        }
        else {
            this.K = tempK;
        }
        
        this.stacks = stacks;
        this.comb = new Combinatorics(N,k);
        this.indices = comb.getCurrent();
        this.pointers = new int[k];
    }
    
    public List<T> getNext() {        
        /* If hit end of pointers, advance to next combination of stacks and
         * reset stack pointers. If there are no more combinations of stacks
         * at this k, indices will be set to length 0 here and the block below
         * will be entered to advance the stack indices to the next larger
         * value fo k */
        if(pointers.length == 0) {
            pointers = new int[k];
            indices = comb.getNext();
        }
        
        /* If there are no more combinations of stacks possible for the current
         * k, then increment k to the next larger subgroup size, re-initialize
         * the stack indices to the first combination at the new size k and 
         * re-initialize the stack pointers to all 0s of length k */
        if(indices.length == 0) {
            k++;
            comb = new Combinatorics(N,k);
            indices = comb.getCurrent();
            pointers = new int[k];
        }
        
        /* If we have exceeded the maximum size subsets to enumerate, stop */
        if(k > K) {
            return null;
        }
        
        List<T> subset = getSubset();
        pointers = advancePointers();
        return subset;
        
    }
    
}
