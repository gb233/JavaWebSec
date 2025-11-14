var L=(t,r,p)=>new Promise((h,i)=>{var c=a=>{try{e(p.next(a))}catch(o){i(o)}},l=a=>{try{e(p.throw(a))}catch(o){i(o)}},e=a=>a.done?h(a.value):Promise.resolve(a.value).then(c,l);e((p=p.apply(t,r)).next())});import{V as y,P as z,aB as q,_ as g,g as H,s as K,a as Z,b as J,t as Q,q as X,l as F,c as Y,F as tt,K as et,a4 as at,e as rt,z as nt,H as it}from"./VulnerabilityDetail-MsIT17yP.js";import{p as st}from"./chunk-4BX2VUAB-v0Rn0R58.js";import{p as lt}from"./treemap-75Q7IDZK-DR824IKn.js";import{d as W}from"./arc-D_a8jMcU.js";import{o as ot}from"./ordinal-Cboi1Yqb.js";import"./index-BerFf6Xh.js";import"./vue-vendor-B7mYG-pd.js";import"./editor-WBIA9GGG.js";import"./element-plus-D5fCfbWr.js";import"./utils-BC0tTvwG.js";import"./collectionApi-B504y9Gc.js";import"./learningNoteApi-BEyM7ipM.js";import"./userActivity-XPhE8lvp.js";import"./init-Gi6I4Gst.js";function ct(t,r){return r<t?-1:r>t?1:r>=t?0:NaN}function pt(t){return t}function ut(){var t=pt,r=ct,p=null,h=y(0),i=y(z),c=y(0);function l(e){var a,o=(e=q(e)).length,d,S,x=0,u=new Array(o),s=new Array(o),m=+h.apply(this,arguments),w=Math.min(z,Math.max(-z,i.apply(this,arguments)-m)),v,$=Math.min(Math.abs(w)/o,c.apply(this,arguments)),T=$*(w<0?-1:1),f;for(a=0;a<o;++a)(f=s[u[a]=a]=+t(e[a],a,e))>0&&(x+=f);for(r!=null?u.sort(function(D,C){return r(s[D],s[C])}):p!=null&&u.sort(function(D,C){return p(e[D],e[C])}),a=0,S=x?(w-o*T)/x:0;a<o;++a,m=v)d=u[a],f=s[d],v=m+(f>0?f*S:0)+T,s[d]={data:e[d],index:a,value:f,startAngle:m,endAngle:v,padAngle:$};return s}return l.value=function(e){return arguments.length?(t=typeof e=="function"?e:y(+e),l):t},l.sortValues=function(e){return arguments.length?(r=e,p=null,l):r},l.sort=function(e){return arguments.length?(p=e,r=null,l):p},l.startAngle=function(e){return arguments.length?(h=typeof e=="function"?e:y(+e),l):h},l.endAngle=function(e){return arguments.length?(i=typeof e=="function"?e:y(+e),l):i},l.padAngle=function(e){return arguments.length?(c=typeof e=="function"?e:y(+e),l):c},l}var gt=it.pie,G={sections:new Map,showData:!1},b=G.sections,N=G.showData,dt=structuredClone(gt),ft=g(()=>structuredClone(dt),"getConfig"),mt=g(()=>{b=new Map,N=G.showData,nt()},"clear"),ht=g(({label:t,value:r})=>{if(r<0)throw new Error(`"${t}" has invalid value: ${r}. Negative values are not allowed in pie charts. All slice values must be >= 0.`);b.has(t)||(b.set(t,r),F.debug(`added new section: ${t}, with value: ${r}`))},"addSection"),vt=g(()=>b,"getSections"),xt=g(t=>{N=t},"setShowData"),yt=g(()=>N,"getShowData"),_={getConfig:ft,clear:mt,setDiagramTitle:X,getDiagramTitle:Q,setAccTitle:J,getAccTitle:Z,setAccDescription:K,getAccDescription:H,addSection:ht,getSections:vt,setShowData:xt,getShowData:yt},St=g((t,r)=>{st(t,r),r.setShowData(t.showData),t.sections.map(r.addSection)},"populateDb"),wt={parse:g(t=>L(void 0,null,function*(){const r=yield lt("pie",t);F.debug(r),St(r,_)}),"parse")},At=g(t=>`
  .pieCircle{
    stroke: ${t.pieStrokeColor};
    stroke-width : ${t.pieStrokeWidth};
    opacity : ${t.pieOpacity};
  }
  .pieOuterCircle{
    stroke: ${t.pieOuterStrokeColor};
    stroke-width: ${t.pieOuterStrokeWidth};
    fill: none;
  }
  .pieTitleText {
    text-anchor: middle;
    font-size: ${t.pieTitleTextSize};
    fill: ${t.pieTitleTextColor};
    font-family: ${t.fontFamily};
  }
  .slice {
    font-family: ${t.fontFamily};
    fill: ${t.pieSectionTextColor};
    font-size:${t.pieSectionTextSize};
    // fill: white;
  }
  .legend text {
    fill: ${t.pieLegendTextColor};
    font-family: ${t.fontFamily};
    font-size: ${t.pieLegendTextSize};
  }
`,"getStyles"),Dt=At,Ct=g(t=>{const r=[...t.values()].reduce((i,c)=>i+c,0),p=[...t.entries()].map(([i,c])=>({label:i,value:c})).filter(i=>i.value/r*100>=1).sort((i,c)=>c.value-i.value);return ut().value(i=>i.value)(p)},"createPieArcs"),$t=g((t,r,p,h)=>{F.debug(`rendering pie chart
`+t);const i=h.db,c=Y(),l=tt(i.getConfig(),c.pie),e=40,a=18,o=4,d=450,S=d,x=et(r),u=x.append("g");u.attr("transform","translate("+S/2+","+d/2+")");const{themeVariables:s}=c;let[m]=at(s.pieOuterStrokeWidth);m!=null||(m=2);const w=l.textPosition,v=Math.min(S,d)/2-e,$=W().innerRadius(0).outerRadius(v),T=W().innerRadius(v*w).outerRadius(v*w);u.append("circle").attr("cx",0).attr("cy",0).attr("r",v+m/2).attr("class","pieOuterCircle");const f=i.getSections(),D=Ct(f),C=[s.pie1,s.pie2,s.pie3,s.pie4,s.pie5,s.pie6,s.pie7,s.pie8,s.pie9,s.pie10,s.pie11,s.pie12];let E=0;f.forEach(n=>{E+=n});const P=D.filter(n=>(n.data.value/E*100).toFixed(0)!=="0"),M=ot(C);u.selectAll("mySlices").data(P).enter().append("path").attr("d",$).attr("fill",n=>M(n.data.label)).attr("class","pieCircle"),u.selectAll("mySlices").data(P).enter().append("text").text(n=>(n.data.value/E*100).toFixed(0)+"%").attr("transform",n=>"translate("+T.centroid(n)+")").style("text-anchor","middle").attr("class","slice"),u.append("text").text(i.getDiagramTitle()).attr("x",0).attr("y",-400/2).attr("class","pieTitleText");const O=[...f.entries()].map(([n,A])=>({label:n,value:A})),k=u.selectAll(".legend").data(O).enter().append("g").attr("class","legend").attr("transform",(n,A)=>{const I=a+o,V=I*O.length/2,U=12*a,j=A*I-V;return"translate("+U+","+j+")"});k.append("rect").attr("width",a).attr("height",a).style("fill",n=>M(n.label)).style("stroke",n=>M(n.label)),k.append("text").attr("x",a+o).attr("y",a-o).text(n=>i.getShowData()?`${n.label} [${n.value}]`:n.label);const B=Math.max(...k.selectAll("text").nodes().map(n=>{var A;return(A=n==null?void 0:n.getBoundingClientRect().width)!=null?A:0})),R=S+e+a+o+B;x.attr("viewBox",`0 0 ${R} ${d}`),rt(x,d,R,l.useMaxWidth)},"draw"),Tt={draw:$t},Bt={parser:wt,db:_,renderer:Tt,styles:Dt};export{Bt as diagram};
