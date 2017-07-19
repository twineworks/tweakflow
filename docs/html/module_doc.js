
var scroller = zenscroll.createScroller($('#sidebar .nav-content')[0], 300, 50)

// map of internal links to their a tags
var nav_links = {}

$('nav a').each(function(i){
  var ref = $(this).attr('href');
  if (ref.indexOf("#") == 0){
    nav_links[ref] = this;
  }
})

var active_nav_link = null;
var waypoints_enabled = false;

var waypoints = $('h1, h2, h3').waypoint({

  handler: function(direction){
    if (nav_links["#"+this.element.id]){
      $(active_nav_link).removeClass("active")
      active_nav_link = nav_links["#"+this.element.id]
      $(active_nav_link).addClass("active")
      scroller.intoView(active_nav_link)
    }
  },

  offset: '50%',
  continuous: false

})

$('nav a').on('click', function(e){
  if (waypoints_enabled){
    waypoints.forEach(function(w){w.disable()})
    waypoints_enabled = false;
  }

  $(active_nav_link).removeClass("active")
  active_nav_link = this
  $(active_nav_link).addClass("active")

  return true;
})

$(window).on('scroll', function(e){
  if (!waypoints_enabled){
    waypoints.forEach(function(w){w.enable()})
    waypoints_enabled = true;
  }
})