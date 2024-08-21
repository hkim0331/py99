# py99/CHANGELOG.md

## Unreleased
### docker
- docker で make uberjar にひどく時間がかかる。CPU に負荷の印はない。
  macos との共有ボリュームが遅い。マウントの仕方ではない。2023-10-08
  モジュールを入れ替えても vulnerable は変わらない．
- wheel 0.37.1 ->0.38.1
- setuptools 59.6.0 -> 65.5.2
https://forums.docker.com/t/docker-desktop-shows-image-has-a-python-wheel-vulnerability-even-though-it-has-been-updated/135996/3
- pip install wheel を Dockerfile で実施しても、
  Docker Desktop が表示する Vulnerabilities は変わらない。
  clojure:temurin-20:lein を入れても二つのパッケージが残る。
    - wheel 0.37.1
    - setuptools 59.6.0
- 積極的に pip uninstall したらどうか？
- pip よりも apt install python3-<module> で．
### clojure/luminus
- Namespace hiccup.core is deprecated since 2.0.
- log が思ったように出せない。vscode のターミナルから http 打った時は出ないが、
  外部ブラウザで URL を探るとログを出す。
### code
- login 中ユーザのリスト。logout したら削除する。
  logout せずにブラウズクローズしたら削除できない。
- /todays: go ボタンを押さずに return-key で go できないか？
- テストに通った回答を受け取ったらダイアログ「他ユーザの回答を読むべし」を出す。
- コードをカラフルに表示する。
- test code, assert インデント4に変更
- html/show_list.html
  テンプレート化を進める。テンプレートに渡すベクタをclj 側で細工する。
  selmer はループを回るだけにする。
- REFACTOR: s ポイント関連が home.clj と services.clj の二箇所にある。
- FIXME: py99.grading:updated コラムにタイムスタンプを入れる。
- auto-reload => meta ヘッダを書けばいい。
  そうするとログインが切れることがない？回答中にリロードされるのは嫌だろ。
### exercices
- ChatGPT 対策、間違い修正問題では？

## v1.0-SNAPSHOT
- updated libraries.

| :file       | :name                          | :current | :latest |
|------------ | ------------------------------ | -------- | --------|
| project.clj | ch.qos.logback/logback-classic | 1.4.14   | 1.5.7   |
|             | cheshire/cheshire              | 5.12.0   | 5.13.0  |
|             | cider/cider-nrepl              | 0.44.0   | 0.49.3  |
|             | cprop/cprop                    | 0.1.19   | 0.1.20  |
|             | hato/hato                      | 0.9.0    | 1.0.0   |
|             | jonase/eastwood                | 1.4.2    | 1.4.3   |
|             | markdown-clj/markdown-clj      | 1.11.7   | 1.12.1  |
|             | metosin/muuntaja               | 0.6.8    | 0.6.10  |
|             | metosin/reitit                 | 0.6.0    | 0.7.1   |
|             | metosin/ring-http-response     | 0.9.3    | 0.9.4   |
|             | mount/mount                    | 0.1.17   | 0.1.18  |
|             | nrepl/nrepl                    | 1.1.0    | 1.3.0   |
|             | org.clojure/clojure            | 1.11.1   | 1.11.4  |
|             | org.clojure/tools.cli          | 1.0.219  | 1.1.230 |
|             | org.clojure/tools.logging      | 1.2.4    | 1.3.0   |
|             | org.clojure/tools.namespace    | 1.4.4    | 1.5.0   |
|             | org.postgresql/postgresql      | 42.7.1   | 42.7.3  |
|             | org.webjars.npm/bulma          | 0.9.4    | 1.0.1   |
|             | org.webjars/webjars-locator    | 0.50     | 0.52    |
|             | ring-webjars/ring-webjars      | 0.2.0    | 0.3.0   |
|             | ring/ring-core                 | 1.11.0   | 1.12.2  |
|             | ring/ring-defaults             | 0.4.0    | 0.5.0   |
|             | ring/ring-devel                | 1.11.0   | 1.12.2  |
|             | selmer/selmer                  | 1.12.59  | 1.12.61 |



## 0.94.905 / 2024-08-21
- docker postgres:14.11

## 0.93.888 / 2024-03-06
### Added
- /api/pt/:login

## 0.92.883 / 2024-02-23
期末テストを総合してキー :et で渡す．

## 0.91.879 / 2024-02-23
### Added
post をトークン付きに．
- post /api/py99! {:login "login" :col "py99" :pt 90 :secret ""}
### Removed
- post /api/py99/:login/:pt 他を廃止した．

## 0.90.874 / 2024-02-23
### Changed
- post /api/goal/:login/:pt
- post /api/seven-four/:login/:pt
- home.clj stop validation

## 0.90.868 / 2024-02-18
アップデートは grading の仕事．
- GET /api/goal/:login
- POST /api/goal/:login
- db/update は戻り値をチェックした方がいい．
- `alter table gradings rename e6 to seven_four;`
- POST /seven-four/:login

## 0.89.861 / 2024-02-18
- POST /api/py99/:login/:pt
- POST /api/comm/:login/:pt
### Fixed
- /home/list-todays-today `today` must be `(today)`.

## 0.88.854 / 2024-02-17
- GET /api/py99/:login
- GET /api/comm/:login
### Fixed
created `py99.clj`, defined following functions.
- (before? s1 s2)
- (count-up m)
- (bin-count data bin)

## 0.87.849 / 2024-02-17
### 闇が深くなった．DockerDesktop にバグだったか？
```
2024-02-17 09:19:25,918 [XNIO-1 task-3] DEBUG py99.routes.home - ret {:exit 0, :out ============================= test session starts ==============================
platform linux -- Python 3.10.12, pytest-6.2.5, py-1.10.0, pluggy-0.13.0
rootdir: /tmp
collected 1 item

../../../tmp/python13747273597146482577.py .                             [100%]

============================== 1 passed in 0.00s ===============================
```
### summary
むやみにコンテナしなければいいか．
Dockerfile を python3 python3-pytest black 入りに戻して，
コンテナのバージョンタグは hkim0331/py99:0.6.4
しばらくコンテナ外開発を続けよう．
### why? docker からエスケープすると起動するのはホストのコマンドなのか？
macos の docker container で，lein repl から (start) したプロセス，
エスケープして python3 を実行すると呼び出されるのは macos の python3.
しかし，linux のコンテナはコンテナ内の python3 を探す．
どっちが正しいかっつうと linux と思うが，動作が違っちゃうのが良くない．
- /api/points/:login
- docker image hkim0331/py99:0.5.2
```
  apt install python3-pytest (not pip3 install)
```
- host の python を起動している．
```
2024-02-16 22:56:17,407 [XNIO-1 task-2] DEBUG py99.routes.home - ret {:exit 1, :out , :err /opt/homebrew/opt/python@3.11/bin/python3.11: No module named pytest
, :timeout false}
```
- nuc.local ではコンテナ内の python3 を探すようだ.

## 0.86.841 / 2024-02-16
- re-re-exam
  py99.gradings に書き足す re-re-results.sql を gradings プロジェクトで作成，
  app.melt の pg に流す．
  py99をm3を見て中再々に表示するように書き足す(これ，0.86)
- bump-version.sh uses '/' instead of '-' for a separator.

## 0.85.831 - 2024-02-01
### Removed
- stop using Black. Difference of versions of black, 2023.06 in Windows,
  2l.12 in ubuntu-jammy, leads some stresses among students.
- black21.12 insists "x**y" should be "x ** y".
  However, black-24.1.1 does not. and black-24.1.1 requires python > 3.11.
  It is not a safe simple job to install 3.11 on ubuntu-jammy.
  So, this is a dirty hack. 2024-01-30

```clojure
(defn- spaces-around-**
  "x**y => x ** y"
  [s]
  (str/replace s #"\*\*" " ** "))
```

## 0.85.819 - 2024-01-30
### Updated
- validation black.
```clojure
(black-test (remove-comments answer))
```
- docker-compose.yml: postgres:14.10
- FROM clojure:temurin-21-lein-jammy
- added `black` in Dockerfile
- copied a part of `bump-version.sh` from `python-book/bump-version.sh`.

## 0.84.8 - 2024-01-08
- allow `if` for self doctest execution.
- add numpy using poetry.
```shell
m24% poetry add numpy
  • Installing numpy (1.26.3)
  • Updating pytest (7.4.2 -> 7.4.4)
```

## 0.84.7 - 2024-01-05
- insert gradings:updated manually. also at app.melt:py99.
- libraries updated.
```sql
update py99.gradings set updated=now();
```

| :file       | :name                               | :current | :latest |
| ----------- | ----------------------------------- | -------- | ------- |
| project.clj | ch.qos.logback/logback-classic      | 1.4.11   | 1.4.14  |
|             | cider/cider-nrepl                   | 0.38.1   | 0.44.0  |
|             | clojure.java-time/clojure.java-time | 1.3.0    | 1.4.2   |
|             | jonase/eastwood                     | 1.4.0    | 1.4.2   |
|             | nrepl/nrepl                         | 1.0.0    | 1.1.0   |
|             | org.postgresql/postgresql           | 42.6.0   | 42.7.1  |
|             | org.webjars/webjars-locator         | 0.47     | 0.50    |
|             | ring/ring-core                      | 1.10.0   | 1.11.0  |
|             | ring/ring-devel                     | 1.10.0   | 1.11.0  |

## 0.84.6 - 2024-01-05
- /api/poings/:login

## 0.84.5 - 2024-01-05
### Changed
- profile.html midterm, re-exam の結果掲載。
  (dp/points? {:login "login"}) で grading のデータは取得できた。

## 0.84.4 - 2024-01-02
### Changed
- display order of submissions. recent top, old bottom.
  - home/submissions (reverse submissions)
  - submissios.html <ol reversed>

## 0.83.3 - 2024-01-01
- made links to prev/next problem from `comment-form.html` and `answer-form.html`.

## 0.83.2 - 2023-12-30
- global var を許していない。
  `g_` のプレフィックスだけ、許そうか。
```clojure
(defn- starts-with-def-import-from-indent?
  [s]
  (or (str/starts-with? s " ")
      (str/starts-with? s "#")
      (str/starts-with? s "\t")
      (str/starts-with? s "def")
      (str/starts-with? s "from")
      (str/starts-with? s "import")
      (str/starts-with? s "g_")))
```

## 0.83.1 - 2023-12-29
- バージョン番号つけ間違い。

## 0.83.0 - 2023-12-29
- 実行文がついている回答を受理しない。

## 0.82.0" - 2023-12-27
- 同じ問題にdocstring だけ違う同じ回答をつけたら "wrong answer" を表示し、受け付けない。

## 0.81.2 - 2023-12-24
- FIXME: home/filte はなぜ、assoc-in を2回？
- ログイン画面のフィルタ入力を止める。

## 0.81.1 - 2023-12-23
### Added
- /api/s/:login/:date
- filter の設定を /login 以外に / に追加。
### Changed
- weeks, period を home.clj から config.clj に移動した。

## 0.80.1 - 2023-12-21
### Added
- recent submissions にも 0.80.0 フィルタを適用する。

## 0.80.0 - 2023-12-21
### Added
- filter. こいつの投稿は見たくないってのを login 時に指定する。
  session に 追加。
```clojure
(assoc-in [:session :filter] filter)
```

## 0.85.819 - 2024-01-30
## 0.85.819 - 2024-01-30
- develop:/logins がエラーはどうしてか？
  REPL から (get-user) だと log フォルダの位置がわからないのでは？
  エラーに対するコードの不備もある。
- /todays-login
  record logined user. now `nobody`s were recorded.

### Changed
- profile.html: section title `day by day submissions`

## 0.79.2 - 2023-12-17
### Changed
- link activities from profile page
- default today in /answers
### Removed
- remove show-comments link from /comments-sent
### Added
- define (today) in home.clj
```
(defn- today []
  (to-date-str (str (l/local-now))))
```

## 0.79.1 - 2023-12-16
- provide default user in show one's comments.

- stop show today's activites in profile page.

## 0.79.0 - 2023-12-16
- Back link to resources/html/admin.html
- show one's submissios to resources/html/comment-form.html

## 0.78.0 - 2023-12-14
admin ページからユーザ login の回答、コメントの一覧表示。
### Added
- home/submissions
- queries:db/answers-by-login
### Changed
- comments-set.html, date:shortDateTime

## 0.77.0 - 2023-12-10
- /s-point, /s-point/:login
- a_syouko09's answer 2023-12-05 13:51:46,
```
こういうのはどうですか？0以外の数を足したやつかける(6-0の日数)

[0,3,0,0,2,0,3] => (3+2+3)*(6-4) =16
[0,0,6,0,0,0,0] => 6*(5-5)=0
これだと毎日する人が点数が高くなると思うのでいいと思います。
```

## 0.76.2 - 2023-12-06
### Changed
- status.html/recent submissions
  {{a.login}}</a>-{{a.num}} が見ずらいので{{a.login}}</a>#{{a.num}}に変更。
- comment-form.html
  form to login's comments below `comments` section.

## 0.76.1 - 2023-11-25
### Added
- `bb.edn` and `bb/midterm.clj`.
  create/drop 211~244 entries in py99.problems for midterm-exam.
- container にも babashka
### Removed
- db-dumps/midterm-exam-{down,up}.sh

## 0.76.0 - 2023-11-25
exam-mode 再チェック
### Changed
- resources/html/{comment-form,answer-form}.html
### Added
- db-dumps/midterm-exam-{down,up}.sh

## 0.75.0 - 2023-11-24
### Added
- routes/home: remove-docstrings
  回答中のdocstring(複数)を削除する。

## 0.74.1 - 2023-11-20
### Changed
- resources/html/comments-sent.html: abbreviat:19 -> abbrebiate:40
### Added
- resources/html/comments-count.html: form to get '/comments-sent'
- src.routes/home.clj:/comments-sent
  すでに /comments-sent/:login エンドポイントは作成済み。
  あらたに作ったのは form parameters を取るエンドポイント。

## 0.73.6 - 2023-10-24
- home/create-anwer! resume redirect
```clojure
  (redirect (str "/answer/" num))
```

## 0.73.5 - 2023-10-22
- bug fixed: create-stock!:db-action
- adjust class chart scale
- admin/users activities にデフォルトで本日の日付

## 0.73.4 - 2023-10-21
### fixed
- /profile/login に login が渡っていない。
  profile を request を引数にするように戻し、admin から呼ばれるときは
  :user キーを request マップに足して呼ぶように変更した。

## 0.73.3 - 2023-10-21
- user activities in admin page
- チャート： 2022 に準じる倍率に戻す。

## 0.73.2 - 2023-10-21
- stock も activities に追加。
- comment! 等は視覚的に弱いので commemt(!) 等に変更。

## 0.73.1 - 2023-10-20
-  FIXME: nobody profile になっている。
```
  32 | nobody   | profile  |   0 | 2023-10-20 22:31:00.162587
```
### Changed
- profile, profile-self: login ではなく、request を引数に取るように。
  0.73 に合わせて。

## 0.73 - 2023-10-20
- layout/render と home/create-answer!, home/create-comment! から
  db/actions! でアクションを記録
- 記録したアクションは profile に表示。
- create actions table
```sql
create table actions (
  id SERIAL PRIMARY KEY,
  login varchar(20),
  action varchar(100),
  num int,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
```

## 0.72.0 - 2023-10-19
- 関数コメントない回答を弾く。

## 0.71.11 - 2023-10-15
- py99.routes.home/has-docstring-test
  if source has docstring, returns nil or raise exception(throw).

## 0.71.10 - 2023-10-15
- create-answer! 後, qa.melt にリダイレクト。
- コンテナの時刻が UTC: docker-compose.yml 中に
```
  environment:
    TZ: Asia/Tokyo
```

## 0.71.9 - 2023-10-13
- `#include nnn` の他に、`# include nnn`(include の前にスペース)、
  `# incude nnn # コメント` を許す。

## 0.71.8 - 2023-10-10
- Answers バーの長さを 1.5
## 0.71.7 - 2023-10-08
### Changed
- Old comments => Comments to `num`
- バーの長さを二倍に。Answers, Comments, rankings.
- stock に confirm.
```
<form action="/stock" method="post" onSubmit="return ok()">
```
### Added
- git-flow in Dockerfile

## 0.71.6 - 2023-10-08
### Changed
- profile.html 2023 バージョンに。
- `Comments` menu on nav bar.
- new endpoint /comments-count
- defined (comments-count request)
- defined db/comments-count-by-number

## 0.71.5 - 2023-10-08
- Todays を Answers からたどるように変更した。

## 0.71.4 - 2023-10-08
- link from answer by problems page to problems.

## 0.71.3 - 2023-10-07
- FIXED devcontainer で pytest を起動できるようになった
  hkim0331/py99:0.4.2

## 0.71.2 - 2023-10-07
- reverse count-answers

## 0.71.1 - 2023-10-07
- screen.css/.mono {fonto-size: normal;}

## 0.71.0 - 2023-10-06
- answers textarea のフォントを等幅に変更した。answer-form.html
- seed problems ボタンを disable した。admin.html, disabled=disabled
- todays の並びを submits name に変更した。todays.html
- Answers by 等の横棒に色をつけた。

## 0.70.0 - 2023-10-05
- bump-version.sh: 日付を about.html に供給する。
- db-dumps/reset.sh: テーブルをリセットする。

## 0.69.0 - 2023-10-04
- upgraded libraries

| :file       | :name                     | :current | :latest |
| ----------- | ------------------------- | -------- | ------- |
| project.clj | cheshire/cheshire         | 5.11.0   | 5.12.0  |
|             | cider/cider-nrepl         | 0.37.1   | 0.38.1  |
|             | markdown-clj/markdown-clj | 1.11.4   | 1.11.7  |

### Changed
- admin page two columns

## 0.68.9 - 2023-10-04
- 99題に絞った。
- /api/problem/:n
- devcontainer extensions:
  - "betterthantomorrow.calva",
  - "jsynowiec.vscode-insertdatestring",
  - "rkwan94.vscode-emacs-improved"
- CHANGELOG中のテーブルをマークダウンのテーブルで書き直した。

## 0.68.8 - 2023-09-21
- refactor.
- resources/doc/problems.edn, problems-after-100.edn.

## 0.68.7 - 2023-09-21
### Changed
improve `\admin` page.
- redirect with flash
- edit/dump/seed(restore?)
- monospace font in `test` textarea

## 0.68.6 - 2023-09-21
### Added
poetry for local development
- pyproject.toml
- poetry.lock
- .venv (gitignored)

```shell
% pip list
Package           Version
----------------- -------
contourpy         1.1.1
cycler            0.11.0
fonttools         4.42.1
iniconfig         2.0.0
kiwisolver        1.4.5
matplotlib        3.8.0
numpy             1.25.2
packaging         23.1
Pillow            10.0.1
pip               23.2.1
pluggy            1.3.0
pyparsing         3.1.1
pytest            7.4.2
python-dateutil   2.8.2
setuptools        68.2.2
setuptools-scm    8.0.1
six               1.16.0
typing_extensions 4.8.0
wheel             0.41.2
```

## 0.68.5 - 2023-09-12
works on m64 devcontainer.

    % cd /home/vscode
    % sudo chown -R vscode:vscode .m2
    % cd /usr/src/app
    % sudo ./restore.sh py99*.sql

## 0.68.4 - 2023-09-12
stock takes  an annotation.
- add note column to stocks table
  (create-migration "stocks")

## 0.68.3 - 2023-09-11
- deploy すると 2023 年版のスケジュールになる。授業開始まで抑制すること。
  最初の週は過去の py99 を見せる。本番は第2週以降。
- navbar admin menu for admin user → やるなら home.clj で。
- exam-mode を分離する。→ 成績見れたほうがいい。
- y-max in graphs -> 分散が大きすぎ。ちょうどのサイズにはならない。
- /admin/ page describe fields
- describe uptime marks
- adjust `weeks` and `period` in home.clj

## 0.68.2 - 2023-09-10
- py99 user's password

      psql> alter role py99 with password '*******'

- r99 のタブが今「Welcome to r99」ですが、
  問題ページを開いている場合例えば40番なら「Welcome to r99-40」など
  何番を解いているタブを開いているか分かるようにしてほしいです！

      <script>document.title = "Py99 -{{problem.num}}-"</script>

## 0.68.1 - 2023-09-10
    % clojure -Tantq outdated :upgrade true :force true

## 0.68.0 - 2023-09-09
- start 2023 version.
- chown -R vscode:vscode /home/vscode/.m2
  コンテナの .m2 のオーナーが root になっていた。
- ひとまず、昨年通り。コンテナでも動くようになった。

## 0.67.2 - 2023-04-30
- re-exam results 公開終わり

## 0.67.1 - 2023-04-29
- もうちょっと加筆。「日曜でこのページは消えます」追加。
## 0.67.0 - 2023-04-29
- 加筆して公開。

## 0.66.0 - 2023-04-28
- 再試験終了。/re-exam-end エンドポイント。

## 0.65.0 - 2023-03-29
ログイン直後に過去1ヶ月のログインログを表示するページ
- clojure -Tantq outdated :upgrade true

## 0.64.0 - 2023-03-05
- updated libraries(reitit 0.6.0, cider-nrepl 0.30.0, postgres 42.5.4)
- updated Dockerfile(include postgresql-client-14)

## 0.63.0 - 2023-02-21
- display result

## 0.62.0 - 2023-02-19
- defined py99.endterm table
- delete "can not firefox" line from login.html

## 0.61.0 - 2023-01-24
- re-re-exam 採点。

## 0.60.0 - 2023-01-18
### Changed
- test mode: 自分の回答は読めるけど、他の人のをクリックしても、自分の回答。

## 0.85.819 - 2024-01-30
- login dev モード。dev で l22 を必要とするのは面倒。

## 0.58.0 - 2023-01-07
use dev-container, docker.
### Changed
- docker-compose.yml: stop exporting ports 5432
- docker-compose.yml: volume mount db-dumps
- home.clj: update `uptime`

## 0.57.1 - 2023-01-07
- uptime on comment-form

## 0.57.0 - 2023-01-06
- display uptime on answer-form.html
```
busy-mark (cond
                    (<= 5 busy) "🔴"
                    (<= 1 busy) "🟡"
                    :else "🟢")
```

## 0.56.0 - 2022-12-25
### Changed
- (def ^:private timeout 30) was 60

## 0.55.0 - 2022-12-25
### updated
- resources/html/midterm.html (copied from py99-aux)
### Changed
- speed up using (doall (pmap ...))
### Fixed
- update-midterm takes num argument, must filter before-12-15?
  but re-exam. feature/re-exam.

## 0.85.819 - 2024-01-30
### Added
- midterm.update! sequentially execute
(db/clear-midterm!) (update-midterm!) (update-re-exam!)

## 0.54.2 - 2022-12-23
- /todays のオーダーを解いた数に。
```
SELECT login, COUNT(login) FROM answers
WHERE DATE(create_at) = DATE(:date)
GROUP BY login
ORDER BY COUNT DESC
```
- /todays に yyyy-mm-dd ではない引数が来たときエラーページを出す。


## 0.54.1 - 2022-12-23
- todays ページに input form, JS でリンク。

## 0.54.0 - 2022-12-23
- feature/todays: yyyy-mm-dd に誰が何題、回答を提出したか。

## 0.53.1 - 2022-12-21
- removed no use codes and comments
- reconsidered exam-mode

## 0.53.0 - 2022-12-21
### Added
- re_exam.clj

## 0.52.1 - 2022-12-15
### FIXED
- forgot expand-include

## 0.52.0 - 2022-12-14
- created midterm.html
- lein 2.10.0 が

## 0.51.1 - 2022-12-12
antq update :upgrade true

## 0.50.1 - 2022-12-10
midterm.html
* FIXME: 自分(hkimura)の回答が/midterm から見えない。


## 0.85.819 - 2024-01-30
- exam-mode: 試験中は自分の回答しかブラウズできない
- midterm 自動採点
- namespace を一気に読み込む calva のキーは？ alt+ctl+c+enter
- testcode を流し込むスクリプトを別プロジェクト py99-aux に作成
- hkimura answers を流し込むスクリプトを py99-aux に作成

## 0.49.1 - 2022-12-07
feature midterm. テスト終わる前には公開しない。
### Added
- src/clj/py99/services.clj
- src/clj/py99/midterm.clj
- resources/html/midterm.html
### Changed
- src/clj/py99/home.clj: added /midterm
- make home/pytest-test public

## 0.48.0 - 2022-12-01
### Added
- `export EXAM_MODE=false` false は小文字。
### Changed
- routes.home - comment-form を layout に一本化して problem ナンバーを表示する。
- log がダブるのはスクリプトからリダイレクトが原因。
```
# bad
java -jar py99.jar >> log/py99.jar
# good
java -jar py99.jar
```

## 0.47.4 - 2022-11-29
- change words: `group assignment` -> `exam submissions`

## 0.47.3 - 2022-11-29
- シンプルに 200 番以上の回答を見せない。

## 0.47.2 - 2022-11-28
- answer-form.html: 動作確認してから submit すること。
  関数コメント(doc string)のない回答は基本的にコメントしない。間違いあっても指摘しないってこと。

## 0.47.1 - 2022-11-27
- VScode のバッファ上書き問題。これ、なんとか抑え込めないか。

## 0.47.0 - 2022-11-27
- #'py99.config/env と (env) を混同しないように。
- 復活 r99c で使ってた self-only を exam-mode として変更し採用。

## 0.46.2 - 2022-11-22
### Changed
- Integer/ParseInt を home/get-answer から home/expand-iclude へ移動
- get-answer が回答を見つけられないときは例外を投げる
- expand-includes は #include の後に数字が見つからないときは例外を投げる

## 0.46.0 - 2022-11-21
### Added
- expand-includes (not yet test recursive actions)

## 0.45.3 - 2022-11-19
### Added
- /admin/problems#{num} で num へジャンプ。

## 0.45.2 - 2022-11-15
## 0.45.1 - 2022-11-15
### Changed
- ユーザごとにコメントをストックできる

## 0.45.0 - 2022-11-14
### Added
- pytest にタイムアウト 60 秒
  https://github.com/honzabrecka/timeout-shell
  assert が複数あるときは、それ全部を実行する時間が timeout に収まらないと
  エラーになる。

## 0.44.2 - 2022-11-12
- db migration for stocks
- stocks implementation

## 0.44.1 - 2022-11-11
- get /stock

## 0.44.0 - 2022-11-11
- post /stock ... ハッタリだけ。
- clj -Tantq outdated :upgrade true

|       :file |                             :name | :current | :latest |
| ----------- | --------------------------------- | -------- | ------- |
| project.clj |    ch.qos.logback/logback-classic |    1.4.3 |   1.4.4 |
|             |                 cider/cider-nrepl |   0.28.6 |  0.28.7 |
|             |                         hato/hato |    0.8.2 |   0.9.0 |
|             | luminus-undertow/luminus-undertow |   0.1.15 |  0.1.16 |
|             |             org.clojure/tools.cli |  1.0.206 | 1.0.214 |
|             |       org.webjars/webjars-locator |     0.45 |    0.46 |


## 0.43.1 - 2022-11-06
- FIX: /profile/:login が auth error
  /admin は middleware/admin で認可している。
  /profile/login はプライベート関数の admin?
  :hkimura と "hkimura" の両方を許すようにした。

## 0.43.0 - 2022-10-20
- Changed home.clj/before? 月曜〆切が日曜〆切になってないか？

## 0.42.2 - 2022-10-18
- display arrows

## 0.42.1 - 2022-10-17
- newer is {right,left} を表示。

## 0.42.0 - 2022-10-14
- Py99-10 等でテストが通らない理由はこれ。
```
$ env pytest
platform linux2 -- Python 2.7.18, pytest-4.6.9, py-1.8.1, pluggy-0.13.0
```

pip3 で pytest をインストールし直し。
```
$ sudo pip3 install -U pytest
```

## 0.41.3 - 2022-10-11
- replaced favicon.ico

## 0.41.1 - 2022-10-05
- fix typo in Makefile

## 0.41.0 - 2022-10-05
- stop seeding problems

## 0.40.1 - 2022-09-27
- test を増やす。

## 0.40.0 - 2022-09-26
- release 準備。

## 0.39.0 - 2022-09-26
- 99 problems.

## 0.38.4 - 2022-09-26
- magic comment for UTF-8

## 0.38.1 - 2022-09-25
- docker-compose.yml, .devcontainer を gitignore しない。

## 0.38.0 - 2022-09-24
- pytest の結果の一部をエラーメッセージとして表示する。

## 0.85.819 - 2024-01-30
最初 vscode ユーザで失敗した後、rebuild container メニューを実行すると
大丈夫、かな？
.devcontaiers, docker-compose.yml は .gitigonore しているので、
場面に応じてその場で書き換えるでいいか？

## 0.37.0 - 2022-09-22
successed remote-container on nuc.local via ssh.
vscode ユーザでは /home/vscode/.m2 のボリュームマウントでエラーになる時がある。
nuc.local では root ユーザで /root/.m2 をボリュームマウントした。

## 0.36.0 - 2022-09-22
### Fixed
- コンテナ内からの hc/get に戻らない。
  原因は hato. cheshire を dependencies に加えないと、
  (hato.client/get {:as :json}) が失敗する。

## 0.35.1 - 2022-09-20
### Fixed
- `re-matches` is for whole-string match.
  home/pytest-test should use `re-find`.

## 0.35.0 - 2022-09-20
- (map-indexed #(vector (inc %1) %2) coll)
### Changed
- color of Problems buttons, from is-primary to is-warning
- class="button is-small is-rounded is-primary is-responsive"
- timbre を tools.logging でリプレース。
  luminus が最初から clojure.tools.logging を dependencies に入れていて、
  timbre と機能が重なるから。
- admin/seed-problems-from-markdown! の改良。test が入れずらい。
  markdown やめて edn で。

## 0.34.1 - 2022-09-20
- test がある問題は test を実行し True になる回答だけを受け付ける。
- test がない時は pytest しない。(re-match #"\S" test) で。

## 0.34.0 - 2022-09-20
### Added
- admin/seed-problems-from-markdown!
### Changed
- html/admin.html: seed button enabled
- admin/seed-problems
- /seed-problems {:post seed-problems-from-markdown!}

## 0.33.0 - 2022-09-20
- require pytest. poetry の外側でやむなく pip install pytest.
- No value supplied for key: /tmp/python14732584781486045261.py のエラーは
  java.io.File/createTempFile をそのまま sh に渡していたからか？
  (.getAbosolutePath tempfile) と囲んだら OK.
- 再び三度、VScode でバッファの内容が書き変わってしまう事件。
  opt+return で home.clj の式を評価すると
  home.clj バッファの内容が outpt.calva-repl で上書きされる。

## 0.32.0 - 2022-09-19
- ライブラリのバージョンアップ
- weekly [submissions comments]

## 0.31.1 - 2022-09-19
## Removed
- removed ~/.git.orig
- removed initdb.d, db-dumps

## 0.31.0 - 2022-09-19
early-deploy
- dns
- nginx
- systemd
- repository

## 0.30.0 - 2022-09-19
restart as Py99 for 2022 classes.
* login の差し替え
  l22.melt の API を利用する。CORS が心配だが。

- - - - - - - - - - - -

## 0.26.4 - 2022-04-07
### Changed
- ログをモチっとしっかり。誰がどこのページをアクセスしたか。
  問題番号とか、回答番号はログに残せてない。


## 0.26.6 - 2022-04-02
- login メッセージを再考した。

## 0.26.5 - 2022-04-02
- frozen.

## 0.26.3 - 2022-03-31
### Added
- login failure を赤で表示。
- robots.txt
- nobody/nobody
### Changed
- postgres read-only (app.melt)
- db エラーを try/catch で捕捉する。

## 0.26.2 - 2022-03-29
### Changed
- ユーザ名、ユーザID を落として、データベースを書き換えないバージョンとする。
  docker compose up --build
- ChromeBook では docker-compise version 3.8 は未サポート。3 に戻そう。
### Fixed
- ERROR:  syntax error at or near "if"
  py99-db-1   | LINE 1: alter table py99.users drop colum if exist sid;
  `column` が正しい。

## 0.26.1 - 2022-03-29
- ERROR: The Compose file './docker-compose.yml' is invalid because:
services.app.environment.py99_REQUIRE_MY_ANSWER contains false, which is an invalid type, it should be a string, number, or a null

上記に基づき、docker-compose.yml 修正。macOS のはエラーになんないんだけどな。

- nuc.local で frozen py99 動作した。
```sh
    % tree .
.
|-- Dockerfile
|-- docker-compose.yml
|-- initdb.d
|   |-- init-db.sh
|   `-- py99-2022-03-29.dump
`-- target
    `-- uberjar
        `-- py99.jar

    % lein uberjar
    % docker compose build
    % docker compose up -d
```

## 0.85.819 - 2024-01-30
- login メニューを作成。register へのリンクをコメントアウト。
- users テーブルから sid と name を落とす。

## 0.24.0 - 2022-03-12
- too late (did not finish) remote container to `lein uberjar`

### Changed
- lein ancient upgrade
  reitite 5.1.17

## 0.23.0 - 2022-03-11
### frozen
- 2021 の py99 は打ち止め。
- docker-compose.yml clojure:lein からイメージ作って、docker 内部で
  r99 できる。

## 0.22.1 - 2022-03-06
### Changed
- Dockerfile, docker-compose.yml

## 0.22.0 - 2022-02-15
### Bug fixed
- parinf 入っていると、無意識に間違って () の対応ずらしちゃった時に、
  勝手に () 調整しちゃってバグが見つけにくくなる。オフするか？

### Added
- upload-restart.sh

## 0.21.5 - 2022-02-15
### Changed
- /wp to display resources/docs/weekly-points.html

## 0.21.4 - 2022-02-14
### Added
- /wp to display wp.html.
- slurp takes `unix/path`. clojure.java.io/resource takes
  java class path(?)

## 0.85.819 - 2024-01-30
### Changed
- updated navbar to include ME, EE, GR, WP
- moved wp.html from resources/public to resources/html

## 0.21.2 - 2022-02-14
### Added
- resources/public/wp.html clojure/my/src/bin_count.clj から手作業で。
- clojure/my/src/my/bin_count.clj は robocar-2021/grading/src/weekly_points.clj に
  名前変更して移動。

## 0.21.1 - 2022-02-13
### Added
- 一行目の終わりに (+3) など。

## 0.21.0 - 2022-02-13
### Added
- /profile/:login admin only.

## 0.20.6 - 2022-02-12
### Bug fixed
- frozen な問題にはコメントできない。

## 0.20.5 - 2022-02-12
### Changed
- older comments: from=>to, to を表示
- first-line: 最大 n 文字じゃなく、最初の改行までを表示する。

## 0.20.4 - 2022-02-11
### Changed
- profile.html: weekly の ul を ol に変更。
- fix typo.

## 0.20.3 - 2022-02-10
### Changed
- 環境変数 py99\_REQUIRE\_MY\_ANSWER=FALSE で、回答を提出していない問題でも、
  他ユーザの回答を読める。

## 0.20.2 - 2022-02-09
### Fixed bug
- submit ボタンが２つ。

## 0.20.1 - 2022-02-09
### Changed
- 期末試験終了につき、新規投稿を受付停止してます。

## 0.20.0 - 2022-02-09
### Added
- (def frozen [320 330 340]) で 320, 330, 340 が `変更できない問題、回答` になる。
- CREATE TABLE frozens (
  num INT NOT NULL,
  update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
- frozen のオンオフがオンラインでできる。
    - /admin/freeze/:num
    - /admin/frozen/
    - /admin/defrost/:num

### Changed
- self-only? のチェックを validate から create-answer! に移した。

## 0.19.2 - 2022-02-07
### Added
- 環境変数 py99\_SELF\_ONLY=TRUE で self-only モード。
- self-only 時はバリデーションをオフする。
### Changed
- home.clj: 最終 weeks を 2022-02-14 に変更。

## 0.19.1 - 2022-02-06
### Added
- home.clj: weeks に 2022-02-11 追加。

## 0.19.0 - 2022-02-06
### Added
- 期末試験。自分の回答以外、表示できないモード。
  環境変数 py99\_SELF\_ONLY=TRUE で振る舞いを変える。

## 0.18.7 - 2022-02-02
### Changed
- number of submissions, number of solved の順に合わせる。
- solved (exclude group assignments) を明記する。

## 0.18.6 - 2022-02-02
### Fixed bug
- /profile, /ranking, rank/solved に表示する distinct solved count が
  グループ課題を含んでいる。

## 0.18.5 - 2022-01-27
### Changed
- admin only /answers

## 0.18.4 - 2022-01-26
### Added
- /answers - answers-by-problems
- forgot bump-version.sh

## 0.18.3 - 2022-01-25
### Fixed Bug
- (re-find #"\+\+ " s) は "i++ )" にマッチしてしまう。
  (re-find #"\+\+\s+[a-zA-Z]" s) で置き換え。

## 0.18.2 - 2022-01-23
### Fix Bug
- インデントが一つもないコードを弾く。

## 0.18.0 - 2022-01-20
- lein ancient upgrade
- py99_LOG_LEVEL

## 0.17.2 - 2022-01-17
## Changed
- /rank/* input width px->mm. windows と mac/linux とでは px の定義が違うって？
- /rank/* only admin can see login id.

## 0.17.1 - 2022-01-16
### Fixed
- dependencies に入れてない environ を home.clj から require していた。

## 0.17.0 - 2022-01-16
### Added
- macOS の開発環境に合わせ、
  postgres:4.1-alpine, clojure:jdk-17-lein-2.9.8-bullseye で
  Dockerfile, docker-compose.yml を用意。nuc.local で動作を確認。
- 開発は生 macOS で。docker は別の PC、環境に開発環境を持ち出すためと位置付ける。
- timbre/set-level! 環境変数を見てセットする。ただ、developは debug、
  production は info にあらかじめセットされている模様。必要ないか。

## 0.16.2 - 2022-01-15
### Added
input を利用した棒グラフでサブミット数他を表示。
自分は自分のアカウント名、他はサブミット数で。
- /rank/submissions
- /rank/solved
- /rank/comments

## 0.16.1 - 2022-01-15
### Docker
- clojure:openjdk-18-lein-2.9.8-slim-bullseye
- postgres:2.9-alpine

## 0.15.7 - 2022-01-10
### Fixed
- when solved is empty, could not (apply max-key :id solved)

## 0.15.6 - 2022-01-08
- グループ回答を個人成績に含めない。
- remove useless codes.
- use (restart-db) after updating queries.sql

## 0.15.5 - 2022-01-06
### Added
- check_indent.clj: exapand-tabs

## 0.15.4 - 2022-01-05
### Changed
- display hkimura info

## 0.15.3 - 2022-01-04
### Fixed
- added remove-open-close in check_indent.clj

## 0.15.2 - 2022-01-04
### Added
- announce bugfix at login page.

## 0.15.1 - 2022-01-04
### Fixed
- profile bug fixed. correct [submit commit] in /profile.

## 0.85.819 - 2024-01-30
### Changed
- date format in comment-form.

## 0.14.9 - 2021-12-26
- show group assignments answer in /profile page.

## 0.14.8 - 2021-12-25
### Added
- weely scores in /profile page.

## 0.14.7 - 2021-12-24
### Added
- indent any. accept indent 2 or 4.
- can not indent one, since indents must be even.
### Changed
- home.clj: (timbre/set-level! :info)

## 0.14.6 - 2021-12-20
### Added
- display 'enabled indent check' on login page.

## 0.14.5 - 2021-12-20
### Added
- display last login timestamp in profile page.
- enable check-indent

## 0.14.4 - 2021-12-19
### Added
- log login users

## 0.14.3 - 2021-12-19
### Fixed
- str/split-lines removes "\n"
  resume the "\n" before apply space-rule?

## 0.14.2 - 2021-12-18
### fixed
- check indents are all evens

## 0.14.1 - 2021-12-18
### Added
- indent-check.html

## 0.85.819 - 2024-01-30
### Added
- indent checker

## 0.13.0 - 2021-12-15
### Added
- /comments/:num /comment/:id ページから。その問題についたコメントを一覧表示する。

## 0.12.3 - 2021-12-14
### Added
- confirm on password change

## 0.12.2 - 2021-12-13
### Changed
- /comments に問題番号を表示する

## 0.12.1 - 2021-12-10
### resume
- 自分の回答よりも他人の回答
  ;;(redirect (str "/comment/" id)))
  (redirect (str "/answers/" num)))

## 0.12.0 - 2021-12-10
### Changed
- バリデーションをリファクタ。
- 回答提出後の戻りページ。
  PostgreSQL の RETURNING id コマンドを発見。
  HugSQL の 1, n, * について理解を深める。

## 0.11.0 - 2021-12-09
### Added
- バリデーションが通らなかったメッセージを表示する。
  コンパイラエラーをそのまま。

## 0.10.2 - 2021-12-08
### Changed
- adjust chart scales.
- change chart colors.

## 0.10.1 - 2021-12-08
### Added
- top 30 commenters in ranking page.

## 0.10.0 - 2021-12-08
### Added
- comments chart in profile page.
### Changed
- DRY! chart.clj
- recent submission の時刻

## 0.9.5 - 2021-12-05
### Changed
- /profile, last submission からのリンクは comment/{id} がいい。
- top 30
### Added
- comments all
- last submission

## 0.9.3 - 2021-12-04
### Fixed
- /profile provlems-solved.
- timbre/info failure validation をログするには、start.sh で
  標準出力をリダイレクトする。

## 0.9.2 - 2021-12-04
simply can see who is recently submit answers.
### Changed
- status page
  remove raning to rankig page
- profile page
  show chat
### Added
- ranking page

## 0.9.1 - 2021-12-04
### Added
- comment, to who?
- last submission

## 0.9.0 - 2021-12-04
### Changed
- refactored home/validate?
### Added
- defined home/space-rule?

## 0.8.23 - 2021-11-28
- login `warning` message

## 0.8.22 - 2021-11-21
### Added
- fake no-check button

## 0.8.21 - 2021-11-20
### Changed
- プロットレンジからはみ出たため、class statistics(y方向データ）を縮尺する。

## 0.8.20 - 2021-11-18
### Added
- /profile
### Removed
- status.html から profile 分を削除、profile.html として独立させた。
- 不必要な SQL 呼び出しを削除、移動させた。
- get /ch-pass はいらないな。
### Changed
- (timbre/set-level! :info)
- ナビの Password を Profile に

## 0.8.19 - 2021-11-17
### Changed
- register button disabled

## 0.8.18 - 2021-11-17
### Changed
- /admin, フォームをコメントアウトする代わりに、ボタンを disabled
- パスワード変更を status.html から外に出す。
### Added
- /ch-pass エンドポイントを作成

## 0.8.17 - 2021-11-17
### Changed
- dash の方が comma よりも見やすい

## 0.8.16 - 2021-11-16
### Added
- top 20 [problem solved]
### Changed
- status page looks improve

## 0.8.15 - 2021-11-15
### Changed
- top 20, recent 20, comments 20
- display order. was recent/top/comments, new top/recent/comments

## 0.8.14 - 2021-11-15
### Changed
- status.recents に問題番号

## 0.8.13
### Changed
- textarea height 300px

## 0.8.12
### Added
- display comments abbreviated.

## 0.8.11 - 2021-11-05
### Fixed
- improve routes/home/wrap. "\n" is the first priority.

## 0.8.10 - 2021-11-04
### Fixed
- Fix `wrap` bug. Shoule return string instead of list of strings.

## 0.8.9 - 2021-11-04
### Added
- (wrap n s) s を n 文字で折り返す。
### Fixme
- Selmer filter に引数を渡したい。今は、
  (add-filter! :wrap66 (fn [x] (wrap 66 x)))
  66 を引数で渡したい。

## 0.8.8 - 2021-11-03
### Fixed
- コメントのオーダーが時間順ではない --- queries.sql を修正。

## 0.8.7 - 2021-10-31
### Added
- 0.8.6 に問題番号の表示を追加。

## 0.8.6 - 2021-10-31
### Added
- 最近飛び交ったコメントを status ページ、Class Statistics セクションで表示。

## 0.8.5 - 2021-10-30
### Bugfix
- パスワードを変えたとき、update_at を更新していなかった。

## 0.8.4 - 2021-10-29
### Changed
- comments-sent を create_at DESC 順に。
- comment-form.html: placeholder='Your comment please. 適当な場所で改行してください。'
- comments received order by create_at desc;

## 0.8.3 - 2021-10-25
### Added
- /comments-sent/:login login はどの問題にコメントしたか？

## 0.8.2 - 2021-10-25
### Added
- respouces/public/numbers.txt
- replit.com 等で動作確認してから submit すること。動作確認せずに submit は減点。
### Changed
- status.html: problems solved: n/99

## 0.8.1 - 2021-10-24
### Changed
- return to the problem-page just solved.
- remove `hkimura` from  top 10, recent 10 lists.
### Added
- link to /answer/num from comment-form.html

## 0.8.0 - 2021-10-23
### Added
- charts.clj: routes/home.clj に詰め込み過ぎを改める。
- individual chart as a bar chart.
- cleanup routes.home/status-page
### Removed
- :individual, :class parameters from status.html

## 0.7.3 - 2021-10-22
### Added
- links to answers which has some comments
- comments sent fields at status.html individual
- /admin/comments displays coments from, comments to info.
### Changed
- status.html: move comment form to bottom of page.

## 0.7.2 - 2021-10-19
### Added
- display problems solved in status page
- display top 10 (include duplicates) in status page

## 0.7.1 - 2021-10-19
### Changed
- stop to use capitalize, login|capitalize, in status.html
### Added
- who send the same answer with you?

## 0.7.0 - 2021-10-18
- feature/svg-plot branch. date オブジェクトだと、
  org.java.Time.DateTime と java.time.LocalDate の見かけは一緒でもマッチは取れるか？
  面倒だ、文字列に変換してしまおう。
### Fixme
- HugSQL で `select date '2021-10-10' + integer '135'` の書き方がわからない。
### Changed
- return date as string rather than date object.
### Added
- can plot SVG graphs.

## 0.6.11 - 2021-10-18
### Bugfix
- 0.6.10 is a mistake. bug fixed in branch svg-plot.

## 0.6.10
enbug. 500 error  when access /admin/
hotfix 0.6.10 start.
### Bugfix
-- simply forgot `name login`. fixed.

### BUG
## 0.6.9
* 旧 r99 から favicon.ico をコピー。
## Changed
* middleware/admin? (get-in request [:session :identity]) が null になるケース。

## 0.6.8 - 2021-10-18
* defined/installed py99.service
* /answer/:n で回答リンクの表示を抑制しない。リンク先で制限かける。
  リンクをたどろうとしたらエラーの方がいい。

## 0.6.7 - 2021-10-17
* display individual/class answers with SVG graph.
一旦、feature/class-svg をマージして出直そう。
グラフの横軸は、個人、クラスとも、ゴールの日までの日にちとする。
クラスはその日の回答数、個人は回答数の積分値とする。ゴールは 99 題。
### Added
* /db-dumps フォルダ。データベースのダンプと、ダンプ・リストアスクリプト。
gitignore する。
* link to qa.melt from navbar.
* /comment/:n で create_at 表示。

## 0.6.6 - 2021-10-17
### Fixed
* 回答つけてないユーザは /comment/:n を見れない。home/comment-from に細工。
  403 を返す。

## 0.6.5 - 2021-10-17
### Added
- recent answers (logins)
- link from recents
- /comment/:n から他の回答も見れた方がいい。

## 0.6.4 - 2021-10-12
## Changed
* improve status.html individual field, class field, sent comments column
* copy protect CSS
https://on-ze.com/archives/5744

## 0.6.3 - 2021-10-12
### Added
* db/answers-by-date ... how many answers in a day?
* db/answers-bu-date-login ... how many answers user `login` submited in a day?

## 0.6.2 - 2021-10-12
### Added
* /ch-pass to change password.

## 0.6.1 - 2021-10-12
### Changed
* improve validate-answer and create-answer!
### Added
* version 表示 in /about. div 要素だけまとめて左寄せしたい。

## 0.6.0 - 2021-10-11
### Added
* syntax check, but not flash back.

## 0.5.1 - 2021-10-11
### Added
* validation for /register
* doc for /regisger
### Changed
* /answer/:id /comment/:id で表示する問題文を |safe でフィルタした。

## 0.5.0 - 2021-10-11
* r99.melt にテスト配置
### Added
* defined comments table
* /comment/:id -- id は answers.id
* can add comments
### Changed
* answer-form.html: s/Answer to/New Answer to/
* Navbar: /Home の代わりに /problems をリンク
* problems.html: {{p.problem|safe}}.
  内容の修正は docs/seed-problems.html でやらないと本番に反映しない。

## 0.4.0 - 2021-10-11
* 回答を表示できるようになった。エンドポイントは /comment/:id
  回答を表示するとはすなわち、コメントできるってことだ。
* answer をボタンに。button is-primary is-small でもやや大きすぎ、ブサイク。
  https://bulma.io/documentation/overview/colors/

## 0.3.5 - 2021-10-10
* VScode バグ？操作ミス？ src/clj/py99routes/home.clj が CHANGELOG.md の内容で
上書きになった。master からチェックアウトした home.clj で develop を上書き。
操作はこれでいいのかな？ 0.3.5 でコミットする。

## 0.3.4 - 2021-10-10
* status problems に色つけ
* /answer-page: 過去回答を md5 でグルーピング表示。自分の回答は same md5 に入るやろ。
* 同じ問題への回答の分類は group-by で。

## 0.3.3  - 2021-10-10
### Added
* problems ... defined table and a route /problems
* answers .... defined table and a route /answer:num
* /status

## 0.3.2 - 2021-10-09
### Added
* /admin/ ... seed problems ボタン。タネいれ。
* /admin/problems ... 問題の表示と編集。
* /admin route -- initdb.d や seed route 作戦の代わりに。
* seeding

本番では lein run migrate の後、
管理者を作成し、ログイン、
/admin/ から問題を入れる。

## 0.3.1 - 2021-10-06
* deploy test version onto app.melt.

## 0.3.0 - 2021-10-06
### Added
* define problems table
* seed problems (99) from `R99.html` by py99.seed.core/seed-problems!
* FIXME: why bad using `for` for seeding? doseq is OK.

## 0.2.0 - 2021-10-04
### Added
* register
* password hash
* Logout
### Changed
* git unignore *.sql

## 0.1.1 - 2021-10-04
### Added
* gitignore .vscode/
* authentication
* access restriction
### Changed
* lein ancient upgrade

## 0.1.0 - 2021-10-04
* project started.
