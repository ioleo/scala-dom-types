# Scala DOM Types
[![Build Status](https://circleci.com/gh/raquo/scala-dom-types.svg?style=svg)](https://circleci.com/gh/raquo/Airstream)
![Maven Central](https://img.shields.io/maven-central/v/com.raquo/domtypes_sjs1_2.13.svg)

_Scala DOM Types_ provides listings and type definitions for Javascript HTML and SVG tags as well as their attributes, DOM properties, and CSS styles.

    "com.raquo" %%% "domtypes" % "0.15.1"    // Scala.js 1.7.1+
    "com.raquo" %% "domtypes" % "0.15.1"     // JVM

Our type definitions are designed for easy integration into any kind of library. You can use this project to build your own DOM libraries like React or Snabbdom, but type-safe. For example, popular Scala.js reactive UI library [Outwatch](https://github.com/OutWatch/outwatch/) recently switched to _Scala DOM Types_, offloading thousands of lines of code and improving type safety ([diff](https://github.com/OutWatch/outwatch/pull/62)). I am also using _Scala DOM Types_ in my own projects:

- [Laminar](https://github.com/raquo/laminar), a high level reactive UI library for Scala.js 
- [Scala DOM Builder](https://github.com/raquo/scala-dom-builder), a low level DOM manipulation and tree tracking library
- [Scala DOM Test Utils](https://github.com/raquo/scala-dom-testutils), a library that verifies that your DOM node / tree matches provided description

`DOM` stands for [Document Object Model](https://developer.mozilla.org/en-US/docs/Web/API/Document_Object_Model/Introduction), in our context it's an object that represents an HTML document along with its HTML elements and their attributes, props and styles.



## Table of Contents

* [Community](#community)
* [Why use _Scala DOM Types_](#why-use-scala-dom-types)
* [What about ScalaTags](#what-about-scalatags)
* [What about scala-js-dom](#what-about-scala-js-dom)
* [Design Goals](#design-goals)
* [Documentation](#documentation)
  * [Codecs](#codecs)
  * [Reflected Attributes](#reflected-attributes)
  * [Complex Keys](#complex-keys)
  * [DOM Events & `dom.Event.target`](#dom-events--domeventtarget)
  * [CSS](#css)
  * [SVG](#svg)
  * [Naming Differences Compared To Native HTML & DOM](#naming-differences-compared-to-native-html--dom)
  * [How to Use _Scala DOM Types_](#how-to-use-_scala-dom-types_)
* [Related Projects](#my-related-projects)



## Community

Please use [github issues](https://github.com/raquo/scala-dom-types/issues) for bugs, feature requests, as well as all kinds of discussions, including questions on usage and integrations. I think this will work better than spreading thin across gitter / stackoverflow / etc. You can _watch_ this project on github to get issue updates if you're interested in following discussions.

See also: [Contribution guide](https://github.com/raquo/scala-dom-types/blob/master/CONTRIBUTING.md)



## Why use _Scala DOM Types_

Canonical use case: you're writing a Scala library that does HTML / DOM construction / manipulation and want to provide a type-safe API like this:

```scala
div(
  h1(rel := "title", "Hello world"),
  p(
    backgroundColor := "red",
    "Welcome to my fancy page!",
    span(draggable := true, "Fancyness is important.")
  ),
  button(onClick := doFancyThing, "Do Fancy Thing"),
  a(href := "http://example.com", title := "foo", "Example")
)
```

Of course, your API doesn't need to look anything like this, that's just an example. _Scala DOM Types_ doesn't actually provide the **`Tag.apply`** and **`:=`** methods that you'd need to make this example work.

If you do in fact want similar syntax, you could extend `Tag`, `HtmlAttr`, `Prop`, etc., or provide your own alternatives to those (_Scala DOM Types_ does not require you to use its own traits).

You can also extend the API of those classes with implicit conversions / implicit classes instead of subclassing. Or you might even use [Scala DOM Builder](https://github.com/raquo/scala-dom-builder) if that's what you need, or some of its individual classes (it's also very extensible and reusable).  

---

You don't need to be writing a whole library to benefit from _Scala DOM Types_, you can use it instead to make your application code more type-safe. For example, your imaginary method

```scala
setProperty(element: dom.Element, propName: String, propValue: Any)
```

could become
 
```scala
setProperty[Value](element: dom.Element, prop: Prop[Value], propValue: Value)
```

Now you can't pass just about any random string as `propName`, and even `propValue` is now type checked.



## What about ScalaTags

[ScalaTags](http://www.lihaoyi.com/scalatags/) is a popular Scala library that contains DOM type definitions similar to what we have here. However, _Scala DOM Types_ is different in a few ways:

- **More type safe**. For example, in _Scala DOM Types_ an `input` tag is linked to Scala.js `HTMLInputElement` class. This lets you provide exact types for the DOM nodes you create, so that you don't need to perform unsafe casts in your application code if you want to e.g. access the `value` property on an `input` you created. Similarly, all attributes, properties and styles are linked to the types that they accept to prevent you from assigning incorrect values.

- **More flexible**. _Scala DOM Types_ does not tell you how to compose your attributes / props / styles / tags together, and does not enforce any rendering paradigm. You are free to implement your own composition. I see that some projects fork ScalaTags just to get the type definitions without everything else. _Scala DOM Types_ does not get in your way, eliminating the need for such forking.

- **Better representation of native DOM types**. _Scala DOM Types_ handles Reflected Attributes consistently, and uses Codecs to properly encode/decode DOM values.

There are some other differences, for example _Scala DOM Types_ uses camelCase for attr / prop / style names because that is consistent with common Scala style.



## What about scala-js-dom

The [scala-js-dom](http://scala-js.github.io/scala-js-dom/) project serves a very different purpose – it provides typed Scala.js interfaces to native Javascript DOM classes such as `HTMLInputElement`. You can use those types when you already have instances of DOM elements, but you can not instantiate those types without using untyped methods like `document.createElement` because that is the only kind of API that Javascript provides for this.

On the other hand, _Scala DOM Types_ lets the consuming library create a type-safe _representation_ of real JS DOM nodes or trees, and it is up to your library's code to instantiate real JS nodes from the provided description. [Scala DOM Builder](https://github.com/raquo/scala-dom-builder) does that in the most straightforward way, but higher level libraries like React, [Snabbdom](https://github.com/raquo/Snabbdom.scala) or [Laminar](https://github.com/raquo/Laminar) could use _Scala DOM Types_ in their own way, e.g. to create _virtual_ or _reactive_ DOM structures.

Oh, and _Scala DOM Types_ **does** work on the JVM. Obviously you can't get native JS types there, but you can provide your own replacements for specific Scala.js types, or just not bother with such specificity (see [`defs.sameRefTags`](https://github.com/raquo/scala-dom-types/blob/master/shared/src/main/scala/com/raquo/domtypes/generic/defs/package.scala)).



## Design Goals

The purpose of _Scala DOM Types_ is to become a standard DOM types library used in Scala.js projects.


#### Precise Types

The most important type information must be encoded as Scala types. For example, DOM properties that only accept integers should be typed as such.  


#### Reasonably Precise Types

The types we provide will never be perfect. For example, MDN has this to say about the `list` attribute (`listId` in our API):

> The value must be the id of a <datalist> element in the same document. [...] This attribute is ignored when the type attribute's value is hidden, checkbox, radio, file, or a button type.

A far as I know, encoding such constraints as Scala types would be very hard, if possible at all.

This is not to say that we are content with the level of type safety we currently have in _Scala DOM Types_. Improvements are welcome as long as they provide significantly more value than burden to users of this library. This kind of thing is often subjective, so I suggest you open an issue for discussion first.


#### Flexibility

_Scala DOM Types_ is a low level library that is used by other libraries. As such, its API should be unopinionated and focused solely on providing useful data about DOM elements / attributes / etc. to consuming libraries in a way that is easy for them to implement.


#### Sanity Preservation Measures

We should provide a better API than the DOM if we can do that in a way that keeps usage discoverable and unsurprising.

Developers familiar with the DOM API should generally be able to discover the names of attributes / tags / etc. they need using IDE autocompletion (assuming they expect the names to match the DOM API). For example: `forId` is a good name for the `for` attribute. It avoids using a Scala reserved word, and it starts with `for` like the original attribute, so it's easy to find. It also implies what kind of string is expected for a value (an `id` of an element).

Within that constraint, we should also try to clean up the more insane corners of the DOM API.

* For example, the difference between `value` attribute vs `value` property trips up even experienced developers all the time. Scala DOM Types on the other hand has a `defaultValue` reflected attribute and a `value` property, which behave the way everyone would expect from the given names or from their knowledge of the DOM API.
* For another example, enumerated attributes like `contentEditable` that in the DOM accept "true" / "false" or "on" / "off" or "yes" / "no" should be boolean attributes in _Scala DOM Types_.

All naming differences with the DOM API should be documented in the README file (see below). Type differences are generally assumed to be self-documenting.



## Documentation

[API doc](https://javadoc.io/doc/com.raquo/domtypes_sjs1_2.13/latest/com/raquo/domtypes/index.html)

TODO:

* Write about general project structure, builders, etc.
* Provide links to specific implementation examples in other libraries (use my keys + implicits, or use your own keys) 


### Codecs

_Scala DOM Types_ provides some normalization of the native HTML / DOM API, which is crazy in places.

For example, there are a few ways to encode a boolean value into an HTML attribute:

1. As presence of the attribute – if attribute is present, `true`, else `false`.
2. As string "true" for true, or "false" for false
3. As string "yes" for true, or "no" for false.

Which one of those you need to use depends on the attribute. For example, attribute `disabled` needs option #1, but attribute `contenteditable` needs option #2. And then there are DOM Properties (as opposed to HTML Attributes) where booleans are encoded as actual booleans.

Similarly, numbers are encoded as strings in attributes, with no such conversion when working with properties.

_Scala DOM Types_ coalesces all these differences using codecs. When implementing a function that builds an attribute, you get provided with the attribute's name (key), datatype, and a codec that knows how to encode / decode that datatype into a value that should be passed to Javascript's native DOM API.

For example, the codecs for the three boolean options above are `BooleanAsPresenceCodec`, `BooleanAsTrueFalseStringCodec`, and `BooleanAsYesNoStringCodec`. They have concrete implementations of encode / decode methods but of course you don't have to use those.


### Reflected Attributes

HTML attributes and DOM properties are different things. As a prerequisite for this section, please read [this StackOverflow answer](https://stackoverflow.com/a/6004028/2601788) first. 

For more on this, read [Section 2.6.1 of this DOM spec](https://html.spec.whatwg.org/multipage/common-dom-interfaces.html#reflecting-content-attributes-in-idl-attributes). Note that it uses the term "IDL attributes" to refer to what we call "DOM properties", and "Content attributes" to refer to what we here call "HTML attributes".

So with that knowledge, `id` for example is a reflected attribute. Setting and reading it works exactly the same way regardless of whether you're using the HTML attribute `id`, or the DOM property `id`. Such reflected attributes live in `ReflectedHtmlAttrs` trait, which lets you build either attributes or properties depending on what implementation of `ReflectedHtmlAttrBuilder` you provide.

To keep you sane, _Scala DOM Types_ reflected attributes also normalize the DOM API a bit. For example, there is no `value` attribute in _Scala DOM Types_. There is only `defaultValue` reflected attribute, which uses either the `value` HTML attribute or the `defaultValue` DOM property depending on how you implement `ReflectedHtmlAttrBuilder`. This is because that attribute and that property behave the same even though they're named differently in the DOM, whereas the `value` DOM property has different behaviour (see the StackOverflow answer linked above). A corresponding HTML attribute with such behaviour does not exist, so in Scala DOM Types the `value` prop is defined in trait `Props`. It is not an attribute, nor is it a a reflected attribute.

Reflected attributes may behave slightly differently depending on whether you implement them as props or attributes. For example, in HTML5 the `cols` reflected attribute has a default value of `20`. If you read the `col` property from an empty `<textarea>` element, you will get `20`. However, if you try to read the attribute `col`, you will get nothing because the attribute was never explicitly set.

Note that Javascript DOM performs better for reading/writing DOM props than reading/writing HTML attributes.


### Complex Keys

Properties like `className` often require special handling in consuming libraries. For example, instead of a `String` based interface, you might want to offer a `Seq[String]` based one for `className`. To facilitate the development of such opinionated APIs we offer these keys in separate traits (`ComplexHtmlKeys` and `ComplexSvgKeys`) that allow for completely custom types.

If you don't need such customization at all, just use `CanonicalComplexHtmlKeys` and `CanonicalComplexSvgKeys` traits which implement these keys similar to all the others.


### DOM Events & `dom.Event.target`

When listening to `onChange`, `onSelect`, `onInput` events found in `FormEventProps`, you often need to access `event.target.value` to get the new value of the input element the event was fired on. However, `dom.Event.target` is an `EventTarget`, whereas the `value` property is only defined on `HTMLInputElement`, which `EventTarget` is not.

Properly typing `target` in JS events is hard because almost all events in which we care about it could fire not only on `HTMLInputElement`, but also `HTMLTextAreaElement`, and even `HTMLElement` in some cases (`onInput` on element with `contentEditable` set to `true`). 

_Scala DOM Types_ provides a few type params in `FormEventProps` to help deal with this mess, as well as the `TypedTargetEvent` type refinement trait. Ultimately, you simply can't safely use `.target` as something other than an `HTMLElement` for most events due to the underlying JS API being very dynamic.

For related discussion see [issue #13](https://github.com/raquo/scala-dom-types/issues/13) and [Outwatch issue #93](https://github.com/OutWatch/outwatch/issues/93), and some comments on [PR #9](https://github.com/raquo/scala-dom-types/pull/9).


### CSS

CSS is rather hard to type properly. A lot of CSS properties accept both numbers and a set of magic strings such as "auto", or specially formed strings in multiple formats such as "2px 5em 0 auto". We attempt to deal with this the same way that ScalaTags does, by defining objects for some CSS properties that have shorthand methods for applicable magic strings defined on them.

The downside of this approach is that this requires _Scala DOM Types_ to venture outside of its design scope, as for such a setup to work _Scala DOM Types_ needs to be aware of the concept of setters – `Modifier`s that set a particular property to a particular value, such as `backGroundColor := "auto"`.

See [Issue #2](https://github.com/raquo/scala-dom-types/issues/2) for discussion.


### SVG

SVG attributes have the same typing problems as CSS properties (see above), but a different solution in _Scala DOM Types_. We basically type most SVG attributes as strings. Eventually we hope to find a better solution that will fit both SVG and CSS use cases. See [Issue #2](https://github.com/raquo/scala-dom-types/issues/2) for discussion.


### Naming Differences Compared To Native HTML & DOM

We try to make the native HTML & DOM API a bit saner to work with in Scala.

#### General

* All identifiers are camelCased, (e.g. `datalist` is `dataList`) for consistency with conventional Scala style.
* `data-<suffix>` attributes are created using `dataAttr(suffix: String)` factory.
* `aria-<suffix>` attributes are available without the `aria-` prefix in the `AriaAttrs` trait. You could thus create `object aria extends AriaAttrs[...]` to namespace those attributes.

#### Attributes & Props
* `value` **attribute** is renamed `defaultValue` because native naming is misleading and confusing ([example](https://stackoverflow.com/a/6004028/2601788))
  * Note that the `value` **property** retains its name
* `checked` **attribute** is renamed to `defaultChecked` for the same reason
  * Note that the `checked` **property** retains its name
* `selected` **attribute** is renamed to `defaultSelected` for the same reason
  * Note that the `selected` **property** retains its name
* `class` **attribute** is renamed to `className` for consistency with reflected property name, and to avoid Scala reserved word
* `for` attribute and `htmlFor` property are available as reflected attribute `forId` for consistency and to avoid Scala reserved word
* `id` reflected attribute is renamed to `idAttr`, `max` attribute to `maxAttr`, `min` to `minAttr`, and `step` to `stepAttr` to free up good names for end user code
* `offset` and `result` SVG attributes renamed to `offsetAttr` and `resultAttr` respectively to free up good names for end user code
* `loading` reflected HTML attribute renamed to `loadingAttr` to avoid using a good name
* `style` attribute is renamed to `styleAttr` to let you implement a custom `style` attribute if you want.
* `content` attribute is renamed to `contentAttr` to avoid conflict with `content` CSS property
* `form` attribute is renamed to `formId` to avoid conflict with `form` tag
* `height` attribute is renamed to `heightAttr` to avoid conflict with `height` CSS property
* `width` attribute is renamed to `widthAttr` to avoid conflict with `width` CSS property
* `list` attribute is renamed to `listId` for clarity and consistency
* `contextmenu` attribute is renamed to `contextMenuId` for clarity and consistency

#### Tags
* Tags renamed to free up good names for end user code:
  * `style` -> `styleTag`, `link` -> `linkTag`, `param` -> `paramTag`, `map` -> `mapTag`
* Other tag renamings:
  * `title` -> `titleTag` to avoid conflict with `title` reflected attribute
  * `object` -> `objectTag` to avoid Scala reserved word


#### Aliases
* Attribute `type` == `typ` == `tpe` to avoid Scala reserved word
* Attribute `className` == `cls` for consistency with Scala / ScalaTags


### How to Use _Scala DOM Types_

So you're considering building a higher level DOM manipulation library on top of _Scala DOM Types_ such as [Laminar](https://github.com/raquo/Laminar), [Outwatch](https://github.com/OutWatch/outwatch) or [ScalaJS-React](https://github.com/japgolly/scalajs-react) (the former two use _Scala DOM Types_, the latter doesn't).

First off, if you're building such a library, you need to know a quite few things about how JS DOM works. _Scala DOM Types_ is just a collection of types, it's not an abstraction layer for the DOM. _You're_ building the abstraction layer. We can't cover everything about JS DOM here, but we did touch on some of the nastier parts above.

Laminar is my own library that uses _Scala DOM Types_ in a pretty standard way. Open [Laminar.scala](https://github.com/raquo/Laminar/blob/master/src/main/scala/com/raquo/laminar/api/Laminar.scala), which is Laminar's public API, and let's see what it has.

As you see we're building up a big `private[laminar] object Laminar`. It's marked private because it's exposed under a different alias, `com.raquo.laminar.L`. End users are expected to import either this object, or all of its properties (`L._`) depending on their preference, everything will work either way.

Our `object Laminar` extends quite a few _Scala DOM Types_ traits. Let's see what some of these traits these are, and what kinds of type params we pass to them.

We see `with ReflectedHtmlAttrs[ReactiveReflectedProp]` – this brings in the [`ReflectedHtmlAttrs`](https://github.com/raquo/scala-dom-types/blob/master/shared/src/main/scala/com/raquo/domtypes/generic/defs/reflectedAttrs/ReflectedHtmlAttrs.scala) trait. The type param of `ReflectedHtmlAttrs` indicates the type of Reflected Attribute instanced that you get when you call `L.id` or `L.alt` etc. In Laminar, this type is [`ReactiveProp`](https://github.com/raquo/Laminar/blob/master/src/main/scala/com/raquo/laminar/keys/ReactiveProp.scala) which extends _Scala DOM Types_ [Prop](https://github.com/raquo/scala-dom-types/blob/master/shared/src/main/scala/com/raquo/domtypes/generic/keys/Prop.scala) class. So ReactiveProp adds Laminar functionality to Prop, but it doesn't actually have to extend Prop, Prop is only provided as a canonical Prop class to help potential interop between libraries and to reduce boilerplate.

Speaking of boilerplate, `ReflectedHtmlAttrs` trait requires Laminar object to also implement `ReflectedHtmlAttrBuilder`. Because Laminar uses its own `ReactiveProp` type, we have to implement it ourselves, and we do it in [`HtmlBuilders`](https://github.com/raquo/Laminar/blob/master/src/main/scala/com/raquo/laminar/builders/HtmlBuilders.scala) trait, which `object Laminar` also extends. But if Laminar just used the canonical `Prop` instead of its own `ReactiveProp`, it could have simply used `Scala DOM Types` [CanonicalReflectedPropBuilder](https://github.com/raquo/scala-dom-types/blob/master/shared/src/main/scala/com/raquo/domtypes/generic/builders/canonical/CanonicalReflectedPropBuilder.scala) instead of re-implementing all the same `reflectedAttr` method in `HtmlBuilders`. But then I would need to add Laminar functionality to `Prop` by means of implicit extension methods, and I personally prefer non-implicit APIs.

If you look at `HtmlBuilders` you will see that it extends quite a few other traits – those follow the same pattern but for different types of keys – html attrs, event props, style props, tags, etc. And you'll see `object Laminar` extending _Scala DOM Types_ traits that provide these listings of attrs, event props, style props, tags, etc.

Lastly, Scala DOM Types code provides quite a few comments explaining what the traits are used for and what the classes represent. Hopefully together with the Laminar example it will be clear enough how to use Scala DOM Types from your library. If you've built libraries on top of ScalaTags, it's a somewhat similar pattern.

Notice that most _Scala DOM Types_ def traits live in the `shared` project. Some of them accept many type params to represent different types of DOM Events or HTML elements. This enables you to use _Scala DOM Types_ on the JVM. For implementing a purely frontend library you will want to use the type aliases defined in the `js` project, specifically in the [package object](https://github.com/raquo/scala-dom-types/blob/master/js/src/main/scala/com/raquo/domtypes/jsdom/defs/package.scala) there. Those aliases provide standard type params for _Scala DOM Types_ definition traits from the scalajs-dom project. Laminar uses those type aliases, and there is no reason not to if you don't need JVM compatibility in your library.


## My Related Projects

- [Laminar](https://github.com/raquo/Laminar) – Reactive UI library based on _Scala DOM Types_
- [Scala DOM Builder](https://github.com/raquo/scala-dom-builder) – Low-level Scala & Scala.js library for building and manipulating DOM trees
- [Scala DOM TestUtils](https://github.com/raquo/scala-dom-testutils) – Test that your Javascript DOM nodes match your expectations



## Author

Nikita Gazarov – [@raquo](https://twitter.com/raquo)



## License and Credits

_Scala DOM Types_ is provided under the [MIT license](https://github.com/raquo/scala-dom-types/blob/master/LICENSE.md).

Files in [`defs`](https://github.com/raquo/scala-dom-types/tree/master/shared/src/main/scala/com/raquo/domtypes/generic/defs) directory contain listings of DOM attributes, props, styles, etc. – Those were adapted from Li Haoyi's [ScalaTags](http://www.lihaoyi.com/scalatags/#License), which is also MIT licensed.

Comments marked with "MDN" or linking to MDN website are taken or derived from content created by Mozilla Contributors and are licensed under Creative Commons Attribution-ShareAlike license (CC-BY-SA), v2.5.
