# ベースイメージにAmazon Correttoを使用
FROM amazoncorretto:17.0.11

# 作業ディレクトリを設定
WORKDIR /app

# JARファイルをコンテナにコピー
COPY target/SemNetApp-1.0-SNAPSHOT.jar /app/SemNetApp.jar

# 必要なポートを公開
EXPOSE 7000

# アプリケーションを実行
CMD ["java", "-jar", "SemNetApp.jar"]
