package JavaApp;

import java.util.ArrayList;
import java.util.HashMap;

public class Link {
	String label;
	Node tail;
	Node head;
	boolean inheritance;

	// コンストラクタ．引数のラベル，テールノード，ヘッドノードを使ってLinkオブジェクトを作成する．
	// また，引数のSementicNetオブジェクトを使ってノードの管理も行う．
	public Link(String theLabel, String theTail, String theHead, SemanticNet sn) {
		label = theLabel;
		HashMap<String, Node> nodesNameTable = sn.getNodesNameTable();
		ArrayList<Node> nodes = sn.getNodes();

		tail = nodesNameTable.get(theTail);
		if (tail == null) {
			tail = new Node(theTail);
			nodes.add(tail);
			nodesNameTable.put(theTail, tail);
		}

		head = nodesNameTable.get(theHead);
		if (head == null) {
			head = new Node(theHead);
			nodes.add(head);
			nodesNameTable.put(theHead, head);
		}

		// is-aリンクであればinheritanceをtrueにする
		inheritance = "is-a".equals(theLabel);
	}

	// クエリ生成用のコンストラクタ．ラベル，テールノード，ヘッドノードを使ってLinkオブジェクトを作成する．
	public Link(String theLabel, String theTail, String theHead) {
		label = theLabel;
		tail = new Node(theTail);
		head = new Node(theHead);
		inheritance = false;
	}

	// Linkのinheritance属性をセットする
	public void setInheritance(boolean value) {
		inheritance = value;
	}

	// Linkのテールノードを返す
	public Node getTail() {
		return tail;
	}

	// Linkのヘッドノードを返す
	public Node getHead() {
		return head;
	}

	// Linkのラベルを返す
	public String getLabel() {
		return label;
	}

	// Linkの完全な名称を返す
	public String getFullName() {
		return tail.getName() + " " + label + " " + head.getName();
	}

	// Linkの文字列表現を返す
	public String toString() {
		String result = tail.getName() + "  =" + label + "=>  " + head.getName();
		if (!inheritance) {
			return result;
		} else {
			return "( " + result + " )";
		}
	}
}