// Track Builder — live, stack multiple segments
function BuilderScreen({ onBack, onPlay }) {
  const [name, setName] = React.useState('Evening Unwind');
  const [segments, setSegments] = React.useState([
    { id: 'a', band: 'alpha', base: 220, beat: 10, duration: 600, noise: 'rain' },
    { id: 'b', band: 'theta', base: 144, beat: 6, duration: 900, noise: 'rain' },
    { id: 'c', band: 'delta', base: 110, beat: 2.5, duration: 1200, noise: 'ocean' },
  ]);
  const [selected, setSelected] = React.useState('b');
  const seg = segments.find(s => s.id === selected);
  const total = segments.reduce((a, s) => a + s.duration, 0);

  const updateSeg = (patch) => {
    setSegments(segs => segs.map(s => s.id === selected ? { ...s, ...patch } : s));
  };

  const removeSeg = (id) => {
    setSegments(segs => {
      const next = segs.filter(s => s.id !== id);
      if (id === selected && next[0]) setSelected(next[0].id);
      return next;
    });
  };

  const addSeg = () => {
    const id = Math.random().toString(36).slice(2, 6);
    const ns = { id, band: 'alpha', base: 200, beat: 10, duration: 600, noise: 'none' };
    setSegments(s => [...s, ns]);
    setSelected(id);
  };

  return (
    <div className="cosmic-bg" style={{ minHeight: '100%' }}>
      {/* Header */}
      <div style={{ position: 'relative', zIndex: 2, padding: '16px 20px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <button onClick={onBack} style={iconBtn}><Icon.Back /></button>
        <div style={{ flex: 1 }}>
          <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.14em', textTransform: 'uppercase' }}>Track Builder</div>
          <input value={name} onChange={e => setName(e.target.value)}
            className="display"
            style={{ background: 'transparent', border: 0, outline: 0, color: 'var(--ink)', fontSize: 20, fontWeight: 500, width: '100%', padding: 0, marginTop: 2 }}/>
        </div>
        <button style={{ ...iconBtn, background: 'rgba(139,125,255,0.18)', borderColor: 'rgba(139,125,255,0.4)', color: 'var(--violet-2)', width: 'auto', padding: '0 14px', gap: 6 }}>
          <Icon.Save size={16} />
          <span style={{ fontSize: 13 }}>Save</span>
        </button>
      </div>

      {/* Timeline summary */}
      <div style={{ position: 'relative', zIndex: 2, padding: '0 20px 14px' }}>
        <div className="mono" style={{ display: 'flex', justifyContent: 'space-between', fontSize: 11, color: 'var(--ink-mute)', marginBottom: 8, letterSpacing: '0.06em' }}>
          <span>TIMELINE · {segments.length} SEGMENTS</span>
          <span>TOTAL {fmtDuration(total)}</span>
        </div>
        {/* Timeline bar */}
        <div style={{ display: 'flex', height: 40, borderRadius: 10, overflow: 'hidden', border: '1px solid var(--line)' }}>
          {segments.map(s => {
            const b = bandById(s.band);
            const w = (s.duration / total) * 100;
            const isSel = s.id === selected;
            return (
              <button key={s.id} onClick={() => setSelected(s.id)}
                style={{
                  width: `${w}%`, position: 'relative',
                  background: `linear-gradient(135deg, hsla(${b.hue}, 75%, 55%, ${isSel ? 0.9 : 0.45}), hsla(${b.hue + 40}, 70%, 35%, ${isSel ? 0.9 : 0.4}))`,
                  borderRight: '1px solid rgba(0,0,0,0.4)',
                  boxShadow: isSel ? `inset 0 0 0 2px hsla(${b.hue}, 90%, 75%, 0.9)` : 'none',
                  color: '#fff', fontSize: 10, letterSpacing: '0.06em',
                  fontFamily: 'JetBrains Mono, monospace',
                  textTransform: 'uppercase',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                }}>
                {b.name}
              </button>
            );
          })}
        </div>
      </div>

      {/* Segment list */}
      <div style={{ position: 'relative', zIndex: 2, padding: '0 20px' }}>
        {segments.map((s, i) => {
          const b = bandById(s.band);
          const isSel = s.id === selected;
          return (
            <div key={s.id} onClick={() => setSelected(s.id)}
              style={{
                display: 'flex', alignItems: 'center', gap: 12,
                padding: '12px 14px', marginBottom: 8, borderRadius: 14,
                background: isSel ? `hsla(${b.hue}, 60%, 30%, 0.22)` : 'rgba(255,255,255,0.03)',
                border: `1px solid ${isSel ? `hsla(${b.hue}, 70%, 55%, 0.5)` : 'var(--line)'}`,
                cursor: 'pointer',
              }}>
              <Icon.Drag />
              <div className="mono" style={{ fontSize: 11, color: 'var(--ink-mute)', width: 20 }}>{String(i+1).padStart(2,'0')}</div>
              <MiniOrb hue={b.hue} size={36} />
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 14, display: 'flex', alignItems: 'center', gap: 8 }}>
                  <span>{b.name}</span>
                  <span className="mono" style={{ fontSize: 11, color: 'var(--ink-mute)' }}>· {s.base}/{s.beat} Hz</span>
                </div>
                <div className="mono" style={{ fontSize: 11, color: 'var(--ink-dim)', marginTop: 2 }}>
                  {fmtDuration(s.duration)} · {s.noise}
                </div>
              </div>
              <button onClick={(e) => { e.stopPropagation(); removeSeg(s.id); }}
                style={{ ...iconBtn, width: 32, height: 32, color: 'var(--ink-mute)', background: 'transparent', border: 0 }}>
                <Icon.Trash />
              </button>
            </div>
          );
        })}
        <button onClick={addSeg} style={{
          width: '100%', padding: '14px', borderRadius: 14,
          border: '1px dashed var(--line-2)', background: 'rgba(255,255,255,0.02)',
          color: 'var(--ink-dim)', fontSize: 13,
          display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8, marginBottom: 16,
        }}>
          <Icon.Plus size={16}/> Add segment
        </button>
      </div>

      {/* Editor for selected */}
      {seg && (
        <div style={{ position: 'relative', zIndex: 2, margin: '0 20px 20px', padding: '16px', borderRadius: 20, background: 'rgba(16,19,42,0.7)', border: '1px solid var(--line)', backdropFilter: 'blur(16px)' }}>
          <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.14em', textTransform: 'uppercase', marginBottom: 12 }}>
            Editing segment · {segments.findIndex(x => x.id === seg.id) + 1}
          </div>

          <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.08em', marginBottom: 8, textTransform: 'uppercase' }}>Wave</div>
          <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', marginBottom: 14 }}>
            {WAVE_BANDS.map(b => (
              <button key={b.id} onClick={() => updateSeg({ band: b.id })}
                className="mono"
                style={{
                  padding: '6px 10px', borderRadius: 999, fontSize: 10,
                  letterSpacing: '0.04em', textTransform: 'uppercase',
                  border: `1px solid ${seg.band === b.id ? `hsla(${b.hue}, 80%, 60%, 0.5)` : 'var(--line)'}`,
                  background: seg.band === b.id ? `hsla(${b.hue}, 70%, 50%, 0.2)` : 'transparent',
                  color: seg.band === b.id ? `hsl(${b.hue}, 85%, 78%)` : 'var(--ink-dim)',
                }}>{b.name}</button>
            ))}
          </div>

          <SliderRow label="Base" value={seg.base} min={40} max={600} unit="Hz" hue={bandById(seg.band).hue}
            onChange={(v) => updateSeg({ base: v })}/>
          <SliderRow label="Beat" value={seg.beat} min={0.5} max={50} step={0.5} unit="Hz" hue={bandById(seg.band).hue}
            onChange={(v) => updateSeg({ beat: v })}/>
          <SliderRow label="Duration" value={Math.floor(seg.duration/60)} min={1} max={90} unit="min" hue={bandById(seg.band).hue}
            onChange={(v) => updateSeg({ duration: v * 60 })}/>

          <div className="mono" style={{ fontSize: 10, color: 'var(--ink-mute)', letterSpacing: '0.08em', marginTop: 14, marginBottom: 8, textTransform: 'uppercase' }}>Background</div>
          <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
            {['none', 'rain', 'ocean', 'forest', 'brown', 'cafe'].map(n => (
              <button key={n} onClick={() => updateSeg({ noise: n })}
                className="mono"
                style={{
                  padding: '5px 10px', borderRadius: 999, fontSize: 10,
                  letterSpacing: '0.04em',
                  border: `1px solid ${seg.noise === n ? 'rgba(94,240,227,0.5)' : 'var(--line)'}`,
                  background: seg.noise === n ? 'rgba(94,240,227,0.15)' : 'transparent',
                  color: seg.noise === n ? 'var(--cyan)' : 'var(--ink-dim)',
                }}>{n}</button>
            ))}
          </div>

          <button onClick={onPlay} style={{
            width: '100%', marginTop: 18, padding: '14px', borderRadius: 14,
            background: 'linear-gradient(135deg, #8b7dff, #5ef0e3)',
            color: '#0a0c18', fontSize: 14, fontWeight: 600, letterSpacing: '0.02em',
            display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
          }}>
            <Icon.Play size={16}/> Preview track
          </button>
        </div>
      )}
    </div>
  );
}

window.BuilderScreen = BuilderScreen;
