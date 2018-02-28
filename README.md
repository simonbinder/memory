# memory

This is an online-two-player memory game realized in clojure and reagent.

## Setup

Frontend and backend run on seperate servers. To start the frontend, run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).


To start the backend, open a seperate console window and run:

    lein run

To get an interactive development environment, you can also run:

    lein repl

Once the repl has started, run:

    (-main)

and the server starts in an interactive repl.
