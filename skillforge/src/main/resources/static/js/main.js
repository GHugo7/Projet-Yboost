// ─── Date dans le hero ────────────────────────────────
const now = new Date();
const months = ['Janvier','Février','Mars','Avril','Mai','Juin',
                 'Juillet','Août','Septembre','Octobre','Novembre','Décembre'];
const dayEl   = document.getElementById('date-day');
const monthEl = document.getElementById('date-month');
if (dayEl)   dayEl.textContent   = now.getDate();
if (monthEl) monthEl.textContent = months[now.getMonth()] + ' ' + now.getFullYear();

// ─── Modale export ────────────────────────────────────
function closeModal(event) {
  if (event.target.id === 'exportModal') {
    document.getElementById('exportModal').classList.remove('open');
  }
}

// ─── Toast notification ───────────────────────────────
// Affiche le toast 3 secondes puis le fait disparaître
const toast = document.getElementById('toast');
if (toast) {
  toast.classList.add('toast-visible');
  setTimeout(() => {
    toast.classList.remove('toast-visible');
    toast.classList.add('toast-hidden');
  }, 3000);
}

// ─── Onglets ──────────────────────────────────────────
function showTab(name) {
  ['profil', 'historique', 'admin'].forEach(t => {
    const el = document.getElementById('tab-' + t);
    if (el) el.style.display = 'none';
  });
  document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('tab-active'));
  const target = document.getElementById('tab-' + name);
  if (target) target.style.display = name === 'profil' ? 'grid' : 'block';
  event.target.classList.add('tab-active');
}

// ─── Modals ───────────────────────────────────────────
function openModal(id) {
  document.getElementById(id).classList.add('open');
}
function closeModal(id) {
  document.getElementById(id).classList.remove('open');
}
function closeOnOverlay(event, id) {
  if (event.target.id === id) closeModal(id);
}

// ─── Prévisualisation avatar ──────────────────────────
function previewAvatar(input) {
  if (input.files && input.files[0]) {
    const reader = new FileReader();
    reader.onload = e => {
      const imgs = document.querySelectorAll('img[alt="Avatar"]');
      imgs.forEach(img => { img.src = e.target.result; img.style.display = 'block'; });
    };
    reader.readAsDataURL(input.files[0]);
  }
}

// ─── Ouvrir le bon onglet si erreur/succès ────────────
// Si une action admin a été faite, on reste sur l'onglet admin
const urlParams = new URLSearchParams(window.location.search);