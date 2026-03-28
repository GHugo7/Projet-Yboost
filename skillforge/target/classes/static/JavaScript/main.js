// La date dans le hero
const now = new Date();
const months = ['Janvier','Février','Mars','Avril','Mai','Juin','Juillet','Août','Septembre','Octobre','Novembre','Décembre'];
document.getElementById('date-day').textContent = now.getDate();
document.getElementById('date-month').textContent = months[now.getMonth()] + ' ' + now.getFullYear();

// Menu d'export
function toggleExport() {
  document.getElementById('exportMenu').classList.toggle('open');
}
document.addEventListener('click', function(e) {
  if (!e.target.closest('.export-dropdown')) {
    document.getElementById('exportMenu').classList.remove('open');
  }
});

function closeModal(event) {
  // Ferme uniquement si on clique sur le fond, pas sur la carte
  if (event.target.id === 'exportModal') {
    document.getElementById('exportModal').classList.remove('open');
  }
}