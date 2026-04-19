// Player screen — live, fully interactive
function PlayerScreen({ preset, onBack, onOpenMixer, onOpenTimer }) {
  const band = bandById(preset.band);
  const [playing, setPlaying] = React.useState(true);
  const [position, setPosition] = React.useState(287); // current s
  const [beat, setBeat] = React.useState(preset.beat);
  const [base, setBase] = React.useState(preset.base);
  const [volume, setVolume] = React.useState(72);
  const [timer, setTimer] = React.useState(Math.floor(preset.duration / 60));
  const [liked, setLiked] = React.useState(false);
  const [noise, setNoise] = React.useState(preset.noise);
  const [noiseVol, setNoiseVol] = React.useState(35);

  // live position tick
  React.useEffect(() => {
    if (!playing) return;
    const id = setInterval(() => {
      setPosition(p => Math.min(p + 1, timer * 60));
    }, 1000);
    return () => clearInterval(id);
  }, [playing, timer]);

  const pct = (position / (timer * 60)) * 100;

  return (
    <div className="cosmic-bg" style={{ minHeight: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Top bar */}
      <div style={{ position: 'relative', zIndex: 2, padding: '16px 20px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <button onClick={onBack} style={iconBtn}><Icon.Back /></button>
        <div style={{ textAlign: 'center' }}>
          <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.14em', textTransform: 'uppercase' }}>Now Playing</div>
          <div style={{ fontSize: 13, color: 'var(--ink-dim)', marginTop: 2 }}>{band.name} · {band.range}</div>
        </div>
        <button style={iconBtn}><Icon.More /></button>
      </div>

      {/* Visualizer */}
      <div style={{ position: 'relative', zIndex: 2, display: 'flex', justifyContent: 'center', marginTop: 10, animation: 'breath 6s ease-in-out infinite' }}>
        <Visualizer beat={beat} base={base} playing={playing} size={260} hue={band.hue} />
      </div>

      {/* Title & frequency */}
      <div style={{ position: 'relative', zIndex: 2, textAlign: 'center', padding: '20px 28px 8px' }}>
        <div className="display" style={{ fontSize: 28, fontWeight: 500, letterSpacing: '-0.02em' }}>{preset.title}</div>
        <div className="mono" style={{ marginTop: 8, fontSize: 13, color: 'var(--ink-dim)' }}>
          {base} Hz <span style={{ color: 'var(--ink-mute)' }}>·</span> Δ {beat} Hz <span style={{ color: 'var(--ink-mute)' }}>·</span> {noise === 'none' ? 'no bg' : noise}
        </div>
      </div>

      {/* Progress */}
      <div style={{ position: 'relative', zIndex: 2, padding: '16px 28px 0' }}>
        <div style={{ position: 'relative', height: 4, background: 'rgba(255,255,255,0.08)', borderRadius: 2, overflow: 'hidden' }}>
          <div style={{ position: 'absolute', left: 0, top: 0, bottom: 0, width: `${pct}%`, background: `linear-gradient(90deg, hsl(${band.hue}, 80%, 70%), hsl(${band.hue + 40}, 80%, 70%))`, borderRadius: 2, boxShadow: `0 0 12px hsla(${band.hue}, 90%, 70%, 0.7)` }}/>
          <div style={{ position: 'absolute', left: `calc(${pct}% - 6px)`, top: -4, width: 12, height: 12, borderRadius: '50%', background: '#fff', boxShadow: `0 0 12px hsla(${band.hue}, 90%, 80%, 0.9)` }}/>
        </div>
        <div className="mono" style={{ display: 'flex', justifyContent: 'space-between', marginTop: 10, fontSize: 11, color: 'var(--ink-mute)' }}>
          <span>{fmtTime(position)}</span>
          <span>-{fmtTime(timer * 60 - position)}</span>
        </div>
      </div>

      {/* Controls */}
      <div style={{ position: 'relative', zIndex: 2, display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 28, padding: '20px 0 8px' }}>
        <button onClick={() => setLiked(v => !v)} style={{ ...iconBtn, color: liked ? 'var(--magenta)' : 'var(--ink-dim)' }}>
          <Icon.Heart />
        </button>
        <button style={iconBtn}><Icon.Prev size={26} /></button>
        <button onClick={() => setPlaying(p => !p)} style={{
          width: 72, height: 72, borderRadius: '50%',
          background: `linear-gradient(135deg, hsl(${band.hue}, 90%, 68%), hsl(${band.hue + 40}, 90%, 55%))`,
          boxShadow: `0 10px 30px hsla(${band.hue}, 80%, 50%, 0.4), inset 0 0 0 1px rgba(255,255,255,0.2)`,
          display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#0a0c18',
        }}>
          {playing ? <Icon.Pause size={28} /> : <Icon.Play size={28} />}
        </button>
        <button style={iconBtn}><Icon.Next size={26} /></button>
        <button onClick={onOpenTimer} style={{ ...iconBtn, color: timer !== Math.floor(preset.duration/60) ? 'var(--cyan)' : 'var(--ink-dim)' }}>
          <Icon.Timer />
        </button>
      </div>

      {/* Bottom sheet */}
      <div style={{ position: 'relative', zIndex: 2, marginTop: 'auto', background: 'rgba(16,19,42,0.7)', borderTop: '1px solid var(--line)', padding: '16px 20px 20px', backdropFilter: 'blur(16px)' }}>
        {/* Tabs */}
        <div style={{ display: 'flex', gap: 8, marginBottom: 14 }}>
          <Tab label="Beat" active />
          <Tab label="Noise" />
          <Tab label="Volume" />
        </div>

        {/* Beat controls */}
        <div style={{ display: 'flex', gap: 10, marginBottom: 14, overflowX: 'auto' }} className="no-scrollbar">
          {WAVE_BANDS.map(b => (
            <button key={b.id} onClick={() => { setBeat(b.id === 'delta' ? 2.5 : b.id === 'theta' ? 6 : b.id === 'alpha' ? 10 : b.id === 'beta' ? 18 : 40); }}
              className="mono" style={{
                padding: '6px 12px', borderRadius: 999, fontSize: 11, letterSpacing: '0.04em',
                border: `1px solid ${b.id === preset.band ? `hsla(${b.hue}, 80%, 60%, 0.5)` : 'var(--line)'}`,
                background: b.id === preset.band ? `hsla(${b.hue}, 70%, 50%, 0.18)` : 'rgba(255,255,255,0.02)',
                color: b.id === preset.band ? `hsl(${b.hue}, 85%, 78%)` : 'var(--ink-dim)',
                whiteSpace: 'nowrap', flexShrink: 0,
              }}>{b.name.toUpperCase()} · {b.range}</button>
          ))}
        </div>

        {/* Two slider rows */}
        <SliderRow label="Base" value={base} min={40} max={600} unit="Hz" onChange={setBase} hue={band.hue}/>
        <SliderRow label="Beat" value={beat} min={0.5} max={50} step={0.5} unit="Hz" onChange={setBeat} hue={band.hue}/>
        <SliderRow label="Vol"  value={volume} min={0} max={100} unit="%" onChange={setVolume} hue={band.hue}/>
      </div>
    </div>
  );
}

function Tab({ label, active }) {
  return (
    <div style={{
      padding: '6px 14px', borderRadius: 999,
      background: active ? 'rgba(139,125,255,0.18)' : 'rgba(255,255,255,0.03)',
      border: `1px solid ${active ? 'rgba(139,125,255,0.4)' : 'var(--line)'}`,
      fontSize: 12, color: active ? 'var(--violet-2)' : 'var(--ink-dim)',
      fontFamily: 'JetBrains Mono, monospace', letterSpacing: '0.03em',
    }}>{label}</div>
  );
}

function SliderRow({ label, value, min, max, step = 1, unit, onChange, hue = 260 }) {
  const pct = ((value - min) / (max - min)) * 100;
  return (
    <div style={{ marginBottom: 10 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
        <span className="mono" style={{ fontSize: 11, color: 'var(--ink-mute)', letterSpacing: '0.08em', textTransform: 'uppercase' }}>{label}</span>
        <span className="mono" style={{ fontSize: 12, color: 'var(--ink)' }}>{value}{unit ? ` ${unit}` : ''}</span>
      </div>
      <div style={{ position: 'relative', height: 18, display: 'flex', alignItems: 'center' }}>
        <div style={{ position: 'absolute', inset: '6px 0', background: 'rgba(255,255,255,0.06)', borderRadius: 3 }}/>
        <div style={{ position: 'absolute', left: 0, top: 6, bottom: 6, width: `${pct}%`, background: `linear-gradient(90deg, hsl(${hue}, 80%, 60%), hsl(${hue + 40}, 80%, 70%))`, borderRadius: 3 }}/>
        <div style={{ position: 'absolute', left: `calc(${pct}% - 9px)`, width: 18, height: 18, borderRadius: '50%', background: '#fff', boxShadow: `0 0 10px hsla(${hue}, 90%, 70%, 0.7)` }}/>
        <input type="range" min={min} max={max} step={step} value={value}
          onChange={(e) => onChange(parseFloat(e.target.value))}
          style={{ position: 'absolute', inset: 0, width: '100%', opacity: 0, cursor: 'pointer' }}/>
      </div>
    </div>
  );
}

const iconBtn = {
  width: 40, height: 40, borderRadius: 12,
  display: 'flex', alignItems: 'center', justifyContent: 'center',
  color: 'var(--ink-dim)',
  background: 'rgba(255,255,255,0.03)',
  border: '1px solid var(--line)',
};

window.PlayerScreen = PlayerScreen;
window.SliderRow = SliderRow;
window.iconBtn = iconBtn;
