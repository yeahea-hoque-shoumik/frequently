// All static screens — Library, CustomHz, Timer, History, Mixer, Saved, Onboarding, Settings

function LibraryScreen({ onPick, onCustom }) {
  return (
    <div className="cosmic-bg" style={{ minHeight: '100%', paddingBottom: 24 }}>
      <div style={{ position: 'relative', zIndex: 2, padding: '18px 20px 8px' }}>
        <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.14em', textTransform: 'uppercase' }}>Good evening, Arun</div>
        <div className="display" style={{ fontSize: 28, fontWeight: 500, marginTop: 4 }}>Pick your frequency</div>
      </div>
      {/* Search */}
      <div style={{ position: 'relative', zIndex: 2, padding: '12px 20px' }}>
        <div style={{
          display: 'flex', alignItems: 'center', gap: 10, padding: '10px 14px',
          borderRadius: 999, background: 'rgba(255,255,255,0.04)', border: '1px solid var(--line)',
        }}>
          <Icon.Search size={16} />
          <span style={{ color: 'var(--ink-mute)', fontSize: 13 }}>Search presets or Hz…</span>
        </div>
      </div>

      {/* Band chips */}
      <div style={{ position: 'relative', zIndex: 2, display: 'flex', gap: 8, overflowX: 'auto', padding: '4px 20px 14px' }} className="no-scrollbar">
        <span className="chip active">All</span>
        {WAVE_BANDS.map(b => (
          <span key={b.id} className="chip" style={{ whiteSpace: 'nowrap' }}>
            <span style={{ width: 6, height: 6, borderRadius: '50%', background: `hsl(${b.hue}, 80%, 65%)` }}/>
            {b.name}
          </span>
        ))}
      </div>

      {/* Custom Hz card */}
      <div style={{ position: 'relative', zIndex: 2, padding: '0 20px 14px' }}>
        <button onClick={onCustom} style={{
          width: '100%', display: 'flex', alignItems: 'center', gap: 14,
          padding: 16, borderRadius: 18,
          background: 'linear-gradient(135deg, rgba(139,125,255,0.22), rgba(94,240,227,0.12))',
          border: '1px solid rgba(139,125,255,0.35)',
          textAlign: 'left',
        }}>
          <div style={{
            width: 48, height: 48, borderRadius: 14,
            background: 'rgba(0,0,0,0.35)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: 'var(--violet-2)',
          }}>
            <Icon.Waves size={22}/>
          </div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 15, fontWeight: 500 }}>Custom frequency</div>
            <div className="mono" style={{ fontSize: 11, color: 'var(--ink-dim)', marginTop: 2 }}>Dial any base & beat Hz</div>
          </div>
          <Icon.Plus />
        </button>
      </div>

      {/* Section header */}
      <SectionHeader title="Featured presets" caption="8 tracks"/>

      <div style={{ position: 'relative', zIndex: 2, padding: '0 20px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
        {PRESETS.slice(0, 4).map(p => {
          const b = bandById(p.band);
          return (
            <button key={p.id} onClick={() => onPick(p)} style={{
              textAlign: 'left', padding: 12, borderRadius: 18,
              background: 'rgba(255,255,255,0.03)', border: '1px solid var(--line)',
            }}>
              <div style={{
                aspectRatio: '1', borderRadius: 14, marginBottom: 10,
                background: `radial-gradient(circle at 30% 30%, hsl(${b.hue}, 85%, 60%), hsl(${b.hue + 40}, 70%, 20%) 70%, #0a0c18)`,
                position: 'relative', overflow: 'hidden',
                boxShadow: `inset 0 0 0 1px rgba(255,255,255,0.08), 0 0 24px hsla(${b.hue}, 80%, 40%, 0.35)`,
              }}>
                <svg viewBox="0 0 80 80" style={{ position: 'absolute', inset: 0, width: '100%', height: '100%', opacity: 0.5 }}>
                  {[0,1,2,3].map(i => (
                    <circle key={i} cx="40" cy="40" r={12 + i*7}
                      fill="none" stroke={`hsl(${b.hue + i*10}, 90%, 80%)`} strokeWidth="0.4" opacity={0.7 - i*0.15}/>
                  ))}
                </svg>
              </div>
              <div className="mono" style={{ fontSize: 9, color: `hsl(${b.hue}, 80%, 75%)`, letterSpacing: '0.12em', textTransform: 'uppercase' }}>{b.name} · {p.beat}HZ</div>
              <div style={{ fontSize: 14, marginTop: 4 }}>{p.title}</div>
              <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', marginTop: 4 }}>{fmtDuration(p.duration)}</div>
            </button>
          );
        })}
      </div>

      <SectionHeader title="Continue listening" caption="Last played"/>
      <div style={{ position: 'relative', zIndex: 2, padding: '0 20px' }}>
        {PRESETS.slice(4, 8).map(p => {
          const b = bandById(p.band);
          return (
            <button key={p.id} onClick={() => onPick(p)} style={{
              width: '100%', display: 'flex', alignItems: 'center', gap: 12,
              padding: '10px 12px', marginBottom: 8, borderRadius: 14,
              background: 'rgba(255,255,255,0.02)', border: '1px solid var(--line)',
              textAlign: 'left',
            }}>
              <MiniOrb hue={b.hue} size={44} />
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 14 }}>{p.title}</div>
                <div className="mono" style={{ fontSize: 11, color: 'var(--ink-mute)', marginTop: 2 }}>
                  {b.name} · {p.beat} Hz · {fmtDuration(p.duration)}
                </div>
              </div>
              <div style={{
                width: 34, height: 34, borderRadius: '50%',
                background: `hsla(${b.hue}, 80%, 55%, 0.15)`,
                border: `1px solid hsla(${b.hue}, 80%, 60%, 0.3)`,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                color: `hsl(${b.hue}, 85%, 80%)`,
              }}><Icon.Play size={14}/></div>
            </button>
          );
        })}
      </div>
    </div>
  );
}

function SectionHeader({ title, caption }) {
  return (
    <div style={{ position: 'relative', zIndex: 2, padding: '18px 20px 10px', display: 'flex', alignItems: 'baseline', justifyContent: 'space-between' }}>
      <div className="display" style={{ fontSize: 18, fontWeight: 500 }}>{title}</div>
      <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.1em', textTransform: 'uppercase' }}>{caption}</div>
    </div>
  );
}

// ─── Custom Hz ────────────────────────────────────────────
function CustomHzScreen({ onBack }) {
  const [base, setBase] = React.useState(200);
  const [beat, setBeat] = React.useState(7.83);
  const suggestions = [
    { name: 'Schumann', beat: 7.83, band: 'theta' },
    { name: 'Love 528', beat: 528, band: 'gamma' },
    { name: 'OM 136.1', beat: 136.1, band: 'alpha' },
    { name: 'Solfeggio 417', beat: 417, band: 'theta' },
  ];
  return (
    <div className="cosmic-bg" style={{ minHeight: '100%' }}>
      <Header title="Custom frequency" onBack={onBack}/>

      <div style={{ position: 'relative', zIndex: 2, padding: '10px 20px 24px' }}>
        <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.1em', textTransform: 'uppercase', textAlign: 'center' }}>Base carrier</div>
        <div style={{ textAlign: 'center', margin: '8px 0 2px' }}>
          <span className="display" style={{ fontSize: 72, fontWeight: 400, letterSpacing: '-0.04em', background: 'linear-gradient(135deg, #a594ff, #5ef0e3)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>{base}</span>
          <span className="mono" style={{ fontSize: 20, color: 'var(--ink-dim)', marginLeft: 6 }}>Hz</span>
        </div>
        <Keypad />

        <div style={{ height: 18 }}/>
        <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.1em', textTransform: 'uppercase', textAlign: 'center' }}>Binaural beat</div>
        <div style={{ textAlign: 'center', marginTop: 8 }}>
          <span className="display" style={{ fontSize: 44, fontWeight: 400, letterSpacing: '-0.04em' }}>{beat}</span>
          <span className="mono" style={{ fontSize: 14, color: 'var(--ink-dim)', marginLeft: 6 }}>Hz</span>
        </div>
        <div style={{ padding: '8px 12px 14px' }}>
          <SliderRow label="Δ" value={beat} min={0.5} max={50} step={0.1} unit="Hz" onChange={setBeat} hue={265}/>
        </div>

        <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.08em', textTransform: 'uppercase', marginBottom: 8 }}>Quick recall</div>
        <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 18 }}>
          {suggestions.map(s => (
            <span key={s.name} className="chip"><span style={{ width: 6, height: 6, borderRadius: '50%', background: `hsl(${bandById(s.band).hue}, 80%, 65%)` }}/>{s.name}</span>
          ))}
        </div>

        <button style={{
          width: '100%', padding: '14px', borderRadius: 14,
          background: 'linear-gradient(135deg, #8b7dff, #5ef0e3)',
          color: '#0a0c18', fontSize: 14, fontWeight: 600,
          display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
        }}>
          <Icon.Play size={16}/> Play {base} / {beat} Hz
        </button>
      </div>
    </div>
  );
}

function Keypad() {
  const keys = ['1','2','3','4','5','6','7','8','9','.','0','⌫'];
  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3,1fr)', gap: 8, marginTop: 14 }}>
      {keys.map(k => (
        <button key={k} className="mono" style={{
          padding: '14px 0', borderRadius: 14,
          background: 'rgba(255,255,255,0.03)', border: '1px solid var(--line)',
          fontSize: 20, color: 'var(--ink)',
        }}>{k}</button>
      ))}
    </div>
  );
}

// ─── Timer ────────────────────────────────────────────────
function TimerScreen({ onBack }) {
  const [minutes, setMinutes] = React.useState(30);
  const presets = [10, 20, 30, 45, 60, 90];
  return (
    <div className="cosmic-bg" style={{ minHeight: '100%' }}>
      <Header title="Timer" onBack={onBack}/>
      <div style={{ position: 'relative', zIndex: 2, padding: '20px 20px 0', textAlign: 'center' }}>
        <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.12em', textTransform: 'uppercase' }}>Session duration</div>
        {/* Circular dial */}
        <div style={{ position: 'relative', width: 240, height: 240, margin: '24px auto' }}>
          <svg viewBox="0 0 100 100" style={{ width: '100%', height: '100%' }}>
            <circle cx="50" cy="50" r="44" fill="none" stroke="rgba(255,255,255,0.06)" strokeWidth="4"/>
            <circle cx="50" cy="50" r="44" fill="none" stroke="url(#tg)" strokeWidth="4"
              strokeDasharray={`${(minutes/120)*276} 276`} strokeLinecap="round"
              transform="rotate(-90 50 50)"/>
            <defs>
              <linearGradient id="tg" x1="0" y1="0" x2="1" y2="1">
                <stop offset="0" stopColor="#8b7dff"/><stop offset="1" stopColor="#5ef0e3"/>
              </linearGradient>
            </defs>
            {Array.from({length: 24}).map((_, i) => {
              const a = (i / 24) * Math.PI * 2 - Math.PI/2;
              const x1 = 50 + Math.cos(a) * 48, y1 = 50 + Math.sin(a) * 48;
              const x2 = 50 + Math.cos(a) * 50, y2 = 50 + Math.sin(a) * 50;
              return <line key={i} x1={x1} y1={y1} x2={x2} y2={y2} stroke="rgba(255,255,255,0.2)" strokeWidth="0.3"/>;
            })}
          </svg>
          <div style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
            <div className="display" style={{ fontSize: 68, fontWeight: 400, letterSpacing: '-0.04em' }}>{minutes}</div>
            <div className="mono" style={{ fontSize: 11, color: 'var(--ink-dim)', letterSpacing: '0.14em' }}>MINUTES</div>
          </div>
        </div>
        <div style={{ display: 'flex', gap: 8, justifyContent: 'center', flexWrap: 'wrap', marginBottom: 22 }}>
          {presets.map(p => (
            <button key={p} onClick={() => setMinutes(p)}
              className="mono" style={{
                padding: '8px 14px', borderRadius: 999, fontSize: 12,
                background: minutes === p ? 'rgba(139,125,255,0.2)' : 'rgba(255,255,255,0.03)',
                border: `1px solid ${minutes === p ? 'rgba(139,125,255,0.4)' : 'var(--line)'}`,
                color: minutes === p ? 'var(--violet-2)' : 'var(--ink-dim)',
              }}>{p}m</button>
          ))}
        </div>
        <div style={{ padding: '0 20px' }}>
          <SliderRow label="Duration" value={minutes} min={1} max={120} unit="min" onChange={setMinutes} hue={265}/>
        </div>
        <div style={{ padding: 20 }}>
          <div style={{ padding: 14, borderRadius: 14, background: 'rgba(255,255,255,0.03)', border: '1px solid var(--line)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ textAlign: 'left' }}>
              <div style={{ fontSize: 13 }}>Fade out at end</div>
              <div className="mono" style={{ fontSize: 11, color: 'var(--ink-mute)' }}>30s gentle ramp</div>
            </div>
            <Toggle on />
          </div>
        </div>
      </div>
    </div>
  );
}

// ─── History ──────────────────────────────────────────────
function HistoryScreen({ onBack }) {
  const totalMin = HISTORY.reduce((a, h) => a + h.duration, 0) / 60;
  return (
    <div className="cosmic-bg" style={{ minHeight: '100%' }}>
      <Header title="Session history" onBack={onBack}/>
      {/* Stats */}
      <div style={{ position: 'relative', zIndex: 2, padding: '0 20px 16px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3,1fr)', gap: 10 }}>
          <Stat value="12" label="This week" accent="violet"/>
          <Stat value={`${Math.round(totalMin/60)}h`} label="Total time" accent="cyan"/>
          <Stat value="5" label="Day streak" accent="magenta"/>
        </div>
      </div>
      {/* 7-day bar chart */}
      <div style={{ position: 'relative', zIndex: 2, padding: '0 20px 6px' }}>
        <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.1em', textTransform: 'uppercase', marginBottom: 10 }}>Last 7 days</div>
        <div style={{ display: 'flex', alignItems: 'flex-end', gap: 6, height: 90, padding: '0 4px' }}>
          {[40, 65, 20, 80, 55, 100, 72].map((v, i) => (
            <div key={i} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6 }}>
              <div style={{
                width: '100%', height: `${v}%`, borderRadius: 6,
                background: `linear-gradient(180deg, hsla(${265 - i*10}, 80%, 65%, 0.95), hsla(${265 - i*10}, 80%, 40%, 0.4))`,
                boxShadow: `0 0 12px hsla(${265 - i*10}, 80%, 50%, 0.25)`,
              }}/>
              <div className="mono" style={{ fontSize: 9, color: 'var(--ink-mute)' }}>{['M','T','W','T','F','S','S'][i]}</div>
            </div>
          ))}
        </div>
      </div>

      {/* List */}
      <div style={{ position: 'relative', zIndex: 2, padding: '12px 20px 20px' }}>
        <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.1em', textTransform: 'uppercase', marginBottom: 10 }}>Recent sessions</div>
        {HISTORY.map(h => {
          const b = bandById(h.band);
          return (
            <div key={h.id} style={{
              display: 'flex', alignItems: 'center', gap: 12,
              padding: '12px 0', borderBottom: '1px solid var(--line)',
            }}>
              <MiniOrb hue={b.hue} size={40}/>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 14, display: 'flex', alignItems: 'center', gap: 8 }}>
                  {h.title}
                  {!h.completed && <span className="mono" style={{ fontSize: 9, color: 'var(--amber)', padding: '2px 6px', borderRadius: 999, border: '1px solid rgba(255,197,107,0.4)' }}>STOPPED</span>}
                </div>
                <div className="mono" style={{ fontSize: 11, color: 'var(--ink-mute)', marginTop: 2 }}>
                  {h.when} · {b.name} · {fmtDuration(h.duration)}
                </div>
              </div>
              <Icon.Play size={14}/>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function Stat({ value, label, accent }) {
  const c = { violet: '#a594ff', cyan: '#5ef0e3', magenta: '#ff7fd4' }[accent];
  return (
    <div style={{ padding: '12px', borderRadius: 14, background: 'rgba(255,255,255,0.03)', border: '1px solid var(--line)' }}>
      <div className="display" style={{ fontSize: 24, fontWeight: 500, color: c }}>{value}</div>
      <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.08em', textTransform: 'uppercase', marginTop: 2 }}>{label}</div>
    </div>
  );
}

// ─── Mixer ────────────────────────────────────────────────
function MixerScreen({ onBack }) {
  const [active, setActive] = React.useState(['rain', 'brown']);
  const [vols] = React.useState({ rain: 60, brown: 35, ocean: 45, forest: 40, pink: 30, white: 25, cafe: 50, fire: 40 });
  return (
    <div className="cosmic-bg" style={{ minHeight: '100%' }}>
      <Header title="Background mixer" onBack={onBack}/>
      <div style={{ position: 'relative', zIndex: 2, padding: '0 20px 20px' }}>
        <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.1em', textTransform: 'uppercase', marginBottom: 10 }}>Stack layers · Long-press to reorder</div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          {NOISES.map(n => {
            const on = active.includes(n.id);
            return (
              <button key={n.id} onClick={() => setActive(a => a.includes(n.id) ? a.filter(x => x !== n.id) : [...a, n.id])}
                style={{
                  textAlign: 'left', padding: 14, borderRadius: 16,
                  background: on ? 'rgba(94,240,227,0.1)' : 'rgba(255,255,255,0.03)',
                  border: `1px solid ${on ? 'rgba(94,240,227,0.4)' : 'var(--line)'}`,
                }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                  <div style={{
                    width: 28, height: 28, borderRadius: 8,
                    background: on ? 'linear-gradient(135deg, #5ef0e3, #8b7dff)' : 'rgba(255,255,255,0.05)',
                    color: on ? '#0a0c18' : 'var(--ink-mute)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                  }}>
                    {on ? <Icon.Check size={14}/> : <Icon.Plus size={14}/>}
                  </div>
                  {on && <div className="mono" style={{ fontSize: 10, color: 'var(--cyan)' }}>{vols[n.id]}%</div>}
                </div>
                <div style={{ fontSize: 14 }}>{n.name}</div>
                <div className="mono" style={{ fontSize: 11, color: 'var(--ink-mute)', marginTop: 2 }}>{n.desc}</div>
                {on && (
                  <div style={{ marginTop: 10, height: 3, background: 'rgba(255,255,255,0.06)', borderRadius: 2, overflow: 'hidden' }}>
                    <div style={{ width: `${vols[n.id]}%`, height: '100%', background: 'linear-gradient(90deg, #5ef0e3, #8b7dff)' }}/>
                  </div>
                )}
              </button>
            );
          })}
        </div>

        {/* Active mix faders */}
        {active.length > 0 && (
          <div style={{ marginTop: 20, padding: 16, borderRadius: 20, background: 'rgba(16,19,42,0.7)', border: '1px solid var(--line)' }}>
            <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.12em', textTransform: 'uppercase', marginBottom: 12 }}>Active mix · {active.length} layers</div>
            {active.map(id => {
              const n = NOISES.find(x => x.id === id);
              return <SliderRow key={id} label={n.name} value={vols[id]} min={0} max={100} unit="%" onChange={() => {}} hue={180}/>;
            })}
          </div>
        )}
      </div>
    </div>
  );
}

// ─── Saved ────────────────────────────────────────────────
function SavedScreen({ onBack, onNew }) {
  return (
    <div className="cosmic-bg" style={{ minHeight: '100%' }}>
      <Header title="My tracks" onBack={onBack} rightIcon={<Icon.Plus/>} onRight={onNew}/>
      <div style={{ position: 'relative', zIndex: 2, padding: '0 20px 20px' }}>
        <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.1em', textTransform: 'uppercase', marginBottom: 12 }}>{SAVED_TRACKS.length} tracks · stored on device</div>
        {SAVED_TRACKS.map(t => (
          <div key={t.id} style={{
            display: 'flex', alignItems: 'center', gap: 12,
            padding: '14px', marginBottom: 10, borderRadius: 16,
            background: 'rgba(255,255,255,0.03)', border: '1px solid var(--line)',
          }}>
            {/* Composite orb showing stacked bands */}
            <div style={{ width: 56, height: 56, borderRadius: 14, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
              {t.bands.map((bId, i) => {
                const b = bandById(bId);
                return <div key={i} style={{ flex: 1, background: `linear-gradient(90deg, hsl(${b.hue}, 70%, 50%), hsl(${b.hue + 40}, 70%, 35%))` }}/>;
              })}
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 14 }}>{t.title}</div>
              <div className="mono" style={{ fontSize: 11, color: 'var(--ink-mute)', marginTop: 2 }}>
                {t.segments} segments · {fmtDuration(t.duration)} · {t.updated}
              </div>
              <div style={{ display: 'flex', gap: 4, marginTop: 6 }}>
                {t.bands.map((bId, i) => {
                  const b = bandById(bId);
                  return <span key={i} className="mono" style={{ fontSize: 9, padding: '2px 6px', borderRadius: 999, background: `hsla(${b.hue}, 70%, 50%, 0.2)`, color: `hsl(${b.hue}, 85%, 78%)`, letterSpacing: '0.04em' }}>{b.name.toUpperCase()}</span>;
                })}
              </div>
            </div>
            <button style={{
              width: 38, height: 38, borderRadius: '50%',
              background: 'linear-gradient(135deg, #8b7dff, #5ef0e3)',
              color: '#0a0c18',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              <Icon.Play size={14}/>
            </button>
          </div>
        ))}
        <button onClick={onNew} style={{
          width: '100%', padding: '16px', borderRadius: 16,
          border: '1px dashed var(--line-2)', background: 'rgba(255,255,255,0.02)',
          color: 'var(--ink-dim)', fontSize: 13,
          display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
        }}>
          <Icon.Plus size={16}/> Build new track
        </button>
      </div>
    </div>
  );
}

// ─── Onboarding ───────────────────────────────────────────
function OnboardingScreen({ onDone }) {
  return (
    <div className="cosmic-bg" style={{ minHeight: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', position: 'relative', zIndex: 2, padding: '0 32px' }}>
        <div style={{ animation: 'breath 5s ease-in-out infinite' }}>
          <Visualizer beat={8} base={200} playing={true} size={240} hue={265}/>
        </div>
        <div className="display" style={{ fontSize: 44, fontWeight: 400, letterSpacing: '-0.03em', marginTop: 32, textAlign: 'center', lineHeight: 1.05 }}>
          Tune your <em style={{ fontStyle: 'italic', background: 'linear-gradient(135deg, #a594ff, #5ef0e3)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>brain</em><br/>to any frequency
        </div>
        <div style={{ fontSize: 15, color: 'var(--ink-dim)', marginTop: 16, textAlign: 'center', lineHeight: 1.5, maxWidth: 300 }}>
          Stack binaural beats, ambient layers, and timed sessions. For focus, sleep, and everything in between.
        </div>
      </div>
      <div style={{ padding: '0 24px 32px', position: 'relative', zIndex: 2 }}>
        <div style={{ display: 'flex', gap: 6, justifyContent: 'center', marginBottom: 20 }}>
          <span style={{ width: 24, height: 4, borderRadius: 2, background: 'var(--violet-2)' }}/>
          <span style={{ width: 4, height: 4, borderRadius: 2, background: 'var(--ink-mute)' }}/>
          <span style={{ width: 4, height: 4, borderRadius: 2, background: 'var(--ink-mute)' }}/>
        </div>
        <button onClick={onDone} style={{
          width: '100%', padding: '16px', borderRadius: 16,
          background: 'linear-gradient(135deg, #8b7dff, #5ef0e3)',
          color: '#0a0c18', fontSize: 15, fontWeight: 600,
          display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 10,
        }}>
          <Icon.Headphones size={18}/> Put on headphones & begin
        </button>
        <div className="mono" style={{ fontSize: 11, color: 'var(--ink-mute)', textAlign: 'center', marginTop: 14, letterSpacing: '0.04em' }}>
          Binaural beats require stereo headphones to work.
        </div>
      </div>
    </div>
  );
}

// ─── Settings ─────────────────────────────────────────────
function SettingsScreen({ onBack }) {
  return (
    <div className="cosmic-bg" style={{ minHeight: '100%' }}>
      <Header title="Settings" onBack={onBack}/>
      <div style={{ position: 'relative', zIndex: 2, padding: '0 20px 20px' }}>
        <SettingsGroup title="Audio">
          <SettingsRow label="High-quality audio" caption="32-bit float synthesis" right={<Toggle on/>}/>
          <SettingsRow label="Auto fade in/out" caption="10s ramp at session edges" right={<Toggle on/>}/>
          <SettingsRow label="Crossfade between segments" caption="3s smooth transitions" right={<Toggle/>}/>
          <SettingsRow label="Max volume safety" caption="Limit to 85 dB" right={<Toggle on/>}/>
        </SettingsGroup>

        <SettingsGroup title="Session">
          <SettingsRow label="Default timer" caption="30 minutes" right={<span className="mono" style={{ fontSize: 12, color: 'var(--ink-dim)' }}>30m ›</span>}/>
          <SettingsRow label="Default base carrier" caption="200 Hz" right={<span className="mono" style={{ fontSize: 12, color: 'var(--ink-dim)' }}>200Hz ›</span>}/>
          <SettingsRow label="Screen dim while playing" caption="Lower distraction" right={<Toggle on/>}/>
          <SettingsRow label="Background noise default" caption="None" right={<span className="mono" style={{ fontSize: 12, color: 'var(--ink-dim)' }}>none ›</span>}/>
        </SettingsGroup>

        <SettingsGroup title="Data">
          <SettingsRow label="Session history" caption="Kept for 90 days" right={<span className="mono" style={{ fontSize: 12, color: 'var(--ink-dim)' }}>90d ›</span>}/>
          <SettingsRow label="Export saved tracks" caption=".wav bundle · on-device" right={<Icon.Share/>}/>
          <SettingsRow label="Clear session data" caption="Cannot be undone" right={<span style={{ color: 'var(--danger)', fontSize: 12 }}>Clear</span>}/>
        </SettingsGroup>

        <div className="mono" style={{ textAlign: 'center', fontSize: 10, color: 'var(--ink-mute)', marginTop: 20, letterSpacing: '0.1em' }}>
          FREQUENTLY v1.0.3 · BUILD 241
        </div>
      </div>
    </div>
  );
}

function SettingsGroup({ title, children }) {
  return (
    <div style={{ marginTop: 18 }}>
      <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.14em', textTransform: 'uppercase', marginBottom: 8, padding: '0 4px' }}>{title}</div>
      <div style={{ borderRadius: 16, background: 'rgba(255,255,255,0.03)', border: '1px solid var(--line)' }}>
        {children}
      </div>
    </div>
  );
}
function SettingsRow({ label, caption, right }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', gap: 12,
      padding: '14px 14px',
      borderBottom: '1px solid var(--line)',
    }}>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 14 }}>{label}</div>
        {caption && <div className="mono" style={{ fontSize: 11, color: 'var(--ink-mute)', marginTop: 2 }}>{caption}</div>}
      </div>
      <div style={{ color: 'var(--ink-dim)' }}>{right}</div>
    </div>
  );
}
function Toggle({ on }) {
  return (
    <div style={{
      width: 42, height: 24, borderRadius: 999,
      background: on ? 'linear-gradient(135deg, #8b7dff, #5ef0e3)' : 'rgba(255,255,255,0.1)',
      position: 'relative',
      border: '1px solid var(--line)',
    }}>
      <div style={{
        position: 'absolute', top: 2, left: on ? 20 : 2,
        width: 18, height: 18, borderRadius: '50%', background: '#fff',
        transition: 'left 0.2s',
      }}/>
    </div>
  );
}

function Header({ title, onBack, rightIcon, onRight }) {
  return (
    <div style={{ position: 'relative', zIndex: 2, padding: '16px 20px 8px', display: 'flex', alignItems: 'center', gap: 12 }}>
      <button onClick={onBack} style={iconBtn}><Icon.Back /></button>
      <div style={{ flex: 1 }}>
        <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.14em', textTransform: 'uppercase' }}>Frequently</div>
        <div className="display" style={{ fontSize: 22, fontWeight: 500, marginTop: 2 }}>{title}</div>
      </div>
      {rightIcon && <button onClick={onRight} style={iconBtn}>{rightIcon}</button>}
    </div>
  );
}

Object.assign(window, {
  LibraryScreen, CustomHzScreen, TimerScreen, HistoryScreen,
  MixerScreen, SavedScreen, OnboardingScreen, SettingsScreen,
  Header, Toggle,
});
