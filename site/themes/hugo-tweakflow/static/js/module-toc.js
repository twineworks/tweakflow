function extractTags(t) {
  const str = t.getAttribute("data-meta-tags");
  if (str == null) return [];
  if (str.trim().length == 0) return [];
  return str.trim().split(" ");
}

function extractMetaData() {
  const allMeta = document.querySelectorAll("div[data-meta]");
  const metaData = Array.from(allMeta).map(t => {
    return {
      id: t.getAttribute("data-meta-id"),
      type: t.getAttribute("data-meta-type"),
      name: t.getAttribute("data-meta-name"),
      tags: extractTags(t)
    };
  });

  return metaData;
}

function makeResultNode(meta) {
  const li = document.createElement("li");
  li.className = "search-result";
  li.setAttribute("data-meta-id", meta.id);
  li.setAttribute("data-meta-type", meta.type);
  li.setAttribute("data-meta-name", meta.name);
  li.setAttribute("data-meta-tags", meta.tags.join(" "));

  const a = document.createElement("a");
  a.href = "#" + meta.id;
  const txt = document.createTextNode(meta.name);
  a.appendChild(txt);

  meta.tags.forEach(tag => {
    const label = document.createElement("span");
    label.className = "label";
    const labelTxt = document.createTextNode(tag);
    label.appendChild(labelTxt);
    a.appendChild(label);
  });

  if (meta.type == "library") {
    const label = document.createElement("span");
    label.className = "label";
    const labelTxt = document.createTextNode("library");
    label.appendChild(labelTxt);
    a.appendChild(label);
  }

  if (meta.type == "module") {
    const label = document.createElement("span");
    label.className = "label";
    const labelTxt = document.createTextNode("module");
    label.appendChild(labelTxt);
    a.appendChild(label);
  }

  li.appendChild(a);
  return li;
}

function buildSearchables(parentNode) {
  const metaData = extractMetaData();
  metaData.forEach(meta => {
    parentNode.appendChild(makeResultNode(meta));
  });
}

function strContains(x, s){
  const xl = x.toLowerCase();
  const sl = s.toLowerCase();
  return xl.indexOf(sl) >= 0;
}

function searchMatch(term, node){
  const searchIn = [];
  searchIn.push(
    node.getAttribute("data-meta-type"),
    node.getAttribute("data-meta-name"),
    node.getAttribute("data-meta-tags")
  );
  const txt = searchIn.join(" ");
  return strContains(txt, term);

}

function searchToc(e){
  const term = e.target.value;
  const nodes = document.querySelectorAll(".search-results .search-result");
  Array.from(nodes).forEach(n => {

    if (searchMatch(term, n)){
      n.classList.remove("hidden");
    }
    else{
      n.classList.add("hidden");
    }

  })

}

document.addEventListener("DOMContentLoaded", event => {
  buildSearchables(document.querySelector(".search-results"));
  const searchInput = document.querySelector(".search-box input");
  searchInput.addEventListener('input', searchToc, false);
});
