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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sean-m
 */
public class ListOfListsEnumeratorTest {
    ListOfListsEnumerator lole;
    
    public ListOfListsEnumeratorTest() {
    
    }
    
    private List<Integer> getList(Integer...args) {
        List<Integer> r = new ArrayList<>();
        for(Integer i : args) {
            r.add(i);
        }
        return r;
    }
    
    @Before
    public void setUp() {
        List<Integer> stack1 = new ArrayList<>();
        List<Integer> stack2 = new ArrayList<>();
        List<Integer> stack3 = new ArrayList<>();
        List<List<Integer>> stacks = new ArrayList<>();
        stack1.add(1);
        stack1.add(2);
        stack1.add(3);
        stack2.add(1);
        stack3.add(1);
        stack3.add(2);
        stacks.add(stack1);
        stacks.add(stack2);
        stacks.add(stack3);
                
        lole = new ListOfListsEnumerator(stacks,2);
    }

    @Test
    public void testGetNext() {
        List<List<Integer>> expected = new ArrayList<>();
        expected.add(getList(1));
        expected.add(getList(2));
        expected.add(getList(3));
        expected.add(getList(1));
        expected.add(getList(1));
        expected.add(getList(2));
        expected.add(getList(1, 1));
        expected.add(getList(2, 1));
        expected.add(getList(3, 1));
        expected.add(getList(1, 1));
        expected.add(getList(1, 2));
        expected.add(getList(2, 1));
        expected.add(getList(2, 2));
        expected.add(getList(3, 1));
        expected.add(getList(3, 2));
        expected.add(getList(1, 1));
        expected.add(getList(1, 2));
        
        int index = 0;
        for(int i=0;i<17;i++) {
            assertEquals(expected.get(i),lole.getNext());
        }
        assertEquals(null,lole.getNext());
    }

    
}
