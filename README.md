# py99

🔥UNDER CONSTRUCTION🔥

generated using Luminus version "4.19"

updated libraries by `antq`

## `R99` => `R99c` => `Py99`

In 2017, I programmed `R99` in COMMON LISP.
It was a course designed for C programming beginners.

The main purpose of it is to provide mutual stdudying environment to
students. Students can see other students' answers if he/she submitted
his/her answers including teacher's advices on this app.
Although simple, which is not an online tutorial.

I planed to rewrite the old `R99` with newly learned language,
Clojure. It was done as `R99c`.

Then in 2022, my university's caliculum changed the programming language
to teach from C to Python.
Truely said, it was me who insist the language change. Before that,
teachers had selected the languages accroding to their own favorites.
C, processing, JavaScript, bash, and so on.
It was obviously wrong for beginners.

So, python.

In case of me, I love Clojure much more than Python.

With spirit of Clojure's functional programming,
benefit of immutable data,
I will teach python. For the preparation, first of all,
I decided to rebuild `R99c` as `Py99`.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## config.edn

`dev-config.edn` and `test-config-edn` are gitignored.
must provide them out from git repository before proceed develpment,
or in production.

## Anual Prep

adjust `weeks` and `period` in home.clj before use.

- weeks
- period

## Run

    cd <to_this_dir>
    code .
    start dev container
    REPL
    choose Leingingen
    in output.calva-repl,
    user> (start)

after starting, calva auto foward ports.
using it, emacs cider can connect the working nrepl.

## License

Copyright © 2021, 2022, 2023 Hiroshi Kimura
