package com.raquo.domtypes.generic.defs.eventProps

import com.raquo.domtypes.generic.builders.EventPropBuilder

/**
  * Window Events
  *
  * For type param docs see [[EventPropBuilder]]
  */
trait WindowEventProps[EP[_ <: DomEvent], DomEvent, DomUIEvent <: DomEvent, DomBeforeUnloadEvent <: DomEvent, DomHashChangeEvent <: DomEvent, DomPageTransitionEvent <: DomEvent, DomPopStateEvent <: DomEvent, DomStorageEvent <: DomEvent] { this: EventPropBuilder[EP, DomEvent] =>

  /**
    * The load event fires at the end of the document loading process. At this
    * point, all of the objects in the document are in the DOM, and all the
    * images and sub-frames have finished loading.
    *
    * MDN
    */
  lazy val onLoad: EP[DomUIEvent] = eventProp("onload")

  /**
    * Script to be run after the document is printed
    */
  lazy val onAfterPrint: EP[DomEvent] = eventProp("onafterprint")

  /**
    * Script to be run before the document is printed
    */
  lazy val onBeforePrint: EP[DomEvent] = eventProp("onbeforeprint")

  /**
    * Script to be run when the document is about to be unloaded
    */
  lazy val onBeforeUnload: EP[DomBeforeUnloadEvent] = eventProp("onbeforeunload")

  /**
    * Script to be run when there has been changes to the anchor part of the a URL
    */
  lazy val onHashChange: EP[DomHashChangeEvent] = eventProp("onhashchange")

  /**
    * Script to be run when the message is triggered
    */
  lazy val onMessage: EP[DomEvent] = eventProp("onmessage")

  /**
    * Script to be run when the browser starts to work offline
    */
  lazy val onOffline: EP[DomEvent] = eventProp("onoffline")

  /**
    * Script to be run when the browser starts to work online
    */
  lazy val onOnline: EP[DomEvent] = eventProp("ononline")

  /**
    * Script to be run when a user navigates away from a page
    */
  lazy val onPageHide: EP[DomPageTransitionEvent] = eventProp("onpagehide")

  /**
    * Script to be run when a user navigates to a page
    */
  lazy val onPageShow: EP[DomPageTransitionEvent] = eventProp("onpageshow")

  /**
    * Script to be run when the window's history changes
    */
  lazy val onPopState: EP[DomPopStateEvent] = eventProp("onpopstate")

  /**
    * Fires when the browser window is resized
    */
  lazy val onResize: EP[DomUIEvent] = eventProp("onresize")

  /**
    * Script to be run when a Web Storage area is updated
    */
  lazy val onStorage: EP[DomStorageEvent] = eventProp("onstorage")

  /**
    * Fires once a page has unloaded (or the browser window has been closed)
    */
  lazy val onUnload: EP[DomUIEvent] = eventProp("onunload")
}
