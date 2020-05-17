# Sweet Tooth To-Do List Example

Get a _taste_ of what it's like to work with Sweet Tooth, a
single-page app framework for Clojure! In this README, I'll guide you
through many of its features by taking you on a depth-first walk
through a single action: creating a new to-do list in a simple to-do
list app.

## Preamble

### What is Sweet Tooth?

My dream for Sweet Tooth is to make it easier, faster, and funner for
developers like you to get your ideas into production.

Swet Tooth does this by giving you the tools to deal with common SPA
concerns like handling forms, configuring routing and navigation, and
managing API calls. These use cases are not particular to the app
you're trying to build, but as a Clojurian you've likely had to figure
them out for yourself when building an SPA. In the same way that you
shouldn't have to write your own filesystem when making a desktop app,
you shouldn't have to write your own form handling system to launch
your cool idea. (BTW - word on the street is in the 80's a new OS
would the street [every month or
so](https://twitter.com/GeePawHill/status/1256342997643526151),
supporting my thesis that operating systems are frameworks.)

I've tried to write Sweet Tooth so that using it doesn't lock you into
some weird, arcane world the way some frameworks do. It's built on top
of popular and proven libraries like re-frame, duct, integrant,
liberator, ring, and reitit, and you can always use those libraries
directly if you need to.

The analogy I've had in mind is taken from the way we use files: files
can be structured as text, text can be structured as JSON, and JSON
can be structured as transit. Each additional degree of structure
gives you added power while requiring more special-purpose tools. Your
YAML tools won't work on a JSON file. However, you can still use the
lower-level tools; `sed` and `awk` work just fine. For more on my
underlying approach to framework development, see [Frameworks and Why
(Clojure) Programmers Need
Them](http://flyingmachinestudios.com/programming/why-programmers-need-frameworks/)

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
that!) In particular, I'll refer to Integrant and re-frame concepts
and explain them only briefly, if at all.

I dive into Sweet Tooth internals more than I would in a tutorial that
was focused solely on building stuff with the framework because you're
experienced and I'm hoping to reveal some of its design and get your
valuable feedback :)

## Walkthrough

For the rest of this doc I'll show you Sweet Tooth's ideas and
features by walking you through what happens when you create a to-do
list. First, get the app running:

1. In a terminal, run `npm install` then `shadow-cljs watch dev`
2. Start a REPL. If prompted to choose between `lein`, `clojure-cli`,
   and `shadow-cljs`, choose `lein`.
3. Evaluate `(dev)` in the REPL. The REPL will pause for a little bit
   while it thinks.
4. Evaluate `(go)`.

The app should now be running at
[http://localhost:3000](http://localhost:3000) and you should see
something like this:

![01 running](docs/walkthrough/01-running.png)

Now, create a to-do list by entering its title and hitting enter or
clicking the "create to-do list" button. You should see a little
activity indicator appear for a split second, then you should get
redirected to your newly-created to-do list. The URL should have
changed to something like
`http://localhost:3000/to-do-list/17592186045431`.

In even these two simple steps there's a lot going on, including:

* Initial rendering
  * App initialization
  * Route handling for `http://localhost:3000/`
  * Rendering the "home page"
* Form handling
  * Managing the input for the to-do list title
  * Submitting the form
  * Displaying an activity indicator
* API request handling
* Frontend response handling
* Navigating to the new to-do list

Let's dig in!

## Initial Rendering

What's involved in displaying the initial form and sidebar? If you
look in the `sweet-tooth.todo-example.frontend.core` namespace, you'll
see:

```clojure
(defn system-config
  "This is a function instead of a static value so that it will pick up
  reloaded changes"
  []
  (mm/meta-merge stconfig/default-config
                 {::stfr/frontend-router {:use    :reitit
                                          :routes froutes/frontend-routes}
                  ::stfr/sync-router     {:use    :reitit
                                          :routes (ig/ref ::eroutes/routes)}

                  ;; Treat handler registration as an external service,
                  ;; interact with it via re-frame effects
                  ::stjehf/handlers {}
                  ::eroutes/routes  ""}))

(defn -main []
  (rf/dispatch-sync [::stcf/init-system (system-config)])
  (rf/dispatch-sync [::stnf/dispatch-current])
  (r/render [app/app] (stcu/el-by-id "app")))
```

Most of this will be unfamiliar, but you look at the very last line
you'll see some code you might recognize:

```clojure
(r/render [app/app] (stcu/el-by-id "app"))
```

We're rendering the `app/app` component to the `<div id="app"></div>`
DOM element. Here's what that looks like:

```clojure
(defn app
  []
  [:div.app
   [:div.head
    [:div.container [:a {:href (stfr/path :home)} "Wow! A To-Do List!"]]]
   [:div.container.grid
    [:div.side @(rf/subscribe [::stnf/routed-component :side])]
    [:div.main @(rf/subscribe [::stnf/routed-component :main])]]])
```

Hmm. There's still nothing here that looks like the forms we see in
the sidebar and main area. What's going on? Here's the high level
overview, which I'll explain in detail in the sections that follow:

1. Integrant initializes system components
2. One component is a _router_ that associates URL patterns with

   * What components to display
   * Lifecycle callbacks that should get dispatched on entering or
     exiting a route
     
   (I haven't shown the router code that ties routes to components and
   lifecycle callbacks, but I'll introduce you to it later.)
3. Another component is a _nav handler_ that reacts to nav events by
   looking up the corresponding _route_, dispatching its lifecycle,
   and setting it as the current route in the appdb
4. The `[::stnf/dispatch-current]` re-frame event causes the nav
   handler to handle the current URL, dispatching its lifecycle and
   setting the current route
5. The `::stnf/routed-component` subscription pulls components for the
   current route out of the app db, and those components get rendered

Now let's go through all this in detail.

### App Initialization

When you open the home page, the app renders the home page's
components. For most projects, your app must perform some kind of
initialization process (to set up route handling, for example) to get
to this point. How does a Sweet Tooth app do it?

Briefly, Sweet Tooth provides a re-frame handler to initialize an
[Integrant](https://github.com/weavejester/integrant) _system_ (check
out the Integrant docs for a description of what a system is and how
Integrant provides a mechanism for starting and stopping
components). The system includes a component for managing nav events,
like loading the initial page or clicking a link. This nav component
looks up the _route_ for the current URL in a
[reitit](https://github.com/metosin/reitit) router. The route defines
_lifecycle callbacks_ and also defines which high-level components
should get displayed.

We'll look at each of these parts of the framework and how we use them
in our app.

First, let's look at the `sweet-tooth.todo-example.frontend.core`
namespace again:

```clojure
(defn system-config
  "This is a function instead of a static value so that it will pick up
  reloaded changes"
  []
  (mm/meta-merge stconfig/default-config
                 {::stfr/frontend-router {:use    :reitit
                                          :routes froutes/frontend-routes}
                  ::stfr/sync-router     {:use    :reitit
                                          :routes (ig/ref ::eroutes/routes)}

                  ;; Treat handler registration as an external service,
                  ;; interact with it via re-frame effects
                  ::stjehf/handlers {}
                  ::eroutes/routes  ""}))

(defn -main []
  (rf/dispatch-sync [::stcf/init-system (system-config)])
  (rf/dispatch-sync [::stnf/dispatch-current])
  (r/render [app/app] (stcu/el-by-id "app")))
```

As is tradition for Lispers, let's start at the bottom and work our
way up. In the `-main` function, you can see we're dispatching two
events and then rendering a component. Let's walk through the
mechanics of what's going on, and then we'll talk about why it works
the way it does.

The first event is:

```clojure
(rf/dispatch-sync [::stcf/init-system (system-config)])
```

`(system-config)` returns an Integrant config, a map describing a
system where each key corresponds to the name of a _system component_
(as opposed to a React compononent) and each value is that component's
configuration. Examples of system components include nav handlers that
react to History events and web worker managers.

Sweet Tooth comes with a bunch o' system components that are meant to
make your life easier, and the default config for those components
lives at `stconfig/default-config`. In the `system-config` function we
merge the default Sweet Tooth config with our app's particular
config. We use
[`meta-merge`](https://github.com/weavejester/meta-merge) because of
its support for deep merging and because of how it gives you some
control over how the two values get merged.

This system config is the payload for the `::stcf/init-system`
event. This event [results in integrant getting
called](https://github.com/sweet-tooth-clojure/frontend/blob/master/src/sweet_tooth/frontend/core/flow.cljc#L123)
to _initialize_ the system:

```clojure
(rf/reg-event-fx ::init-system
  (fn [_ [_ config]]
    {::init-system config}))

(rf/reg-fx ::init-system
  (fn [config]
    (reset! rfdb/app-db {:sweet-tooth/system (-> config
                                                 ig/prep
                                                 ig/init)})))
```

Integrant initializes an app by initializing individual components in
dependency order; the nav handler component depends on a router
component, so the router gets initialized before the nav handler.

Why do we use Integrant to initialize our app? A few reasons:

1. Sometimes we want to render different React components at different
   stages of the system's readiness. For example, you might want to
   show a loading indicator while the app sets up whatever state is
   necessary for it to be used, and then render the app proper once
   the system is ready. Integrant makes it a lot easier to determine
   when the system is ready.
2. Integrant has a very simple model for handling both initializing
   _and_ halting a system. This is very useful for local development
   with livereload when you have components that modify global state,
   for example by attaching event listeners to the window. Livereload
   can call `(ig/halt!)` on the system, giving each component to clean
   up after itself (remove its listeners) before code gets reloaded.
3. Integrant makes it easier to code to interfaces. The nav handler
   component depends on a router, and by default it depends on a
   reitit router. However, you could provide a bidi or silk router
   instead, as long as it can conform to the same interface. (This
   isn't particular to the initialization process per se but I threw
   it in because why not!?)

So that explains Integrant and how it fits into the app initialization
process, the first step in the `-main` function:

```clojure
(defn -main []
  (rf/dispatch-sync [::stcf/init-system (system-config)])
  (rf/dispatch-sync [::stnf/dispatch-current])
  (r/render [app/app] (stcu/el-by-id "app")))
```

To understand the next step, `(rf/dispatch-sync
[::stnf/dispatch-current])`, we'll take a closer look at Sweet Tooth's
_nav handler_ component.

### The nav handler component

You can see the nav handler's default config in the
[`sweet-tooth.frontend.config`](https://github.com/sweet-tooth-clojure/frontend/blob/master/src/sweet_tooth/frontend/config.cljs)
namespace:

```clojure
{::stnf/handler {:dispatch-route-handler ::stnf/dispatch-route
                 :check-can-unload?      true
                 :router                 (ig/ref ::stfr/frontend-router)
                 :global-lifecycle       (ig/ref ::stnf/global-lifecycle)}}
```

On initialization, it [uses an adapted version of the accountant
library](https://github.com/sweet-tooth-clojure/frontend/blob/master/src/sweet_tooth/frontend/nav/flow.cljs#L26)
to register javascript event handlers for nav events. These
_javascript event_ handlers will dispatch _re-frame events_; Sweet
Tooth's default configuration, above, has the js event handlers
dispatching the `::stnf/disptach-route` re-frame event by default. In
extremely simplified pseudocode, it's as if the following gets
evaluated when the nav component is initialized:

```clojure
(js/listen js/NavEvent #(rf/dispatch [::stnf/dispatch-route]))
```

`::stnf/dispatch-route` is one of the gnarlier bits of Sweet Tooth,
and we don't need to go into all the details of how it works.
Ultimately what it does is:

1. Figures out what _route_ corresponds to the potential new URL
   proposed by the navigation event using a _router_. (I say
   _potential_ URL because it's possible for nav events to get
   rejected.)
1. Dispatches the route's _lifecycle callbacks_
2. Sets the currently active route in the re-frame app db

In the -main function, we see `(rf/dispatch-sync
[::stnf/dispatch-current])`. This behaves almost identically to
`::stnf/dispatch-route`; the only difference is that it operates on
the current URL.

To understand this process fully, we'll need to look at this router
that I keep talking about.

### The router component

I kept saying that the nav handler uses a router to look up
routes. Where does the router come from? You can see it in the config
for the nav handler:

```clojure
{::stnf/handler {:dispatch-route-handler ::stnf/dispatch-route
                 :check-can-unload?      true
                 :router                 (ig/ref ::stfr/frontend-router) ;; <--- There it is!
                 :global-lifecycle       (ig/ref ::stnf/global-lifecycle)}}
```

The config includes a _reference_ to another component,
`::stfr/frontend-router`. We actually saw the configuration for _that_
component in `sweet-tooth.todo-example.frontend.core`:

```clojure
(defn system-config
  "This is a function instead of a static value so that it will pick up
  reloaded changes"
  []
  (mm/meta-merge stconfig/default-config
                 {::stfr/frontend-router {:use    :reitit
                                          :routes froutes/frontend-routes}
                  ::stfr/sync-router     {:use    :reitit
                                          :routes (ig/ref ::eroutes/routes)}

                  ;; Treat handler registration as an external service,
                  ;; interact with it via re-frame effects
                  ::stjehf/handlers {}
                  ::eroutes/routes  ""}))
```

So the `::stfr/frontend-router` component gets initialized with this
configuration:

```clojure
{:use    :reitit
 :routes froutes/frontend-routes}
```

`:use` specifies what library should be used to parse route data into
a router, and reitit is supported out of the box. `:routes` specifies
the route data. Here's `froutes/frontend-routes`:

```clojure
(ns sweet-tooth.todo-example.frontend.routes
  (:require [sweet-tooth.frontend.sync.flow :as stsf]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.frontend.nav.flow :as stnf]
            [sweet-tooth.todo-example.cross.validate :as v]
            [sweet-tooth.todo-example.frontend.components.home :as h]
            [sweet-tooth.todo-example.frontend.components.todo-lists.list :as tll]
            [sweet-tooth.todo-example.frontend.components.todo-lists.show :as tls]
            [sweet-tooth.todo-example.frontend.components.ui :as ui]
            [clojure.spec.alpha :as s]
            [reitit.coercion.spec :as rs]))

(s/def :db/id int?)

(def frontend-routes
  [["/"
    {:name       :home
     :lifecycle  {:param-change [::stsf/sync-once [:get :todo-lists]]}
     :components {:side [tll/component]
                  :main [h/component]}
     :title      "To-Do List"}]

   ["/todo-list/{db/id}"
    {:name       :show-todo-list
     :lifecycle  {:param-change [[::stff/initialize-form [:todos :create] {:validate (ui/validate-with v/todo-rules)}]
                                 [::stsf/sync-once [:get :todo-lists]]
                                 [::stnf/get-with-route-params :todo-list]]}
     :components {:side [tll/component]
                  :main [tls/component]}
     :coercion   rs/coercion
     :parameters {:path (s/keys :req [:db/id])}
     :title      "To-Do List"}]])
```

You can see that each route has a `:components` key, a map with
`:side` and `:main` keys. When you load the home page, `tll/component`
shows up in the side bar, and `tls/component` shows up in the "main"
column.

At the beginning of all this I asked how the `app` component worked:

```clojure
(defn app
  []
  [:div.app
   [:div.head
    [:div.container [:a {:href (stfr/path :home)} "Wow! A To-Do List!"]]]
   [:div.container.grid
    [:div.side @(rf/subscribe [::stnf/routed-component :side])]
    [:div.main @(rf/subscribe [::stnf/routed-component :main])]]])
```

Now we have all the pieces to solve the puzzle:

1. A nav handler gets created on initialization
2. It's passed a router that associates URL paths with components
3. We dispatch `(rf/dispatch-sync [::stnf/dispatch-current])`. This
   sets the current route in the re-frame app db.
4. The `::stnf/routed-component` subscription looks up the
   `:component` key for the current route in the app db.
5. Those components get rendered.

## Form Handlng

Form handling is one of those corners of SPA development that's ripe
for frameworking: it's somewhat tedious and difficult to get right,
and time spent on it takes away from spending time on building your
product. Sweet Tooth has a featureful, extensible system for working
with forms. The form system consists of:

* Form represenation, both the shape of form data and the convention
  for storing forms in the global state app
* The component system for building form inputs
* The set of handlers for updating form data
* Form submission
* The set of callbacks for form state transitions

To ground the discussion, let's look at the small form found in the
`sweet-tooth.todo-example.frontend.components.home` namespace:

```clojure
(stfc/with-form [:todo-lists :create]
  [:form (on-submit {:sync {:on {:success [[::stff/submit-form-success :$ctx {:clear [:buffer :ui-state]}]
                                           [::stnf/navigate-to-synced-entity :show-todo-list :$ctx]
                                           [:focus-element "#todo-list-title" 100]]}}})
   [input :text :todo-list/title
    {:id          "todo-list-title"
     :placeholder "new to-do list title"
     :no-label    true}]
   [:input {:type "submit" :value "create to-do list"}]
   [ui/form-state-feedback form]])
```

(Side note: if you actually submit the form it will disappear. It only
shows when you have no to-do lists. To get it back, evaluate
`(recreate-db) (reset)` in the REPL from the `dev` namespace, then
refresh localhost:3000).

You'll notice some peculiarties: What is `stfc/with-form`? Where did
`input` come from - there's no binding for it in sight? And `form`, in
the last line?

I'll answer those questions, but first let's focus on answering more
basic questions: How does this form manage state so that it can submit
input to the backend? The first step to answering that is looking at
how forms are stored in the global state atom:

### Form representation

The form shown above has its state stored in the global state atom
under `[:form :todo-lists :create]`. You can check that for yourself
by hitting `Ctrl-h` when viewing the to-do list app in your browser;
this should open the [re-frame
10x](https://github.com/day8/re-frame-10x) dashboard. If you click on
the app-db link and enter the path above, you should see values get
updated when you type.

If you think of the global state atom as a filesystem, forms are
stored under the `:form` "directory" in the same way that logs are
generally saved to `/var/log` on *nix systems. Files have names, and
forms have names; the form above is named `[:todo-lists
:create]`. This form name is closed over by event handlers and
subscriptions created by `stfc/with-form`, making it possible to
easily manage this particular form's state.

So that's _where_ forms are stored. But what form data gets stored?
Forms are represented as a map with the following keys:

* `:buffer` is a map that stores the current state of form
  inputs. When you type into the input element, whatever you type gets
  put here. The example above shows the component `[input :text
  :todo-list/title ...]`. Its value is stored under `[:buffer
  :todo-list/title]` in the form map. The buffer map's keys are
  _attributes_. For example, I will refer to `:todo-list/title` as an
  attribute.
* `:base` is a map that can be used to reset a form or discard
  changes.
* `:errors` is a map where keys are form attributes and
  values are error messages.
* `:input-events` is used to control the display of error
  messages. For example: if you're typing into a password confirmation
  input, the field is invalid as you type, but you don't want to
  display error messages until the input loses focus (unless you want
  to come off as extremely aggressive).
* `:state` refers to the form's submission state: unsubmitted, active,
  success. I think. I'm not sure this is a good idea.
* `:ui-state` This is a convenience for when you want to, say,
  show/hide a form. I'm not sure this is a good idea either, but
  associating ui state with a form makes it easy to completely reset a
  form. For example, when you navigate out of a view where you've
  shown a form, you might want to completely reset the form state so
  that the form isn't showing the next time you navigate to that
  view. Or something.

Now that we know how we represent and store forms, let's look at the
input components and how they update form state.

### Input components

Input components need to:

* Store their value somewhere
* Update their value in response to... well, input
* Display their value

Sweet Tooth provides tools to create input components that manage
these tasks consistently, unobtrusively, and extensibly. Look at the
how the to-do list title input is defined:

```clojure
[input :text :todo-list/title
 {:id          "todo-list-title"
  :placeholder "new to-do list title"
  :no-label    true}]
```

This works with no `:value` or `:on-change` in sight, which is as it
should be; those are details that should be handled for you, and
shouldn't have to clutter your code with them. But how does `input`
achieve this? That's what we'll look at in this section.

The high level strategy is:

1. Create event handlers and subscriptions that are common to all
   input components. I'll refer to these as _input options_.
2. Modify input options according to an input's type as necessary. For
   example, with most inputs you display the current value by
   providing a `:value` key. With checkboxes and radio buttons,
   though, you instead provide a `:checked` key.
3. Pass the input options to the appropriate components. `:text`,
   `:password`, and `:number` input types can all be handled with a
   `[:input input-opts]` HTML element, but a `:select` needs special
   handling.

To see how Sweet Tooth implements this strategy, let's look at the
input component in context:

```clojure
(stfc/with-form [:todo-lists :create]
  [:form
   [input :text :todo-list/title
    {:id          "todo-list-title"
     :placeholder "new to-do list title"
     :no-label    true}]])
```

In the expression `(stfc/with-form [:todo-lists :create])`,
`with-form` is a macro—the only one in Sweet Tooth's frontend
lib!—that creates a bunch of bindings. (If you really, really, really
hate that, like with a passion, then you can use the function
`stfc/form` and destructure the bindings yourself.)

One of the values it binds is the `input` function. (Functions are
Reagent components. This is completely badass.) `input` closes over
the form's name, `[:todo-lists :create]`. `input` uses that
name and the argument `:todo-list/title` to create event handlers that
will update the attribute's value in the global state atom at the path
`[:form :todo-lists :create :buffer :todo-list/title]`. It
likewise creates subscriptions for the attribute's buffer and its
errors. These subscriptions and handlers are composed in a map and
passed to the multimethod `stfc/input-type-opts`.

`stfc/input-type-opts` is implemented for different input types as
needed: `:checkbox`, `:date`, etc. This multimethod performs any
transformations necessary so that the generic form subscriptions and
handlers will work with the specified input type. For example, the
`:checkbox` implementation returns a `:default-checked` key instead of
a `:value` key.

The `input` function takes the updated options from
`stfc/input-type-opts` and passes them to the multimethod
`stfc/input`.  `stfc/input` is implemented for different input
elements like `<select>`, `<textarea>`, etc.

So that explains what I mean when I say that Sweet Tooth's input
component system is _consistent_ and _unobtrusive_: all form inputs
are managed using the same tools, and the implementation details are
in the background where they belong (you don't have to pass
`:on-change` to `input` unless you want custom behavior.)

The system is _extensible_ in that you can use these tools for custom
input types, which I think is pretty cool. Here's an example of
extending `stfc/input` so you can use a markdown editor:

```clojure
(ns sweet-tooth.todo-example.frontend.components.ui.simplemde
  (:require ["react-simplemde-editor" :default SimpleMDE]
            [sweet-tooth.frontend.form.components :as stfc]))

(defmethod stfc/input :simplemde
  [{:keys [partial-form-path attr-path value]}]
  [:> SimpleMDE {:onChange (fn [val] (stfc/dispatch-new-val partial-form-path attr-path val))
                 :value    value}])
```

To try this out, modify
`sweet-tooth.todo-example.frontend.components.home` by changing
`[input :text :todo-list/title ...]` to `[input :simplemde
:todo-list/title ...]`

Sweet Tooth provides all the machinery necessary for this new input
type to participate in the form abstracton! You, the developer, don't
have to agonize over whether to use global or local state, or
otherwise figure out how to get your custom input component to play
with the rest of your form.

### Submitting the form


### Displaying an activity indicator

## notes to self

You should ignore everything after this point; it's just notes to myself.

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
