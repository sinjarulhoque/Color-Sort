import wave
import math
import struct
import os

def generate_wav(filename, duration, start_freq, end_freq, vol=0.5, arpeggio=None):
    sample_rate = 44100
    with wave.open(filename, 'w') as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(sample_rate)
        
        phase = 0.0
        for i in range(int(sample_rate * duration)):
            t = float(i) / sample_rate
            
            if arpeggio:
                freq = arpeggio[0][1]
                for atime, afreq in arpeggio:
                    if t >= atime: freq = afreq
            else:
                freq = start_freq + (end_freq - start_freq) * (t / duration)
            
            phase += 2.0 * math.pi * freq / sample_rate
            value = int(vol * 32767.0 * math.sin(phase))
            
            # envelope to avoid clicks
            env = 1.0
            if t < 0.01: env = t / 0.01
            elif t > duration - 0.05: env = (duration - t) / 0.05
            value = int(value * env)
            
            w.writeframesraw(struct.pack('<h', value))

os.makedirs('app/src/main/res/raw', exist_ok=True)
# Select (pop)
generate_wav('app/src/main/res/raw/select.wav', 0.05, 800, 400, vol=0.4)
# Drop (bloop)
generate_wav('app/src/main/res/raw/drop.wav', 0.1, 400, 1200, vol=0.4)
# Complete (chime)
generate_wav('app/src/main/res/raw/complete.wav', 0.6, 0, 0, vol=0.4, arpeggio=[
    (0.0, 523.25), (0.15, 659.25), (0.3, 783.99), (0.45, 1046.50)
])
print("Sounds generated.")
