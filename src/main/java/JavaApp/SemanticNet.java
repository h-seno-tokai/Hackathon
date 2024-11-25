package JavaApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

/***
 * SemanticNetクラス
 * セマンティックネットワークを管理するクラス。ノードやリンクの追加、クエリ処理を行う。
 */
public class SemanticNet {

    private ArrayList<Node> nodes;  // Nodeのリスト
    private HashMap<String, Node> nodesNameTable;  // Node名で検索できるテーブル

    // コンストラクタ。ノードリストとノード名テーブルを初期化する。
    public SemanticNet() {
        nodes = new ArrayList<>();
        nodesNameTable = new HashMap<>();
    }
    //企業の画像のpathを返すメソッド
    public String getPicturePath(String company) {
        for (Node node : nodes) {
            ArrayList<Link> arriveAtMeLinks = node.getArriveAtMeLinks();
            for (Link link : arriveAtMeLinks) {
                if ("picturePath".equals(link.getLabel()) && company.equals(link.getTail().getName())) {
                    return link.getHead().getName();
                }
            }
        }
        return null;
    }

    //企業のURLを取得．企業名を引数に受け取り，ラベルノードがURLのリンクの対応するヘッドノードを返す
    public String getURL(String company) {
        for (Node node : nodes) {
            ArrayList<Link> arriveAtMeLinks = node.getArriveAtMeLinks();
            for (Link link : arriveAtMeLinks) {
                if ("URL".equals(link.getLabel()) && company.equals(link.getTail().getName())) {
                    return link.getHead().getName();
                }
            }
        }
        return null;
    }
    //getURLと同じ手法で企業の補足情報を取得
    public String getSupplement(String company) {
        for (Node node : nodes) {
            ArrayList<Link> arriveAtMeLinks = node.getArriveAtMeLinks();
            for (Link link : arriveAtMeLinks) {
                if ("suppplement".equals(link.getLabel()) && company.equals(link.getTail().getName())) {
                    return link.getHead().getName();
                }
            }
        }
        return null;
    }

    //カテゴリを取得．ラベルがis-aリンクのテールノードのリストを返す
    public ArrayList<String> getcategory() {
        HashSet<String> categorySet = new HashSet<>();
        for (Node node : nodes) {
            ArrayList<Link> arriveAtMeLinks = node.getArriveAtMeLinks();
            for (Link link : arriveAtMeLinks) {
                if ("is-a".equals(link.getLabel())) {
                    categorySet.add(link.getHead().getName());
                }
            }
        }
        return new ArrayList<>(categorySet);
    }

    //所在地を取得．
    public ArrayList<String> getlocation() {
        HashSet<String> locationSet = new HashSet<>();
        for (Node node : nodes) {
            ArrayList<Link> arriveAtMeLinks = node.getArriveAtMeLinks();
            for (Link link : arriveAtMeLinks) {
                if ("locate".equals(link.getLabel())) {
                    locationSet.add(link.getHead().getName());
                }
            }
        }
        return new ArrayList<>(locationSet);
    }

    // ノードリストとノード名テーブルを返すためのメソッド
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public HashMap<String, Node> getNodesNameTable() {
        return nodesNameTable;
    }

    // リンクをデータベースに追加し、ノードのリンク情報も更新する
    public void addLink(Link link) {
        try {
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO links (label, tail, head) VALUES (?, ?, ?)");
            ps.setString(1, link.getLabel());
            ps.setString(2, link.getTail().getName());
            ps.setString(3, link.getHead().getName());
            ps.executeUpdate();

            // Nodeに出発・到着リンクを追加
            link.getTail().addDepartFromMeLinks(link);
            link.getHead().addArriveAtMeLinks(link);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // データベースからリンク情報を取得する
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<>();
        try {
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT label, tail, head FROM links");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String label = rs.getString("label");
                String tail = rs.getString("tail");
                String head = rs.getString("head");
                links.add(new Link(label, tail, head, this));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return links;
    }

    // データベースのリンクが空かどうかを確認する
    public boolean isEmpty() {
        try {
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM links");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    // データベースのリンクを全て削除する
    public void DeleteAllLinks() {
        try {
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM links");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 初期リンクを追加する
    public void addInitialLinks() {
        addLink(new Link("is-a", "Fujitsu", "system-integrator", this));
        addLink(new Link("is-a", "NEC", "system-integrator", this));
        addLink(new Link("is-a", "NTT-Data", "system-integrator", this));
        addLink(new Link("is-a", "Rakuten", "e-commerce", this));
        addLink(new Link("is-a", "Mercari", "e-commerce", this));
        addLink(new Link("is-a", "Yahoo-Japan", "internet-services", this));
        addLink(new Link("is-a", "LINE", "social-networking", this));
        addLink(new Link("is-a", "SoftBank", "telecommunications", this));
        addLink(new Link("is-a", "NTT-Communications", "telecommunications", this));
        addLink(new Link("is-a", "KDDI", "telecommunications", this));
        addLink(new Link("is-a", "Sony", "electronics", this));
        addLink(new Link("is-a", "Panasonic", "electronics", this));
        addLink(new Link("is-a", "Canon", "electronics", this));
        addLink(new Link("is-a", "Trend-Micro", "cybersecurity", this));
        addLink(new Link("is-a", "CyberAgent", "digital-marketing", this));
        addLink(new Link("is-a", "GMO-Internet", "internet-services", this));
        addLink(new Link("locate", "Fujitsu", "Tokyo", this));
        addLink(new Link("locate", "NEC", "Tokyo", this));
        addLink(new Link("locate", "NTT-Data", "Tokyo", this));
        addLink(new Link("locate", "Rakuten", "Tokyo", this));
        addLink(new Link("locate", "Mercari", "Tokyo", this));
        addLink(new Link("locate", "Yahoo-Japan", "Tokyo", this));
        addLink(new Link("locate", "LINE", "Tokyo", this));
        addLink(new Link("locate", "SoftBank", "Tokyo", this));
        addLink(new Link("locate", "NTT-Communications", "Tokyo", this));
        addLink(new Link("locate", "KDDI", "Tokyo", this));
        addLink(new Link("locate", "Sony", "Tokyo", this));
        addLink(new Link("locate", "Panasonic", "Osaka", this));
        addLink(new Link("locate", "Canon", "Tokyo", this));
        addLink(new Link("locate", "Trend-Micro", "Tokyo", this));
        addLink(new Link("locate", "CyberAgent", "Tokyo", this));
        addLink(new Link("locate", "GMO-Internet", "Tokyo", this));
        addLink(new Link("URL", "Fujitsu", "https://www.fujitsu.com/", this));
        addLink(new Link("URL", "NEC", "https://www.nec.com/", this));
        addLink(new Link("URL", "NTT-Data", "https://www.nttdata.com/", this));
        addLink(new Link("URL", "Rakuten", "https://www.rakuten.com/", this));
        addLink(new Link("URL", "Mercari", "https://www.mercari.com/", this));
        addLink(new Link("URL", "Yahoo-Japan", "https://www.yahoo.co.jp/", this));
        addLink(new Link("URL", "LINE", "https://line.me/", this));
        addLink(new Link("URL", "SoftBank", "https://www.softbank.jp/", this));
        addLink(new Link("URL", "NTT-Communications", "https://www.ntt.com/", this));
        addLink(new Link("URL", "KDDI", "https://www.kddi.com/", this));
        addLink(new Link("URL", "Sony", "https://www.sony.com/", this));
        addLink(new Link("URL", "Panasonic", "https://www.panasonic.com/", this));
        addLink(new Link("URL", "Canon", "https://global.canon/", this));
        addLink(new Link("URL", "Trend-Micro", "https://www.trendmicro.com/", this));
        addLink(new Link("URL", "CyberAgent", "https://www.cyberagent.co.jp/", this));
        addLink(new Link("URL", "GMO-Internet", "https://www.gmo.jp/", this));
        addLink(new Link("suppplement", "Fujitsu", "富士通株式会社（ふじつう、英: Fujitsu Limited）は、神奈川県川崎市中原区に本社を置く日本の総合電機メーカー兼総合ITベンダー。通信システム、情報処理システムおよび電子デバイスの製造・販売ならびにそれらに関するサービスの提供を行っている。", this));
        addLink(new Link("suppplement", "NEC", "日本電気株式会社（にほんでんき、英: NEC Corporation）は、東京都港区に本社を置く日本の電機メーカー。ITサービス、通信機器、セキュリティソリューションなど多岐にわたる事業を展開し、特にICT分野で国内外でのプレゼンスを誇る。", this));
        addLink(new Link("suppplement", "NTT-Data", "株式会社エヌ・ティ・ティ・データ（NTT DATA Corporation）は、東京都江東区に本社を置く、情報システムの開発・運用を主な事業とする日本のITサービス企業。NTTグループの一員であり、国内最大級のITサービスプロバイダー。", this));
        addLink(new Link("suppplement", "Rakuten", "楽天グループ株式会社（らくてん、英: Rakuten Group, Inc.）は、東京都世田谷区に本社を置く、日本を代表するECサイト運営や金融サービスを展開する企業。ECサイト「楽天市場」を中心に、幅広いオンラインサービスを提供している。", this));
        addLink(new Link("suppplement", "Mercari", "株式会社メルカリ（Mercari, Inc.）は、東京都港区に本社を置く日本のIT企業。フリマアプリ「メルカリ」の運営を行い、国内外でのサービス展開を行っている。", this));
        addLink(new Link("suppplement", "Yahoo-Japan", "Zホールディングス株式会社（旧称: Yahoo Japan Corporation）は、東京都千代田区に本社を置く日本のIT企業。検索エンジンやオンラインショッピング、広告事業を展開するインターネット関連企業。", this));
        addLink(new Link("suppplement", "LINE", "LINE株式会社は、東京都新宿区に本社を置く日本のIT企業。無料メッセージアプリ「LINE」を中心に、広告、フィンテック、エンターテインメント事業を展開。", this));
        addLink(new Link("suppplement", "SoftBank", "ソフトバンク株式会社（SoftBank Corp.）は、東京都港区に本社を置く日本の通信事業者。モバイル通信事業を中心に、インターネットサービスやIoT分野での事業も展開。", this));
        addLink(new Link("suppplement", "NTT-Communications", "エヌ・ティ・ティ・コミュニケーションズ株式会社（NTT Communications Corporation）は、東京都千代田区に本社を置く、日本の通信事業会社。国際通信やクラウドサービスを中心とした事業を行っている。", this));
        addLink(new Link("suppplement", "KDDI", "KDDI株式会社（ケイディーディーアイ、英: KDDI Corporation）は、東京都千代田区に本社を置く日本の通信事業者。auブランドで知られるモバイル通信を中心に、固定通信やIoT事業も展開している。", this));
        addLink(new Link("suppplement", "Sony", "ソニーグループ株式会社（Sony Group Corporation）は、東京都港区に本社を置く日本の多国籍コングロマリット。家電、音楽、映画、ゲームなど多岐にわたる分野で世界的なプレゼンスを持つ。", this));
        addLink(new Link("suppplement", "Panasonic", "パナソニック株式会社（Panasonic Corporation）は、大阪府門真市に本社を置く日本の総合電機メーカー。家電、住宅設備、エコソリューションなどの多様な事業を展開している。", this));
        addLink(new Link("suppplement", "Canon", "キヤノン株式会社（Canon Inc.）は、東京都大田区に本社を置く日本の電機メーカー。カメラ、プリンター、医療機器などを中心に幅広い分野で事業を展開。", this));
        addLink(new Link("suppplement", "Trend-Micro", "トレンドマイクロ株式会社（Trend Micro Incorporated）は、東京都渋谷区に本社を置く日本のITセキュリティ企業。ウイルス対策ソフトウェアやセキュリティソリューションを提供。", this));
        addLink(new Link("suppplement", "CyberAgent", "株式会社サイバーエージェント（CyberAgent, Inc.）は、東京都渋谷区に本社を置く日本のIT企業。広告事業、ゲーム、メディア運営を中心に事業を展開。", this));
        addLink(new Link("suppplement", "GMO-Internet", "GMOインターネット株式会社（GMO Internet Group, Inc.）は、東京都渋谷区に本社を置く日本のIT企業。ドメイン登録、ホスティング、インターネット広告事業を展開。", this));
        addLink(new Link("picturePath", "Fujitsu", "/Fujitsu.jpg", this));
        addLink(new Link("picturePath", "NEC", "/NEC.jpg", this));
        addLink(new Link("picturePath", "NTT-Data", "/NTT-Data.jpg", this));
        addLink(new Link("picturePath", "Rakuten", "/Rakuten.jpg", this));
        addLink(new Link("picturePath", "Mercari", "/Mercari.jpg", this));
        addLink(new Link("picturePath", "Yahoo-Japan", "/Yahoo-Japan.jpg", this));
        addLink(new Link("picturePath", "LINE", "/LINE.jpg", this));
        addLink(new Link("picturePath", "SoftBank", "/SoftBank.jpg", this));
        addLink(new Link("picturePath", "NTT-Communications", "/NTT-Communications.jpg", this));
        addLink(new Link("picturePath", "KDDI", "/KDDI.jpg", this));
        addLink(new Link("picturePath", "Sony", "/Sony.jpg", this));
        addLink(new Link("picturePath", "Panasonic", "/Panasonic.jpg", this));
        addLink(new Link("picturePath", "Canon", "/Canon.jpg", this));
        addLink(new Link("picturePath", "Trend-Micro", "/Trend-Micro.jpg", this));
        addLink(new Link("picturePath", "CyberAgent", "/CyberAgent.jpg", this));
        addLink(new Link("picturePath", "GMO-Internet", "/GMO-Internet.jpg", this));


    }

    // クエリを実行し、結果を文字列で返す
    public ArrayList<String> query(ArrayList<Link> queries) {
        System.out.println("*** Query ***");
        for (Link q : queries) {
            System.out.println(q.toString());
        }

        List<List<Map<String, String>>> bindingsList = new ArrayList<>();
        for (Link query : queries) {
            List<Map<String, String>> bindings = queryLink(query);
            if (bindings.isEmpty()) {
                // クエリが失敗した場合
                return null;
            } else {
                bindingsList.add(bindings);
            }
        }

        List<Map<String, String>> results = join(bindingsList);

        if (results.isEmpty()) {
            return null;
        }
        //クエリの検索結果を基に？xに当てはまる企業名のリストを作成する
        ArrayList<String> companyList = new ArrayList<>();
        for (Map<String, String> result : results) {
            companyList.add(result.get("?x"));
        }
        //企業名を全て表示
        for (String company : companyList) {
            System.out.println(company);
        }
        return companyList;
    }

    // 単一のクエリを実行する
    public List<Map<String, String>> queryLink(Link theQuery) {
        List<Map<String, String>> bindingsList = new ArrayList<>();
        List<Link> links = getLinks();
        for (Link link : links) {
            Map<String, String> bindings = new HashMap<>();
            if (new Matcher().matching(theQuery.getFullName(), link.getFullName(), bindings)) {
                bindingsList.add(bindings);
            }
        }
        return bindingsList;
    }

    // 複数のクエリ結果を結合する
    public List<Map<String, String>> join(List<List<Map<String, String>>> bindingsList) {
        int size = bindingsList.size();
        switch (size) {
            case 0:
                // バインディングがない場合
                return new ArrayList<>();
            case 1:
                return bindingsList.get(0);
            case 2:
                return joinBindings(bindingsList.get(0), bindingsList.get(1));
            default:
                List<Map<String, String>> first = bindingsList.get(0);
                bindingsList.remove(0);
                List<Map<String, String>> rest = join(bindingsList);
                return joinBindings(first, rest);
        }
    }

    // 2つのバインディングリストを結合する
    public List<Map<String, String>> joinBindings(List<Map<String, String>> bindings1, List<Map<String, String>> bindings2) {
        List<Map<String, String>> resultBindings = new ArrayList<>();
        for (Map<String, String> b1 : bindings1) {
            for (Map<String, String> b2 : bindings2) {
                Map<String, String> merged = joinBinding(b1, b2);
                if (merged != null) {
                    resultBindings.add(merged);
                }
            }
        }
        return resultBindings;
    }

    // 2つのバインディングを結合する
    public Map<String, String> joinBinding(Map<String, String> b1, Map<String, String> b2) {
        Map<String, String> result = new HashMap<>(b1);
        for (String key : b2.keySet()) {
            String value1 = result.get(key);
            String value2 = b2.get(key);
            if (value1 != null && !value1.equals(value2)) {
                // 競合がある場合、失敗
                return null;
            } else {
                result.put(key, value2);
            }
        }
        return result;
    }

    // リンクの一覧を表示する
    public void printLinks() {
        System.out.println("*** Links ***");
        List<Link> links = getLinks();
        for (int i = 0; i < links.size(); i++) {
            System.out.println(((Link) links.get(i)).toString());
        }
    }

    // ノードの一覧を表示する
    public void printNodes() {
        System.out.println("*** Nodes ***");
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println(((Node) nodes.get(i)).toString());
        }
    }
}