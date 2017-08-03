
var scroller = zenscroll.createScroller($('aside .scrollable-content')[0], 300, 50)

// do not scroll the page when scrolling past the boundaries of the toc
$(document).on('wheel', 'aside .scrollable-content', function(evt) {
  var offsetTop = this.scrollTop + parseInt(evt.originalEvent.deltaY, 10);
  var offsetBottom = this.scrollHeight - this.getBoundingClientRect().height - offsetTop;

  if (offsetTop < 0 || offsetBottom < 0) {
    evt.preventDefault();
  } else {
    evt.stopImmediatePropagation();
  }
});

// map of internal links to their a tags
var nav_links = {}

$('aside nav a').each(function(i) {
  var ref = $(this).attr('href');
  if (ref.indexOf("#") == 0) {
    nav_links[ref] = this;
  }
})

var active_nav_link = null;
var waypoints_enabled = true;

var waypoints = $('h1, h2, h3, h4, h5, h6').waypoint({

  handler: function(direction) {
    if (nav_links["#" + this.element.id]) {
      $(active_nav_link).removeClass("active")
      active_nav_link = nav_links["#" + this.element.id]
      $(active_nav_link).addClass("active")
      scroller.intoView(active_nav_link)
    }
  },

  offset: '10%',
  continuous: false

})

$('aside nav a').on('click', function(e) {
  if (waypoints_enabled) {
    waypoints.forEach(function(w) {
      w.disable()
    })
    waypoints_enabled = false;
  }

  $(active_nav_link).removeClass("active")
  active_nav_link = this
  $(active_nav_link).addClass("active")

  return true;
})

$(window).on('scroll', function(e) {
  if (!waypoints_enabled) {
    waypoints.forEach(function(w) {
      w.enable()
    })
    waypoints_enabled = true;
  }
})

var resize_aside = function(e) {
  var aside = $("aside.toc .toc-wrapper")
  var viewport_height = $(window).height()
  var aside_top = aside.offset().top
  var document_scroll_top = $(document).scrollTop();
  var footer_top = $("footer").offset().top;
  var footer_height = viewport_height - footer_top
  var footer_visible_px = viewport_height + document_scroll_top - footer_top
  var footer_space = 0

  if (footer_visible_px > 0) {
    footer_space = footer_visible_px
  }
  aside.height(viewport_height - aside_top + document_scroll_top - footer_space)

}

var init = function(){
  var aside_toc_element = $("aside.toc .toc-wrapper")[0]
  if (aside_toc_element) {
    var sticky = new Waypoint.Sticky({
      element: $("aside.toc .toc-wrapper")[0]
    })
    $(window).on('scroll', resize_aside)
    $(window).on('resize', resize_aside)
    resize_aside()
  }
}

// initializing twice helps with issues that can occur when visiting a url with
// a #fragment in them
init();

$(function(){
  init();
})
