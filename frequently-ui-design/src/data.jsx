// Preset data for Frequently

const WAVE_BANDS = [
  { id: 'delta',  name: 'Delta',  range: '0.5–4 Hz',  desc: 'Deep sleep, healing',         hue: 220, swatch: ['#3b3a8f', '#1a1b3a'] },
  { id: 'theta',  name: 'Theta',  range: '4–8 Hz',    desc: 'Meditation, creativity',      hue: 265, swatch: ['#6b4fd1', '#241a55'] },
  { id: 'alpha',  name: 'Alpha',  range: '8–13 Hz',   desc: 'Calm focus, relaxation',      hue: 195, swatch: ['#3ab5c8', '#12323a'] },
  { id: 'beta',   name: 'Beta',   range: '13–30 Hz',  desc: 'Alert thinking, productivity',hue: 160, swatch: ['#3fc79a', '#0e3128'] },
  { id: 'gamma',  name: 'Gamma',  range: '30–100 Hz', desc: 'Peak cognition, insight',     hue: 45,  swatch: ['#ffb347', '#3a2608'] },
];

const PRESETS = [
  { id: 'p1', title: 'Dream Gateway',  band: 'delta', base: 100, beat: 2.5, duration: 2700, plays: 142, noise: 'rain',    desc: 'Drift into restorative sleep',       art: 0 },
  { id: 'p2', title: 'Moon Meditation',band: 'theta', base: 136.1, beat: 6,  duration: 1800, plays: 98,  noise: 'ocean',   desc: 'Guided to theta hypnagogia',         art: 1 },
  { id: 'p3', title: 'Calm Focus',     band: 'alpha', base: 200, beat: 10, duration: 1800, plays: 320, noise: 'brown',   desc: 'Clear mind, soft attention',          art: 2 },
  { id: 'p4', title: 'Flow State',     band: 'beta',  base: 256, beat: 18, duration: 3600, plays: 211, noise: 'cafe',    desc: 'Deep work, sustained focus',          art: 3 },
  { id: 'p5', title: 'Creative Spark', band: 'gamma', base: 432, beat: 40, duration: 1200, plays: 64,  noise: 'none',    desc: 'Insight, pattern recognition',        art: 4 },
  { id: 'p6', title: 'Lucid Drift',    band: 'theta', base: 144, beat: 7.83,duration: 2400, plays: 77,  noise: 'forest',  desc: 'Schumann resonance, vivid dreams',    art: 5 },
  { id: 'p7', title: 'Morning Ascent', band: 'alpha', base: 220, beat: 12, duration: 900,  plays: 156, noise: 'none',    desc: 'Gentle wake, clear head',             art: 6 },
  { id: 'p8', title: 'Deep Recovery',  band: 'delta', base: 110, beat: 3,  duration: 3600, plays: 43,  noise: 'rain',    desc: 'Nap, physical restoration',           art: 7 },
];

const NOISES = [
  { id: 'rain',   name: 'Rain',         desc: 'Soft drizzle' },
  { id: 'ocean',  name: 'Ocean',        desc: 'Waves, distant' },
  { id: 'forest', name: 'Forest',       desc: 'Leaves, birds' },
  { id: 'brown',  name: 'Brown noise',  desc: 'Deep rumble' },
  { id: 'pink',   name: 'Pink noise',   desc: 'Balanced hiss' },
  { id: 'white',  name: 'White noise',  desc: 'Full spectrum' },
  { id: 'cafe',   name: 'Cafe',         desc: 'Murmur, clinks' },
  { id: 'fire',   name: 'Fireplace',    desc: 'Crackle, pop' },
];

const HISTORY = [
  { id: 'h1', title: 'Calm Focus',    band: 'alpha', when: 'Today, 9:14 AM',    duration: 1800, completed: true },
  { id: 'h2', title: 'Flow State',    band: 'beta',  when: 'Today, 6:02 AM',    duration: 2400, completed: true },
  { id: 'h3', title: 'Dream Gateway', band: 'delta', when: 'Yesterday, 10:45 PM', duration: 2700, completed: true },
  { id: 'h4', title: 'Moon Meditation', band: 'theta', when: 'Yesterday, 8:30 PM', duration: 1520, completed: false },
  { id: 'h5', title: 'Custom — 7.83 Hz', band: 'theta', when: 'Apr 16, 11:02 PM', duration: 1800, completed: true },
  { id: 'h6', title: 'Morning Ascent', band: 'alpha', when: 'Apr 16, 7:15 AM',  duration: 900,  completed: true },
  { id: 'h7', title: 'Creative Spark', band: 'gamma', when: 'Apr 15, 2:30 PM',  duration: 1200, completed: true },
  { id: 'h8', title: 'Deep Recovery', band: 'delta', when: 'Apr 14, 3:10 PM',   duration: 2100, completed: false },
];

const SAVED_TRACKS = [
  { id: 's1', title: 'Evening Unwind', segments: 3, duration: 3600, updated: 'Apr 17', bands: ['alpha','theta','delta'] },
  { id: 's2', title: 'Morning Ramp',   segments: 2, duration: 1500, updated: 'Apr 15', bands: ['alpha','beta'] },
  { id: 's3', title: 'Study Session',  segments: 4, duration: 5400, updated: 'Apr 12', bands: ['beta','alpha','beta','alpha'] },
  { id: 's4', title: 'Pre-Sleep Stack',segments: 3, duration: 2700, updated: 'Apr 08', bands: ['alpha','theta','delta'] },
];

function fmtTime(sec) {
  const m = Math.floor(sec / 60);
  const s = Math.floor(sec % 60);
  return `${m}:${String(s).padStart(2,'0')}`;
}
function fmtDuration(sec) {
  const h = Math.floor(sec / 3600);
  const m = Math.floor((sec % 3600) / 60);
  if (h) return `${h}h ${m}m`;
  return `${m} min`;
}
function bandById(id) { return WAVE_BANDS.find(b => b.id === id); }

Object.assign(window, { WAVE_BANDS, PRESETS, NOISES, HISTORY, SAVED_TRACKS, fmtTime, fmtDuration, bandById });
