# Childcare Matching App

保育士と保育園をマッチングするためのWebアプリケーションのバックエンドです。  
Spring Boot を用いて構築されています。

## 主な機能

- 保育士・保育園のユーザー登録・認証
- 求人情報の投稿・検索・詳細表示
- お気に入り登録
- 面接管理
- 管理者によるユーザー・求人管理
- プッシュ通知機能
- CSVエクスポート

## ディレクトリ構成

```
src/main/java/com/matchingapp/
  ├── config/         # セキュリティ設定
  ├── controller/     # 各種コントローラー
  ├── entity/         # エンティティ（DBモデル）
  ├── repository/     # リポジトリ（DBアクセス）
  ├── service/        # サービス層
src/main/resources/
  ├── static/         # 静的ファイル（CSS, JS等）
  ├── templates/      # Thymeleafテンプレート
  ├── application.properties # 設定ファイル
  ├── schema.sql      # DBスキーマ
  ├── data.sql        # 初期データ
```

## セットアップ方法

### 前提

- Java 17 以上
- Maven 3.8 以上
- MySQL などのRDBMS（H2等でも可）

### ビルド & 実行

1. リポジトリをクローン
    ```sh
    git clone <このリポジトリのURL>
    cd childcare-matching-app
    ```

2. データベース設定  
   `src/main/resources/application.properties` を編集し、DB接続情報を設定してください。

3. ビルド
    ```sh
    mvn clean package
    ```

4. アプリケーション起動
    ```sh
    mvn spring-boot:run
    ```
    または
    ```sh
    java -jar target/childcare-matching-app-0.0.1-SNAPSHOT.jar
    ```

5. ブラウザでアクセス  
   `http://localhost:8080/` でアプリケーションにアクセスできます。

## 主要なエンドポイント

- `/login` : ログイン画面
- `/register_nurse` : 保育士登録
- `/register_nursery` : 保育園登録
- `/dashboard` : ユーザーダッシュボード
- `/admin/*` : 管理者用画面

## 画面遷移図

```mermaid
flowchart TD
  A["ログイン画面 (/login)"] -->|"ログイン成功"| B["ダッシュボード (/dashboard)"]
  B --> C["求人検索 (保育士) (/nurse_job_search)"]
  B --> D["求人投稿 (保育園) (/nursery_job_posting_form)"]
  B --> E["お気に入り一覧 (保育士) (/nurse_favorites)"]
  B --> F["面接一覧 (保育士/保育園) (/nurse_interview_list, /nursery_interview_list)"]
  B --> G["マイページ (保育士/保育園) (/nurse_my_page, /nursery_my_page)"]
  B --> H["管理者画面 (/admin/*)"]
  C --> I["求人詳細 (/nurse_job_detail)"]
  I --> E
  D --> J["求人管理 (保育園) (/nursery_job_posting_management)"]
  G --> K["プロフィール編集 (/nurse_profile_edit, /nursery_profile_edit)"]
  H --> L["ユーザー管理 (/admin_user_management)"]
  H --> M["求人審査 (/admin_job_posting_review)"]
```

## ER図

```mermaid
erDiagram
  USER {
    int id PK
    string name
    string email
    string password
    string role
  }
  NURSERY {
    int id PK
    string name
    string address
    string phone
  }
  JOB_POSTING {
    int id PK
    int nursery_id FK
    string title
    string description
    date posted_at
  }
  FAVORITE {
    int id PK
    int user_id FK
    int job_posting_id FK
  }
  INTERVIEW {
    int id PK
    int user_id FK
    int job_posting_id FK
    date scheduled_at
    string status
  }
  QUALIFICATION {
    int id PK
    string name
  }
  USER_QUALIFICATION {
    int id PK
    int user_id FK
    int qualification_id FK
  }
  WORK_EXPERIENCE {
    int id PK
    int user_id FK
    string workplace
    date start_date
    date end_date
  }
  NOTICE {
    int id PK
    string title
    string content
    date posted_at
  }
  ADMIN {
    int id PK
    string name
    string email
    string password
  }
  USER ||--o{ FAVORITE : "has"
  USER ||--o{ INTERVIEW : "has"
  USER ||--o{ USER_QUALIFICATION : "has"
  USER ||--o{ WORK_EXPERIENCE : "has"
  NURSERY ||--o{ JOB_POSTING : "posts"
  JOB_POSTING ||--o{ FAVORITE : "is favorited in"
  JOB_POSTING ||--o{ INTERVIEW : "is applied in"
  QUALIFICATION ||--o{ USER_QUALIFICATION : "is held by"
```

## ライセンス

このプロジェクトはMITライセンスの下で公開されています。 

---

## 🛠️ 技術スタック

| 種類         | 使用技術・サービス         |
|:-------------|:--------------------------|
| 言語         | Java ![Java](https://img.shields.io/badge/Java-95%25-blue?logo=java) <br> HTML ![HTML](https://img.shields.io/badge/HTML-5%25-orange?logo=html5) |
| フレームワーク | Spring Boot, Thymeleaf    |
| DB           | PostgreSQL, H2 (開発用)   |
| ビルド       | Maven                     |
| その他       | JPA, Spring Security, etc.|

---

## 🤝 コントリビューション

Pull Request・Issueは大歓迎です！  
バグ報告・機能提案・ドキュメント修正など、どなたでもお気軽にご参加ください。

1. Issueを立ててください
2. Forkしてブランチを作成
3. コード修正後、Pull Requestを送信

---

## 📬 連絡先

- 作者: [吉永　歩]
- お問い合わせ: [yay2024@llc-yay.com]
- [GitHub Issues](https://github.com/yoshiayu/childcare-matching-app/issues) からもご連絡ください

--- 
