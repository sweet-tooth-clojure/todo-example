# Sweet Tooth Todo List Example

Get a _taste_ of what it's like to work with Sweet Tooth, a
single-page app framework for Clojure!

## What is Sweet Tooth?

My dream for Sweet Tooth is to create more opportunity for developers
like you to get your apps into production faster.

It does this by supporting common use cases like form handling,
navigation, and making API calls. These use cases are not particular
to the app you're trying to build, but as a Clojurian you've likely
had to figure them out for yourself when building an SPA. In the same
way that you shouldn't have to write your own filesystem when making a
desktop app, you shouldn't have to write your own form handling system
to launch your cool idea.

I've tried to write Sweet Tooth so that using it doesn't lock you into
its own weird, arcane world the way some frameworks do. It's built on
top of popular and proven libraries like re-frame, duct, integrant,
liberator, ring, and reitit, and you can always use those libraries
directly if you need to.

The analogy I've had in mind is taken from the way we use files: files
can be structured as text, text can be structured as JSON, and JSON
can be structured as transit. Each additional degree of structure
gives you added power while requiring more special-purpose tools. Your
YAML tools won't work on a JSON file. However, you can still use the
lower-level tools; `sed` and `awk` work just fine.

## What isn't Sweet Tooth?

## Target Audience

Some Clojure and ClojureScript experience.

## Running

1. In a terminal, run `shadow-cljs watch dev`
2. Open a REPL. If prompted to choose between `lein`, `clojure-cli`,
   and `shadow-cljs`, choose `lein`.
3. Evaluate `(dev)` in the REPL. The REPL will pause for a little bit
   while it thinks.
4. Evaluate `(go)`.

The app should now be running at
[http://localhost:3000](http://localhost:3000).

## Guided Tour



### Frontend

* routing
* syncing
* forms
* activity indicator
* validation
* expiring subscriptions
* re-frame sugar
* sync language

### Backend

* routing
* endpoints

## Exercises

## Prior Art

* [Hoplon](http://hoplon.io/)
* [Luminus](https://luminusweb.com/)
* [Pedestal](http://pedestal.io/)
* [Fulcro](http://fulcro.fulcrologic.com/)
* [Coast](https://github.com/coast-framework/coast)
* [Keechma](https://github.com/keechma/keechma)

Or see the entire list of frameworks at
[https://www.clojure-toolbox.com/](https://www.clojure-toolbox.com/)
under Web Frameworks.
