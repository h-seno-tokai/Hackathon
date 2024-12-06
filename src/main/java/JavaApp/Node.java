package JavaApp;

import java.util.ArrayList;

class Node {
	String name;

	// 自分から出ていくリンク
	ArrayList<Link> departFromMeLinks;
	// 自分に入ってくるリンク
	ArrayList<Link> arriveAtMeLinks;

	// コンストラクタ。引数の名前でNodeオブジェクトを作成する。
	// また、自分から出ていくリンクと自分に入ってくるリンクの管理用のリストを初期化する。
	public Node(String theName) {
		name = theName;
		departFromMeLinks = new ArrayList<Link>();
		arriveAtMeLinks = new ArrayList<Link>();
	}

	// ノードに到達するis-aリンクのテールノードを返す
	public ArrayList<Node> getISATails() {
		ArrayList<Node> isaParents = new ArrayList<Node>();
		for (Link theLink : departFromMeLinks) {
			if ("is-a".equals(theLink.getLabel())) {
				// theLink: thisNode(is child) =is-a=> parentNode
				isaParents.add(theLink.getHead());
			}
		}
		return isaParents;
	}


	// ノードから出ていくリンクをリストに追加する
	public void addDepartFromMeLinks(Link theLink) {
		departFromMeLinks.add(theLink);
	}

	// ノードから出ていくリンクのリストを返す
	public ArrayList<Link> getDepartFromMeLinks() {
		return departFromMeLinks;
	}

	// ノードに到達するリンクをリストに追加する
	public void addArriveAtMeLinks(Link theLink) {
		arriveAtMeLinks.add(theLink);
	}

	// ノードに到達するリンクのリストを返す
	public ArrayList<Link> getArriveAtMeLinks() {
		return arriveAtMeLinks;
	}

	// ノードの名前を返す
	public String getName() {
		return name;
	}

	// ノードの文字列表現を返す
	public String toString() {
		return name;
	}
}