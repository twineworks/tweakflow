

document.addEventListener("DOMContentLoaded", event => {
  const dropdownTriggers = Array.from(document.querySelectorAll(".dropdown-trigger"));
  dropdownTriggers.forEach(d => {
    d.addEventListener("click", (e) => {
      e.preventDefault();
      const dropdown = e.target.closest(".dropdown");
      if (dropdown){
        dropdown.classList.toggle("is-active");
      }
      const trigger = e.target.closest(".dropdown-trigger");
      if (trigger){
        const caret = trigger.querySelector("i.caret");
        if (caret) {
          if (caret.classList.contains("fa-caret-right")){
            caret.classList.remove("fa-caret-right");
            caret.classList.add("fa-caret-down");
          }
          else{
            caret.classList.remove("fa-caret-down");
            caret.classList.add("fa-caret-right");
          }
        }
      }

    }, false)
  })

});
