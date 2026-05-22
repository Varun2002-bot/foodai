const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => Array.from(root.querySelectorAll(selector));

function setDrawerOpen(name, open) {
  const drawer = document.querySelector(`[data-drawer="${name}"]`);
  if (!drawer) return;
  drawer.classList.toggle("open", open);
  drawer.setAttribute("aria-hidden", open ? "false" : "true");
}

function setSheetOpen(open) {
  const sheet = document.querySelector(`[data-sheet="stop"]`);
  if (!sheet) return;
  sheet.classList.toggle("open", open);
  sheet.setAttribute("aria-hidden", open ? "false" : "true");
}

function setDriverMode(mode) {
  const shell = document.querySelector(".driver-shell");
  if (!shell) return;
  shell.setAttribute("data-driver-mode", mode);
  $$(`[data-action="set-driver-mode"]`).forEach((btn) => {
    btn.classList.toggle("active", btn.getAttribute("data-mode") === mode);
  });

  const coach = $(".coach-body");
  if (coach) {
    coach.innerHTML =
      mode === "ride"
        ? "Next: <b>pickup passenger</b> in 4 min. Keep batch paused — resume after drop."
        : "Swap order: do <b>Pickup #2</b> before Drop #1 to avoid <b>12m idle</b>.";
  }

  const stackTitle = $(".stack-head .h2");
  if (stackTitle) stackTitle.textContent = mode === "ride" ? "Ride Task" : "Batch #214";

  const stackMeta = $(".stack-head .mini-pill");
  if (stackMeta) stackMeta.textContent = mode === "ride" ? "Pickup • Trip • Drop" : "3 pickups • 2 drops";
}

function toggleFocus() {
  const shell = document.querySelector(".driver-shell");
  if (!shell) return;
  const next = shell.getAttribute("data-focus") === "on" ? "off" : "on";
  shell.setAttribute("data-focus", next);
}

function openStopSheet(kind) {
  const title = $("[data-sheet-title]");
  const body = $("[data-sheet-body]");
  if (!title || !body) return;

  if (kind === "pickup") {
    title.textContent = "Pickup";
    body.innerHTML = `
      <div class="mini-card">
        <div class="mini-title">Pickup verification</div>
        <div class="mini-body">Show code / scan QR, confirm items, then tap Complete.</div>
      </div>
      <div class="mini-card">
        <div class="mini-title">AI suggestion</div>
        <div class="mini-body">If wait exceeds 10 min, swap sequence to protect ETA and batch health.</div>
      </div>
      <div class="mini-card warn-card">
        <div class="mini-title">Issue shortcuts</div>
        <div class="mini-body">Store closed • Long wait • Item missing • Wrong code</div>
      </div>
    `;
  } else {
    title.textContent = "Dropoff";
    body.innerHTML = `
      <div class="mini-card">
        <div class="mini-title">Delivery details</div>
        <div class="mini-body">Contactless + OTP enabled. Gate note: North 2. Customer prefers quiet drop.</div>
      </div>
      <div class="mini-card">
        <div class="mini-title">Proof of delivery</div>
        <div class="mini-body">OTP or photo proof (if OTP fails). Auto-advance to the next stop.</div>
      </div>
      <div class="mini-card">
        <div class="mini-title">Safety</div>
        <div class="mini-body">Masked calling, quick incident report, and focus mode prompts.</div>
      </div>
    `;
  }

  setSheetOpen(true);
}

function wireGlobalActions() {
  document.addEventListener("click", (e) => {
    const target = e.target instanceof Element ? e.target.closest("[data-action]") : null;
    if (!target) return;
    const action = target.getAttribute("data-action");

    if (action === "toggle-assistant") return setDrawerOpen("assistant", true);
    if (action === "close-assistant") return setDrawerOpen("assistant", false);
    if (action === "toggle-cart") return setDrawerOpen("cart", true);
    if (action === "close-cart") return setDrawerOpen("cart", false);

    if (action === "toggle-mode") {
      const button = target;
      const text = button.textContent || "";
      const next = text.includes("Food") ? "Grocery" : text.includes("Grocery") ? "Rides" : "Food";
      button.textContent = `Mode: ${next}`;
      return;
    }

    if (action === "set-driver-mode") {
      const mode = target.getAttribute("data-mode") || "delivery";
      return setDriverMode(mode);
    }
    if (action === "toggle-focus") return toggleFocus();

    if (action === "open-pickup") return openStopSheet("pickup");
    if (action === "open-dropoff") return openStopSheet("dropoff");
    if (action === "close-sheet") return setSheetOpen(false);

    if (action === "complete-stop") {
      setSheetOpen(false);
      const etaStrong = $(".eta-strong");
      if (etaStrong) etaStrong.textContent = "Next: 4 min";
      return;
    }
  });

  document.addEventListener("keydown", (e) => {
    if (e.key !== "Escape") return;
    setDrawerOpen("assistant", false);
    setDrawerOpen("cart", false);
    setSheetOpen(false);
  });
}

wireGlobalActions();

