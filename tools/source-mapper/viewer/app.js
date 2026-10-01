const state={map:null,curation:null,nodes:new Map(),edges:[],history:[],selected:null};
const $=id=>document.getElementById(id);
const esc=value=>String(value??"").replace(/[&<>"']/g,ch=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[ch]));

async function init(){
  try{
    const [map,curation]=await Promise.all([fetch("../output/system-map.json").then(check),fetch("../curation/system-map-curation.json").then(check)]);
    state.map=map;state.curation=curation;
    [...map.nodes,...curation.semanticNodes].forEach(node=>state.nodes.set(node.id,node));
    state.edges=map.edges;state.history=[{kind:"system",label:"System"}];
    $("map-stats").textContent=`${map.nodes.length} generated nodes · ${map.edges.length} generated edges`;
    setupSearch();renderSystem();
  }catch(error){$("map").innerHTML=`<p><strong>Map could not be loaded.</strong><br>${esc(error.message)}<br><span class="muted">Run the viewer through the documented local HTTP server.</span></p>`;}
}
function check(response){if(!response.ok)throw new Error(`${response.url}: ${response.status}`);return response.json()}
function description(node){return state.curation.descriptions[node.id]||node.description||roleFrom(node)||"Analyzer-generated structural fact."}
function roleFrom(node){if(node.type==="SQL")return "SQL declared by a MyBatis mapper annotation.";if(node.type==="DB_OBJECT")return `${node.details?.objectKind||"Database"} object referenced by the map.`;return ""}
function evidenceForNode(node){const generated=state.edges.filter(e=>e.from===node.id||e.to===node.id).map(e=>e.evidence?.level);const curated=state.curation.systemView.edges.filter(e=>e.from===node.id||e.to===node.id).map(e=>e.evidence);return [...new Set([...generated,...curated].filter(Boolean))]}
function badge(level){return `<span class="pill"><i class="badge ${level.toLowerCase()}">${level[0]}</i> ${esc(level)}</span>`}
function nodeCard(node,selected=false){return `<button class="node ${esc(node.type)} ${selected?'selected':''}" data-node="${esc(node.id)}"><span class="kind">${esc(node.type)}</span><strong>${esc(node.label)}</strong><p>${esc(description(node))}</p></button>`}
function bindNodes(){document.querySelectorAll("[data-node]").forEach(el=>el.addEventListener("click",()=>navigate(el.dataset.node)))}
function renderSystem(){
  state.history=[{kind:"system",label:"System"}];state.selected=null;
  $("view-kicker").textContent="SYSTEM VIEW";$("view-title").textContent=state.curation.system.label;
  $("view-summary").textContent="The primary execution-oriented path. Framework-generated links are visibly marked as inferred.";
  const ids=state.curation.systemView.nodeIds;
  const cards=ids.map(id=>state.nodes.get(id)).filter(Boolean).map(nodeCard).join("");
  const relationships=state.curation.systemView.edges.map(edge=>{const from=state.nodes.get(edge.from),to=state.nodes.get(edge.to);return `<div class="edge" style="width:auto;align-items:flex-start"><span>${badge(edge.evidence)} <strong>${esc(from?.label)}</strong> → <strong>${esc(to?.label)}</strong> · ${esc(edge.label)}</span></div>`}).join("");
  const html=`<div class="neighborhood">${cards}</div><div style="width:100%;border-top:1px solid var(--line);padding-top:18px"><span class="eyebrow">EXECUTION RELATIONSHIPS</span>${relationships}</div>`;
  $("map").innerHTML=html;$("detail").innerHTML=`<span class="eyebrow">START HERE</span><h3>${esc(state.curation.system.label)}</h3><p>${esc(state.curation.system.description)}</p><dl><dt>FIRST VIEW</dt><dd>${ids.length} curated nodes from ${state.map.nodes.length} generated facts</dd><dt>HOW TO USE</dt><dd>Select a node to inspect its direct neighborhood and source evidence.</dd></dl>`;
  renderBreadcrumb();bindNodes();
}
function navigate(id){const node=state.nodes.get(id);if(!node)return;state.selected=id;state.history=[{kind:"system",label:"System"},{kind:"node",id,label:node.label}];renderNeighborhood(node)}
function renderNeighborhood(node){
  const related=state.edges.filter(e=>e.from===node.id||e.to===node.id);
  const neighborIds=[...new Set(related.flatMap(e=>[e.from,e.to]).filter(id=>id!==node.id))];
  const neighbors=neighborIds.map(id=>state.nodes.get(id)).filter(Boolean).slice(0,12);
  $("view-kicker").textContent=`${node.type} DRILL-DOWN`;$("view-title").textContent=node.label;
  $("view-summary").textContent=`Selected node plus ${neighbors.length} directly related generated facts.`;
  $("map").innerHTML=`<div class="neighborhood">${nodeCard(node,true)}${neighbors.map(n=>nodeCard(n)).join("")}</div>`;
  renderDetail(node,related);renderBreadcrumb();bindNodes();
}
function renderDetail(node,related){
  const src=node.source?`${node.source.path}:${node.source.line}`:"";const evidence=evidenceForNode(node);
  const annotations=node.details?.annotations||[];const sql=node.details?.statement;
  const relatedNodes=[...new Set(related.map(e=>e.from===node.id?e.to:e.from))].map(id=>state.nodes.get(id)).filter(Boolean);
  $("detail").innerHTML=`<span class="eyebrow">${esc(node.type)}</span><h3>${esc(node.label)}</h3><p>${esc(description(node))}</p><div>${evidence.length?evidence.map(badge).join(""):'<span class="muted">No edge evidence</span>'}</div><dl>${src?`<dt>SOURCE</dt><dd><code>${esc(src)}</code></dd>`:""}${annotations.length?`<dt>ANNOTATIONS</dt><dd>${annotations.map(a=>`<span class="pill">@${esc(a)}</span>`).join("")}</dd>`:""}${node.details?.package?`<dt>PACKAGE</dt><dd>${esc(node.details.package)}</dd>`:""}${node.details?.owner?`<dt>CONTAINING TYPE</dt><dd>${esc(node.details.owner)}</dd>`:""}${sql?`<dt>SQL</dt><dd class="sql">${esc(sql)}</dd>`:""}<dt>RELATED</dt><dd>${relatedNodes.length?relatedNodes.map(n=>`<button class="pill result" data-node="${esc(n.id)}">${esc(n.label)}</button>`).join(""):"No generated relationships"}</dd></dl>`;
}
function renderBreadcrumb(){$("breadcrumb").innerHTML=state.history.map((item,index)=>`${index?'<span class="crumb-sep">›</span> ':''}<button data-crumb="${index}">${esc(item.label)}</button>`).join("");document.querySelectorAll("[data-crumb]").forEach(el=>el.addEventListener("click",()=>Number(el.dataset.crumb)===0?renderSystem():navigate(state.history[Number(el.dataset.crumb)].id)))}
function setupSearch(){const input=$("search"),results=$("search-results");input.addEventListener("input",()=>{const q=input.value.trim().toLowerCase();if(!q){results.innerHTML="";return}const found=[...state.nodes.values()].filter(n=>n.label.toLowerCase().includes(q)).slice(0,10);results.innerHTML=found.map(n=>`<button class="result" data-search="${esc(n.id)}"><strong>${esc(n.label)}</strong> <span class="muted">${esc(n.type)}</span></button>`).join("");document.querySelectorAll("[data-search]").forEach(el=>el.addEventListener("click",()=>{navigate(el.dataset.search);input.value="";results.innerHTML=""}))})}
document.addEventListener("DOMContentLoaded",init);
