// Visualizer — binaural orbital pulse with live waveform
function Visualizer({ beat = 8, base = 200, playing = true, size = 240, hue = 265 }) {
  const canvasRef = React.useRef(null);
  const startRef = React.useRef(performance.now());
  const pausedAtRef = React.useRef(0);

  React.useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const dpr = window.devicePixelRatio || 1;
    canvas.width = size * dpr; canvas.height = size * dpr;
    ctx.scale(dpr, dpr);
    let raf;

    const draw = (now) => {
      if (!playing) {
        pausedAtRef.current = now - startRef.current;
      } else {
        startRef.current = now - pausedAtRef.current;
      }
      const t = (playing ? (now - startRef.current) : pausedAtRef.current) / 1000;
      ctx.clearRect(0, 0, size, size);

      const cx = size / 2, cy = size / 2;

      // Binaural interference: two sine frequencies, left & right
      const fL = base;
      const fR = base + beat;

      // Draw waveforms (compressed time)
      const drawWave = (freq, color, yOff, amp) => {
        ctx.beginPath();
        ctx.strokeStyle = color;
        ctx.lineWidth = 1.2;
        for (let x = 0; x < size; x++) {
          const phase = (x / size) * 6 + t * (freq / 40);
          const y = cy + yOff + Math.sin(phase) * amp;
          if (x === 0) ctx.moveTo(x, y); else ctx.lineTo(x, y);
        }
        ctx.stroke();
      };

      // Orbital rings
      const beatPhase = t * beat * 0.4;
      const pulse = 0.5 + 0.5 * Math.sin(beatPhase * Math.PI);

      for (let r = 0; r < 5; r++) {
        const radius = 50 + r * 16 + pulse * 6;
        const alpha = (0.10 + r * 0.025) * (1 - r * 0.12);
        ctx.beginPath();
        ctx.arc(cx, cy, radius, 0, Math.PI * 2);
        ctx.strokeStyle = `hsla(${hue + r * 8}, 85%, 70%, ${alpha})`;
        ctx.lineWidth = 1;
        ctx.stroke();
      }

      // Core glow
      const grad = ctx.createRadialGradient(cx, cy, 0, cx, cy, 70);
      grad.addColorStop(0, `hsla(${hue}, 90%, 75%, ${0.35 + pulse * 0.25})`);
      grad.addColorStop(0.5, `hsla(${hue + 40}, 90%, 60%, ${0.15 + pulse * 0.1})`);
      grad.addColorStop(1, 'hsla(260, 90%, 50%, 0)');
      ctx.fillStyle = grad;
      ctx.beginPath();
      ctx.arc(cx, cy, 70, 0, Math.PI * 2);
      ctx.fill();

      // Orbiting particles (one per channel)
      const orbR = 62 + pulse * 4;
      const angL = t * 0.6;
      const angR = -t * 0.6 + Math.PI;
      [[angL, `hsla(${hue + 20}, 95%, 75%, 1)`], [angR, `hsla(${hue - 20}, 95%, 75%, 1)`]].forEach(([a, color]) => {
        const px = cx + Math.cos(a) * orbR;
        const py = cy + Math.sin(a) * orbR;
        const pg = ctx.createRadialGradient(px, py, 0, px, py, 12);
        pg.addColorStop(0, color);
        pg.addColorStop(1, 'rgba(0,0,0,0)');
        ctx.fillStyle = pg;
        ctx.beginPath(); ctx.arc(px, py, 12, 0, Math.PI * 2); ctx.fill();
      });

      // Waveforms above + below
      drawWave(fL, `hsla(${hue}, 80%, 70%, 0.35)`, -size/2 + 30, 10);
      drawWave(fR, `hsla(${hue + 30}, 80%, 70%, 0.35)`, size/2 - 30, 10);

      raf = requestAnimationFrame(draw);
    };

    raf = requestAnimationFrame(draw);
    return () => cancelAnimationFrame(raf);
  }, [beat, base, playing, size, hue]);

  return (
    <div style={{ position: 'relative', width: size, height: size }}>
      <canvas
        ref={canvasRef}
        style={{ width: size, height: size, display: 'block' }}
      />
    </div>
  );
}

// Lightweight static spectrum for list items
function MiniOrb({ hue = 260, size = 56, style = 'orb' }) {
  if (style === 'stack') {
    return (
      <div style={{
        width: size, height: size, borderRadius: 14,
        background: `linear-gradient(135deg, hsl(${hue}, 70%, 45%), hsl(${hue + 40}, 70%, 30%))`,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        boxShadow: `inset 0 0 0 1px rgba(255,255,255,0.08)`,
      }}>
        <svg width={size*0.6} height={size*0.6} viewBox="0 0 40 40" fill="none">
          <path d="M2 20c4 0 4-10 8-10s4 10 8 10 4-10 8-10 4 10 8 10 4-10 8-10"
            stroke={`hsl(${hue + 60}, 100%, 85%)`} strokeWidth="1.5" strokeLinecap="round" opacity="0.9"/>
          <path d="M2 26c4 0 4-6 8-6s4 6 8 6 4-6 8-6 4 6 8 6 4-6 8-6"
            stroke={`hsl(${hue - 20}, 100%, 80%)`} strokeWidth="1.2" strokeLinecap="round" opacity="0.6"/>
        </svg>
      </div>
    );
  }
  return (
    <div style={{
      width: size, height: size, borderRadius: '50%',
      background: `radial-gradient(circle at 30% 30%, hsl(${hue}, 90%, 72%), hsl(${hue + 30}, 70%, 25%) 70%, #0a0c18 100%)`,
      boxShadow: `inset 0 0 0 1px rgba(255,255,255,0.1), 0 0 24px hsla(${hue}, 80%, 50%, 0.3)`,
    }}/>
  );
}

window.Visualizer = Visualizer;
window.MiniOrb = MiniOrb;
