# R99
<!--
void functions
-->
1. 整数ひとつをキーボードから入力、それに 1 を足した数をプリントする関数 void add1_p(void).
1. 整数 x, y を引数にとり、それらを足した数を戻り値とする関数 int add2(int x, int y).
1. 整数 x, y を引数にとり、それらを足した数をプリントする関数 void add2_p(int x, int y).
1. 円の半径（整数）を引数にとり、その円の面積（浮動小数点数）を戻り値とする関数 float en(int r).
1. 円の半径（整数）をキーボードから読み、その円の面積（浮動小数点数）をプリントする関数 void en_p(void).
1. 整数 x を引数にとり、それが偶数だったら 1、奇数だったら 0 を返す関数 int is_even(int x).
1. キーボードから整数ひとつを入力し、それが偶数だったら "偶数です"、奇数だったら "奇数です" とプリントする関数 void parity_p(void).
1. 整数 n の絶対値を返す関数 int abs(int n).
1. 整数 n の 2 乗を戻り値とする関数 int square(int n).
1. 整数 n の m 乗を返す関数 int power(int n, int m). C言語では n<sup>m</sup> を n^m では計算できない。
1. 整数 x, y を引数にとり、それらの和、差、積、整数商をプリントする関数 void arithmetic_p(int x, int y).
1. 彼・彼女の年齢を y とする。彼・彼女がティーンエイジャーだったら真、そうでなければ偽を返す関数 int is_teenage(int y).
1. 西暦 year を引数にとり昭和、平成、令和の和暦をプリントする関数 void era_p(int year).  era_p(2020)は "令和2年" をプリントする。
1. 西暦 year を引数にとり、閏年であれば 1、そうでなければ 0 を返す関数 int is_leap(int year). 西暦が 4 で割り切れれば閏年。ただし、100 で割り切れる時は平年。ただし、400 で割り切れる時は閏年。
1. hh 時 mm 分 ss 秒の hh, mm, ss を引数にとり、0 時 0 分 0 秒からの通算秒に変換した整数を戻り値とする関数 int time_to_int(int h, int m, int s).
1. 時刻 h1:m1:s1 と 時刻 h2:m2:s2 の間の秒数を整数で返す関数 int sec_between(int h1, int m1, int s1, int h2, int m2, int s2).
1. 平年の 1 月 1 日から同年 mm 月 dd 日までの日数を返す関数 int days(int mm, int dd).
1. y1 年 m1 月 d1 日から y2 年 m2 月 d2 月までの日数を返す関数int days_between(int y1, int m1, int d1, int y2, int m2, int d2). 自分は今日まで何日生きてきましたか？
1. 時刻 h1:m1:s1 と 時刻 h2:m2:s2 をそれぞれ文字列として受け取り、それらの時間差を文字列で戻す関数char * times_between_string(char time1[ ], char time2[ ]).
1. 整数 x, y を引数にとり、大きい方の整数を戻り値とする関数 int max2(int x, int y).
1. 整数三つを引数にとり、もっとも大きい整数を戻り値とする関数 int max3(int x, int y, int z).
1. 整数 4 つを引数にとり、もっとも大きい整数を戻り値とする関数 int max4(int x, int y, int z, int w).
1. 3つの整数を引数とし、それらの長さを各辺とする三角形がありうるかどうかを判定する関数 int triangle(int x, int y, int z).
1. 3つの整数を引数とし、それらの長さを各辺とする直角三角形がありうるかどうかを判定する関数 int right_angle(int x, int y, int z).
<!--
number theory, prime numbers
-->
1. 整数 n から m までの総和を求める関数 int sum(int n, int m).
1. 正の整数 n から m までの整数の積を求める関数 int product(int n, int m).
1. 整数 n の各桁の総和を返す関数 int sum_of_digits(int n). sum_of_digits(12345) の戻り値は 15 。
1. 整数 n を引数とし、それが 3 の倍数だったら 1、5 の倍数だったら 2、3 の倍数でもあり、かつ、5 の倍数でもあったら 3、いずれでもなかったら 0 を返す関数 int fz(int n).
1. 3 で割って1余り、5 で割って2余り、7 で割って 3 余る正の整数の最小のものはなにか？（孫子の問題）
1. 整数一つを引数とし、その約数を全てプリントする関数 void divisors_p(int n).
1. n 以下の整数のうち、もっとも約数の多い数を返す関数 int most_divisors(int n). 前問に同じ。most_divisors(1000)の戻り値は 840.
1. 整数一つを引数とし、その約数の合計を戻り値とする関数 int sum_of_divisors(int n).
1. n 以下の素数(nは整数)は何個あるかを戻り値とする関数 int primes(int n).  primes(10000) の戻り値はおそらく 1229。
1. 整数一つを引数とし、その数が素数だったら1、素数でなかったら 0 を返す関数int is_prime(int n).約数が何個あるかを数えて素数判定するのは遅い。速い素数判定を望む。repl.it で is_prime(2147483549) が3秒以内で 1 を返せば速いと認める。回答には次で得られる計算時間を入れること。<br><pre> time ./main1real    0m1.020suser    0m0.511ssys 0m0.009s</pre>
1. n 未満の素数の和を求める関数int sum_primes_under(int n).sum_primes_under(1000) は 76127 のはず。
1. 2<sup>16</sup>を超えない最大の素数は何か？ それは 65521.
1. 2<sup>31</sup>を超えない最大の素数は何か？ それはきっと 2147483647.
1. 4 番目までの素数を足すと 2 + 3 + 5 + 7 = 17.1000 番目までの素数の和を求めなさい。それはたぶん 3682913.
1. 10000 未満の素数 p1, p2, p3 で、p1<sup>2</sup> + p2<sup>2</sup> = p3<sup>2</sup>を満たすものを探せ。
1. 2520 は 1 から 10 の数字のすべての整数で割り切れる最小の整数である。1 から 20 までの整数すべてで割り切れる最小の整数は何か？
1. 600851475143 の素因数のうち最大のものを求めよ。
1. 10 までの整数をその約数の数で分類すると、1-(1), 2-(2,3,5,7),3-(4,9), 4-(6,8,10) となってグループ 2 が最もたくさんのメンバーを持ち、そのメンバーの和は 17 になる。同様に 1000までの整数を分類し、もっともたくさんのメンバーを持つグループを特定し、総和せよ。多分その数は 143*** くらいになる。
1. n を整数とする。factorial(n) + 2 が立方数となるような n を全て求めよ。
<!--
perfect numbers
-->
1. 整数一つを引数とし、その数が完全数かどうかを判定する関数 int is_perfect(int n).
1. n よりも大きい完全数はなにか？を求める関数 int next_perfect(int n).next_perfect(28) はきっと 496。
<!--
square, cubic ...
-->
1. 整数 n が平方数であるかどうかを判定する関数 int is_square(int n). 237169 は平方数である。
1. 整数 n が立方数であるかどうかを判定する関数 int is_cubic(int n). 9663597 は立方数である。
1. 整数 n が 二つの整数の2乗の和として表されるかどうかを判定する関数int is_squeare_sum(int n). is_square_sum(30505) は 1 を返す。30505 = 123<sup></sup>2</sup>+124<sup>2</sup>.
1. 整数 n のルートを超えない最大の整数を返す関数int sqrt_int(int n). sqrt_int(24) の戻り値は4, sqrt_int(25)の戻り値は5 を期待している。ヒントは前問。ライブラリ sqrt を使うことは反則とする。
1. 1000000 未満の整数で、平方数かつ立方数でもある最大の数を探す関数。int square_cubic(int n).square_cubic(1000000) の戻り値はきっと、531***.
1. 3 桁の整数の桁を入れ替えた整数を返す関数int rev3(int n).例えば rev(314) の戻り値は 413 になる。310 は 13 だな。
1. rev3(n) が元の整数 n と等しくなる3桁の整数は何個あるかを返す関数int how_many_rev3(void).
<!--
float numbers
-->
1. float x を四捨五入した int を返す関数int f_to_i(float x).ヒント: 浮動小数点数 x の整数部分は (int)x で得られる。かっこの付け方が妙だけど、こう書く。(int)3.5 は 3 だよ。4にならない。
1. float x を小数点第 2 位で四捨五入した float を返す関数float f_to_f1(float x).f_to_f1(3.14159265) の戻り値は 3.100000 になる。
1. float x を小数点第 n+1 位で四捨五入した float を返す関数float f_to_f(float x, int n).f_to_f(3.14159265, 4) の戻り値は 3.141600になる。
<!--
random numbers
-->
1. 関数 long random(void) を呼ぶとすごく大きい正の整数乱数が返ってくる。random( ) を利用し、0 以上 n 未満の正の整数乱数を返す関数int my_rand(int n). <br>* 注意1, R99 は #include &lt;stdlib.h> が前提です。<br>* 注意2, #include &lt;unistd.h> を加えた後、プログラムでmy_rand( ) 呼ぶ前のどこかで一度（だいたいは main( )の最初で） 、<pre>srandom(getpid( ));</pre>ってやると、乱数がバラバラになります（変な日本語だけど、これやらないといつも同じ順で乱数が出てくる）
1. 0.0 &le; r &lt; 1.0 の浮動小数点数乱数 r を返す関数float randf(void).
1. n &le; r &lt; m の整数乱数 r を返す関数int rand_int(int n, int m).
1. 上の randf( ) を呼び出して、2次元乱数 [x, y](0.0 &le; x &lt; 1.0, 0.0 &le; y &lt; 1.0)を n 個プリントする関数void randf_p(int n).
1. 上の randf( ) を応用し、円周率 pi を求める関数float pi(int n).2次元乱数 [x,y]、(0.0 &le; x &lt; 1.0, 0.0 &le; y &lt; 1.0)を n = 1,000 個発生し、x^2 + y^2 &le; 1 となるものを数える。多分それは 785 近辺の数になる。とすると円周率 pi は  (785/1000)*4 と推定できる。n を増やすと pi の精度は上がるはず。
1. サイズ n の整数配列 a[ ] に 0~99 の乱数をセットする関数void init_randoms_99(int a[ ] , int n).
1. 上で作った乱数配列 a[ ] 中にみつからない 0~99 の数をプリントする関数void find_not(int a[ ], int n).
1. 上で作った乱数配列 a[ ] ( サイズは n) 中に一番たくさん重複して現れる数を返す関数int find_max_dupli(int a[ ], int n).
1. 上で作った乱数配列 a[ ] を要素の大きさ順に並べ替えた配列を b[ ] とする関数void sort(int a[ ], int b[ ], int n).(2021-01-16 [追記] hkimura の書き方悪かったか。b[0] には配列 a の一番小さい要素が来るように。2021-02-01 問題文修正、みんな誤解している。a[ ]を破壊してb[ ] にコピーを取るんじゃないよ。)
1. 上で並べ替えた配列 b[ ] が正しく要素順になっているかを確認する関数int is_sorted(int b[ ], int n).
1. int * shuffle(int n) を定義せよ。戻り値は 0~n-1 の n 個の正整数が重複なく順番バラバラに入った配列。完成するとビンゴゲームに使えるぞ。関数名を bingo( ) にしようか。
<!--
files
-->
1. ファイル<a href="integers.txt">integers.txt</a> をダウンロードし、適当な場所にセーブしてください。ファイル "numbers.txt"には一行にひとつ、整数が書き込まれている。そのファイルの1行目の数を返す関数int head(void).
1. ファイル"numbers.txt"が何行あるかを返す関数int lines(void).
1. ファイル"numbers.txt"の n 行目の数字を返す関数int nth(int n).
1. ファイル"numbers.txt"の最初の十行に含まれる整数の総和を返す関数int sum10(void).
1. ファイル"numbers.txt"の最初の n 行に含まれる整数の総和を返す関数int sum_n(int n).
1. ファイル "numbers.txt"の最後の n 行に含まれる整数の総和を返す関数int sum_tail(int n).
1. ファイル名を文字列 fname として引数にとり、そのファイルの中身を表示する関数 void cat(char *fname)
1. ファイル名を文字列 fname として引数にとり、そのファイルの中身を行番号つきで表示する関数 void n_cat(char *fname)
<!--
recursion
-->
1. 関数 int factorial(int n) を定義せよ。factorial(5) は 5! の値を戻り値とする。一般に factoria(n) = n * factorial(n-1).0! は 1 だよ。
1. 0!, 1!, 2! ... を次々に計算していき、n! > m となる最小の n を求める関数int factorial_over(int m). factorial_over(2000000) の戻り値は
1. 0!, 1!, 2! ... を次々に計算していき、n! < 0 となる最小の n を求める関数int factorial_overflow(void).C 言語ではこういうことが起こる。int が有限だからね。
1. フィボナッチ数列を計算する関数 int fibo(int n) を定義せよ。fibo(0) = 0, fibo(1) = 1, fibo(2) = 1 で、一般にfibo(n) = fibo(n-1) + fibo(n-2) だ。
1. フィボナッチ数が最初に n を超えるのはいくらかを求める関数int fibo_over(n). fibo_over(20000) の戻り値はきっと 23 だ。
1. n 以上 m 未満となるフィボナッチ数の総和を返す関数int sum_of_fibo_between(int n, int m).sum_of_fibo_between(10000,100000) の戻り値は 178700 よりちょっと大きい。
<!--
strings
-->
1. 文字列 s が空文字列 "" かどうかを判定する関数int is_empty(char* s).
1. 文字列 s の長さを返す関数int str_len(char* s).
1. 文字列 s に含まれる文字 c の数を返す関数 int count_chars(char* s, char c).
1. 二つの文字列 s1, s2 の先頭の n 文字が等しいかどうかを判定する関数int str_eql_n(char* s1, char* s2, int n).
1. 文字列 s1 と文字列 s2 が完全に等しいかどうかを判定する関数int str_eql(char* s1, char* s2).
1. 文字列 s1 を文字列 s2 にコピーする関数void str_copy(char* s1, char* s2).s2 は s1 をコピーするに十分な長さがあると仮定してよい。以下同様。str_copy(s1,s2) の実行後、str_sql(s1, s2) が真になること。
1. 文字列 s1 の後ろに文字列 s2 を連結する関数char* str_append(char* s1, char* s2).str_append("abc", "def") の実行後、str_eql(s1, "abcdef")は真になる。s1 を破壊しないバージョンも作ってみよう。s1 と s2 を連結した文字列を返すように。
1. 文字列 s1 中に文字列 s2 が出現するかどうかを判定する関数int str_search(char* s1, char* s2).s2 が s1 の何文字目から出現しているかを返す。見つからなかった時は -1 を返せ。戻り値 が 0 の時は「s1 の先頭に s2 は見つかる」の意味になる。
1. 文字列 s1 の n 文字目からの m 文字を削除するchar* str_remove(char* s1, int n, int m). 戻り値は s1.s1="0123456789" として、str_remove(s1, 5,3) の後、s1 は "0123489" になる。s1 を書き換えない、安全なバージョンにも挑戦しよう。
1. 文字列 s1 の n 文字目に文字列 s2 を挿入するchar* str_insert(char* s1, int n, char* s2).戻り値は挿入後の文字列（ポインタ）。printf("%s\n", str_insert("012345", 3, "abc")) は0123abc45 を印字する。
1. 文字列 s1 中に現れる文字列 s2 を文字列 s3 で置き換えるchar* str_subst(char* s1, char* s2, char* s3).戻り値は置き換え後の文字列（ポインタ）。
1. 文字列 s を全て大文字にして返す char *toUpper(char* s).printf("%s\n", toUpper("I am small letters, ain't I?")) はI AM SMALL LETTERS, AIN'T I? を印字する。
1. 文字列 s1 を整数に変換して返す関数 int str_to_int(char* s1).str_to_int("314")の戻り値は 314 になる。atoi( ) 使わずに行ってみよう。
1. 文字列 *s を逆にした文字列を返す関数 char* str_reverse(char* s).printf("%s\n", str_reverse("abcdef")) がプリントするのは"fedcba\n"
1. 文字列 *s1 が日本語文字列の場合、s1 を逆順にした文字列を返す関数char *jstr_reverse(char* s1)).printf("%s\n", jstr_reverse("おはようございます。")) がプリントするのは"。すまいざごうよはお\n"
1. 左右どちらから読んでも同じ値になる数を回文数という。2桁の数の積で表される回文数のうち、最大のものは 9009 = 91 × 99 である。3桁の数の積で表される回文数の最大値を求めよ。
1. n * m と同じ計算をする関数 int stoic_times(int n, int m) を定義せよ。負の数も考慮すること。stoic_times( ) ほか、補助関数中でも * を使うのは反則とする。
1. ++ と -- のみを使い、x + y と同じ計算をする関数 int stoic_add(int x, int y) を定義せよ。負の数も考慮すること。
<!--
take functions as parameters
-->
1. 4/(1+x<sup>2</sup>) を x について 0 から 1 まで積分すると pi になる。これをスマートにプログラムしてください。
