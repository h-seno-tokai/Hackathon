package JavaApp;

import io.javalin.Javalin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * アプリケーションのルーティングを担当するクラス
 */
public class AppRouter {
    public static void setupRoutes(Javalin app) {
        app.get("/JavaApp", ctx -> {
            Map<String, Object> model = new HashMap<>();
            String queryStr = ctx.queryParam("queryStr");

            SemanticNet sn = new SemanticNet();
            sn.DeleteAllLinks();
            if (sn.isEmpty()) {
                sn.addInitialLinks();
            }

            model.put("sn", sn);
            model.put("categoryOptions", sn.getcategory());
            model.put("locationOptions", sn.getlocation());
            model.put("salaryOptions", sn.getAverageSalary());
            model.put("employeeOptions", sn.getEmployeeCount());


            if (queryStr != null) {
                ArrayList<Link> query = SemNetApp.strToQuery(queryStr);
                ArrayList<String> companyList = sn.query(query);

                ArrayList<Company> result = new ArrayList<>();
                for (String company : companyList) {
                    result.add(new Company(company, sn.getURL(company), sn.getSupplement(company), sn.getPicturePath(company)));
                }
                model.put("result", result);
            } else {
                queryStr = "?x is-a ?y\n?y donot ?z";
            }

            model.put("query", queryStr);
            ctx.render("/JavaApp.html", model);
        });

        app.post("/JavaApp", ctx -> {
            Map<String, Object> model = new HashMap<>();
            String selectedOption = ctx.formParam("categoryOption");
            String locationOption = ctx.formParam("locationOption");
            String salaryOption = ctx.formParam("salaryOption");
            String employeeOption = ctx.formParam("employeeOption");
            //取得した選択肢をウェブに送り返す．
            model.put("categoryOption", selectedOption);
            model.put("locationOption", locationOption);
            model.put("salaryOption", salaryOption);
            model.put("employeeOption", employeeOption);
            String queryStr = "";

            if (selectedOption != null && !selectedOption.isEmpty()) {
                queryStr = "?x is-a " + selectedOption;
            }
            if (locationOption != null && !locationOption.isEmpty()) {
                queryStr += "\n?x locate " + locationOption;
            }
            if (salaryOption != null && !salaryOption.isEmpty()) {
                queryStr += "\n?x averageSalary " + salaryOption;
            }
            if (employeeOption != null && !employeeOption.isEmpty()) {
                queryStr += "\n?x employeeCount " + employeeOption;
            }

            SemanticNet sn = new SemanticNet();
            sn.DeleteAllLinks();
            if (sn.isEmpty()) {
                sn.addInitialLinks();
            }

            model.put("sn", sn);
            model.put("categoryOptions", sn.getcategory());
            model.put("locationOptions", sn.getlocation());
            model.put("salaryOptions", sn.getAverageSalary());
            model.put("employeeOptions", sn.getEmployeeCount());

            ArrayList<Link> query = SemNetApp.strToQuery(queryStr);
            ArrayList<String> companyList = sn.query(query);

            ArrayList<Company> result = new ArrayList<>();
            if (companyList != null) {
                for (String company : companyList) {
                    result.add(new Company(company, sn.getURL(company), sn.getSupplement(company), sn.getPicturePath(company)));
                }
            } else {
                result.add(new Company("結果なし", "http:/localhost:7000/JavaApp", "結果なし", ""));
            }

            model.put("result", result);
            model.put("query", queryStr);

            ctx.render("/JavaApp.html", model);
        });

        app.get("/JavaApp/Ikkyu", ctx -> ctx.render("/hyperlink.html"));
        app.post("/JavaApp/Ikkyu", ctx -> ctx.render("/hyperlink.html"));
    }
}
