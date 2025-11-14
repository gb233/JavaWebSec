var _=Object.defineProperty;var b=Object.getOwnPropertySymbols;var E=Object.prototype.hasOwnProperty,D=Object.prototype.propertyIsEnumerable;var M=(a,t,e)=>t in a?_(a,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[t]=e,C=(a,t)=>{for(var e in t||(t={}))E.call(t,e)&&M(a,e,t[e]);if(b)for(var e of b(t))D.call(t,e)&&M(a,e,t[e]);return a};var A=(a,t,e)=>new Promise((r,s)=>{var o=i=>{try{n(e.next(i))}catch(d){s(d)}},l=i=>{try{n(e.throw(i))}catch(d){s(d)}},n=i=>i.done?r(i.value):Promise.resolve(i.value).then(o,l);n((e=e.apply(a,t)).next())});import{_ as c,s as G,g as z,t as P,q as H,a as V,b as W,K as B,z as j,F as w,G as L,H as q,l as K,a1 as N}from"./VulnerabilityDetail-MsIT17yP.js";import{p as U}from"./chunk-4BX2VUAB-v0Rn0R58.js";import{p as X}from"./treemap-75Q7IDZK-DR824IKn.js";import"./index-BerFf6Xh.js";import"./vue-vendor-B7mYG-pd.js";import"./editor-WBIA9GGG.js";import"./element-plus-D5fCfbWr.js";import"./utils-BC0tTvwG.js";import"./collectionApi-B504y9Gc.js";import"./learningNoteApi-BEyM7ipM.js";import"./userActivity-XPhE8lvp.js";var x={showLegend:!0,ticks:5,max:null,min:0,graticule:"circle"},T={axes:[],curves:[],options:x},h=structuredClone(T),Y=q.radar,Z=c(()=>w(C(C({},Y),L().radar)),"getConfig"),O=c(()=>h.axes,"getAxes"),J=c(()=>h.curves,"getCurves"),Q=c(()=>h.options,"getOptions"),tt=c(a=>{h.axes=a.map(t=>{var e;return{name:t.name,label:(e=t.label)!=null?e:t.name}})},"setAxes"),et=c(a=>{h.curves=a.map(t=>{var e;return{name:t.name,label:(e=t.label)!=null?e:t.name,entries:at(t.entries)}})},"setCurves"),at=c(a=>{if(a[0].axis==null)return a.map(e=>e.value);const t=O();if(t.length===0)throw new Error("Axes must be populated before curves for reference entries");return t.map(e=>{const r=a.find(s=>{var o;return((o=s.axis)==null?void 0:o.$refText)===e.name});if(r===void 0)throw new Error("Missing entry for axis "+e.label);return r.value})},"computeCurveEntries"),rt=c(a=>{var e,r,s,o,l,n,i,d,u,p;const t=a.reduce((m,g)=>(m[g.name]=g,m),{});h.options={showLegend:(r=(e=t.showLegend)==null?void 0:e.value)!=null?r:x.showLegend,ticks:(o=(s=t.ticks)==null?void 0:s.value)!=null?o:x.ticks,max:(n=(l=t.max)==null?void 0:l.value)!=null?n:x.max,min:(d=(i=t.min)==null?void 0:i.value)!=null?d:x.min,graticule:(p=(u=t.graticule)==null?void 0:u.value)!=null?p:x.graticule}},"setOptions"),st=c(()=>{j(),h=structuredClone(T)},"clear"),f={getAxes:O,getCurves:J,getOptions:Q,setAxes:tt,setCurves:et,setOptions:rt,getConfig:Z,clear:st,setAccTitle:W,getAccTitle:V,setDiagramTitle:H,getDiagramTitle:P,getAccDescription:z,setAccDescription:G},nt=c(a=>{U(a,f);const{axes:t,curves:e,options:r}=a;f.setAxes(t),f.setCurves(e),f.setOptions(r)},"populate"),ot={parse:c(a=>A(void 0,null,function*(){const t=yield X("radar",a);K.debug(t),nt(t)}),"parse")},it=c((a,t,e,r)=>{var $;const s=r.db,o=s.getAxes(),l=s.getCurves(),n=s.getOptions(),i=s.getConfig(),d=s.getDiagramTitle(),u=B(t),p=lt(u,i),m=($=n.max)!=null?$:Math.max(...l.map(y=>Math.max(...y.entries))),g=n.min,v=Math.min(i.width,i.height)/2;ct(p,o,v,n.ticks,n.graticule),dt(p,o,v,i),S(p,o,l,g,m,n.graticule,i),F(p,l,n.showLegend,i),p.append("text").attr("class","radarTitle").text(d).attr("x",0).attr("y",-i.height/2-i.marginTop)},"draw"),lt=c((a,t)=>{const e=t.width+t.marginLeft+t.marginRight,r=t.height+t.marginTop+t.marginBottom,s={x:t.marginLeft+t.width/2,y:t.marginTop+t.height/2};return a.attr("viewbox",`0 0 ${e} ${r}`).attr("width",e).attr("height",r),a.append("g").attr("transform",`translate(${s.x}, ${s.y})`)},"drawFrame"),ct=c((a,t,e,r,s)=>{if(s==="circle")for(let o=0;o<r;o++){const l=e*(o+1)/r;a.append("circle").attr("r",l).attr("class","radarGraticule")}else if(s==="polygon"){const o=t.length;for(let l=0;l<r;l++){const n=e*(l+1)/r,i=t.map((d,u)=>{const p=2*u*Math.PI/o-Math.PI/2,m=n*Math.cos(p),g=n*Math.sin(p);return`${m},${g}`}).join(" ");a.append("polygon").attr("points",i).attr("class","radarGraticule")}}},"drawGraticule"),dt=c((a,t,e,r)=>{const s=t.length;for(let o=0;o<s;o++){const l=t[o].label,n=2*o*Math.PI/s-Math.PI/2;a.append("line").attr("x1",0).attr("y1",0).attr("x2",e*r.axisScaleFactor*Math.cos(n)).attr("y2",e*r.axisScaleFactor*Math.sin(n)).attr("class","radarAxisLine"),a.append("text").text(l).attr("x",e*r.axisLabelFactor*Math.cos(n)).attr("y",e*r.axisLabelFactor*Math.sin(n)).attr("class","radarAxisLabel")}},"drawAxes");function S(a,t,e,r,s,o,l){const n=t.length,i=Math.min(l.width,l.height)/2;e.forEach((d,u)=>{if(d.entries.length!==n)return;const p=d.entries.map((m,g)=>{const v=2*Math.PI*g/n-Math.PI/2,$=k(m,r,s,i),y=$*Math.cos(v),I=$*Math.sin(v);return{x:y,y:I}});o==="circle"?a.append("path").attr("d",R(p,l.curveTension)).attr("class",`radarCurve-${u}`):o==="polygon"&&a.append("polygon").attr("points",p.map(m=>`${m.x},${m.y}`).join(" ")).attr("class",`radarCurve-${u}`)})}c(S,"drawCurves");function k(a,t,e,r){const s=Math.min(Math.max(a,t),e);return r*(s-t)/(e-t)}c(k,"relativeRadius");function R(a,t){const e=a.length;let r=`M${a[0].x},${a[0].y}`;for(let s=0;s<e;s++){const o=a[(s-1+e)%e],l=a[s],n=a[(s+1)%e],i=a[(s+2)%e],d={x:l.x+(n.x-o.x)*t,y:l.y+(n.y-o.y)*t},u={x:n.x-(i.x-l.x)*t,y:n.y-(i.y-l.y)*t};r+=` C${d.x},${d.y} ${u.x},${u.y} ${n.x},${n.y}`}return`${r} Z`}c(R,"closedRoundCurve");function F(a,t,e,r){if(!e)return;const s=(r.width/2+r.marginRight)*3/4,o=-(r.height/2+r.marginTop)*3/4,l=20;t.forEach((n,i)=>{const d=a.append("g").attr("transform",`translate(${s}, ${o+i*l})`);d.append("rect").attr("width",12).attr("height",12).attr("class",`radarLegendBox-${i}`),d.append("text").attr("x",16).attr("y",0).attr("class","radarLegendText").text(n.label)})}c(F,"drawLegend");var pt={draw:it},ut=c((a,t)=>{let e="";for(let r=0;r<a.THEME_COLOR_LIMIT;r++){const s=a[`cScale${r}`];e+=`
		.radarCurve-${r} {
			color: ${s};
			fill: ${s};
			fill-opacity: ${t.curveOpacity};
			stroke: ${s};
			stroke-width: ${t.curveStrokeWidth};
		}
		.radarLegendBox-${r} {
			fill: ${s};
			fill-opacity: ${t.curveOpacity};
			stroke: ${s};
		}
		`}return e},"genIndexStyles"),mt=c(a=>{const t=N(),e=L(),r=w(t,e.themeVariables),s=w(r.radar,a);return{themeVariables:r,radarOptions:s}},"buildRadarStyleOptions"),gt=c(({radar:a}={})=>{const{themeVariables:t,radarOptions:e}=mt(a);return`
	.radarTitle {
		font-size: ${t.fontSize};
		color: ${t.titleColor};
		dominant-baseline: hanging;
		text-anchor: middle;
	}
	.radarAxisLine {
		stroke: ${e.axisColor};
		stroke-width: ${e.axisStrokeWidth};
	}
	.radarAxisLabel {
		dominant-baseline: middle;
		text-anchor: middle;
		font-size: ${e.axisLabelFontSize}px;
		color: ${e.axisColor};
	}
	.radarGraticule {
		fill: ${e.graticuleColor};
		fill-opacity: ${e.graticuleOpacity};
		stroke: ${e.graticuleColor};
		stroke-width: ${e.graticuleStrokeWidth};
	}
	.radarLegendText {
		text-anchor: start;
		font-size: ${e.legendFontSize}px;
		dominant-baseline: hanging;
	}
	${ut(t,e)}
	`},"styles"),Tt={parser:ot,db:f,renderer:pt,styles:gt};export{Tt as diagram};
