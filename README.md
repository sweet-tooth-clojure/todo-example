# Sweet Tooth Todo List Example

Get a _taste_ of what it's like to work with Sweet Tooth, a
single-page app framework for Clojure! In this README, I'll guide you
through many of its features by walking you through a single action:
creating a new todo list in a simple todo list app.

## Preamble

### What is Sweet Tooth?

My dream for Sweet Tooth is to make it easier, faster, and funner for
developers like you to get your ideas into production.

It does this by supporting common use cases like form handling,
navigation, and handling API calls. These use cases are not particular
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

Eventually, I'd like to make it dramatically easier for beginners to
make cool stuff and show it to their friends and family. For that
reason, I haven't put any effort into making use of tools like GraphQL
which can be quite powerful but harder for beginners to grok and
which, in my opinion, many apps don't need.

### What isn't Sweet Tooth?

Sweet Tooth is not for creating server-rendered apps. It hasn't been
used for high-traffic projects, so it's currently not for that,
either.

### Target Audience

This README has been written for people who have written at least one
Clojure web app. It assumes knowledge of web app development; it is
not intended for complete for beginners. (Maybe one day I'll write
that!) In particular, I'll refer to re-frame concepts without
explaining them.

## Walkthrough

For the rest of this doc I'll show you Sweet Tooth's ideas and
features by walking you through what happens when you create a todo
list. First, get the app running:

1. In a terminal, run `shadow-cljs watch dev`
2. Start a REPL. If prompted to choose between `lein`, `clojure-cli`,
   and `shadow-cljs`, choose `lein`.
3. Evaluate `(dev)` in the REPL. The REPL will pause for a little bit
   while it thinks.
4. Evaluate `(go)`.

The app should now be running at
[http://localhost:3000](http://localhost:3000) and you should see
something like this:

![01 running](docs/walkthrough/01-running.png)

Now, create a todo list by entering its title and hitting enter
clicking the "create todo list" button. You should see a little
activity indicator appear for a split second, then you should get
redirected to your newly-created todo list. The URL should have
changed to something like
`http://localhost:3000/todo-list/17592186045431`.



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
