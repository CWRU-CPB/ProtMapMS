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
 * An interval tree to match query values to all intervals that they intersect.
 * 
 * @author Sean Maxwell
 */
public class IntervalTree {
    
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * 
     * IntervalTree vertex class
     * 
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * **/
    private class IntervalNode {
    
        public double low;
        public double high;
        public double key;
        public IntervalNode parent;
        public IntervalNode left;
        public IntervalNode right;
        public IntervalNode middle;
        public int id;
        
        public IntervalNode(IntervalNode p, double l, double h, int i, double k) {
            low    = l;
            high   = h;
            key    = k;
            parent = p;
            left   = null;
            right  = null;
            middle = null;
            id     = i;
        }
        
        public IntervalNode leftChild() {
            return left;
        }
        
        public void setLeftChild(IntervalNode c) {
            left = c;
        }
        
        public IntervalNode rightChild() {
            return right;
        }
        
        public void setRightChild(IntervalNode c) {
            right = c;
        }
        
        public IntervalNode middleChild() {
            return middle;
        }
        
        public IntervalNode parent() {
            return parent;
        }
        
        public void setParent(IntervalNode p) {
            parent = p;
        }
        
        public void setMiddleChild(IntervalNode c) {
            middle = c;
        }
        
        public double low() {
            return low;
        }
        
        public void setLow(double l) {
            low = l;
        }
        
        public double high() {
            return high;
        }
        
        public void setHigh(double h) {
            high = h;
        }
        
        public int id() {
            return id;
        }
        
        @Override
        public String toString() {
            int lid = left   != null ? left.id   : -1;
            int mid = middle != null ? middle.id : -1;
            int rid = right  != null ? right.id  : -1;
            int pid = parent != null ? parent.id : -1;
            
            
            
            return String.format("NODE[ID=%d,LOW=%f,HIGH=%f,KEY=%f,LEFT=%d,MIDDLE=%d,RIGHT=%d,PARENT=%d]\n",id,low,high,key,lid,mid,rid,pid);
        }
        
        public void replaceChild(IntervalNode target, IntervalNode source) {
            if(left != null && left == target) {
                left = source;
            }
            else if(middle != null && middle == target) {
                middle = source;
            }
            else if(right != null && right == target) {
                right = source;
            }
            else {
                System.err.printf("Trying to replace non-existent child!!!\n");
            }
        }
    }
    
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * 
     * IntervalTree class
     * 
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * **/
    private IntervalNode root;
    private int nHits;
    
    private int[] extendIDs(int[] ids) {
        int[] more = new int[ids.length+100];
        System.arraycopy(ids, 0, more, 0, ids.length);
        return more;
    }
    
    private int[] trimIDs(int[] ids, int n) {
        int[] trim = new int[n];
        System.arraycopy(ids,0,trim,0,n);
        return trim;
    }
    
    /**
     * Create a new empty RangeTree.
     */
    public IntervalTree() {
        root = null;
    }
    
    /**
     * Add a range to IntervalTree.
     * 
     * @param low Lower bound of range.
     * @param high Upper bound of range.
     * @param key A value that the range represents (the center for instance)
     * @param id A unique id that identifies the range.
     */
    public void add(double low, double high, double key, int id) {
        if(root == null) {
            root = new IntervalNode(null,low,high,id,key);
        }
        else {
            rAdd(root,low,high,key,id);
        }
    }
    
    private void rAdd(IntervalNode node, double low, double high, double key, int id) {
        /* If the insert range, swallows the current node range, replace the
	 * current node with a new range node representing the insert range,
	 * and attach the swallowed range node (wich might be a subtree) to the
	 * middle branch of the replacing node */
	if(low <  node.low() && high >= node.high() ||
	   low <= node.low() && high >  node.high() ) {
		IntervalNode pivot = new IntervalNode(node.parent(),low,high,id,key);
		IntervalNode temp;
                
		/* Attach middle children to the new node */
		pivot.setMiddleChild(node);
		
		/* Find first lesser region that is not contained by the this
		 * range */
		temp = node;
		while(temp != null && temp.low() >= low) {
			temp = temp.leftChild();
		}
		if(temp != null) {
			pivot.setLeftChild(temp);
			temp.parent().setLeftChild(null);
			temp.setParent(pivot);
		}
		
		/* Find first greater region that is not contained by the this
		 * range */
		temp = node;
		while(temp != null && temp.high() <= high) {
			temp = temp.rightChild();
		}
		if(temp != null) {
			pivot.setRightChild(temp);
			temp.parent().setRightChild(null);
			temp.setParent(pivot);
		}
		
		if(node.parent() != null) {
			node.parent().replaceChild(node,pivot);
		}
		else {
			root = pivot;
		}
		node.setParent(pivot);
	}
        
        /* If the range to add is the same as an existing range */
        else if(low == node.low() && high == node.high()) {
            System.err.printf("Skipping duplicate {L=%f,H=%f,ID=%d,KEY=%f} colliding with {L=%f,H=%f,ID=%d,KEY=%f}\n",
                              low,high,id,key,node.low(),node.high(),node.id(),node.key);
        }
	
	/* If the insert range is swallowed by the current node, follow the 
	 * middle edge to find the correct insert position, or create a new 
	 * middle child if the edge does not exist. */
        else if(low >= node.low() && high <= node.high()) {
		if(node.middleChild() == null) {
			node.setMiddleChild(new IntervalNode(node,low,high,id,key));
		}
		else {
			rAdd(node.middleChild(),low,high,key,id);
		}
	}
	
	/* If the insert range overlaps the current node range by crossing the
	 * current node's lower bound, or the insert range is comprised of values
	 * all less than the current range, either follow the lower bound child
	 * edge, or create a new lower bound child if the edge does not exist.*/
        else if(high < node.low() || (low < node.low() && high < node.high())) {
		if(node.leftChild() == null) {
			node.setLeftChild(new IntervalNode(node,low,high,id,key));
		}
		else {
			rAdd(node.leftChild(),low,high,key,id);	
		}
	}
	
	/* If the insert range overlaps the current node range by crossing the
	 * current node's upper bound, or the insert range is comprised of values
	 * all greater than the current range, either follow the upper bound
	 * child edge, or create a new upper bound child if the edge does not 
	 * exist. */
        else if(low > node.high() || (low > node.low() && high > node.high())) {
		if(node.rightChild() == null) {
			node.setRightChild(new IntervalNode(node,low,high,id,key));
		}
		else {
			rAdd(node.rightChild(),low,high,key,id);	
		}
	}
	
	/* The range is swallowed by a larger range */
	else {
		System.err.printf("Error: unmatchable insert for (%f,%f) :: (%f,%f)\n",low,high,node.low(),node.high());
	}
    }
    
    /**
     * Convenience method to print a IntervalTree.
     */
    public void printTree() {
        if(root == null) {
            System.out.printf("(/)\n");
        }
        else {
            rPrint(root,"","\n");
        }
    }
    
    private void rPrint(IntervalNode node, String indent, String term) {
        if(node.leftChild() != null) {
            rPrint(node.leftChild(),indent+"   ",term);
            System.out.printf("%s  /\n",indent);
        }
        
        System.out.printf("%s%d[%f,%f]%s",indent,node.id(),node.low(),node.high(),term);
        
        if(node.middleChild() != null) {
            System.out.printf("%s  -",indent);
            rPrint(node.middleChild(),indent+"   ",term);
        }
        
        if(node.rightChild() != null) {
            System.out.printf("%s  \\\n",indent);
            rPrint(node.rightChild(),indent+"   ",term);
        }
    }
    
    /**
     * Fetch a list of IDs for ranges the argument value falls within.
     * 
     * @param v Value to find inclusive ranges for.
     * 
     * @return List of IDs that the argument value falls within.
     */
    public int[] find(double v) {
        nHits = 0;
        int[] hits;
        
        if(root == null) {
            return new int[0];
        }
        else {
            hits = new int[100];
            hits = rFind(root,v,hits);
            return trimIDs(hits,nHits);
        }
    }
    
    private int[] rFind(IntervalNode node, double v, int[] hits) {
        if(v >= node.low() && v <= node.high()) {
            hits[nHits] = node.id();
            nHits++;
            if(nHits == hits.length) {
                hits = extendIDs(hits);
            }
            
            if(node.leftChild() != null) {
                hits = rFind(node.leftChild(),v,hits);
            }
            
            if(node.middleChild() != null) {
                hits = rFind(node.middleChild(),v,hits);
            }
            
            if(node.rightChild() != null) {
                hits = rFind(node.rightChild(),v,hits);
            }
        }
        else if(v < node.low()) {
            if(node.leftChild() != null) {
                hits = rFind(node.leftChild(),v,hits);
            }
        }
        else if(v > node.high()) {
            if(node.rightChild() != null) {
                hits = rFind(node.rightChild(),v,hits);
            }
        }
        else {
            System.err.printf("Strange execution finding %f\n",v);
        }
        
        return hits;
    }
    
}
