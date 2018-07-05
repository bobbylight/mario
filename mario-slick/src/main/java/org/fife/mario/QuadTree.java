package org.fife.mario;

import java.util.ArrayList;
import java.util.List;

import org.fife.mario.level.Area;



public class QuadTree {

	private Area area;
	private Node root;

	private static final float WIDTH_THRESHOLD			= 300;


	public QuadTree(Area area) {

		this.area = area;
		float w = area.getWidth();
		float h = area.getHeight();
System.out.println("--- --- --- " + area.getWidth() + ", " + area.getHeight());
		root = new Node(null, 0,0, w,h);
		subdivide(root);
	}


	public int getDepth() {
		int count = 0;
		Node node = root;
		while (node!=null && node.children!=null) {
			count++;
			node = node.children[0];
		}
		return count;
	}


	private void subdivide(Node node) {

		node.children = new Node[4];

		float midX = (node.x + node.x2)/2;
		float midY = (node.y + node.y2)/2;

		node.children[0] = new Node(node, node.x,node.y,  midX,midY);
		node.children[1] = new Node(node, midX,node.y,    node.x2,midY);
		node.children[2] = new Node(node, midX,midY,      node.x2,node.y2);
		node.children[3] = new Node(node, node.x,midY,    midX,node.y2);

		float newW = midX - node.x;
		if (newW>WIDTH_THRESHOLD) {
			for (int i=0; i<4; i++) {
				subdivide(node.children[i]);
			}
		}

	}


	private static class Node {

		public Node parent;
		public float x;
		public float y;
		public float x2;
		public float y2;
		public Node[] children;
		public List<AbstractEntity> entities;

		Node(Node parent, float x, float y, float x2, float y2) {
			this.parent = parent;
			this.x = x;
			this.y = y;
			this.x2 = x2;
			this.y2 = y2;
			entities = new ArrayList<>();
		}

		public float getWidth() {
			return x2 - x;
		}

	}
}
