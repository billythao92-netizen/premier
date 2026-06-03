import { useEffect, useState, useRef } from 'react'
import type { Selection } from '../types'
import { CHANNEL_EMOJI, CHANNEL_LABEL } from '../types'

interface Props {
  photo: string
  photoFile: File | null
  selections: Selection[]
  countdownSeconds: number
  onCancel: () => void
  onDone: () => void
}

type Phase = 'counting' | 'sharing'

export default function CountdownScreen({
  photo,
  photoFile,
  selections,
  countdownSeconds,
  onCancel,
  onDone,
}: Props) {
  const [count, setCount] = useState(countdownSeconds)
  const [phase, setPhase] = useState<Phase>('counting')
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null)
  const cancelledRef = useRef(false)

  useEffect(() => {
    timerRef.current = setInterval(() => {
      setCount(prev => {
        if (prev <= 1) {
          clearInterval(timerRef.current!)
          if (!cancelledRef.current) setPhase('sharing')
          return 0
        }
        return prev - 1
      })
    }, 1000)
    return () => clearInterval(timerRef.current!)
  }, [])

  function cancel() {
    cancelledRef.current = true
    clearInterval(timerRef.current!)
    onCancel()
  }

  // Web Share API — shares the photo file, user picks app from native sheet
  async function shareWithWebAPI() {
    if (!photoFile) return
    try {
      await navigator.share({
        files: [photoFile],
        title: 'Une photo pour toi!',
        text: 'Regarde cette belle photo!',
      })
    } catch {
      // User dismissed share sheet — that's OK
    }
  }

  // Opens the channel-specific app with recipient pre-filled
  function openChannel(sel: Selection) {
    const { recipient, channel } = sel
    switch (channel) {
      case 'sms': {
        const isIOS = /iPad|iPhone|iPod/.test(navigator.userAgent)
        const sep = isIOS ? '&' : '?'
        window.location.href = `sms:${recipient.phone}${sep}body=${encodeURIComponent('Regarde cette belle photo!')}`
        break
      }
      case 'email': {
        window.location.href = `mailto:${recipient.email}?subject=${encodeURIComponent('Une photo pour toi!')}&body=${encodeURIComponent('Regarde cette belle photo!')}`
        break
      }
      case 'whatsapp': {
        const phone = recipient.phone.replace(/\D/g, '')
        window.open(`https://wa.me/${phone}?text=${encodeURIComponent('Regarde cette belle photo!')}`, '_blank')
        break
      }
      case 'messenger': {
        window.open('https://www.messenger.com/new', '_blank')
        break
      }
    }
  }

  const canWebShare =
    typeof navigator.share === 'function' &&
    photoFile !== null &&
    (typeof navigator.canShare !== 'function' || navigator.canShare({ files: [photoFile] }))

  return (
    <div
      className="screen countdown-screen"
      style={{ backgroundImage: `url(${photo})` }}
    >
      <div className="countdown-overlay" />

      {phase === 'counting' && (
        <div className="countdown-body">
          <div className="countdown-label">Envoi dans…</div>
          <div className="countdown-number">{count}</div>
          <button className="cancel-btn" onClick={cancel}>
            ANNULER ✋
          </button>
        </div>
      )}

      {phase === 'sharing' && (
        <div className="sharing-panel">
          <div className="sharing-title">C'est parti! 🎉</div>

          {canWebShare && (
            <button className="webshare-btn" onClick={shareWithWebAPI}>
              📤 Partager la photo
            </button>
          )}

          <div className="share-list">
            {selections.map((sel, i) => (
              <button
                key={i}
                className="share-item-btn"
                onClick={() => openChannel(sel)}
              >
                {CHANNEL_EMOJI[sel.channel]}&nbsp;
                <strong>{sel.recipient.name}</strong>&nbsp;via {CHANNEL_LABEL[sel.channel]}
              </button>
            ))}
          </div>

          <a className="download-link" href={photo} download="photo.jpg">
            ⬇️ Télécharger la photo
          </a>

          <button className="done-btn" onClick={onDone}>✅ Terminé</button>
        </div>
      )}
    </div>
  )
}
