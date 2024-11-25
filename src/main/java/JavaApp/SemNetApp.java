package JavaApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;

/**
 * SemNetAppクラス
 */
public class SemNetApp {
    public static void main(String[] args) {
        // H2データベースの初期化
        Database.initializeDatabase();

        // Thymeleafのテンプレート設定
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");

        // TemplateEngineの設定
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        // JavalinにThymeleafを登録
        JavalinRenderer.register(new JavalinThymeleaf(templateEngine), ".html");

        // Javalinアプリの作成
        Javalin app = Javalin.create().start(7000);

        app.get("/JavaApp", ctx -> {
            Map<String, Object> model = new HashMap<>();
            String queryStr = ctx.queryParam("queryStr");
            SemanticNet sn = new SemanticNet();
            sn.DeleteAllLinks();
            if (sn.isEmpty()) {
                sn.addInitialLinks();
            }

            model.put("sn", sn);
            //カテゴリと所在地の選択肢を取得して送信
            model.put("categoryOptions", sn.getcategory());
            model.put("locationOptions", sn.getlocation());
            if (queryStr != null) {
                ArrayList<Link> query = strToQuery(queryStr);
                ArrayList<String> CompanyList = sn.query(query);
                //カンパニーリストにある会社の名前からgetURLメソッドを用いて，会社名とURL，会社補足情報の情報を保持するCompanyオブジェクトを作成しそのリストを結果としてプットする
                ArrayList<Company> result = new ArrayList<>();
                for (String company : CompanyList) {
                    result.add(new Company(company, sn.getURL(company), sn.getSupplement(company)));
                }
                model.put("result", result);
            } else {
                queryStr = "?x is-a ?y\n?y donot ?z";
            }
            //クエリを送信
            model.put("query", queryStr);
            ctx.render("/JavaApp.html", model);
        });

        app.post("/JavaApp", ctx -> {
            Map<String, Object> model = new HashMap<>();
            String selectedOption = ctx.formParam("categoryOption");// ユーザーが選択したオプションを取得
            String locationOption = ctx.formParam("locationOption");// ユーザーが選択したオプションを取得
            String queryStr = "";

            if (selectedOption != null && !selectedOption.isEmpty()) {
                queryStr = "?x is-a " + selectedOption;  // 選択されたオプションでqueryStrを作成
            }

            if (locationOption != null && !locationOption.isEmpty()) {
                queryStr = queryStr + "\n" + "?x locate " + locationOption;  // 選択されたオプションでqueryStrを作成
            }

            SemanticNet sn = new SemanticNet();
            sn.DeleteAllLinks();
            if (sn.isEmpty()) {
                sn.addInitialLinks();
            }

            model.put("sn", sn);
            model.put("categoryOptions", sn.getcategory());
            model.put("locationOptions", sn.getlocation());
            model.put("categoryOption", selectedOption);
            model.put("locationOption", locationOption);

            if (queryStr != null) {
                ArrayList<Link> query = strToQuery(queryStr);
                ArrayList<String> CompanyList = sn.query(query);
                //カンパニーリストにある会社の名前からgetURLメソッドを用いて，会社名とURL，会社補足情報の情報を保持するCompanyオブジェクトを作成しそのリストを結果としてプットする
                ArrayList<Company> result = new ArrayList<>();
                //結果がない場合は結果なしと表示
                if(!(CompanyList ==null)){
                    for (String company : CompanyList) {
                        result.add(new Company(company, sn.getURL(company), sn.getSupplement(company)));
                        //会社概要をターミナルに表示
                        System.out.println(sn.getSupplement(company));
                    }
                }else{
                    result.add(new Company("結果なし", "http://localhost:7000/JavaApp", "結果なし"));
                }
                model.put("result", result);
            }
            model.put("query", queryStr);
            ctx.render("/JavaApp.html", model);
        });
    }

    private static ArrayList<Link> strToQuery(String queryStr) {
        ArrayList<Link> query = new ArrayList<>();
        if (queryStr != null) {
            String[] lines = queryStr.split("\n");
            for (String line : lines) {
                line = line.trim();
                String[] tokens = line.split("\\s+");
                if (tokens.length == 3) {
                    query.add(new Link(tokens[1], tokens[0], tokens[2], new SemanticNet()));
                }
            }
        }
        return query;
    }
}


