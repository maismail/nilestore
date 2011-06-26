
function initCurrView(mydata,location)
{
$.ajax({
  type:"POST",
  url: "/getwholeview",
  data:mydata,
  success: function(res){
  res = jQuery.parseJSON(res);
  var xlabels = res.ss;
  var ylabels = res.si;
  var data = res.links;
  var sslinks = res.sslinks;
  var maxVal=11;
  
var w = 25*xlabels.length,
    h = 25*ylabels.length,
    x = pv.Scale.ordinal(xlabels).split(0,w),
    y = pv.Scale.ordinal(ylabels).split(0, h),
    l=ylabels[0].length+20,
    c = pv.Scale.linear(0 ,maxVal).range("green","red","yellow");

var div = document.getElementById(location);

var vis = new pv.Panel()
	 .canvas(div)
    .width(w)
    .height(h)
    .bottom(100)
    .left(200)
    .right(30)
    .top(20)
    .events("all")
    .event("mousemove", pv.Behavior.point());


vis.add(pv.Rule)
    .data(ylabels)
    .bottom(y)
    .strokeStyle(function(d) { return d ? "#eee" : "#000"})
  .anchor("left").add(pv.Label);
  	

vis.add(pv.Rule)
    .data(xlabels)
    .left(x)
    .strokeStyle(function(d){ return d ? "#eee" : "#000"})
  .add(pv.Label)
  	.bottom(-l)
  	.textBaseline("middle")
  	.textAngle(-Math.PI/2)
  	.events("all").cursor("pointer").title(function(d) {return sslinks[d]})
    .event("click",function(d){ self.location = sslinks[d]});
	

vis.add(pv.Dot)
    .def("active", -1)
    .data(data)
    .left(function(d){ return x(d.ss)})
    .bottom(function(d){  return y(d.si)})
    .strokeStyle(function(d){ return d.val == "dead" ? "black" : c(d.val)})
    .fillStyle(function(d){ return d.val =="dead" ? this.strokeStyle().alpha(1) : this.strokeStyle().alpha(.4)})
    .event("point", function(){ return this.active(this.index).parent})
    .event("unpoint", function(){ return this.active(-1).parent})
  .anchor("right").add(pv.Label)
    .visible(function(){ return  this.anchorTarget().active() == this.index})
    .text(function(d){ return d.val/*.toFixed(2)*/});

vis.render();
  }
});
}

function initGrView(mydata,location)
{
$.ajax({
  type:"POST",
  url: "/getgroupedview",
  data:mydata,
  success: function(res){
  res = jQuery.parseJSON(res);
  
  data=res.data;
  n=data.length;
  m=data[0].length;
  sslinks=res.sslinks;
  ylabels = res.nodes;
  legend = res.legend;
  
var w = 600,
    h = 40*n,
    x = pv.Scale.linear(0, 1).range(0, w),
    y = pv.Scale.ordinal(pv.range(n)).splitBanded(0, h, 4/5);

var div = document.getElementById(location);

var vis = new pv.Panel()
	 .canvas(div)
    .width(w)
    .height(h)
    .bottom(20)
    .left(100)
    .right(10)
    .top(5);


var bar = vis.add(pv.Panel)
    .data(data)
    .top(function(){ return y(this.index)})
    .height(y.range().band)
  .add(pv.Bar)
    .data(function(d){return d})
    .top(function(){return this.index * y.range().band / m })
    .height(y.range().band / m)
    .left(0)
    .width(x)
    .fillStyle(pv.Colors.category10().by(pv.index));


bar.anchor("right").add(pv.Label)
    .textStyle("white")
	.text(function(d) { return (d*100).toFixed(1) + "%"}).
	events("mouseover").title(function(d){ return (d*100).toFixed(1) + "% (" + legend[this.index] +")"});


bar.parent.anchor("left").add(pv.Label)
    .textAlign("right")
    .textMargin(5)
    .text(function(){ return ylabels[this.parent.index]}).events("all").cursor("pointer").title(function(){ return sslinks[ylabels[this.parent.index]]})
    .event("click",function(){ self.location = sslinks[ylabels[this.parent.index]]});


vis.add(pv.Rule)
    .data(x.ticks(10))
    .left(x)
    .strokeStyle(function(d) {return  d ? "rgba(255,255,255,.3)" : "#000" })
  .add(pv.Rule)
    .bottom(0)
    .height(5)
    .strokeStyle("#000")
  .anchor("bottom").add(pv.Label)
    .text(function(d){ return (d*100).toFixed(0)+"%"});

vis.render();

  }
});

}