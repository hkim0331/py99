import http.client
import json

DEBUG = False


def debug(*args):
    if DEBUG:
        print(*args)


def fetch(url, endpoint):
    try:
        conn = http.client.HTTPSConnection(url)
        conn.request("GET", endpoint, headers={"Host": url})
        response = conn.getresponse()
        debug("response.status", response.status)
        if response.status == 200:
            ret = response.read()
            return ret
        raise Exception("fetch()", "can not read")
    except Exception as e:
        print(e)
        raise
    finally:
        debug("close()")
        conn.close()


def answers(n):
    s = fetch("py99.melt.kyutech.ac.jp", "/api/recents/" + str(n))
    debug("s", s)
    dict = json.loads(s)
    for i in range(n):
        print(dict[i]["num"], dict[i]["login"], "\t", dict[i]["create_at"])


answers(4)
