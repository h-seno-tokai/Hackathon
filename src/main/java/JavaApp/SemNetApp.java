package JavaApp;

import io.javalin.http.staticfiles.Location;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.ArrayList;

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

        // Javalinアプリケーションを作成
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/templates/Album", Location.CLASSPATH); // 静的ファイルの設定
        }).start(7000);

        // ルーティングを設定
        AppRouter.setupRoutes(app);
    }

    /**
     * クエリ文字列をLinkオブジェクトのリストに変換
     */
    public static ArrayList<Link> strToQuery(String queryStr) {
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
