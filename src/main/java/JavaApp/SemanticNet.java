package JavaApp;

import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

/**
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

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public HashMap<String, Node> getNodesNameTable() {
        return nodesNameTable;
    }

    // 初期リンクを追加する（処理を委譲）
    public void addInitialLinks() {
        InitialLinkAdder.addInitialLinks(this);
    }

    public String getPicturePath(String company) {
        return getAttribute(company, "picturePath");
    }

    public String getURL(String company) {
        return getAttribute(company, "URL");
    }

    public String getSupplement(String company) {
        return getAttribute(company, "supplement");
    }

    private String getAttribute(String company, String label) {
        for (Node node : nodes) {
            ArrayList<Link> arriveAtMeLinks = node.getArriveAtMeLinks();
            for (Link link : arriveAtMeLinks) {
                if (label.equals(link.getLabel()) && company.equals(link.getTail().getName())) {
                    return link.getHead().getName();
                }
            }
        }
        return null;
    }

    public ArrayList<String> getcategory() {
        return getAttributeList("category");
    }

    public ArrayList<String> getAverageSalary() {
        ArrayList<String> averageSalaries = getAttributeList("averageSalary");
        return sortAverageSalaries(averageSalaries);
    }

    private ArrayList<String> sortAverageSalaries(ArrayList<String> salaries) {
        // ソート用のリストを作成
        List<SalaryRange> ranges = new ArrayList<>();
        for (String salary : salaries) {
            if (!salary.equals("N/A")) { // "N/A" を削除
                ranges.add(new SalaryRange(salary));
            }
        }

        // 最小値を基準にソート
        ranges.sort(Comparator.comparingInt(SalaryRange::getMin));

        // 元の形式に戻す
        ArrayList<String> sortedSalaries = new ArrayList<>();
        for (SalaryRange range : ranges) {
            sortedSalaries.add(range.getOriginalText());
        }

        return sortedSalaries;
    }

    // 範囲を解析するクラス
    private class SalaryRange {
        private String originalText;
        private int min;

        public SalaryRange(String text) {
            this.originalText = text;
            this.min = parseMinValue(text);
        }

        public String getOriginalText() {
            return originalText;
        }

        public int getMin() {
            return min;
        }

        private int parseMinValue(String text) {
            Pattern pattern = Pattern.compile("(\\d+)");
            java.util.regex.Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1)) * 10000; // 万円単位を考慮
            }
            return Integer.MAX_VALUE; // エラー時の保険
        }
    }


    public ArrayList<String> getEmployeeCount() {
        ArrayList<String> employeeCounts = getAttributeList("employeeCount");
        return sortEmployeeCounts(employeeCounts);
    }

    private ArrayList<String> sortEmployeeCounts(ArrayList<String> counts) {
        // ソート用のリストを作成
        List<EmployeeRange> ranges = new ArrayList<>();
        for (String count : counts) {
            ranges.add(new EmployeeRange(count));
        }

        // 最小値を基準にソート
        ranges.sort(Comparator.comparingInt(EmployeeRange::getMin));

        // 元の形式に戻す
        ArrayList<String> sortedCounts = new ArrayList<>();
        for (EmployeeRange range : ranges) {
            sortedCounts.add(range.getOriginalText());
        }

        return sortedCounts;
    }

    // 範囲を解析するクラス
    private class EmployeeRange {
        private String originalText;
        private int min;

        public EmployeeRange(String text) {
            this.originalText = text;
            this.min = parseMinValue(text);
        }

        public String getOriginalText() {
            return originalText;
        }

        public int getMin() {
            return min;
        }

        private int parseMinValue(String text) {
            Pattern pattern = Pattern.compile("(\\d+|～)"); // 数字または "～" を取得
            java.util.regex.Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                String minStr = matcher.group(1);
                if (minStr.equals("～")) {
                    return 0; // "～" の場合は最小値を0に設定
                }
                // "万人" を考慮
                if (text.contains("万人")) {
                    return Integer.parseInt(minStr) * 10000;
                }
                return Integer.parseInt(minStr);
            }
            return Integer.MAX_VALUE; // エラー時の保険
        }
    }

    public ArrayList<String> getlocation() {
        return getAttributeList("locate");
    }

    private ArrayList<String> getAttributeList(String label) {
        HashSet<String> attributeSet = new HashSet<>();
        for (Node node : nodes) {
            ArrayList<Link> arriveAtMeLinks = node.getArriveAtMeLinks();
            for (Link link : arriveAtMeLinks) {
                if (label.equals(link.getLabel())) {
                    attributeSet.add(link.getHead().getName());
                }
            }
        }
        return new ArrayList<>(attributeSet);
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

    // クエリを実行し、結果を文字列で返す
    public ArrayList<String> query(ArrayList<Link> queries) {
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
        return companyList;
    }

    // 単一のクエリを実行する
    public List<Map<String, String>> queryLink(Link theQuery) {
        List<Map<String, String>> bindingsList = new ArrayList<>();
        List<Link> links = getLinks();
        Matcher matcher = new Matcher(this); // SemanticNetの参照を渡す

        // まずは直接マッチするリンクを探す
        for (Link link : links) {
            Map<String, String> bindings = new HashMap<>();
            if (matcher.matching(theQuery.getFullName(), link.getFullName(), bindings)) {
                bindingsList.add(bindings);
            }
        }

        // 直接マッチがない場合はis-a継承を試す
        if (bindingsList.isEmpty()) {
            bindingsList = queryWithInheritance(theQuery);
        }

        return bindingsList;
    }

    // is-aリンクによる継承を利用して再クエリする
    private List<Map<String, String>> queryWithInheritance(Link theQuery) {
        List<Map<String, String>> bindingsList = new ArrayList<>();
        Matcher m = new Matcher(this);

        String queryTail = theQuery.getTail().getName();

        // テールが変数なら全ノードを対象に、定数ならそのノードのみ対象にする
        for (Node node : nodes) {
            if (queryTail.startsWith("?") || node.getName().equals(queryTail)) {
                // node から is-a を辿って継承されるリンクを取得
                List<Link> inheritedLinks = getInheritedLinks(node);
                for (Link inheritedLink : inheritedLinks) {
                    Map<String, String> bindings = new HashMap<>();
                    if (m.matching(theQuery.getFullName(), inheritedLink.getFullName(), bindings)) {
                        bindingsList.add(bindings);
                    }
                }
            }
        }

        return bindingsList;
    }

    // ノードからis-aを辿り、上位ノードのリンクを継承する
    private List<Link> getInheritedLinks(Node node) {
        List<Link> inheritedLinks = new ArrayList<>();
        Set<Node> visited = new HashSet<>();
        gatherInheritedLinks(node, inheritedLinks, visited);
        return inheritedLinks;
    }

    private void gatherInheritedLinks(Node node, List<Link> result, Set<Node> visited) {
        if (visited.contains(node)) {
            return;
        }
        visited.add(node);

        // 自ノードの出発リンクのうち、is-a以外の属性リンクを追加
        for (Link l : node.getDepartFromMeLinks()) {
            if (!"is-a".equals(l.getLabel())) {
                result.add(l);
            }
        }

        // 親ノード(is-aで指し示される上位概念)にも同様の処理を再帰的に適用
        ArrayList<Node> parents = node.getISATails();
        for (Node parent : parents) {
            gatherInheritedLinks(parent, result, visited);
        }
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

    public boolean isA(String child, String parent) {
        if (child.equals(parent)) return true; // 同じならtrue
        Node childNode = nodesNameTable.get(child);
        if (childNode == null) return false;

        Set<Node> visited = new HashSet<>();
        return isARecursive(childNode, parent, visited);
    }

    private boolean isARecursive(Node node, String parent, Set<Node> visited) {
        if (node.getName().equals(parent)) return true;
        if (!visited.add(node)) return false; // ループ防止

        // 親概念をたどる
        for (Node p : node.getISATails()) {
            if (isARecursive(p, parent, visited)) {
                return true;
            }
        }
        return false;
    }

}