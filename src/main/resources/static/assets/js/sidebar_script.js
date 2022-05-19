let arrow = document.querySelectorAll(".list__head");
for (var i = 0; i < arrow.length; i++) {
	arrow[i].addEventListener("click", (e)=>{
		let arrowParent = e.target.parentElement.parentElement.parentElement;//selecting main parent of arrow
		arrowParent.classList.toggle("showMenu");
	});
}

let sidebar = document.querySelector(".sidebar");
let sidebarBtn = document.querySelector(".logo-details");
let chevron = document.getElementById("chevron");
let btn_group = document.getElementById("btn_group");
console.log(sidebarBtn);
sidebarBtn.addEventListener("click", ()=>{
  sidebar.classList.toggle("close");
  chevron.classList.toggle("chevrons-r");
  btn_group.classList.toggle("btn_group");

});


window.onload = function() {
	const getDatePickerTitle = elem => {
		// From the label or the aria-label
		const label = elem.nextElementSibling;
		let titleText = '';
		if (label && label.tagName === 'LABEL') {
			titleText = label.textContent;
		} else {
			titleText = elem.getAttribute('aria-label') || '';
		}
		return titleText;
	}

};

