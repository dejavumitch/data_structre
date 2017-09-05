package datastrct;

import java.util.HashSet;
import java.util.Random;

public class RedBlackTree {

	public static void main(String[] args) {
		/*
		RBTree redBlackTree = new RBTree(10);
		redBlackTree.offer(29);
		redBlackTree.offer(35);
		redBlackTree.offer(5);
		redBlackTree.offer(21);
		redBlackTree.offer(77);
		
		for (RBTreeNode node : redBlackTree.set) {
			if (node == null) {
				System.out.println("null");
			} else {
				System.out.println(node.getValue() 
						+ ",   parent: " + (node.parent == null ? "null" : node.parent.getValue()) 
						+ ",   left: " + (node.left == null ? "null" : node.left.getValue()) 
						+ ",   right: " + (node.right == null ? "null" : node.right.getValue()));
			}
			
		}
		
		redBlackTree.offer(7);
		System.out.println("null");
		for (RBTreeNode node : redBlackTree.set) {
			if (node == null) {
				System.out.println("null");
			} else {
				System.out.println(node.getValue() 
						+ ",   parent: " + (node.parent == null ? "null" : node.parent.getValue()) 
						+ ",   left: " + (node.left == null ? "null" : node.left.getValue()) 
						+ ",   right: " + (node.right == null ? "null" : node.right.getValue()));
			}
			
		}
		 */
		
		
		RBTreeTest.getInstance();
		
	}
}

class RBTreeTest{
	private static RBTreeTest instance = null;
	protected RBTreeTest(){
		test(100);
	}
	public static RBTreeTest getInstance() {
		if (instance == null) {
			instance = new RBTreeTest();
		}
		return instance;
	}
	private void test(int capacity) {
		RBTree redBlackTree = new RBTree(capacity);
		int[] input = new int[capacity];
		
		fillArray(input);
		shuffleArray(input);
		
		for (int i = 0; i < capacity; i++) {
			System.out.println("insert(" + i + "): " + input[i] + " , size: " + redBlackTree.size());
			redBlackTree.offer(input[i]);
			if (!redBlackTree.isValidRedBlackTree()) {
				System.out.println("Invalid");
				break;
			}
		}
		redBlackTree.printTree();
		shuffleArray(input);
		for (int i = 0; i < capacity; i++) {
			redBlackTree.delete(input[i]);
			if (!redBlackTree.isValidRedBlackTree()) {
				System.out.println("Not valid");
				break;
			}
		}
	}
	
	private void shuffleArray(int[] input) {
		Random ran = new Random();
		for (int i = input.length - 1; i > 0; i--) {
			int j = ran.nextInt(i + 1);
			swap(input, i, j);
		}
	}

	private void swap(int[] input, int i, int j) {
		if (i == j) {
			return;
		}
		int temp = input[i];
		input[i] = input[j];
		input[j] = temp;
	}

	private void fillArray(int[] input) {
		for (int i = 0; i < input.length; i++) {
			input[i] = i;
		}
	}
}

class RBTree{
	private RBTreeNode root;
	private final int capacity;
	private int size = 0;	
	public RBTree(int cap) {
		capacity = cap;
	}
	
	public int size() {
		return size;
	}
	
	public void printTree() {
		printTreeInOrder(root, 3);
		System.out.println("--------------------------------size: " + size + "/" + capacity);
	}
	
	private void printTreeInOrder(RBTreeNode start, int space) {
		if(start == null) {
			return;
	    }
		printTreeInOrder(start.right, space + 5);
	    for(int i=0; i < space; i++) {
	        System.out.print(" ");
	    }
	    System.out.println(start.getValue() + " " + (start.isRed() ? "R" : "B"));
	    printTreeInOrder(start.left, space + 5);
	}
	
	
	public boolean isValidRedBlackTree() {
		if (root == null) {
			return true;
		}
		if (root.isRed()) {
			System.out.print("Root is not black");
			return false;
		}
		return (getBlackHeight(root) != -1) && doubleRedParentChild(root);
	}
	
	private boolean doubleRedParentChild(RBTreeNode node) {
		if (node == null) {
			return true;
		}
		if (node.isRed() && node.parent != null && node.parent.isRed()) {
			return false;
		}
		return doubleRedParentChild(node.left) && doubleRedParentChild(node.right);
	}
	
	private int getBlackHeight(RBTreeNode node) {
		if (node == null) {
			return 1;
		}
		int left = getBlackHeight(node.left);
		int right = getBlackHeight(node.right);
		
		if (left != right) {
			return -1;
		}
		return node.isRed() ? left : left + 1;
	}
	
	
	public boolean offer(int value) {
		if (size == capacity) {
			return false;
		} 
		if (root == null) {
			root = new RBTreeNode(value);
		} else {
			addToTree(value);
		}
		size++;
		root.toBlack();
		return true;
	}
	
	private void addToTree(int value) {
		RBTreeNode cur = root;
		RBTreeNode prev = null;
		boolean isLeft = false;
		while (cur != null) {
			prev = cur;
			if (cur.getValue() > value) {
				cur = cur.left;
				isLeft = true;
			} else {
				cur = cur.right;
				isLeft = false;
			}
		}
		cur = new RBTreeNode(value);
		if (isLeft) {
			prev.left = cur;
		} else {
			prev.right = cur;
		}
		cur.parent = prev;
		
		if (prev.isRed()) {
			check(cur);
		}
	}
	
	private void check(RBTreeNode cur) {
		if (cur.parent == null || cur.parent.parent == null || !cur.parent.isRed()) {
			return;
		}
		RBTreeNode parent = cur.parent;
		RBTreeNode grandParent = parent.parent;
		
		boolean leftChild = parent.left == cur;
		boolean leftParent = grandParent.left == parent;
		RBTreeNode uncle = leftParent ? grandParent.right : grandParent.left;
		
		if (uncle != null && uncle.isRed()) {
			// toggle colors
			parent.toBlack();
			uncle.toBlack();
			grandParent.toRed();
			check(grandParent);
			return;
		} 
		
		// different directions, make them the same
		if (leftChild ^ leftParent) {
			if (leftChild) {
				rightRotate(grandParent, parent, cur);
				leftRotate(grandParent.parent, grandParent, cur);
			} else {
				leftRotate(grandParent, parent, cur);
				rightRotate(grandParent.parent, grandParent, cur);
			}
			cur.toBlack();
			grandParent.toRed();
		} else {
			if (leftParent) {
				rightRotate(grandParent.parent, grandParent, parent);
			} else {
				leftRotate(grandParent.parent, grandParent, parent);
			}
			parent.toBlack();
			grandParent.toRed();
		}
	}
	

	
	private void leftRotate(RBTreeNode grandParent, RBTreeNode parent, RBTreeNode cur) {
		RBTreeNode leftChild = cur.left;
		if (grandParent == null) {
			root = cur;
		} else {
			if (grandParent.left == parent) {
				grandParent.left = cur;
			} else {
				grandParent.right = cur;
			}
		}
		cur.parent = grandParent;
		cur.left = parent;
		parent.parent = cur;
		parent.right = leftChild;
		if (leftChild != null) {
			leftChild.parent = parent;
		}
	}

	private void rightRotate(RBTreeNode grandParent, RBTreeNode parent, RBTreeNode cur) {
		RBTreeNode rightChild = cur.right;
		if (grandParent == null) {
			root = cur;
		} else {
			if (grandParent.left == parent) {
				grandParent.left = cur;
			} else {
				grandParent.right = cur;
			}
		}
		cur.parent = grandParent;
		cur.right = parent;
		parent.parent = cur;
		parent.left = rightChild;
		if (rightChild != null) {
			rightChild.parent = parent;
		}
	}

	public boolean delete(int value) {
		if (size == 0) {
			return false;
		}
		RBTreeNode target = findNode(value);
		if (target == null) {
			return false;
		}
		delete(target);
		size--;
		if (root != null) {
			root.toBlack();
		}
		return true;
	}
	
	
	private void delete(RBTreeNode target) {
		if (target.right == null || target.left == null) {
			// delete target itself
			deleteSelf(target);
		} else {
			RBTreeNode successorNode = minInSubtree(target.right);
			target.setValue(successorNode.getValue());
			deleteSelf(successorNode);
		}
	}
	
	private void deleteSelf(RBTreeNode target) {
		if (target.isRed()) {			
			disconnectNode(target);
		} else if (target.right != null || target.left != null) {
			// one child then must be red
			RBTreeNode parent = target.parent;
			RBTreeNode child = null;
			if (target.right != null) {
				child = target.right;
				target.right = null;
			} else {
				child = target.left;
				target.left = null;
			}
			if (parent != null) {
				if (parent.left == target) {
					parent.left = child;
				} else {
					parent.right = child;
				}
			} else {
				// target must be root
				root = child;
			}
			target.parent = null;
			child.parent = parent;
			child.toBlack();
			
		} else {
			// deleting a black leaf (non-null, can be root) node, may cause unbalanced heights of black nodes
			deleteBlackLeafNodeCase1(target);
			if (target == root) {
				root = null;
			} else {
				disconnectNode(target);
			}
		}
	}
	
	
	private void disconnectNode(RBTreeNode target) {
		RBTreeNode parent = target.parent;
		if (parent.left == target) {
			parent.left = null;
		} else {
			parent.right = null;
		}
		target.parent = null;
	}
	
	// target is root
	private void deleteBlackLeafNodeCase1(RBTreeNode target) {
		if (target == root) {
			return;
		}
		deleteBlackLeafNodeCase2(target);
	}
	
	// sibling is red (then nephew must exist and is black, parent must be black)
	private void deleteBlackLeafNodeCase2(RBTreeNode target) {
		RBTreeNode sibling = getSibling(target);
		if (sibling.isRed()) {
			RBTreeNode parent = target.parent;
			if (parent.left == target) {
				leftRotate(parent, target, sibling, true);
			} else {
				rightRotate(parent, target, sibling, true);
			}
		}
		
		deleteBlackLeafNodeCase3(target);
	}
	
	private void rightRotate(RBTreeNode parent, RBTreeNode target, RBTreeNode sibling, boolean changeColor) {
		RBTreeNode grandParent = parent.parent;
		RBTreeNode nephew = sibling.right;
		parent.left = nephew;
		if (nephew != null) {
			nephew.parent = parent;
		}
		sibling.right = parent;
		parent.parent = sibling;
		if (grandParent == null) {
			root = sibling;
		} else {
			sibling.parent = grandParent;
			if (grandParent.left == parent) {
				grandParent.left = sibling;
			} else {
				grandParent.right = sibling;
			}
		}
		if (changeColor) {
			sibling.toBlack();
			parent.toRed();
		}
	}
	
	private void leftRotate(RBTreeNode parent, RBTreeNode target, RBTreeNode sibling, boolean changeColor) {
		RBTreeNode grandParent = parent.parent;
		RBTreeNode nephew = sibling.left;
		parent.right = nephew;
		if (nephew != null) {
			nephew.parent = parent;
		}
		sibling.left = parent;
		parent.parent = sibling;
		if (grandParent == null) {
			root = sibling;
		} else {
			sibling.parent = grandParent;
			if (grandParent.left == parent) {
				grandParent.left = sibling;
			} else {
				grandParent.right = sibling;
			}
		}
		if (changeColor) {
			sibling.toBlack();
			parent.toRed();
		}
	}
	
	
	private RBTreeNode getSibling(RBTreeNode target) {
		RBTreeNode parent = target.parent;
		if (parent.left == target) {
			return parent.right;
		} else {
			return parent.left;
		}
	}
	
	// parent and sibling and nephew are black
	private void deleteBlackLeafNodeCase3(RBTreeNode target) {
		RBTreeNode sibling = getSibling(target);
		if (!target.parent.isRed() && !sibling.isRed() && (sibling.left == null || !sibling.left.isRed()) 
				&& (sibling.right == null || !sibling.right.isRed())) {
			sibling.toRed();
			deleteBlackLeafNodeCase1(target.parent);
			return;
		} 
		deleteBlackLeafNodeCase4(target);
	}
	
	// parent is red, sibling and nephew are black
	private void deleteBlackLeafNodeCase4(RBTreeNode target) {
		RBTreeNode sibling = getSibling(target);
		if (target.parent.isRed() && !sibling.isRed() && (sibling.left == null || !sibling.left.isRed()) 
				&& (sibling.right == null || !sibling.right.isRed())) {
			target.parent.toBlack();
			sibling.toRed();
			return;
		}
		deleteBlackLeafNodeCase5(target);
	}
	
	// sibling is black, closer nephew is red, further nephew is black
	private void deleteBlackLeafNodeCase5(RBTreeNode target) {
		RBTreeNode sibling = getSibling(target);
		if (!sibling.isRed()) {
			RBTreeNode parent = target.parent;
			if (parent.left == target && sibling.left != null && sibling.left.isRed() 
					&& (sibling.right == null || !sibling.right.isRed())) {
				rightRotate(sibling, sibling.right, sibling.left, true);
			}
			if (parent.right == target && sibling.right != null && sibling.right.isRed() 
					&& (sibling.left == null || !sibling.left.isRed())) {
				leftRotate(sibling, sibling.left, sibling.right, true);
			}
		}
		deleteBlackLeafNodeCase6(target);
	}
	
	// sibling is black, further nephew is red
	private void deleteBlackLeafNodeCase6(RBTreeNode target) {
		RBTreeNode sibling = getSibling(target);
		RBTreeNode parent = target.parent;
		if (parent.isRed()) {
			sibling.toRed();
		} else {
			sibling.toBlack();
		}
		parent.toBlack();
		if (parent.left == target) {
			sibling.right.toBlack();
			leftRotate(parent, target, sibling, false);
		} else {
			sibling.left.toBlack();
			rightRotate(parent, target, sibling, false);
		}
	}
	
	
	
	private RBTreeNode minInSubtree(RBTreeNode node) {
		while (node.left != null) {
			node = node.left;
		}
		return node;
	}
	
	private RBTreeNode findNode(int value) {
		RBTreeNode cur = root;
		while (cur != null && cur.getValue() != value) {
			if (cur.getValue() < value) {
				cur = cur.right;
			} else {
				cur = cur.left;
			}
		}
		return cur;
	}
	
}



class RBTreeNode{
	private boolean isRed = false;
	private int value;
	public RBTreeNode left, right, parent;
	public RBTreeNode(int value) {
		this.value = value;
		isRed = true;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public void toBlack () {
		isRed = false;
	}
	public void toRed () {
		isRed = true;
	}
	public boolean isRed() {
		return isRed;
	}
	public int getValue() {
		return value;
	}
	
}


