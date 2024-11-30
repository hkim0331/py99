def errors():
    """
    データを文字列で返す。データ区切りは改行"\n".
    """
    return """
-rw-r--r-- 1 ubuntu ubuntu  224 11月 17 19:59 3759069426632780-ksk0427.py
-rw-r--r-- 1 ubuntu ubuntu  138 11月 17 20:09 3759658331018792-ksk0427.py
... あとは省略
"""


def get_login(s):
    """
    s はデータの1行。
    -rw-r--r-- 1 ubuntu ubuntu  137 11月 17 23:59 3773486450270793-katarina.py
    この行から katarina を取り出して返す。
    """
    return s[62:-3]


def get_logins():
    """
    errors() を改行で区切ったリストのそれぞれからログインを切り出す。
    """
    return [get_login(s) for s in errors().split("\n")]


def comp(x,y):
    """
    昇順、降順、比較の仕方をここで定義する。
    """
    if x < y:
        return -1
    if x > y:
        return 1
    return 0


def smallers(xs, f):
    return [x for x in xs if f(x,xs[0]) < 0]


def equals(xs, f):
    return [x for x in xs if f(x,xs[0]) == 0]


def largers(xs,f):
    return [x for x in xs if f(x,xs[0]) > 0]


def qsort(xs,f):
    if xs == []:
        return []
    return qsort(smallers(xs, f),f) + equals(xs, f) + qsort(largers(xs,f), f)

# print(qsort(get_logins(), comp))

def compress(xs):
    """
    [1,1,1,2,2,3,1,1,1] => [[1,3],[2,2],[1,3]]に変換する。
    """
    if xs == []:
        return []
    c = 1
    try:
        while xs[c] == xs[0]:
            c += 1
        return [[xs[0], c]] + compress(xs[c:])
    except Exception:
        return [[xs[0], c]]

results = compress(qsort(get_logins(), comp))

# results はこんな感じになるので、
# [['', 2], ['abcddd', 3], ['ahwx3763', 3], ['aisaka', 1],...
# 要素の第2要素でソーティングする。それ用の comp 関数を定義する。

def comp2(x,y):
    """
    x、y を x[1], y[1] で比較するように。
    """
    if x[1] < y[1]:
        return -1
    if x[1] > y[1]:
        return 1
    return 0

# print(qsort(results, comp2))
